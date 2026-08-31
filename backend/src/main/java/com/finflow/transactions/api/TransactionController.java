package com.finflow.transactions.api;

import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.transactions.application.TransactionService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController {

    private final TransactionService service;
    private final CurrentUserProvider currentUserProvider;

    TransactionController(TransactionService service, CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    ResponseEntity<List<TransactionResponse>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var transactions = service.listTransactions(currentUserProvider.currentUserId(), from, to);
        return ResponseEntity.ok(transactions.stream().map(TransactionMapper::toResponse).toList());
    }

    @PostMapping
    ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        var transaction = service.createTransaction(
                currentUserProvider.currentUserId(), TransactionMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionMapper.toResponse(transaction));
    }
}
