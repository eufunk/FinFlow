package com.finflow.goals.infrastructure;

import com.finflow.goals.domain.FinancialGoal;
import com.finflow.goals.domain.FinancialGoalRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FinancialGoalJpaRepository extends FinancialGoalRepository, JpaRepository<FinancialGoal, UUID> {

    @Override
    List<FinancialGoal> findByUserId(UUID userId);
}
