package com.carrilloabogados.n8n.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para alerta de lead HOT enviada por n8n.
 * 
 * NOTA: lead_id es String (formato: "2026-01-11T02:08:10.022Z-email-at-domain.com")
 * NO es UUID porque n8n genera IDs basados en timestamp + email.
 */
public class LeadHotAlertDto {

    @JsonProperty("event_type")
    private String eventType;

    private Instant timestamp;

    @JsonProperty("lead_id")
    private String leadId;  // Cambiado de UUID a String

    private Integer score;

    private String urgency;

    @JsonProperty("lead_data")
    private LeadData leadData;

    @JsonProperty("notification_channels")
    private String[] notificationChannels;

    @JsonProperty("email_sent_to")  // Nuevo campo
    private String emailSentTo;

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

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public LeadData getLeadData() {
        return leadData;
    }

    public void setLeadData(LeadData leadData) {
        this.leadData = leadData;
    }

    public String[] getNotificationChannels() {
        return notificationChannels;
    }

    public void setNotificationChannels(String[] notificationChannels) {
        this.notificationChannels = notificationChannels;
    }

    public String getEmailSentTo() {
        return emailSentTo;
    }

    public void setEmailSentTo(String emailSentTo) {
        this.emailSentTo = emailSentTo;
    }

    /**
     * Datos resumidos del lead para notificación.
     */
    public static class LeadData {
        private String nombre;
        private String email;
        private String empresa;
        private String servicio;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmpresa() {
            return empresa;
        }

        public void setEmpresa(String empresa) {
            this.empresa = empresa;
        }

        public String getServicio() {
            return servicio;
        }

        public void setServicio(String servicio) {
            this.servicio = servicio;
        }
    }
}
