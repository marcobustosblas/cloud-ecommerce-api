package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.marco.cloud_ecommerce_api.application.user.UserRequestDTO;
import com.marco.cloud_ecommerce_api.application.user.UserResponseDTO;
import com.marco.cloud_ecommerce_api.application.user.UserService;
import com.marco.cloud_ecommerce_api.domain.user.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO registered = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    // -- endpoints de usuario listo autenticado (ahora sin security)

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponseDTO> updateEmail(
            @PathVariable UUID id, @RequestParam String newEmail) {
        return ResponseEntity.ok(userService.updateEmail(id, newEmail));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponseDTO> updatePassword(
            @PathVariable UUID id, @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.updatePassword(id, newPassword));
    }

    // -- endpoints para admin

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponseDTO> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<UserResponseDTO> block(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.blockUser(id));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserResponseDTO> addRole(
            @PathVariable UUID id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.addRole(id, role));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<UserResponseDTO> removeRole(
            @PathVariable UUID id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.removeRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
