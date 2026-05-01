package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class ProductRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProductRepositoryAdapter productRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    private CategoryJpaEntity testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new CategoryJpaEntity("Electronics");
        testCategory = categoryJpaRepository.save(testCategory);
    }

    @Test
    void shouldSaveAndFindProduct() {
        Product product = new Product(
                "TEST-SKU-001",
                "Test Name Product",
                "Test Description",
                new BigDecimal("99.99"),
                testCategory.getId(),
                null,
                10
        );
        product.activate();

        Product saved = productRepository.save(product);
        Product found = productRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getSku()).isEqualTo("TEST-SKU-001");
        assertThat(found.getName()).isEqualTo("Test Name Product");
        assertThat(found.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(found.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void shouldFindProductBySku() {
        Product product = new Product(
                "TEST-SKU-002",
                "Another Product",
                "Description",
                new BigDecimal("49.99"),
                testCategory.getId(),
                null,
                5
        );
        productRepository.save(product);

        Product found = productRepository.findBySku("TEST-SKU-002").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getSku()).isEqualTo("TEST-SKU-002");
    }

    @Test
    void shouldCheckIfSkuExists() {
        Product product = new Product(
                "TEST-SKU-003",
                "Unique Product",
                "Description",
                new BigDecimal("29.99"),
                testCategory.getId(),
                null,
                3
        );
        productRepository.save(product);

        assertThat(productRepository.existsBySku("TEST-SKU-003")).isTrue();
        assertThat(productRepository.existsBySku("NON-EXISTENT")).isFalse();
    }

}
