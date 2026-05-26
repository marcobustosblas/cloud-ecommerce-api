package com.marco.cloud_ecommerce_api.application.cart;


import com.marco.cloud_ecommerce_api.domain.cart.CartItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
public class CartResponseDTO {

    private final UUID cartId;
    private final UUID userId;
    private final List<CartItemResponseDTO> items;
    private final BigDecimal total;
    private final boolean empty;

    public CartResponseDTO(UUID cartId, UUID userId, List<CartItemResponseDTO> items, BigDecimal total, boolean empty) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.total = total;
        this.empty = empty;
    }
}
