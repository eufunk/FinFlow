package com.finflow.goals.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.goals.application.FinancialGoalService;
import com.finflow.goals.domain.FinancialGoal;
import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.shared.Money;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FinancialGoalController.class)
class FinancialGoalControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FinancialGoalService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void createReturns201WithProjection() throws Exception {
        FinancialGoal goal = FinancialGoal.create(
                USER_ID, "Notgroschen", Money.of("6000"), Money.of("1000"), null, Money.of("200"), null);
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.createGoal(eq(USER_ID), any())).thenReturn(goal);

        String body = """
                {"name": "Notgroschen", "targetAmount": 6000, "currentAmount": 1000, "monthlyContribution": 200}
                """;

        mockMvc.perform(post("/api/v1/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Notgroschen"))
                .andExpect(jsonPath("$.achieved").value(false))
                .andExpect(jsonPath("$.estimatedAchievementDate").exists());
    }

    @Test
    void createRejectsBlankName() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);

        String body = """
                {"name": "", "targetAmount": 6000, "currentAmount": 1000, "monthlyContribution": 200}
                """;

        mockMvc.perform(post("/api/v1/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void createRejectsTargetDateInThePast() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);

        String body = """
                {"name": "Ziel", "targetAmount": 6000, "currentAmount": 1000, "targetDate": "2020-01-01", "monthlyContribution": 200}
                """;

        mockMvc.perform(post("/api/v1/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsGoalsForCurrentUser() throws Exception {
        FinancialGoal goal = FinancialGoal.create(
                USER_ID, "Notgroschen", Money.of("6000"), Money.of("6000"), null, Money.ZERO, null);
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.listGoals(USER_ID)).thenReturn(List.of(goal));

        mockMvc.perform(get("/api/v1/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].achieved").value(true));
    }
}
