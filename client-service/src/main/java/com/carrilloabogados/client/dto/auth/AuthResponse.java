package com.carrilloabogados.client.dto.auth;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.carrilloabogados.client.domain.RoleBasedAuthority;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación exitosa.
 * Contiene el token JWT y datos básicos del usuario.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AuthResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Token JWT para autenticación
     */
    private String accessToken;

    /**
     * Tipo de token (siempre "Bearer")
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Segundos hasta expiración del token
     */
    private Long expiresIn;

    /**
     * Token de refresco (para obtener nuevo access token)
     */
    private String refreshToken;

    // ========== Datos del usuario ==========

    private UUID accountId;

    private String email;

    private String firstName;

    private String lastName;

    private String fullName;

    private String avatarUrl;

    private RoleBasedAuthority role;

    private Boolean isStaff;

    @JsonFormat(shape = Shape.STRING)
    private Instant lastLoginAt;

    // ========== Permisos ==========

    /**
     * Lista de permisos/scopes del usuario
     */
    private String[] permissions;
}
