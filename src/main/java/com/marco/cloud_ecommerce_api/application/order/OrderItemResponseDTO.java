package com.marco.cloud_ecommerce_api.application.order;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderItemResponseDTO {

    private final UUID productId;
    private final String productName;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subTotal;

    public OrderItemResponseDTO(UUID productId, String productName, Integer quantity, BigDecimal unitPrice, BigDecimal subTotal) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
    }

}
