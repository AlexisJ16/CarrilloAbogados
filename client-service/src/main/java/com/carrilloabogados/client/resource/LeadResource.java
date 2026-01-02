package com.carrilloabogados.client.resource;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.dto.LeadDto;
import com.carrilloabogados.client.dto.response.collection.DtoCollectionResponse;
import com.carrilloabogados.client.service.LeadService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller para gestión de Leads.
 * 
 * Endpoints principales:
 * - POST /api/leads - Captura de lead desde formulario web
 * - GET /api/leads - Lista todos los leads
 * - GET /api/leads/{id} - Obtiene un lead por ID
 * - PUT /api/leads/{id} - Actualiza un lead
 * - DELETE /api/leads/{id} - Elimina un lead
 * 
 * Endpoints para n8n integration:
 * - PATCH /api/leads/{id}/scoring - Actualiza scoring (desde webhook n8n)
 * - PATCH /api/leads/{id}/status - Actualiza estado en funnel
 * - PATCH /api/leads/{id}/engagement - Actualiza métricas de email
 * - POST /api/leads/{id}/convert - Convierte lead a cliente
 * 
 * Endpoints de búsqueda:
 * - GET /api/leads/category/{category} - Por categoría
 * - GET /api/leads/status/{status} - Por estado
 * - GET /api/leads/search - Búsqueda por texto
 * - GET /api/leads/hot - Leads HOT pendientes
 * - GET /api/leads/inactive - Leads inactivos
 */
@RestController
@RequestMapping(value = { "/api/leads" })
@Slf4j
@RequiredArgsConstructor
public class LeadResource {

    private final LeadService leadService;

    // ========== CRUD Básico ==========

    /**
     * Lista todos los leads.
     */
    @GetMapping
    public ResponseEntity<DtoCollectionResponse<LeadDto>> findAll() {
        log.info("*** LeadDto List, controller; fetch all leads *");
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.findAll()));
    }

    /**
     * Lista leads con paginación.
     */
    @GetMapping("/paged")
    public ResponseEntity<Page<LeadDto>> findAllPaged(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("*** LeadDto Page, controller; fetch all leads with pagination *");
        return ResponseEntity.ok(leadService.findAll(pageable));
    }

    /**
     * Obtiene un lead por ID.
     */
    @GetMapping("/{leadId}")
    public ResponseEntity<LeadDto> findById(@PathVariable @NotBlank final String leadId) {
        log.info("*** LeadDto, controller; fetch lead by id: {} *", leadId);
        return ResponseEntity.ok(leadService.findById(UUID.fromString(leadId.strip())));
    }

    /**
     * Obtiene un lead por email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<LeadDto> findByEmail(@PathVariable @NotBlank final String email) {
        log.info("*** LeadDto, controller; fetch lead by email: {} *", email);
        return ResponseEntity.ok(leadService.findByEmail(email.strip()));
    }

    /**
     * Captura un nuevo lead desde formulario web.
     * Este es el endpoint principal para el portal público.
     * 
     * Después de guardar, se emite evento "lead.capturado" a NATS
     * para que n8n procese el scoring y responda automáticamente.
     */
    @PostMapping
    public ResponseEntity<LeadDto> capture(
            @RequestBody @NotNull @Valid final LeadDto leadDto) {
        log.info("*** LeadDto, controller; capturing new lead - email: {} *", leadDto.getEmail());
        LeadDto captured = leadService.capture(leadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(captured);
    }

    /**
     * Actualiza un lead existente.
     */
    @PutMapping("/{leadId}")
    public ResponseEntity<LeadDto> update(
            @PathVariable @NotBlank final String leadId,
            @RequestBody @NotNull @Valid final LeadDto leadDto) {
        log.info("*** LeadDto, controller; update lead with id: {} *", leadId);
        return ResponseEntity.ok(leadService.update(UUID.fromString(leadId.strip()), leadDto));
    }

    /**
     * Elimina un lead por ID.
     */
    @DeleteMapping("/{leadId}")
    public ResponseEntity<Void> deleteById(@PathVariable @NotBlank final String leadId) {
        log.info("*** Void, controller; delete lead by id: {} *", leadId);
        leadService.deleteById(UUID.fromString(leadId.strip()));
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica si un email ya existe como lead.
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam @NotBlank final String email) {
        log.info("*** Boolean, controller; check if email exists: {} *", email);
        return ResponseEntity.ok(leadService.existsByEmail(email.strip()));
    }

    // ========== Endpoints para n8n integration ==========

    /**
     * Actualiza el scoring de un lead.
     * Llamado desde webhook de n8n después de calcular el score.
     */
    @PatchMapping("/{leadId}/scoring")
    public ResponseEntity<LeadDto> updateScoring(
            @PathVariable @NotBlank final String leadId,
            @RequestParam @NotNull final Integer score,
            @RequestParam @NotNull final LeadCategory category) {
        log.info("*** LeadDto, controller; update scoring for lead: {} *", leadId);
        return ResponseEntity.ok(leadService.updateScoring(
                UUID.fromString(leadId.strip()), score, category));
    }

    /**
     * Actualiza el estado del lead en el funnel.
     */
    @PatchMapping("/{leadId}/status")
    public ResponseEntity<LeadDto> updateStatus(
            @PathVariable @NotBlank final String leadId,
            @RequestParam @NotNull final LeadStatus status) {
        log.info("*** LeadDto, controller; update status for lead: {} *", leadId);
        return ResponseEntity.ok(leadService.updateStatus(
                UUID.fromString(leadId.strip()), status));
    }

    /**
     * Actualiza métricas de engagement de emails.
     * Llamado cuando n8n recibe eventos de Mailersend.
     */
    @PatchMapping("/{leadId}/engagement")
    public ResponseEntity<LeadDto> updateEngagement(
            @PathVariable @NotBlank final String leadId,
            @RequestParam(required = false) final Integer sent,
            @RequestParam(required = false) final Integer opened,
            @RequestParam(required = false) final Integer clicked) {
        log.info("*** LeadDto, controller; update engagement for lead: {} *", leadId);
        return ResponseEntity.ok(leadService.updateEmailEngagement(
                UUID.fromString(leadId.strip()), sent, opened, clicked));
    }

    /**
     * Convierte un lead a cliente.
     * Se asigna el clientId y se marca como CONVERTIDO.
     */
    @PostMapping("/{leadId}/convert")
    public ResponseEntity<LeadDto> convertToClient(
            @PathVariable @NotBlank final String leadId,
            @RequestParam @NotNull final Integer clientId) {
        log.info("*** LeadDto, controller; converting lead {} to client {} *", leadId, clientId);
        return ResponseEntity.ok(leadService.convertToClient(
                UUID.fromString(leadId.strip()), clientId));
    }

    // ========== Endpoints de búsqueda ==========

    /**
     * Lista leads por categoría (HOT/WARM/COLD).
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<DtoCollectionResponse<LeadDto>> findByCategory(
            @PathVariable @NotNull final LeadCategory category) {
        log.info("*** LeadDto List, controller; fetch leads by category: {} *", category);
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.findByCategory(category)));
    }

    /**
     * Lista leads por categoría con paginación.
     */
    @GetMapping("/category/{category}/paged")
    public ResponseEntity<Page<LeadDto>> findByCategoryPaged(
            @PathVariable @NotNull final LeadCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("*** LeadDto Page, controller; fetch leads by category: {} with pagination *", category);
        return ResponseEntity.ok(leadService.findByCategory(category, pageable));
    }

    /**
     * Lista leads por estado del funnel.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<DtoCollectionResponse<LeadDto>> findByStatus(
            @PathVariable @NotNull final LeadStatus status) {
        log.info("*** LeadDto List, controller; fetch leads by status: {} *", status);
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.findByStatus(status)));
    }

    /**
     * Lista leads por estado con paginación.
     */
    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<LeadDto>> findByStatusPaged(
            @PathVariable @NotNull final LeadStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("*** LeadDto Page, controller; fetch leads by status: {} with pagination *", status);
        return ResponseEntity.ok(leadService.findByStatus(status, pageable));
    }

    /**
     * Búsqueda de leads por texto (nombre o empresa).
     */
    @GetMapping("/search")
    public ResponseEntity<DtoCollectionResponse<LeadDto>> search(
            @RequestParam @NotBlank final String query) {
        log.info("*** LeadDto List, controller; search leads by query: {} *", query);
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.search(query.strip())));
    }

    /**
     * Obtiene leads HOT pendientes de notificación al abogado.
     */
    @GetMapping("/hot")
    public ResponseEntity<DtoCollectionResponse<LeadDto>> findHotLeadsPending() {
        log.info("*** LeadDto List, controller; fetch HOT leads pending notification *");
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.findHotLeadsPending()));
    }

    /**
     * Obtiene leads inactivos para reactivación.
     */
    @GetMapping("/inactive")
    public ResponseEntity<DtoCollectionResponse<LeadDto>> findInactiveLeads(
            @RequestParam(defaultValue = "30") final int days) {
        log.info("*** LeadDto List, controller; fetch leads inactive for {} days *", days);
        return ResponseEntity.ok(new DtoCollectionResponse<>(leadService.findInactiveLeads(days)));
    }

}
