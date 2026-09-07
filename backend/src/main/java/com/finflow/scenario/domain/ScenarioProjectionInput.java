package com.finflow.scenario.domain;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;

/** Rein interne Eingabe für den Calculator, entkoppelt von der JPA-Entity (siehe Scenario.project()). */
record ScenarioProjectionInput(
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
