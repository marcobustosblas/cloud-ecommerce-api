package com.marco.cloud_ecommerce_api.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Order {
    private final UUID id;
    private final UUID userId;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private final String idempotentKey;

    // ===== CONSTRUCTOR 1: BIRTH (nueva orden) =====

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

    // ===== CONSTRUCTOR 2: RECONSTRUCTION (desde BD) =====

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

    // ===== STATE TRANSITIONS (COMMANDS) =====

    public void pay() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending orders can be paid. Current status: " + this.status
            );
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending orders can be cancelled. Current status: " + this.status
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    // ===== QUERIES =====

    public boolean canBePaid() {
        return this.status == OrderStatus.PENDING;
    }

    public boolean canBeCancelled() {
        return this.status == OrderStatus.PENDING;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== GETTERS =====

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", status=" + status +
                ", itemsCount=" + items.size() +
                ", total=" + getTotal() +
                ", idempotentKey='" + idempotentKey + '\'' +
                '}';
    }

}
