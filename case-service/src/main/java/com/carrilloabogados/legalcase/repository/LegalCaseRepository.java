package com.carrilloabogados.legalcase.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrilloabogados.legalcase.domain.CasePriority;
import com.carrilloabogados.legalcase.domain.CaseStatus;
import com.carrilloabogados.legalcase.domain.LegalCase;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Integer> {

    /**
     * Find legal case by case number
     */
    Optional<LegalCase> findByCaseNumber(String caseNumber);

    /**
     * Find all cases for a specific client
     */
    List<LegalCase> findByClientIdOrderByStartDateDesc(Integer clientId);

    /**
     * Find all cases for a specific client with pagination
     */
    Page<LegalCase> findByClientId(Integer clientId, Pageable pageable);

    /**
     * Find all cases by status
     */
    List<LegalCase> findByStatusOrderByStartDateDesc(CaseStatus status);

    /**
     * Find all active cases (not completed, closed, or cancelled)
     */
    @Query("SELECT lc FROM LegalCase lc WHERE lc.status IN ('PENDING', 'IN_PROGRESS', 'WAITING', 'UNDER_REVIEW', 'SUSPENDED') ORDER BY lc.priority DESC, lc.startDate ASC")
    List<LegalCase> findActiveCases();

    /**
     * Find all cases by responsible lawyer
     */
    List<LegalCase> findByResponsibleLawyerOrderByStartDateDesc(String responsibleLawyer);

    /**
     * Find all cases by priority
     */
    List<LegalCase> findByPriorityOrderByStartDateDesc(CasePriority priority);

    /**
     * Find all cases by case type
     */
    List<LegalCase> findByCaseTypeCaseTypeIdOrderByStartDateDesc(Integer caseTypeId);

    /**
     * Find cases with upcoming hearings
     */
    @Query("SELECT lc FROM LegalCase lc WHERE lc.nextHearingDate IS NOT NULL AND lc.nextHearingDate > CURRENT_TIMESTAMP ORDER BY lc.nextHearingDate ASC")
    List<LegalCase> findCasesWithUpcomingHearings();

    /**
     * Find cases within date range
     */
    List<LegalCase> findByStartDateBetweenOrderByStartDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Find cases that are overdue (past estimated duration)
     * Uses PostgreSQL compatible date arithmetic: (CURRENT_DATE - startDate)
     * returns integer days
     */
    @Query("SELECT lc FROM LegalCase lc WHERE lc.estimatedDurationDays IS NOT NULL " +
            "AND lc.status IN ('PENDING', 'IN_PROGRESS', 'WAITING', 'UNDER_REVIEW') " +
            "AND (CURRENT_DATE - lc.startDate) > lc.estimatedDurationDays " +
            "ORDER BY lc.priority DESC, lc.startDate ASC")
    List<LegalCase> findOverdueCases();

    /**
     * Find cases by title containing text (case insensitive)
     */
    List<LegalCase> findByTitleContainingIgnoreCaseOrderByStartDateDesc(String title);

    /**
     * Find cases by description containing text (case insensitive)
     */
    List<LegalCase> findByDescriptionContainingIgnoreCaseOrderByStartDateDesc(String description);

    /**
     * Find cases by court name
     */
    List<LegalCase> findByCourtNameContainingIgnoreCaseOrderByStartDateDesc(String courtName);

    /**
     * Find cases by judge name
     */
    List<LegalCase> findByJudgeNameContainingIgnoreCaseOrderByStartDateDesc(String judgeName);

    /**
     * Count cases by status
     */
    long countByStatus(CaseStatus status);

    /**
     * Count cases by client
     */
    long countByClientId(Integer clientId);

    /**
     * Count active cases for a lawyer
     */
    @Query("SELECT COUNT(lc) FROM LegalCase lc WHERE lc.responsibleLawyer = :lawyer " +
            "AND lc.status IN ('PENDING', 'IN_PROGRESS', 'WAITING', 'UNDER_REVIEW')")
    long countActiveCasesByLawyer(@Param("lawyer") String responsibleLawyer);

    /**
     * Get total amount for cases by status
     */
    @Query("SELECT COALESCE(SUM(lc.totalAmount), 0) FROM LegalCase lc WHERE lc.status = :status")
    Double getTotalAmountByStatus(@Param("status") CaseStatus status);

    /**
     * Find recently updated cases
     */
    @Query("SELECT lc FROM LegalCase lc ORDER BY lc.updatedAt DESC")
    List<LegalCase> findRecentlyUpdatedCases(Pageable pageable);

    /**
     * Search cases by multiple criteria
     */
    @Query("SELECT lc FROM LegalCase lc WHERE " +
            "(:clientId IS NULL OR lc.clientId = :clientId) AND " +
            "(:status IS NULL OR lc.status = :status) AND " +
            "(:responsibleLawyer IS NULL OR lc.responsibleLawyer = :responsibleLawyer) AND " +
            "(:caseTypeId IS NULL OR lc.caseType.caseTypeId = :caseTypeId) AND " +
            "(:searchText IS NULL OR lc.title LIKE %:searchText% OR lc.description LIKE %:searchText%) " +
            "ORDER BY lc.startDate DESC")
    Page<LegalCase> searchCases(
            @Param("clientId") Integer clientId,
            @Param("status") CaseStatus status,
            @Param("responsibleLawyer") String responsibleLawyer,
            @Param("caseTypeId") Integer caseTypeId,
            @Param("searchText") String searchText,
            Pageable pageable);

}