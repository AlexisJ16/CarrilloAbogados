package com.carrilloabogados.legalcase.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CaseTypeDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("caseTypeId")
    private Integer caseTypeId;

    @JsonProperty("name")
    @NotBlank(message = "Case type name must not be blank")
    @Size(max = 100, message = "Case type name must not exceed 100 characters")
    private String name;

    @JsonProperty("description")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @JsonProperty("category")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @JsonProperty("isActive")
    @Builder.Default
    private Boolean isActive = true;

    @JsonProperty("estimatedDurationDays")
    private Integer estimatedDurationDays;

    @JsonProperty("baseFee")
    private Double baseFee;

    @JsonProperty("hourlyRate")
    private Double hourlyRate;

    @JsonProperty("requiresCourtFiling")
    @Builder.Default
    private Boolean requiresCourtFiling = false;

    @JsonProperty("requiresNotarization")
    @Builder.Default
    private Boolean requiresNotarization = false;

    @JsonProperty("complexityLevel")
    private Integer complexityLevel;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}