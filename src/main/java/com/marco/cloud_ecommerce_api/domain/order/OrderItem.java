package com.marco.cloud_ecommerce_api.domain.order;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {
    private final UUID productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;

    // --- CONSTRUCTOR 1: TEMPORAL (para creación desde RequestDTO) ---
    // Usado cuando el frontend envía la orden (solo tiene productId y quantity)

    public OrderItem(UUID productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.productId = productId;
        this.productName = null;
        this.quantity = quantity;
        this.unitPrice = null;
    }


    // --- CONSTRUCTOR 2: DEFINITIVO (para enriquecer con datos de BD) ---

    public OrderItem(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        if (unitPrice == null) {
            throw new IllegalStateException("OrderItem not yet enriched with price");
        }
        return this.unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    // --- METHOD PARA "ENRIQUECER" UN ITEM TEMPORAL ---
    // Crea un NUEVO OrderItem (inmutable) con los datos completos

    public OrderItem assignProductDetails(String productName, BigDecimal unitPrice) {
        if (this.productName != null && this.unitPrice != null) {
            throw new IllegalStateException("OrderItem already enriched");
        }
        return new OrderItem(this.productId, productName, this.quantity, unitPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem that)) return false;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

}
