package com.marco.cloud_ecommerce_api.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Inventory {
    private UUID id;
    private Integer quantity;
    private Integer reservedQuantity;
    private LocalDateTime lastUpdated;
    private boolean active;

    // 1. Constructor para crear cosas nuevas
    public Inventory(Integer quantity, Integer reservedQuantity, boolean active) {
        this(UUID.randomUUID(), quantity, reservedQuantity, active);
    }

    // 1. Constructor Completo
    public Inventory(UUID id, Integer quantity, Integer reservedQuantity, boolean active) {
        this.id = id;
        this.quantity = (quantity != null) ? quantity : 0;
        this.reservedQuantity = (reservedQuantity != null) ? reservedQuantity : 0;
        this.active = active;
        this.lastUpdated = LocalDateTime.now();
    }

    // 2. Lógica de Dominio (Mis reglas de negocio)
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean hasAvailableStock(int requested) {
        if (requested <= 0) throw new IllegalArgumentException("Units must be > 0");
        if (!active) return false;
        return getAvailableQuantity() >= requested;
    }

    public void deductStock(int amount) {
        if (!hasAvailableStock(amount)) {
            throw new IllegalStateException("Insufficient stock to deduct");
        }
        this.quantity -= amount;
        this.lastUpdated = LocalDateTime.now();
    }

    // 3. Getters (Para que otras capas puedan leer los datos)
    public UUID getId() { return id; }
    public Integer getQuantity() { return quantity; }
    public Integer getReservedQuantity() { return reservedQuantity; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public boolean isActive() { return active; }

    // Nota: No pongo Setters indiscriminados para proteger la integridad del objeto.
    // Los cambios se hacen a través de métodos de negocio como deductStock.
}
