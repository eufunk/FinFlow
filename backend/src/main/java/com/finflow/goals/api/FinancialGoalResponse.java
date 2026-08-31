package com.finflow.goals.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FinancialGoalResponse(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        BigDecimal monthlyContribution,
        BigDecimal expectedAnnualReturn,
        boolean achieved,
        LocalDate estimatedAchievementDate,
        Instant createdAt,
        Instant updatedAt) {
}
