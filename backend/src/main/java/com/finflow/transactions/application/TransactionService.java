package com.finflow.transactions.application;

import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.CategoryNotFoundException;
import com.finflow.transactions.domain.CategoryRepository;
import com.finflow.transactions.domain.CategoryTypeMismatchException;
import com.finflow.transactions.domain.Transaction;
import com.finflow.transactions.domain.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Transaction createTransaction(UUID userId, CreateTransactionCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        if (category.type() != command.type()) {
            throw new CategoryTypeMismatchException(command.type(), category.type());
        }

        Transaction transaction = Transaction.record(
                userId, command.categoryId(), command.amount(), command.type(),
                command.bookedAt(), command.description());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction {} recorded for user {}", saved.id(), userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Transaction> listTransactions(UUID userId, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return transactionRepository.findByUserId(userId);
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to must both be provided together");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }
        return transactionRepository.findByUserIdAndBookedAtBetween(userId, from, to);
    }
}
