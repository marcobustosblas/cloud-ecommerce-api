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

        CategoryJpaEntity category = categoryRepository.save(new CategoryJpaEntity("Test Category"));

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

        // verifico que el producto si está presente
        assertThat(productRepository.findById(productId)).isPresent();

        // "Eliminar" (soft delete)
        productRepository.delete(product);
        productRepository.flush();
        entityManager.clear();

        // Verificación del Repositorio (Hibernate filtra los que tienen active = false)
        assertThat(productRepository.findById(productId)).isEmpty();

        // Verificación Base de Datos (SQL Nativo para saltar filtros)
        Object result = entityManager.getEntityManager()
                .createNativeQuery("SELECT active FROM products WHERE id = :id")
                .setParameter("id", productId)
                .getSingleResult();

        // active = false significa que está "soft deleted"
        assertThat(result).isEqualTo(false); // false = inactivo, true = activo
    }
}
