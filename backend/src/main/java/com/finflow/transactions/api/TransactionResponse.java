package com.finflow.transactions.api;

import com.finflow.transactions.domain.TransactionSource;
import com.finflow.transactions.domain.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID categoryId,
        BigDecimal amount,
        TransactionType type,
        LocalDate bookedAt,
        String description,
        TransactionSource source,
        Instant createdAt) {
}
