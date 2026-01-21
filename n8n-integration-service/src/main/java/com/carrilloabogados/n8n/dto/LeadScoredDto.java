package com.carrilloabogados.n8n.dto;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para recibir callback de score calculado por n8n.
 * 
 * NOTA: lead_id es String (formato: "2026-01-11T02:08:10.022Z-email-at-domain.com")
 * NO es UUID porque n8n genera IDs basados en timestamp + email.
 */
public class LeadScoredDto {

    @JsonProperty("event_type")
    private String eventType;

    private Instant timestamp;

    @JsonProperty("lead_id")
    private String leadId;  // Cambiado de UUID a String

    private Integer score;

    private String category;

    @JsonProperty("ai_analysis")
    private Map<String, Object> aiAnalysis;  // Nuevo campo para recibir análisis completo de IA

    @JsonProperty("score_breakdown")
    private Map<String, Integer> scoreBreakdown;

    @JsonProperty("processed_at")
    private Instant processedAt;

    @JsonProperty("response_sent")
    private Boolean responseSent;

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(Map<String, Object> aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public Map<String, Integer> getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(Map<String, Integer> scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public Boolean getResponseSent() {
        return responseSent;
    }

    public void setResponseSent(Boolean responseSent) {
        this.responseSent = responseSent;
    }

    /**
     * Verifica si el lead es HOT (score >= 70)
     */
    public boolean isHotLead() {
        return score != null && score >= 70;
    }

    /**
     * Verifica si el lead es WARM (score 40-69)
     */
    public boolean isWarmLead() {
        return score != null && score >= 40 && score < 70;
    }
}
