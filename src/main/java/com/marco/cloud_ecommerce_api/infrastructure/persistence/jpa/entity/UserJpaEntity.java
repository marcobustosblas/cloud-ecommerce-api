package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity;

import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.domain.user.UserStatus;

import jakarta.persistence.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)
public class UserJpaEntity implements UserDetails {

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

    // ELIMINO EL CAMPO 'active' DE AQUÍ. Hibernate lo crea en la DB por el @SoftDelete.
    // El campo 'active' me dio más problemas que la cresta

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false) // error visto en el name el 27-04
    private LocalDateTime updatedAt;

    public UserJpaEntity() {}

    public UserJpaEntity(String email, String passwordHash, Set<Role> roles, UserStatus status) {
        // this.id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.status = status;
    }

    // All-Args para Rehidratación
    public UserJpaEntity(UUID id, String email, String passwordHash, Set<Role> roles,
                         UserStatus status, UUID cartId,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.status = status;
        this.cartId = cartId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // métodos de negocio para el ciclo de vida
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    // method helper para agregar rol
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // MÉTODOS DE UserDetails (Spring Security)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
        // mi email actúa como el username único de inicio de sesión
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
        // No manejo expiración de cuenta todavía
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
        // No manejo bloqueo de cuenta todavía
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
        // No manejo expiración de credenciales
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.ACTIVE;
        // Un usuario está habilitado si su estado es ACTIVE
        // Con @SoftDelete, Hibernate filtra automáticamente los eliminados
    }

    // Getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return roles; }
    public UserStatus getStatus() { return status; }
    public UUID getCartId() { return cartId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void setStatus(UserStatus status) { this.status = status; }
    public void setCartId(UUID cartId) { this.cartId = cartId; }

    public void setActive(boolean active) {
        if (active) activate(); else deactivate();
    }
}
