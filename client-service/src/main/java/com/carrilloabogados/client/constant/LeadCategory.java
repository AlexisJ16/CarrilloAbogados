package com.carrilloabogados.client.constant;

/**
 * Categorización del lead basada en el score calculado por n8n.
 * 
 * HOT: ≥70 pts → Notificación inmediata al abogado
 * WARM: 40-69 pts → Nurturing con IA
 * COLD: <40 pts → Respuesta genérica
 */
public enum LeadCategory {

    HOT, // Score ≥ 70 - Alta prioridad, contactar inmediatamente
    WARM, // Score 40-69 - Nurturing automatizado con IA
    COLD // Score < 40 - Respuesta genérica, bajo potencial

}
