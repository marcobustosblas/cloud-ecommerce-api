package com.marco.cloud_ecommerce_api.application.cart;


import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CartItemResponseDTO {

    private final UUID productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;

    public CartItemResponseDTO(UUID productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal subTotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subTotal;
    }
}
