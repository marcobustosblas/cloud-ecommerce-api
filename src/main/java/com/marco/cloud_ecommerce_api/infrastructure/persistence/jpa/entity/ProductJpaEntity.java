package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import com.marco.cloud_ecommerce_api.domain.product.Inventory;
import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
@EntityListeners(AuditingEntityListener.class)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageURL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "update_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relation con Category (DUEÑO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryJpaEntity category;

    // Relation con Inventory (INVERSO - sin orphan [false por defecto])
    @OneToOne(
            mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    ) private InventoryJpaEntity inventory;

    // Constructor vacío para JPA
    protected ProductJpaEntity() {}

    // Constructor para creación
    public ProductJpaEntity(String sku, String name, String description, BigDecimal price,
                            String imageURL, ProductStatus status, CategoryJpaEntity category) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.status = status;
        this.category = category;
    }

    // All-Args Constructor para el Mapper (Rehidratación)
    public ProductJpaEntity(UUID id, String sku, String name, String description,
                            BigDecimal price, String imageURL, ProductStatus status,
                            CategoryJpaEntity category,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.status = status;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // soft delete methods (para control manual)
    public void deactivate() {
        this.status = ProductStatus.DEACTIVATED;
        this.deactivatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
        this.deactivatedAt = null;
    }

    // method helper sabroso
    public void setInventory(InventoryJpaEntity inventory) {
        this.inventory = inventory;
        if (inventory != null) {
            inventory.setProduct(this);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getImageURL() { return imageURL; }
    public CategoryJpaEntity getCategory() { return category; }
    public ProductStatus getStatus() { return status; }
    public InventoryJpaEntity getInventory() { return inventory; }
    public LocalDateTime getDeactivatedAt() { return deactivatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setCategory(CategoryJpaEntity category) { this.category = category; }
    public void setStatus(ProductStatus status) { this.status = status; }
    public void setDeactivatedAt(LocalDateTime deactivatedAt) { this.deactivatedAt = deactivatedAt; }

}
