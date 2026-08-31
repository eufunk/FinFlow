package com.finflow.transactions.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Klassifiziert Transaktionen (z. B. Gehalt, Miete). In diesem Slice ausschließlich per
 * Flyway-Seed (V11) angelegt - kein öffentlicher Erzeugungsweg über die Anwendung, daher
 * kein Factory-Constructor wie bei den anderen Entities.
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    protected Category() {
        // for JPA
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public TransactionType type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
