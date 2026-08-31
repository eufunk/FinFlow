package com.finflow.analytics.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.analytics.application.FinancialHealthService;
import com.finflow.analytics.domain.FinancialHealthCalculator;
import com.finflow.analytics.domain.FinancialHealthInput;
import com.finflow.financialprofile.domain.FinancialProfileNotFoundException;
import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.shared.Money;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FinancialHealthController.class)
class FinancialHealthControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FinancialHealthService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void getReturnsScoreWithAllCategories() throws Exception {
        var input = new FinancialHealthInput(
                Money.of("3000"), Money.of("2400"), Money.of("14400"), Money.ZERO,
                3, Money.of("3000"), Money.of("10000"), 4);
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.calculateScore(USER_ID)).thenReturn(FinancialHealthCalculator.calculate(input));

        mockMvc.perform(get("/api/v1/financial-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(100))
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.emergencyFund.points").value(20))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void getReturns404WhenProfileMissing() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.calculateScore(USER_ID)).thenThrow(new FinancialProfileNotFoundException(USER_ID));

        mockMvc.perform(get("/api/v1/financial-health"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}
