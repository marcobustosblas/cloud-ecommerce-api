package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID>{

    Optional<ProductJpaEntity> findBySku(String sku);

    boolean existsBySku(String sku);

}
