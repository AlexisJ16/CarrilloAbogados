package com.carrilloabogados.client.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para transferencia de datos de Lead.
 * 
 * Usado para:
 * - Captura desde formulario web (POST /api/leads)
 * - Respuestas de API
 * - Actualización de scoring desde n8n webhooks
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LeadDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.NON_NULL)
    private UUID leadId;

    // ========== Datos de contacto ==========

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Size(max = 200, message = "El nombre de empresa no puede exceder 200 caracteres")
    private String empresa;

    @Size(max = 100, message = "El cargo no puede exceder 100 caracteres")
    private String cargo;

    @Size(max = 100, message = "El servicio no puede exceder 100 caracteres")
    private String servicio;

    @Size(max = 2000, message = "El mensaje no puede exceder 2000 caracteres")
    private String mensaje;

    // ========== Scoring (solo lectura para API, actualizado por n8n) ==========

    @JsonInclude(Include.NON_NULL)
    private Integer leadScore;

    @JsonInclude(Include.NON_NULL)
    private LeadCategory leadCategory;

    @JsonInclude(Include.NON_NULL)
    private LeadStatus leadStatus;

    // ========== Tracking de engagement ==========

    @JsonInclude(Include.NON_NULL)
    private Integer emailsSent;

    @JsonInclude(Include.NON_NULL)
    private Integer emailsOpened;

    @JsonInclude(Include.NON_NULL)
    private Integer emailsClicked;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant lastContact;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant lastEngagement;

    // ========== Origen y conversión ==========

    @JsonInclude(Include.NON_NULL)
    private LeadSource source;

    @JsonInclude(Include.NON_NULL)
    private Integer clientId;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant convertedAt;

    // ========== Timestamps ==========

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant createdAt;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant updatedAt;

}
