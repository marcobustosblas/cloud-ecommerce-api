package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.order.OrderItem;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.OrderItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    // JPA → Domain (para LEER de BD)
    public OrderItem toDomain(OrderItemJpaEntity entity) {
        if (entity == null) return null;
        return new OrderItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }
    // Domain → JPA (para GUARDAR en BD)
    public OrderItemJpaEntity toJpaEntity(OrderItem domain) {
        if (domain == null) return null;
        return new OrderItemJpaEntity(
                domain.getProductId(),
                domain.getProductName(),
                domain.getQuantity(),
                domain.getUnitPrice()
        );
    }
}
