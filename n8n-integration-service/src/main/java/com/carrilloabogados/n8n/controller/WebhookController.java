package com.carrilloabogados.n8n.controller;

import com.carrilloabogados.n8n.dto.LeadHotAlertDto;
import com.carrilloabogados.n8n.dto.LeadScoredDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Recibe callback cuando n8n calcula el score de un lead.
     * 
     * Llamado por: SUB-A después de procesar el lead
     * Acción: Actualizar score en base de datos local
     */
    @PostMapping("/lead-scored")
    public ResponseEntity<Map<String, Object>> handleLeadScored(@RequestBody LeadScoredDto dto) {
        log.info("Received lead-scored callback from n8n. Lead ID: {}, Score: {}, Category: {}",
                dto.getLeadId(), dto.getScore(), dto.getCategory());

        try {
            // TODO: Actualizar score en client-service via REST o NATS
            // Por ahora solo logueamos
            log.info("Lead {} scored as {} with {} points",
                    dto.getLeadId(), dto.getCategory(), dto.getScore());

            if (dto.isHotLead()) {
                log.warn("🔥 HOT LEAD detected! Lead ID: {}", dto.getLeadId());
                // TODO: Trigger notificación interna
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("score_received", dto.getScore());
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
     * Recibe alerta cuando n8n detecta un lead HOT.
     * 
     * Llamado por: SUB-A cuando score >= 70
     * Acción: Notificar abogado asignado
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

            // TODO: Enviar notificación a notification-service
            // Por ahora registramos en log

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", dto.getLeadId());
            response.put("notification_sent", true);

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
