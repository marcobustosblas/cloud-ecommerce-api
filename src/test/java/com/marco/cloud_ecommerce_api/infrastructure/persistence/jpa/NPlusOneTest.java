package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
@ActiveProfiles("test")
public class NPlusOneTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Demostrar el problema N+1")
    void demonstrateNPlusOneProblem() {
        LocalDateTime now = LocalDateTime.now();

        // Creo 5 categorías y 5 productos usando el constructor completo
        for (int i = 0; i < 5; i++) {
            CategoryJpaEntity cat = categoryRepository.save(new CategoryJpaEntity(
                    UUID.randomUUID(),
                    "Cat" + i,
                    "Description for generic category " + i,
                    true,
                    now,
                    now
            ));

            ProductJpaEntity product = new ProductJpaEntity(
                    "SKU-" + i,
                    "Product " + i,
                    "Description " + i,
                    new BigDecimal("100.0"),
                    "http://image.com/" + i,
                    ProductStatus.ACTIVE,
                    cat
            );

            productRepository.save(product);
        }

        entityManager.flush();
        entityManager.clear();

        System.out.println("\n=== [INICIO] EJECUTANDO CONSULTA findAll() ===");
        List<ProductJpaEntity> products = productRepository.findAll();

        System.out.println("\n=== [PASO] ACCEDIENDO A LAS CATEGORÍAS (Disparador de N) ===");
        for (ProductJpaEntity p : products) {
            String categoryName = p.getCategory().getName();
            System.out.println("-> Product: " + p.getName() + " | Categoría: " + categoryName);
        }
        System.out.println("\n=== [FIN] REVISAR LOGS ARRIBA ===");

        assertThat(products).hasSize(5);
    }
}