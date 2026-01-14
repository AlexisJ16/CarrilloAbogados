package com.carrilloabogados.client.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.dto.LeadDto;
import com.carrilloabogados.client.service.LeadService;

/**
 * Controlador para endpoints de integración con n8n.
 * Recibe callbacks desde n8n-integration-service.
 */
@RestController
@RequestMapping("/api/leads")
public class LeadIntegrationController {

    private static final Logger log = LoggerFactory.getLogger(LeadIntegrationController.class);

    private final LeadService leadService;

    public LeadIntegrationController(LeadService leadService) {
        this.leadService = leadService;
    }

    /**
     * Actualiza el score de un lead (llamado desde n8n via
     * n8n-integration-service).
     * 
     * POST /api/leads/{leadId}/score
     * 
     * Body:
     * {
     * "score": 95,
     * "category": "HOT",
     * "ai_analysis": {...}
     * }
     */
    @PatchMapping("/{leadId}/score")
    public ResponseEntity<Map<String, Object>> updateLeadScore(
            @PathVariable UUID leadId,
            @RequestBody Map<String, Object> payload) {

        log.info("📨 Received score update from n8n for lead: {} - Score: {}, Category: {}",
                leadId, payload.get("score"), payload.get("category"));

        try {
            Integer score = (Integer) payload.get("score");
            String categoryStr = (String) payload.get("category");
            LeadCategory category = LeadCategory.valueOf(categoryStr);

            // Actualizar scoring en base de datos
            LeadDto updated = leadService.updateScoring(leadId, score, category);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", updated.getLeadId());
            response.put("score", updated.getLeadScore());
            response.put("category", updated.getLeadCategory());
            response.put("updated_at", updated.getUpdatedAt());

            log.info("✅ Lead score updated successfully: {} -> Score: {}, Category: {}",
                    leadId, score, category);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Failed to update lead score: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("lead_id", leadId);

            return ResponseEntity.internalServerError().body(error);
        }
    }

    /**
     * Crea notificación para lead HOT (llamado desde n8n via
     * n8n-integration-service).
     * 
     * POST /api/notifications/hot-lead
     * 
     * Body:
     * {
     * "lead_id": "uuid",
     * "score": 95,
     * "category": "HOT",
     * "urgency": "HIGH",
     * "lead_data": {...}
     * }
     */
    @PostMapping("/notifications/hot-lead")
    public ResponseEntity<Map<String, Object>> createHotLeadNotification(
            @RequestBody Map<String, Object> payload) {

        String leadId = (String) payload.get("lead_id");
        Integer score = (Integer) payload.get("score");

        log.warn("🔥 HOT LEAD notification received from n8n: {} - Score: {}", leadId, score);

        try {
            // TODO: Crear notificación en notification-service via REST o NATS
            // Por ahora solo registramos en logs
            Map<String, Object> leadData = (Map<String, Object>) payload.get("lead_data");

            if (leadData != null) {
                log.warn("🔥 HOT Lead: {} ({}) - Empresa: {} - Servicio: {}",
                        leadData.get("nombre"),
                        leadData.get("email"),
                        leadData.get("empresa"),
                        leadData.get("servicio"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("lead_id", leadId);
            response.put("notification_created", true);
            response.put("message", "HOT lead notification logged successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Failed to process HOT lead notification: {}", e.getMessage(), e);

            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("lead_id", leadId);

            return ResponseEntity.internalServerError().body(error);
        }
    }
}
