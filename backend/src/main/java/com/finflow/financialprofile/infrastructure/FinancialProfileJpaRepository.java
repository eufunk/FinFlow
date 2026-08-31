package com.finflow.financialprofile.infrastructure;

import com.finflow.financialprofile.domain.FinancialProfile;
import com.finflow.financialprofile.domain.FinancialProfileRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface FinancialProfileJpaRepository extends FinancialProfileRepository, JpaRepository<FinancialProfile, UUID> {

    @Override
    Optional<FinancialProfile> findByUserId(UUID userId);
}
