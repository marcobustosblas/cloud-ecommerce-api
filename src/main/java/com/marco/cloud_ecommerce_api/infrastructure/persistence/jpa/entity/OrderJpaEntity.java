package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import com.marco.cloud_ecommerce_api.domain.order.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "idempotent_key", nullable = false, unique = true)
    private String idempotentKey;

    @Version
    private Long version;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected OrderJpaEntity() {}

    public OrderJpaEntity(UUID userId, OrderStatus status, String idempotentKey) {
        this.userId = userId;
        this.status = status;
        this.idempotentKey = idempotentKey;
    }

    public OrderJpaEntity(UUID id, UUID userId, OrderStatus status,
                          String idempotentKey, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.idempotentKey = idempotentKey;
        this.createdAt = createdAt;
    }

    public void addItem(OrderItemJpaEntity item) {
        this.items.add(item);
        item.setOrder(this);
    }

}
