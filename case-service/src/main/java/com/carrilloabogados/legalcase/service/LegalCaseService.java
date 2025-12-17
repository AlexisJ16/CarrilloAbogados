package com.carrilloabogados.legalcase.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carrilloabogados.legalcase.domain.CasePriority;
import com.carrilloabogados.legalcase.domain.CaseStatus;
import com.carrilloabogados.legalcase.dto.LegalCaseDto;

public interface LegalCaseService {

	List<LegalCaseDto> findAll();
	
	Page<LegalCaseDto> findAll(Pageable pageable);
	
	LegalCaseDto findById(Integer caseId);
	
	LegalCaseDto findByCaseNumber(String caseNumber);
	
	LegalCaseDto save(LegalCaseDto legalCaseDto);
	
	LegalCaseDto update(LegalCaseDto legalCaseDto);
	
	LegalCaseDto update(Integer caseId, LegalCaseDto legalCaseDto);
	
	void deleteById(Integer caseId);
	
	// Business logic methods
	
	List<LegalCaseDto> findByClientId(Integer clientId);
	
	Page<LegalCaseDto> findByClientId(Integer clientId, Pageable pageable);
	
	List<LegalCaseDto> findByStatus(CaseStatus status);
	
	List<LegalCaseDto> findActiveCases();
	
	List<LegalCaseDto> findByResponsibleLawyer(String responsibleLawyer);
	
	List<LegalCaseDto> findByPriority(CasePriority priority);
	
	List<LegalCaseDto> findByCaseType(Integer caseTypeId);
	
	List<LegalCaseDto> findCasesWithUpcomingHearings();
	
	List<LegalCaseDto> findByDateRange(LocalDate startDate, LocalDate endDate);
	
	List<LegalCaseDto> findOverdueCases();
	
	List<LegalCaseDto> searchByTitle(String title);
	
	List<LegalCaseDto> searchByDescription(String description);
	
	List<LegalCaseDto> findByCourtName(String courtName);
	
	List<LegalCaseDto> findByJudgeName(String judgeName);
	
	// Statistics and metrics
	
	long countByStatus(CaseStatus status);
	
	long countByClientId(Integer clientId);
	
	long countActiveCasesByLawyer(String responsibleLawyer);
	
	Double getTotalAmountByStatus(CaseStatus status);
	
	List<LegalCaseDto> findRecentlyUpdatedCases(int limit);
	
	// Search functionality
	
	Page<LegalCaseDto> searchCases(
			Integer clientId, 
			CaseStatus status, 
			String responsibleLawyer, 
			Integer caseTypeId, 
			String searchText, 
			Pageable pageable
	);
	
	// Case management operations
	
	LegalCaseDto updateStatus(Integer caseId, CaseStatus newStatus);
	
	LegalCaseDto updatePriority(Integer caseId, CasePriority newPriority);
	
	LegalCaseDto assignLawyer(Integer caseId, String lawyerName);
	
	LegalCaseDto setNextHearing(Integer caseId, java.time.LocalDateTime hearingDate);
	
	LegalCaseDto closeCase(Integer caseId, LocalDate endDate, String notes);
	
	LegalCaseDto reopenCase(Integer caseId, String notes);
	
}