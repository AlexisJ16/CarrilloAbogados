package com.carrilloabogados.client.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.domain.Lead;
import com.carrilloabogados.client.dto.LeadDto;
import com.carrilloabogados.client.event.LeadCapturedEvent;
import com.carrilloabogados.client.exception.wrapper.LeadNotFoundException;
import com.carrilloabogados.client.helper.LeadMappingHelper;
import com.carrilloabogados.client.repository.LeadRepository;
import com.carrilloabogados.client.service.EventPublisher;
import com.carrilloabogados.client.service.LeadService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de Leads.
 * 
 * Maneja la captura, gestión y actualización de leads para
 * integración con n8n Cloud marketing automation.
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final EventPublisher eventPublisher;

    // ========== CRUD Básico ==========

    @Override
    public List<LeadDto> findAll() {
        log.info("*** LeadDto List, service; fetch all leads *");
        return leadRepository.findAll()
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Page<LeadDto> findAll(Pageable pageable) {
        log.info("*** LeadDto Page, service; fetch all leads with pagination *");
        return leadRepository.findAll(pageable)
                .map(LeadMappingHelper::map);
    }

    @Override
    public LeadDto findById(UUID leadId) {
        log.info("*** LeadDto, service; fetch lead by id: {} *", leadId);
        return leadRepository.findById(leadId)
                .map(LeadMappingHelper::map)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));
    }

    @Override
    public LeadDto findByEmail(String email) {
        log.info("*** LeadDto, service; fetch lead by email: {} *", email);
        return leadRepository.findByEmail(email)
                .map(LeadMappingHelper::map)
                .orElseThrow(() -> new LeadNotFoundException("Lead with email: %s not found".formatted(email)));
    }

    @Override
    public LeadDto capture(LeadDto leadDto) {
        log.info("*** LeadDto, service; capturing new lead from form - email: {} *", leadDto.getEmail());

        Lead leadToProcess;
        boolean isNewLead = false;

        // Verificar si ya existe un lead con este email
        if (leadRepository.existsByEmail(leadDto.getEmail())) {
            log.info("*** Lead with email {} already exists, updating message and re-triggering automation *",
                    leadDto.getEmail());
            // Obtener el lead existente y actualizar el mensaje (nuevo contacto del mismo
            // cliente)
            leadToProcess = leadRepository.findByEmail(leadDto.getEmail())
                    .orElseThrow(() -> new LeadNotFoundException("Lead not found"));
            // Actualizar el mensaje con el nuevo contacto
            leadToProcess.setMensaje(leadDto.getMensaje());
            leadToProcess.setServicio(leadDto.getServicio());
            leadToProcess = leadRepository.save(leadToProcess);
        } else {
            // Crear entidad desde DTO
            Lead lead = LeadMappingHelper.map(leadDto);
            leadToProcess = leadRepository.save(lead);
            isNewLead = true;
            log.info("*** Lead captured successfully with id: {} *", leadToProcess.getLeadId());
        }

        // SIEMPRE emitir evento "lead.capturado" a NATS para n8n
        // Esto permite re-procesar leads existentes (ej: cliente envía segundo mensaje)
        publishLeadCapturedEvent(leadToProcess);

        return LeadMappingHelper.map(leadToProcess);
    }

    /**
     * Construye y publica el evento LeadCapturedEvent a NATS.
     * El evento incluye datos pre-calculados para facilitar el scoring en n8n.
     */
    private void publishLeadCapturedEvent(Lead lead) {
        try {
            LeadCapturedEvent event = LeadCapturedEvent.builder()
                    .leadId(lead.getLeadId())
                    .nombre(lead.getNombre())
                    .email(lead.getEmail())
                    .telefono(lead.getTelefono())
                    .empresa(lead.getEmpresa())
                    .cargo(lead.getCargo())
                    .servicio(lead.getServicio())
                    .mensaje(lead.getMensaje())
                    .source(lead.getSource())
                    .initialCategory(lead.getLeadCategory())
                    .initialStatus(lead.getLeadStatus())
                    // Campos pre-calculados para scoring en n8n
                    .hasCorpEmail(lead.hasCorpEmail())
                    .isCLevel(lead.isCLevel())
                    .isHighValueService(lead.isHighValueService())
                    .messageLength(lead.getMensaje() != null ? lead.getMensaje().length() : 0)
                    .build();

            boolean published = eventPublisher.publishLeadCaptured(event);

            if (published) {
                log.info("*** Lead captured event published to NATS for lead: {} *", lead.getLeadId());
            } else {
                log.warn("*** Lead captured event NOT published (NATS unavailable) for lead: {} *", lead.getLeadId());
            }
        } catch (Exception e) {
            // No fallar la captura del lead si falla la publicación del evento
            log.error("*** Failed to publish lead captured event: {} *", e.getMessage());
        }
    }

    @Override
    public LeadDto update(UUID leadId, LeadDto leadDto) {
        log.info("*** LeadDto, service; update lead with id: {} *", leadId);

        Lead existing = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));

        // Actualizar solo datos de contacto (no scoring)
        Lead updated = LeadMappingHelper.updateFromDto(existing, leadDto);

        return LeadMappingHelper.map(leadRepository.save(updated));
    }

    @Override
    public void deleteById(UUID leadId) {
        log.info("*** Void, service; delete lead by id: {} *", leadId);

        if (!leadRepository.existsById(leadId)) {
            throw new LeadNotFoundException("Lead with id: %s not found".formatted(leadId));
        }

        leadRepository.deleteById(leadId);
    }

    // ========== Búsquedas por categoría/estado ==========

    @Override
    public List<LeadDto> findByCategory(LeadCategory category) {
        log.info("*** LeadDto List, service; fetch leads by category: {} *", category);
        return leadRepository.findByLeadCategory(category)
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Page<LeadDto> findByCategory(LeadCategory category, Pageable pageable) {
        log.info("*** LeadDto Page, service; fetch leads by category: {} with pagination *", category);
        return leadRepository.findByLeadCategory(category, pageable)
                .map(LeadMappingHelper::map);
    }

    @Override
    public List<LeadDto> findByStatus(LeadStatus status) {
        log.info("*** LeadDto List, service; fetch leads by status: {} *", status);
        return leadRepository.findByLeadStatus(status)
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Page<LeadDto> findByStatus(LeadStatus status, Pageable pageable) {
        log.info("*** LeadDto Page, service; fetch leads by status: {} with pagination *", status);
        return leadRepository.findByLeadStatus(status, pageable)
                .map(LeadMappingHelper::map);
    }

    // ========== Operaciones para n8n integration ==========

    @Override
    public LeadDto updateScoring(UUID leadId, Integer score, LeadCategory category) {
        log.info("*** LeadDto, service; update scoring for lead: {} - score: {}, category: {} *",
                leadId, score, category);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));

        LeadMappingHelper.updateScoring(lead, score, category, null);

        return LeadMappingHelper.map(leadRepository.save(lead));
    }

    @Override
    public LeadDto updateStatus(UUID leadId, LeadStatus status) {
        log.info("*** LeadDto, service; update status for lead: {} - status: {} *", leadId, status);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));

        lead.setLeadStatus(status);

        // Si pasa a NURTURING, registrar inicio de contacto
        if (status == LeadStatus.NURTURING && lead.getLastContact() == null) {
            lead.setLastContact(Instant.now());
        }

        return LeadMappingHelper.map(leadRepository.save(lead));
    }

    @Override
    public LeadDto updateEmailEngagement(UUID leadId, Integer sent, Integer opened, Integer clicked) {
        log.info("*** LeadDto, service; update email engagement for lead: {} *", leadId);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));

        if (sent != null)
            lead.setEmailsSent(sent);
        if (opened != null)
            lead.setEmailsOpened(opened);
        if (clicked != null)
            lead.setEmailsClicked(clicked);

        // Actualizar last engagement si hubo interacción
        if ((opened != null && opened > 0) || (clicked != null && clicked > 0)) {
            lead.setLastEngagement(Instant.now());
        }

        return LeadMappingHelper.map(leadRepository.save(lead));
    }

    @Override
    public LeadDto convertToClient(UUID leadId, Integer clientId) {
        log.info("*** LeadDto, service; converting lead {} to client {} *", leadId, clientId);

        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead with id: %s not found".formatted(leadId)));

        lead.setClientId(clientId);
        lead.setLeadStatus(LeadStatus.CONVERTIDO);
        lead.setConvertedAt(Instant.now());

        return LeadMappingHelper.map(leadRepository.save(lead));
    }

    @Override
    public List<LeadDto> findHotLeadsPending() {
        log.info("*** LeadDto List, service; fetch HOT leads pending notification *");
        return leadRepository.findHotLeadsPendingNotification()
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<LeadDto> findInactiveLeads(int daysInactive) {
        log.info("*** LeadDto List, service; fetch leads inactive for {} days *", daysInactive);
        Instant cutoffDate = Instant.now().minus(daysInactive, ChronoUnit.DAYS);
        return leadRepository.findInactiveLeads(cutoffDate)
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<LeadDto> search(String query) {
        log.info("*** LeadDto List, service; search leads by query: {} *", query);
        return leadRepository.searchByNombreOrEmpresa(query)
                .stream()
                .map(LeadMappingHelper::map)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return leadRepository.existsByEmail(email);
    }

}
