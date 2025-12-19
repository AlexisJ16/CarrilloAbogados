package com.carrilloabogados.client.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.dto.LeadDto;

/**
 * Service interface para gestión de Leads.
 * 
 * Funcionalidades principales:
 * - Captura de leads desde formulario web
 * - CRUD básico
 * - Actualización de scoring (desde n8n)
 * - Búsquedas por categoría/estado
 */
public interface LeadService {

    // ========== CRUD Básico ==========

    /**
     * Obtiene todos los leads.
     */
    List<LeadDto> findAll();

    /**
     * Obtiene todos los leads con paginación.
     */
    Page<LeadDto> findAll(Pageable pageable);

    /**
     * Busca un lead por ID.
     */
    LeadDto findById(UUID leadId);

    /**
     * Busca un lead por email.
     */
    LeadDto findByEmail(String email);

    /**
     * Captura un nuevo lead desde el formulario web.
     * Este método:
     * 1. Valida datos
     * 2. Guarda el lead
     * 3. Emite evento "lead.capturado" a NATS
     */
    LeadDto capture(LeadDto leadDto);

    /**
     * Actualiza un lead existente.
     */
    LeadDto update(UUID leadId, LeadDto leadDto);

    /**
     * Elimina un lead por ID.
     */
    void deleteById(UUID leadId);

    // ========== Búsquedas por categoría/estado ==========

    /**
     * Busca leads por categoría (HOT/WARM/COLD).
     */
    List<LeadDto> findByCategory(LeadCategory category);

    /**
     * Busca leads por categoría con paginación.
     */
    Page<LeadDto> findByCategory(LeadCategory category, Pageable pageable);

    /**
     * Busca leads por estado del funnel.
     */
    List<LeadDto> findByStatus(LeadStatus status);

    /**
     * Busca leads por estado con paginación.
     */
    Page<LeadDto> findByStatus(LeadStatus status, Pageable pageable);

    // ========== Operaciones para n8n integration ==========

    /**
     * Actualiza el scoring de un lead (llamado desde webhook de n8n).
     */
    LeadDto updateScoring(UUID leadId, Integer score, LeadCategory category);

    /**
     * Actualiza el estado del lead en el funnel.
     */
    LeadDto updateStatus(UUID leadId, LeadStatus status);

    /**
     * Actualiza métricas de engagement de emails.
     */
    LeadDto updateEmailEngagement(UUID leadId, Integer sent, Integer opened, Integer clicked);

    /**
     * Convierte un lead a cliente.
     */
    LeadDto convertToClient(UUID leadId, Integer clientId);

    /**
     * Obtiene leads HOT pendientes de notificación.
     */
    List<LeadDto> findHotLeadsPending();

    /**
     * Obtiene leads inactivos para reactivación.
     * 
     * @param daysInactive Número de días sin engagement
     */
    List<LeadDto> findInactiveLeads(int daysInactive);

    /**
     * Busca leads por texto (nombre o empresa).
     */
    List<LeadDto> search(String query);

    /**
     * Verifica si un email ya existe como lead.
     */
    boolean existsByEmail(String email);

}
