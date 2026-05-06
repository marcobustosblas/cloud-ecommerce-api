package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderItem;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final OrderItemMapper itemMapper;

    public OrderMapper(OrderItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public Order toDomain(OrderJpaEntity entity) {
        if (entity == null) return null;

        // Convierte la lista de JPA Entities a lista de Domain objects
        List<OrderItem> domainItems =
                entity.getItems().stream().map(itemMapper::toDomain).toList();

        return new Order(
                entity.getId(),
                entity.getUserId(),
                entity.getStatus(),
                domainItems,
                entity.getCreatedAt(),
                entity.getIdempotentKey(),
                entity.getVersion()
        );
    }

    public OrderJpaEntity toJpaEntity(Order domain) {
        if (domain == null) return null;
        OrderJpaEntity entity;
        if (domain.getVersion() == null) {
            entity = new OrderJpaEntity(
                    domain.getUserId(),
                    domain.getStatus(),
                    domain.getIdempotentKey()
            );
        } else {
            entity = new OrderJpaEntity(
                    domain.getId(),
                    domain.getUserId(),
                    domain.getStatus(),
                    domain.getIdempotentKey(),
                    domain.getCreatedAt(),
                    domain.getVersion()
            );
        }
        domain.getItems().stream().map(itemMapper::toJpaEntity).forEach(entity::addItem);
        return entity;
    }

}
