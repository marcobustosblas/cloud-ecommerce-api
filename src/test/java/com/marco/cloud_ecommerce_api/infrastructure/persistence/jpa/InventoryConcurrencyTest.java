package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa;

import com.marco.cloud_ecommerce_api.domain.product.ProductStatus;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.InventoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.ProductJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.InventoryJpaRepository;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class InventoryConcurrencyTest {

    @Autowired
    private InventoryJpaRepository inventoryRepository;

    @Autowired
    private TransactionTemplate transactionTemplate; // Para manejar transacciones manuales en el test

    private UUID inventoryId;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private CategoryJpaRepository categoryRepository;

    @BeforeEach
    void setUp() {
        inventoryId = transactionTemplate.execute(status -> {
            // Crear categoría
            CategoryJpaEntity category = categoryRepository.save(new CategoryJpaEntity("Concurrency Category"));

            // 1. Creamos un producto ficticio para cumplir con la restricción
            ProductJpaEntity product = new ProductJpaEntity(
                    "CONCURRENCY-SKU",
                    "Concurrency Product",
                    "Test product for concurrency",
                    new BigDecimal("100.00"),
                    "http://image.com/test",
                    ProductStatus.ACTIVE,
                    category
            );
            ProductJpaEntity savedProduct = productRepository.save(product);

            // 2. Ahora creamos el inventario asociado a ese producto
            InventoryJpaEntity inventory = new InventoryJpaEntity();
            inventory.setQuantity(10);
            inventory.setReservedQuantity(0);
            inventory.setVersion(0L);
            inventory.setProduct(savedProduct);

            return inventoryRepository.save(inventory).getId();
        });
    }

    @Test
    @DisplayName("Debe fallar un hilo cuando dos intentan reservar el mismo stock simultáneamente")
    void testOptimisticLockingConcurrency() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        Runnable reservationTask = () -> {
            try {
                latch.await(); // Espera a que el latch se libere para correr al mismo tiempo que el otro
                transactionTemplate.execute(status -> {
                    InventoryJpaEntity inv = inventoryRepository.findById(inventoryId).orElseThrow();
                    // Simulo lógica de negocio: reservar 1 unidad
                    inv.setReservedQuantity(inv.getReservedQuantity() + 1);
                    inventoryRepository.save(inv);
                    return null;
                });
                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();
                System.out.println("Error esperado por concurrencia: " + e.getMessage());
            }
        };

        // Lanzo los dos hilos
        executor.submit(reservationTask);
        executor.submit(reservationTask);

        latch.countDown(); // Ambos hilos arrancan aquí
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verificación: Uno debe tener éxito y el otro debe fallar por OptimisticLockingFailureException
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        // El stock final debe reflejar solo una reserva exitosa
        InventoryJpaEntity finalInv = inventoryRepository.findById(inventoryId).orElseThrow();
        assertThat(finalInv.getReservedQuantity()).isEqualTo(1);
        assertThat(finalInv.getVersion()).isEqualTo(1L);
    }

}
