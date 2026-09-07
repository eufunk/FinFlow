package com.finflow.scenario.domain;

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
import java.util.UUID;

/**
 * Speichert die Eingabeparameter einer vom Nutzer angelegten Zukunftssimulation (siehe Phase 4).
 * Das Ergebnis (ScenarioProjectionResult) wird bewusst nie persistiert, sondern bei jedem Abruf
 * über {@link #project(LocalDate)} neu berechnet - reine, deterministische Funktion der
 * Parameter, kein zweiter "Source of Truth".
 */
@Entity
@Table(name = "scenarios")
public class Scenario {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String name;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current_capital"))
    private Money currentCapital;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_savings"))
    private Money monthlySavings;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "annual_return"))
    private Percentage annualReturn;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "inflation"))
    private Percentage inflation;

    @Column(name = "duration_in_years", nullable = false)
    private int durationInYears;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "monthly_income"))
    private Money monthlyIncome;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "income_growth"))
    private Percentage incomeGrowth;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "expenses_growth"))
    private Percentage expensesGrowth;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "target_capital"))
    private Money targetCapital;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Scenario() {
        // for JPA
    }

    private Scenario(
            UUID id, UUID userId, String name, Money currentCapital, Money monthlySavings,
            Percentage annualReturn, Percentage inflation, int durationInYears, Money monthlyIncome,
            Percentage incomeGrowth, Percentage expensesGrowth, Money targetCapital, Instant now) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.name = requireNonBlank(name);
        this.currentCapital = requireNonNegative(currentCapital, "currentCapital");
        this.monthlySavings = requireNonNegative(monthlySavings, "monthlySavings");
        this.annualReturn = Objects.requireNonNull(annualReturn, "annualReturn must not be null");
        this.inflation = Objects.requireNonNull(inflation, "inflation must not be null");
        this.durationInYears = requireDurationInRange(durationInYears);
        requireConsistentIncomeProjection(monthlyIncome, monthlySavings, incomeGrowth, expensesGrowth);
        this.monthlyIncome = monthlyIncome;
        this.incomeGrowth = incomeGrowth;
        this.expensesGrowth = expensesGrowth;
        this.targetCapital = requirePositiveIfPresent(targetCapital, "targetCapital");
        this.createdAt = now;
    }

    public static Scenario create(
            UUID userId, String name, Money currentCapital, Money monthlySavings, Percentage annualReturn,
            Percentage inflation, int durationInYears, Money monthlyIncome, Percentage incomeGrowth,
            Percentage expensesGrowth, Money targetCapital) {
        return new Scenario(
                UUID.randomUUID(), userId, name, currentCapital, monthlySavings, annualReturn, inflation,
                durationInYears, monthlyIncome, incomeGrowth, expensesGrowth, targetCapital, Instant.now());
    }

    private static String requireNonBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return name;
    }

    private static Money requireNonNegative(Money value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.isNegative()) {
            throw new IllegalArgumentException(field + " must not be negative: " + value.amount());
        }
        return value;
    }

    private static Money requirePositiveIfPresent(Money value, String field) {
        if (value != null && !value.isPositive()) {
            throw new IllegalArgumentException(field + " must be positive when given: " + value.amount());
        }
        return value;
    }

    private static int requireDurationInRange(int durationInYears) {
        if (durationInYears < 1 || durationInYears > 100) {
            throw new IllegalArgumentException("durationInYears must be between 1 and 100: " + durationInYears);
        }
        return durationInYears;
    }

    private static void requireConsistentIncomeProjection(
            Money monthlyIncome, Money monthlySavings, Percentage incomeGrowth, Percentage expensesGrowth) {
        if (incomeGrowth == null && expensesGrowth == null) {
            return;
        }
        if (monthlyIncome == null) {
            throw new IllegalArgumentException(
                    "monthlyIncome is required when incomeGrowth or expensesGrowth is given");
        }
        if (monthlySavings.amount().compareTo(monthlyIncome.amount()) > 0) {
            throw new IllegalArgumentException("monthlySavings must not exceed monthlyIncome");
        }
    }

    /** Berechnet das Simulationsergebnis neu - siehe ScenarioProjectionCalculator für die Formel. */
    public ScenarioProjectionResult project(LocalDate today) {
        ScenarioProjectionInput input = new ScenarioProjectionInput(
                currentCapital, monthlySavings, annualReturn, inflation, durationInYears,
                monthlyIncome, incomeGrowth, expensesGrowth, targetCapital);
        return ScenarioProjectionCalculator.calculate(input, today);
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

    public Money currentCapital() {
        return currentCapital;
    }

    public Money monthlySavings() {
        return monthlySavings;
    }

    public Percentage annualReturn() {
        return annualReturn;
    }

    public Percentage inflation() {
        return inflation;
    }

    public int durationInYears() {
        return durationInYears;
    }

    public Money monthlyIncome() {
        return monthlyIncome;
    }

    public Percentage incomeGrowth() {
        return incomeGrowth;
    }

    public Percentage expensesGrowth() {
        return expensesGrowth;
    }

    public Money targetCapital() {
        return targetCapital;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scenario other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
