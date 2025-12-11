package com.carrilloabogados.legalcase.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "legal_cases")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"activities", "caseDocuments"})
@Data
@Builder
public final class LegalCase extends AbstractMappedEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "case_id", unique = true, nullable = false, updatable = false)
	private Integer caseId;
	
	@Column(name = "case_number", unique = true, nullable = false)
	@NotBlank(message = "Case number must not be blank")
	@Size(max = 50, message = "Case number must not exceed 50 characters")
	private String caseNumber;
	
	@Column(name = "title", nullable = false)
	@NotBlank(message = "Case title must not be blank")
	@Size(max = 200, message = "Case title must not exceed 200 characters")
	private String title;
	
	@Column(name = "description", columnDefinition = "TEXT")
	@Size(max = 2000, message = "Case description must not exceed 2000 characters")
	private String description;
	
	@Column(name = "client_id", nullable = false)
	@NotNull(message = "Client ID must not be null")
	private Integer clientId;
	
	@Column(name = "responsible_lawyer")
	@Size(max = 100, message = "Responsible lawyer name must not exceed 100 characters")
	private String responsibleLawyer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_type_id", nullable = false)
	@NotNull(message = "Case type must not be null")
	private CaseType caseType;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@NotNull(message = "Case status must not be null")
	private CaseStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "priority")
	private CasePriority priority;
	
	@Column(name = "start_date", nullable = false)
	@NotNull(message = "Start date must not be null")
	private LocalDate startDate;
	
	@Column(name = "end_date")
	private LocalDate endDate;
	
	@Column(name = "court_name")
	@Size(max = 150, message = "Court name must not exceed 150 characters")
	private String courtName;
	
	@Column(name = "judge_name")
	@Size(max = 100, message = "Judge name must not exceed 100 characters")
	private String judgeName;
	
	@Column(name = "estimated_duration_days")
	private Integer estimatedDurationDays;
	
	@Column(name = "total_amount")
	private Double totalAmount;
	
	@Column(name = "next_hearing_date")
	private LocalDateTime nextHearingDate;
	
	@Column(name = "notes", columnDefinition = "TEXT")
	private String notes;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "legalCase", fetch = FetchType.LAZY)
	private Set<CaseActivity> activities;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "legalCase", fetch = FetchType.LAZY)
	private Set<CaseDocument> caseDocuments;
	
}