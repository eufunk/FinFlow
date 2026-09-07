package com.finflow.scenario.domain;

import com.finflow.shared.Money;
import java.time.LocalDate;
import java.util.List;

public record ScenarioProjectionResult(
        Money projectedCapital,
        List<YearlySnapshot> yearlyDevelopment,
        Money totalContributions,
        Money investmentGrowth,
        Money inflationAdjustedValue,
        boolean goalReached,
        LocalDate goalReachedDate) {
}
