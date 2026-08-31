package com.finflow.goals.api;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFinancialGoalRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Positive BigDecimal targetAmount,
        @NotNull @PositiveOrZero BigDecimal currentAmount,
        @FutureOrPresent LocalDate targetDate,
        @NotNull @PositiveOrZero BigDecimal monthlyContribution,
        BigDecimal expectedAnnualReturn) {
}
