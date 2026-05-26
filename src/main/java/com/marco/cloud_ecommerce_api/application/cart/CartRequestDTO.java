package com.marco.cloud_ecommerce_api.application.cart;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CartRequestDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private List<CartItemRequestDTO> items;

}
