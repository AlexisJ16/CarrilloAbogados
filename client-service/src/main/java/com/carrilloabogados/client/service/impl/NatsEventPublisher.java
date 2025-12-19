package com.carrilloabogados.client.service.impl;

import java.nio.charset.StandardCharsets;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.carrilloabogados.client.event.LeadCapturedEvent;
import com.carrilloabogados.client.service.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de EventPublisher usando NATS.
 * 
 * Publica eventos a NATS para que n8n-integration-service
 * los reenvíe al webhook de n8n Cloud.
 * 
 * Si NATS no está disponible (natsConnection es null),
 * los eventos se loguean pero no se pierden - pueden
 * ser reprocesados desde la base de datos si es necesario.
 */
@Service
@Slf4j
public class NatsEventPublisher implements EventPublisher {

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    public NatsEventPublisher(@Nullable Connection natsConnection) {
        this.natsConnection = natsConnection;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public boolean publishLeadCaptured(LeadCapturedEvent event) {
        log.info("*** Publishing lead captured event for lead: {} ***", event.getLeadId());
        return publish(TOPIC_LEAD_CAPTURED, event);
    }

    @Override
    public boolean publish(String topic, Object payload) {
        // Serializar payload a JSON
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("*** Failed to serialize event payload: {} ***", e.getMessage());
            return false;
        }

        // Si NATS no está disponible, solo loguear
        if (natsConnection == null) {
            log.warn("*** NATS not available. Event logged but not published ***");
            log.info("*** [NATS-MOCK] Topic: {} | Payload: {} ***", topic, json);
            return false;
        }

        // Verificar conexión
        if (natsConnection.getStatus() != Connection.Status.CONNECTED) {
            log.warn("*** NATS not connected (status: {}). Event logged but not published ***",
                    natsConnection.getStatus());
            log.info("*** [NATS-QUEUE] Topic: {} | Payload: {} ***", topic, json);
            return false;
        }

        try {
            // Publicar a NATS
            natsConnection.publish(topic, json.getBytes(StandardCharsets.UTF_8));
            log.info("*** Event published to NATS topic: {} ***", topic);
            log.debug("*** Event payload: {} ***", json);
            return true;

        } catch (Exception e) {
            log.error("*** Failed to publish event to NATS topic {}: {} ***", topic, e.getMessage());
            log.info("*** [NATS-FAILED] Topic: {} | Payload: {} ***", topic, json);
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return natsConnection != null &&
                natsConnection.getStatus() == Connection.Status.CONNECTED;
    }

}
