package com.marco.cloud_ecommerce_api.application.category;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {

    // RequestDTO (JSON del Cliente) → Domain (Mi Corazón de Negocio)
    public Category toDomain(CategoryRequestDTO request) {
        if (request == null) return null;
        // Paso el nombre y la descripción que vienen de la petición HTTP
        return new Category(
                request.getName(),
                request.getDescription());
    }

    // Domain → ResponseDTO
    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return  null;
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
