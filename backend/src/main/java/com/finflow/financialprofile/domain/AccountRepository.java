package com.finflow.financialprofile.domain;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    List<Account> findByUserId(UUID userId);

    Account save(Account account);
}
