package com.finflow.financialprofile.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FinancialProfileTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void createsProfileWithValidValues() {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));

        assertThat(profile.userId()).isEqualTo(USER_ID);
        assertThat(profile.monthlyIncome()).isEqualTo(Money.of("3000"));
        assertThat(profile.createdAt()).isEqualTo(profile.updatedAt());
    }

    @Test
    void rejectsNegativeMonthlyIncome() {
        assertThatThrownBy(() -> FinancialProfile.create(
                USER_ID, Money.of("-1"), Money.of("2000"), Money.of("6000"), Money.of("0")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monthlyIncome");
    }

    @Test
    void rejectsNegativeTotalDebt() {
        assertThatThrownBy(() -> FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("-500")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalDebt");
    }

    @Test
    void updateReplacesValuesAndBumpsUpdatedAt() throws InterruptedException {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));
        var createdAt = profile.createdAt();

        Thread.sleep(5);
        profile.update(Money.of("3500"), Money.of("2100"), Money.of("6500"), Money.of("100"));

        assertThat(profile.monthlyIncome()).isEqualTo(Money.of("3500"));
        assertThat(profile.totalDebt()).isEqualTo(Money.of("100"));
        assertThat(profile.createdAt()).isEqualTo(createdAt);
        assertThat(profile.updatedAt()).isAfter(createdAt);
    }

    @Test
    void updateRejectsNegativeValuesJustLikeCreate() {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));

        assertThatThrownBy(() -> profile.update(Money.of("3000"), Money.of("-1"), Money.of("6000"), Money.of("0")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("monthlyExpenses");
    }
}
