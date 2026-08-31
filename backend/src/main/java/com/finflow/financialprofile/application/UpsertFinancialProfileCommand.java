package com.finflow.financialprofile.application;

import com.finflow.shared.Money;

public record UpsertFinancialProfileCommand(
        Money monthlyIncome,
        Money monthlyExpenses,
        Money emergencyFund,
        Money totalDebt) {
}
