package com.marco.cloud_ecommerce_api.application.order;


import com.marco.cloud_ecommerce_api.domain.order.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderResponseDTO {

    private final UUID id;
    private final UUID userId;
    private final OrderStatus status;
    private final List<OrderItemResponseDTO> items;
    private final BigDecimal total;
    private final LocalDateTime createdAt;
    private final String idempotentKey;

    public OrderResponseDTO(UUID id, UUID userId, OrderStatus status, List<OrderItemResponseDTO> items, BigDecimal total, LocalDateTime createdAt, String idempotentKey) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
        this.idempotentKey = idempotentKey;
    }

}
