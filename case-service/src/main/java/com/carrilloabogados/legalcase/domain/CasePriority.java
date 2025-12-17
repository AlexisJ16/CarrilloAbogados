package com.carrilloabogados.legalcase.domain;

/**
 * Enumeration representing the priority levels for legal cases
 */
public enum CasePriority {
    /**
     * Low priority case - standard processing
     */
    LOW("Baja", 1),
    
    /**
     * Normal priority case - default level
     */
    NORMAL("Normal", 2),
    
    /**
     * High priority case - expedited processing
     */
    HIGH("Alta", 3),
    
    /**
     * Urgent case - immediate attention required
     */
    URGENT("Urgente", 4),
    
    /**
     * Critical case - highest priority
     */
    CRITICAL("Crítica", 5);
    
    private final String displayName;
    private final int level;
    
    CasePriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Check if this priority is higher than another
     */
    public boolean isHigherThan(CasePriority other) {
        return this.level > other.level;
    }
    
    /**
     * Check if this priority requires immediate attention
     */
    public boolean requiresImmediateAttention() {
        return this == URGENT || this == CRITICAL;
    }
}