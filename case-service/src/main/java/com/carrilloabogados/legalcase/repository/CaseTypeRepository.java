package com.carrilloabogados.legalcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrilloabogados.legalcase.domain.CaseType;

public interface CaseTypeRepository extends JpaRepository<CaseType, Integer> {

	/**
	 * Find case type by name
	 */
	Optional<CaseType> findByName(String name);
	
	/**
	 * Find all active case types
	 */
	List<CaseType> findByIsActiveTrueOrderByNameAsc();
	
	/**
	 * Find all case types ordered by name
	 */
	List<CaseType> findAllByOrderByNameAsc();
	
	/**
	 * Find case types by category
	 */
	List<CaseType> findByCategoryOrderByNameAsc(String category);
	
	/**
	 * Find case types by category (active only)
	 */
	List<CaseType> findByCategoryAndIsActiveTrueOrderByNameAsc(String category);
	
	/**
	 * Find case types that require court filing
	 */
	List<CaseType> findByRequiresCourtFilingTrueAndIsActiveTrueOrderByNameAsc();
	
	/**
	 * Find case types that require notarization
	 */
	List<CaseType> findByRequiresNotarizationTrueAndIsActiveTrueOrderByNameAsc();
	
	/**
	 * Find case types by complexity level
	 */
	List<CaseType> findByComplexityLevelAndIsActiveTrueOrderByNameAsc(Integer complexityLevel);
	
	/**
	 * Find case types within estimated duration range
	 */
	List<CaseType> findByEstimatedDurationDaysBetweenAndIsActiveTrueOrderByEstimatedDurationDaysAsc(
			Integer minDays, Integer maxDays);
	
	/**
	 * Find case types by base fee range
	 */
	List<CaseType> findByBaseFeeGreaterThanEqualAndBaseFeeIsNotNullAndIsActiveTrueOrderByBaseFeeAsc(Double minFee);
	
	/**
	 * Find case types by hourly rate range
	 */
	List<CaseType> findByHourlyRateGreaterThanEqualAndHourlyRateIsNotNullAndIsActiveTrueOrderByHourlyRateAsc(Double minRate);
	
	/**
	 * Search case types by name or description
	 */
	@Query("SELECT ct FROM CaseType ct WHERE " +
	       "(ct.name LIKE %:searchText% OR ct.description LIKE %:searchText%) " +
	       "AND ct.isActive = true ORDER BY ct.name ASC")
	List<CaseType> searchCaseTypes(@Param("searchText") String searchText);
	
	/**
	 * Get distinct categories
	 */
	@Query("SELECT DISTINCT ct.category FROM CaseType ct WHERE ct.category IS NOT NULL AND ct.isActive = true ORDER BY ct.category")
	List<String> findDistinctCategories();
	
	/**
	 * Count active case types by category
	 */
	long countByCategoryAndIsActiveTrue(String category);
	
	/**
	 * Find most used case types (based on case count)
	 */
	@Query("SELECT ct FROM CaseType ct WHERE ct.isActive = true " +
	       "ORDER BY (SELECT COUNT(lc) FROM LegalCase lc WHERE lc.caseType = ct) DESC")
	List<CaseType> findMostUsedCaseTypes();
	
	/**
	 * Check if case type name exists (case insensitive)
	 */
	@Query("SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END FROM CaseType ct WHERE LOWER(ct.name) = LOWER(:name)")
	boolean existsByNameIgnoreCase(@Param("name") String name);
	
}