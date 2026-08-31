package com.finflow.transactions.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository {

    List<Transaction> findByUserId(UUID userId);

    List<Transaction> findByUserIdAndBookedAtBetween(UUID userId, LocalDate from, LocalDate to);

    Transaction save(Transaction transaction);
}
