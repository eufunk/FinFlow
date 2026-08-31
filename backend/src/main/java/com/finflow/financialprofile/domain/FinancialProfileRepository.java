package com.finflow.financialprofile.domain;

import java.util.Optional;
import java.util.UUID;

/** Port: von application genutzt, Implementierung liegt in infrastructure. */
public interface FinancialProfileRepository {

    Optional<FinancialProfile> findByUserId(UUID userId);

    FinancialProfile save(FinancialProfile profile);
}
