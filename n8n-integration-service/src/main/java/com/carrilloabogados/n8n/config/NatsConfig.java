package com.carrilloabogados.n8n.config;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import jakarta.annotation.PreDestroy;

/**
 * Configuración de conexión NATS para escuchar eventos de los microservicios.
 */
@Configuration
@ConditionalOnProperty(name = "nats.enabled", havingValue = "true", matchIfMissing = true)
public class NatsConfig {

    private static final Logger log = LoggerFactory.getLogger(NatsConfig.class);

    @Value("${nats.server:nats://localhost:4222}")
    private String natsServer;

    private Connection natsConnection;

    @Bean
    public Connection natsConnection() {
        try {
            Options options = new Options.Builder()
                    .server(natsServer)
                    .connectionName("n8n-integration-service")
                    .reconnectWait(Duration.ofSeconds(2))
                    .maxReconnects(-1) // Infinite reconnects
                    .connectionTimeout(Duration.ofSeconds(10))
                    .pingInterval(Duration.ofSeconds(30))
                    .build();

            natsConnection = Nats.connect(options);
            log.info("✅ Connected to NATS server at: {}", natsServer);
            return natsConnection;

        } catch (IOException | InterruptedException e) {
            log.warn("⚠️ Could not connect to NATS server at {}. Events will not be processed. Error: {}",
                    natsServer, e.getMessage());
            return null;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (natsConnection != null) {
            try {
                natsConnection.close();
                log.info("NATS connection closed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error closing NATS connection", e);
            }
        }
    }
}
