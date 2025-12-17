package com.carrilloabogados.legalcase.domain;

/**
 * Enumeration representing the different statuses a legal case can have
 */
public enum CaseStatus {
    /**
     * Case has been created but work has not started
     */
    PENDING("Pendiente"),
    
    /**
     * Case is currently being worked on
     */
    IN_PROGRESS("En Progreso"),
    
    /**
     * Case is waiting for external action (client, court, opposing party)
     */
    WAITING("Esperando"),
    
    /**
     * Case is under review by senior lawyer or partner
     */
    UNDER_REVIEW("En Revisión"),
    
    /**
     * Case has been temporarily suspended
     */
    SUSPENDED("Suspendido"),
    
    /**
     * Case has been completed successfully
     */
    COMPLETED("Completado"),
    
    /**
     * Case was closed without completion
     */
    CLOSED("Cerrado"),
    
    /**
     * Case was cancelled by client or circumstances
     */
    CANCELLED("Cancelado");
    
    private final String displayName;
    
    CaseStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this status represents an active case
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == WAITING || this == UNDER_REVIEW;
    }
    
    /**
     * Check if this status represents a closed case
     */
    public boolean isClosed() {
        return this == COMPLETED || this == CLOSED || this == CANCELLED;
    }
}