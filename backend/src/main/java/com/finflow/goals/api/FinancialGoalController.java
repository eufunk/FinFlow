package com.finflow.goals.api;

import com.finflow.goals.application.FinancialGoalService;
import com.finflow.identity.application.CurrentUserProvider;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goals")
class FinancialGoalController {

    private final FinancialGoalService service;
    private final CurrentUserProvider currentUserProvider;

    FinancialGoalController(FinancialGoalService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<List<FinancialGoalResponse>> list() {
        var goals = service.listGoals(currentUserProvider.currentUserId());
        return ResponseEntity.ok(goals.stream().map(FinancialGoalMapper::toResponse).toList());
    }

    @PostMapping
    ResponseEntity<FinancialGoalResponse> create(@Valid @RequestBody CreateFinancialGoalRequest request) {
        var goal = service.createGoal(currentUserProvider.currentUserId(), FinancialGoalMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(FinancialGoalMapper.toResponse(goal));
    }
}
