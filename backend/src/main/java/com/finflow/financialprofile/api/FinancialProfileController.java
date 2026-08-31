package com.finflow.financialprofile.api;

import com.finflow.financialprofile.application.FinancialProfileService;
import com.finflow.identity.application.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nur Orchestrierung (Request entgegennehmen, validieren, Service aufrufen, DTO zurückgeben) -
 * keine Business-Logik hier, siehe Backend-Architektur in Phase 3.
 */
@RestController
@RequestMapping("/api/v1/profile")
class FinancialProfileController {

    private final FinancialProfileService service;
    private final CurrentUserProvider currentUserProvider;

    FinancialProfileController(FinancialProfileService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<FinancialProfileResponse> getProfile() {
        var profile = service.getProfile(currentUserProvider.currentUserId());
        return ResponseEntity.ok(FinancialProfileMapper.toResponse(profile));
    }

    @PutMapping
    ResponseEntity<FinancialProfileResponse> upsertProfile(@Valid @RequestBody UpsertFinancialProfileRequest request) {
        var profile = service.upsertProfile(
                currentUserProvider.currentUserId(), FinancialProfileMapper.toCommand(request));
        return ResponseEntity.ok(FinancialProfileMapper.toResponse(profile));
    }
}
