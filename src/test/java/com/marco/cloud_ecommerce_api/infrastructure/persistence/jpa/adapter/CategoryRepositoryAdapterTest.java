package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional // Fundamental para evitar el error de "transaction" que vi antes
public class CategoryRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CategoryRepositoryAdapter categoryRepository;

    @Test
    void shouldSaveAndFindCategory() {
        // 1. Preparar el dato
        Category category = new Category("Test Category");

        // 2. Ejecutar la acción
        Category saved = categoryRepository.save(category);

        // 3. Verificar
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

        var found = categoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Category");
    }

    @Test
    void shouldFindCategoryByName() {
        Category category = new Category("Clothing");
        categoryRepository.save(category);

        Category found = categoryRepository.findByName("Clothing").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Clothing");
    }

    @Test
    void shouldCheckIfNameExists() {
        Category category = new Category("Toys");
        categoryRepository.save(category);

        assertThat(categoryRepository.existsByName("Toys")).isTrue();
        assertThat(categoryRepository.existsByName("NonExistent")).isFalse();
    }
}