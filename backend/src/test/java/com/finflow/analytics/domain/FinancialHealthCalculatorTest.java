package com.finflow.analytics.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.shared.Money;
import org.junit.jupiter.api.Test;

class FinancialHealthCalculatorTest {

    // ---------- Emergency Fund ----------

    @Test
    void emergencyFundFullScoreAtSixMonths() {
        CategoryScore score = FinancialHealthCalculator.emergencyFundScore(Money.of("12000"), Money.of("2000"));

        assertThat(score.points()).isEqualByComparingTo("20");
    }

    @Test
    void emergencyFundHalfScoreAtThreeMonths() {
        CategoryScore score = FinancialHealthCalculator.emergencyFundScore(Money.of("6000"), Money.of("2000"));

        assertThat(score.points()).isEqualByComparingTo("10");
    }

    @Test
    void emergencyFundZeroWhenNoExpensesBasis() {
        CategoryScore score = FinancialHealthCalculator.emergencyFundScore(Money.of("1000"), Money.ZERO);

        assertThat(score.points()).isEqualByComparingTo("0");
        assertThat(score.explanation()).contains("Ausgabenbasis");
    }

    @Test
    void emergencyFundCapsAtMaxScoreBeyondTarget() {
        CategoryScore score = FinancialHealthCalculator.emergencyFundScore(Money.of("100000"), Money.of("2000"));

        assertThat(score.points()).isEqualByComparingTo("20");
    }

    // ---------- Savings Rate ----------

    @Test
    void savingsRateFullScoreAtTwentyPercent() {
        CategoryScore score = FinancialHealthCalculator.savingsRateScore(Money.of("3000"), Money.of("2400"));

        assertThat(score.points()).isEqualByComparingTo("20");
    }

    @Test
    void savingsRateHalfScoreAtTenPercent() {
        CategoryScore score = FinancialHealthCalculator.savingsRateScore(Money.of("3000"), Money.of("2700"));

        assertThat(score.points()).isEqualByComparingTo("10");
    }

    @Test
    void savingsRateClampsToZeroWhenExpensesExceedIncome() {
        CategoryScore score = FinancialHealthCalculator.savingsRateScore(Money.of("3000"), Money.of("3300"));

        assertThat(score.points()).isEqualByComparingTo("0");
    }

    @Test
    void savingsRateZeroWhenNoIncome() {
        CategoryScore score = FinancialHealthCalculator.savingsRateScore(Money.ZERO, Money.of("500"));

        assertThat(score.points()).isEqualByComparingTo("0");
        assertThat(score.explanation()).contains("Einkommen");
    }

    // ---------- Debt Ratio ----------

    @Test
    void debtRatioFullScoreAtZeroDebt() {
        CategoryScore score = FinancialHealthCalculator.debtRatioScore(Money.ZERO, Money.of("3000"));

        assertThat(score.points()).isEqualByComparingTo("20");
    }

    @Test
    void debtRatioZeroAtFortyPercentCeiling() {
        // annualIncome = 36000, debt = 14400 -> ratio = 40%
        CategoryScore score = FinancialHealthCalculator.debtRatioScore(Money.of("14400"), Money.of("3000"));

        assertThat(score.points()).isEqualByComparingTo("0");
    }

    @Test
    void debtRatioHalfScoreAtTwentyPercent() {
        CategoryScore score = FinancialHealthCalculator.debtRatioScore(Money.of("7200"), Money.of("3000"));

        assertThat(score.points()).isEqualByComparingTo("10");
    }

    @Test
    void debtRatioZeroWhenNoIncome() {
        CategoryScore score = FinancialHealthCalculator.debtRatioScore(Money.of("1000"), Money.ZERO);

        assertThat(score.points()).isEqualByComparingTo("0");
        assertThat(score.explanation()).contains("Einkommen");
    }

    @Test
    void debtRatioClampsAtZeroBeyondCeiling() {
        CategoryScore score = FinancialHealthCalculator.debtRatioScore(Money.of("100000"), Money.of("3000"));

        assertThat(score.points()).isEqualByComparingTo("0");
    }

    // ---------- Insurance Coverage ----------

    @Test
    void insuranceCoverageZeroWhenNoRiskCovered() {
        assertThat(FinancialHealthCalculator.insuranceCoverageScore(0).points()).isEqualByComparingTo("0");
    }

    @Test
    void insuranceCoverageFullWhenAllThreeCovered() {
        assertThat(FinancialHealthCalculator.insuranceCoverageScore(3).points()).isEqualByComparingTo("20");
    }

    @Test
    void insuranceCoveragePartialForOneOfThree() {
        assertThat(FinancialHealthCalculator.insuranceCoverageScore(1).points()).isEqualByComparingTo("7");
    }

    @Test
    void insuranceCoverageIgnoresCountsBeyondCoreRiskTotal() {
        assertThat(FinancialHealthCalculator.insuranceCoverageScore(5).points()).isEqualByComparingTo("20");
    }

    // ---------- Investment/Diversification ----------

    @Test
    void investmentDiversificationFullScore() {
        CategoryScore score = FinancialHealthCalculator.investmentDiversificationScore(
                Money.of("3000"), Money.of("10000"), 4);

        assertThat(score.points()).isEqualByComparingTo("20");
    }

    @Test
    void investmentDiversificationZeroWhenNoAssets() {
        CategoryScore score = FinancialHealthCalculator.investmentDiversificationScore(Money.ZERO, Money.ZERO, 0);

        assertThat(score.points()).isEqualByComparingTo("0");
    }

    @Test
    void investmentDiversificationCombinesBothHalves() {
        // investedShare = 10% von Ziel 30% -> 10 * (0.1/0.3) = 3.33; 2 von 4 Kontoarten -> 5; Summe gerundet 8
        CategoryScore score = FinancialHealthCalculator.investmentDiversificationScore(
                Money.of("1000"), Money.of("10000"), 2);

        assertThat(score.points()).isEqualByComparingTo("8");
    }

    // ---------- Risk Level ----------

    @Test
    void riskLevelThresholds() {
        assertThat(FinancialHealthCalculator.riskLevelFor(100)).isEqualTo(RiskLevel.LOW);
        assertThat(FinancialHealthCalculator.riskLevelFor(75)).isEqualTo(RiskLevel.LOW);
        assertThat(FinancialHealthCalculator.riskLevelFor(74)).isEqualTo(RiskLevel.MODERATE);
        assertThat(FinancialHealthCalculator.riskLevelFor(50)).isEqualTo(RiskLevel.MODERATE);
        assertThat(FinancialHealthCalculator.riskLevelFor(49)).isEqualTo(RiskLevel.ELEVATED);
        assertThat(FinancialHealthCalculator.riskLevelFor(25)).isEqualTo(RiskLevel.ELEVATED);
        assertThat(FinancialHealthCalculator.riskLevelFor(24)).isEqualTo(RiskLevel.HIGH);
        assertThat(FinancialHealthCalculator.riskLevelFor(0)).isEqualTo(RiskLevel.HIGH);
    }

    // ---------- Full calculate() wiring ----------

    @Test
    void calculateSumsAllFiveCategoriesIntoTotalScore() {
        FinancialHealthInput input = new FinancialHealthInput(
                Money.of("3000"), Money.of("2400"), Money.of("14400"), Money.ZERO,
                3, Money.of("3000"), Money.of("10000"), 4);

        FinancialHealthScore result = FinancialHealthCalculator.calculate(input);

        assertThat(result.totalScore()).isEqualTo(100);
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(result.recommendations()).isEmpty();
    }

    @Test
    void calculateProducesRecommendationsOnlyForWeakCategories() {
        FinancialHealthInput input = new FinancialHealthInput(
                Money.of("3000"), Money.of("2900"), Money.ZERO, Money.of("30000"),
                0, Money.ZERO, Money.ZERO, 0);

        FinancialHealthScore result = FinancialHealthCalculator.calculate(input);

        assertThat(result.totalScore()).isLessThan(50);
        assertThat(result.recommendations()).isNotEmpty();
        assertThat(result.recommendations()).allMatch(text -> !text.isBlank());
    }

    @Test
    void calculateHandlesCompletelyEmptyProfileWithoutCrashing() {
        FinancialHealthInput input = new FinancialHealthInput(
                Money.ZERO, Money.ZERO, Money.ZERO, Money.ZERO, 0, Money.ZERO, Money.ZERO, 0);

        FinancialHealthScore result = FinancialHealthCalculator.calculate(input);

        assertThat(result.totalScore()).isEqualTo(0);
        assertThat(result.riskLevel()).isEqualTo(RiskLevel.HIGH);
    }
}
