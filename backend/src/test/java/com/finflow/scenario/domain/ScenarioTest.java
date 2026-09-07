package com.finflow.scenario.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ScenarioTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void createsScenarioWithValidValues() {
        Scenario scenario = Scenario.create(
                USER_ID, "Current Plan", Money.of("5000"), Money.of("500"),
                Percentage.ofFraction("0.04"), Percentage.ofFraction("0.02"), 20,
                null, null, null, null);

        assertThat(scenario.userId()).isEqualTo(USER_ID);
        assertThat(scenario.durationInYears()).isEqualTo(20);
    }

    @Test
    void rejectsNegativeCurrentCapital() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.of("-1"), Money.of("500"),
                Percentage.ZERO, Percentage.ZERO, 10, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currentCapital");
    }

    @Test
    void rejectsNegativeMonthlySavings() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.of("1000"), Money.of("-500"),
                Percentage.ZERO, Percentage.ZERO, 10, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monthlySavings");
    }

    @Test
    void rejectsDurationOutsideOneToHundredYears() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.ZERO, Money.ZERO, Percentage.ZERO, Percentage.ZERO, 0,
                null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.ZERO, Money.ZERO, Percentage.ZERO, Percentage.ZERO, 101,
                null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void allowsNegativeAnnualReturnAndInflation() {
        Scenario scenario = Scenario.create(
                USER_ID, "Pessimistisch", Money.of("1000"), Money.ZERO,
                Percentage.ofFraction("-0.05"), Percentage.ofFraction("-0.01"), 5,
                null, null, null, null);

        assertThat(scenario.annualReturn().asFraction()).isEqualByComparingTo("-0.05");
    }

    @Test
    void requiresMonthlyIncomeWhenIncomeGrowthIsGiven() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.ZERO, Money.of("500"), Percentage.ZERO, Percentage.ZERO, 5,
                null, Percentage.ofFraction("0.03"), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monthlyIncome");
    }

    @Test
    void rejectsMonthlySavingsGreaterThanMonthlyIncome() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.ZERO, Money.of("3000"), Percentage.ZERO, Percentage.ZERO, 5,
                Money.of("2000"), Percentage.ofFraction("0.03"), null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsZeroOrNegativeTargetCapitalWhenGiven() {
        assertThatThrownBy(() -> Scenario.create(
                USER_ID, "Ziel", Money.ZERO, Money.of("100"), Percentage.ZERO, Percentage.ZERO, 5,
                null, null, null, Money.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void projectDelegatesToCalculator() {
        Scenario scenario = Scenario.create(
                USER_ID, "Ziel", Money.of("1000"), Money.of("100"), Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, null);

        var result = scenario.project(LocalDate.now());

        assertThat(result.projectedCapital()).isEqualTo(Money.of("2200"));
    }
}
