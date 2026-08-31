package com.finflow.financialprofile.domain;

import com.finflow.shared.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Hält die für Financial Health Score und Scenario Engine benötigten Basis-Kennzahlen
 * eines Nutzers (siehe Phase 4 Domain Model). Genau ein Profil pro User.
 */
@Entity
@Table(name = "financial_profiles")
public class FinancialProfile {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_income"))
    private Money monthlyIncome;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_expenses"))
    private Money monthlyExpenses;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "emergency_fund"))
    private Money emergencyFund;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "total_debt"))
    private Money totalDebt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FinancialProfile() {
        // for JPA
    }

    private FinancialProfile(
            UUID id, UUID userId, Money monthlyIncome, Money monthlyExpenses, Money emergencyFund,
            Money totalDebt, Instant now) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        applyValues(monthlyIncome, monthlyExpenses, emergencyFund, totalDebt);
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static FinancialProfile create(
            UUID userId, Money monthlyIncome, Money monthlyExpenses, Money emergencyFund, Money totalDebt) {
        return new FinancialProfile(
                UUID.randomUUID(), userId, monthlyIncome, monthlyExpenses, emergencyFund, totalDebt, Instant.now());
    }

    public void update(Money monthlyIncome, Money monthlyExpenses, Money emergencyFund, Money totalDebt) {
        applyValues(monthlyIncome, monthlyExpenses, emergencyFund, totalDebt);
        this.updatedAt = Instant.now();
    }

    private void applyValues(Money monthlyIncome, Money monthlyExpenses, Money emergencyFund, Money totalDebt) {
        this.monthlyIncome = requireNonNegative(monthlyIncome, "monthlyIncome");
        this.monthlyExpenses = requireNonNegative(monthlyExpenses, "monthlyExpenses");
        this.emergencyFund = requireNonNegative(emergencyFund, "emergencyFund");
        this.totalDebt = requireNonNegative(totalDebt, "totalDebt");
    }

    private static Money requireNonNegative(Money value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException(field + " must not be negative: " + value.amount());
        }
        return value;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public Money monthlyIncome() {
        return monthlyIncome;
    }

    public Money monthlyExpenses() {
        return monthlyExpenses;
    }

    public Money emergencyFund() {
        return emergencyFund;
    }

    public Money totalDebt() {
        return totalDebt;
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
        if (!(o instanceof FinancialProfile other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
