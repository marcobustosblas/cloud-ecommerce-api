package com.marco.cloud_ecommerce_api.application.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Datos requeridos para crear o actualizar un producto en el catálogo")
public class ProductRequestDTO {

    @Schema(description = "Código SKU único del producto", example = "SKU-MOTO-G5")
    @NotBlank(message = "SKU is required")
    private String sku;

    @Schema(description = "Nombre comercial del producto", example = "Smartphone Moto G5")
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Schema(description = "Descripción detallada de las especificaciones del producto", example = "Pantalla 5.5 pulgadas, 32GB almacenamiento, 3GB RAM")
    private String description;

    @Schema(description = "Precio unitario del producto en USD", example = "199.99")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @Schema(description = "Identificador único de la categoría asociada", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @Schema(description = "URL de la imagen del producto alojada en el servidor", example = "https://mi-ecommerce.com/images/moto-g5.jpg")
    private String imageUrl;

    @Schema(description = "Cantidad inicial de existencias físicas para el inventario", example = "50", defaultValue = "0")
    @Min(value = 0, message = "Initial quantity cannot be negative")
    private int initialQuantity = 0;
}