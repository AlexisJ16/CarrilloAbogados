package com.carrilloabogados.client.helper;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.domain.Lead;
import com.carrilloabogados.client.dto.LeadDto;

/**
 * Helper para mapear entre Lead entity y LeadDto.
 */
public interface LeadMappingHelper {

    /**
     * Mapea de Lead entity a LeadDto.
     */
    static LeadDto map(final Lead lead) {
        if (lead == null) {
            return null;
        }

        return LeadDto.builder()
                .leadId(lead.getLeadId())
                // Datos de contacto
                .nombre(lead.getNombre())
                .email(lead.getEmail())
                .telefono(lead.getTelefono())
                .empresa(lead.getEmpresa())
                .cargo(lead.getCargo())
                .servicio(lead.getServicio())
                .mensaje(lead.getMensaje())
                // Scoring
                .leadScore(lead.getLeadScore())
                .leadCategory(lead.getLeadCategory())
                .leadStatus(lead.getLeadStatus())
                // Tracking
                .emailsSent(lead.getEmailsSent())
                .emailsOpened(lead.getEmailsOpened())
                .emailsClicked(lead.getEmailsClicked())
                .lastContact(lead.getLastContact())
                .lastEngagement(lead.getLastEngagement())
                // Origen y conversión
                .source(lead.getSource())
                .clientId(lead.getClientId())
                .convertedAt(lead.getConvertedAt())
                // Timestamps
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }

    /**
     * Mapea de LeadDto a Lead entity.
     * Usado principalmente para crear nuevos leads.
     */
    static Lead map(final LeadDto dto) {
        if (dto == null) {
            return null;
        }

        return Lead.builder()
                .leadId(dto.getLeadId())
                // Datos de contacto
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .empresa(dto.getEmpresa())
                .cargo(dto.getCargo())
                .servicio(dto.getServicio())
                .mensaje(dto.getMensaje())
                // Scoring - usar valores por defecto si no vienen
                .leadScore(dto.getLeadScore() != null ? dto.getLeadScore() : 0)
                .leadCategory(dto.getLeadCategory() != null ? dto.getLeadCategory() : LeadCategory.COLD)
                .leadStatus(dto.getLeadStatus() != null ? dto.getLeadStatus() : LeadStatus.NUEVO)
                // Tracking
                .emailsSent(dto.getEmailsSent() != null ? dto.getEmailsSent() : 0)
                .emailsOpened(dto.getEmailsOpened() != null ? dto.getEmailsOpened() : 0)
                .emailsClicked(dto.getEmailsClicked() != null ? dto.getEmailsClicked() : 0)
                .lastContact(dto.getLastContact())
                .lastEngagement(dto.getLastEngagement())
                // Origen y conversión
                .source(dto.getSource() != null ? dto.getSource() : LeadSource.WEBSITE)
                .clientId(dto.getClientId())
                .convertedAt(dto.getConvertedAt())
                .build();
    }

    /**
     * Actualiza una entidad Lead existente con datos de un DTO.
     * No sobrescribe campos de scoring/tracking (manejados por n8n).
     */
    static Lead updateFromDto(final Lead existing, final LeadDto dto) {
        if (dto == null) {
            return existing;
        }

        // Solo actualizar datos de contacto (no scoring/tracking)
        if (dto.getNombre() != null)
            existing.setNombre(dto.getNombre());
        if (dto.getEmail() != null)
            existing.setEmail(dto.getEmail());
        if (dto.getTelefono() != null)
            existing.setTelefono(dto.getTelefono());
        if (dto.getEmpresa() != null)
            existing.setEmpresa(dto.getEmpresa());
        if (dto.getCargo() != null)
            existing.setCargo(dto.getCargo());
        if (dto.getServicio() != null)
            existing.setServicio(dto.getServicio());
        if (dto.getMensaje() != null)
            existing.setMensaje(dto.getMensaje());
        if (dto.getSource() != null)
            existing.setSource(dto.getSource());

        return existing;
    }

    /**
     * Actualiza campos de scoring (usado por webhook de n8n).
     */
    static Lead updateScoring(final Lead existing, Integer score, LeadCategory category, LeadStatus status) {
        if (score != null)
            existing.setLeadScore(score);
        if (category != null)
            existing.setLeadCategory(category);
        if (status != null)
            existing.setLeadStatus(status);
        return existing;
    }

}
