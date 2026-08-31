package com.finflow.transactions.infrastructure;

import com.finflow.transactions.domain.Transaction;
import com.finflow.transactions.domain.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TransactionJpaRepository extends TransactionRepository, JpaRepository<Transaction, UUID> {

    @Override
    List<Transaction> findByUserId(UUID userId);

    @Override
    List<Transaction> findByUserIdAndBookedAtBetween(UUID userId, LocalDate from, LocalDate to);
}
