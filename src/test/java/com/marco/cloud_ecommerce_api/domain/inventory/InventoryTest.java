package com.marco.cloud_ecommerce_api.domain.inventory;

import com.marco.cloud_ecommerce_api.domain.product.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Domain Unit Tests")
class InventoryTest {

    @Test
    @DisplayName("Debería crear un inventario nuevo asociado a un producto con stock inicial")
    void shouldCreateInventory_withProductAndInitialStock() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Act
        Inventory inventory = new Inventory(productId, 50);

        // Assert
        assertNotNull(inventory.getId(), "El ID del inventario debe autogenerarse");
        assertEquals(productId, inventory.getProductId(), "Debe quedar vinculado al productId");
        assertEquals(50, inventory.getQuantity(), "La cantidad total física debe ser la inicial");
        assertEquals(0, inventory.getReservedQuantity(), "Las reservas deben iniciar en 0");
        assertNotNull(inventory.getLastUpdated());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el constructor completo recibe datos inconsistentes")
    void shouldThrowException_whenFullConstructorReceivesInvalidData() {
        UUID invId = UUID.randomUUID();
        UUID prodId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // 1. Validar ID nulo
        assertThrows(IllegalArgumentException.class, () -> new Inventory(null, prodId, 10, 0, now));
        // 2. Validar ProductID nulo
        assertThrows(IllegalArgumentException.class, () -> new Inventory(invId, null, 10, 0, now));
        // 3. Validar cantidad total negativa
        assertThrows(IllegalArgumentException.class, () -> new Inventory(invId, prodId, -5, 0, now));
        // 4. Validar cantidad reservada negativa
        assertThrows(IllegalArgumentException.class, () -> new Inventory(invId, prodId, 10, -1, now));
        // 5. Validar que la reserva no supere al stock físico
        assertThrows(IllegalArgumentException.class, () -> new Inventory(invId, prodId, 10, 11, now));
    }

    @Test
    @DisplayName("Debería reconstruirse correctamente con el constructor de infraestructura")
    void shouldReconstructInventory_withFullConstructor() {
        // Arrange
        UUID invId = UUID.randomUUID();
        UUID prodId = UUID.randomUUID();
        LocalDateTime historicalDate = LocalDateTime.now().minusDays(5);

        // Act
        Inventory inventory = new Inventory(invId, prodId, 100, 30, historicalDate);

        // Assert
        assertEquals(invId, inventory.getId());
        assertEquals(prodId, inventory.getProductId());
        assertEquals(100, inventory.getQuantity());
        assertEquals(30, inventory.getReservedQuantity());
        assertEquals(historicalDate, inventory.getLastUpdated(), "Debe respetar la fecha original de la BD");
    }
}