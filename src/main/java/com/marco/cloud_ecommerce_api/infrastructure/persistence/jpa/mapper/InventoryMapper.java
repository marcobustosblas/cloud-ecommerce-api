package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.product.Inventory;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.InventoryJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Inventory (Domain) y InventoryJpaEntity (JPA).
 * Responsabilidades:
 * 1. toDomain(): Rehidratar Inventory desde JPA
 * 2. toJpaEntity(): Convertir Inventory a JPA para persistencia
 * @since Semana 16
 */
@Component
public class InventoryMapper {

    /**
     * Convierte InventoryJpaEntity a Inventory (domain).
     *
     * @param entity La entidad JPA de Inventory
     * @return Inventory del dominio
     */
    public Inventory toDomain(InventoryJpaEntity entity) {
        if (entity == null) return null;

        return new Inventory(
                entity.getQuantity(),
                entity.getReservedQuantity()
        );
    }

    /**
     * Convierte Inventory (domain) a InventoryJpaEntity.
     *
     * @param domain El Inventory del dominio
     * @return Entidad JPA de Inventory
     */
    public InventoryJpaEntity toJpaEntity(Inventory domain) {
        if (domain == null) return null;

        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setQuantity(domain.getQuantity());
        entity.setReservedQuantity(domain.getReservedQuantity());
        // version se maneja automáticamente en JPA

        return entity;
    }
}
