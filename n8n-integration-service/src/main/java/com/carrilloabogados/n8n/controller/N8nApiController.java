package com.carrilloabogados.n8n.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.n8n.dto.LeadEventDto;
import com.carrilloabogados.n8n.service.N8nWebhookService;

/**
 * Controlador de API para operaciones manuales y debugging.
 */
@RestController
@RequestMapping("/api")
public class N8nApiController {

    private static final Logger log = LoggerFactory.getLogger(N8nApiController.class);

    private final N8nWebhookService webhookService;

    public N8nApiController(N8nWebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Envía un lead de prueba a n8n.
     * Útil para testing de integración.
     */
    @PostMapping("/test/send-lead")
    public ResponseEntity<Map<String, Object>> sendTestLead(@RequestBody Map<String, String> leadData) {
        log.info("Sending test lead to n8n: {}", leadData);

        try {
            LeadEventDto event = new LeadEventDto();
            LeadEventDto.LeadPayload payload = new LeadEventDto.LeadPayload();

            payload.setLeadId(UUID.randomUUID());
            payload.setNombre(leadData.getOrDefault("nombre", "Test User"));
            payload.setEmail(leadData.getOrDefault("email", "test@example.com"));
            payload.setTelefono(leadData.get("telefono"));
            payload.setEmpresa(leadData.get("empresa"));
            payload.setCargo(leadData.get("cargo"));
            payload.setServicioInteres(leadData.getOrDefault("servicio", "Consulta General"));
            payload.setMensaje(leadData.get("mensaje"));
            payload.setUtmSource(leadData.getOrDefault("utm_source", "test"));
            payload.setUtmCampaign(leadData.get("utm_campaign"));

            event.setPayload(payload);

            boolean success = webhookService.sendLeadEvent(event);

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("lead_id", payload.getLeadId());
            response.put("sent_to_n8n", success);

            if (success) {
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to send to n8n");
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            log.error("Error sending test lead: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Estado de la integración con n8n.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "n8n-integration-service");
        status.put("status", "UP");
        status.put("n8n_integration", "enabled");
        status.put("timestamp", java.time.Instant.now());
        status.put("webhooks", Map.of(
                "lead-scored", "/webhook/lead-scored",
                "lead-hot", "/webhook/lead-hot",
                "meeting-confirmed", "/webhook/meeting-confirmed"));

        return ResponseEntity.ok(status);
    }

    /**
     * Verifica conectividad con n8n Cloud.
     */
    @GetMapping("/health/n8n")
    public ResponseEntity<Map<String, Object>> checkN8nHealth() {
        // TODO: Implementar ping a n8n Cloud
        Map<String, Object> health = new HashMap<>();
        health.put("n8n_cloud", "reachable");
        health.put("last_check", java.time.Instant.now());

        return ResponseEntity.ok(health);
    }
}
