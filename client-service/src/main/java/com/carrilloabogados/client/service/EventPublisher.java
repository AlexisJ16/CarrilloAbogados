package com.carrilloabogados.client.service;

import com.carrilloabogados.client.event.LeadCapturedEvent;

/**
 * Interface para publicar eventos a NATS.
 * 
 * Los eventos siguen el patrón de topics:
 * carrillo.events.<domain>.<action>
 * 
 * Ejemplos:
 * - carrillo.events.lead.capturado
 * - carrillo.events.lead.scored
 * - carrillo.events.cliente.creado
 */
public interface EventPublisher {

    /**
     * Topic base para eventos de leads
     */
    String TOPIC_LEAD_CAPTURED = "carrillo.events.lead.capturado";
    String TOPIC_LEAD_SCORED = "carrillo.events.lead.scored";
    String TOPIC_LEAD_HOT = "carrillo.events.lead.hot";

    /**
     * Publica un evento de lead capturado.
     * 
     * @param event El evento a publicar
     * @return true si se publicó exitosamente, false si NATS no está disponible
     */
    boolean publishLeadCaptured(LeadCapturedEvent event);

    /**
     * Publica un evento genérico a un topic específico.
     * 
     * @param topic   El topic donde publicar
     * @param payload El objeto a serializar y publicar
     * @return true si se publicó exitosamente
     */
    boolean publish(String topic, Object payload);

    /**
     * Verifica si NATS está conectado y disponible.
     */
    boolean isConnected();

}
