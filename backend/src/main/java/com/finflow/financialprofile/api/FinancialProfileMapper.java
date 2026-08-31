package com.finflow.financialprofile.api;

import com.finflow.financialprofile.application.UpsertFinancialProfileCommand;
import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.shared.Money;

/**
 * Manuelles Mapping statt MapStruct: bei einer einzelnen, kleinen Entity ist der zusätzliche
 * Build-Step/Dependency-Overhead von MapStruct nicht gerechtfertigt (siehe ADR-005, DTOs statt
 * direkter Entity-Exposition). Sollte die Anzahl der Mapper wachsen, ist der Umstieg jederzeit
 * möglich, ohne Controller/Service anzufassen.
 */
final class FinancialProfileMapper {

    private FinancialProfileMapper() {
    }

    static FinancialProfileResponse toResponse(FinancialProfile profile) {
        return new FinancialProfileResponse(
                profile.id(),
                profile.userId(),
                profile.monthlyIncome().amount(),
                profile.monthlyExpenses().amount(),
                profile.emergencyFund().amount(),
                profile.totalDebt().amount(),
                profile.createdAt(),
                profile.updatedAt());
    }

    static UpsertFinancialProfileCommand toCommand(UpsertFinancialProfileRequest request) {
        return new UpsertFinancialProfileCommand(
                Money.of(request.monthlyIncome()),
                Money.of(request.monthlyExpenses()),
                Money.of(request.emergencyFund()),
                Money.of(request.totalDebt()));
    }
}
