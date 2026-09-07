package com.finflow.scenario.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.identity.domain.Role;
import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioRepository;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** Läuft mangels Docker in dieser Entwicklungsumgebung nicht (siehe FinancialProfileJpaRepositoryIT). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ScenarioJpaRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private ScenarioRepository repository;

    @Autowired
    private UserRepository userRepository;

    private UUID persistUser() {
        User user = User.register("test-" + UUID.randomUUID() + "@finflow.example", "hash", Role.USER);
        return userRepository.save(user).id();
    }

    @Test
    void savesAndFindsScenarioOnlyForOwningUser() {
        UUID ownerId = persistUser();
        UUID otherUserId = persistUser();
        Scenario scenario = Scenario.create(
                ownerId, "Current Plan", Money.of("5000"), Money.of("500"), Percentage.ofFraction("0.04"),
                Percentage.ofFraction("0.02"), 20, null, null, null, null);

        Scenario saved = repository.save(scenario);

        assertThat(repository.findByIdAndUserId(saved.id(), ownerId)).isPresent();
        assertThat(repository.findByIdAndUserId(saved.id(), otherUserId)).isEmpty();
    }
}
