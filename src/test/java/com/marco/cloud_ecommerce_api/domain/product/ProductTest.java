package com.marco.cloud_ecommerce_api.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// import static org.junit.Assert.assertEquals; linea mala!
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Unit Tests")
public class ProductTest {

    // TESTS DE IGUALDAD E IDENTIDAD
    @Test
    @DisplayName("Deberían ser iguales dos productos con el mismo ID")
    void shouldBeEqual_whenProductsHaveSameId() {
        UUID sharedId = UUID.randomUUID();

        Product product1 = new Product(sharedId, "SKU-1", "Prod 1", "Desc", new BigDecimal("10"), UUID.randomUUID(), "url", ProductStatus.DRAFT, LocalDateTime.now(), LocalDateTime.now(), new Inventory(5, 0));
        Product product2 = new Product(sharedId, "SKU-2", "Prod 2", "Desc", new BigDecimal("20"), UUID.randomUUID(), "url", ProductStatus.DRAFT, LocalDateTime.now(), LocalDateTime.now(), new Inventory(10, 0));

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    // TESTS DE SEGURIDAD DE INVENTORY (ANTI-NPE)

    @Test
    @DisplayName("Debería tener inventory por defecto cuando se rehidrata sin inventory")
    void shouldHaveDefaultInventory_whenRehydratedWithoutInventory() {
        Product product = new Product(
                UUID.randomUUID(),
                "SKU-001",
                "Laptop",
                "Desc",
                new BigDecimal("1000"),
                UUID.randomUUID(),
                "image.jpg",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null  // <- Inyección manual de nulo
        );
        assertNotNull(product.getInventory(), "El inventario interno jamás debe ser null");
        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("getStock debe retornar de forma segura 0 unidades disponibles si el objeto interno es nulo")
    void shouldReturnZeroStock_whenInventoryIsNull() {
        Product product = new Product(
                UUID.randomUUID(),
                "SKU-001",
                "Laptop",
                "Desc",
                new BigDecimal("1000"),
                UUID.randomUUID(),
                "image.jpg",
                ProductStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now(),
                null);

        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("No debería lanzar NPE al evaluar disponibilidad si el inventario es null")
    void shouldNotThrowNPE_whenHasStockAndInventoryIsNull() {
        Product product = new Product(
                UUID.randomUUID(),
                "SKU-001",
                "Laptop",
                "Desc",
                new BigDecimal("1000"),
                UUID.randomUUID(),
                "image.jpg",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null);

        // Evalúa falsedad lógica de forma controlada
        assertFalse(product.hasStock(5));
    }

    @Test
    @DisplayName("Debería crear inventory automáticamente al llamar a restock si era null")
    void shouldCreateInventory_whenRestockingAndInventoryIsNull() {
        // Product con inventory null
        Product product = new Product(
                UUID.randomUUID(),
                "SKU-001",
                "Laptop",
                "Desc",
                new BigDecimal("1000"),
                UUID.randomUUID(),
                "image.jpg",
                ProductStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        product.restock(10);

        // Inventory se creó y tiene stock
        assertNotNull(product.getInventory());
        assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Debería lanzar excepción al desactivar producto con reservas activas")
    void shouldThrowException_whenDeactivatingProductWithActiveReservations() {
        Product product = new Product(
                "SKU-001", "Laptop", "Desc", new BigDecimal("1000"),
                UUID.randomUUID(), "image.jpg", 10
        );
        product.activate();
        product.reserveStock(3);

        // Lanza excepción al desactivar
        assertThrows(IllegalStateException.class, product::deactivate);
    }

    @Test
    @DisplayName("Debería funcionar correctamente con inventory existente")
    void shouldWorkCorrectly_withExistingInventory() {
        // Un producto con stock inicial (nace como DRAFT)
        Product product = new Product(
                "SKU-001", "Laptop", "Desc", new BigDecimal("1000"),
                UUID.randomUUID(), "image.jpg", 10
        );
        // Transition el estado para habilitar las operaciones comerciales
        product.activate();

        // Se opera sobre el stock de forma normal
        product.reserveStock(3);
        product.confirmOrder(3);

        // El stock disponible final debe ser 7
        assertEquals(7, product.getStock());
    }
}
