package com.finflow.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void roundsToTwoDecimalPlaces() {
        Money money = Money.of(new BigDecimal("12.345"));

        assertThat(money.amount()).isEqualByComparingTo("12.35");
    }

    @Test
    void addsTwoAmounts() {
        Money result = Money.of("10.00").add(Money.of("5.50"));

        assertThat(result).isEqualTo(Money.of("15.50"));
    }

    @Test
    void subtractsTwoAmounts() {
        Money result = Money.of("10.00").subtract(Money.of("15.00"));

        assertThat(result).isEqualTo(Money.of("-5.00"));
        assertThat(result.isNegative()).isTrue();
    }

    @Test
    void equalityIsBasedOnValueNotScale() {
        assertThat(Money.of("10")).isEqualTo(Money.of("10.00"));
    }

    @Test
    void zeroConstantIsZero() {
        assertThat(Money.ZERO.amount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void rejectsNullAmount() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> Money.of((BigDecimal) null));
    }
}
