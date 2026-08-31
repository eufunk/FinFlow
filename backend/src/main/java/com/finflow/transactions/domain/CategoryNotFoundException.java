package com.finflow.transactions.domain;

import com.finflow.shared.NotFoundException;
import java.util.UUID;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found: " + categoryId);
    }
}
