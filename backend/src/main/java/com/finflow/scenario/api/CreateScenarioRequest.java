package com.finflow.scenario.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateScenarioRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @PositiveOrZero BigDecimal currentCapital,
        @NotNull @PositiveOrZero BigDecimal monthlySavings,
        @NotNull BigDecimal annualReturn,
        @NotNull BigDecimal inflation,
        @NotNull @Min(1) @Max(100) Integer durationInYears,
        BigDecimal monthlyIncome,
        BigDecimal incomeGrowth,
        BigDecimal expensesGrowth,
        BigDecimal targetCapital) {
}
