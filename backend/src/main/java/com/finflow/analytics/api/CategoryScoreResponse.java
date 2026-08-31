package com.finflow.analytics.api;

import java.math.BigDecimal;

public record CategoryScoreResponse(BigDecimal points, BigDecimal maxPoints, String explanation) {
}
