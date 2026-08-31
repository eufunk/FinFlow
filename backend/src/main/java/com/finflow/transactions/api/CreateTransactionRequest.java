package com.finflow.transactions.api;

import com.finflow.transactions.domain.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        @NotNull UUID categoryId,
        @NotNull @PastOrPresent LocalDate bookedAt,
        @Size(max = 255) String description) {
}
