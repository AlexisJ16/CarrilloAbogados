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
 * Response DTO for case metrics and statistics
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CaseMetricsResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("pendingCount")
    private Long pendingCount;

    @JsonProperty("inProgressCount")
    private Long inProgressCount;

    @JsonProperty("waitingCount")
    private Long waitingCount;

    @JsonProperty("underReviewCount")
    private Long underReviewCount;

    @JsonProperty("suspendedCount")
    private Long suspendedCount;

    @JsonProperty("completedCount")
    private Long completedCount;

    @JsonProperty("closedCount")
    private Long closedCount;

    @JsonProperty("cancelledCount")
    private Long cancelledCount;

    @JsonProperty("totalActiveAmount")
    private Double totalActiveAmount;

    @JsonProperty("totalCompletedAmount")
    private Double totalCompletedAmount;

    @JsonProperty("totalCases")
    public Long getTotalCases() {
        return safeSum(pendingCount, inProgressCount, waitingCount, underReviewCount,
                suspendedCount, completedCount, closedCount, cancelledCount);
    }

    @JsonProperty("totalActiveCases")
    public Long getTotalActiveCases() {
        return safeSum(pendingCount, inProgressCount, waitingCount, underReviewCount, suspendedCount);
    }

    private Long safeSum(Long... values) {
        long sum = 0;
        for (Long value : values) {
            if (value != null) {
                sum += value;
            }
        }
        return sum;
    }
}
