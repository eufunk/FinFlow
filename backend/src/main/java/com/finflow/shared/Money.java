package com.finflow.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable monetary amount. MVP ist EUR-only, daher wird keine Currency-Spalte
 * persistiert - sobald Multi-Currency gebraucht wird, kann sie ergänzt werden.
 * Enthält bewusst keine "nicht negativ"-Regel: das ist eine Invariante einzelner
 * Felder (z. B. FinancialProfile.monthlyIncome), nicht des Geldbetrags an sich.
 */
@Embeddable
public final class Money {

    public static final Money ZERO = Money.of(BigDecimal.ZERO);

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    protected Money() {
        // for JPA
    }

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        return new Money(amount);
    }

    public static Money of(String amount) {
        return of(new BigDecimal(amount));
    }

    public static Money of(long amount) {
        return of(BigDecimal.valueOf(amount));
    }

    public BigDecimal amount() {
        return amount;
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money other)) {
            return false;
        }
        return amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
