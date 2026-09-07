package com.finflow.scenario.application;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;

public record CreateScenarioCommand(
        String name,
        Money currentCapital,
        Money monthlySavings,
        Percentage annualReturn,
        Percentage inflation,
        int durationInYears,
        Money monthlyIncome,
        Percentage incomeGrowth,
        Percentage expensesGrowth,
        Money targetCapital) {
}
