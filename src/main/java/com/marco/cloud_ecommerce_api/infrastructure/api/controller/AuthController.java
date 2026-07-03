package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.marco.cloud_ecommerce_api.application.user.UserRequestDTO;
import com.marco.cloud_ecommerce_api.application.user.UserResponseDTO;
import com.marco.cloud_ecommerce_api.application.user.UserService;
import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.infrastructure.api.dto.AuthRequest;
import com.marco.cloud_ecommerce_api.infrastructure.api.dto.AuthResponse;
import com.marco.cloud_ecommerce_api.infrastructure.api.dto.RegisterRequest;
import com.marco.cloud_ecommerce_api.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toSet());

            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .email(userDetails.getUsername())
                    .roles(roles)
                    .message("Login exitoso")
                    .build();

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .message("Credenciales inválidas")
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserRequestDTO userRequest = new UserRequestDTO();
            userRequest.setEmail(request.getEmail());
            userRequest.setPassword(request.getPassword());

            UserResponseDTO savedUser = userService.register(userRequest);

            // si el cliente solicitó un rol especifico (como ADMIN) se lo agrego
            if (request.getRole() != null && request.getRole() != Role.CUSTOMER) {
                userService.addRole(savedUser.getId(), request.getRole());
                savedUser = userService.findById(savedUser.getId());
            }

            AuthResponse response = AuthResponse.builder()
                    .email(savedUser.getEmail())
                    .roles(savedUser.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                    .message("Usuario registrado exitosamente. Por favor inicia sesión para generar tu token.")
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(AuthResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

}
