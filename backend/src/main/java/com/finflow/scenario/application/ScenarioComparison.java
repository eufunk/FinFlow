package com.finflow.scenario.application;

import com.finflow.scenario.domain.Scenario;
import com.finflow.scenario.domain.ScenarioProjectionResult;

public record ScenarioComparison(Scenario scenario, ScenarioProjectionResult result) {
}
