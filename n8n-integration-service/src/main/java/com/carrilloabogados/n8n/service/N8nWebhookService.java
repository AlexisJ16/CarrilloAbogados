package com.carrilloabogados.n8n.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.carrilloabogados.n8n.config.N8nCloudConfig;
import com.carrilloabogados.n8n.dto.LeadEventDto;

/**
 * Servicio para enviar eventos a n8n Cloud via webhooks.
 */
@Service
public class N8nWebhookService {

    private static final Logger log = LoggerFactory.getLogger(N8nWebhookService.class);

    private final RestTemplate restTemplate;
    private final N8nCloudConfig config;

    public N8nWebhookService(RestTemplate restTemplate, N8nCloudConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Envía un evento de nuevo lead a n8n para procesamiento.
     * 
     * @param leadEvent Datos del lead capturado
     * @return true si se envió exitosamente
     */
    public boolean sendLeadEvent(LeadEventDto leadEvent) {
        if (!config.isEnabled()) {
            log.info("n8n integration is disabled. Skipping lead event.");
            return false;
        }

        String url = config.getWebhooks().getLeadEvents();

        log.info("🔍 DEBUG - URL from config: {}", url);
        log.info("Sending lead event to n8n: {} for lead {}",
                leadEvent.getEventType(),
                leadEvent.getPayload().getLeadId());

        return sendWithRetry(url, leadEvent, config.getRetry().getMaxAttempts());
    }

    /**
     * Envía un evento de cita agendada a n8n.
     * 
     * @param meetingData Datos de la cita
     * @return true si se envió exitosamente
     */
    public boolean sendMeetingEvent(Map<String, Object> meetingData) {
        if (!config.isEnabled()) {
            log.info("n8n integration is disabled. Skipping meeting event.");
            return false;
        }

        String url = config.getWebhooks().getMeetingEvents();

        log.info("Sending meeting event to n8n: {}", meetingData.get("event_type"));

        return sendWithRetry(url, meetingData, config.getRetry().getMaxAttempts());
    }

    /**
     * Envía un evento de caso cerrado a n8n.
     * 
     * @param caseData Datos del caso
     * @return true si se envió exitosamente
     */
    public boolean sendCaseEvent(Map<String, Object> caseData) {
        if (!config.isEnabled()) {
            log.info("n8n integration is disabled. Skipping case event.");
            return false;
        }

        String url = config.getWebhooks().getCaseEvents();

        log.info("Sending case event to n8n: {}", caseData.get("event_type"));

        return sendWithRetry(url, caseData, config.getRetry().getMaxAttempts());
    }

    /**
     * Envía una solicitud HTTP con reintentos.
     */
    private boolean sendWithRetry(String url, Object payload, int maxAttempts) {
        int attempt = 0;

        while (attempt < maxAttempts) {
            attempt++;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Object> request = new HttpEntity<>(payload, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Successfully sent event to n8n. Response: {}", response.getBody());
                    return true;
                } else {
                    log.warn("n8n returned non-success status: {}", response.getStatusCode());
                }

            } catch (RestClientException e) {
                log.error("Attempt {}/{} failed to send event to n8n: {}",
                        attempt, maxAttempts, e.getMessage());

                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(config.getRetry().getDelayMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
        }

        log.error("Failed to send event to n8n after {} attempts", maxAttempts);
        return false;
    }
}
