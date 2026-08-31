package com.finflow.financialprofile.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UpsertFinancialProfileRequest(
        @NotNull @PositiveOrZero BigDecimal monthlyIncome,
        @NotNull @PositiveOrZero BigDecimal monthlyExpenses,
        @NotNull @PositiveOrZero BigDecimal emergencyFund,
        @NotNull @PositiveOrZero BigDecimal totalDebt) {
}
