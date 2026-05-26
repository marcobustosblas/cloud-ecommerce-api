package com.marco.cloud_ecommerce_api.application.user;

import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.domain.user.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class UserResponseDTO {

    private UUID id;
    private String email;
    private Set<Role> roles;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDTO(UUID id, String email, Set<Role> roles, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
