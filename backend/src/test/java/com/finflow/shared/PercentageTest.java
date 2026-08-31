package com.finflow.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PercentageTest {

    @Test
    void storesFractionWithFourDecimalPlaces() {
        Percentage percentage = Percentage.ofFraction("0.0425");

        assertThat(percentage.asFraction()).isEqualByComparingTo("0.0425");
    }

    @Test
    void zeroConstantIsZero() {
        assertThat(Percentage.ZERO.asFraction()).isEqualByComparingTo("0");
    }

    @Test
    void allowsNegativeValues() {
        Percentage percentage = Percentage.ofFraction("-0.02");

        assertThat(percentage.asFraction()).isEqualByComparingTo("-0.02");
    }

    @Test
    void equalityIsBasedOnValueNotScale() {
        assertThat(Percentage.ofFraction("0.04")).isEqualTo(Percentage.ofFraction("0.0400"));
    }
}
