package com.marco.cloud_ecommerce_api.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID userId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private final String idempotentKey;

    public Order(UUID userId, List<OrderItem> items, String idempotentKey) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (idempotentKey == null || idempotentKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key is required");
        }
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>(items);
        this.idempotentKey = idempotentKey;
        this.createdAt = LocalDateTime.now();
    }

    public Order(UUID id, UUID userId, OrderStatus status, List<OrderItem> items,
                 LocalDateTime createdAt, String idempotentKey) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt;
        this.idempotentKey = idempotentKey;
    }

}
