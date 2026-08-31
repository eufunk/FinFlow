package com.finflow.analytics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinancialHealthServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FinancialProfileService financialProfileService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InsurancePolicyQueryService insurancePolicyQueryService;

    private FinancialHealthService service;

    @BeforeEach
    void setUp() {
        service = new FinancialHealthService(financialProfileService, accountQueryService, insurancePolicyQueryService);
    }

    @Test
    void aggregatesAccountsAndActivePoliciesIntoTheScore() {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2400"), Money.of("14400"), Money.ZERO);
        when(financialProfileService.getProfile(USER_ID)).thenReturn(profile);

        when(accountQueryService.listAccounts(USER_ID)).thenReturn(List.of(
                Account.open(USER_ID, "Girokonto", AccountType.CHECKING, Money.of("2000")),
                Account.open(USER_ID, "Depot", AccountType.INVESTMENT, Money.of("3000"))));

        InsurancePolicy activeLiability = InsurancePolicy.cover(
                USER_ID, InsuranceType.LIABILITY, Money.of("5000000"), LocalDate.now().minusYears(1), null);
        InsurancePolicy expiredDisability = InsurancePolicy.cover(
                USER_ID, InsuranceType.DISABILITY, Money.of("1000"), LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(1));
        when(insurancePolicyQueryService.listPolicies(USER_ID)).thenReturn(List.of(activeLiability, expiredDisability));

        var result = service.calculateScore(USER_ID);

        // 1 von 3 Kernrisiken aktiv abgedeckt (die abgelaufene zählt nicht)
        assertThat(result.insuranceCoverage().points()).isEqualByComparingTo("7");
        // investedAmount 3000 von totalAssets 5000 = 60%, 2 von 4 Kontoarten
        assertThat(result.investmentDiversification().points()).isGreaterThan(java.math.BigDecimal.ZERO);
    }

    @Test
    void ignoresPoliciesOutsideTheThreeCoreRiskTypes() {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2400"), Money.of("14400"), Money.ZERO);
        when(financialProfileService.getProfile(USER_ID)).thenReturn(profile);
        when(accountQueryService.listAccounts(USER_ID)).thenReturn(List.of());

        InsurancePolicy health = InsurancePolicy.cover(
                USER_ID, InsuranceType.HEALTH, Money.of("100000"), LocalDate.now().minusYears(1), null);
        when(insurancePolicyQueryService.listPolicies(USER_ID)).thenReturn(List.of(health));

        var result = service.calculateScore(USER_ID);

        assertThat(result.insuranceCoverage().points()).isEqualByComparingTo("0");
    }
}
