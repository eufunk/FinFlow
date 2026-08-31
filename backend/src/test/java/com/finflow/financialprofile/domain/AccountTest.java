package com.finflow.financialprofile.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Test
    void opensAccountWithValidValues() {
        Account account = Account.open(USER_ID, "Tagesgeld", AccountType.SAVINGS, Money.of("5000"));

        assertThat(account.userId()).isEqualTo(USER_ID);
        assertThat(account.type()).isEqualTo(AccountType.SAVINGS);
        assertThat(account.balance()).isEqualTo(Money.of("5000"));
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> Account.open(USER_ID, " ", AccountType.CHECKING, Money.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void rejectsNegativeBalance() {
        assertThatThrownBy(() -> Account.open(USER_ID, "Girokonto", AccountType.CHECKING, Money.of("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("balance");
    }
}
