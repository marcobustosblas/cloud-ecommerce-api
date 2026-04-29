package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;


import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.domain.product.ProductRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper.ProductMapper;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        CategoryJpaEntity category = categoryJpaRepository
                .findById(product.getCategoryId())
                .orElseThrow(
                        () -> new RuntimeException("Category not found " + product.getCategoryId())
                );
        // Convertir dominio a entidad JPA
        ProductJpaEntity entity = mapper.toJpaEntity(product, category);

        // Guardar
        ProductJpaEntity savedEntity = jpaRepository.save(entity);

        // 4. Convertir de vuelta a dominio
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return jpaRepository.findBySku(sku).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

}
