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
    private LocalDateTime updatedAt;

    // -- CONSTRUCTOR 1: BIRTH (nuevo usuario) --
    public User(String email, String passwordHash) {
        this.id = UUID.randomUUID();
        setEmail(email);
        setPasswordHash(passwordHash);
        this.roles = new HashSet<>();
        this.roles.add(Role.CUSTOMER);
        this.status = UserStatus.ACTIVE;
        this.cartId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
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
        this.updatedAt = updateAt;
    }

    // --- Validaciones del constructor ---

    private void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    private void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        if (passwordHash.length() <= 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters");
        }
        this.passwordHash = passwordHash;
    }

    // --- UPDATE METHODS

    public void changeEmail(String newEmail) {
        setEmail(newEmail);
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(String newPasswordHash) {
        setPasswordHash(newPasswordHash);
        this.updatedAt = LocalDateTime.now();
    }

    // --- ROLE MANAGEMENT

    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (this.roles.add(role)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (role == Role.CUSTOMER && roles.size() == 1) {
            throw new IllegalArgumentException("User must have at least one role. Cannot remove the last one.");
        }
        if (roles.remove(role)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    // --- STATUS MANAGEMENT

    public void activate() {
        if (this.status == UserStatus.BLOCKED) {
            throw new IllegalStateException("Blocked users cannot be activated");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == UserStatus.BLOCKED) {
            throw new IllegalStateException("Blocked users cannot be deactivated");
        }
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
        this.updatedAt = LocalDateTime.now();
    }

    // --- CART MANAGEMENT, commands ---

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeCartId() {
        this.cartId = null;
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
