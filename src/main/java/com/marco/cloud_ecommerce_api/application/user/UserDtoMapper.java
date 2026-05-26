package com.marco.cloud_ecommerce_api.application.user;

import com.marco.cloud_ecommerce_api.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    /* RequestDTO → Domain (para crear un nuevo usuario)
    *  Nota: el service entrega el passwordHash
    */
    public User toDomain(UserRequestDTO request, String passwordHash) {
        if (request == null) return null;
        return new User(request.getEmail(), passwordHash);
    }

    /*
     * Domain → ResponseDTO
     * Excluye passwordHash y cartId por seguridad
     */
    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getRoles(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
