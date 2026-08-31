package com.finflow.analytics.api;

import com.finflow.analytics.domain.CategoryScore;
import com.finflow.analytics.domain.FinancialHealthScore;

final class FinancialHealthMapper {

    private FinancialHealthMapper() {
    }

    static FinancialHealthScoreResponse toResponse(FinancialHealthScore score) {
        return new FinancialHealthScoreResponse(
                score.totalScore(),
                toResponse(score.emergencyFund()),
                toResponse(score.savingsRate()),
                toResponse(score.debtRatio()),
                toResponse(score.insuranceCoverage()),
                toResponse(score.investmentDiversification()),
                score.recommendations(),
                score.riskLevel());
    }

    private static CategoryScoreResponse toResponse(CategoryScore categoryScore) {
        return new CategoryScoreResponse(categoryScore.points(), categoryScore.maxPoints(), categoryScore.explanation());
    }
}
