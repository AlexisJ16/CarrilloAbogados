package com.carrilloabogados.legalcase.domain;

import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "case_activities")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class CaseActivity extends AbstractMappedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id", unique = true, nullable = false, updatable = false)
    private Integer activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @NotNull(message = "Legal case must not be null")
    private LegalCase legalCase;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Activity title must not be blank")
    @Size(max = 200, message = "Activity title must not exceed 200 characters")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Activity description must not exceed 1000 characters")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    @NotNull(message = "Activity type must not be null")
    private ActivityType activityType;

    @Column(name = "performed_by")
    @Size(max = 100, message = "Performed by must not exceed 100 characters")
    private String performedBy;

    @Column(name = "activity_date", nullable = false)
    @NotNull(message = "Activity date must not be null")
    private LocalDateTime activityDate;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "hours_spent")
    private Double hoursSpent;

    @Column(name = "billable_hours")
    private Double billableHours;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_milestone")
    @Builder.Default
    private Boolean isMilestone = false;

}