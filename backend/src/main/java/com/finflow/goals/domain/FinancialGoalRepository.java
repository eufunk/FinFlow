package com.finflow.goals.domain;

import java.util.List;
import java.util.UUID;

public interface FinancialGoalRepository {

    List<FinancialGoal> findByUserId(UUID userId);

    FinancialGoal save(FinancialGoal goal);
}
