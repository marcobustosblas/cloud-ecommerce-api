package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserJpaEntity u WHERE u.email = :email AND u.status = 'ACTIVE'")
    Optional<UserJpaEntity> findActiveByEmail(@Param("email") String email);

}
