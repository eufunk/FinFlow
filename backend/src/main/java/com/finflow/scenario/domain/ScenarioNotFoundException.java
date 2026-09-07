package com.finflow.scenario.domain;

import com.finflow.shared.NotFoundException;
import java.util.UUID;

/** Wird auch geworfen, wenn das Szenario existiert, aber einem anderen User gehört (kein Leak von Existenz). */
public class ScenarioNotFoundException extends NotFoundException {

    public ScenarioNotFoundException(UUID scenarioId) {
        super("Scenario not found: " + scenarioId);
    }
}
