package com.carrilloabogados.n8n.listener;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.carrilloabogados.n8n.dto.LeadEventDto;
import com.carrilloabogados.n8n.service.N8nWebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;

/**
 * Escucha eventos NATS desde los microservicios y los reenvía a n8n Cloud.
 * 
 * Eventos soportados:
 * - carrillo.events.lead.created -> Envía a n8n para scoring
 * - carrillo.events.case.closed -> Envía a n8n para follow-up
 * - carrillo.events.appointment.scheduled -> Envía a n8n para confirmación
 */
@Component
public class NatsEventListener {

    private static final Logger log = LoggerFactory.getLogger(NatsEventListener.class);

    private final Connection natsConnection;
    private final N8nWebhookService webhookService;
    private final ObjectMapper objectMapper;

    @Value("${nats.subjects.lead-created:carrillo.events.lead.created}")
    private String leadCreatedSubject;

    @Value("${nats.subjects.case-closed:carrillo.events.case.closed}")
    private String caseClosedSubject;

    @Value("${nats.subjects.appointment-scheduled:carrillo.events.appointment.scheduled}")
    private String appointmentScheduledSubject;

    private Dispatcher dispatcher;

    public NatsEventListener(@Nullable Connection natsConnection,
            N8nWebhookService webhookService,
            ObjectMapper objectMapper) {
        this.natsConnection = natsConnection;
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        if (natsConnection == null) {
            log.warn("⚠️ NATS connection not available. Event listening disabled.");
            return;
        }

        try {
            dispatcher = natsConnection.createDispatcher(this::handleMessage);

            // Subscribe to all relevant subjects
            dispatcher.subscribe(leadCreatedSubject);
            log.info("📡 Subscribed to: {}", leadCreatedSubject);

            dispatcher.subscribe(caseClosedSubject);
            log.info("📡 Subscribed to: {}", caseClosedSubject);

            dispatcher.subscribe(appointmentScheduledSubject);
            log.info("📡 Subscribed to: {}", appointmentScheduledSubject);

            log.info("✅ NATS Event Listener started successfully");

        } catch (Exception e) {
            log.error("❌ Failed to start NATS listener: {}", e.getMessage(), e);
        }
    }

    private void handleMessage(Message msg) {
        String subject = msg.getSubject();
        String data = new String(msg.getData(), StandardCharsets.UTF_8);

        log.info("📨 Received event on [{}]: {}", subject, data);

        try {
            if (subject.equals(leadCreatedSubject)) {
                handleLeadCreated(data);
            } else if (subject.equals(caseClosedSubject)) {
                handleCaseClosed(data);
            } else if (subject.equals(appointmentScheduledSubject)) {
                handleAppointmentScheduled(data);
            } else {
                log.warn("Unknown subject: {}", subject);
            }
        } catch (Exception e) {
            log.error("Error processing message from {}: {}", subject, e.getMessage(), e);
        }
    }

    /**
     * Procesa evento de lead creado desde client-service.
     * Transforma a formato n8n y envía al webhook.
     */
    @SuppressWarnings("unchecked")
    private void handleLeadCreated(String data) {
        try {
            Map<String, Object> leadData = objectMapper.readValue(data, Map.class);

            // Crear evento para n8n
            LeadEventDto event = new LeadEventDto();
            event.setEventType("new_lead");
            event.setTimestamp(Instant.now());
            event.setSource("portal_web");

            LeadEventDto.LeadPayload payload = new LeadEventDto.LeadPayload();

            // Mapear campos del evento NATS al DTO de n8n
            payload.setLeadId(parseUUID(leadData.get("leadId")));
            payload.setNombre(getString(leadData, "nombre"));
            payload.setEmail(getString(leadData, "email"));
            payload.setTelefono(getString(leadData, "telefono"));
            payload.setEmpresa(getString(leadData, "empresa"));
            payload.setCargo(getString(leadData, "cargo"));
            payload.setServicioInteres(getString(leadData, "servicioInteres"));
            payload.setMensaje(getString(leadData, "mensaje"));
            payload.setUtmSource(getString(leadData, "utmSource"));
            payload.setUtmCampaign(getString(leadData, "utmCampaign"));

            event.setPayload(payload);

            // Enviar a n8n Cloud
            boolean success = webhookService.sendLeadEvent(event);

            if (success) {
                log.info("✅ Lead {} sent to n8n successfully", payload.getLeadId());
            } else {
                log.error("❌ Failed to send lead {} to n8n", payload.getLeadId());
            }

        } catch (Exception e) {
            log.error("Error processing lead.created event: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa evento de caso cerrado.
     */
    @SuppressWarnings("unchecked")
    private void handleCaseClosed(String data) {
        try {
            Map<String, Object> caseData = objectMapper.readValue(data, Map.class);

            // Add event type if not present
            caseData.put("event_type", "case_closed");

            boolean success = webhookService.sendCaseEvent(caseData);

            log.info("Case closed event sent to n8n: success={}", success);

        } catch (Exception e) {
            log.error("Error processing case.closed event: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa evento de cita agendada.
     */
    @SuppressWarnings("unchecked")
    private void handleAppointmentScheduled(String data) {
        try {
            Map<String, Object> appointmentData = objectMapper.readValue(data, Map.class);

            // Add event type if not present
            appointmentData.put("event_type", "meeting_scheduled");

            boolean success = webhookService.sendMeetingEvent(appointmentData);

            log.info("Meeting event sent to n8n: success={}", success);

        } catch (Exception e) {
            log.error("Error processing appointment.scheduled event: {}", e.getMessage(), e);
        }
    }

    // Helper methods
    private UUID parseUUID(Object value) {
        if (value == null)
            return UUID.randomUUID();
        if (value instanceof UUID)
            return (UUID) value;
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID();
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}
