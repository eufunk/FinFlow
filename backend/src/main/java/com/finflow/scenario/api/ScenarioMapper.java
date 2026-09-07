package com.finflow.scenario.api;

import com.finflow.scenario.application.CreateScenarioCommand;
import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioProjectionResult;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;

final class ScenarioMapper {

    private ScenarioMapper() {
    }

    static ScenarioResponse toResponse(Scenario scenario) {
        return new ScenarioResponse(
                scenario.id(),
                scenario.name(),
                scenario.currentCapital().amount(),
                scenario.monthlySavings().amount(),
                scenario.annualReturn().asFraction(),
                scenario.inflation().asFraction(),
                scenario.durationInYears(),
                scenario.monthlyIncome() != null ? scenario.monthlyIncome().amount() : null,
                scenario.incomeGrowth() != null ? scenario.incomeGrowth().asFraction() : null,
                scenario.expensesGrowth() != null ? scenario.expensesGrowth().asFraction() : null,
                scenario.targetCapital() != null ? scenario.targetCapital().amount() : null,
                scenario.createdAt());
    }

    static ScenarioResultResponse toResultResponse(Scenario scenario, ScenarioProjectionResult result) {
        var yearlyDevelopment = result.yearlyDevelopment().stream()
                .map(snapshot -> new YearlySnapshotResponse(snapshot.year(), snapshot.capital().amount()))
                .toList();

        return new ScenarioResultResponse(
                scenario.id(),
                scenario.name(),
                result.projectedCapital().amount(),
                yearlyDevelopment,
                result.totalContributions().amount(),
                result.investmentGrowth().amount(),
                result.inflationAdjustedValue().amount(),
                result.goalReached(),
                result.goalReachedDate());
    }

    static CreateScenarioCommand toCommand(CreateScenarioRequest request) {
        return new CreateScenarioCommand(
                request.name(),
                Money.of(request.currentCapital()),
                Money.of(request.monthlySavings()),
                Percentage.ofFraction(request.annualReturn()),
                Percentage.ofFraction(request.inflation()),
                request.durationInYears(),
                request.monthlyIncome() != null ? Money.of(request.monthlyIncome()) : null,
                request.incomeGrowth() != null ? Percentage.ofFraction(request.incomeGrowth()) : null,
                request.expensesGrowth() != null ? Percentage.ofFraction(request.expensesGrowth()) : null,
                request.targetCapital() != null ? Money.of(request.targetCapital()) : null);
    }
}
