package com.carrilloabogados.client.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuración de conexión a NATS.
 * 
 * NATS es el sistema de mensajería usado para comunicación
 * asíncrona entre microservicios y con n8n.
 * 
 * Propiedades configurables:
 * - nats.server: URL del servidor NATS
 * - nats.enabled: Habilitar/deshabilitar NATS (para desarrollo local)
 */
@Configuration
@Slf4j
public class NatsConfig {

    @Value("${nats.server:nats://localhost:4222}")
    private String natsServer;

    @Value("${nats.enabled:false}")
    private boolean natsEnabled;

    @Value("${nats.connection-name:client-service}")
    private String connectionName;

    @Value("${nats.reconnect.max-attempts:60}")
    private int maxReconnectAttempts;

    @Value("${nats.reconnect.wait-seconds:2}")
    private int reconnectWaitSeconds;

    private Connection natsConnection;

    /**
     * Bean de conexión NATS.
     * Si NATS está deshabilitado, retorna null y los publishers
     * deben manejar esto gracefully.
     */
    @Bean
    public Connection natsConnection() {
        if (!natsEnabled) {
            log.warn("*** NATS is disabled. Events will not be published. ***");
            return null;
        }

        try {
            Options options = new Options.Builder()
                    .server(natsServer)
                    .connectionName(connectionName)
                    .maxReconnects(maxReconnectAttempts)
                    .reconnectWait(Duration.ofSeconds(reconnectWaitSeconds))
                    .connectionListener((conn, type) -> {
                        log.info("*** NATS connection event: {} ***", type);
                    })
                    .errorListener(new io.nats.client.ErrorListener() {
                        @Override
                        public void errorOccurred(Connection conn, String error) {
                            log.error("*** NATS error: {} ***", error);
                        }

                        @Override
                        public void exceptionOccurred(Connection conn, Exception exp) {
                            log.error("*** NATS exception: {} ***", exp.getMessage());
                        }

                        @Override
                        public void slowConsumerDetected(Connection conn, io.nats.client.Consumer consumer) {
                            log.warn("*** NATS slow consumer detected ***");
                        }
                    })
                    .build();

            natsConnection = Nats.connect(options);
            log.info("*** Successfully connected to NATS at {} ***", natsServer);
            return natsConnection;

        } catch (IOException | InterruptedException e) {
            log.error("*** Failed to connect to NATS at {}: {} ***", natsServer, e.getMessage());
            log.warn("*** NATS events will be logged but not published ***");
            return null;
        }
    }

    @PreDestroy
    public void closeConnection() {
        if (natsConnection != null) {
            try {
                natsConnection.close();
                log.info("*** NATS connection closed ***");
            } catch (InterruptedException e) {
                log.error("*** Error closing NATS connection: {} ***", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

}
