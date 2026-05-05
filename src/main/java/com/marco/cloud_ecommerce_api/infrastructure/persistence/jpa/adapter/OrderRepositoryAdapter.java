package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper.OrderMapper;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toJpaEntity(order);
        OrderJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdempotentKey(String idempotentKey) {
        return jpaRepository.findByIdempotentKey(idempotentKey).map(mapper::toDomain);
    }

}
