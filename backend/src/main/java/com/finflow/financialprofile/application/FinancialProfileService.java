package com.finflow.financialprofile.application;

import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.financialprofile.domain.FinancialProfileNotFoundException;
import com.finflow.financialprofile.domain.FinancialProfileRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialProfileService {

    private static final Logger log = LoggerFactory.getLogger(FinancialProfileService.class);

    private final FinancialProfileRepository repository;

    public FinancialProfileService(FinancialProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public FinancialProfile getProfile(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new FinancialProfileNotFoundException(userId));
    }

    @Transactional
    public FinancialProfile upsertProfile(UUID userId, UpsertFinancialProfileCommand command) {
        FinancialProfile profile = repository.findByUserId(userId)
                .map(existing -> {
                    existing.update(
                            command.monthlyIncome(), command.monthlyExpenses(),
                            command.emergencyFund(), command.totalDebt());
                    return existing;
                })
                .orElseGet(() -> FinancialProfile.create(
                        userId, command.monthlyIncome(), command.monthlyExpenses(),
                        command.emergencyFund(), command.totalDebt()));

        FinancialProfile saved = repository.save(profile);
        log.info("Financial profile upserted for user {}", userId);
        return saved;
    }
}
