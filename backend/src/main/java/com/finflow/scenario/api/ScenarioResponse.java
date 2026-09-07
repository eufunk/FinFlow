package com.finflow.scenario.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ScenarioResponse(
        UUID id,
        String name,
        BigDecimal currentCapital,
        BigDecimal monthlySavings,
        BigDecimal annualReturn,
        BigDecimal inflation,
        int durationInYears,
        BigDecimal monthlyIncome,
        BigDecimal incomeGrowth,
        BigDecimal expensesGrowth,
        BigDecimal targetCapital,
        Instant createdAt) {
}
