package com.finflow.transactions.domain;

/** Verletzung der Invariante "transaction.type muss category.type entsprechen" (siehe Phase 4). */
public class CategoryTypeMismatchException extends IllegalArgumentException {

    public CategoryTypeMismatchException(TransactionType transactionType, TransactionType categoryType) {
        super("Transaction type " + transactionType + " does not match category type " + categoryType);
    }
}
