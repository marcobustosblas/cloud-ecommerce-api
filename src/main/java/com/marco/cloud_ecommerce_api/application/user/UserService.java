package com.marco.cloud_ecommerce_api.application.user;

// import jakarta.transaction.Transactional;
import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.domain.user.User;
import com.marco.cloud_ecommerce_api.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final BCryptPasswordEncoder passwordEncoder; // Solo para hashear

    // --- MÉTODOS DE LECTURA ---

    @Transactional(readOnly = true)
    public UserResponseDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id: " + id + " not found"));
        return userDtoMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email: " + email + " not found"));
        return userDtoMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userDtoMapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // --- MÉTODOS DE ESCRITURA ---

    public UserResponseDTO register(UserRequestDTO request) {
        // Valido que el email no existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Hashes la contraseña
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // Creo usuario (dominio)
        User user = userDtoMapper.toDomain(request, passwordHash);

        // Persisto
        User saved = userRepository.save(user);

        // Devuelvo respuesta
        return userDtoMapper.toResponseDTO(saved);
    }

    public UserResponseDTO updateEmail(UUID id, String newEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User with id: " + id + " not found"));
        user.changeEmail(newEmail);
        User updated = userRepository.save(user);
        return userDtoMapper.toResponseDTO(updated);
    }

    public UserResponseDTO updatePassword(UUID id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User with id: " + id + " not found"));
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.changePassword(newPasswordHash);
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO addRole(UUID id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("User with id: " + id + " not found")
                );
        user.addRole(role);
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO removeRole(UUID id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("User with id: " + id + " not found")
                );
        user.removeRole(role);
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("User with id: " + id + " not found")
                );
        user.activate();
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User with id: " + id + " not found"));
        user.deactivate();
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO blockUser(UUID id) {
        User user =  userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found: " + id));
        user.block();
        return userDtoMapper.toResponseDTO(userRepository.save(user));
    }

    public void deleteUser(UUID id) {
        // Soft delete: el usuario se desactiva (con esto logra: active = false)
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new RuntimeException("User with id: " + id + " not found")
                );
        user.deactivate();
        userRepository.save(user);
    }
}
