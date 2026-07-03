package com.marco.cloud_ecommerce_api.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marco.cloud_ecommerce_api.infrastructure.api.dto.AuthRequest;
import com.marco.cloud_ecommerce_api.infrastructure.api.dto.RegisterRequest;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    // TEST 1: Registro exitoso

    @Test
    void register_shouldReturnSuccess_whenValidRequest() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("1234567")
                .build();

        ResultActions result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value(containsString("Usuario registrado exitosamente")));
    }

    @Test
    void login_shouldReturnToken_whenValidCredentials() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("login@example.com")
                .password("123456")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        AuthRequest loginRequest = AuthRequest.builder()
                .email("login@example.com")
                .password("123456")
                .build();

        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.email").value("login@example.com"))
                .andExpect(jsonPath("$.message").value("Login exitoso"));
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("fail@example.com")
                .password("123456")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        AuthRequest loginRequest = AuthRequest.builder()
                .email("fail@example.com")
                .password("wrongpassword")
                .build();

        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("Credenciales inválidas")));
    }

}
