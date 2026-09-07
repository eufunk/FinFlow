package com.finflow.shared.dev;

import com.finflow.financialprofile.application.FinancialProfileService;
import com.finflow.financialprofile.application.UpsertFinancialProfileCommand;
import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountRepository;
import com.finflow.financialprofile.domain.AccountType;
import com.finflow.goals.application.CreateFinancialGoalCommand;
import com.finflow.goals.application.FinancialGoalService;
import com.finflow.identity.domain.Role;
import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import com.finflow.insurance.domain.InsurancePolicy;
import com.finflow.insurance.domain.InsurancePolicyRepository;
import com.finflow.insurance.domain.InsuranceType;
import com.finflow.scenario.application.CreateScenarioCommand;
import com.finflow.scenario.application.ScenarioService;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import com.finflow.transactions.application.CreateTransactionCommand;
import com.finflow.transactions.application.TransactionService;
import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.CategoryRepository;
import com.finflow.transactions.domain.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Legt bei fehlendem Login (Security kommt erst in Phase 9) einen festen Demo-User mit
 * realistischen Beispieldaten an, damit das Frontend gegen echte Daten entwickelt werden kann
 * (siehe Testdaten-Konzept aus Phase 4). Nur unter dem Spring-Profil "dev" aktiv, idempotent
 * (überspringt sich selbst, wenn der Demo-User schon existiert).
 *
 * <p>Greift bewusst direkt auf Account-/InsurancePolicy-Repository-Ports zu statt über einen
 * Application-Service: für diese beiden Module existiert noch kein schreibender Service (siehe
 * Phase 5, Financial Health Engine), und ein Seeder ist ohnehin kein fachlicher Use-Case, sondern
 * Bootstrap-Infrastruktur - vergleichbar mit den Flyway-Seed-Migrationen für Categories.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    public static final String DEMO_USER_EMAIL = "demo@finflow.dev";

    private final UserRepository userRepository;
    private final FinancialProfileService financialProfileService;
    private final AccountRepository accountRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;
    private final FinancialGoalService financialGoalService;
    private final ScenarioService scenarioService;

    public DevDataSeeder(
            UserRepository userRepository, FinancialProfileService financialProfileService,
            AccountRepository accountRepository, InsurancePolicyRepository insurancePolicyRepository,
            CategoryRepository categoryRepository, TransactionService transactionService,
            FinancialGoalService financialGoalService, ScenarioService scenarioService) {
        this.userRepository = userRepository;
        this.financialProfileService = financialProfileService;
        this.accountRepository = accountRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
        this.financialGoalService = financialGoalService;
        this.scenarioService = scenarioService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByEmail(DEMO_USER_EMAIL).isPresent()) {
            log.info("Dev seed skipped - demo user already exists");
            return;
        }

        UUID userId = userRepository.save(User.register(DEMO_USER_EMAIL, "not-a-real-hash", Role.USER)).id();

        financialProfileService.upsertProfile(userId, new UpsertFinancialProfileCommand(
                Money.of("3800"), Money.of("2900"), Money.of("9000"), Money.of("4000")));

        accountRepository.save(Account.open(userId, "Girokonto", AccountType.CHECKING, Money.of("2500")));
        accountRepository.save(Account.open(userId, "Tagesgeld", AccountType.SAVINGS, Money.of("8000")));
        accountRepository.save(Account.open(userId, "Depot", AccountType.INVESTMENT, Money.of("6000")));

        // Absichtlich nur 2 von 3 Kernrisiken abgedeckt - realistischerer Demo-Score als 100/100
        insurancePolicyRepository.save(InsurancePolicy.cover(
                userId, InsuranceType.LIABILITY, Money.of("5000000"), LocalDate.now().minusYears(2), null));
        insurancePolicyRepository.save(InsurancePolicy.cover(
                userId, InsuranceType.HOUSEHOLD, Money.of("50000"), LocalDate.now().minusYears(1), null));

        seedTransactions(userId);

        financialGoalService.createGoal(userId, new CreateFinancialGoalCommand(
                "Notgroschen aufstocken", Money.of("12000"), Money.of("9000"),
                LocalDate.now().plusYears(2), Money.of("300"), Percentage.ofFraction("0.02")));

        scenarioService.createScenario(userId, new CreateScenarioCommand(
                "Current Plan", Money.of("14500"), Money.of("500"), Percentage.ofFraction("0.05"),
                Percentage.ofFraction("0.02"), 20, null, null, null, Money.of("200000")));
        scenarioService.createScenario(userId, new CreateScenarioCommand(
                "Mehr sparen", Money.of("14500"), Money.of("750"), Percentage.ofFraction("0.05"),
                Percentage.ofFraction("0.02"), 20, null, null, null, Money.of("200000")));

        log.info("Dev seed created demo user {} ({})", DEMO_USER_EMAIL, userId);
    }

    private void seedTransactions(UUID userId) {
        Category gehalt = findCategory("Gehalt", TransactionType.INCOME);
        Category wohnen = findCategory("Wohnen", TransactionType.EXPENSE);
        Category lebensmittel = findCategory("Lebensmittel", TransactionType.EXPENSE);
        Category transport = findCategory("Transport", TransactionType.EXPENSE);
        Category freizeit = findCategory("Freizeit", TransactionType.EXPENSE);

        for (int monthsAgo = 2; monthsAgo >= 0; monthsAgo--) {
            LocalDate monthStart = LocalDate.now().minusMonths(monthsAgo).withDayOfMonth(1);

            record Entry(Category category, TransactionType type, String amount, int dayOfMonth, String description) {
            }

            List<Entry> entries = List.of(
                    new Entry(gehalt, TransactionType.INCOME, "3800", 1, "Gehalt"),
                    new Entry(wohnen, TransactionType.EXPENSE, "1100", 3, "Miete"),
                    new Entry(lebensmittel, TransactionType.EXPENSE, "320", 10, "Supermarkt"),
                    new Entry(lebensmittel, TransactionType.EXPENSE, "180", 22, "Supermarkt"),
                    new Entry(transport, TransactionType.EXPENSE, "120", 5, "Tankstelle"),
                    new Entry(freizeit, TransactionType.EXPENSE, "90", 18, "Restaurant"));

            for (Entry entry : entries) {
                int lastDayOfMonth = monthStart.lengthOfMonth();
                LocalDate bookedAt = monthStart.withDayOfMonth(Math.min(entry.dayOfMonth(), lastDayOfMonth));
                transactionService.createTransaction(userId, new CreateTransactionCommand(
                        Money.of(entry.amount()), entry.type(), entry.category().id(), bookedAt, entry.description()));
            }
        }
    }

    private Category findCategory(String name, TransactionType type) {
        return categoryRepository.findAll().stream()
                .filter(category -> category.name().equals(name) && category.type() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Expected category seeded by V11 migration: " + name + "/" + type));
    }
}
