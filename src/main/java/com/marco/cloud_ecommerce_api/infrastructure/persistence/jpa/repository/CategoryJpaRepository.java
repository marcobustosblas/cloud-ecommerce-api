package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    Optional<CategoryJpaEntity> findByName(String name);

    boolean existsByName(String name);

}
