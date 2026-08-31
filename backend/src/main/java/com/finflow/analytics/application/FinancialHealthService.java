package com.finflow.analytics.application;

import com.finflow.analytics.domain.FinancialHealthCalculator;
import com.finflow.analytics.domain.FinancialHealthInput;
import com.finflow.analytics.domain.FinancialHealthScore;
import com.finflow.financialprofile.application.AccountQueryService;
import com.finflow.financialprofile.application.FinancialProfileService;
import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountType;
import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.insurance.application.InsurancePolicyQueryService;
import com.finflow.insurance.domain.InsurancePolicy;
import com.finflow.insurance.domain.InsuranceType;
import com.finflow.shared.Money;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestriert die Financial-Health-Berechnung: sammelt Rohdaten aus drei Modulen (financial
 * profile, financialprofile.Account, insurance) ausschließlich über deren Application-Services
 * (siehe Backend-Architektur, "Zugriffe zwischen Modulen nur über die Application-Schicht") und
 * delegiert die eigentliche Berechnung an den reinen FinancialHealthCalculator.
 */
@Service
public class FinancialHealthService {

    private static final Set<InsuranceType> CORE_RISK_TYPES =
            EnumSet.of(InsuranceType.LIABILITY, InsuranceType.DISABILITY, InsuranceType.HOUSEHOLD);

    private final FinancialProfileService financialProfileService;
    private final AccountQueryService accountQueryService;
    private final InsurancePolicyQueryService insurancePolicyQueryService;

    public FinancialHealthService(
            FinancialProfileService financialProfileService,
            AccountQueryService accountQueryService,
            InsurancePolicyQueryService insurancePolicyQueryService) {
        this.financialProfileService = financialProfileService;
        this.accountQueryService = accountQueryService;
        this.insurancePolicyQueryService = insurancePolicyQueryService;
    }

    @Transactional(readOnly = true)
    public FinancialHealthScore calculateScore(UUID userId) {
        // wirft FinancialProfileNotFoundException -> 404, wenn noch kein Profil existiert
        // (siehe Phase 2 Acceptance Criteria "Hinweis zur Vervollständigung des Profils")
        FinancialProfile profile = financialProfileService.getProfile(userId);

        List<Account> accounts = accountQueryService.listAccounts(userId);
        Money totalAssets = accounts.stream().map(Account::balance).reduce(Money.ZERO, Money::add);
        Money investedAmount = accounts.stream()
                .filter(account -> account.type() == AccountType.INVESTMENT)
                .map(Account::balance)
                .reduce(Money.ZERO, Money::add);
        int distinctAssetClassCount = (int) accounts.stream()
                .filter(account -> account.balance().isPositive())
                .map(Account::type)
                .distinct()
                .count();

        LocalDate today = LocalDate.now();
        List<InsurancePolicy> policies = insurancePolicyQueryService.listPolicies(userId);
        int coveredCoreRisks = (int) policies.stream()
                .filter(policy -> CORE_RISK_TYPES.contains(policy.type()))
                .filter(policy -> policy.isActiveOn(today))
                .map(InsurancePolicy::type)
                .distinct()
                .count();

        FinancialHealthInput input = new FinancialHealthInput(
                profile.monthlyIncome(), profile.monthlyExpenses(), profile.emergencyFund(), profile.totalDebt(),
                coveredCoreRisks, investedAmount, totalAssets, distinctAssetClassCount);

        return FinancialHealthCalculator.calculate(input);
    }
}
