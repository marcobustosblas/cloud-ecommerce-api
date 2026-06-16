package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;


import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.domain.product.ProductRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper.ProductMapper;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
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
        ProductJpaEntity savedEntity = productJpaRepository.save(entity);

        // 4. Convertir de vuelta a dominio
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productJpaRepository.findBySku(sku).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll()
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productJpaRepository.existsBySku(sku);
    }

    @Override
    public Page<Product> findAllFilteredPage(Specification<ProductJpaEntity> spec, Pageable pageable) {
        return productJpaRepository.findAll(spec, pageable).map(mapper::toDomain);
    }
}
