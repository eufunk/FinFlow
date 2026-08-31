package com.finflow.transactions.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finflow.transactions.application.CategoryQueryService;
import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.TransactionType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryQueryService service;

    @Test
    void listReturnsAllCategories() throws Exception {
        Category category = org.mockito.Mockito.mock(Category.class);
        when(category.id()).thenReturn(UUID.randomUUID());
        when(category.name()).thenReturn("Gehalt");
        when(category.type()).thenReturn(TransactionType.INCOME);
        when(service.listCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gehalt"))
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }
}
