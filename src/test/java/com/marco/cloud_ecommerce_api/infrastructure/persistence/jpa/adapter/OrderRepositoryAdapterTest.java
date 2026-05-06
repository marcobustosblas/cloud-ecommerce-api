package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.order.Order;
import com.marco.cloud_ecommerce_api.domain.order.OrderItem;
import com.marco.cloud_ecommerce_api.domain.order.OrderStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.mapper.OrderMapper;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.OrderJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Testcontainers
@Transactional
public class OrderRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private OrderRepositoryAdapter orderRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrderMapper orderMapper;

    private UUID testUserId;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testOrderItem = new OrderItem(
                UUID.randomUUID(),
                "Test Product",
                3,
                new BigDecimal("29.99")
        );
    }

    // T1
    @Test
    @DisplayName("Debe guardar y recuperar una orden con sus ítems")
    void shouldSaveAndFindOrder() {
        Order order = new Order(testUserId, List.of(testOrderItem), "unique-key-001");
        Order save = orderRepository.save(order);

        assertThat(save.getId()).isNotNull();
        assertThat(save.getUserId()).isEqualTo(testUserId);
        assertThat(save.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(save.getIdempotentKey()).isEqualTo("unique-key-001");
        assertThat(save.getItems()).hasSize(1);
        assertThat(save.getItems().get(0).getProductName()).isEqualTo("Test Product");
        assertThat(save.getItems().get(0).getQuantity()).isEqualTo(3);

        Order found = orderRepository.findById(save.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(save.getId());
    }

    // T2
    @Test
    @DisplayName("Buscar por idempotency key")
    void shouldFindOrderByIdempotentKey() {
        String idempotentKey = "unique-key-002";
        Order order = new Order(testUserId, List.of(testOrderItem), idempotentKey);
        orderRepository.save(order);

        Order found = orderRepository.findByIdempotentKey(idempotentKey).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getIdempotentKey()).isEqualTo(idempotentKey);
        assertThat(found.getUserId()).isEqualTo(testUserId);
    }

    // T3
    @Test
    @DisplayName("No debe encontrar orden con idempotency key inexistente")
    void shouldReturnEmptyWhenIdempotentKeyNotFound() {
        Order found = orderRepository.findByIdempotentKey("no-key").orElse(null);
        assertThat(found).isNull();
    }

    // T4
    @Test
    @DisplayName("Actualizar estado de orden y persistir en base de datos")
    void shouldUpdateAndPersistOrderStatus() {
        Order initialOrder = new Order(testUserId, List.of(testOrderItem), "unique-key-003");
        Order saved = orderRepository.save(initialOrder);

        // Limpiar el EntityManager para que no use datos viejos en memoria
        entityManager.flush();
        entityManager.clear();

        // Recuperar (Ahora JPA se ve obligado a leer la versión de la DB)
        Order found = orderRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(found.getVersion()); // <--- Si esto falla, el Mapper toDomain está mal

        found.pay();

        // Guardar actualización
        // Si el Mapper toJpaEntity mete la versión en la entidad, esto funcionará.
        assertDoesNotThrow(() -> orderRepository.save(found));
    }

    // T5
    @Test
    @DisplayName("Actualizar estado de orden")
    void shouldUpdateOrderStatus() {
        Order order = new Order(testUserId, List.of(testOrderItem), "unique-key-004");
        Order saved = orderRepository.save(order);

        saved.pay();
        Order updated = orderRepository.save(saved);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.PAID);

        // Verificar que persiste en BD
        Order found = orderRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    // T6
    @Test
    @DisplayName("Cancelar la orden")
    void shouldCancelOrder() {
        Order order = new Order(testUserId, List.of(testOrderItem), "unique-key-005");
        Order saved = orderRepository.save(order);

        saved.cancel();
        Order updated = orderRepository.save(saved);

        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    // T7
    @Test
    @DisplayName("No debe permitir duplicados por idempotency key")
    void shouldNotAllowDuplicateIdempotentKey() {
        String duplicatedKey = "duplicate-key-006";
        Order order1 = new Order(testUserId, List.of(testOrderItem), duplicatedKey);
        orderRepository.save(order1);

        Order order2 = new Order(UUID.randomUUID(), List.of(testOrderItem), duplicatedKey);

        assertThrows(RuntimeException.class,
                () -> {
                    orderRepository.save(order2);
                    entityManager.flush();
                }
        );
    }

    // T8
    @Test
    @DisplayName("Orden con múltiples items")
    void shouldSaveOrderWithMultipleItems() {
        List<OrderItem> items = List.of(
                new OrderItem(UUID.randomUUID(), "Product A", 1, new BigDecimal("10.00")),
                new OrderItem(UUID.randomUUID(), "Product B", 3, new BigDecimal("15.00")),
                new OrderItem(UUID.randomUUID(), "Product C", 2, new BigDecimal("20.00"))
        );

        Order order = new Order(testUserId, items, "unique-key-007");
        Order saved = orderRepository.save(order);

        assertThat(saved.getItems()).hasSize(3);
        assertThat(saved.getTotal()).isEqualByComparingTo(
                new BigDecimal("10.00")
                        .add(new BigDecimal("45.00"))
                        .add(new BigDecimal("40.00"))
        ); // Total = 95.00
    }

    // T9
    @Test
    @DisplayName("Orden sin items debe fallar")
    void shouldFailToCreateOrderWithoutItems() {
        assertThrows(IllegalArgumentException.class,
        () -> new Order(testUserId, List.of(), "unique-key-008")
        );
    }
}
