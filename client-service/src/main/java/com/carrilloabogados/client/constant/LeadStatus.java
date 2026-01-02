package com.carrilloabogados.client.constant;

/**
 * Estado del lead en el funnel de conversión.
 * 
 * Flujo típico: NUEVO → NURTURING → MQL → SQL → CONVERTIDO
 * Alternativo: Cualquier estado → CHURNED (abandonado)
 */
public enum LeadStatus {

    NUEVO, // Lead recién capturado, pendiente de procesar
    NURTURING, // En secuencia de nurturing automatizada (12 emails)
    MQL, // Marketing Qualified Lead - cumple criterios de marketing
    SQL, // Sales Qualified Lead - calificado para contacto comercial
    CONVERTIDO, // Convertido a cliente (clientId asignado)
    CHURNED // Abandonado, no respondió a reactivación

}
