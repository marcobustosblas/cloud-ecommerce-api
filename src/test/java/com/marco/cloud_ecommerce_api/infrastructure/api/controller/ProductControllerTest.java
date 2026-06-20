package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ProductController productController;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @BeforeEach
    public void setUp() {
        // 🌟 Inicializamos MockMvc manualmente inyectando el resolvedor de Pageable de Spring Data
        // Esto soluciona CUALQUIER error 400 relacionado con parámetros de paginación en los tests
        this.mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        // 1. Limpieza
        productJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();

        // 2. Crear categoría
        CategoryJpaEntity category = new CategoryJpaEntity("Tecnología");
        category = categoryJpaRepository.save(category);

        // 3. Crear Producto 1
        ProductJpaEntity p1 = new ProductJpaEntity(
                "PROD-001",
                "Laptop Gaming",
                "Laptop con tarjeta RTX",
                new BigDecimal("1500.00"),
                "http://image.com/laptop",
                ProductStatus.ACTIVE,
                category
        );
        productJpaRepository.save(p1);

        // 4. Crear Producto 2
        ProductJpaEntity p2 = new ProductJpaEntity(
                "PROD-002",
                "Mouse Gamer",
                "Mouse ergonómico",
                new BigDecimal("50.00"),
                "http://image.com/mouse",
                ProductStatus.ACTIVE,
                category
        );
        productJpaRepository.save(p2);

        productJpaRepository.flush();
        categoryJpaRepository.flush();
    }

    @Test
    void shouldReturnPaginatedAndSortedProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc") // Formato directo perfecto
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[*].name", hasItems("Laptop Gaming", "Mouse Gamer")));
    }
}