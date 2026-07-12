package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "inventory")
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id")
    private UUID inventoryId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reserved_quantity", nullable = false)
    private int reservedQuantity;

    @Version
    private Long version;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private ProductJpaEntity product;

    public InventoryJpaEntity() {}

    public InventoryJpaEntity(UUID id, int quantity, int reservedQuantity, LocalDateTime lastUpdated) {
        this.inventoryId = id;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.lastUpdated = lastUpdated;
    }

    // All-Args para Rehidratación
    // se añade la version para el bloqueo optimista y la relación con el product
    public InventoryJpaEntity(UUID id, int quantity, int reserved_quantity,
                              Long version, LocalDateTime lastUpdated, ProductJpaEntity product) {
        this.inventoryId = id;
        this.quantity = quantity;
        this.reservedQuantity = reserved_quantity;
        this.version = version;
        this.lastUpdated = lastUpdated;
        this.product = product;
    }

    @PreUpdate
    @PrePersist
    public void updateTime() {
        this.lastUpdated = LocalDateTime.now();
    }

}
