package com.finflow.scenario.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ScenarioResultResponse(
        UUID scenarioId,
        String scenarioName,
        BigDecimal projectedCapital,
        List<YearlySnapshotResponse> yearlyDevelopment,
        BigDecimal totalContributions,
        BigDecimal investmentGrowth,
        BigDecimal inflationAdjustedValue,
        boolean goalReached,
        LocalDate goalReachedDate) {
}
