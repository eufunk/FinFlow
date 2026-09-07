package com.finflow.scenario.application;

import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioNotFoundException;
import com.finflow.scenario.domain.ScenarioProjectionResult;
import com.finflow.scenario.domain.ScenarioRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    private final ScenarioRepository repository;

    public ScenarioService(ScenarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Scenario createScenario(UUID userId, CreateScenarioCommand command) {
        Scenario scenario = Scenario.create(
                userId, command.name(), command.currentCapital(), command.monthlySavings(),
                command.annualReturn(), command.inflation(), command.durationInYears(),
                command.monthlyIncome(), command.incomeGrowth(), command.expensesGrowth(),
                command.targetCapital());

        Scenario saved = repository.save(scenario);
        log.info("Scenario {} created for user {}", saved.id(), userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Scenario> listScenarios(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Scenario getScenario(UUID userId, UUID scenarioId) {
        return repository.findByIdAndUserId(scenarioId, userId)
                .orElseThrow(() -> new ScenarioNotFoundException(scenarioId));
    }

    @Transactional(readOnly = true)
    public ScenarioProjectionResult calculateScenario(UUID userId, UUID scenarioId) {
        return getScenario(userId, scenarioId).project(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ScenarioComparison> compareScenarios(UUID userId, List<UUID> scenarioIds) {
        LocalDate today = LocalDate.now();
        List<ScenarioComparison> comparisons = new ArrayList<>();
        for (UUID scenarioId : scenarioIds) {
            Scenario scenario = getScenario(userId, scenarioId);
            comparisons.add(new ScenarioComparison(scenario, scenario.project(today)));
        }
        return comparisons;
    }
}
