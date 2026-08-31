package com.finflow.analytics.domain;

import com.finflow.shared.Money;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Reine, deterministische Berechnung des Financial Health Score (siehe Business Rules aus
 * Phase 2/4 und Prompt 6). Kein Spring, keine I/O, keine Zufallswerte - jede Empfehlung ist die
 * Erklärung einer konkreten Kategorie, keine Blackbox (siehe Prompt 6 "Wichtig").
 *
 * <p>Fünf Kategorien à max. 20 Punkte = 0-100 gesamt:
 * <ol>
 *   <li>Emergency Fund: volle Punktzahl ab 6 Monatsausgaben Rücklage.</li>
 *   <li>Savings Rate: volle Punktzahl ab 20 % Sparquote.</li>
 *   <li>Debt Ratio: volle Punktzahl bei 0 %, 0 Punkte ab 40 % Schuldenquote (invertiert).</li>
 *   <li>Insurance Coverage: volle Punktzahl bei Abdeckung aller 3 Kernrisiken (Haftpflicht,
 *       Berufsunfähigkeit, Hausrat).</li>
 *   <li>Investment/Diversification: je 10 Punkte für "Anteil investiertes Vermögen am
 *       Gesamtvermögen" (Ziel 30 %) und "Anzahl unterschiedlicher Kontoarten" (Ziel 4).</li>
 * </ol>
 */
public final class FinancialHealthCalculator {

    private static final MathContext MC = new MathContext(10);
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MAX_CATEGORY_SCORE = BigDecimal.valueOf(20);
    private static final BigDecimal HALF_CATEGORY_SCORE = BigDecimal.valueOf(10);

    private static final int EMERGENCY_FUND_TARGET_MONTHS = 6;
    private static final BigDecimal SAVINGS_RATE_TARGET = new BigDecimal("0.20");
    private static final BigDecimal DEBT_RATIO_CEILING = new BigDecimal("0.40");
    private static final int CORE_INSURANCE_RISK_COUNT = 3;
    private static final BigDecimal INVESTED_SHARE_TARGET = new BigDecimal("0.30");
    private static final int ASSET_CLASS_COUNT_FOR_FULL_SCORE = 4;

    private FinancialHealthCalculator() {
    }

    public static FinancialHealthScore calculate(FinancialHealthInput input) {
        CategoryScore emergencyFund = emergencyFundScore(input.emergencyFund(), input.monthlyExpenses());
        CategoryScore savingsRate = savingsRateScore(input.monthlyIncome(), input.monthlyExpenses());
        CategoryScore debtRatio = debtRatioScore(input.totalDebt(), input.monthlyIncome());
        CategoryScore insuranceCoverage = insuranceCoverageScore(input.coveredCoreInsuranceRiskCount());
        CategoryScore investmentDiversification = investmentDiversificationScore(
                input.investedAmount(), input.totalAssets(), input.distinctAssetClassCount());

        int totalScore = List.of(emergencyFund, savingsRate, debtRatio, insuranceCoverage, investmentDiversification)
                .stream()
                .map(CategoryScore::points)
                .reduce(ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        RiskLevel riskLevel = riskLevelFor(totalScore);
        List<String> recommendations = collectRecommendations(
                emergencyFund, savingsRate, debtRatio, insuranceCoverage, investmentDiversification);

        return new FinancialHealthScore(
                totalScore, emergencyFund, savingsRate, debtRatio, insuranceCoverage,
                investmentDiversification, recommendations, riskLevel);
    }

    static CategoryScore emergencyFundScore(Money emergencyFund, Money monthlyExpenses) {
        if (!monthlyExpenses.isPositive()) {
            return category(ZERO, "Keine Ausgabenbasis hinterlegt – Notgroschen kann nicht bewertet werden.");
        }
        BigDecimal monthsCovered = emergencyFund.amount().divide(monthlyExpenses.amount(), MC);
        BigDecimal ratio = monthsCovered.divide(BigDecimal.valueOf(EMERGENCY_FUND_TARGET_MONTHS), MC);
        BigDecimal points = clamp(ratio.multiply(MAX_CATEGORY_SCORE), ZERO, MAX_CATEGORY_SCORE);
        String explanation = "Dein Notgroschen deckt aktuell %s Monatsausgaben ab (Ziel: %d Monate)."
                .formatted(format(monthsCovered), EMERGENCY_FUND_TARGET_MONTHS);
        return category(points, explanation);
    }

    static CategoryScore savingsRateScore(Money monthlyIncome, Money monthlyExpenses) {
        if (!monthlyIncome.isPositive()) {
            return category(ZERO, "Kein Einkommen hinterlegt – Sparquote kann nicht bewertet werden.");
        }
        BigDecimal savingsRate = monthlyIncome.amount().subtract(monthlyExpenses.amount())
                .divide(monthlyIncome.amount(), MC);
        BigDecimal points = clamp(
                savingsRate.divide(SAVINGS_RATE_TARGET, MC).multiply(MAX_CATEGORY_SCORE), ZERO, MAX_CATEGORY_SCORE);
        String explanation = "Deine Sparquote liegt bei %s%% (Ziel: mindestens %s%%)."
                .formatted(formatPercent(savingsRate), formatPercent(SAVINGS_RATE_TARGET));
        return category(points, explanation);
    }

    static CategoryScore debtRatioScore(Money totalDebt, Money monthlyIncome) {
        BigDecimal annualIncome = monthlyIncome.amount().multiply(BigDecimal.valueOf(12));
        if (annualIncome.signum() <= 0) {
            return category(ZERO, "Kein Einkommen hinterlegt – Schuldenquote kann nicht bewertet werden.");
        }
        BigDecimal debtRatio = totalDebt.amount().divide(annualIncome, MC);
        BigDecimal points = clamp(
                MAX_CATEGORY_SCORE.subtract(debtRatio.divide(DEBT_RATIO_CEILING, MC).multiply(MAX_CATEGORY_SCORE)),
                ZERO, MAX_CATEGORY_SCORE);
        String explanation = "Deine Schuldenquote liegt bei %s%% des Jahreseinkommens (Zielobergrenze: %s%%)."
                .formatted(formatPercent(debtRatio), formatPercent(DEBT_RATIO_CEILING));
        return category(points, explanation);
    }

    static CategoryScore insuranceCoverageScore(int coveredCoreInsuranceRiskCount) {
        int covered = Math.max(0, Math.min(coveredCoreInsuranceRiskCount, CORE_INSURANCE_RISK_COUNT));
        BigDecimal points = MAX_CATEGORY_SCORE.multiply(BigDecimal.valueOf(covered))
                .divide(BigDecimal.valueOf(CORE_INSURANCE_RISK_COUNT), MC);
        String explanation = "%d von %d Kernrisiken (Haftpflicht, Berufsunfähigkeit, Hausrat) sind abgesichert."
                .formatted(covered, CORE_INSURANCE_RISK_COUNT);
        return category(points, explanation);
    }

    static CategoryScore investmentDiversificationScore(
            Money investedAmount, Money totalAssets, int distinctAssetClassCount) {
        BigDecimal investedShareScore;
        BigDecimal investedShare;
        if (totalAssets.isPositive()) {
            investedShare = investedAmount.amount().divide(totalAssets.amount(), MC);
            investedShareScore = clamp(
                    investedShare.divide(INVESTED_SHARE_TARGET, MC).multiply(HALF_CATEGORY_SCORE),
                    ZERO, HALF_CATEGORY_SCORE);
        } else {
            investedShare = ZERO;
            investedShareScore = ZERO;
        }

        int classCount = Math.max(0, distinctAssetClassCount);
        BigDecimal diversificationScore = clamp(
                HALF_CATEGORY_SCORE.multiply(BigDecimal.valueOf(classCount))
                        .divide(BigDecimal.valueOf(ASSET_CLASS_COUNT_FOR_FULL_SCORE), MC),
                ZERO, HALF_CATEGORY_SCORE);

        BigDecimal points = investedShareScore.add(diversificationScore);
        String explanation = "%s%% deines Vermögens ist investiert, verteilt auf %d Kontoarten."
                .formatted(formatPercent(investedShare), classCount);
        return category(points, explanation);
    }

    static RiskLevel riskLevelFor(int totalScore) {
        if (totalScore >= 75) {
            return RiskLevel.LOW;
        }
        if (totalScore >= 50) {
            return RiskLevel.MODERATE;
        }
        if (totalScore >= 25) {
            return RiskLevel.ELEVATED;
        }
        return RiskLevel.HIGH;
    }

    private static List<String> collectRecommendations(CategoryScore... scores) {
        List<String> recommendations = new ArrayList<>();
        for (CategoryScore score : scores) {
            if (score.isWeak()) {
                recommendations.add(score.explanation());
            }
        }
        return List.copyOf(recommendations);
    }

    private static CategoryScore category(BigDecimal points, String explanation) {
        return new CategoryScore(points.setScale(0, RoundingMode.HALF_UP), MAX_CATEGORY_SCORE, explanation);
    }

    private static BigDecimal clamp(BigDecimal value, BigDecimal min, BigDecimal max) {
        if (value.compareTo(min) < 0) {
            return min;
        }
        if (value.compareTo(max) > 0) {
            return max;
        }
        return value;
    }

    private static String format(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP).toPlainString();
    }

    private static String formatPercent(BigDecimal fraction) {
        return fraction.multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP).toPlainString();
    }
}
