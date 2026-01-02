package com.carrilloabogados.legalcase.dto.response;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for activity metrics and statistics
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ActivityMetricsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("totalActivities")
    private Long totalActivities;

    @JsonProperty("completedActivities")
    private Long completedActivities;

    @JsonProperty("pendingActivities")
    private Long pendingActivities;

    @JsonProperty("totalHoursSpent")
    private Double totalHoursSpent;

    @JsonProperty("totalBillableHours")
    private Double totalBillableHours;

    @JsonProperty("totalCost")
    private Double totalCost;

    @JsonProperty("completionRate")
    public Double getCompletionRate() {
        if (totalActivities == null || totalActivities == 0) {
            return 0.0;
        }
        long completed = completedActivities != null ? completedActivities : 0;
        return (double) completed / totalActivities * 100;
    }

    @JsonProperty("billableRate")
    public Double getBillableRate() {
        if (totalHoursSpent == null || totalHoursSpent == 0) {
            return 0.0;
        }
        double billable = totalBillableHours != null ? totalBillableHours : 0.0;
        return billable / totalHoursSpent * 100;
    }
}
