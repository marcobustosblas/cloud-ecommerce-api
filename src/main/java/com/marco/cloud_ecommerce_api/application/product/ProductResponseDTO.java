package com.marco.cloud_ecommerce_api.application.product;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductResponseDTO {

    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private UUID categoryId;
    private String categoryName;  // Opcional: para mostrar nombre de categoría
    private String imageUrl;
    private ProductStatus status;
    private int availableStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponseDTO(UUID id, String sku, String name, String description, BigDecimal price, UUID categoryId, String categoryName, String imageUrl, ProductStatus status, int availableStock, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.status = status;
        this.availableStock = availableStock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
