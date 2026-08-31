package com.finflow.insurance.infrastructure;

import com.finflow.insurance.domain.InsurancePolicy;
import com.finflow.insurance.domain.InsurancePolicyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InsurancePolicyJpaRepository extends InsurancePolicyRepository, JpaRepository<InsurancePolicy, UUID> {

    @Override
    List<InsurancePolicy> findByUserId(UUID userId);
}
