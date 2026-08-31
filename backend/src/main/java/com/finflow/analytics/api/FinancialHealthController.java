package com.finflow.analytics.api;

import com.finflow.analytics.application.FinancialHealthService;
import com.finflow.identity.application.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/financial-health")
class FinancialHealthController {

    private final FinancialHealthService service;
    private final CurrentUserProvider currentUserProvider;

    FinancialHealthController(FinancialHealthService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<FinancialHealthScoreResponse> get() {
        var score = service.calculateScore(currentUserProvider.currentUserId());
        return ResponseEntity.ok(FinancialHealthMapper.toResponse(score));
    }
}
