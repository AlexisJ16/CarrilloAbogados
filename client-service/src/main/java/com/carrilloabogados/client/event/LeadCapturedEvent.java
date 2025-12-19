package com.carrilloabogados.client.event;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento emitido cuando se captura un nuevo lead.
 * 
 * Este evento se publica a NATS en el topic "carrillo.events.lead.capturado"
 * para que n8n-integration-service lo reenvíe al webhook de n8n Cloud.
 * 
 * n8n procesa este evento para:
 * 1. Calcular el lead score
 * 2. Clasificar como HOT/WARM/COLD
 * 3. Enviar respuesta automática con IA
 * 4. Iniciar secuencia de nurturing si aplica
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LeadCapturedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Tipo de evento para routing en n8n
     */
    @Builder.Default
    private String eventType = "lead.capturado";

    /**
     * Timestamp del evento
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * ID único del lead
     */
    private UUID leadId;

    // ========== Datos de contacto para scoring ==========

    private String nombre;
    private String email;
    private String telefono;
    private String empresa;
    private String cargo;
    private String servicio;
    private String mensaje;

    // ========== Metadatos para scoring ==========

    private LeadSource source;
    private LeadCategory initialCategory;
    private LeadStatus initialStatus;

    // ========== Campos calculados para ayudar a n8n ==========

    /**
     * Indica si el email es corporativo (no Gmail, Hotmail, etc.)
     */
    private boolean hasCorpEmail;

    /**
     * Indica si el cargo sugiere un C-Level
     */
    private boolean isCLevel;

    /**
     * Indica si el servicio es de alto valor (marca, litigio)
     */
    private boolean isHighValueService;

    /**
     * Longitud del mensaje para scoring
     */
    private int messageLength;

}
