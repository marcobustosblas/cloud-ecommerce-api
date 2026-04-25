package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.domain.user.UserStatus;

import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "cart_id")
    private UUID cartId;

    // Campo técnico para el soft delete
    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserJpaEntity() {}

    public UserJpaEntity(String email, String passwordHash, Set<Role> roles, UserStatus status) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.status = status;
        this.active = true;
    }

    // métodos de negocio para el ciclo de vida
    public void deactivate() {
        this.active = false;
        this.status = UserStatus.INACTIVE; // sincronizo con el estado de negocio
    }

    public void activate() {
        this.active = true;
        this.status = UserStatus.ACTIVE;
    }

    // method helper para agregar rol
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return roles; }
    public UserStatus getStatus() { return status; }
    public UUID getCartId() { return cartId; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }
    public void setActive(boolean active) { this.active = active; }

}
