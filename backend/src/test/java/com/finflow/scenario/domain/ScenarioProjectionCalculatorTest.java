package com.finflow.scenario.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ScenarioProjectionCalculatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 1);

    private static ScenarioProjectionInput basicInput(
            Money currentCapital, Money monthlySavings, Percentage annualReturn, Percentage inflation,
            int durationInYears) {
        return new ScenarioProjectionInput(
                currentCapital, monthlySavings, annualReturn, inflation, durationInYears,
                null, null, null, null);
    }

    // ---------- 0 % Rendite ----------

    @Test
    void zeroReturnGrowsPurelyThroughContributions() {
        var input = basicInput(Money.of("1000"), Money.of("100"), Percentage.ZERO, Percentage.ZERO, 1);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.projectedCapital()).isEqualTo(Money.of("2200"));
        assertThat(result.totalContributions()).isEqualTo(Money.of("1200"));
        assertThat(result.investmentGrowth()).isEqualTo(Money.ZERO);
    }

    // ---------- negative Rendite ----------

    @Test
    void negativeReturnShrinksCapitalWithoutFlooringAtZero() {
        var input = basicInput(Money.of("1000"), Money.ZERO, Percentage.ofFraction("-0.12"), Percentage.ZERO, 1);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.projectedCapital().amount()).isLessThan(Money.of("1000").amount());
        assertThat(result.projectedCapital().amount()).isPositive();
    }

    @Test
    void expensesOutpacingIncomeCanDriveCapitalBelowZero() {
        // Reine negative Rendite ohne Beiträge nähert sich der Null nur asymptotisch (capital *=
        // growthFactor > 0 behält immer das Vorzeichen). Echtes negatives Kapital entsteht erst
        // durch negative Beiträge - hier: Ausgaben wachsen deutlich schneller als das Einkommen.
        var input = new ScenarioProjectionInput(
                Money.ZERO, Money.of("100"), Percentage.ZERO, Percentage.ZERO, 5,
                Money.of("1000"), Percentage.ZERO, Percentage.ofFraction("1.00"), null);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.projectedCapital().amount()).isLessThan(java.math.BigDecimal.ZERO);
    }

    // ---------- 0 € Sparrate ----------

    @Test
    void zeroSavingsRateGrowsPurelyThroughReturns() {
        var input = basicInput(Money.of("1000"), Money.ZERO, Percentage.ofFraction("0.06"), Percentage.ZERO, 1);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.totalContributions()).isEqualTo(Money.ZERO);
        assertThat(result.projectedCapital().amount()).isGreaterThan(Money.of("1000").amount());
    }

    // ---------- sehr lange Laufzeiten ----------

    @Test
    void veryLongDurationProducesFullYearlyBreakdownWithoutHanging() {
        var input = basicInput(Money.of("1000"), Money.of("50"), Percentage.ofFraction("0.05"), Percentage.ofFraction("0.02"), 100);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.yearlyDevelopment()).hasSize(101); // Jahr 0 bis 100
        assertThat(result.projectedCapital().amount()).isPositive();
    }

    // ---------- Inflation ----------

    @Test
    void inflationReducesRealValueButNotNominalValue() {
        var input = basicInput(Money.of("1000"), Money.ZERO, Percentage.ZERO, Percentage.ofFraction("0.10"), 1);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.projectedCapital()).isEqualTo(Money.of("1000"));
        assertThat(result.inflationAdjustedValue()).isEqualTo(Money.of("909.09"));
    }

    @Test
    void extremeDeflationDoesNotCrashDivision() {
        // Inflation = -100 % ist eine entartete Eingabe (Divisor 0) - darf nicht crashen
        var input = basicInput(Money.of("1000"), Money.ZERO, Percentage.ZERO, Percentage.ofFraction("-1.00"), 1);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.inflationAdjustedValue()).isEqualTo(result.projectedCapital());
    }

    // ---------- Rundungsprobleme ----------

    @Test
    void nonTerminatingMonthlyRateDoesNotThrow() {
        // 7 % / 12 ergibt einen periodischen Dezimalbruch - darf keine ArithmeticException werfen
        var input = basicInput(Money.of("1000"), Money.of("77.77"), Percentage.ofFraction("0.07"), Percentage.ZERO, 30);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.projectedCapital().amount().scale()).isLessThanOrEqualTo(2);
    }

    // ---------- Einkommens-/Ausgabenwachstum ----------

    @Test
    void incomeAndExpensesGrowthChangeContributionYearByYear() {
        var input = new ScenarioProjectionInput(
                Money.ZERO, Money.of("500"), Percentage.ZERO, Percentage.ZERO, 2,
                Money.of("3000"), Percentage.ofFraction("0.05"), Percentage.ofFraction("0.02"), null);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        // Jahr 1: Beitrag bleibt 500 (Basiswerte) -> 6000; Jahr 2: Einkommen 3150, Ausgaben 2550 -> Beitrag 600 -> 7200
        assertThat(result.totalContributions()).isEqualTo(Money.of("13200"));
        assertThat(result.projectedCapital()).isEqualTo(Money.of("13200"));
    }

    // ---------- goalReached / goalReachedDate ----------

    @Test
    void goalReachedImmediatelyWhenAlreadyAboveTarget() {
        var input = new ScenarioProjectionInput(
                Money.of("2000"), Money.ZERO, Percentage.ZERO, Percentage.ZERO, 5,
                null, null, null, Money.of("1000"));

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.goalReached()).isTrue();
        assertThat(result.goalReachedDate()).isEqualTo(TODAY);
    }

    @Test
    void goalReachedAfterExactNumberOfMonths() {
        var input = new ScenarioProjectionInput(
                Money.ZERO, Money.of("100"), Percentage.ZERO, Percentage.ZERO, 5,
                null, null, null, Money.of("1200"));

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.goalReached()).isTrue();
        assertThat(result.goalReachedDate()).isEqualTo(TODAY.plusMonths(12));
    }

    @Test
    void goalNeverReachedWithinDuration() {
        var input = new ScenarioProjectionInput(
                Money.ZERO, Money.ZERO, Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, Money.of("1000000"));

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.goalReached()).isFalse();
        assertThat(result.goalReachedDate()).isNull();
    }

    @Test
    void noTargetCapitalMeansGoalReachedIsAlwaysFalse() {
        var input = basicInput(Money.of("100000"), Money.of("1000"), Percentage.ofFraction("0.05"), Percentage.ZERO, 10);

        var result = ScenarioProjectionCalculator.calculate(input, TODAY);

        assertThat(result.goalReached()).isFalse();
        assertThat(result.goalReachedDate()).isNull();
    }
}
