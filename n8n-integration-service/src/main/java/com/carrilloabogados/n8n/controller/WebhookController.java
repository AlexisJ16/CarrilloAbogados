package com.carrilloabogados.n8n.controller;

import com.carrilloabogados.n8n.config.MicroservicesConfig;
import com.carrilloabogados.n8n.dto.LeadHotAlertDto;
import com.carrilloabogados.n8n.dto.LeadScoredDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import io.nats.client.Connection;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para recibir webhooks de n8n Cloud.
 * Estos endpoints son llamados por n8n cuando procesa eventos.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final RestTemplate restTemplate;
    private final MicroservicesConfig microservicesConfig;
    private final Connection natsConnection;

    public WebhookController(RestTemplate restTemplate, 
                            MicroservicesConfig microservicesConfig,
                            @Nullable Connection natsConnection) {
        this.restTemplate = restTemplate;
        this.microservicesConfig = microservicesConfig;
        this.natsConnection = natsConnection;
    }

    /**
     * Recibe callback cuando n8n calcula el score de un lead.
     * 
     * Llamado por: SUB-A después de procesar el lead
     * Acción: Actualizar score en base de datos local via client-service
     */
    @PostMapping("/lead-scored")
    public ResponseEntity<Map<String, Object>> handleLeadScored(@RequestBody LeadScoredDto dto) {
        log.info("Received lead-scored callback from n8n. Lead ID: {}, Score: {}, Category: {}",
                dto.getLeadId(), dto.getScore(), dto.getCategory());

        try {
            // Actualizar score en client-service via REST
            boolean updated = updateLeadScoreInClientService(dto);
            
            log.info("Lead {} scored as {} with {} points. Updated in DB: {}",
                    dto.getLeadId(), dto.getCategory(), dto.getScore(), updated);

            if (dto.isHotLead()) {
                log.warn("🔥 HOT LEAD detected! Lead ID: {}", dto.getLeadId());
                // Publicar evento de notificación urgente a NATS
                publishHotLeadNotification(dto);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("score_received", dto.getScore());
            response.put("updated_in_db", updated);
            response.put("processed", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing lead-scored callback: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Actualiza el score del lead en client-service.
     */
    private boolean updateLeadScoreInClientService(LeadScoredDto dto) {
        try {
            String url = microservicesConfig.getClientServiceUrl() + "/api/leads/" + dto.getLeadId() + "/score";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("score", dto.getScore());
            payload.put("category", dto.getCategory());
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.PATCH, request, String.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Failed to update lead score in client-service: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Publica evento de lead HOT a NATS para notificación urgente.
     */
    private void publishHotLeadNotification(LeadScoredDto dto) {
        if (natsConnection == null) {
            log.warn("NATS not available, cannot publish hot lead notification");
            return;
        }
        try {
            String subject = "carrillo.notifications.hot-lead";
            String message = String.format(
                "{\"leadId\":\"%s\",\"score\":%d,\"category\":\"%s\",\"urgency\":\"HIGH\"}",
                dto.getLeadId(), dto.getScore(), dto.getCategory()
            );
            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
            log.info("Published hot lead notification to NATS: {}", subject);
        } catch (Exception e) {
            log.error("Failed to publish hot lead notification: {}", e.getMessage());
        }
    }

    /**
     * Recibe alerta cuando n8n detecta un lead HOT.
     * 
     * Llamado por: SUB-A cuando score >= 70
     * Acción: Notificar abogado asignado via NATS y notification-service
     */
    @PostMapping("/lead-hot")
    public ResponseEntity<Map<String, Object>> handleHotLead(@RequestBody LeadHotAlertDto dto) {
        log.warn("🔥 HOT LEAD ALERT from n8n! Lead ID: {}, Score: {}, Urgency: {}",
                dto.getLeadId(), dto.getScore(), dto.getUrgency());

        try {
            // Log datos del lead para acción inmediata
            if (dto.getLeadData() != null) {
                log.warn("HOT Lead Details: {} ({}) - {} - {}",
                        dto.getLeadData().getNombre(),
                        dto.getLeadData().getEmail(),
                        dto.getLeadData().getEmpresa(),
                        dto.getLeadData().getServicio());
            }

            // Enviar notificación urgente a NATS para notification-service
            boolean notificationSent = sendUrgentNotification(dto);
            
            // Actualizar estado del lead a MQL (Marketing Qualified Lead)
            updateLeadStatusToMQL(dto.getLeadId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("notification_sent", notificationSent);
            response.put("assigned_to", dto.getAssignedTo() != null ? dto.getAssignedTo() : "default_lawyer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing hot-lead alert: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Envía notificación urgente a NATS para que notification-service la procese.
     */
    private boolean sendUrgentNotification(LeadHotAlertDto dto) {
        if (natsConnection == null) {
            log.warn("NATS not available, cannot send urgent notification");
            return false;
        }
        try {
            String subject = "carrillo.notifications.urgent";
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "HOT_LEAD_ALERT");
            notification.put("leadId", dto.getLeadId());
            notification.put("score", dto.getScore());
            notification.put("urgency", dto.getUrgency());
            notification.put("assignedTo", dto.getAssignedTo() != null ? dto.getAssignedTo() : "gerenciacarrilloabgd@gmail.com");
            notification.put("timestamp", java.time.Instant.now().toString());
            
            if (dto.getLeadData() != null) {
                notification.put("leadName", dto.getLeadData().getNombre());
                notification.put("leadEmail", dto.getLeadData().getEmail());
                notification.put("leadEmpresa", dto.getLeadData().getEmpresa());
                notification.put("leadServicio", dto.getLeadData().getServicio());
            }
            
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String message = mapper.writeValueAsString(notification);
            
            natsConnection.publish(subject, message.getBytes(StandardCharsets.UTF_8));
            log.info("📨 Published urgent notification to NATS: {}", subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send urgent notification: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado del lead a MQL en client-service.
     */
    private void updateLeadStatusToMQL(java.util.UUID leadId) {
        try {
            String url = microservicesConfig.getClientServiceUrl() + "/api/leads/" + leadId + "/update-status";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("status", "MQL");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
            
            log.info("Lead {} status updated to MQL", leadId);
        } catch (Exception e) {
            log.warn("Failed to update lead status to MQL: {}", e.getMessage());
        }
    }

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Recibe confirmación de cita agendada.
     * 
     * Llamado por: SUB-F cuando se agenda una reunión
     * Acción: Sincronizar con calendar-service
     */
    @PostMapping("/meeting-confirmed")
    public ResponseEntity<Map<String, Object>> handleMeetingConfirmed(@RequestBody Map<String, Object> payload) {
        log.info("Received meeting-confirmed callback from n8n: {}", payload);

        try {
            // TODO: Sincronizar con calendar-service

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("meeting_synced", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing meeting-confirmed callback: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Health check del servicio de webhooks.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "n8n-integration-webhook-receiver");
        health.put("timestamp", java.time.Instant.now());

        return ResponseEntity.ok(health);
    }
}
