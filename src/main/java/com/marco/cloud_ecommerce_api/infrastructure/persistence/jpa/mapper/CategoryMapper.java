package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toDomain(CategoryJpaEntity entity) {
        if (entity == null) return null;
        return new Category(
                entity.getId(),
                entity.getName(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    public CategoryJpaEntity toJpaEntity(Category domain) {
        if (domain == null) return null;

        // Siempre crear nueva entidad SIN ID (JPA lo generará)
        // Cuando recupo de BD, el mapper de toDomain usó el constructor all-args
        // Pero para guardar, siempre uso el constructor simple
        CategoryJpaEntity entity = new CategoryJpaEntity(domain.getName());
        entity.setActive(domain.isActive());
        // No setteo createdAt/updatedAt porque @PrePersist los maneja
        return entity;
    }
}
