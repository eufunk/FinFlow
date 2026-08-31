package com.finflow.financialprofile.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.financialprofile.domain.FinancialProfileRepository;
import com.finflow.identity.domain.Role;
import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import com.finflow.shared.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Integrationstest gegen eine echte Postgres-Instanz via Testcontainers (siehe ADR-007) statt
 * einer In-Memory-Datenbank, damit Constraints/Typen aus den Flyway-Migrationen tatsächlich
 * geprüft werden. Benötigt einen laufenden Docker-Daemon - läuft in dieser Entwicklungsumgebung
 * mangels Docker nicht, ist aber lauffähig in CI/auf einer Maschine mit Docker.
 *
 * users.id wird über den öffentlichen UserRepository-Port des identity-Moduls angelegt (nicht
 * über eine JPA-Beziehung, siehe FinancialProfile-Klassenkommentar zu Modulgrenzen) - @DataJpaTest
 * lädt dessen Spring-Data-Implementierung automatisch mit, da sie ebenfalls ein JPA-Repository ist.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FinancialProfileJpaRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private FinancialProfileRepository repository;

    @Autowired
    private UserRepository userRepository;

    private UUID persistUser() {
        User user = User.register("test-" + UUID.randomUUID() + "@finflow.example", "hash", Role.USER);
        return userRepository.save(user).id();
    }

    @Test
    void savesAndReloadsProfileByUserId() {
        UUID userId = persistUser();
        FinancialProfile profile = FinancialProfile.create(
                userId, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));

        repository.save(profile);

        var reloaded = repository.findByUserId(userId);

        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().monthlyIncome()).isEqualTo(Money.of("3000"));
    }

    @Test
    void returnsEmptyWhenNoProfileExistsForUser() {
        UUID userId = persistUser();

        assertThat(repository.findByUserId(userId)).isEmpty();
    }
}
