package com.marco.cloud_ecommerce_api.application.category;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {

    // RequestDTO → Domain
    public Category toDomain(CategoryRequestDTO request) {
        if (request == null) return null;
        return new Category(request.getName());
    }

    // Domain → ResponseDTO
    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return  null;
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
