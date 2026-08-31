package com.finflow.transactions.application;

import com.finflow.shared.Money;
import com.finflow.transactions.domain.TransactionType;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionCommand(
        Money amount,
        TransactionType type,
        UUID categoryId,
        LocalDate bookedAt,
        String description) {
}
