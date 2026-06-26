package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.marco.cloud_ecommerce_api.application.common.PageResponseDTO;
import com.marco.cloud_ecommerce_api.application.product.ProductFilterDTO;
import com.marco.cloud_ecommerce_api.infrastructure.api.exception.ErrorResponse;
import com.marco.cloud_ecommerce_api.application.product.ProductRequestDTO;
import com.marco.cloud_ecommerce_api.application.product.ProductResponseDTO;
import com.marco.cloud_ecommerce_api.application.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Endpoints para la gestión, búsqueda avanzada y paginación del catálogo de AICES")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Obtener catálogo de productos paginado y filtrado",
            description = "Permite buscar productos aplicando filtros dinámicos por precio, categoría o texto, retornando un objeto paginado estándar."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda procesada con éxito y catálogo retornado."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de filtrado inválidos o error de sintaxis.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)) // Vincula el manejador global
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error inesperado en el servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> findAll(
            ProductFilterDTO filter, // Spring lee los parámetros de la URL (?search=...&minPrice=...) y llena este DTO
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        // Llamo al servicio que acabo de modificar
        Page<ProductResponseDTO> page = productService.findAllFilteredPage(filter, pageable);

        // Devuelvo la página con los filtros aplicados (para que el cliente sepa qué buscó)
        PageResponseDTO<ProductResponseDTO> response = new PageResponseDTO<>(page, filter);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable UUID id, @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponseDTO> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.activateProduct(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponseDTO> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deactivateProduct(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
