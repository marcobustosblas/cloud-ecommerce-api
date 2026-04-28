package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    // DB -> Negocio
    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;
        return new Product(
                entity.getId(), // Mantiene el ID real
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory().getId(),  // Solo el ID de la categoría
                entity.getImageURL(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    // Negocio -> DB
    public ProductJpaEntity toJpaEntity(Product domain, CategoryJpaEntity category) {
        if (domain == null) return null;
        return new ProductJpaEntity(
                domain.getId(),
                domain.getSku(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getImageURL(),
                domain.getStatus(),
                category,
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

}
