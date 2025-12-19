package com.carrilloabogados.legalcase.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carrilloabogados.legalcase.domain.ActivityType;
import com.carrilloabogados.legalcase.dto.CaseActivityDto;

public interface CaseActivityService {

    List<CaseActivityDto> findAll();

    Page<CaseActivityDto> findAll(Pageable pageable);

    CaseActivityDto findById(Integer activityId);

    CaseActivityDto save(CaseActivityDto caseActivityDto);

    CaseActivityDto update(CaseActivityDto caseActivityDto);

    CaseActivityDto update(Integer activityId, CaseActivityDto caseActivityDto);

    void deleteById(Integer activityId);

    // Business logic methods - by case

    List<CaseActivityDto> findByCaseId(Integer caseId);

    Page<CaseActivityDto> findByCaseId(Integer caseId, Pageable pageable);

    List<CaseActivityDto> findCompletedByCaseId(Integer caseId);

    List<CaseActivityDto> findPendingByCaseId(Integer caseId);

    // Business logic methods - by performer

    List<CaseActivityDto> findByPerformedBy(String performedBy);

    // Business logic methods - by type

    List<CaseActivityDto> findByActivityType(ActivityType activityType);

    // Business logic methods - deadlines and scheduling

    List<CaseActivityDto> findWithDeadlines();

    List<CaseActivityDto> findOverdueActivities();

    List<CaseActivityDto> findUpcomingDeadlines(int daysAhead);

    List<CaseActivityDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Activity management operations

    CaseActivityDto markAsCompleted(Integer activityId);

    CaseActivityDto markAsIncomplete(Integer activityId);

    CaseActivityDto updateDeadline(Integer activityId, LocalDateTime deadline);

    CaseActivityDto logHours(Integer activityId, Double hoursSpent, Double billableHours);

    // Milestones

    List<CaseActivityDto> findMilestonesByCaseId(Integer caseId);

    CaseActivityDto markAsMilestone(Integer activityId, boolean isMilestone);

    // Statistics

    long countByCaseId(Integer caseId);

    long countCompletedByCaseId(Integer caseId);

    long countPendingByCaseId(Integer caseId);

    Double getTotalHoursByCaseId(Integer caseId);

    Double getTotalBillableHoursByCaseId(Integer caseId);

    Double getTotalCostByCaseId(Integer caseId);
}
