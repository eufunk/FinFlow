package com.finflow.financialprofile.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.financialprofile.application.AccountQueryService;
import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountType;
import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.shared.Money;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountQueryService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void listReturnsAccountsForCurrentUser() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.listAccounts(USER_ID)).thenReturn(List.of(
                Account.open(USER_ID, "Girokonto", AccountType.CHECKING, Money.of("2500"))));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Girokonto"))
                .andExpect(jsonPath("$[0].type").value("CHECKING"))
                .andExpect(jsonPath("$[0].balance").value(2500.0));
    }
}
