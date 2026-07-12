package com.marco.cloud_ecommerce_api.domain.category;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Category {
    private final UUID id;
    private String name;
    private String description;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor para nueva categoría
    public Category(String name, String description) {
        validateName(name);
        validateDescription(description);
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    // Constructor RECONSTRUCTION (desde BD)
    public Category(UUID id, String name, String description, boolean active,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (id == null) throw new IllegalArgumentException("ID cannot be null");
        validateName(name);
        validateDescription(description);
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
        this.id = id;
        this.name = name;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Keep private: No reason for public exposure.
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
    }

    public void updateDescription(String newDescription) {
        validateDescription(newDescription);
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }

    public void rename(String newName) {
        validateName(newName);
        this.name = newName;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {return this.id;}
    public String getName() {return this.name;}
    public String getDescription() { return this.description; }
    public boolean isActive() {return this.active;}
    public LocalDateTime getCreatedAt() {return this.createdAt;}
    public LocalDateTime getUpdatedAt() {return this.updatedAt;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
