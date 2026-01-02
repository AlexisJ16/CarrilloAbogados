package com.carrilloabogados.legalcase.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.carrilloabogados.legalcase.dto.CaseTypeDto;

public interface CaseTypeService {

    List<CaseTypeDto> findAll();

    Page<CaseTypeDto> findAll(Pageable pageable);

    CaseTypeDto findById(Integer caseTypeId);

    CaseTypeDto findByName(String name);

    CaseTypeDto save(CaseTypeDto caseTypeDto);

    CaseTypeDto update(CaseTypeDto caseTypeDto);

    CaseTypeDto update(Integer caseTypeId, CaseTypeDto caseTypeDto);

    void deleteById(Integer caseTypeId);

    // Business logic methods

    List<CaseTypeDto> findActiveTypes();

    List<CaseTypeDto> findByCategory(String category);

    List<CaseTypeDto> findActiveBycategory(String category);

    List<CaseTypeDto> findRequiringCourtFiling();

    List<CaseTypeDto> findRequiringNotarization();

    List<CaseTypeDto> findByComplexityLevel(Integer level);

    List<CaseTypeDto> findByComplexityRange(Integer minLevel, Integer maxLevel);

    List<String> findAllCategories();

    // Case type management

    CaseTypeDto activate(Integer caseTypeId);

    CaseTypeDto deactivate(Integer caseTypeId);
}
