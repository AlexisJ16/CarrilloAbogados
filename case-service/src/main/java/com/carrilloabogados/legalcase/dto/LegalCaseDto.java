package com.carrilloabogados.legalcase.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.carrilloabogados.legalcase.domain.CasePriority;
import com.carrilloabogados.legalcase.domain.CaseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public final class LegalCaseDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("caseId")
	private Integer caseId;
	
	@JsonProperty("caseNumber")
	@NotBlank(message = "Case number must not be blank")
	@Size(max = 50, message = "Case number must not exceed 50 characters")
	private String caseNumber;
	
	@JsonProperty("title")
	@NotBlank(message = "Case title must not be blank")
	@Size(max = 200, message = "Case title must not exceed 200 characters")
	private String title;
	
	@JsonProperty("description")
	@Size(max = 2000, message = "Case description must not exceed 2000 characters")
	private String description;
	
	@JsonProperty("clientId")
	@NotNull(message = "Client ID must not be null")
	private Integer clientId;
	
	@JsonProperty("responsibleLawyer")
	@Size(max = 100, message = "Responsible lawyer name must not exceed 100 characters")
	private String responsibleLawyer;
	
	@JsonProperty("caseType")
	@NotNull(message = "Case type must not be null")
	private CaseTypeDto caseType;
	
	@JsonProperty("status")
	@NotNull(message = "Case status must not be null")
	private CaseStatus status;
	
	@JsonProperty("priority")
	private CasePriority priority;
	
	@JsonProperty("startDate")
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "Start date must not be null")
	private LocalDate startDate;
	
	@JsonProperty("endDate")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@JsonProperty("courtName")
	@Size(max = 150, message = "Court name must not exceed 150 characters")
	private String courtName;
	
	@JsonProperty("judgeName")
	@Size(max = 100, message = "Judge name must not exceed 100 characters")
	private String judgeName;
	
	@JsonProperty("estimatedDurationDays")
	private Integer estimatedDurationDays;
	
	@JsonProperty("totalAmount")
	private Double totalAmount;
	
	@JsonProperty("nextHearingDate")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime nextHearingDate;
	
	@JsonProperty("notes")
	private String notes;
	
	@JsonProperty("activities")
	private Set<CaseActivityDto> activities;
	
	@JsonProperty("caseDocuments")
	private Set<CaseDocumentDto> caseDocuments;
	
	@JsonProperty("createdAt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private java.time.Instant createdAt;
	
	@JsonProperty("updatedAt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private java.time.Instant updatedAt;
	
}