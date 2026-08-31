package com.finflow.insurance.domain;

import java.util.List;
import java.util.UUID;

public interface InsurancePolicyRepository {

    List<InsurancePolicy> findByUserId(UUID userId);

    InsurancePolicy save(InsurancePolicy policy);
}
