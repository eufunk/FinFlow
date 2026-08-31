package com.finflow.transactions.api;

import com.finflow.transactions.domain.TransactionType;
import java.util.UUID;

public record CategoryResponse(UUID id, String name, TransactionType type) {
}
