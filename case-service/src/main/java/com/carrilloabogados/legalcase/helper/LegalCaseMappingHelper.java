package com.carrilloabogados.legalcase.helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import com.carrilloabogados.legalcase.domain.LegalCase;
import com.carrilloabogados.legalcase.dto.LegalCaseDto;

public interface LegalCaseMappingHelper {

    public static LegalCaseDto map(final LegalCase legalCase) {
        return LegalCaseDto.builder()
                .caseId(legalCase.getCaseId())
                .caseNumber(legalCase.getCaseNumber())
                .title(legalCase.getTitle())
                .description(legalCase.getDescription())
                .clientId(legalCase.getClientId())
                .responsibleLawyer(legalCase.getResponsibleLawyer())
                .caseType(legalCase.getCaseType() != null ? CaseTypeMappingHelper.map(legalCase.getCaseType()) : null)
                .status(legalCase.getStatus())
                .priority(legalCase.getPriority())
                .startDate(legalCase.getStartDate())
                .endDate(legalCase.getEndDate())
                .courtName(legalCase.getCourtName())
                .judgeName(legalCase.getJudgeName())
                .estimatedDurationDays(legalCase.getEstimatedDurationDays())
                .totalAmount(legalCase.getTotalAmount())
                .nextHearingDate(legalCase.getNextHearingDate())
                .notes(legalCase.getNotes())
                .activities(legalCase.getActivities() != null ? legalCase.getActivities().stream()
                        .map(CaseActivityMappingHelper::map)
                        .collect(Collectors.toSet()) : null)
                .caseDocuments(legalCase.getCaseDocuments() != null ? legalCase.getCaseDocuments().stream()
                        .map(CaseDocumentMappingHelper::map)
                        .collect(Collectors.toSet()) : null)
                .createdAt(toLocalDateTime(legalCase.getCreatedAt()))
                .updatedAt(toLocalDateTime(legalCase.getUpdatedAt()))
                .build();
    }

    public static LegalCase map(final LegalCaseDto legalCaseDto) {
        return LegalCase.builder()
                .caseId(legalCaseDto.getCaseId())
                .caseNumber(legalCaseDto.getCaseNumber())
                .title(legalCaseDto.getTitle())
                .description(legalCaseDto.getDescription())
                .clientId(legalCaseDto.getClientId())
                .responsibleLawyer(legalCaseDto.getResponsibleLawyer())
                .caseType(legalCaseDto.getCaseType() != null ? CaseTypeMappingHelper.map(legalCaseDto.getCaseType())
                        : null)
                .status(legalCaseDto.getStatus())
                .priority(legalCaseDto.getPriority())
                .startDate(legalCaseDto.getStartDate())
                .endDate(legalCaseDto.getEndDate())
                .courtName(legalCaseDto.getCourtName())
                .judgeName(legalCaseDto.getJudgeName())
                .estimatedDurationDays(legalCaseDto.getEstimatedDurationDays())
                .totalAmount(legalCaseDto.getTotalAmount())
                .nextHearingDate(legalCaseDto.getNextHearingDate())
                .notes(legalCaseDto.getNotes())
                .build();
    }

    /**
     * Map to DTO without collections (for performance in list views)
     */
    public static LegalCaseDto mapWithoutCollections(final LegalCase legalCase) {
        return LegalCaseDto.builder()
                .caseId(legalCase.getCaseId())
                .caseNumber(legalCase.getCaseNumber())
                .title(legalCase.getTitle())
                .description(legalCase.getDescription())
                .clientId(legalCase.getClientId())
                .responsibleLawyer(legalCase.getResponsibleLawyer())
                .caseType(legalCase.getCaseType() != null ? CaseTypeMappingHelper.map(legalCase.getCaseType()) : null)
                .status(legalCase.getStatus())
                .priority(legalCase.getPriority())
                .startDate(legalCase.getStartDate())
                .endDate(legalCase.getEndDate())
                .courtName(legalCase.getCourtName())
                .judgeName(legalCase.getJudgeName())
                .estimatedDurationDays(legalCase.getEstimatedDurationDays())
                .totalAmount(legalCase.getTotalAmount())
                .nextHearingDate(legalCase.getNextHearingDate())
                .notes(legalCase.getNotes())
                .createdAt(toLocalDateTime(legalCase.getCreatedAt()))
                .updatedAt(toLocalDateTime(legalCase.getUpdatedAt()))
                .build();
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}