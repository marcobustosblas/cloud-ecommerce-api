package com.marco.cloud_ecommerce_api.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private UUID userId;
    private UUID productId;
    private String idempotentKey;
    private final BigDecimal defaultPrice = new BigDecimal("35.00");
    private List<OrderItem> defaultItems;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        idempotentKey = "test-key-" + UUID.randomUUID();
        OrderItem item = new OrderItem(productId, "Zapatos", 3, defaultPrice);
        defaultItems = List.of(item);
    }

    @Test
    @DisplayName("Debe crear un ítem de orden con datos válidos")
    void shouldCreateOrderItem() {
        OrderItem item = new OrderItem(productId, "Zapatillas", 5, defaultPrice);

        assertNotNull(item);
        assertEquals(productId, item.getProductId());
        assertEquals(5, item.getQuantity());
    }

    @Test
    @DisplayName("Debe calcular el subtotal (precio * cantidad) correctamente")
    void shouldCalculateSubtotal() {
        OrderItem item = new OrderItem(productId, "Camisa", 5, defaultPrice);
        assertEquals(new BigDecimal("175.00"), item.getSubtotal());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la cantidad es cero o negativa")
    void shouldThrowExceptionWhenQuantityIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(productId, "Zapatillas", 0, defaultPrice);
        });
    }

    @Test
    @DisplayName("Toda orden nueva debe nacer en estado PENDING")
    void shouldCreateOrderWithPendingStatus() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Debe calcular el total de la orden sumando todos sus ítems")
    void shouldCalculateTotalSumOfItems() {
        List<OrderItem> items = Arrays.asList(
                new OrderItem(productId, "Lentes", 3, new BigDecimal("15.00")),
                new OrderItem(productId, "Gorro", 1, new BigDecimal("40.00")),
                new OrderItem(productId, "Camisas", 7, new BigDecimal("30.00"))
        );
        Order order = new Order(userId, items, idempotentKey);

        assertEquals(new BigDecimal("295.00"), order.getTotal());
        assertEquals(3, order.getItems().size());
    }

    @Test
    @DisplayName("Debe permitir pagar una orden que está pendiente")
    void shouldTransitionToPaidStatus() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.pay();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void shouldTransitionToCancelledStatus() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("No debe permitir pagar una orden que ya fue pagada (Idempotencia de estado)")
    void shouldFailToPayWhenStatusIsNotPending() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.pay();

        assertThrows(IllegalStateException.class, order::pay);
    }

    @Test
    @DisplayName("Debe proteger la lista de ítems contra modificaciones externas")
    void shouldReturnImmutableListOfItems() {
        Order order = new Order(userId, defaultItems, idempotentKey);

        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            items.add(new OrderItem(UUID.randomUUID(), "OrderColado", 3, defaultPrice));
        });
    }

    @Test
    @DisplayName("Debe indicar que una orden pendiente puede ser pagada")
    void shouldIndicatePendingOrderCanBePaid() {
        Order order = new Order(userId, defaultItems, idempotentKey);

        assertTrue(order.canBePaid());
        assertTrue(order.canBeCancelled());
    }

    @Test
    @DisplayName("Debe indicar que una orden pagada no puede ser pagada nuevamente ni cancelada")
    void shouldIndicatePaidOrderCannotBePaidOrCancelled() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.pay();

        assertFalse(order.canBePaid());
        assertFalse(order.canBeCancelled());
    }

}
