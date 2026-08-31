package com.finflow.analytics.domain;

import com.finflow.shared.Money;

/**
 * Rohdaten für die Financial-Health-Berechnung, bewusst entkoppelt davon, welches Modul sie
 * liefert (FinancialProfile, Account, InsurancePolicy) - siehe FinancialHealthCalculator.
 */
public record FinancialHealthInput(
        Money monthlyIncome,
        Money monthlyExpenses,
        Money emergencyFund,
        Money totalDebt,
        int coveredCoreInsuranceRiskCount,
        Money investedAmount,
        Money totalAssets,
        int distinctAssetClassCount) {
}
