package com.marco.cloud_ecommerce_api.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    Page<Product> findAllPage(Pageable pageable);
}
