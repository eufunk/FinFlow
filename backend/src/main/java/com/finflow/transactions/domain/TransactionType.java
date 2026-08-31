package com.finflow.transactions.domain;

/** Gemeinsam von Category und Transaction genutzt, damit der Typ-Abgleich (Invariante) typsicher ist. */
public enum TransactionType {
    INCOME,
    EXPENSE
}
