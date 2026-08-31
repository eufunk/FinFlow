package com.finflow.goals.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.finflow.goals.domain.FinancialGoal;
import com.finflow.goals.domain.FinancialGoalRepository;
import com.finflow.shared.Money;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinancialGoalServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FinancialGoalRepository repository;

    private FinancialGoalService service;

    @BeforeEach
    void setUp() {
        service = new FinancialGoalService(repository);
    }

    @Test
    void createsGoalAndPersistsIt() {
        when(repository.save(any(FinancialGoal.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var command = new CreateFinancialGoalCommand(
                "Notgroschen", Money.of("6000"), Money.of("1000"), null, Money.of("200"), null);

        FinancialGoal result = service.createGoal(USER_ID, command);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.name()).isEqualTo("Notgroschen");
        verify(repository).save(any(FinancialGoal.class));
    }

    @Test
    void listsGoalsForUser() {
        when(repository.findByUserId(USER_ID)).thenReturn(List.of());

        service.listGoals(USER_ID);

        verify(repository).findByUserId(USER_ID);
    }
}
