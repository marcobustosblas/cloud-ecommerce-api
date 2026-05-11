package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID>{

    Optional<ProductJpaEntity> findBySku(String sku);

    boolean existsBySku(String sku);

    @Override
    @NonNull
    @EntityGraph(attributePaths = "category")
    List<ProductJpaEntity> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = "category")
    Optional<ProductJpaEntity> findById(@NonNull UUID id);

}
