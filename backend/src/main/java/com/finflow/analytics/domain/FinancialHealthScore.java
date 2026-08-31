package com.finflow.analytics.domain;

import java.util.List;

public record FinancialHealthScore(
        int totalScore,
        CategoryScore emergencyFund,
        CategoryScore savingsRate,
        CategoryScore debtRatio,
        CategoryScore insuranceCoverage,
        CategoryScore investmentDiversification,
        List<String> recommendations,
        RiskLevel riskLevel) {
}
