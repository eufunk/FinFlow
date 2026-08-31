package com.finflow.transactions.domain;

import com.finflow.shared.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Repräsentiert eine einzelne Einnahme oder Ausgabe (siehe Phase 4 Domain Model). categoryId
 * wird - wie userId - als reiner UUID-Wert gehalten statt als JPA-Beziehung, um nicht auf
 * Lazy-Loading angewiesen zu sein; der Typ-Abgleich mit der Category erfolgt einmalig beim
 * Anlegen in TransactionService, nicht bei jedem Zugriff auf die Entity.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(name = "booked_at", nullable = false)
    private LocalDate bookedAt;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionSource source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Transaction() {
        // for JPA
    }

    private Transaction(
            UUID id, UUID userId, UUID categoryId, Money amount, TransactionType type, LocalDate bookedAt,
            String description, TransactionSource source, Instant createdAt) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.categoryId = Objects.requireNonNull(categoryId, "categoryId must not be null");
        this.amount = requirePositive(amount);
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.bookedAt = requireNotInFuture(bookedAt);
        this.description = description;
        this.source = Objects.requireNonNull(source, "source must not be null");
        this.createdAt = createdAt;
    }

    /** Der einzige öffentliche Erzeugungsweg in diesem Slice - Herkunft ist immer MANUAL. */
    public static Transaction record(
            UUID userId, UUID categoryId, Money amount, TransactionType type, LocalDate bookedAt, String description) {
        return new Transaction(
                UUID.randomUUID(), userId, categoryId, amount, type, bookedAt, description,
                TransactionSource.MANUAL, Instant.now());
    }

    private static Money requirePositive(Money value) {
        Objects.requireNonNull(value, "amount must not be null");
        if (!value.isPositive()) {
            throw new IllegalArgumentException("amount must be positive: " + value.amount());
        }
        return value;
    }

    private static LocalDate requireNotInFuture(LocalDate date) {
        Objects.requireNonNull(date, "bookedAt must not be null");
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("bookedAt must not be in the future: " + date);
        }
        return date;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public UUID categoryId() {
        return categoryId;
    }

    public Money amount() {
        return amount;
    }

    public TransactionType type() {
        return type;
    }

    public LocalDate bookedAt() {
        return bookedAt;
    }

    public String description() {
        return description;
    }

    public TransactionSource source() {
        return source;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
