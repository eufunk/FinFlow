package com.finflow.scenario.domain;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reine, deterministische Zukunftssimulation für die Scenario Engine (siehe Prompt 7).
 *
 * <p><b>Mathematische Annahmen (bewusst dokumentiert, siehe Prompt 7 "Erkläre die
 * mathematischen Annahmen"):</b>
 * <ul>
 *   <li>Die jährliche Rendite wird linear auf Monate umgelegt (monthlyReturn = annualReturn / 12),
 *       nicht geometrisch ((1+r)^(1/12)-1) - konsistent mit GoalProjectionCalculator und für die
 *       hier relevanten Renditegrößen eine für Endnutzer nachvollziehbare Näherung.</li>
 *   <li>Ohne incomeGrowth/expensesGrowth bleibt monthlySavings über die gesamte Laufzeit exakt
 *       konstant.</li>
 *   <li>Mit incomeGrowth/expensesGrowth wird die anfängliche Ausgabenbasis als
 *       monthlyIncome - monthlySavings hergeleitet; Einkommen und Ausgaben wachsen jährlich
 *       (nicht monatlich) mit ihrer jeweiligen Rate, der monatliche Beitrag eines Jahres ist die
 *       Differenz aus beiden.</li>
 *   <li>Es gibt keine Deckelung bei 0: negative Rendite oder ein negativer Beitrag (Ausgaben
 *       wachsen schneller als das Einkommen) dürfen das Kapital sinken lassen, auch unter 0
 *       (siehe Edge Case "negative Rendite im Szenario").</li>
 * </ul>
 */
final class ScenarioProjectionCalculator {

    private static final MathContext MC = new MathContext(15);

    private ScenarioProjectionCalculator() {
    }

    static ScenarioProjectionResult calculate(ScenarioProjectionInput input, LocalDate today) {
        BigDecimal monthlyReturn = input.annualReturn().asFraction().divide(BigDecimal.valueOf(12), MC);
        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyReturn);

        BigDecimal capital = input.currentCapital().amount();
        BigDecimal totalContributions = BigDecimal.ZERO;
        boolean goalReached = false;
        LocalDate goalReachedDate = null;

        BigDecimal initialExpenses = null;
        if (usesIncomeProjection(input)) {
            initialExpenses = input.monthlyIncome().amount().subtract(input.monthlySavings().amount());
        }

        List<YearlySnapshot> yearlyDevelopment = new ArrayList<>();
        yearlyDevelopment.add(new YearlySnapshot(0, Money.of(capital)));

        BigDecimal targetCapital = input.targetCapital() != null ? input.targetCapital().amount() : null;
        if (targetCapital != null && capital.compareTo(targetCapital) >= 0) {
            goalReached = true;
            goalReachedDate = today;
        }

        for (int year = 1; year <= input.durationInYears(); year++) {
            BigDecimal monthlyContribution = monthlyContributionForYear(input, year, initialExpenses);

            for (int month = 1; month <= 12; month++) {
                capital = capital.multiply(growthFactor, MC).add(monthlyContribution);
                totalContributions = totalContributions.add(monthlyContribution);

                if (targetCapital != null && !goalReached && capital.compareTo(targetCapital) >= 0) {
                    goalReached = true;
                    int monthsElapsed = (year - 1) * 12 + month;
                    goalReachedDate = today.plusMonths(monthsElapsed);
                }
            }

            yearlyDevelopment.add(new YearlySnapshot(year, Money.of(capital)));
        }

        Money projectedCapital = Money.of(capital);
        Money investmentGrowth = Money.of(
                capital.subtract(input.currentCapital().amount()).subtract(totalContributions));
        Money inflationAdjustedValue = inflationAdjust(projectedCapital, input.inflation(), input.durationInYears());

        return new ScenarioProjectionResult(
                projectedCapital, List.copyOf(yearlyDevelopment), Money.of(totalContributions),
                investmentGrowth, inflationAdjustedValue, goalReached, goalReachedDate);
    }

    private static boolean usesIncomeProjection(ScenarioProjectionInput input) {
        return input.incomeGrowth() != null || input.expensesGrowth() != null;
    }

    private static BigDecimal monthlyContributionForYear(
            ScenarioProjectionInput input, int year, BigDecimal initialExpenses) {
        if (!usesIncomeProjection(input)) {
            return input.monthlySavings().amount();
        }
        int yearsElapsed = year - 1;
        Percentage incomeGrowth = input.incomeGrowth() != null ? input.incomeGrowth() : Percentage.ZERO;
        Percentage expensesGrowth = input.expensesGrowth() != null ? input.expensesGrowth() : Percentage.ZERO;

        BigDecimal incomeFactor = BigDecimal.ONE.add(incomeGrowth.asFraction()).pow(yearsElapsed, MC);
        BigDecimal expensesFactor = BigDecimal.ONE.add(expensesGrowth.asFraction()).pow(yearsElapsed, MC);

        BigDecimal incomeAtYear = input.monthlyIncome().amount().multiply(incomeFactor, MC);
        BigDecimal expensesAtYear = initialExpenses.multiply(expensesFactor, MC);

        return incomeAtYear.subtract(expensesAtYear);
    }

    private static Money inflationAdjust(Money nominalValue, Percentage inflation, int durationInYears) {
        BigDecimal divisor = BigDecimal.ONE.add(inflation.asFraction()).pow(durationInYears, MC);
        if (divisor.signum() == 0) {
            // entartete Eingabe (Inflation = -100 %): reale Kaufkraft ist nicht sinnvoll definierbar
            return nominalValue;
        }
        return Money.of(nominalValue.amount().divide(divisor, MC));
    }
}
