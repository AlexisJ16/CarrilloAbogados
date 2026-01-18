package com.carrilloabogados.n8n.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.n8n.dto.LeadHotAlertDto;
import com.carrilloabogados.n8n.dto.LeadScoredDto;
import com.carrilloabogados.n8n.service.ClientServiceIntegration;

/**
 * Controlador para recibir webhooks de n8n Cloud.
 * Estos endpoints son llamados por n8n cuando procesa eventos.
 */
@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ClientServiceIntegration clientServiceIntegration;

    public WebhookController(ClientServiceIntegration clientServiceIntegration) {
        this.clientServiceIntegration = clientServiceIntegration;
    }

    /**
     * Recibe callback cuando n8n calcula el score de un lead.
     * 
     * Llamado por: SUB-A después de procesar el lead
     * Acción: Actualizar score en base de datos local
     */
    @PostMapping("/lead-scored")
    public ResponseEntity<Map<String, Object>> handleLeadScored(@RequestBody(required = false) LeadScoredDto dto) {
        log.info("🔔 ==> WEBHOOK CALLBACK RECEIVED: /lead-scored");

        if (dto == null) {
            log.error("❌ Received null DTO in lead-scored callback");
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Request body is null or invalid");
            return ResponseEntity.badRequest().body(error);
        }

        log.info("📨 Received lead-scored callback from n8n. Lead ID: {}, Score: {}, Category: {}",
                dto.getLeadId(), dto.getScore(), dto.getCategory());

        try {
            // Actualizar score en client-service
            boolean updated = clientServiceIntegration.updateLeadScore(dto);

            if (dto.isHotLead()) {
                log.warn("🔥 HOT LEAD detected! Lead ID: {}", dto.getLeadId());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("score_received", dto.getScore());
            response.put("updated_in_db", updated);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error processing lead-scored callback: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Recibe alerta cuando n8n detecta un lead HOT.
     * 
     * Llamado por: SUB-A cuando score >= 70
     * Acción: Notificar abogado asignado
     */
    @PostMapping("/lead-hot")
    public ResponseEntity<Map<String, Object>> handleHotLead(@RequestBody(required = false) LeadHotAlertDto dto) {
        log.info("🔔 ==> WEBHOOK CALLBACK RECEIVED: /lead-hot");

        if (dto == null) {
            log.error("❌ Received null DTO in lead-hot callback");
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Request body is null or invalid");
            return ResponseEntity.badRequest().body(error);
        }

        log.warn("🔥 HOT LEAD ALERT from n8n! Lead ID: {}, Score: {}, Urgency: {}",
                dto.getLeadId(), dto.getScore(), dto.getUrgency());

        try {
            // Log datos del lead para acción inmediata
            if (dto.getLeadData() != null) {
                log.warn("🔥 HOT Lead Details: {} ({}) - {} - {}",
                        dto.getLeadData().getNombre(),
                        dto.getLeadData().getEmail(),
                        dto.getLeadData().getEmpresa(),
                        dto.getLeadData().getServicio());
            }

            // Crear notificación en client-service
            boolean notified = clientServiceIntegration.createHotLeadNotification(dto);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("notification_created", notified);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error processing hot-lead alert: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());

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

    /**
     * Endpoint de prueba para verificar conectividad desde n8n Cloud.
     * n8n puede llamar este endpoint para confirmar que el servidor es accesible.
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testWebhook(@RequestBody(required = false) Map<String, Object> payload) {
        log.info("🧪 TEST WEBHOOK RECEIVED");
        log.info("🧪 Payload: {}", payload);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Webhook endpoint is reachable");
        response.put("received_payload", payload);
        response.put("timestamp", java.time.Instant.now());

        return ResponseEntity.ok(response);
    }
}
