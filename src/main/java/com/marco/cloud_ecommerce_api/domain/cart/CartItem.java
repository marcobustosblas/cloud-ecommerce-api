package com.marco.cloud_ecommerce_api.domain.cart;

import java.math.BigDecimal;
import java.util.UUID;

public class CartItem {
    private final UUID productId;
    private final String productName;
    private int quantity;
    private final BigDecimal unitPrice;

    private static final int MAX_QUANTITY_PER_PRODUCT = 10;

    public CartItem(UUID productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (quantity > MAX_QUANTITY_PER_PRODUCT) {
            throw new IllegalArgumentException(
                    "Quantity cannot exceed maximum of " + MAX_QUANTITY_PER_PRODUCT + " per product"
            );
        }
        this.productId = productId;
        this.productName = null;
        this.quantity = quantity;
        this.unitPrice = null;
        // El precio, nombre y subtotal se quedan en sus valores por defecto
        // hasta que el servicio los busque en la base de datos de productos.
    }

    public CartItem(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (quantity > MAX_QUANTITY_PER_PRODUCT) {
            throw new IllegalArgumentException(
                    "Quantity cannot exceed maximum of " + MAX_QUANTITY_PER_PRODUCT + " per product"
            );
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be greater than zero");
        }
        int newQuantity = this.quantity + amount;
        if (newQuantity > MAX_QUANTITY_PER_PRODUCT) {
            throw new IllegalStateException(
                    "Cannot exceed maximum quantity of " + MAX_QUANTITY_PER_PRODUCT +
                    " per product. Current: " + this.quantity + ", Requested: " + amount
            );
        }
        this.quantity = newQuantity;
    }

    public void setQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (newQuantity > MAX_QUANTITY_PER_PRODUCT) {
            throw new IllegalStateException("Cannot set quantity greater than " + MAX_QUANTITY_PER_PRODUCT);
        }
        this.quantity = newQuantity;
    }

    // Method para asignar atributos inmutables
    public CartItem assignCartDetails(String productName, BigDecimal unitPrice) {
        if (this.productName != null || this.unitPrice != null) {
            throw new IllegalStateException("CartItem already enriched");
        }
        return new CartItem(this.productId, productName, this.quantity, unitPrice);
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem cartItem)) return false;
        return productId != null && productId.equals(cartItem.productId);
    }

    @Override
    public int hashCode() {
        return productId != null ? productId.hashCode() : 0;
    }

}
