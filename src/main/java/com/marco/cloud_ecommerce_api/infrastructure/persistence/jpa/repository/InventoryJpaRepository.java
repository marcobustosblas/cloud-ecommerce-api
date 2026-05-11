package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.InventoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryJpaEntity, UUID> {

    @Override
    @NonNull
    Optional<InventoryJpaEntity> findById(@NonNull UUID id);

}
