package com.finflow.analytics.domain;

import java.math.BigDecimal;

/** Bewertung einer einzelnen Financial-Health-Kategorie inkl. nachvollziehbarer Erklärung. */
public record CategoryScore(BigDecimal points, BigDecimal maxPoints, String explanation) {

    public boolean isWeak() {
        // unter 75% der maximal möglichen Punktzahl gilt als "verbesserungswürdig" -> Empfehlung
        return points.compareTo(maxPoints.multiply(new BigDecimal("0.75"))) < 0;
    }
}
