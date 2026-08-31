package com.finflow.financialprofile.application;

import com.finflow.financialprofile.domain.Account;
import com.finflow.financialprofile.domain.AccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Sanktionierter Zugriffspunkt für andere Module (z. B. analytics) - siehe Backend-Architektur. */
@Service
public class AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<Account> listAccounts(UUID userId) {
        return accountRepository.findByUserId(userId);
    }
}
