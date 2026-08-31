package com.finflow.financialprofile.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FinancialProfileResponse(
        UUID id,
        UUID userId,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpenses,
        BigDecimal emergencyFund,
        BigDecimal totalDebt,
        Instant createdAt,
        Instant updatedAt) {
}
