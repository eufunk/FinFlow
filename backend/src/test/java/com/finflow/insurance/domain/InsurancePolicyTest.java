package com.finflow.insurance.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InsurancePolicyTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void coversWithValidValues() {
        InsurancePolicy policy = InsurancePolicy.cover(
                USER_ID, InsuranceType.LIABILITY, Money.of("5000000"), LocalDate.now(), null);

        assertThat(policy.type()).isEqualTo(InsuranceType.LIABILITY);
        assertThat(policy.isActiveOn(LocalDate.now())).isTrue();
    }

    @Test
    void rejectsValidUntilBeforeValidFrom() {
        LocalDate from = LocalDate.now();
        LocalDate until = from.minusDays(1);

        assertThatThrownBy(() -> InsurancePolicy.cover(USER_ID, InsuranceType.HOUSEHOLD, Money.ZERO, from, until))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNegativeCoverageAmount() {
        assertThatThrownBy(() -> InsurancePolicy.cover(
                USER_ID, InsuranceType.HEALTH, Money.of("-1"), LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isNotActiveAfterValidUntil() {
        InsurancePolicy policy = InsurancePolicy.cover(
                USER_ID, InsuranceType.DISABILITY, Money.of("1000"), LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(1));

        assertThat(policy.isActiveOn(LocalDate.now())).isFalse();
    }

    @Test
    void isActiveOnValidUntilDateItself() {
        LocalDate until = LocalDate.now();
        InsurancePolicy policy = InsurancePolicy.cover(
                USER_ID, InsuranceType.DISABILITY, Money.of("1000"), LocalDate.now().minusYears(1), until);

        assertThat(policy.isActiveOn(until)).isTrue();
    }
}
