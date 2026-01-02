package com.carrilloabogados.legalcase.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.carrilloabogados.legalcase.domain.ActivityType;
import com.carrilloabogados.legalcase.dto.CaseActivityDto;
import com.carrilloabogados.legalcase.dto.response.ActivityMetricsResponse;
import com.carrilloabogados.legalcase.service.CaseActivityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Case Activity management
 * Base path: /api/activities
 */
@RestController
@RequestMapping("/api/activities")
@Slf4j
@RequiredArgsConstructor
public class CaseActivityController {

    private final CaseActivityService caseActivityService;

    // ============== CRUD Operations ==============

    /**
     * GET /api/activities - Get all case activities
     */
    @GetMapping
    public ResponseEntity<List<CaseActivityDto>> findAll() {
        log.info("*** CaseActivityDto List, controller; fetch all case activities ***");
        return ResponseEntity.ok(caseActivityService.findAll());
    }

    /**
     * GET /api/activities/page - Get case activities with pagination
     */
    @GetMapping("/page")
    public ResponseEntity<Page<CaseActivityDto>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "activityDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("*** CaseActivityDto Page, controller; fetch all case activities paginated ***");
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(caseActivityService.findAll(pageable));
    }

    /**
     * GET /api/activities/{activityId} - Get case activity by ID
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<CaseActivityDto> findById(@PathVariable Integer activityId) {
        log.info("*** CaseActivityDto, controller; fetch case activity by id ***");
        return ResponseEntity.ok(caseActivityService.findById(activityId));
    }

    /**
     * POST /api/activities - Create a new case activity
     */
    @PostMapping
    public ResponseEntity<CaseActivityDto> save(@RequestBody @Valid CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, controller; save case activity ***");
        return new ResponseEntity<>(caseActivityService.save(caseActivityDto), HttpStatus.CREATED);
    }

    /**
     * PUT /api/activities - Update a case activity
     */
    @PutMapping
    public ResponseEntity<CaseActivityDto> update(@RequestBody @Valid CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, controller; update case activity ***");
        return ResponseEntity.ok(caseActivityService.update(caseActivityDto));
    }

    /**
     * PUT /api/activities/{activityId} - Update a case activity by ID
     */
    @PutMapping("/{activityId}")
    public ResponseEntity<CaseActivityDto> updateById(
            @PathVariable Integer activityId,
            @RequestBody @Valid CaseActivityDto caseActivityDto) {
        log.info("*** CaseActivityDto, controller; update case activity by id ***");
        return ResponseEntity.ok(caseActivityService.update(activityId, caseActivityDto));
    }

    /**
     * DELETE /api/activities/{activityId} - Delete a case activity
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer activityId) {
        log.info("*** Void, controller; delete case activity by id ***");
        caseActivityService.deleteById(activityId);
        return ResponseEntity.noContent().build();
    }

    // ============== Business Logic Queries - By Case ==============

    /**
     * GET /api/activities/case/{caseId} - Get activities by case ID
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<CaseActivityDto>> findByCaseId(@PathVariable Integer caseId) {
        log.info("*** CaseActivityDto List, controller; fetch case activities by case id ***");
        return ResponseEntity.ok(caseActivityService.findByCaseId(caseId));
    }

    /**
     * GET /api/activities/case/{caseId}/page - Get activities by case ID with
     * pagination
     */
    @GetMapping("/case/{caseId}/page")
    public ResponseEntity<Page<CaseActivityDto>> findByCaseIdPaginated(
            @PathVariable Integer caseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("*** CaseActivityDto Page, controller; fetch case activities by case id paginated ***");
        Pageable pageable = PageRequest.of(page, size, Sort.by("activityDate").descending());
        return ResponseEntity.ok(caseActivityService.findByCaseId(caseId, pageable));
    }

    /**
     * GET /api/activities/case/{caseId}/completed - Get completed activities by
     * case ID
     */
    @GetMapping("/case/{caseId}/completed")
    public ResponseEntity<List<CaseActivityDto>> findCompletedByCaseId(@PathVariable Integer caseId) {
        log.info("*** CaseActivityDto List, controller; fetch completed case activities by case id ***");
        return ResponseEntity.ok(caseActivityService.findCompletedByCaseId(caseId));
    }

    /**
     * GET /api/activities/case/{caseId}/pending - Get pending activities by case ID
     */
    @GetMapping("/case/{caseId}/pending")
    public ResponseEntity<List<CaseActivityDto>> findPendingByCaseId(@PathVariable Integer caseId) {
        log.info("*** CaseActivityDto List, controller; fetch pending case activities by case id ***");
        return ResponseEntity.ok(caseActivityService.findPendingByCaseId(caseId));
    }

    /**
     * GET /api/activities/case/{caseId}/milestones - Get milestones by case ID
     */
    @GetMapping("/case/{caseId}/milestones")
    public ResponseEntity<List<CaseActivityDto>> findMilestonesByCaseId(@PathVariable Integer caseId) {
        log.info("*** CaseActivityDto List, controller; fetch milestones by case id ***");
        return ResponseEntity.ok(caseActivityService.findMilestonesByCaseId(caseId));
    }

    // ============== Business Logic Queries - By Other Criteria ==============

    /**
     * GET /api/activities/performer/{performedBy} - Get activities by performer
     */
    @GetMapping("/performer/{performedBy}")
    public ResponseEntity<List<CaseActivityDto>> findByPerformedBy(@PathVariable String performedBy) {
        log.info("*** CaseActivityDto List, controller; fetch case activities by performer ***");
        return ResponseEntity.ok(caseActivityService.findByPerformedBy(performedBy));
    }

    /**
     * GET /api/activities/type/{activityType} - Get activities by type
     */
    @GetMapping("/type/{activityType}")
    public ResponseEntity<List<CaseActivityDto>> findByActivityType(@PathVariable ActivityType activityType) {
        log.info("*** CaseActivityDto List, controller; fetch case activities by type ***");
        return ResponseEntity.ok(caseActivityService.findByActivityType(activityType));
    }

    // ============== Deadlines and Scheduling ==============

    /**
     * GET /api/activities/deadlines - Get activities with deadlines
     */
    @GetMapping("/deadlines")
    public ResponseEntity<List<CaseActivityDto>> findWithDeadlines() {
        log.info("*** CaseActivityDto List, controller; fetch case activities with deadlines ***");
        return ResponseEntity.ok(caseActivityService.findWithDeadlines());
    }

    /**
     * GET /api/activities/overdue - Get overdue activities
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<CaseActivityDto>> findOverdueActivities() {
        log.info("*** CaseActivityDto List, controller; fetch overdue case activities ***");
        return ResponseEntity.ok(caseActivityService.findOverdueActivities());
    }

    /**
     * GET /api/activities/upcoming - Get activities with upcoming deadlines
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<CaseActivityDto>> findUpcomingDeadlines(
            @RequestParam(defaultValue = "7") int daysAhead) {
        log.info("*** CaseActivityDto List, controller; fetch upcoming deadlines ***");
        return ResponseEntity.ok(caseActivityService.findUpcomingDeadlines(daysAhead));
    }

    /**
     * GET /api/activities/date-range - Get activities by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<CaseActivityDto>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("*** CaseActivityDto List, controller; fetch case activities by date range ***");
        return ResponseEntity.ok(caseActivityService.findByDateRange(startDate, endDate));
    }

    // ============== Activity Management Operations ==============

    /**
     * PATCH /api/activities/{activityId}/complete - Mark activity as completed
     */
    @PatchMapping("/{activityId}/complete")
    public ResponseEntity<CaseActivityDto> markAsCompleted(@PathVariable Integer activityId) {
        log.info("*** CaseActivityDto, controller; mark case activity as completed ***");
        return ResponseEntity.ok(caseActivityService.markAsCompleted(activityId));
    }

    /**
     * PATCH /api/activities/{activityId}/incomplete - Mark activity as incomplete
     */
    @PatchMapping("/{activityId}/incomplete")
    public ResponseEntity<CaseActivityDto> markAsIncomplete(@PathVariable Integer activityId) {
        log.info("*** CaseActivityDto, controller; mark case activity as incomplete ***");
        return ResponseEntity.ok(caseActivityService.markAsIncomplete(activityId));
    }

    /**
     * PATCH /api/activities/{activityId}/deadline - Update activity deadline
     */
    @PatchMapping("/{activityId}/deadline")
    public ResponseEntity<CaseActivityDto> updateDeadline(
            @PathVariable Integer activityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline) {

        log.info("*** CaseActivityDto, controller; update case activity deadline ***");
        return ResponseEntity.ok(caseActivityService.updateDeadline(activityId, deadline));
    }

    /**
     * PATCH /api/activities/{activityId}/hours - Log hours for activity
     */
    @PatchMapping("/{activityId}/hours")
    public ResponseEntity<CaseActivityDto> logHours(
            @PathVariable Integer activityId,
            @RequestParam Double hoursSpent,
            @RequestParam(required = false) Double billableHours) {

        log.info("*** CaseActivityDto, controller; log hours for case activity ***");
        return ResponseEntity.ok(caseActivityService.logHours(activityId, hoursSpent, billableHours));
    }

    /**
     * PATCH /api/activities/{activityId}/milestone - Set milestone status
     */
    @PatchMapping("/{activityId}/milestone")
    public ResponseEntity<CaseActivityDto> markAsMilestone(
            @PathVariable Integer activityId,
            @RequestParam boolean isMilestone) {

        log.info("*** CaseActivityDto, controller; mark case activity as milestone ***");
        return ResponseEntity.ok(caseActivityService.markAsMilestone(activityId, isMilestone));
    }

    // ============== Metrics ==============

    /**
     * GET /api/activities/case/{caseId}/metrics - Get activity metrics for a case
     */
    @GetMapping("/case/{caseId}/metrics")
    public ResponseEntity<ActivityMetricsResponse> getMetricsByCaseId(@PathVariable Integer caseId) {
        log.info("*** ActivityMetricsResponse, controller; fetch activity metrics by case id ***");

        ActivityMetricsResponse metrics = ActivityMetricsResponse.builder()
                .totalActivities(caseActivityService.countByCaseId(caseId))
                .completedActivities(caseActivityService.countCompletedByCaseId(caseId))
                .pendingActivities(caseActivityService.countPendingByCaseId(caseId))
                .totalHoursSpent(caseActivityService.getTotalHoursByCaseId(caseId))
                .totalBillableHours(caseActivityService.getTotalBillableHoursByCaseId(caseId))
                .totalCost(caseActivityService.getTotalCostByCaseId(caseId))
                .build();

        return ResponseEntity.ok(metrics);
    }
}
