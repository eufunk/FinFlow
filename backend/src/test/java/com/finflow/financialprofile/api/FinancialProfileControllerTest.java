package com.finflow.financialprofile.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.financialprofile.application.FinancialProfileService;
import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.financialprofile.domain.FinancialProfileNotFoundException;
import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.shared.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FinancialProfileController.class)
class FinancialProfileControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FinancialProfileService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void getProfileReturnsOkWithBody() throws Exception {
        FinancialProfile profile = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.getProfile(USER_ID)).thenReturn(profile);

        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID.toString()))
                .andExpect(jsonPath("$.monthlyIncome").value(3000.0));
    }

    @Test
    void getProfileReturns404WhenMissing() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.getProfile(USER_ID)).thenThrow(new FinancialProfileNotFoundException(USER_ID));

        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void putProfileRejectsNegativeMonthlyIncome() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        String body = """
                {"monthlyIncome": -1, "monthlyExpenses": 2000, "emergencyFund": 6000, "totalDebt": 0}
                """;

        mockMvc.perform(put("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void putProfileReturnsUpdatedProfile() throws Exception {
        FinancialProfile updated = FinancialProfile.create(
                USER_ID, Money.of("4000"), Money.of("2500"), Money.of("7000"), Money.of("100"));
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.upsertProfile(eq(USER_ID), any())).thenReturn(updated);

        String body = """
                {"monthlyIncome": 4000, "monthlyExpenses": 2500, "emergencyFund": 7000, "totalDebt": 100}
                """;

        mockMvc.perform(put("/api/v1/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyIncome").value(4000.0));
    }
}
