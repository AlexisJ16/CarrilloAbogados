package com.carrilloabogados.legalcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrilloabogados.legalcase.domain.ActivityType;
import com.carrilloabogados.legalcase.domain.CaseActivity;

public interface CaseActivityRepository extends JpaRepository<CaseActivity, Integer> {

	/**
	 * Find all activities for a specific legal case
	 */
	List<CaseActivity> findByLegalCaseCaseIdOrderByActivityDateDesc(Integer caseId);
	
	/**
	 * Find all activities for a specific legal case with pagination
	 */
	Page<CaseActivity> findByLegalCaseCaseId(Integer caseId, Pageable pageable);
	
	/**
	 * Find activities by performer
	 */
	List<CaseActivity> findByPerformedByOrderByActivityDateDesc(String performedBy);
	
	/**
	 * Find activities by type
	 */
	List<CaseActivity> findByActivityTypeOrderByActivityDateDesc(ActivityType activityType);
	
	/**
	 * Find completed activities for a case
	 */
	List<CaseActivity> findByLegalCaseCaseIdAndIsCompletedTrueOrderByCompletionDateDesc(Integer caseId);
	
	/**
	 * Find pending activities for a case
	 */
	List<CaseActivity> findByLegalCaseCaseIdAndIsCompletedFalseOrderByActivityDateDesc(Integer caseId);
	
	/**
	 * Find activities with deadlines
	 */
	List<CaseActivity> findByDeadlineIsNotNullAndIsCompletedFalseOrderByDeadlineAsc();
	
	/**
	 * Find overdue activities
	 */
	@Query("SELECT ca FROM CaseActivity ca WHERE ca.deadline IS NOT NULL " +
	       "AND ca.deadline < CURRENT_TIMESTAMP AND ca.isCompleted = false " +
	       "ORDER BY ca.deadline ASC")
	List<CaseActivity> findOverdueActivities();
	
	/**
	 * Find activities within date range
	 */
	List<CaseActivity> findByActivityDateBetweenOrderByActivityDateDesc(
			LocalDateTime startDate, LocalDateTime endDate);
	
	/**
	 * Find billable activities for a case
	 */
	List<CaseActivity> findByLegalCaseCaseIdAndBillableHoursIsNotNullOrderByActivityDateDesc(Integer caseId);
	
	/**
	 * Find milestone activities for a case
	 */
	List<CaseActivity> findByLegalCaseCaseIdAndIsMilestoneTrueOrderByActivityDateDesc(Integer caseId);
	
	/**
	 * Find activities by performer within date range
	 */
	List<CaseActivity> findByPerformedByAndActivityDateBetweenOrderByActivityDateDesc(
			String performedBy, LocalDateTime startDate, LocalDateTime endDate);
	
	/**
	 * Search activities by title or description
	 */
	@Query("SELECT ca FROM CaseActivity ca WHERE " +
	       "(ca.title LIKE %:searchText% OR ca.description LIKE %:searchText%) " +
	       "ORDER BY ca.activityDate DESC")
	List<CaseActivity> searchActivities(@Param("searchText") String searchText);
	
	/**
	 * Count activities for a case
	 */
	long countByLegalCaseCaseId(Integer caseId);
	
	/**
	 * Count completed activities for a case
	 */
	long countByLegalCaseCaseIdAndIsCompletedTrue(Integer caseId);
	
	/**
	 * Count pending activities for a case
	 */
	long countByLegalCaseCaseIdAndIsCompletedFalse(Integer caseId);
	
	/**
	 * Get total billable hours for a case
	 */
	@Query("SELECT COALESCE(SUM(ca.billableHours), 0) FROM CaseActivity ca WHERE ca.legalCase.caseId = :caseId")
	Double getTotalBillableHoursByCase(@Param("caseId") Integer caseId);
	
	/**
	 * Get total cost for activities in a case
	 */
	@Query("SELECT COALESCE(SUM(ca.cost), 0) FROM CaseActivity ca WHERE ca.legalCase.caseId = :caseId")
	Double getTotalCostByCase(@Param("caseId") Integer caseId);
	
	/**
	 * Get total hours spent on a case
	 */
	@Query("SELECT COALESCE(SUM(ca.hoursSpent), 0) FROM CaseActivity ca WHERE ca.legalCase.caseId = :caseId")
	Double getTotalHoursSpentByCase(@Param("caseId") Integer caseId);
	
	/**
	 * Find recent activities across all cases
	 */
	@Query("SELECT ca FROM CaseActivity ca ORDER BY ca.activityDate DESC")
	List<CaseActivity> findRecentActivities(Pageable pageable);
	
	/**
	 * Find activities by multiple criteria
	 */
	@Query("SELECT ca FROM CaseActivity ca WHERE " +
	       "(:caseId IS NULL OR ca.legalCase.caseId = :caseId) AND " +
	       "(:performedBy IS NULL OR ca.performedBy = :performedBy) AND " +
	       "(:activityType IS NULL OR ca.activityType = :activityType) AND " +
	       "(:isCompleted IS NULL OR ca.isCompleted = :isCompleted) AND " +
	       "(:searchText IS NULL OR ca.title LIKE %:searchText% OR ca.description LIKE %:searchText%) " +
	       "ORDER BY ca.activityDate DESC")
	Page<CaseActivity> searchActivities(
			@Param("caseId") Integer caseId,
			@Param("performedBy") String performedBy,
			@Param("activityType") ActivityType activityType,
			@Param("isCompleted") Boolean isCompleted,
			@Param("searchText") String searchText,
			Pageable pageable
	);
	
}