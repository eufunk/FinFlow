package com.finflow.financialprofile.domain;

import com.finflow.shared.NotFoundException;
import java.util.UUID;

public class FinancialProfileNotFoundException extends NotFoundException {

    public FinancialProfileNotFoundException(UUID userId) {
        super("Financial profile not found for user " + userId);
    }
}
