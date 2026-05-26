package com.marco.cloud_ecommerce_api.application.category;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import com.marco.cloud_ecommerce_api.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper categoryDtoMapper;

    /* Crear una nueva categoría */
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        Category category = categoryDtoMapper.toDomain(request);
        Category saved = categoryRepository.save(category);
        return categoryDtoMapper.toResponseDTO(saved);
    }

    /* Buscar categoría por ID */
    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found: " + id));
        return categoryDtoMapper.toResponseDTO(category);
    }

    /* Buscar categoría por nombre */
    @Transactional(readOnly = true)
    public CategoryResponseDTO findByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(()-> new RuntimeException("Category not found: " + name));
        return categoryDtoMapper.toResponseDTO(category);
    }

    /* Listar todas las categorías */
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream().map(categoryDtoMapper::toResponseDTO).toList();
    }

    /* Actualizar categoría */
    public CategoryResponseDTO updateCategory(UUID id, CategoryResponseDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found: " + id));
        category.rename(request.getName());
        Category updated = categoryRepository.save(category);
        return categoryDtoMapper.toResponseDTO(updated);
    }

    /* Activar categoría */
    public CategoryResponseDTO activateCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found: " + id));
        category.activate();
        Category updated = categoryRepository.save(category);
        return categoryDtoMapper.toResponseDTO(updated);
    }

    /* Desactivar categoría */
    public CategoryResponseDTO deactivateCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found: " + id));
        category.deactivate();
        Category updated = categoryRepository.save(category);
        return categoryDtoMapper.toResponseDTO(updated);
    }

    /* Eliminar categoría (solo si no tiene productos asociados) */
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        // Nota: La validación de productos asociados se hará en el repositorio
        // o en una capa superior. Por ahora, lo elimino
        categoryRepository.deleteById(id);
    }
}
