package com.marco.cloud_ecommerce_api.domain.cart;

import java.util.ArrayList;
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

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }
}
