package com.finflow.insurance.domain;

import com.finflow.shared.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Erfasst eine Versicherung des Nutzers inkl. Deckungssumme für die Coverage-Bewertung im
 * Health Score (siehe Phase 4). Noch ohne eigenen REST-Endpoint zum Anlegen - siehe
 * financialprofile.domain.Account für dieselbe bewusste Scoping-Entscheidung.
 */
@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InsuranceType type;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "coverage_amount"))
    private Money coverageAmount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    protected InsurancePolicy() {
        // for JPA
    }

    private InsurancePolicy(
            UUID id, UUID userId, InsuranceType type, Money coverageAmount, LocalDate validFrom, LocalDate validUntil) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.coverageAmount = requireNonNegative(coverageAmount);
        this.validFrom = Objects.requireNonNull(validFrom, "validFrom must not be null");
        this.validUntil = requireValidRange(validFrom, validUntil);
    }

    public static InsurancePolicy cover(
            UUID userId, InsuranceType type, Money coverageAmount, LocalDate validFrom, LocalDate validUntil) {
        return new InsurancePolicy(UUID.randomUUID(), userId, type, coverageAmount, validFrom, validUntil);
    }

    private static Money requireNonNegative(Money value) {
        Objects.requireNonNull(value, "coverageAmount must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException("coverageAmount must not be negative: " + value.amount());
        }
        return value;
    }

    private static LocalDate requireValidRange(LocalDate validFrom, LocalDate validUntil) {
        if (validUntil != null && validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("validUntil must not be before validFrom");
        }
        return validUntil;
    }

    /** Edge Case 20: abgelaufene Policen zählen nicht als aktive Coverage. */
    public boolean isActiveOn(LocalDate date) {
        return validUntil == null || !validUntil.isBefore(date);
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public InsuranceType type() {
        return type;
    }

    public Money coverageAmount() {
        return coverageAmount;
    }

    public LocalDate validFrom() {
        return validFrom;
    }

    public LocalDate validUntil() {
        return validUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InsurancePolicy other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
