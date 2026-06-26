package com.marco.cloud_ecommerce_api.infrastructure.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@Schema(description = "Respuesta estándar de error de la API")
public class ErrorResponse {

    @Schema(description = "Fecha y hora del error", example = "2026-06-26T10:00:00")
    private final LocalDateTime timestamp;

    @Schema(description = "Código HTTP del error", example = "400")
    private final int status;

    @Schema(description = "Nombre del error", example = "Validation Error")
    private final String error;

    @Schema(description = "Mensaje legible del error", example = "Invalid input data")
    private final String message;

    @Schema(description = "Ruta donde ocurrió el error", example = "/api/products")
    private final String path;

    @Schema(description = "Detalles adicionales (ej: errores por campo)", example = "{\"name\": \"Name is required\"}")
    private final Map<String, String> details;
}