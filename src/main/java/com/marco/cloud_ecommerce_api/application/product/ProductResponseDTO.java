package com.marco.cloud_ecommerce_api.application.product;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Schema(description = "Información detallada de un producto")
public class ProductResponseDTO {

    @Schema(description = "ID único del producto", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID id;

    @Schema(description = "Código SKU", example = "SKU-001")
    private final String sku;

    @Schema(description = "Nombre del producto", example = "Laptop Gaming")
    private final String name;

    @Schema(description = "Descripción del producto")
    private final String description;

    @Schema(description = "Precio en USD", example = "99.99")
    private final BigDecimal price;

    @Schema(description = "ID de la categoría", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID categoryId;

    @Schema(description = "Nombre de la categoría", example = "Electronics")
    private final String categoryName;

    @Schema(description = "URL de la imagen", example = "https://tienda.com/images/product.jpg")
    private final String imageUrl;

    @Schema(description = "Estado del producto", example = "ACTIVE")
    private final ProductStatus status;

    @Schema(description = "Stock disponible", example = "10")
    private final int availableStock;

    @Schema(description = "Fecha de creación", example = "2026-06-26T10:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización", example = "2026-06-26T10:00:00")
    private final LocalDateTime updatedAt;

    public ProductResponseDTO(UUID id, String sku, String name, String description, BigDecimal price, UUID categoryId, String categoryName, String imageUrl, ProductStatus status, int availableStock, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.status = status;
        this.availableStock = availableStock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
