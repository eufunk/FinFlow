package com.finflow.scenario.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScenarioRepository {

    List<Scenario> findByUserId(UUID userId);

    /** Ownership-Check auf Query-Ebene statt nachträglicher Prüfung (siehe Edge Case "fremde Ressource per ID"). */
    Optional<Scenario> findByIdAndUserId(UUID id, UUID userId);

    Scenario save(Scenario scenario);
}
