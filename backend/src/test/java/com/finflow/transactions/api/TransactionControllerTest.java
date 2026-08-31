package com.finflow.transactions.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.shared.Money;
import com.finflow.transactions.application.TransactionService;
import com.finflow.transactions.domain.CategoryNotFoundException;
import com.finflow.transactions.domain.Transaction;
import com.finflow.transactions.domain.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void createReturns201WithBody() throws Exception {
        Transaction transaction = Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("50"), TransactionType.EXPENSE, LocalDate.now(), "Einkauf");
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.createTransaction(eq(USER_ID), any())).thenReturn(transaction);

        String body = """
                {"amount": 50, "type": "EXPENSE", "categoryId": "%s", "bookedAt": "%s", "description": "Einkauf"}
                """.formatted(CATEGORY_ID, LocalDate.now());

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void createRejectsNegativeAmount() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);

        String body = """
                {"amount": -50, "type": "EXPENSE", "categoryId": "%s", "bookedAt": "%s"}
                """.formatted(CATEGORY_ID, LocalDate.now());

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createReturns404WhenCategoryMissing() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.createTransaction(eq(USER_ID), any())).thenThrow(new CategoryNotFoundException(CATEGORY_ID));

        String body = """
                {"amount": 50, "type": "EXPENSE", "categoryId": "%s", "bookedAt": "%s"}
                """.formatted(CATEGORY_ID, LocalDate.now());

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsTransactionsForCurrentUser() throws Exception {
        Transaction transaction = Transaction.record(
                USER_ID, CATEGORY_ID, Money.of("50"), TransactionType.EXPENSE, LocalDate.now(), null);
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.listTransactions(USER_ID, null, null)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryId").value(CATEGORY_ID.toString()));
    }

    @Test
    void listRejectsOnlyFromParam() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.listTransactions(USER_ID, LocalDate.now(), null))
                .thenThrow(new IllegalArgumentException("from and to must both be provided together"));

        mockMvc.perform(get("/api/v1/transactions").param("from", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }
}
