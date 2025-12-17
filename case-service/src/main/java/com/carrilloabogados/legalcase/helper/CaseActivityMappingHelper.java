package com.carrilloabogados.legalcase.helper;

import com.carrilloabogados.legalcase.domain.CaseActivity;
import com.carrilloabogados.legalcase.dto.CaseActivityDto;

public interface CaseActivityMappingHelper {

	public static CaseActivityDto map(final CaseActivity caseActivity) {
		return CaseActivityDto.builder()
				.activityId(caseActivity.getActivityId())
				.caseId(caseActivity.getLegalCase() != null ? 
						caseActivity.getLegalCase().getCaseId() : null)
				.title(caseActivity.getTitle())
				.description(caseActivity.getDescription())
				.activityType(caseActivity.getActivityType())
				.performedBy(caseActivity.getPerformedBy())
				.activityDate(caseActivity.getActivityDate())
				.deadline(caseActivity.getDeadline())
				.isCompleted(caseActivity.getIsCompleted())
				.completionDate(caseActivity.getCompletionDate())
				.hoursSpent(caseActivity.getHoursSpent())
				.billableHours(caseActivity.getBillableHours())
				.cost(caseActivity.getCost())
				.notes(caseActivity.getNotes())
				.isMilestone(caseActivity.getIsMilestone())
				.createdAt(caseActivity.getCreatedAt())
				.updatedAt(caseActivity.getUpdatedAt())
				.build();
	}

	public static CaseActivity map(final CaseActivityDto caseActivityDto) {
		return CaseActivity.builder()
				.activityId(caseActivityDto.getActivityId())
				.title(caseActivityDto.getTitle())
				.description(caseActivityDto.getDescription())
				.activityType(caseActivityDto.getActivityType())
				.performedBy(caseActivityDto.getPerformedBy())
				.activityDate(caseActivityDto.getActivityDate())
				.deadline(caseActivityDto.getDeadline())
				.isCompleted(caseActivityDto.getIsCompleted())
				.completionDate(caseActivityDto.getCompletionDate())
				.hoursSpent(caseActivityDto.getHoursSpent())
				.billableHours(caseActivityDto.getBillableHours())
				.cost(caseActivityDto.getCost())
				.notes(caseActivityDto.getNotes())
				.isMilestone(caseActivityDto.getIsMilestone())
				.build();
	}
	
}