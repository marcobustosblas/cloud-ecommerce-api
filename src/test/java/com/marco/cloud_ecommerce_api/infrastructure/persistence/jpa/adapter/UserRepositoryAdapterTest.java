package com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.adapter;

import com.marco.cloud_ecommerce_api.domain.user.Role;
import com.marco.cloud_ecommerce_api.domain.user.User;
import com.marco.cloud_ecommerce_api.domain.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class UserRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private UserRepositoryAdapter userRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = new User("test@example.com", "estoy-sabroso123");

        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@example.com");
        assertThat(found.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User("find@example.com", "estoy-jugoso123");
        User saved = userRepository.save(user);

        User found = userRepository.findByEmail("find@example.com").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("find@example.com");
    }

    @Test
    void shouldCheckIfEmailExists() {
        User user = new User("exists@gmail.com", "estoy-sexy123");
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("exists@gmail.com")).isTrue();
        assertThat(userRepository.existsByEmail("noexist@gmail.com")).isFalse();
    }

    @Test
    void shouldSaveUserWithRoles() {
        User user = new User("admin@gmail.com", "soyelbigboss@company.com");
        user.addRole(Role.ADMIN);

        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.hasRole(Role.ADMIN)).isTrue();
        assertThat(found.hasRole(Role.CUSTOMER)).isTrue();
    }

}
