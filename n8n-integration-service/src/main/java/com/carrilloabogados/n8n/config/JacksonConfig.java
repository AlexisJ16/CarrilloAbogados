package com.carrilloabogados.n8n.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuración de Jackson para serialización/deserialización JSON.
 * Importante para manejar tipos java.time (Instant, LocalDateTime, etc.)
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registrar módulo para tipos java.time
        mapper.registerModule(new JavaTimeModule());

        // No serializar fechas como timestamps (array de números)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Permitir deserializar incluso si hay campos desconocidos
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
