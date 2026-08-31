package com.finflow.goals.domain;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Reine, deterministische Berechnung des Zielerreichungsdatums (siehe Business Rule
 * "Zielerreichungsdatum", Phase 2/4):
 *
 * <pre>
 * capital(0) = currentAmount
 * capital(n) = capital(n-1) * (1 + monthlyReturn) + monthlyContribution
 * gesucht: kleinstes n, für das capital(n) &gt;= targetAmount
 * </pre>
 *
 * Eine harte Obergrenze von 100 Jahren verhindert eine Endlosschleife, wenn monthlyContribution
 * &lt;= 0 und monthlyReturn &lt;= 0 das Ziel nie erreichen (siehe Edge Cases "0 € Sparrate",
 * "negative Rendite").
 */
final class GoalProjectionCalculator {

    private static final int MAX_MONTHS = 100 * 12;
    private static final MathContext MATH_CONTEXT = new MathContext(12);

    private GoalProjectionCalculator() {
    }

    static Optional<LocalDate> estimateAchievementDate(
            Money currentAmount, Money targetAmount, Money monthlyContribution,
            Percentage annualReturn, LocalDate today) {

        if (currentAmount.amount().compareTo(targetAmount.amount()) >= 0) {
            return Optional.of(today);
        }

        BigDecimal monthlyReturn = annualReturn.asFraction().divide(BigDecimal.valueOf(12), MATH_CONTEXT);
        BigDecimal growthFactor = BigDecimal.ONE.add(monthlyReturn);
        BigDecimal capital = currentAmount.amount();
        BigDecimal contribution = monthlyContribution.amount();
        BigDecimal target = targetAmount.amount();

        for (int month = 1; month <= MAX_MONTHS; month++) {
            capital = capital.multiply(growthFactor, MATH_CONTEXT).add(contribution);
            if (capital.compareTo(target) >= 0) {
                return Optional.of(today.plusMonths(month));
            }
        }
        return Optional.empty();
    }
}
