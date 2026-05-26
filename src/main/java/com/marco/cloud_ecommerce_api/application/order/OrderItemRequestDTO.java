package com.marco.cloud_ecommerce_api.application.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderItemRequestDTO {

    @NotBlank(message = "Product ID es required")
    private UUID productId;

    @NotBlank(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be al last 1")
    private Integer quantity;

}
