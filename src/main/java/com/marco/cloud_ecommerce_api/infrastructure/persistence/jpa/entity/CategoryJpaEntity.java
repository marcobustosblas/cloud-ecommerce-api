package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "categories")
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación inversa con Product
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<ProductJpaEntity> products = new ArrayList<>();

    // 1. Constructor Vacío Obligatorio para Hibernate
    public CategoryJpaEntity() {}

    // 2. Constructor Completo para Rehidratación desde el Mapper / Persistencia
    public CategoryJpaEntity(UUID id, String name, String description, boolean active,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) {this.id = id;}
    // el setId sirve para setUp() en los tests

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public List<ProductJpaEntity> getProducts() { return products; }
    public void setProducts(List<ProductJpaEntity> products) { this.products = products; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    public void prePersist() {
        this.active = true; // Fuerzo estado activo nativo al insertar por primera vez
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
