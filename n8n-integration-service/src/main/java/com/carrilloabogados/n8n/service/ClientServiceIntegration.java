package com.carrilloabogados.n8n.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.carrilloabogados.n8n.dto.LeadHotAlertDto;
import com.carrilloabogados.n8n.dto.LeadScoredDto;

/**
 * Servicio para integración con client-service.
 * Actualiza leads en PostgreSQL basándose en callbacks de n8n.
 */
@Service
public class ClientServiceIntegration {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceIntegration.class);

    private final RestTemplate restTemplate;

    @Value("${client-service.base-url:http://localhost:8200/client-service}")
    private String clientServiceBaseUrl;

    public ClientServiceIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Actualiza el score de un lead en client-service.
     * 
     * @param dto Datos del lead scored desde n8n
     * @return true si se actualizó exitosamente
     */
    public boolean updateLeadScore(LeadScoredDto dto) {
        String url = clientServiceBaseUrl + "/api/leads/" + dto.getLeadId() + "/score";

        log.info("Updating lead score in client-service: {} -> Score: {}, Category: {}",
                dto.getLeadId(), dto.getScore(), dto.getCategory());

        try {
            // Preparar payload para client-service
            Map<String, Object> payload = new HashMap<>();
            payload.put("score", dto.getScore());
            payload.put("category", dto.getCategory());

            // Incluir score_breakdown como análisis adicional si existe
            if (dto.getScoreBreakdown() != null && !dto.getScoreBreakdown().isEmpty()) {
                payload.put("score_breakdown", dto.getScoreBreakdown());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    request,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Lead score updated successfully in client-service: {}", dto.getLeadId());
                return true;
            } else {
                log.warn("⚠️ Unexpected response from client-service: {}", response.getStatusCode());
                return false;
            }

        } catch (RestClientException e) {
            log.error("❌ Failed to update lead score in client-service: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Crea una notificación para un lead HOT.
     * 
     * @param dto Datos del lead HOT desde n8n
     * @return true si se creó la notificación exitosamente
     */
    public boolean createHotLeadNotification(LeadHotAlertDto dto) {
        String url = clientServiceBaseUrl + "/api/notifications/hot-lead";

        log.warn("🔥 Creating HOT lead notification: {} - Score: {}", dto.getLeadId(), dto.getScore());

        try {
            // Preparar payload para notification
            Map<String, Object> payload = new HashMap<>();
            payload.put("lead_id", dto.getLeadId());
            payload.put("score", dto.getScore());
            payload.put("urgency", dto.getUrgency());
            payload.put("lead_data", dto.getLeadData());
            payload.put("timestamp", dto.getTimestamp());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ HOT lead notification created successfully: {}", dto.getLeadId());
                return true;
            } else {
                log.warn("⚠️ Unexpected response from notification endpoint: {}", response.getStatusCode());
                return false;
            }

        } catch (RestClientException e) {
            log.error("❌ Failed to create HOT lead notification: {}", e.getMessage());
            return false;
        }
    }
}
