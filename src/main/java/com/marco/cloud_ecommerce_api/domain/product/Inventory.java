package com.marco.cloud_ecommerce_api.domain.product;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Inventory {
    private UUID id;
    private int quantity;
    private int reservedQuantity;
    private LocalDateTime lastUpdated;

    // Constructor para JPA (Indispensable para mañana)
    protected Inventory() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Constructor para crear cosas nuevas
    public Inventory(int quantity, int reservedQuantity) {
        this(UUID.randomUUID(), quantity, reservedQuantity);
    }

    // Constructor Completo (El que usará JPA mañana)
    public Inventory(UUID id, int quantity, int reservedQuantity) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        validateQuantity(quantity);
        if (reservedQuantity < 0)
            throw new IllegalArgumentException("Reserved quantity cannot be negative");
        if (reservedQuantity > quantity)
            throw new IllegalArgumentException("Reserved quantity [" + reservedQuantity + "] cannot exceed total quantity [" + quantity + "]");
        this.id = id;
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.lastUpdated = LocalDateTime.now();
    }

    // Validaciones para el constructor:
    private void validateQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Total quantity cannot be negative");
    }

    // Lógica de Dominio (Mis reglas de negocio)

    // --- COMANDOS (ahora son de uso del agregado Product) ---

    void restock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Restock amount must be positive");
        this.quantity += amount;
        checkInvariants();
        this.lastUpdated = LocalDateTime.now();
    }

    void reserveStock(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Reservation amount must be positive");
        if (getAvailableQuantity() < amount) throw new IllegalStateException("Insufficient stock. Available: " + getAvailableQuantity() + ", Requested: " + amount);
        this.reservedQuantity += amount;
        checkInvariants();
        this.lastUpdated = LocalDateTime.now();
    }

    void confirmReservation(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
        if (amount > this.reservedQuantity)
            throw new IllegalStateException("Cannot confirm [" + amount + "] units. Only [" + reservedQuantity + "] are reserved");
        this.quantity -= amount; // ¡Error salvado, había puesto =-!
        this.reservedQuantity -= amount;
        checkInvariants();
        this.lastUpdated = LocalDateTime.now();
    }

    void releaseReservation(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Release amount must be positive");
        }
        if (amount > this.reservedQuantity) {
            throw new IllegalStateException(
                    "Cannot release [" + amount + "] units. Only [" + reservedQuantity + "] are reserved"
            );
        }
        this.reservedQuantity -= amount;
        checkInvariants();
        this.lastUpdated = LocalDateTime.now();
    }

    void clearAllReservations() {
        if (this.reservedQuantity == 0) return;
        this.reservedQuantity = 0;
        checkInvariants();
        this.lastUpdated = LocalDateTime.now();
    }

    // --- QUERIES ---

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean hasAvailableStock(int requested) {
        if (requested <= 0) {
            throw new IllegalArgumentException("Requested amount must be positive");
            // incluso en queries debo proteger mi dominio.
        }
        return getAvailableQuantity() >= requested;
    }

    private void checkInvariants() { // EL GUARDIÁN
        if (quantity < 0) throw new IllegalStateException("Data corruption: total quantity is negative");
        if (reservedQuantity < 0) throw new IllegalStateException("Data corruption: reserved quantity is negative");
        if (reservedQuantity > quantity) throw new IllegalStateException("Data corruption: reserved exceeds total");
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // --- IDENTITY (Fundamental para JPA) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory that)) return false;
        if (this.id == null || that.id == null) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    // 3. Getters (Para que otras capas puedan leer los datos)
    public UUID getId() { return id; }
    public int getQuantity() { return quantity; }
    public int getReservedQuantity() { return reservedQuantity; }

    // Nota: No pongo Setters indiscriminados para proteger la integridad del objeto.
    // Los cambios se hacen a través de métodos de negocio como deductStock.
}
