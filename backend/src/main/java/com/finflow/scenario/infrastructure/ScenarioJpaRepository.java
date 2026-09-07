package com.finflow.scenario.infrastructure;

import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ScenarioJpaRepository extends ScenarioRepository, JpaRepository<Scenario, UUID> {

    @Override
    List<Scenario> findByUserId(UUID userId);

    @Override
    Optional<Scenario> findByIdAndUserId(UUID id, UUID userId);
}
