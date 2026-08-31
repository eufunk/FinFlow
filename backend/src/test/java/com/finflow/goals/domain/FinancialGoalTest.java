package com.finflow.goals.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FinancialGoalTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void createsGoalWithValidValues() {
        FinancialGoal goal = FinancialGoal.create(
                USER_ID, "Notgroschen", Money.of("6000"), Money.of("1000"),
                LocalDate.now().plusYears(1), Money.of("200"), Percentage.ofFraction("0.02"));

        assertThat(goal.userId()).isEqualTo(USER_ID);
        assertThat(goal.name()).isEqualTo("Notgroschen");
        assertThat(goal.isAchieved()).isFalse();
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> FinancialGoal.create(
                USER_ID, "  ", Money.of("6000"), Money.of("1000"), null, Money.of("200"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void rejectsZeroTargetAmount() {
        assertThatThrownBy(() -> FinancialGoal.create(
                USER_ID, "Ziel", Money.ZERO, Money.ZERO, null, Money.of("200"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetAmount");
    }

    @Test
    void rejectsNegativeCurrentAmount() {
        assertThatThrownBy(() -> FinancialGoal.create(
                USER_ID, "Ziel", Money.of("6000"), Money.of("-1"), null, Money.of("200"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currentAmount");
    }

    @Test
    void rejectsTargetDateInThePast() {
        assertThatThrownBy(() -> FinancialGoal.create(
                USER_ID, "Ziel", Money.of("6000"), Money.of("0"),
                LocalDate.now().minusDays(1), Money.of("200"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("targetDate");
    }

    @Test
    void isAchievedWhenCurrentAmountReachesTarget() {
        FinancialGoal goal = FinancialGoal.create(
                USER_ID, "Ziel", Money.of("5000"), Money.of("5000"), null, Money.ZERO, null);

        assertThat(goal.isAchieved()).isTrue();
        assertThat(goal.estimateAchievementDate(LocalDate.now())).contains(LocalDate.now());
    }

    @Test
    void estimateAchievementDateDefaultsToZeroReturnWhenNotSpecified() {
        FinancialGoal goal = FinancialGoal.create(
                USER_ID, "Ziel", Money.of("2000"), Money.of("1000"), null, Money.of("100"), null);

        var result = goal.estimateAchievementDate(LocalDate.now());

        assertThat(result).isPresent();
    }
}
