package com.finflow.scenario.api;

import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.scenario.application.ScenarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scenarios")
class ScenarioController {

    private final ScenarioService service;
    private final CurrentUserProvider currentUserProvider;

    ScenarioController(ScenarioService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<List<ScenarioResponse>> list() {
        var scenarios = service.listScenarios(currentUserProvider.currentUserId());
        return ResponseEntity.ok(scenarios.stream().map(ScenarioMapper::toResponse).toList());
    }

    @PostMapping
    ResponseEntity<ScenarioResponse> create(@Valid @RequestBody CreateScenarioRequest request) {
        var scenario = service.createScenario(currentUserProvider.currentUserId(), ScenarioMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ScenarioMapper.toResponse(scenario));
    }

    @GetMapping("/{id}/result")
    ResponseEntity<ScenarioResultResponse> result(@PathVariable UUID id) {
        UUID userId = currentUserProvider.currentUserId();
        var scenario = service.getScenario(userId, id);
        var result = service.calculateScenario(userId, id);
        return ResponseEntity.ok(ScenarioMapper.toResultResponse(scenario, result));
    }

    @GetMapping("/compare")
    ResponseEntity<List<ScenarioResultResponse>> compare(@RequestParam List<UUID> ids) {
        var comparisons = service.compareScenarios(currentUserProvider.currentUserId(), ids);
        return ResponseEntity.ok(comparisons.stream()
                .map(comparison -> ScenarioMapper.toResultResponse(comparison.scenario(), comparison.result()))
                .toList());
    }
}
