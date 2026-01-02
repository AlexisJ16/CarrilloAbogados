package com.carrilloabogados.legalcase.controller;

import java.time.LocalDate;
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

import com.carrilloabogados.legalcase.domain.CasePriority;
import com.carrilloabogados.legalcase.domain.CaseStatus;
import com.carrilloabogados.legalcase.dto.LegalCaseDto;
import com.carrilloabogados.legalcase.dto.response.CaseMetricsResponse;
import com.carrilloabogados.legalcase.service.LegalCaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Legal Case management
 * Base path: /api/cases
 */
@RestController
@RequestMapping("/api/cases")
@Slf4j
@RequiredArgsConstructor
public class LegalCaseController {

    private final LegalCaseService legalCaseService;

    // ============== CRUD Operations ==============

    /**
     * GET /api/cases - Get all legal cases
     */
    @GetMapping
    public ResponseEntity<List<LegalCaseDto>> findAll() {
        log.info("*** LegalCaseDto List, controller; fetch all legal cases ***");
        return ResponseEntity.ok(legalCaseService.findAll());
    }

    /**
     * GET /api/cases/page - Get legal cases with pagination
     */
    @GetMapping("/page")
    public ResponseEntity<Page<LegalCaseDto>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("*** LegalCaseDto Page, controller; fetch all legal cases paginated ***");
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(legalCaseService.findAll(pageable));
    }

    /**
     * GET /api/cases/{caseId} - Get legal case by ID
     */
    @GetMapping("/{caseId}")
    public ResponseEntity<LegalCaseDto> findById(@PathVariable Integer caseId) {
        log.info("*** LegalCaseDto, controller; fetch legal case by id ***");
        return ResponseEntity.ok(legalCaseService.findById(caseId));
    }

    /**
     * GET /api/cases/number/{caseNumber} - Get legal case by case number
     */
    @GetMapping("/number/{caseNumber}")
    public ResponseEntity<LegalCaseDto> findByCaseNumber(@PathVariable String caseNumber) {
        log.info("*** LegalCaseDto, controller; fetch legal case by case number ***");
        return ResponseEntity.ok(legalCaseService.findByCaseNumber(caseNumber));
    }

    /**
     * POST /api/cases - Create a new legal case
     */
    @PostMapping
    public ResponseEntity<LegalCaseDto> save(@RequestBody @Valid LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, controller; save legal case ***");
        return new ResponseEntity<>(legalCaseService.save(legalCaseDto), HttpStatus.CREATED);
    }

    /**
     * PUT /api/cases - Update a legal case
     */
    @PutMapping
    public ResponseEntity<LegalCaseDto> update(@RequestBody @Valid LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, controller; update legal case ***");
        return ResponseEntity.ok(legalCaseService.update(legalCaseDto));
    }

    /**
     * PUT /api/cases/{caseId} - Update a legal case by ID
     */
    @PutMapping("/{caseId}")
    public ResponseEntity<LegalCaseDto> updateById(
            @PathVariable Integer caseId,
            @RequestBody @Valid LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, controller; update legal case by id ***");
        return ResponseEntity.ok(legalCaseService.update(caseId, legalCaseDto));
    }

    /**
     * DELETE /api/cases/{caseId} - Delete a legal case
     */
    @DeleteMapping("/{caseId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer caseId) {
        log.info("*** Void, controller; delete legal case by id ***");
        legalCaseService.deleteById(caseId);
        return ResponseEntity.noContent().build();
    }

    // ============== Business Logic Queries ==============

    /**
     * GET /api/cases/client/{clientId} - Get cases by client ID
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<LegalCaseDto>> findByClientId(@PathVariable Integer clientId) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by client id ***");
        return ResponseEntity.ok(legalCaseService.findByClientId(clientId));
    }

    /**
     * GET /api/cases/client/{clientId}/page - Get cases by client ID with
     * pagination
     */
    @GetMapping("/client/{clientId}/page")
    public ResponseEntity<Page<LegalCaseDto>> findByClientIdPaginated(
            @PathVariable Integer clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("*** LegalCaseDto Page, controller; fetch legal cases by client id paginated ***");
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return ResponseEntity.ok(legalCaseService.findByClientId(clientId, pageable));
    }

    /**
     * GET /api/cases/status/{status} - Get cases by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LegalCaseDto>> findByStatus(@PathVariable CaseStatus status) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by status ***");
        return ResponseEntity.ok(legalCaseService.findByStatus(status));
    }

    /**
     * GET /api/cases/active - Get all active cases
     */
    @GetMapping("/active")
    public ResponseEntity<List<LegalCaseDto>> findActiveCases() {
        log.info("*** LegalCaseDto List, controller; fetch active legal cases ***");
        return ResponseEntity.ok(legalCaseService.findActiveCases());
    }

    /**
     * GET /api/cases/lawyer/{lawyerName} - Get cases by responsible lawyer
     */
    @GetMapping("/lawyer/{lawyerName}")
    public ResponseEntity<List<LegalCaseDto>> findByResponsibleLawyer(@PathVariable String lawyerName) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by lawyer ***");
        return ResponseEntity.ok(legalCaseService.findByResponsibleLawyer(lawyerName));
    }

    /**
     * GET /api/cases/priority/{priority} - Get cases by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<LegalCaseDto>> findByPriority(@PathVariable CasePriority priority) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by priority ***");
        return ResponseEntity.ok(legalCaseService.findByPriority(priority));
    }

    /**
     * GET /api/cases/type/{caseTypeId} - Get cases by case type
     */
    @GetMapping("/type/{caseTypeId}")
    public ResponseEntity<List<LegalCaseDto>> findByCaseType(@PathVariable Integer caseTypeId) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by case type ***");
        return ResponseEntity.ok(legalCaseService.findByCaseType(caseTypeId));
    }

    /**
     * GET /api/cases/upcoming-hearings - Get cases with upcoming hearings
     */
    @GetMapping("/upcoming-hearings")
    public ResponseEntity<List<LegalCaseDto>> findCasesWithUpcomingHearings() {
        log.info("*** LegalCaseDto List, controller; fetch legal cases with upcoming hearings ***");
        return ResponseEntity.ok(legalCaseService.findCasesWithUpcomingHearings());
    }

    /**
     * GET /api/cases/overdue - Get overdue cases
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<LegalCaseDto>> findOverdueCases() {
        log.info("*** LegalCaseDto List, controller; fetch overdue legal cases ***");
        return ResponseEntity.ok(legalCaseService.findOverdueCases());
    }

    /**
     * GET /api/cases/date-range - Get cases by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<LegalCaseDto>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("*** LegalCaseDto List, controller; fetch legal cases by date range ***");
        return ResponseEntity.ok(legalCaseService.findByDateRange(startDate, endDate));
    }

    /**
     * GET /api/cases/recent - Get recently updated cases
     */
    @GetMapping("/recent")
    public ResponseEntity<List<LegalCaseDto>> findRecentlyUpdatedCases(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("*** LegalCaseDto List, controller; fetch recently updated legal cases ***");
        return ResponseEntity.ok(legalCaseService.findRecentlyUpdatedCases(limit));
    }

    // ============== Search Operations ==============

    /**
     * GET /api/cases/search/title - Search cases by title
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<LegalCaseDto>> searchByTitle(@RequestParam String query) {
        log.info("*** LegalCaseDto List, controller; search legal cases by title ***");
        return ResponseEntity.ok(legalCaseService.searchByTitle(query));
    }

    /**
     * GET /api/cases/search/description - Search cases by description
     */
    @GetMapping("/search/description")
    public ResponseEntity<List<LegalCaseDto>> searchByDescription(@RequestParam String query) {
        log.info("*** LegalCaseDto List, controller; search legal cases by description ***");
        return ResponseEntity.ok(legalCaseService.searchByDescription(query));
    }

    /**
     * GET /api/cases/search/court - Search cases by court name
     */
    @GetMapping("/search/court")
    public ResponseEntity<List<LegalCaseDto>> findByCourtName(@RequestParam String query) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by court name ***");
        return ResponseEntity.ok(legalCaseService.findByCourtName(query));
    }

    /**
     * GET /api/cases/search/judge - Search cases by judge name
     */
    @GetMapping("/search/judge")
    public ResponseEntity<List<LegalCaseDto>> findByJudgeName(@RequestParam String query) {
        log.info("*** LegalCaseDto List, controller; fetch legal cases by judge name ***");
        return ResponseEntity.ok(legalCaseService.findByJudgeName(query));
    }

    /**
     * GET /api/cases/search - Advanced search with multiple filters
     */
    @GetMapping("/search")
    public ResponseEntity<Page<LegalCaseDto>> searchCases(
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) CaseStatus status,
            @RequestParam(required = false) String responsibleLawyer,
            @RequestParam(required = false) Integer caseTypeId,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("*** LegalCaseDto Page, controller; advanced search legal cases ***");
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return ResponseEntity.ok(legalCaseService.searchCases(clientId, status, responsibleLawyer,
                caseTypeId, searchText, pageable));
    }

    // ============== Case Management Operations ==============

    /**
     * PATCH /api/cases/{caseId}/status - Update case status
     */
    @PatchMapping("/{caseId}/status")
    public ResponseEntity<LegalCaseDto> updateStatus(
            @PathVariable Integer caseId,
            @RequestParam CaseStatus status) {

        log.info("*** LegalCaseDto, controller; update legal case status ***");
        return ResponseEntity.ok(legalCaseService.updateStatus(caseId, status));
    }

    /**
     * PATCH /api/cases/{caseId}/priority - Update case priority
     */
    @PatchMapping("/{caseId}/priority")
    public ResponseEntity<LegalCaseDto> updatePriority(
            @PathVariable Integer caseId,
            @RequestParam CasePriority priority) {

        log.info("*** LegalCaseDto, controller; update legal case priority ***");
        return ResponseEntity.ok(legalCaseService.updatePriority(caseId, priority));
    }

    /**
     * PATCH /api/cases/{caseId}/assign - Assign lawyer to case
     */
    @PatchMapping("/{caseId}/assign")
    public ResponseEntity<LegalCaseDto> assignLawyer(
            @PathVariable Integer caseId,
            @RequestParam String lawyerName) {

        log.info("*** LegalCaseDto, controller; assign lawyer to legal case ***");
        return ResponseEntity.ok(legalCaseService.assignLawyer(caseId, lawyerName));
    }

    /**
     * PATCH /api/cases/{caseId}/hearing - Set next hearing date
     */
    @PatchMapping("/{caseId}/hearing")
    public ResponseEntity<LegalCaseDto> setNextHearing(
            @PathVariable Integer caseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hearingDate) {

        log.info("*** LegalCaseDto, controller; set next hearing for legal case ***");
        return ResponseEntity.ok(legalCaseService.setNextHearing(caseId, hearingDate));
    }

    /**
     * PATCH /api/cases/{caseId}/close - Close a case
     */
    @PatchMapping("/{caseId}/close")
    public ResponseEntity<LegalCaseDto> closeCase(
            @PathVariable Integer caseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String notes) {

        log.info("*** LegalCaseDto, controller; close legal case ***");
        return ResponseEntity.ok(legalCaseService.closeCase(caseId, endDate, notes));
    }

    /**
     * PATCH /api/cases/{caseId}/reopen - Reopen a closed case
     */
    @PatchMapping("/{caseId}/reopen")
    public ResponseEntity<LegalCaseDto> reopenCase(
            @PathVariable Integer caseId,
            @RequestParam(required = false) String notes) {

        log.info("*** LegalCaseDto, controller; reopen legal case ***");
        return ResponseEntity.ok(legalCaseService.reopenCase(caseId, notes));
    }

    // ============== Statistics and Metrics ==============

    /**
     * GET /api/cases/metrics - Get case metrics and statistics
     */
    @GetMapping("/metrics")
    public ResponseEntity<CaseMetricsResponse> getMetrics() {
        log.info("*** CaseMetricsResponse, controller; fetch case metrics ***");

        CaseMetricsResponse metrics = CaseMetricsResponse.builder()
                .pendingCount(legalCaseService.countByStatus(CaseStatus.PENDING))
                .inProgressCount(legalCaseService.countByStatus(CaseStatus.IN_PROGRESS))
                .waitingCount(legalCaseService.countByStatus(CaseStatus.WAITING))
                .completedCount(legalCaseService.countByStatus(CaseStatus.COMPLETED))
                .closedCount(legalCaseService.countByStatus(CaseStatus.CLOSED))
                .cancelledCount(legalCaseService.countByStatus(CaseStatus.CANCELLED))
                .totalActiveAmount(legalCaseService.getTotalAmountByStatus(CaseStatus.IN_PROGRESS))
                .totalCompletedAmount(legalCaseService.getTotalAmountByStatus(CaseStatus.COMPLETED))
                .build();

        return ResponseEntity.ok(metrics);
    }

    /**
     * GET /api/cases/metrics/client/{clientId} - Get case count for a client
     */
    @GetMapping("/metrics/client/{clientId}")
    public ResponseEntity<Long> countByClientId(@PathVariable Integer clientId) {
        log.info("*** Long, controller; count legal cases by client id ***");
        return ResponseEntity.ok(legalCaseService.countByClientId(clientId));
    }

    /**
     * GET /api/cases/metrics/lawyer/{lawyerName} - Get active case count for a
     * lawyer
     */
    @GetMapping("/metrics/lawyer/{lawyerName}")
    public ResponseEntity<Long> countActiveCasesByLawyer(@PathVariable String lawyerName) {
        log.info("*** Long, controller; count active legal cases by lawyer ***");
        return ResponseEntity.ok(legalCaseService.countActiveCasesByLawyer(lawyerName));
    }
}
