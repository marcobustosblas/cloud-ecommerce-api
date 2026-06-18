package com.marco.cloud_ecommerce_api.application.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderRequestDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequestDTO> items;

    // Ya no es @NotBlank en el JSON. Se llena internamente desde el Header.
    private String idempotentKey;

}
