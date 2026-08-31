package com.finflow.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable Prozentwert, intern als Bruchteil gehalten (0,0425 = 4,25 %) statt als 0-100-Zahl,
 * damit Business Rules nicht wiederholt durch 100 teilen müssen (siehe Phase 4 Value Objects).
 * Bewusst ohne Wertebereichsprüfung: erlaubte Grenzen (z. B. negative Rendite ist erlaubt, ein
 * Debt Ratio über 100 % ist erlaubt) entscheidet die jeweilige Business Rule, nicht das VO selbst.
 */
@Embeddable
public final class Percentage {

    public static final Percentage ZERO = Percentage.ofFraction(BigDecimal.ZERO);

    @Column(name = "value", precision = 6, scale = 4)
    private BigDecimal value;

    protected Percentage() {
        // for JPA
    }

    private Percentage(BigDecimal value) {
        this.value = value.setScale(4, RoundingMode.HALF_UP);
    }

    public static Percentage ofFraction(BigDecimal value) {
        Objects.requireNonNull(value, "value must not be null");
        return new Percentage(value);
    }

    public static Percentage ofFraction(String value) {
        return ofFraction(new BigDecimal(value));
    }

    public BigDecimal asFraction() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Percentage other)) {
            return false;
        }
        return value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return value.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
