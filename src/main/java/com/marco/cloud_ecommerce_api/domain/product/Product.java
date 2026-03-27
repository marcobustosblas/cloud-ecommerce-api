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
    private UUID categoryId; // reference to Category aggregate
    private Inventory inventory;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Product() {}

    public Product(
            String sku, String name, String description, BigDecimal price,
            String imageURL, UUID categoryId, int initialQuantity
    ) {
        // 1. Identidad
        this.id = UUID.randomUUID();

        // 2. Validaciones de entrada (Fail Fast)
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("Sku cannot be null or blank");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be greater than zero");
        if (categoryId == null)
            throw new IllegalArgumentException("CategoryId cannot be null");
        if (initialQuantity < 0)
            throw new IllegalArgumentException("Initial quantity cannot be negative");

        // 3. Asignación de campos
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.categoryId = categoryId;

        // 4. Estado inicial y Composición
        this.status = ProductStatus.DRAFT;
        this.inventory = new Inventory(initialQuantity, 0);

        // 5. Auditoría
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // 1 METHODS
    // 1.1Business Methods

    // 1.1.1 Configuración (fase DRAFT)

    public void updateBasicInfo(String name, String description) {
        if (this.status != ProductStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot be modify product basic info. Product must be in DRAFT state. " +
                    "Current state: " + this.status
            );
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePrice(BigDecimal newPrice) {
        if (this.status != ProductStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot modify product price. Product must be in DRAFT state. " +
                    "Current state: " + this.status
            );
        }
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeCategory(UUID newCategoryId) {
        if (this.status != ProductStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot change product category. Product must be in DRAFT state. " +
                    "Current state: " + this.status
            );
        }
        if (newCategoryId == null) {
            throw new IllegalArgumentException("CategoryId cannot be null");
        }
        this.categoryId = newCategoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateImageUrl(String imageUrl) {
        if (this.status == ProductStatus.DEACTIVATED) {
            throw new IllegalStateException(
                    "Cannot update image of deactivated product"
            );
        }
        this.imageURL = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    // 1.1.2 Transiciones de estado (State Transitions)

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
        if (categoryId == null)
            throw new IllegalStateException("Product must have a category");
        // 3. State transition
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status != ProductStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE products can be deactivated");
        }
        if (this.inventory.getReservedQuantity() > 0) {
            throw new IllegalStateException("Cannot deactivate product with active reservations");
        }
        this.status = ProductStatus.DEACTIVATED;
        this.updatedAt = LocalDateTime.now();
    }



}
