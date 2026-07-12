package com.marco.cloud_ecommerce_api.domain.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // TESTS DE ORDER ITEM

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

    // TESTS DE CREACIÓN Y LÓGICA DE NEGOCIO (BIRTH)

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
    @DisplayName("Debe proteger la lista de ítems contra modificaciones externas")
    void shouldReturnImmutableListOfItems() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            items.add(new OrderItem(UUID.randomUUID(), "OrderColao", 3, defaultPrice));
        });
    }

    // TESTS DE TRANSICIONES DE ESTADO

    @Test
    @DisplayName("Debe permitir pagar una orden que está pendiente")
    void shouldTransitionToPaidStatus() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.pay();

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("Debe permitir cancelar una orden que está pendiente")
    void shouldTransitionToCancelledStatus() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("NO DEBE permitir pagar una orden que ya fue pagada (Idempotencia de estado)")
    void shouldFailToPayWhenStatusIsNotPending() {
        Order order = new Order(userId, defaultItems, idempotentKey);
        order.pay();

        assertThrows(IllegalStateException.class, order::pay);
    }

    @Test
    @DisplayName("Debe indicar correctamente si una orden puede o no ejecutarse")
    void shouldIndicatePendingOrderQueries() {
        Order order = new Order(userId, defaultItems, idempotentKey);

        assertTrue(order.canBePaid());
        assertTrue(order.canBeCancelled());

        order.pay();

        assertFalse(order.canBePaid());
        assertFalse(order.canBeCancelled());
    }

    // TESTS DE CONSTRUCTOR DE RECONSTRUCCIÓN (INFRAESTRUCTURA)

    @Test
    @DisplayName("Debe rehidratar una orden desde la BD con todos sus datos")
    void shouldReconstructOrderFromDatabase() {
        UUID orderId = UUID.randomUUID();
        UUID rUserId = UUID.randomUUID();
        List<OrderItem> items = List.of(
                new OrderItem(UUID.randomUUID(), "Producto", 2, new BigDecimal("50.00"))
        );
        LocalDateTime pastDate = LocalDateTime.now().minusDays(3);
        Long version = 5L;
        String reconKey = "recon-key-123";

        Order order = new Order(orderId, rUserId, OrderStatus.PAID, items, pastDate, reconKey, version);

        assertEquals(orderId, order.getId());
        assertEquals(rUserId, order.getUserId());
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(pastDate, order.getCreatedAt());
        assertEquals(version, order.getVersion());
        assertEquals(reconKey, order.getIdempotentKey());
        assertEquals(new BigDecimal("100.00"), order.getTotal());
    }

    @Test
    @DisplayName("Debe lanzar excepción si faltan parámetros obligatorios en reconstrucción")
    void shouldThrowExceptionWhenReconstructionDataIsInvalid() {
        UUID orderId = UUID.randomUUID();
        List<OrderItem> items = List.of(new OrderItem(UUID.randomUUID(), "Producto", 1, new BigDecimal("10.00")));
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // 1. Validar versión nula
        assertThrows(IllegalArgumentException.class,
                () -> new Order(orderId, userId, OrderStatus.PENDING, items, pastDate, "key", null));
        // 2. Validar ID nulo
        assertThrows(IllegalArgumentException.class,
                () -> new Order(null, userId, OrderStatus.PENDING, items, pastDate, "key", 1L));
        // 3. Validar Items nulos
        assertThrows(IllegalArgumentException.class,
                () -> new Order(orderId, userId, OrderStatus.PENDING, null, pastDate, "key", 1L));
    }

    // TESTS DE EQUALS / HASHCODE

    @Test
    @DisplayName("Dos órdenes con el mismo ID deben ser iguales independientemente del estado")
    void shouldBeEqualWhenSameId() {
        UUID sharedId = UUID.randomUUID();
        List<OrderItem> items = List.of(new OrderItem(UUID.randomUUID(), "Product", 1, new BigDecimal("10.00")));
        LocalDateTime now = LocalDateTime.now();

        Order order1 = new Order(sharedId, userId, OrderStatus.PENDING, items, now, "key-1", 1L);
        Order order2 = new Order(sharedId, userId, OrderStatus.PAID, items, now, "key-2", 2L);

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    @DisplayName("Dos órdenes con ID diferente deben ser diferentes")
    void shouldNotBeEqual_whenDifferentId() {
        List<OrderItem> items = List.of(new OrderItem(UUID.randomUUID(), "Producto", 1, new BigDecimal("10.00")));
        LocalDateTime now = LocalDateTime.now();

        Order order1 = new Order(UUID.randomUUID(), userId, OrderStatus.PENDING, items, now, "key-1", 1L);
        Order order2 = new Order(UUID.randomUUID(), userId, OrderStatus.PENDING, items, now, "key-2", 1L);

        assertNotEquals(order1, order2);
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
