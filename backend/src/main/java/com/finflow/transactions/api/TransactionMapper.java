package com.finflow.transactions.api;

import com.finflow.shared.Money;
import com.finflow.transactions.application.CreateTransactionCommand;
import com.finflow.transactions.domain.Transaction;

final class TransactionMapper {

    private TransactionMapper() {
    }

    static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.categoryId(),
                transaction.amount().amount(),
                transaction.type(),
                transaction.bookedAt(),
                transaction.description(),
                transaction.source(),
                transaction.createdAt());
    }

    static CreateTransactionCommand toCommand(CreateTransactionRequest request) {
        return new CreateTransactionCommand(
                Money.of(request.amount()),
                request.type(),
                request.categoryId(),
                request.bookedAt(),
                request.description());
    }
}
