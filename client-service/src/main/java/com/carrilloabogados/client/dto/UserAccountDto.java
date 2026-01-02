package com.carrilloabogados.client.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.carrilloabogados.client.domain.RoleBasedAuthority;
import com.carrilloabogados.client.domain.UserAccount.AuthProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
 * DTO para UserAccount.
 * 
 * Usado para:
 * - Registro de usuarios (clientes)
 * - Creación de cuentas de staff (abogados/admin)
 * - Respuestas de autenticación
 * - Perfil de usuario
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAccountDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.NON_NULL)
    private UUID accountId;

    // ========== Datos básicos ==========

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String firstName;

    @Size(max = 150, message = "El apellido no puede exceder 150 caracteres")
    private String lastName;

    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    private String avatarUrl;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;

    // ========== Rol y estado ==========

    @JsonInclude(Include.NON_NULL)
    private RoleBasedAuthority role;

    @JsonInclude(Include.NON_NULL)
    private Boolean isActive;

    @JsonInclude(Include.NON_NULL)
    private Boolean isEmailVerified;

    @JsonInclude(Include.NON_NULL)
    private AuthProvider authProvider;

    // ========== Solo para registro ==========

    /**
     * Contraseña en texto plano (solo para registro/cambio, nunca se retorna)
     */
    @JsonIgnore
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;

    // ========== Campos calculados (solo lectura) ==========

    @JsonInclude(Include.NON_NULL)
    private String fullName;

    @JsonInclude(Include.NON_NULL)
    private Boolean isStaff;

    // ========== Timestamps ==========

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant lastLoginAt;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant createdAt;

    @JsonInclude(Include.NON_NULL)
    @JsonFormat(shape = Shape.STRING)
    private Instant updatedAt;
}
