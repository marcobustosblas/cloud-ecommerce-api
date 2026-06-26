package com.marco.cloud_ecommerce_api.infrastructure.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marco.cloud_ecommerce_api.application.product.ProductRequestDTO;
import com.marco.cloud_ecommerce_api.application.product.ProductService;
import com.marco.cloud_ecommerce_api.domain.exception.ResourceNotFoundException;
import com.marco.cloud_ecommerce_api.infrastructure.api.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    // ===== TEST 1: VALIDACIÓN FALLIDA (@Valid) → 400 BAD REQUEST =====
    @Test
    void shouldReturn400WhenValidationFails() throws Exception {
        // Objeto inválido (Falta el Nombre para violar @NotBlank)
        ProductRequestDTO invalidRequest = new ProductRequestDTO();
        invalidRequest.setSku("SKU-001");
        invalidRequest.setPrice(new BigDecimal("99.99"));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    // ===== TEST 2: RECURSO NO ENCONTRADO EN FILTRADO → 404 NOT FOUND =====
    @Test
    void shouldReturn404WhenDomainThrowsResourceNotFound() throws Exception {
        // Fuerzo al servicio del catálogo a lanzar la excepción de dominio que cree ayer
        String validUuidString = "550e8400-e29b-41d4-a716-446655440000";
        when(productService.findAllFilteredPage(any(), any()))
                .thenThrow(new ResourceNotFoundException("Categoría", "id", validUuidString));

        mockMvc.perform(get("/api/products")
                        .param("categoryId", validUuidString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoría con id '" + validUuidString + "' no fue encontrado."));
    }



}
