package com.finflow.transactions.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.finflow.shared.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransactionTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    @Test
    void recordsAValidTransaction() {
        Transaction transaction = Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("100"), TransactionType.EXPENSE, LocalDate.now(), "Lebensmittel");

        assertThat(transaction.userId()).isEqualTo(USER_ID);
        assertThat(transaction.categoryId()).isEqualTo(CATEGORY_ID);
        assertThat(transaction.amount()).isEqualTo(Money.of("100"));
        assertThat(transaction.source()).isEqualTo(TransactionSource.MANUAL);
    }

    @Test
    void rejectsZeroAmount() {
        assertThatThrownBy(() -> Transaction.record(
                USER_ID, CATEGORY_ID, Money.ZERO, TransactionType.EXPENSE, LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("-5"), TransactionType.EXPENSE, LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void rejectsBookedAtInTheFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("10"), TransactionType.INCOME, tomorrow, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bookedAt");
    }

    @Test
    void acceptsBookedAtToday() {
        Transaction transaction = Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("10"), TransactionType.INCOME, LocalDate.now(), null);

        assertThat(transaction.bookedAt()).isEqualTo(LocalDate.now());
    }
}
