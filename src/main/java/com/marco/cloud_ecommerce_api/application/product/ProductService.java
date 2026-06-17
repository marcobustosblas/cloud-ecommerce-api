package com.marco.cloud_ecommerce_api.application.product;

import com.marco.cloud_ecommerce_api.domain.category.Category;
import com.marco.cloud_ecommerce_api.domain.category.CategoryRepository;
import com.marco.cloud_ecommerce_api.domain.product.Product;
import com.marco.cloud_ecommerce_api.domain.product.ProductRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDtoMapper productDtoMapper;

    // --- CASOS DE USO ---

    /* Crear un nuevo producto */
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        // 1. Validar que la categoría existe
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new RuntimeException(
                        "Category not found: " + request.getCategoryId()
                ));

        // 2. Crear producto: Convertir RequestDTO → Domain (usa DTO mapper)
        Product product = productDtoMapper.toDomain(request);

        // 3. Si el producto debe estar activo inmediatamente (opcional)
        // product.activate();

        // 4. Guardar (el repository usa internamente el persistence mapper)
        Product saved = productRepository.save(product);

        // 5. Convertir Domain → ResponseDTO (usa DTO mapper)
        return productDtoMapper.toResponseDTO(saved, category.getName());
    }

    /* Buscar producto por ID */
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        // Buscar categoría para obtener su nombre
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        return productDtoMapper.toResponseDTO(product, category != null ? category.getName(): null);
    }

    /**
     * Listar productos con filtros dinámicos y paginación.
     * Los filtros son opcionales (si vienen null, se ignoran).
     * Solo devuelve productos activos (soft delete = false).
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAllFilteredPage(ProductFilterDTO filter, Pageable pageable) {
        Specification<ProductJpaEntity> spec = ProductSpecification.filterByActiveStatus()
                .and(ProductSpecification.filterByCategory(filter.getCategoryId()))
                .and(ProductSpecification.filterByCategoryIn(filter.getCategoryIds()))
                .and(ProductSpecification.filterByMinimumPrice(filter.getMinPrice()))
                .and(ProductSpecification.filterByMaxPrice(filter.getMaxPrice()))
                .and(ProductSpecification.filterBySearchText(filter.getSearch()));

        // 2. Llamo al adaptador usando el nuevo method que acabo de programar
        return productRepository.findAllFilteredPage(spec, pageable)
                .map(product -> {
                    // Busco el nombre de la categoría para el DTO de respuesta
                    String categoryName = categoryRepository.findById(product.getCategoryId())
                            .map(Category::getName)
                            .orElse(null);
                    return productDtoMapper.toResponseDTO(product, categoryName);
                });
    }

    /* Actualizar producto */
    public ProductResponseDTO updateProduct(UUID id, ProductRequestDTO request) {
        // 1. Buscar producto existente
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found: " + id));

        // 2. Verificar que la nueva categoría existe
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found: " + request.getCategoryId()));

        // 3. Actualizar datos (usando métodos del dominio)
        product.updateBasicInfo(request.getName(), request.getDescription());
        product.updatePrice(request.getPrice());
        product.changeCategory(request.getCategoryId());
        product.updateImageUrl(request.getImageUrl());

        // 4. Actualizar stock (si es necesario)
        if (request.getInitialQuantity() > 0) {
            product.restock(request.getInitialQuantity());
        }

        // 5. Persistir
        Product updated = productRepository.save(product);

        // 6. Convertir a DTO
        return productDtoMapper.toResponseDTO(updated, category.getName());
    }

    /* Activar producto */
    public ProductResponseDTO activateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found: " + id));
        product.activate();
        Product saved = productRepository.save(product);
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        return productDtoMapper.toResponseDTO(saved, category!= null ? category.getName() : null);
    }

    /* Desactivar producto (soft delete) */
    public ProductResponseDTO deactivateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found: " + id));
        product.deactivate();
        Product saved = productRepository.save(product);
        Category category = categoryRepository.findById(saved.getCategoryId()).orElse(null);
        return productDtoMapper.toResponseDTO(saved, category != null ? category.getName() : null);
    }

    /* Eliminar producto (hard delete - solo usar si es necesario) */
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

}
