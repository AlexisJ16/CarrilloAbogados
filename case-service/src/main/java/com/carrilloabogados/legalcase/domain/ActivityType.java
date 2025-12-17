package com.carrilloabogados.legalcase.domain;

/**
 * Enumeration representing different types of activities in a legal case
 */
public enum ActivityType {
    /**
     * Initial client consultation
     */
    CONSULTATION("Consulta", "Consulta inicial con el cliente"),
    
    /**
     * Document review and analysis
     */
    DOCUMENT_REVIEW("Revisión de Documentos", "Revisión y análisis de documentos"),
    
    /**
     * Legal research
     */
    RESEARCH("Investigación", "Investigación legal y jurisprudencial"),
    
    /**
     * Document drafting
     */
    DRAFTING("Redacción", "Elaboración de documentos legales"),
    
    /**
     * Court filing
     */
    FILING("Radicación", "Radicación de documentos en juzgado"),
    
    /**
     * Court hearing or appearance
     */
    HEARING("Audiencia", "Comparecencia en audiencia judicial"),
    
    /**
     * Client meeting
     */
    MEETING("Reunión", "Reunión con cliente"),
    
    /**
     * Phone call with client or opposing party
     */
    PHONE_CALL("Llamada", "Llamada telefónica"),
    
    /**
     * Email communication
     */
    EMAIL("Correo", "Comunicación por correo electrónico"),
    
    /**
     * Negotiation with opposing party
     */
    NEGOTIATION("Negociación", "Negociación con contraparte"),
    
    /**
     * Court appearance
     */
    COURT_APPEARANCE("Comparecencia", "Comparecencia judicial"),
    
    /**
     * Mediation session
     */
    MEDIATION("Mediación", "Sesión de mediación"),
    
    /**
     * Settlement discussion
     */
    SETTLEMENT("Conciliación", "Discusión de acuerdo"),
    
    /**
     * Evidence collection
     */
    EVIDENCE_COLLECTION("Recolección de Pruebas", "Recolección de evidencia"),
    
    /**
     * Expert witness consultation
     */
    EXPERT_CONSULTATION("Consulta Experto", "Consulta con perito"),
    
    /**
     * Travel for case-related business
     */
    TRAVEL("Viaje", "Viaje relacionado con el caso"),
    
    /**
     * Administrative task
     */
    ADMINISTRATIVE("Administrativo", "Tarea administrativa"),
    
    /**
     * Follow-up activity
     */
    FOLLOW_UP("Seguimiento", "Seguimiento del caso"),
    
    /**
     * Case closure activities
     */
    CASE_CLOSURE("Cierre", "Actividades de cierre del caso");
    
    private final String displayName;
    private final String description;
    
    ActivityType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this activity type is billable by default
     */
    public boolean isDefaultBillable() {
        return switch (this) {
            case CONSULTATION, DOCUMENT_REVIEW, RESEARCH, DRAFTING, 
                 HEARING, MEETING, NEGOTIATION, COURT_APPEARANCE, 
                 MEDIATION, SETTLEMENT, EVIDENCE_COLLECTION, 
                 EXPERT_CONSULTATION -> true;
            case PHONE_CALL, EMAIL, ADMINISTRATIVE, TRAVEL, 
                 FOLLOW_UP, FILING, CASE_CLOSURE -> false;
        };
    }
    
    /**
     * Check if this activity type requires court involvement
     */
    public boolean requiresCourt() {
        return switch (this) {
            case FILING, HEARING, COURT_APPEARANCE -> true;
            default -> false;
        };
    }
}