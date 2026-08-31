package com.finflow.transactions.infrastructure;

import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.CategoryRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CategoryJpaRepository extends CategoryRepository, JpaRepository<Category, UUID> {
}
