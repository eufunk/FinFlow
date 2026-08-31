package com.finflow.transactions.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.finflow.identity.domain.Role;
import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import com.finflow.shared.Money;
import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.CategoryRepository;
import com.finflow.transactions.domain.Transaction;
import com.finflow.transactions.domain.TransactionRepository;
import com.finflow.transactions.domain.TransactionType;
import java.time.LocalDate;
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
 * Läuft mangels Docker in dieser Entwicklungsumgebung nicht (siehe FinancialProfileJpaRepositoryIT),
 * kompiliert aber und ist auf einer Maschine/CI mit Docker lauffähig. Nutzt bewusst die per Flyway
 * (V11) echt gesäten Categories statt sie manuell zu konstruieren - Category hat in diesem Slice
 * keinen öffentlichen Erzeugungsweg (siehe Klassenkommentar).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TransactionJpaRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID persistUser() {
        User user = User.register("test-" + UUID.randomUUID() + "@finflow.example", "hash", Role.USER);
        return userRepository.save(user).id();
    }

    private Category seededExpenseCategory() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.type() == TransactionType.EXPENSE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Expected V11 to have seeded at least one EXPENSE category"));
    }

    @Test
    void savesAndListsTransactionsForUser() {
        UUID userId = persistUser();
        Category category = seededExpenseCategory();
        Transaction transaction = Transaction.record(
                userId, category.id(), Money.of("42.50"), TransactionType.EXPENSE, LocalDate.now(), "Testkauf");

        transactionRepository.save(transaction);

        var found = transactionRepository.findByUserId(userId);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).amount()).isEqualTo(Money.of("42.50"));
    }

    @Test
    void filtersTransactionsByBookedAtRange() {
        UUID userId = persistUser();
        Category category = seededExpenseCategory();
        transactionRepository.save(Transaction.record(
                userId, category.id(), Money.of("10"), TransactionType.EXPENSE, LocalDate.now().minusDays(60), null));
        transactionRepository.save(Transaction.record(
                userId, category.id(), Money.of("20"), TransactionType.EXPENSE, LocalDate.now(), null));

        var found = transactionRepository.findByUserIdAndBookedAtBetween(
                userId, LocalDate.now().minusDays(1), LocalDate.now());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).amount()).isEqualTo(Money.of("20"));
    }
}
