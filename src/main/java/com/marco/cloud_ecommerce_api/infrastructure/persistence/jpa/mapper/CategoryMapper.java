package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // Base de Datos (JPA Entity) → Corazón de Negocio (Domain)
    public Category toDomain(CategoryJpaEntity entity) {
        if (entity == null) return null;
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // Corazón de Negocio (Domain) → Base de Datos (JPA Entity)
    public CategoryJpaEntity toJpaEntity(Category domain) {
        if (domain == null) return null;

        // SOLUCIÓN: Paso absolutamente TODOS los datos mapeados desde el dominio.
        // Al pasarle domain.getId(), Hibernate sabe perfectamente si debe hacer un INSERT (si es nuevo)
        // o un UPDATE (si el ID ya existía en la base de datos).
        return new CategoryJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
