package com.finflow.analytics.api;

import com.finflow.analytics.domain.RiskLevel;
import java.util.List;

public record FinancialHealthScoreResponse(
        int totalScore,
        CategoryScoreResponse emergencyFund,
        CategoryScoreResponse savingsRate,
        CategoryScoreResponse debtRatio,
        CategoryScoreResponse insuranceCoverage,
        CategoryScoreResponse investmentDiversification,
        List<String> recommendations,
        RiskLevel riskLevel) {
}
