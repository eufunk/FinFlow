package com.finflow.financialprofile.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.financialprofile.domain.FinancialProfileNotFoundException;
import com.finflow.financialprofile.domain.FinancialProfileRepository;
import com.finflow.shared.Money;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FinancialProfileServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private FinancialProfileRepository repository;

    private FinancialProfileService service;

    @BeforeEach
    void setUp() {
        service = new FinancialProfileService(repository);
    }

    @Test
    void getProfileReturnsExistingProfile() {
        FinancialProfile existing = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        FinancialProfile result = service.getProfile(USER_ID);

        assertThat(result).isEqualTo(existing);
    }

    @Test
    void getProfileThrowsWhenNotFound() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProfile(USER_ID))
                .isInstanceOf(FinancialProfileNotFoundException.class);
    }

    @Test
    void upsertCreatesNewProfileWhenNoneExists() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(repository.save(any(FinancialProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new UpsertFinancialProfileCommand(
                Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));

        FinancialProfile result = service.upsertProfile(USER_ID, command);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.monthlyIncome()).isEqualTo(Money.of("3000"));
        verify(repository).save(any(FinancialProfile.class));
    }

    @Test
    void upsertUpdatesExistingProfileInPlace() {
        FinancialProfile existing = FinancialProfile.create(
                USER_ID, Money.of("3000"), Money.of("2000"), Money.of("6000"), Money.of("0"));
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(FinancialProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var command = new UpsertFinancialProfileCommand(
                Money.of("4000"), Money.of("2500"), Money.of("7000"), Money.of("100"));

        FinancialProfile result = service.upsertProfile(USER_ID, command);

        assertThat(result.id()).isEqualTo(existing.id());
        assertThat(result.monthlyIncome()).isEqualTo(Money.of("4000"));
    }
}
