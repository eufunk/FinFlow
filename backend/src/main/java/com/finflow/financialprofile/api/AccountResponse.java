package com.finflow.financialprofile.api;

import com.finflow.financialprofile.domain.AccountType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(UUID id, String name, AccountType type, BigDecimal balance, Instant updatedAt) {
}
