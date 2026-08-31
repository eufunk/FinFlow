package com.finflow.goals.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GoalProjectionCalculatorTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 1, 1);

    @Test
    void alreadyAchievedReturnsToday() {
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("10000"), Money.of("5000"), Money.of("100"), Percentage.ofFraction("0.04"), TODAY);

        assertThat(result).contains(TODAY);
    }

    @Test
    void exactMatchIsAlsoAchieved() {
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("5000"), Money.of("5000"), Money.ZERO, Percentage.ZERO, TODAY);

        assertThat(result).contains(TODAY);
    }

    @Test
    void zeroReturnStillReachesGoalViaContributions() {
        // 1000 + 12 * 500 = 7000 >= 6000 nach 10 Monaten (bei 0% Rendite rein additiv)
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("1000"), Money.of("6000"), Money.of("500"), Percentage.ZERO, TODAY);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(TODAY.plusMonths(10));
    }

    @Test
    void negativeReturnDelaysButCanStillReachGoal() {
        Optional<LocalDate> zeroReturn = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("1000"), Money.of("6000"), Money.of("500"), Percentage.ZERO, TODAY);
        Optional<LocalDate> negativeReturn = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("1000"), Money.of("6000"), Money.of("500"), Percentage.ofFraction("-0.05"), TODAY);

        assertThat(negativeReturn).isPresent();
        assertThat(negativeReturn.get()).isAfterOrEqualTo(zeroReturn.orElseThrow());
    }

    @Test
    void zeroContributionAndZeroReturnNeverReachesHigherTarget() {
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("1000"), Money.of("6000"), Money.ZERO, Percentage.ZERO, TODAY);

        assertThat(result).isEmpty();
    }

    @Test
    void zeroContributionAndNegativeReturnNeverReachesGoal() {
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("1000"), Money.of("6000"), Money.ZERO, Percentage.ofFraction("-0.01"), TODAY);

        assertThat(result).isEmpty();
    }

    @Test
    void positiveReturnCanReachGoalFasterThanZeroReturn() {
        Optional<LocalDate> zeroReturn = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("50000"), Money.of("100000"), Money.of("100"), Percentage.ZERO, TODAY);
        Optional<LocalDate> positiveReturn = GoalProjectionCalculator.estimateAchievementDate(
                Money.of("50000"), Money.of("100000"), Money.of("100"), Percentage.ofFraction("0.06"), TODAY);

        assertThat(positiveReturn).isPresent();
        assertThat(zeroReturn).isPresent();
        assertThat(positiveReturn.get()).isBefore(zeroReturn.get());
    }

    @Test
    void veryLongDurationsAreBoundedInsteadOfHanging() {
        Optional<LocalDate> result = GoalProjectionCalculator.estimateAchievementDate(
                Money.ZERO, Money.of("1000000000"), Money.of("1"), Percentage.ZERO, TODAY);

        assertThat(result).isEmpty();
    }
}
