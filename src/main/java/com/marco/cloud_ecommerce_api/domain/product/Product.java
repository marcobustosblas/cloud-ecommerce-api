package com.marco.cloud_ecommerce_api.domain.product;

import com.marco.cloud_ecommerce_api.domain.category.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    private UUID id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageURL;
    private Category category;
    private Inventory inventory;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Product() {}

    public Product(String sku, String name, String description, BigDecimal price, String imageURL, Category category, int initialQuantity) {
        // 1. Identidad
        this.id = UUID.randomUUID();

        // 2. Validaciones de entrada (Fail Fast)
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("Sku cannot be null or blank");
        if (name == null || name.isBlank())
            throw new IllegalStateException("Name cannot be null or blank");
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");
        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");
        if (initialQuantity < 0)
            throw new IllegalArgumentException("Initial quantity cannot be negative");

        // 3. Asignación de campos
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.category = category;

        // 4. Estado inicial y Composición
        this.status = ProductStatus.DRAFT;
        this.inventory = new Inventory(initialQuantity, 0);

        // 5. Auditoría
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void activate() {
        // 1. Transition guard
        if (this.status != ProductStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT products can be activated");
        }
        // 2. Business invariants
        if (this.price == null || this.price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalStateException("Product price must be greater than zero to activate");
        if (this.name == null || this.name.isBlank())
            throw new IllegalStateException("Product name must not be blank to activate");
        if (this.description == null || this.description.isBlank())
            throw new IllegalStateException("Product description must not be blank to activate");
        if (category == null) 
            throw new IllegalStateException("Product must have a category");
        // 3. State transition
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

}
