package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductSoftDeleteTest {

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe marcar el producto como borrado pero mantener el registro en la DB")
    void shouldSoftDeleteProduct() {
        LocalDateTime now = LocalDateTime.now();
        CategoryJpaEntity category = categoryRepository.save(new CategoryJpaEntity(
                UUID.randomUUID(),
                "Test Category",
                "Description for soft delete test tracking",
                true,
                now,
                now
        ));

        ProductJpaEntity product = new ProductJpaEntity(
                "SOFT-DELETE-SKU",
                "Product for Soft Delete",
                "Description",
                new BigDecimal("50.00"),
                "http://image.com/test",
                ProductStatus.ACTIVE,
                category
        );
        product = productRepository.save(product);
        UUID productId = product.getId();

        assertThat(productRepository.findById(productId)).isPresent();

        productRepository.delete(product);
        productRepository.flush();
        entityManager.clear();

        assertThat(productRepository.findById(productId)).isEmpty();

        Object result = entityManager.getEntityManager()
                .createNativeQuery("SELECT active FROM products WHERE id = :id")
                .setParameter("id", productId)
                .getSingleResult();

        assertThat(result).isEqualTo(false);
    }
}