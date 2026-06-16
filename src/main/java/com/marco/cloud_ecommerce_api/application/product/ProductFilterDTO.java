package com.marco.cloud_ecommerce_api.application.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductFilterDTO {
    private UUID categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String search;
}
