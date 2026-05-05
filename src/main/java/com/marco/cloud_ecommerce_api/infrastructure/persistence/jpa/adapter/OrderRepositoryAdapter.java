package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper.OrderMapper;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toJpaEntity(order);
        OrderJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        String jpql = "SELECT DISTINCT o FROM OrderJpaEntity o LEFT JOIN FETCH o.items WHERE o.id=:id";
        TypedQuery<OrderJpaEntity> query = entityManager.createQuery(jpql, OrderJpaEntity.class);
        query.setParameter("id", id);
        try {
            OrderJpaEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Order> findByIdempotentKey(String idempotentKey) {
        String jpql = "SELECT DISTINCT o FROM OrderJpaEntity o LEFT JOIN FETCH o.items WHERE o.idempotentKey=:key";
        TypedQuery<OrderJpaEntity> query = entityManager.createQuery(jpql, OrderJpaEntity.class);
        query.setParameter("key", idempotentKey);
        try {
            OrderJpaEntity entity = query.getSingleResult();
            return Optional.of(mapper.toDomain(entity));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}
