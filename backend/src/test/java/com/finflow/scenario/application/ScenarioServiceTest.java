package com.finflow.scenario.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioNotFoundException;
import com.finflow.scenario.domain.ScenarioRepository;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    private static final UUID SCENARIO_ID = UUID.randomUUID();

    @Mock
    private ScenarioRepository repository;

    private ScenarioService service;

    @BeforeEach
    void setUp() {
        service = new ScenarioService(repository);
    }

    private CreateScenarioCommand command() {
        return new CreateScenarioCommand(
                "Current Plan", Money.of("5000"), Money.of("500"), Percentage.ofFraction("0.04"),
                Percentage.ofFraction("0.02"), 20, null, null, null, null);
    }

    @Test
    void createsScenarioAndPersistsIt() {
        when(repository.save(any(Scenario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scenario result = service.createScenario(USER_ID, command());

        assertThat(result.userId()).isEqualTo(USER_ID);
    }

    @Test
    void getScenarioThrowsWhenNotFoundOrNotOwned() {
        when(repository.findByIdAndUserId(SCENARIO_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getScenario(OTHER_USER_ID, SCENARIO_ID))
                .isInstanceOf(ScenarioNotFoundException.class);
    }

    @Test
    void calculateScenarioReturnsProjection() {
        Scenario scenario = Scenario.create(
                USER_ID, "Ziel", Money.of("1000"), Money.of("100"), Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, null);
        when(repository.findByIdAndUserId(SCENARIO_ID, USER_ID)).thenReturn(Optional.of(scenario));

        var result = service.calculateScenario(USER_ID, SCENARIO_ID);

        assertThat(result.projectedCapital()).isEqualTo(Money.of("2200"));
    }

    @Test
    void compareScenariosCalculatesEachOneIndependently() {
        Scenario currentPlan = Scenario.create(
                USER_ID, "Current Plan", Money.of("1000"), Money.of("100"), Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, null);
        Scenario scenarioB = Scenario.create(
                USER_ID, "Scenario B", Money.of("1000"), Money.of("200"), Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, null);
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        when(repository.findByIdAndUserId(idA, USER_ID)).thenReturn(Optional.of(currentPlan));
        when(repository.findByIdAndUserId(idB, USER_ID)).thenReturn(Optional.of(scenarioB));

        List<ScenarioComparison> comparisons = service.compareScenarios(USER_ID, List.of(idA, idB));

        assertThat(comparisons).hasSize(2);
        assertThat(comparisons.get(0).result().projectedCapital()).isEqualTo(Money.of("2200"));
        assertThat(comparisons.get(1).result().projectedCapital()).isEqualTo(Money.of("3400"));
    }
}
