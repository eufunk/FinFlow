package com.finflow.goals.application;

import com.finflow.goals.domain.FinancialGoal;
import com.finflow.goals.domain.FinancialGoalRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialGoalService {

    private static final Logger log = LoggerFactory.getLogger(FinancialGoalService.class);

    private final FinancialGoalRepository repository;

    public FinancialGoalService(FinancialGoalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FinancialGoal createGoal(UUID userId, CreateFinancialGoalCommand command) {
        FinancialGoal goal = FinancialGoal.create(
                userId, command.name(), command.targetAmount(), command.currentAmount(),
                command.targetDate(), command.monthlyContribution(), command.expectedAnnualReturn());

        FinancialGoal saved = repository.save(goal);
        log.info("Financial goal {} created for user {}", saved.id(), userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<FinancialGoal> listGoals(UUID userId) {
        return repository.findByUserId(userId);
    }
}
