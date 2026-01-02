package com.carrilloabogados.client.dto.auth;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de nuevos clientes.
 * Los clientes se registran con email/password.
 * Staff (abogados/admin) se registran via OAuth2 Google.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String firstName;

    @Size(max = 150, message = "El apellido no puede exceder 150 caracteres")
    private String lastName;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;

    /**
     * Acepta términos y condiciones (requerido)
     */
    private Boolean acceptTerms;

    /**
     * Acepta política de privacidad (requerido)
     */
    private Boolean acceptPrivacy;
}
