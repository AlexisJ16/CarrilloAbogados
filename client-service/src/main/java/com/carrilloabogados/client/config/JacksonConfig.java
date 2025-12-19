package com.carrilloabogados.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuración de Jackson para serialización/deserialización JSON.
 * 
 * Registra el módulo JavaTimeModule para soporte de java.time.Instant
 * y otros tipos de fecha/hora de Java 8+.
 * 
 * @author Alexis - Carrillo Abogados
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registrar módulo para java.time (Instant, LocalDate, etc.)
        mapper.registerModule(new JavaTimeModule());

        // Escribir fechas como strings ISO-8601, no como timestamps numéricos
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
