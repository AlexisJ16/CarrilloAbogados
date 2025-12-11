package com.carrilloabogados.legalcase.helper;

import com.carrilloabogados.legalcase.domain.CaseType;
import com.carrilloabogados.legalcase.dto.CaseTypeDto;

public interface CaseTypeMappingHelper {

	public static CaseTypeDto map(final CaseType caseType) {
		return CaseTypeDto.builder()
				.caseTypeId(caseType.getCaseTypeId())
				.name(caseType.getName())
				.description(caseType.getDescription())
				.category(caseType.getCategory())
				.isActive(caseType.getIsActive())
				.estimatedDurationDays(caseType.getEstimatedDurationDays())
				.baseFee(caseType.getBaseFee())
				.hourlyRate(caseType.getHourlyRate())
				.requiresCourtFiling(caseType.getRequiresCourtFiling())
				.requiresNotarization(caseType.getRequiresNotarization())
				.complexityLevel(caseType.getComplexityLevel())
				.createdAt(caseType.getCreatedAt())
				.updatedAt(caseType.getUpdatedAt())
				.build();
	}

	public static CaseType map(final CaseTypeDto caseTypeDto) {
		return CaseType.builder()
				.caseTypeId(caseTypeDto.getCaseTypeId())
				.name(caseTypeDto.getName())
				.description(caseTypeDto.getDescription())
				.category(caseTypeDto.getCategory())
				.isActive(caseTypeDto.getIsActive())
				.estimatedDurationDays(caseTypeDto.getEstimatedDurationDays())
				.baseFee(caseTypeDto.getBaseFee())
				.hourlyRate(caseTypeDto.getHourlyRate())
				.requiresCourtFiling(caseTypeDto.getRequiresCourtFiling())
				.requiresNotarization(caseTypeDto.getRequiresNotarization())
				.complexityLevel(caseTypeDto.getComplexityLevel())
				.build();
	}
	
}