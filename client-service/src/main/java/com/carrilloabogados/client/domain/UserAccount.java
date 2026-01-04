package com.carrilloabogados.client.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad unificada para cuentas de usuario del sistema.
 * 
 * Soporta los 4 tipos de usuario:
 * - CLIENT: Clientes del bufete (login por email/password o magic link)
 * - LAWYER: Abogados @carrilloabgd.com (OAuth2 Google)
 * - ADMIN: Administradores @carrilloabgd.com (OAuth2 Google)
 * 
 * VISITOR no requiere cuenta en el sistema.
 */
@Entity
@Table(name = "user_accounts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UserAccount extends AbstractMappedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id", unique = true, nullable = false, updatable = false)
    private UUID accountId;

    // ========== Datos básicos ==========

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "first_name", length = 150)
    private String firstName;

    @Size(max = 150, message = "El apellido no puede exceder 150 caracteres")
    @Column(name = "last_name", length = 150)
    private String lastName;

    @Size(max = 500, message = "La URL de imagen no puede exceder 500 caracteres")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "phone", length = 20)
    private String phone;

    // ========== Rol y permisos ==========

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private RoleBasedAuthority role = RoleBasedAuthority.ROLE_CLIENT;

    // ========== Estado de la cuenta ==========

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    // ========== Autenticación ==========

    /**
     * Tipo de autenticación:
     * - LOCAL: Email/Password (para clientes)
     * - OAUTH2_GOOGLE: Google Workspace (para staff)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 20)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    /**
     * ID del proveedor OAuth2 (sub claim de Google)
     */
    @Column(name = "provider_id", length = 255)
    private String providerId;

    /**
     * Hash de la contraseña (solo para auth LOCAL)
     * Null para usuarios OAuth2
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    // ========== Relaciones opcionales ==========

    /**
     * Si es CLIENT, referencia al Lead original del que se convirtió
     */
    @Column(name = "source_lead_id")
    private UUID sourceLeadId;

    /**
     * Si es LAWYER, ID del abogado en el sistema legacy (si aplica)
     */
    @Column(name = "lawyer_id")
    private Integer lawyerId;

    // ========== Timestamps ==========

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    // ========== Métodos de utilidad ==========

    /**
     * Nombre completo del usuario
     */
    public String getFullName() {
        if (firstName == null && lastName == null) {
            return email;
        }
        return String.format("%s %s",
                firstName != null ? firstName : "",
                lastName != null ? lastName : "").trim();
    }

    /**
     * Verifica si el usuario es staff (abogado o admin)
     */
    public boolean isStaff() {
        return role != null && role.isStaff();
    }

    /**
     * Verifica si el usuario puede acceder al panel interno
     */
    public boolean canAccessInternalPanel() {
        return isActive && isEmailVerified && isStaff();
    }

    /**
     * Verifica si el email es del dominio corporativo
     */
    public boolean hasCompanyDomain() {
        return email != null && email.toLowerCase().endsWith("@carrilloabgd.com");
    }

    /**
     * Enum para tipos de autenticación
     */
    public enum AuthProvider {
        LOCAL, // Email/Password
        OAUTH2_GOOGLE // Google Workspace OAuth2
    }
}
