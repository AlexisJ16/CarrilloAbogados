package com.carrilloabogados.legalcase.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.carrilloabogados.legalcase.domain.ActivityType;
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
public final class CaseActivityDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("activityId")
    private Integer activityId;

    @JsonProperty("caseId")
    @NotNull(message = "Case ID must not be null")
    private Integer caseId;

    @JsonProperty("title")
    @NotBlank(message = "Activity title must not be blank")
    @Size(max = 200, message = "Activity title must not exceed 200 characters")
    private String title;

    @JsonProperty("description")
    @Size(max = 1000, message = "Activity description must not exceed 1000 characters")
    private String description;

    @JsonProperty("activityType")
    @NotNull(message = "Activity type must not be null")
    private ActivityType activityType;

    @JsonProperty("performedBy")
    @Size(max = 100, message = "Performed by must not exceed 100 characters")
    private String performedBy;

    @JsonProperty("activityDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Activity date must not be null")
    private LocalDateTime activityDate;

    @JsonProperty("deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @JsonProperty("isCompleted")
    @Builder.Default
    private Boolean isCompleted = false;

    @JsonProperty("completionDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completionDate;

    @JsonProperty("hoursSpent")
    private Double hoursSpent;

    @JsonProperty("billableHours")
    private Double billableHours;

    @JsonProperty("cost")
    private Double cost;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("isMilestone")
    @Builder.Default
    private Boolean isMilestone = false;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}