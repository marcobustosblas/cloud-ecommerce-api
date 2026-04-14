package com.marco.cloud_ecommerce_api.domain.user;

import java.time.LocalDateTime;
import java.util.*;

public class User {
    private final UUID id;
    private String email;
    private String passwordHash;
    private Set<Role> roles;
    private UserStatus status;
    private UUID cartId;
    private final LocalDateTime createdAt;
    private LocalDateTime updateAt;

    // -- CONSTRUCTOR 1: BIRTH (nuevo usuario) --
    public User(String email, String passwordHash) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>();
        this.roles.add(Role.CUSTOMER);
        this.status = UserStatus.ACTIVE;
        this.cartId = null;
        this.createdAt = LocalDateTime.now();
        this.updateAt = this.createdAt;
    }

    // --- CONSTRUCTOR 2: RECONSTRUCTION (desde BD) ---
    public User(UUID id, String email, String passwordHash, Set<Role> roles, UserStatus status,
                UUID cartId, LocalDateTime createdAt, LocalDateTime updateAt) {
        if (id == null) throw new IllegalArgumentException("User ID cannot be null");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Password cannot be null");
        if (roles == null) throw new IllegalArgumentException("Roles cannot be null");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        if (createdAt == null) throw new IllegalArgumentException("CreatedAt cannot be null");
        if (updateAt == null) throw new IllegalArgumentException("UpdatedAt cannot be null");

        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>(roles);
        this.status = status;
        this.cartId = cartId;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    // --- COMMANDS ---

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
        this.updateAt = LocalDateTime.now();
    }

    public void removeCartId() {
        this.cartId = null;
        this.updateAt = LocalDateTime.now();
    }

    // --- GETTERS ---

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public UserStatus getStatus() {
        return status;
    }

    public UUID getCartId() {
        return cartId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

}
