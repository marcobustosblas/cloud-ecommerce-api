package com.marco.cloud_ecommerce_api.application.product;

import com.marco.cloud_ecommerce_api.domain.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoMapper {

    // Domain → ResponseDTO
    public ProductResponseDTO toResponseDTO(Product product, String categoryName) {
        if (product == null) return null;
        return new ProductResponseDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategoryId(),
                categoryName,
                product.getImageURL(),
                product.getStatus(),
                product.getAvailableStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    // RequestDTO → Domain (parcial, para creación)
    public Product toDomain(ProductRequestDTO request) {
        return new Product(
                request.getSku(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategoryId(),
                request.getImageUrl(),
                request.getInitialQuantity()
        );
    }

}
