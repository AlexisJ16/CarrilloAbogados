package com.carrilloabogados.legalcase.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrilloabogados.legalcase.domain.CaseType;
import com.carrilloabogados.legalcase.dto.CaseTypeDto;
import com.carrilloabogados.legalcase.exception.wrapper.CaseTypeNotFoundException;
import com.carrilloabogados.legalcase.helper.CaseTypeMappingHelper;
import com.carrilloabogados.legalcase.repository.CaseTypeRepository;
import com.carrilloabogados.legalcase.service.CaseTypeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CaseTypeServiceImpl implements CaseTypeService {

    private final CaseTypeRepository caseTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findAll() {
        log.info("*** CaseTypeDto List, service; fetch all case types ***");
        return caseTypeRepository.findAllByOrderByNameAsc()
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseTypeDto> findAll(Pageable pageable) {
        log.info("*** CaseTypeDto Page, service; fetch all case types with pagination ***");
        return caseTypeRepository.findAll(pageable)
                .map(CaseTypeMappingHelper::map);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseTypeDto findById(Integer caseTypeId) {
        log.info("*** CaseTypeDto, service; fetch case type by id ***");
        return caseTypeRepository.findById(caseTypeId)
                .map(CaseTypeMappingHelper::map)
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with id: %d not found", caseTypeId)));
    }

    @Override
    @Transactional(readOnly = true)
    public CaseTypeDto findByName(String name) {
        log.info("*** CaseTypeDto, service; fetch case type by name ***");
        return caseTypeRepository.findByName(name)
                .map(CaseTypeMappingHelper::map)
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with name: %s not found", name)));
    }

    @Override
    public CaseTypeDto save(CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, service; save case type ***");

        CaseType caseType = CaseTypeMappingHelper.map(caseTypeDto);
        caseType.setCreatedAt(Instant.now());
        caseType.setUpdatedAt(Instant.now());

        // Set defaults
        if (caseType.getIsActive() == null) {
            caseType.setIsActive(true);
        }
        if (caseType.getRequiresCourtFiling() == null) {
            caseType.setRequiresCourtFiling(false);
        }
        if (caseType.getRequiresNotarization() == null) {
            caseType.setRequiresNotarization(false);
        }

        CaseType savedType = caseTypeRepository.save(caseType);
        log.info("*** Case type saved with id: {} ***", savedType.getCaseTypeId());

        return CaseTypeMappingHelper.map(savedType);
    }

    @Override
    public CaseTypeDto update(CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, service; update case type ***");

        CaseType existingType = caseTypeRepository.findById(caseTypeDto.getCaseTypeId())
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with id: %d not found", caseTypeDto.getCaseTypeId())));

        updateFields(existingType, caseTypeDto);
        existingType.setUpdatedAt(Instant.now());

        CaseType updatedType = caseTypeRepository.save(existingType);
        log.info("*** Case type updated with id: {} ***", updatedType.getCaseTypeId());

        return CaseTypeMappingHelper.map(updatedType);
    }

    @Override
    public CaseTypeDto update(Integer caseTypeId, CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, service; update case type by id ***");
        caseTypeDto.setCaseTypeId(caseTypeId);
        return update(caseTypeDto);
    }

    @Override
    public void deleteById(Integer caseTypeId) {
        log.info("*** Void, service; delete case type by id ***");

        if (!caseTypeRepository.existsById(caseTypeId)) {
            throw new CaseTypeNotFoundException(
                    String.format("Case type with id: %d not found", caseTypeId));
        }

        caseTypeRepository.deleteById(caseTypeId);
        log.info("*** Case type deleted with id: {} ***", caseTypeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findActiveTypes() {
        log.info("*** CaseTypeDto List, service; fetch active case types ***");
        return caseTypeRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findByCategory(String category) {
        log.info("*** CaseTypeDto List, service; fetch case types by category ***");
        return caseTypeRepository.findByCategoryOrderByNameAsc(category)
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findActiveBycategory(String category) {
        log.info("*** CaseTypeDto List, service; fetch active case types by category ***");
        return caseTypeRepository.findByCategoryAndIsActiveTrueOrderByNameAsc(category)
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findRequiringCourtFiling() {
        log.info("*** CaseTypeDto List, service; fetch case types requiring court filing ***");
        return caseTypeRepository.findByRequiresCourtFilingTrueAndIsActiveTrueOrderByNameAsc()
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findRequiringNotarization() {
        log.info("*** CaseTypeDto List, service; fetch case types requiring notarization ***");
        return caseTypeRepository.findByRequiresNotarizationTrueAndIsActiveTrueOrderByNameAsc()
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findByComplexityLevel(Integer level) {
        log.info("*** CaseTypeDto List, service; fetch case types by complexity level ***");
        return caseTypeRepository.findByComplexityLevelAndIsActiveTrueOrderByNameAsc(level)
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaseTypeDto> findByComplexityRange(Integer minLevel, Integer maxLevel) {
        log.info("*** CaseTypeDto List, service; fetch case types by complexity range ***");
        return caseTypeRepository
                .findByComplexityLevelBetweenAndIsActiveTrueOrderByComplexityLevelAsc(minLevel, maxLevel)
                .stream()
                .map(CaseTypeMappingHelper::map)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllCategories() {
        log.info("*** String List, service; fetch all case type categories ***");
        return caseTypeRepository.findDistinctCategories();
    }

    @Override
    public CaseTypeDto activate(Integer caseTypeId) {
        log.info("*** CaseTypeDto, service; activate case type ***");

        CaseType caseType = caseTypeRepository.findById(caseTypeId)
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with id: %d not found", caseTypeId)));

        caseType.setIsActive(true);
        caseType.setUpdatedAt(Instant.now());

        CaseType updatedType = caseTypeRepository.save(caseType);
        log.info("*** Case type {} activated ***", caseTypeId);

        return CaseTypeMappingHelper.map(updatedType);
    }

    @Override
    public CaseTypeDto deactivate(Integer caseTypeId) {
        log.info("*** CaseTypeDto, service; deactivate case type ***");

        CaseType caseType = caseTypeRepository.findById(caseTypeId)
                .orElseThrow(() -> new CaseTypeNotFoundException(
                        String.format("Case type with id: %d not found", caseTypeId)));

        caseType.setIsActive(false);
        caseType.setUpdatedAt(Instant.now());

        CaseType updatedType = caseTypeRepository.save(caseType);
        log.info("*** Case type {} deactivated ***", caseTypeId);

        return CaseTypeMappingHelper.map(updatedType);
    }

    // ============== Private Helper Methods ==============

    private void updateFields(CaseType existingType, CaseTypeDto dto) {
        if (dto.getName() != null) {
            existingType.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            existingType.setDescription(dto.getDescription());
        }
        if (dto.getCategory() != null) {
            existingType.setCategory(dto.getCategory());
        }
        if (dto.getIsActive() != null) {
            existingType.setIsActive(dto.getIsActive());
        }
        if (dto.getEstimatedDurationDays() != null) {
            existingType.setEstimatedDurationDays(dto.getEstimatedDurationDays());
        }
        if (dto.getBaseFee() != null) {
            existingType.setBaseFee(dto.getBaseFee());
        }
        if (dto.getHourlyRate() != null) {
            existingType.setHourlyRate(dto.getHourlyRate());
        }
        if (dto.getRequiresCourtFiling() != null) {
            existingType.setRequiresCourtFiling(dto.getRequiresCourtFiling());
        }
        if (dto.getRequiresNotarization() != null) {
            existingType.setRequiresNotarization(dto.getRequiresNotarization());
        }
        if (dto.getComplexityLevel() != null) {
            existingType.setComplexityLevel(dto.getComplexityLevel());
        }
    }
}
