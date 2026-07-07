package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.product.Inventory;
import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final InventoryMapper inventoryMapper;

    public ProductMapper(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    // JPA → DOMAIN (DB -> Negocio)
    public Product toDomain(ProductJpaEntity entity) {
        if (entity == null) return null;

        // 1. Mapeo el inventario interno si la entidad lo tiene
        Inventory inventory = null;
        if (entity.getInventory() != null) {
            inventory = inventoryMapper.toDomain(entity.getInventory());
        }

        // 2. Usamos el nuevo constructor de rehidratación de 11 parámetros
        return new Product(
                entity.getId(), // Mantiene el ID real
                entity.getSku(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                //entity.getCategory().getId(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                // Evita NullPointerException. Solo el ID de la categoría
                entity.getImageURL(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                inventory
        );
    }

    // DOMAIN → JPA (Negocio -> DB)
    public ProductJpaEntity toJpaEntity(Product domain, CategoryJpaEntity category) {
        if (domain == null) return null;
        ProductJpaEntity entity = new ProductJpaEntity(
                domain.getSku(),
                domain.getName(),
                domain.getDescription(),
                domain.getPrice(),
                domain.getImageURL(),
                domain.getStatus(),
                category
        );
        entity.setActive(domain.isActive());
        return entity;
    }
}
