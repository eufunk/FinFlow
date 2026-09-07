package com.finflow.scenario.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.identity.application.CurrentUserProvider;
import com.finflow.scenario.application.ScenarioComparison;
import com.finflow.scenario.application.ScenarioService;
import com.finflow.scenario.domain.Scenario;
import com.finflow.shared.Money;
import com.finflow.shared.Percentage;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ScenarioController.class)
class ScenarioControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScenarioService service;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    private Scenario scenario(UUID id, String name, String monthlySavings) {
        Scenario s = Scenario.create(
                USER_ID, name, Money.of("1000"), Money.of(monthlySavings), Percentage.ZERO, Percentage.ZERO, 1,
                null, null, null, null);
        return s;
    }

    @Test
    void createReturns201() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.createScenario(eq(USER_ID), any())).thenReturn(scenario(UUID.randomUUID(), "Current Plan", "500"));

        String body = """
                {"name": "Current Plan", "currentCapital": 1000, "monthlySavings": 500,
                 "annualReturn": 0.04, "inflation": 0.02, "durationInYears": 20}
                """;

        mockMvc.perform(post("/api/v1/scenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Current Plan"));
    }

    @Test
    void createRejectsDurationBeyondHundredYears() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);

        String body = """
                {"name": "Zu lang", "currentCapital": 1000, "monthlySavings": 500,
                 "annualReturn": 0.04, "inflation": 0.02, "durationInYears": 500}
                """;

        mockMvc.perform(post("/api/v1/scenarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsScenariosForCurrentUser() throws Exception {
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.listScenarios(USER_ID)).thenReturn(List.of(scenario(UUID.randomUUID(), "Current Plan", "500")));

        mockMvc.perform(get("/api/v1/scenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Current Plan"));
    }

    @Test
    void resultReturnsCalculatedProjection() throws Exception {
        UUID scenarioId = UUID.randomUUID();
        Scenario s = scenario(scenarioId, "Current Plan", "100");
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.getScenario(USER_ID, scenarioId)).thenReturn(s);
        when(service.calculateScenario(USER_ID, scenarioId)).thenReturn(s.project(LocalDate.now()));

        mockMvc.perform(get("/api/v1/scenarios/" + scenarioId + "/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectedCapital").value(2200.0))
                .andExpect(jsonPath("$.yearlyDevelopment").isArray());
    }

    @Test
    void compareBindsCommaSeparatedIdsAndReturnsBothResults() throws Exception {
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();
        Scenario a = scenario(idA, "Current Plan", "500");
        Scenario b = scenario(idB, "Scenario B", "750");
        when(currentUserProvider.currentUserId()).thenReturn(USER_ID);
        when(service.compareScenarios(USER_ID, List.of(idA, idB))).thenReturn(List.of(
                new ScenarioComparison(a, a.project(LocalDate.now())),
                new ScenarioComparison(b, b.project(LocalDate.now()))));

        mockMvc.perform(get("/api/v1/scenarios/compare").param("ids", idA + "," + idB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scenarioName").value("Current Plan"))
                .andExpect(jsonPath("$[1].scenarioName").value("Scenario B"));
    }
}
