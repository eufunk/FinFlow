package com.finflow.financialprofile.infrastructure;

import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface AccountJpaRepository extends AccountRepository, JpaRepository<Account, UUID> {

    @Override
    List<Account> findByUserId(UUID userId);
}
