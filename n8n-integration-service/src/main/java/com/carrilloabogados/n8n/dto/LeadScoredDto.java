package com.carrilloabogados.n8n.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para recibir callback de score calculado por n8n.
 */
public class LeadScoredDto {

    @JsonProperty("event_type")
    private String eventType;

    private Instant timestamp;

    @JsonProperty("lead_id")
    private UUID leadId;

    private Integer score;

    private String category;

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

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(UUID leadId) {
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
