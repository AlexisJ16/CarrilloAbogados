package com.carrilloabogados.legalcase.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "case_types")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = { "legalCases" })
@Data
@Builder
public final class CaseType extends AbstractMappedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_type_id", unique = true, nullable = false, updatable = false)
    private Integer caseTypeId;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Case type name must not be blank")
    @Size(max = 100, message = "Case type name must not exceed 100 characters")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Column(name = "category")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;

    @Column(name = "base_fee")
    private Double baseFee;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "requires_court_filing")
    @Builder.Default
    private Boolean requiresCourtFiling = false;

    @Column(name = "requires_notarization")
    @Builder.Default
    private Boolean requiresNotarization = false;

    @Column(name = "complexity_level")
    private Integer complexityLevel; // 1-5 scale

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "caseType", fetch = FetchType.LAZY)
    private Set<LegalCase> legalCases;

}