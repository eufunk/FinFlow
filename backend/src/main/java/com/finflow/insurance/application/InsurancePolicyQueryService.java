package com.finflow.insurance.application;

import com.finflow.insurance.domain.InsurancePolicy;
import com.finflow.insurance.domain.InsurancePolicyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sanktionierter Zugriffspunkt für andere Module (z. B. analytics) - siehe Backend-Architektur. */
@Service
public class InsurancePolicyQueryService {

    private final InsurancePolicyRepository repository;

    public InsurancePolicyQueryService(InsurancePolicyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<InsurancePolicy> listPolicies(UUID userId) {
        return repository.findByUserId(userId);
    }
}
