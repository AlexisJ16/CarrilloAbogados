package com.carrilloabogados.client.constant;

/**
 * Origen del lead - de dónde llegó el prospecto.
 * 
 * Importante para tracking de campañas y ROI de canales.
 */
public enum LeadSource {

    WEBSITE, // Formulario de contacto del sitio web
    REFERRAL, // Referido por cliente existente
    LINKEDIN, // Campaña o contacto vía LinkedIn
    EVENTO, // Evento presencial o webinar
    GOOGLE_ADS, // Campaña de Google Ads
    TELEFONO, // Llamada telefónica entrante
    OTRO // Otra fuente no categorizada

}
