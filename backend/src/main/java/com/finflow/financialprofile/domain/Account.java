package com.finflow.financialprofile.domain;

import com.finflow.shared.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Momentaufnahme eines Vermögenswerts (Konto/Investment) für die Net-Worth-Berechnung
 * (siehe Phase 4). Bewusst nicht aus Transactions abgeleitet - kein Double-Entry-Ledger im
 * MVP, balance wird manuell gepflegt. In diesem Slice nur lesend über AccountQueryService für
 * die Financial Health Engine genutzt, noch ohne eigenen REST-Endpoint zum Anlegen/Ändern.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;

    @Embedded
    private Money balance;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Account() {
        // for JPA
    }

    private Account(UUID id, UUID userId, String name, AccountType type, Money balance, Instant now) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.name = requireNonBlank(name);
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.balance = requireNonNegative(balance);
        this.updatedAt = now;
    }

    public static Account open(UUID userId, String name, AccountType type, Money balance) {
        return new Account(UUID.randomUUID(), userId, name, type, balance, Instant.now());
    }

    private static String requireNonBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }

    private static Money requireNonNegative(Money value) {
        Objects.requireNonNull(value, "balance must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException("balance must not be negative: " + value.amount());
        }
        return value;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String name() {
        return name;
    }

    public AccountType type() {
        return type;
    }

    public Money balance() {
        return balance;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
