package com.finflow.goals.application;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;

public record CreateFinancialGoalCommand(
        String name,
        Money targetAmount,
        Money currentAmount,
        LocalDate targetDate,
        Money monthlyContribution,
        Percentage expectedAnnualReturn) {
}
