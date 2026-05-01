package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
        this.id = id;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.lastUpdated = lastUpdated;
    }

    // All-Args para Rehidratación
    // se añade la version para el bloqueo optimista y la relación con el product
    public InventoryJpaEntity(UUID id, int quantity, int reserved_quantity,
                              Long version, LocalDateTime lastUpdated, ProductJpaEntity product) {
        this.id = id;
        this.quantity = quantity;
        this.reservedQuantity = reserved_quantity;
        this.version = version;
        this.lastUpdated = lastUpdated;
        this.product = product;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ProductJpaEntity getProduct() {
        return product;
    }

    public void setProduct(ProductJpaEntity product) {
        this.product = product;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @PreUpdate
    @PrePersist
    public void updateTime() {
        this.lastUpdated = LocalDateTime.now();
    }

}
