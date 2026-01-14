package com.carrilloabogados.n8n.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler para capturar errores de validación y
 * deserialización.
 * Ayuda a diagnosticar problemas con callbacks de n8n.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Captura errores de deserialización JSON (400 Bad Request típicos).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        log.error("❌ JSON Parse Error: {}", ex.getMessage());
        log.error("❌ Root cause: {}", ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Unknown");

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Invalid JSON format");
        error.put("message", ex.getMessage());
        error.put("root_cause", ex.getRootCause() != null ? ex.getRootCause().getMessage() : null);
        error.put("timestamp", java.time.Instant.now());

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Captura errores de validación de Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        log.error("❌ Validation Error: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Validation failed");
        error.put("field_errors", ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList());
        error.put("timestamp", java.time.Instant.now());

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Captura cualquier otro error interno.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        log.error("❌ Unexpected Error: {}", ex.getMessage(), ex);

        Map<String, Object> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", ex.getMessage());
        error.put("type", ex.getClass().getSimpleName());
        error.put("timestamp", java.time.Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
