package com.finflow.goals.domain;

import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Repräsentiert ein Sparziel mit Zielbetrag, Fortschritt und Zieldatum (siehe Phase 4 Domain Model). */
@Entity
@Table(name = "financial_goals")
public class FinancialGoal {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "target_amount"))
    private Money targetAmount;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current_amount"))
    private Money currentAmount;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_contribution"))
    private Money monthlyContribution;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "expected_annual_return"))
    private Percentage expectedAnnualReturn;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FinancialGoal() {
        // for JPA
    }

    private FinancialGoal(
            UUID id, UUID userId, String name, Money targetAmount, Money currentAmount, LocalDate targetDate,
            Money monthlyContribution, Percentage expectedAnnualReturn, Instant now) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.name = requireNonBlank(name);
        this.targetAmount = requirePositive(targetAmount, "targetAmount");
        this.currentAmount = requireNonNegative(currentAmount, "currentAmount");
        this.targetDate = requireNotInPast(targetDate);
        this.monthlyContribution = requireNonNegative(monthlyContribution, "monthlyContribution");
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static FinancialGoal create(
            UUID userId, String name, Money targetAmount, Money currentAmount, LocalDate targetDate,
            Money monthlyContribution, Percentage expectedAnnualReturn) {
        return new FinancialGoal(
                UUID.randomUUID(), userId, name, targetAmount, currentAmount, targetDate,
                monthlyContribution, expectedAnnualReturn, Instant.now());
    }

    private static String requireNonBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }

    private static Money requirePositive(Money value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (!value.isPositive()) {
            throw new IllegalArgumentException(field + " must be positive: " + value.amount());
        }
        return value;
    }

    private static Money requireNonNegative(Money value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException(field + " must not be negative: " + value.amount());
        }
        return value;
    }

    private static LocalDate requireNotInPast(LocalDate targetDate) {
        if (targetDate != null && targetDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("targetDate must not be in the past: " + targetDate);
        }
        return targetDate;
    }

    /** Edge Case 8: Zielbetrag <= aktuelles Kapital -> Ziel gilt sofort als erreicht. */
    public boolean isAchieved() {
        return currentAmount.amount().compareTo(targetAmount.amount()) >= 0;
    }

    /**
     * Liefert leer ("nicht erreichbar"), wenn das Ziel innerhalb von 100 Jahren nicht erreicht wird.
     * Ohne hinterlegte Rendite-Annahme wird konservativ mit 0 % gerechnet.
     */
    public Optional<LocalDate> estimateAchievementDate(LocalDate today) {
        Percentage returnAssumption = expectedAnnualReturn != null ? expectedAnnualReturn : Percentage.ZERO;
        return GoalProjectionCalculator.estimateAchievementDate(
                currentAmount, targetAmount, monthlyContribution, returnAssumption, today);
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

    public Money targetAmount() {
        return targetAmount;
    }

    public Money currentAmount() {
        return currentAmount;
    }

    public LocalDate targetDate() {
        return targetDate;
    }

    public Money monthlyContribution() {
        return monthlyContribution;
    }

    public Percentage expectedAnnualReturn() {
        return expectedAnnualReturn;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FinancialGoal other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
