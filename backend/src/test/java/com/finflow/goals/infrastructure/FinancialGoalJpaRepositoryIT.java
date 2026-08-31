package com.finflow.goals.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.goals.domain.FinancialGoal;
import com.finflow.goals.domain.FinancialGoalRepository;
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

/** Läuft mangels Docker in dieser Entwicklungsumgebung nicht (siehe FinancialProfileJpaRepositoryIT). */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FinancialGoalJpaRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private FinancialGoalRepository repository;

    @Autowired
    private UserRepository userRepository;

    private UUID persistUser() {
        User user = User.register("test-" + UUID.randomUUID() + "@finflow.example", "hash", Role.USER);
        return userRepository.save(user).id();
    }

    @Test
    void savesAndListsGoalsForUser() {
        UUID userId = persistUser();
        FinancialGoal goal = FinancialGoal.create(
                userId, "Notgroschen", Money.of("6000"), Money.of("1000"), null, Money.of("200"), null);

        repository.save(goal);

        var found = repository.findByUserId(userId);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).name()).isEqualTo("Notgroschen");
    }
}
