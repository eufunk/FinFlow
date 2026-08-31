package com.finflow.transactions.api;

import com.finflow.transactions.application.CategoryQueryService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Schreibgeschützt: Kategorien werden in diesem Slice nur per Flyway-Seed (V11) gepflegt. */
@RestController
@RequestMapping("/api/v1/categories")
class CategoryController {

    private final CategoryQueryService service;

    CategoryController(CategoryQueryService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<CategoryResponse>> list() {
        return ResponseEntity.ok(service.listCategories().stream().map(CategoryMapper::toResponse).toList());
    }
}
