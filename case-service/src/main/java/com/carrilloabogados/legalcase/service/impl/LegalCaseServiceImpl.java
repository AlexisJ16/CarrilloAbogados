package com.carrilloabogados.legalcase.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrilloabogados.legalcase.domain.CasePriority;
import com.carrilloabogados.legalcase.domain.CaseStatus;
import com.carrilloabogados.legalcase.domain.CaseType;
import com.carrilloabogados.legalcase.domain.LegalCase;
import com.carrilloabogados.legalcase.dto.LegalCaseDto;
import com.carrilloabogados.legalcase.exception.wrapper.CaseTypeNotFoundException;
import com.carrilloabogados.legalcase.exception.wrapper.LegalCaseNotFoundException;
import com.carrilloabogados.legalcase.helper.LegalCaseMappingHelper;
import com.carrilloabogados.legalcase.repository.CaseTypeRepository;
import com.carrilloabogados.legalcase.repository.LegalCaseRepository;
import com.carrilloabogados.legalcase.service.LegalCaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class LegalCaseServiceImpl implements LegalCaseService {

    private final LegalCaseRepository legalCaseRepository;
    private final CaseTypeRepository caseTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findAll() {
        log.info("*** LegalCaseDto List, service; fetch all legal cases ***");
        return legalCaseRepository.findAll()
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalCaseDto> findAll(Pageable pageable) {
        log.info("*** LegalCaseDto Page, service; fetch all legal cases with pagination ***");
        return legalCaseRepository.findAll(pageable)
                .map(LegalCaseMappingHelper::mapWithoutCollections);
    }

    @Override
    @Transactional(readOnly = true)
    public LegalCaseDto findById(Integer caseId) {
        log.info("*** LegalCaseDto, service; fetch legal case by id ***");
        return legalCaseRepository.findById(caseId)
                .map(LegalCaseMappingHelper::map)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));
    }

    @Override
    @Transactional(readOnly = true)
    public LegalCaseDto findByCaseNumber(String caseNumber) {
        log.info("*** LegalCaseDto, service; fetch legal case by case number ***");
        return legalCaseRepository.findByCaseNumber(caseNumber)
                .map(LegalCaseMappingHelper::map)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with case number: %s not found", caseNumber)));
    }

    @Override
    public LegalCaseDto save(LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, service; save legal case ***");

        // Validate and get case type
        CaseType caseType = validateAndGetCaseType(legalCaseDto);

        LegalCase legalCase = LegalCaseMappingHelper.map(legalCaseDto);
        legalCase.setCaseType(caseType);
        legalCase.setCreatedAt(Instant.now());
        legalCase.setUpdatedAt(Instant.now());

        // Set default status if not provided
        if (legalCase.getStatus() == null) {
            legalCase.setStatus(CaseStatus.PENDING);
        }

        // Set default priority if not provided
        if (legalCase.getPriority() == null) {
            legalCase.setPriority(CasePriority.NORMAL);
        }

        // Set estimated duration from case type if not provided
        if (legalCase.getEstimatedDurationDays() == null && caseType.getEstimatedDurationDays() != null) {
            legalCase.setEstimatedDurationDays(caseType.getEstimatedDurationDays());
        }

        LegalCase savedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case saved with id: {} ***", savedCase.getCaseId());

        return LegalCaseMappingHelper.map(savedCase);
    }

    @Override
    public LegalCaseDto update(LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, service; update legal case ***");

        LegalCase existingCase = legalCaseRepository.findById(legalCaseDto.getCaseId())
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", legalCaseDto.getCaseId())));

        updateCaseFields(existingCase, legalCaseDto);
        existingCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(existingCase);
        log.info("*** Legal case updated with id: {} ***", updatedCase.getCaseId());

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto update(Integer caseId, LegalCaseDto legalCaseDto) {
        log.info("*** LegalCaseDto, service; update legal case by id ***");
        legalCaseDto.setCaseId(caseId);
        return update(legalCaseDto);
    }

    @Override
    public void deleteById(Integer caseId) {
        log.info("*** Void, service; delete legal case by id ***");

        if (!legalCaseRepository.existsById(caseId)) {
            throw new LegalCaseNotFoundException(
                    String.format("Legal case with id: %d not found", caseId));
        }

        legalCaseRepository.deleteById(caseId);
        log.info("*** Legal case deleted with id: {} ***", caseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByClientId(Integer clientId) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by client id ***");
        return legalCaseRepository.findByClientIdOrderByStartDateDesc(clientId)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalCaseDto> findByClientId(Integer clientId, Pageable pageable) {
        log.info("*** LegalCaseDto Page, service; fetch legal cases by client id with pagination ***");
        return legalCaseRepository.findByClientId(clientId, pageable)
                .map(LegalCaseMappingHelper::mapWithoutCollections);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByStatus(CaseStatus status) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by status ***");
        return legalCaseRepository.findByStatusOrderByStartDateDesc(status)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findActiveCases() {
        log.info("*** LegalCaseDto List, service; fetch active legal cases ***");
        return legalCaseRepository.findActiveCases()
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByResponsibleLawyer(String responsibleLawyer) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by lawyer ***");
        return legalCaseRepository.findByResponsibleLawyerOrderByStartDateDesc(responsibleLawyer)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByPriority(CasePriority priority) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by priority ***");
        return legalCaseRepository.findByPriorityOrderByStartDateDesc(priority)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByCaseType(Integer caseTypeId) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by case type ***");
        return legalCaseRepository.findByCaseTypeCaseTypeIdOrderByStartDateDesc(caseTypeId)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findCasesWithUpcomingHearings() {
        log.info("*** LegalCaseDto List, service; fetch legal cases with upcoming hearings ***");
        return legalCaseRepository.findCasesWithUpcomingHearings()
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by date range ***");
        return legalCaseRepository.findByStartDateBetweenOrderByStartDateDesc(startDate, endDate)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findOverdueCases() {
        log.info("*** LegalCaseDto List, service; fetch overdue legal cases ***");
        return legalCaseRepository.findOverdueCases()
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> searchByTitle(String title) {
        log.info("*** LegalCaseDto List, service; search legal cases by title ***");
        return legalCaseRepository.findByTitleContainingIgnoreCaseOrderByStartDateDesc(title)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> searchByDescription(String description) {
        log.info("*** LegalCaseDto List, service; search legal cases by description ***");
        return legalCaseRepository.findByDescriptionContainingIgnoreCaseOrderByStartDateDesc(description)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByCourtName(String courtName) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by court name ***");
        return legalCaseRepository.findByCourtNameContainingIgnoreCaseOrderByStartDateDesc(courtName)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findByJudgeName(String judgeName) {
        log.info("*** LegalCaseDto List, service; fetch legal cases by judge name ***");
        return legalCaseRepository.findByJudgeNameContainingIgnoreCaseOrderByStartDateDesc(judgeName)
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(CaseStatus status) {
        log.info("*** Long, service; count legal cases by status ***");
        return legalCaseRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByClientId(Integer clientId) {
        log.info("*** Long, service; count legal cases by client id ***");
        return legalCaseRepository.countByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveCasesByLawyer(String responsibleLawyer) {
        log.info("*** Long, service; count active legal cases by lawyer ***");
        return legalCaseRepository.countActiveCasesByLawyer(responsibleLawyer);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalAmountByStatus(CaseStatus status) {
        log.info("*** Double, service; get total amount by status ***");
        return legalCaseRepository.getTotalAmountByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LegalCaseDto> findRecentlyUpdatedCases(int limit) {
        log.info("*** LegalCaseDto List, service; fetch recently updated legal cases ***");
        return legalCaseRepository.findRecentlyUpdatedCases(PageRequest.of(0, limit))
                .stream()
                .map(LegalCaseMappingHelper::mapWithoutCollections)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalCaseDto> searchCases(Integer clientId, CaseStatus status,
            String responsibleLawyer, Integer caseTypeId, String searchText, Pageable pageable) {
        log.info("*** LegalCaseDto Page, service; search legal cases ***");
        return legalCaseRepository.searchCases(clientId, status, responsibleLawyer, caseTypeId, searchText, pageable)
                .map(LegalCaseMappingHelper::mapWithoutCollections);
    }

    @Override
    public LegalCaseDto updateStatus(Integer caseId, CaseStatus newStatus) {
        log.info("*** LegalCaseDto, service; update legal case status ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        CaseStatus oldStatus = legalCase.getStatus();
        legalCase.setStatus(newStatus);
        legalCase.setUpdatedAt(Instant.now());

        // If case is being completed or closed, set end date
        if ((newStatus == CaseStatus.COMPLETED || newStatus == CaseStatus.CLOSED)
                && legalCase.getEndDate() == null) {
            legalCase.setEndDate(LocalDate.now());
        }

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} status changed from {} to {} ***", caseId, oldStatus, newStatus);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto updatePriority(Integer caseId, CasePriority newPriority) {
        log.info("*** LegalCaseDto, service; update legal case priority ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        CasePriority oldPriority = legalCase.getPriority();
        legalCase.setPriority(newPriority);
        legalCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} priority changed from {} to {} ***", caseId, oldPriority, newPriority);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto assignLawyer(Integer caseId, String lawyerName) {
        log.info("*** LegalCaseDto, service; assign lawyer to legal case ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        String previousLawyer = legalCase.getResponsibleLawyer();
        legalCase.setResponsibleLawyer(lawyerName);
        legalCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} lawyer changed from {} to {} ***", caseId, previousLawyer, lawyerName);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto setNextHearing(Integer caseId, LocalDateTime hearingDate) {
        log.info("*** LegalCaseDto, service; set next hearing for legal case ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        legalCase.setNextHearingDate(hearingDate);
        legalCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} next hearing set to {} ***", caseId, hearingDate);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto closeCase(Integer caseId, LocalDate endDate, String notes) {
        log.info("*** LegalCaseDto, service; close legal case ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        legalCase.setStatus(CaseStatus.CLOSED);
        legalCase.setEndDate(endDate != null ? endDate : LocalDate.now());
        if (notes != null && !notes.isBlank()) {
            String existingNotes = legalCase.getNotes() != null ? legalCase.getNotes() + "\n\n" : "";
            legalCase.setNotes(existingNotes + "[CIERRE] " + notes);
        }
        legalCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} closed ***", caseId);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    @Override
    public LegalCaseDto reopenCase(Integer caseId, String notes) {
        log.info("*** LegalCaseDto, service; reopen legal case ***");

        LegalCase legalCase = legalCaseRepository.findById(caseId)
                .orElseThrow(() -> new LegalCaseNotFoundException(
                        String.format("Legal case with id: %d not found", caseId)));

        if (legalCase.getStatus() != CaseStatus.CLOSED && legalCase.getStatus() != CaseStatus.CANCELLED) {
            throw new IllegalStateException(
                    String.format("Legal case %d cannot be reopened because it is not closed or cancelled", caseId));
        }

        legalCase.setStatus(CaseStatus.IN_PROGRESS);
        legalCase.setEndDate(null);
        if (notes != null && !notes.isBlank()) {
            String existingNotes = legalCase.getNotes() != null ? legalCase.getNotes() + "\n\n" : "";
            legalCase.setNotes(existingNotes + "[REAPERTURA] " + notes);
        }
        legalCase.setUpdatedAt(Instant.now());

        LegalCase updatedCase = legalCaseRepository.save(legalCase);
        log.info("*** Legal case {} reopened ***", caseId);

        return LegalCaseMappingHelper.map(updatedCase);
    }

    // ============== Private Helper Methods ==============

    private CaseType validateAndGetCaseType(LegalCaseDto legalCaseDto) {
        if (legalCaseDto.getCaseType() == null || legalCaseDto.getCaseType().getCaseTypeId() == null) {
            throw new IllegalArgumentException("Case type ID must be provided");
        }

        return caseTypeRepository.findById(legalCaseDto.getCaseType().getCaseTypeId())
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with id: %d not found",
                                legalCaseDto.getCaseType().getCaseTypeId())));
    }

    private void updateCaseFields(LegalCase existingCase, LegalCaseDto dto) {
        if (dto.getCaseNumber() != null) {
            existingCase.setCaseNumber(dto.getCaseNumber());
        }
        if (dto.getTitle() != null) {
            existingCase.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            existingCase.setDescription(dto.getDescription());
        }
        if (dto.getClientId() != null) {
            existingCase.setClientId(dto.getClientId());
        }
        if (dto.getResponsibleLawyer() != null) {
            existingCase.setResponsibleLawyer(dto.getResponsibleLawyer());
        }
        if (dto.getCaseType() != null && dto.getCaseType().getCaseTypeId() != null) {
            CaseType caseType = caseTypeRepository.findById(dto.getCaseType().getCaseTypeId())
                    .orElseThrow(() -> new CaseTypeNotFoundException(
                            String.format("Case type with id: %d not found", dto.getCaseType().getCaseTypeId())));
            existingCase.setCaseType(caseType);
        }
        if (dto.getStatus() != null) {
            existingCase.setStatus(dto.getStatus());
        }
        if (dto.getPriority() != null) {
            existingCase.setPriority(dto.getPriority());
        }
        if (dto.getStartDate() != null) {
            existingCase.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            existingCase.setEndDate(dto.getEndDate());
        }
        if (dto.getCourtName() != null) {
            existingCase.setCourtName(dto.getCourtName());
        }
        if (dto.getJudgeName() != null) {
            existingCase.setJudgeName(dto.getJudgeName());
        }
        if (dto.getEstimatedDurationDays() != null) {
            existingCase.setEstimatedDurationDays(dto.getEstimatedDurationDays());
        }
        if (dto.getTotalAmount() != null) {
            existingCase.setTotalAmount(dto.getTotalAmount());
        }
        if (dto.getNextHearingDate() != null) {
            existingCase.setNextHearingDate(dto.getNextHearingDate());
        }
        if (dto.getNotes() != null) {
            existingCase.setNotes(dto.getNotes());
        }
    }
}
