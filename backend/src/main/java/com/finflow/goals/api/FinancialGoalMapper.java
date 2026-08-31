package com.finflow.goals.api;

import com.finflow.goals.application.CreateFinancialGoalCommand;
import com.finflow.goals.domain.FinancialGoal;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;

final class FinancialGoalMapper {

    private FinancialGoalMapper() {
    }

    static FinancialGoalResponse toResponse(FinancialGoal goal) {
        // LocalDate.now() bewusst hier statt clock-injiziert: die eigentliche Berechnung
        // (GoalProjectionCalculator) ist bereits mit explizitem "today"-Parameter unit-getestet,
        // hier geht es nur um den aktuellen Kalendertag zum Zeitpunkt des Requests.
        LocalDate today = LocalDate.now();
        var estimatedAchievementDate = goal.estimateAchievementDate(today).orElse(null);

        return new FinancialGoalResponse(
                goal.id(),
                goal.name(),
                goal.targetAmount().amount(),
                goal.currentAmount().amount(),
                goal.targetDate(),
                goal.monthlyContribution().amount(),
                goal.expectedAnnualReturn() != null ? goal.expectedAnnualReturn().asFraction() : null,
                goal.isAchieved(),
                estimatedAchievementDate,
                goal.createdAt(),
                goal.updatedAt());
    }

    static CreateFinancialGoalCommand toCommand(CreateFinancialGoalRequest request) {
        return new CreateFinancialGoalCommand(
                request.name(),
                Money.of(request.targetAmount()),
                Money.of(request.currentAmount()),
                request.targetDate(),
                Money.of(request.monthlyContribution()),
                request.expectedAnnualReturn() != null ? Percentage.ofFraction(request.expectedAnnualReturn()) : null);
    }
}
