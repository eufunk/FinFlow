package com.finflow.analytics.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.financialprofile.application.FinancialProfileService;
import com.finflow.financialprofile.application.UpsertFinancialProfileCommand;
import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountRepository;
import com.finflow.financialprofile.domain.AccountType;
import com.finflow.identity.domain.Role;
import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import com.finflow.insurance.domain.InsurancePolicy;
import com.finflow.insurance.domain.InsurancePolicyRepository;
import com.finflow.insurance.domain.InsuranceType;
import com.finflow.shared.Money;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * End-to-End-Test der "zentralen Business-Komponente" über echte Postgres-Daten aus drei
 * Modulen hinweg (identity, financialprofile inkl. Account, insurance). Läuft mangels Docker in
 * dieser Entwicklungsumgebung nicht (siehe FinancialProfileJpaRepositoryIT), kompiliert aber.
 */
@SpringBootTest
@Testcontainers
class FinancialHealthServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private FinancialHealthService financialHealthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialProfileService financialProfileService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    @Test
    void calculatesAFullScoreForAWellPreparedUser() {
        UUID userId = userRepository.save(
                User.register("healthy-" + UUID.randomUUID() + "@finflow.example", "hash", Role.USER)).id();

        financialProfileService.upsertProfile(userId, new UpsertFinancialProfileCommand(
                Money.of("3000"), Money.of("2400"), Money.of("14400"), Money.ZERO));

        accountRepository.save(Account.open(userId, "Girokonto", AccountType.CHECKING, Money.of("3500")));
        accountRepository.save(Account.open(userId, "Tagesgeld", AccountType.SAVINGS, Money.of("3500")));
        accountRepository.save(Account.open(userId, "Depot", AccountType.INVESTMENT, Money.of("3000")));

        insurancePolicyRepository.save(InsurancePolicy.cover(
                userId, InsuranceType.LIABILITY, Money.of("5000000"), LocalDate.now().minusYears(1), null));
        insurancePolicyRepository.save(InsurancePolicy.cover(
                userId, InsuranceType.DISABILITY, Money.of("1500"), LocalDate.now().minusYears(1), null));
        insurancePolicyRepository.save(InsurancePolicy.cover(
                userId, InsuranceType.HOUSEHOLD, Money.of("50000"), LocalDate.now().minusYears(1), null));

        var score = financialHealthService.calculateScore(userId);

        assertThat(score.totalScore()).isGreaterThanOrEqualTo(80);
        assertThat(score.insuranceCoverage().points()).isEqualByComparingTo("20");
    }
}
