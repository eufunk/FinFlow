package com.finflow.financialprofile.api;

import com.finflow.financialprofile.application.AccountQueryService;
import com.finflow.identity.application.CurrentUserProvider;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Schreibgeschützt: Accounts werden in diesem Slice nur intern (Dev-Seed) angelegt, siehe Account-Klassenkommentar. */
@RestController
@RequestMapping("/api/v1/accounts")
class AccountController {

    private final AccountQueryService service;
    private final CurrentUserProvider currentUserProvider;

    AccountController(AccountQueryService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<List<AccountResponse>> list() {
        var accounts = service.listAccounts(currentUserProvider.currentUserId());
        return ResponseEntity.ok(accounts.stream()
                .map(account -> new AccountResponse(
                        account.id(), account.name(), account.type(), account.balance().amount(), account.updatedAt()))
                .toList());
    }
}
