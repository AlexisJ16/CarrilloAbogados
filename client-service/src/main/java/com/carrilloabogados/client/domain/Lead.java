package com.carrilloabogados.client.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;

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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad Lead para captura de prospectos desde el portal web.
 * 
 * Integración con n8n Cloud:
 * - Al crear un lead, se emite evento "lead.capturado" a NATS
 * - n8n calcula el leadScore y actualiza via webhook
 * - HOT leads (≥70 pts) notifican inmediatamente al abogado
 * 
 * Campos de scoring son actualizados por n8n, no por el sistema.
 */
@Entity
@Table(name = "leads")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class Lead extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lead_id", unique = true, nullable = false, updatable = false)
    private UUID leadId;

    // ========== Datos de contacto (capturados del formulario) ==========

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 200, message = "El nombre de empresa no puede exceder 200 caracteres")
    @Column(name = "empresa", length = 200)
    private String empresa;

    @Size(max = 100, message = "El cargo no puede exceder 100 caracteres")
    @Column(name = "cargo", length = 100)
    private String cargo;

    @Size(max = 100, message = "El servicio no puede exceder 100 caracteres")
    @Column(name = "servicio", length = 100)
    private String servicio;

    @Size(max = 2000, message = "El mensaje no puede exceder 2000 caracteres")
    @Column(name = "mensaje", length = 2000)
    private String mensaje;

    // ========== Scoring (calculado por n8n) ==========

    /**
     * Score del lead (0-100), calculado por n8n.
     * Criterios:
     * - Base: 30 pts
     * - Servicio "marca"/"litigio": +20 pts
     * - Mensaje > 50 chars: +10 pts
     * - Tiene teléfono: +10 pts
     * - Tiene empresa: +10 pts
     * - Email corporativo: +10 pts
     * - Cargo C-Level: +20 pts
     */
    @Builder.Default
    @Column(name = "lead_score")
    private Integer leadScore = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "lead_category", length = 10)
    private LeadCategory leadCategory = LeadCategory.COLD;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "lead_status", length = 20)
    private LeadStatus leadStatus = LeadStatus.NUEVO;

    // ========== Tracking de engagement (actualizado por n8n) ==========

    @Builder.Default
    @Column(name = "emails_sent")
    private Integer emailsSent = 0;

    @Builder.Default
    @Column(name = "emails_opened")
    private Integer emailsOpened = 0;

    @Builder.Default
    @Column(name = "emails_clicked")
    private Integer emailsClicked = 0;

    @Column(name = "last_contact")
    private Instant lastContact;

    @Column(name = "last_engagement")
    private Instant lastEngagement;

    // ========== Origen y conversión ==========

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20)
    private LeadSource source = LeadSource.WEBSITE;

    /**
     * Si el lead se convierte en cliente, se guarda el ID del cliente.
     * Este campo vincula con la entidad User/Client.
     */
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "converted_at")
    private Instant convertedAt;

    // ========== Lifecycle callbacks ==========

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        setCreatedAt(now);
        setUpdatedAt(now);
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(Instant.now());
    }

    // ========== Métodos de utilidad ==========

    /**
     * Determina si el lead tiene un email corporativo (no Gmail, Hotmail, etc.)
     */
    public boolean hasCorpEmail() {
        if (email == null)
            return false;
        String lowerEmail = email.toLowerCase();
        return !lowerEmail.endsWith("@gmail.com")
                && !lowerEmail.endsWith("@hotmail.com")
                && !lowerEmail.endsWith("@outlook.com")
                && !lowerEmail.endsWith("@yahoo.com")
                && !lowerEmail.endsWith("@live.com");
    }

    /**
     * Determina si el cargo indica un C-Level
     */
    public boolean isCLevel() {
        if (cargo == null)
            return false;
        String lowerCargo = cargo.toLowerCase();
        return lowerCargo.contains("ceo")
                || lowerCargo.contains("cfo")
                || lowerCargo.contains("cto")
                || lowerCargo.contains("coo")
                || lowerCargo.contains("director")
                || lowerCargo.contains("gerente general")
                || lowerCargo.contains("presidente")
                || lowerCargo.contains("fundador")
                || lowerCargo.contains("founder");
    }

    /**
     * Determina si el servicio es de alto valor (marca o litigio)
     */
    public boolean isHighValueService() {
        if (servicio == null)
            return false;
        String lowerServicio = servicio.toLowerCase();
        return lowerServicio.contains("marca")
                || lowerServicio.contains("litigio")
                || lowerServicio.contains("competencia")
                || lowerServicio.contains("propiedad industrial");
    }

}
