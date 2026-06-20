package com.marco.cloud_ecommerce_api.domain.product;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    void deleteById(UUID id);
    boolean existsBySku(String sku);
    Page<Product> findAllFilteredPage(Specification<ProductJpaEntity> spec, Pageable pageable);
}
