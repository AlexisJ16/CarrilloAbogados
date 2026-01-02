package com.carrilloabogados.legalcase.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.carrilloabogados.legalcase.dto.CaseTypeDto;
import com.carrilloabogados.legalcase.service.CaseTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for Case Type management
 * Base path: /api/case-types
 */
@RestController
@RequestMapping("/api/case-types")
@Slf4j
@RequiredArgsConstructor
public class CaseTypeController {

    private final CaseTypeService caseTypeService;

    // ============== CRUD Operations ==============

    /**
     * GET /api/case-types - Get all case types
     */
    @GetMapping
    public ResponseEntity<List<CaseTypeDto>> findAll() {
        log.info("*** CaseTypeDto List, controller; fetch all case types ***");
        return ResponseEntity.ok(caseTypeService.findAll());
    }

    /**
     * GET /api/case-types/page - Get case types with pagination
     */
    @GetMapping("/page")
    public ResponseEntity<Page<CaseTypeDto>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("*** CaseTypeDto Page, controller; fetch all case types paginated ***");
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(caseTypeService.findAll(pageable));
    }

    /**
     * GET /api/case-types/{caseTypeId} - Get case type by ID
     */
    @GetMapping("/{caseTypeId}")
    public ResponseEntity<CaseTypeDto> findById(@PathVariable Integer caseTypeId) {
        log.info("*** CaseTypeDto, controller; fetch case type by id ***");
        return ResponseEntity.ok(caseTypeService.findById(caseTypeId));
    }

    /**
     * GET /api/case-types/name/{name} - Get case type by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<CaseTypeDto> findByName(@PathVariable String name) {
        log.info("*** CaseTypeDto, controller; fetch case type by name ***");
        return ResponseEntity.ok(caseTypeService.findByName(name));
    }

    /**
     * POST /api/case-types - Create a new case type
     */
    @PostMapping
    public ResponseEntity<CaseTypeDto> save(@RequestBody @Valid CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, controller; save case type ***");
        return new ResponseEntity<>(caseTypeService.save(caseTypeDto), HttpStatus.CREATED);
    }

    /**
     * PUT /api/case-types - Update a case type
     */
    @PutMapping
    public ResponseEntity<CaseTypeDto> update(@RequestBody @Valid CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, controller; update case type ***");
        return ResponseEntity.ok(caseTypeService.update(caseTypeDto));
    }

    /**
     * PUT /api/case-types/{caseTypeId} - Update a case type by ID
     */
    @PutMapping("/{caseTypeId}")
    public ResponseEntity<CaseTypeDto> updateById(
            @PathVariable Integer caseTypeId,
            @RequestBody @Valid CaseTypeDto caseTypeDto) {
        log.info("*** CaseTypeDto, controller; update case type by id ***");
        return ResponseEntity.ok(caseTypeService.update(caseTypeId, caseTypeDto));
    }

    /**
     * DELETE /api/case-types/{caseTypeId} - Delete a case type
     */
    @DeleteMapping("/{caseTypeId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer caseTypeId) {
        log.info("*** Void, controller; delete case type by id ***");
        caseTypeService.deleteById(caseTypeId);
        return ResponseEntity.noContent().build();
    }

    // ============== Business Logic Queries ==============

    /**
     * GET /api/case-types/active - Get all active case types
     */
    @GetMapping("/active")
    public ResponseEntity<List<CaseTypeDto>> findActiveTypes() {
        log.info("*** CaseTypeDto List, controller; fetch active case types ***");
        return ResponseEntity.ok(caseTypeService.findActiveTypes());
    }

    /**
     * GET /api/case-types/category/{category} - Get case types by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<CaseTypeDto>> findByCategory(@PathVariable String category) {
        log.info("*** CaseTypeDto List, controller; fetch case types by category ***");
        return ResponseEntity.ok(caseTypeService.findByCategory(category));
    }

    /**
     * GET /api/case-types/category/{category}/active - Get active case types by
     * category
     */
    @GetMapping("/category/{category}/active")
    public ResponseEntity<List<CaseTypeDto>> findActiveBycategory(@PathVariable String category) {
        log.info("*** CaseTypeDto List, controller; fetch active case types by category ***");
        return ResponseEntity.ok(caseTypeService.findActiveBycategory(category));
    }

    /**
     * GET /api/case-types/categories - Get all categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> findAllCategories() {
        log.info("*** String List, controller; fetch all case type categories ***");
        return ResponseEntity.ok(caseTypeService.findAllCategories());
    }

    /**
     * GET /api/case-types/court-filing - Get case types requiring court filing
     */
    @GetMapping("/court-filing")
    public ResponseEntity<List<CaseTypeDto>> findRequiringCourtFiling() {
        log.info("*** CaseTypeDto List, controller; fetch case types requiring court filing ***");
        return ResponseEntity.ok(caseTypeService.findRequiringCourtFiling());
    }

    /**
     * GET /api/case-types/notarization - Get case types requiring notarization
     */
    @GetMapping("/notarization")
    public ResponseEntity<List<CaseTypeDto>> findRequiringNotarization() {
        log.info("*** CaseTypeDto List, controller; fetch case types requiring notarization ***");
        return ResponseEntity.ok(caseTypeService.findRequiringNotarization());
    }

    /**
     * GET /api/case-types/complexity/{level} - Get case types by complexity level
     */
    @GetMapping("/complexity/{level}")
    public ResponseEntity<List<CaseTypeDto>> findByComplexityLevel(@PathVariable Integer level) {
        log.info("*** CaseTypeDto List, controller; fetch case types by complexity level ***");
        return ResponseEntity.ok(caseTypeService.findByComplexityLevel(level));
    }

    /**
     * GET /api/case-types/complexity - Get case types by complexity range
     */
    @GetMapping("/complexity")
    public ResponseEntity<List<CaseTypeDto>> findByComplexityRange(
            @RequestParam Integer minLevel,
            @RequestParam Integer maxLevel) {
        log.info("*** CaseTypeDto List, controller; fetch case types by complexity range ***");
        return ResponseEntity.ok(caseTypeService.findByComplexityRange(minLevel, maxLevel));
    }

    // ============== Case Type Management ==============

    /**
     * PATCH /api/case-types/{caseTypeId}/activate - Activate a case type
     */
    @PatchMapping("/{caseTypeId}/activate")
    public ResponseEntity<CaseTypeDto> activate(@PathVariable Integer caseTypeId) {
        log.info("*** CaseTypeDto, controller; activate case type ***");
        return ResponseEntity.ok(caseTypeService.activate(caseTypeId));
    }

    /**
     * PATCH /api/case-types/{caseTypeId}/deactivate - Deactivate a case type
     */
    @PatchMapping("/{caseTypeId}/deactivate")
    public ResponseEntity<CaseTypeDto> deactivate(@PathVariable Integer caseTypeId) {
        log.info("*** CaseTypeDto, controller; deactivate case type ***");
        return ResponseEntity.ok(caseTypeService.deactivate(caseTypeId));
    }
}
