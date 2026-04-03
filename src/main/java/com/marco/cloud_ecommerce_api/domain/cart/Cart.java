package com.marco.cloud_ecommerce_api.domain.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Cart {
    private final UUID id;
    private final UUID userId;
    private final List<CartItem> items;

    public Cart(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public Cart(UUID id, UUID userId, List<CartItem> items) {
        if (id == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>(items);
    }

    public void addItem(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        // Validaciones
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

        // Busco si el producto ya existe en el carrito, en caso contrario lo agrego sabrosamente
        items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.addQuantity(quantity),
                        () -> items.add(new CartItem(productId, productName, quantity, unitPrice))
                );
    }

    public void removeItem(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void updateQuantity(UUID productId, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (quantity <= 0) {
            removeItem(productId);
            return;
        }
        items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(
                        item -> item.setQuantity(quantity)
                );
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotal() {
        BigDecimal totalSum = BigDecimal.ZERO;
        for (CartItem item: items) {
            totalSum = totalSum.add(item.getSubtotal());
        }
        return totalSum;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart cart)) return false;
        return id != null && id.equals(cart.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
