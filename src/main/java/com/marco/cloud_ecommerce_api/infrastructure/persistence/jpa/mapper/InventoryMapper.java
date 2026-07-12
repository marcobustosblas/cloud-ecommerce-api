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
     * Convierte InventoryJpaEntity (BD) a Inventory (Dominio).
     * Rehidratación completa con todas las invariantes.
     */
    public Inventory toDomain(InventoryJpaEntity entity) {
        if (entity == null) return null;

        return new Inventory(
                entity.getInventoryId(),
                entity.getProduct() != null ? entity.getProduct().getId() : null,
                entity.getQuantity(),
                entity.getReservedQuantity(),
                entity.getLastUpdated()
        );
    }

    /**
     * Convierte Inventory (Dominio) a InventoryJpaEntity (Infraestructura).
     * Mantiene los IDs para que Hibernate sepa si hace INSERT o UPDATE.
     */
    public InventoryJpaEntity toJpaEntity(Inventory domain) {
        if (domain == null) return null;

        InventoryJpaEntity entity = new InventoryJpaEntity();

        entity.setInventoryId(domain.getId());
        entity.setQuantity(domain.getQuantity());
        entity.setReservedQuantity(domain.getReservedQuantity());
        entity.setLastUpdated(domain.getLastUpdated());
        // version se maneja automáticamente en JPA

        // Nota: El enlace del objeto ProductJpaEntity completo se suele manejar
        // en el ProductMapper, ya que el Inventario forma parte de su agregado.

        return entity;
    }
}
