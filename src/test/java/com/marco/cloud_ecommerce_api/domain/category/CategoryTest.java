package com.marco.cloud_ecommerce_api.domain.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Domain Unit Tests")
public class CategoryTest {

    @Test
    @DisplayName("Debería crear una categoría con nombre y descripción válidos")
    void shouldCreateCategory_withNameAndDescription() {
        // Arrange & Act
        Category category = new Category("Electronics", "All electronic devices");

        // Assert
        assertNotNull(category.getId(), "El ID debe ser autogenerado por el dominio");
        assertEquals("Electronics", category.getName());
        assertEquals("All electronic devices", category.getDescription());
        assertTrue(category.isActive(), "La categoría debe nacer activa por defecto");
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
    }

    @Test
    @DisplayName("Debería lanzar excepción si los atributos obligatorios son nulos o vacíos")
    void shouldThrowException_whenAttributesAreInvalid() {
        // Validar restricciones del nombre
        assertThrows(IllegalArgumentException.class, () -> new Category(null, "Valid Description"));
        assertThrows(IllegalArgumentException.class, () -> new Category("   ", "Valid Description"));

        // Validar restricciones de la descripción
        assertThrows(IllegalArgumentException.class, () -> new Category("Valid Name", null));
        assertThrows(IllegalArgumentException.class, () -> new Category("Valid Name", ""));
    }

    @Test
    @DisplayName("Debería cambiar el estado a inactivo al desactivar")
    void shouldDeactivateCategory() {
        // Arrange
        Category category = new Category("Books", "Physical and digital books");

        // Act
        category.deactivate();

        // Assert
        assertFalse(category.isActive(), "La categoría debería pasar a estar inactiva");
    }

    @Test
    @DisplayName("Debería permitir reactivar una categoría previamente desactivada")
    void shouldActivateCategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        // Simulo una categoría inactiva usando el constructor de reconstrucción
        Category category = new Category(categoryId, "Sports", "Fitness gear", false, now, now);

        // Act
        category.activate();

        // Assert
        assertTrue(category.isActive(), "La categoría debería volver a estar activa");
    }
}
