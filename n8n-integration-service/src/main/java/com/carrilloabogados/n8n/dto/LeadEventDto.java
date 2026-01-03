package com.carrilloabogados.n8n.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para eventos de lead enviados a n8n Cloud.
 * Estructura compatible con el webhook /lead-events del Orquestador.
 */
public class LeadEventDto {

    @JsonProperty("event_type")
    private String eventType;

    private Instant timestamp;

    private String source;

    private LeadPayload payload;

    public LeadEventDto() {
        this.eventType = "new_lead";
        this.timestamp = Instant.now();
        this.source = "portal_web";
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LeadPayload getPayload() {
        return payload;
    }

    public void setPayload(LeadPayload payload) {
        this.payload = payload;
    }

    /**
     * Inner class para el payload del lead.
     */
    public static class LeadPayload {

        @JsonProperty("lead_id")
        private UUID leadId;

        private String nombre;

        private String email;

        private String telefono;

        private String empresa;

        private String cargo;

        @JsonProperty("servicio_interes")
        private String servicioInteres;

        private String mensaje;

        @JsonProperty("utm_source")
        private String utmSource;

        @JsonProperty("utm_campaign")
        private String utmCampaign;

        // Getters and Setters
        public UUID getLeadId() {
            return leadId;
        }

        public void setLeadId(UUID leadId) {
            this.leadId = leadId;
        }

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

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getEmpresa() {
            return empresa;
        }

        public void setEmpresa(String empresa) {
            this.empresa = empresa;
        }

        public String getCargo() {
            return cargo;
        }

        public void setCargo(String cargo) {
            this.cargo = cargo;
        }

        public String getServicioInteres() {
            return servicioInteres;
        }

        public void setServicioInteres(String servicioInteres) {
            this.servicioInteres = servicioInteres;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getUtmSource() {
            return utmSource;
        }

        public void setUtmSource(String utmSource) {
            this.utmSource = utmSource;
        }

        public String getUtmCampaign() {
            return utmCampaign;
        }

        public void setUtmCampaign(String utmCampaign) {
            this.utmCampaign = utmCampaign;
        }
    }
}
