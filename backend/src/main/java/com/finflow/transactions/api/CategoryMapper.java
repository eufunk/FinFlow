package com.finflow.transactions.api;

import com.finflow.transactions.domain.Category;

final class CategoryMapper {

    private CategoryMapper() {
    }

    static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.id(), category.name(), category.type());
    }
}
