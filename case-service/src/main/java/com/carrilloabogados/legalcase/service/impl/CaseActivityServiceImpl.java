package com.carrilloabogados.legalcase.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrilloabogados.legalcase.domain.ActivityType;
import com.carrilloabogados.legalcase.domain.CaseActivity;
import com.carrilloabogados.legalcase.domain.LegalCase;
import com.carrilloabogados.legalcase.dto.CaseActivityDto;
import com.carrilloabogados.legalcase.exception.wrapper.CaseActivityNotFoundException;
import com.carrilloabogados.legalcase.exception.wrapper.LegalCaseNotFoundException;
import com.carrilloabogados.legalcase.helper.CaseActivityMappingHelper;
import com.carrilloabogados.legalcase.repository.CaseActivityRepository;
import com.carrilloabogados.legalcase.repository.LegalCaseRepository;
import com.carrilloabogados.legalcase.service.CaseActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CaseActivityServiceImpl implements CaseActivityService {

    private final CaseActivityRepository caseActivityRepository;
    private final LegalCaseRepository legalCaseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findAll() {
        log.info("*** CaseActivityDto List, service; fetch all case activities ***");
        return caseActivityRepository.findAll()
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseActivityDto> findAll(Pageable pageable) {
        log.info("*** CaseActivityDto Page, service; fetch all case activities with pagination ***");
        return caseActivityRepository.findAll(pageable)
                .map(CaseActivityMappingHelper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseActivityDto findById(Integer activityId) {
        log.info("*** CaseActivityDto, service; fetch case activity by id ***");
        return caseActivityRepository.findById(activityId)
                .map(CaseActivityMappingHelper::map)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));
    }

    @Override
    public CaseActivityDto save(CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, service; save case activity ***");

        // Validate legal case exists
        LegalCase legalCase = legalCaseRepository.findById(caseActivityDto.getCaseId())
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseActivityDto.getCaseId())));

        CaseActivity activity = CaseActivityMappingHelper.map(caseActivityDto);
        activity.setLegalCase(legalCase);
        activity.setCreatedAt(Instant.now());
        activity.setUpdatedAt(Instant.now());

        // Set defaults
        if (activity.getIsCompleted() == null) {
            activity.setIsCompleted(false);
        }
        if (activity.getIsMilestone() == null) {
            activity.setIsMilestone(false);
        }
        if (activity.getActivityDate() == null) {
            activity.setActivityDate(LocalDateTime.now());
        }

        CaseActivity savedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity saved with id: {} for case: {} ***",
                savedActivity.getActivityId(), legalCase.getCaseId());

        return CaseActivityMappingHelper.map(savedActivity);
    }

    @Override
    public CaseActivityDto update(CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, service; update case activity ***");

        CaseActivity existingActivity = caseActivityRepository.findById(caseActivityDto.getActivityId())
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", caseActivityDto.getActivityId())));

        updateFields(existingActivity, caseActivityDto);
        existingActivity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(existingActivity);
        log.info("*** Case activity updated with id: {} ***", updatedActivity.getActivityId());

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    public CaseActivityDto update(Integer activityId, CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, service; update case activity by id ***");
        caseActivityDto.setActivityId(activityId);
        return update(caseActivityDto);
    }

    @Override
    public void deleteById(Integer activityId) {
        log.info("*** Void, service; delete case activity by id ***");

        if (!caseActivityRepository.existsById(activityId)) {
            throw new CaseActivityNotFoundException(
                    String.format("Case activity with id: %d not found", activityId));
        }

        caseActivityRepository.deleteById(activityId);
        log.info("*** Case activity deleted with id: {} ***", activityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findByCaseId(Integer caseId) {
        log.info("*** CaseActivityDto List, service; fetch case activities by case id ***");
        return caseActivityRepository.findByLegalCaseCaseIdOrderByActivityDateDesc(caseId)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseActivityDto> findByCaseId(Integer caseId, Pageable pageable) {
        log.info("*** CaseActivityDto Page, service; fetch case activities by case id with pagination ***");
        return caseActivityRepository.findByLegalCaseCaseId(caseId, pageable)
                .map(CaseActivityMappingHelper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findCompletedByCaseId(Integer caseId) {
        log.info("*** CaseActivityDto List, service; fetch completed case activities by case id ***");
        return caseActivityRepository.findByLegalCaseCaseIdAndIsCompletedTrueOrderByCompletionDateDesc(caseId)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findPendingByCaseId(Integer caseId) {
        log.info("*** CaseActivityDto List, service; fetch pending case activities by case id ***");
        return caseActivityRepository.findByLegalCaseCaseIdAndIsCompletedFalseOrderByActivityDateDesc(caseId)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findByPerformedBy(String performedBy) {
        log.info("*** CaseActivityDto List, service; fetch case activities by performer ***");
        return caseActivityRepository.findByPerformedByOrderByActivityDateDesc(performedBy)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findByActivityType(ActivityType activityType) {
        log.info("*** CaseActivityDto List, service; fetch case activities by type ***");
        return caseActivityRepository.findByActivityTypeOrderByActivityDateDesc(activityType)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findWithDeadlines() {
        log.info("*** CaseActivityDto List, service; fetch case activities with deadlines ***");
        return caseActivityRepository.findByDeadlineIsNotNullAndIsCompletedFalseOrderByDeadlineAsc()
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findOverdueActivities() {
        log.info("*** CaseActivityDto List, service; fetch overdue case activities ***");
        return caseActivityRepository.findOverdueActivities(LocalDateTime.now())
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findUpcomingDeadlines(int daysAhead) {
        log.info("*** CaseActivityDto List, service; fetch upcoming deadlines ***");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusDays(daysAhead);
        return caseActivityRepository.findUpcomingDeadlines(now, deadline)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("*** CaseActivityDto List, service; fetch case activities by date range ***");
        return caseActivityRepository.findByActivityDateBetweenOrderByActivityDateDesc(startDate, endDate)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    public CaseActivityDto markAsCompleted(Integer activityId) {
        log.info("*** CaseActivityDto, service; mark case activity as completed ***");

        CaseActivity activity = caseActivityRepository.findById(activityId)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));

        activity.setIsCompleted(true);
        activity.setCompletionDate(LocalDateTime.now());
        activity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity {} marked as completed ***", activityId);

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    public CaseActivityDto markAsIncomplete(Integer activityId) {
        log.info("*** CaseActivityDto, service; mark case activity as incomplete ***");

        CaseActivity activity = caseActivityRepository.findById(activityId)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));

        activity.setIsCompleted(false);
        activity.setCompletionDate(null);
        activity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity {} marked as incomplete ***", activityId);

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    public CaseActivityDto updateDeadline(Integer activityId, LocalDateTime deadline) {
        log.info("*** CaseActivityDto, service; update case activity deadline ***");

        CaseActivity activity = caseActivityRepository.findById(activityId)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));

        activity.setDeadline(deadline);
        activity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity {} deadline updated to {} ***", activityId, deadline);

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    public CaseActivityDto logHours(Integer activityId, Double hoursSpent, Double billableHours) {
        log.info("*** CaseActivityDto, service; log hours for case activity ***");

        CaseActivity activity = caseActivityRepository.findById(activityId)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));

        // Add to existing hours
        double currentHoursSpent = activity.getHoursSpent() != null ? activity.getHoursSpent() : 0.0;
        double currentBillableHours = activity.getBillableHours() != null ? activity.getBillableHours() : 0.0;

        activity.setHoursSpent(currentHoursSpent + hoursSpent);
        if (billableHours != null) {
            activity.setBillableHours(currentBillableHours + billableHours);
        }
        activity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity {} hours logged: {} spent, {} billable ***",
                activityId, hoursSpent, billableHours);

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseActivityDto> findMilestonesByCaseId(Integer caseId) {
        log.info("*** CaseActivityDto List, service; fetch milestones by case id ***");
        return caseActivityRepository.findMilestonesByCaseId(caseId)
                .stream()
                .map(CaseActivityMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    public CaseActivityDto markAsMilestone(Integer activityId, boolean isMilestone) {
        log.info("*** CaseActivityDto, service; mark case activity as milestone ***");

        CaseActivity activity = caseActivityRepository.findById(activityId)
                .orElseThrow(() -> new CaseActivityNotFoundException(
                        String.format("Case activity with id: %d not found", activityId)));

        activity.setIsMilestone(isMilestone);
        activity.setUpdatedAt(Instant.now());

        CaseActivity updatedActivity = caseActivityRepository.save(activity);
        log.info("*** Case activity {} milestone status set to {} ***", activityId, isMilestone);

        return CaseActivityMappingHelper.map(updatedActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCaseId(Integer caseId) {
        log.info("*** Long, service; count case activities by case id ***");
        return caseActivityRepository.countByLegalCaseCaseId(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCompletedByCaseId(Integer caseId) {
        log.info("*** Long, service; count completed case activities by case id ***");
        return caseActivityRepository.countByLegalCaseCaseIdAndIsCompletedTrue(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingByCaseId(Integer caseId) {
        log.info("*** Long, service; count pending case activities by case id ***");
        return caseActivityRepository.countByLegalCaseCaseIdAndIsCompletedFalse(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalHoursByCaseId(Integer caseId) {
        log.info("*** Double, service; get total hours by case id ***");
        return caseActivityRepository.getTotalHoursByCaseId(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalBillableHoursByCaseId(Integer caseId) {
        log.info("*** Double, service; get total billable hours by case id ***");
        return caseActivityRepository.getTotalBillableHoursByCaseId(caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalCostByCaseId(Integer caseId) {
        log.info("*** Double, service; get total cost by case id ***");
        return caseActivityRepository.getTotalCostByCaseId(caseId);
    }

    // ============== Private Helper Methods ==============

    private void updateFields(CaseActivity existingActivity, CaseActivityDto dto) {
        if (dto.getTitle() != null) {
            existingActivity.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            existingActivity.setDescription(dto.getDescription());
        }
        if (dto.getActivityType() != null) {
            existingActivity.setActivityType(dto.getActivityType());
        }
        if (dto.getPerformedBy() != null) {
            existingActivity.setPerformedBy(dto.getPerformedBy());
        }
        if (dto.getActivityDate() != null) {
            existingActivity.setActivityDate(dto.getActivityDate());
        }
        if (dto.getDeadline() != null) {
            existingActivity.setDeadline(dto.getDeadline());
        }
        if (dto.getIsCompleted() != null) {
            existingActivity.setIsCompleted(dto.getIsCompleted());
            if (dto.getIsCompleted() && existingActivity.getCompletionDate() == null) {
                existingActivity.setCompletionDate(LocalDateTime.now());
            }
        }
        if (dto.getHoursSpent() != null) {
            existingActivity.setHoursSpent(dto.getHoursSpent());
        }
        if (dto.getBillableHours() != null) {
            existingActivity.setBillableHours(dto.getBillableHours());
        }
        if (dto.getCost() != null) {
            existingActivity.setCost(dto.getCost());
        }
        if (dto.getNotes() != null) {
            existingActivity.setNotes(dto.getNotes());
        }
        if (dto.getIsMilestone() != null) {
            existingActivity.setIsMilestone(dto.getIsMilestone());
        }
    }
}
