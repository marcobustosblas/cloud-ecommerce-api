package com.marco.cloud_ecommerce_api.domain.user;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void shouldCreateUserWithCustomerRoleAndActiveStatus() {
        User user = new User("test@example.com", "password123");

        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertTrue(user.hasRole(Role.CUSTOMER));
        assertFalse(user.hasRole(Role.ADMIN));
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("invalid-email", "password123");
        });
    }

    @Test
    void shouldThrowExceptionForShortPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            new User("test@example.com", "123");
        });
    }

    @Test
    void shouldChangeEmail() {
        User user = new User("old@example.com", "password123");
        user.changeEmail("new@example.com");

        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void shouldAddAdminRole() {
        User user = new User("admin@example.com", "password123");
        user.addRole(Role.ADMIN);

        assertTrue(user.hasRole(Role.ADMIN));
        assertTrue(user.isAdmin());
    }

    @Test
    void shouldNotRemoveCustomerIfOnlyRole() {
        User user = new User("test@example.com", "password123");

        assertThrows(IllegalStateException.class, () -> {
            user.removeRole(Role.CUSTOMER);
        });
    }

    @Test
    void shouldActivateAndDeactivate() {
        User user = new User("test@example.com", "password123");
        user.deactivate();

        assertEquals(UserStatus.INACTIVE, user.getStatus());

        user.activate();
        assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void shouldBlockUser() {
        User user = new User("test@example.com", "password123");
        user.block();

        assertEquals(UserStatus.BLOCKED, user.getStatus());
    }

    @Test
    void shouldNotActivateBlockedUser() {
        User user = new User("test@example.com", "password123");
        user.block();

        assertThrows(IllegalStateException.class, user::activate);
    }

    @Test
    void shouldReturnImmutableRolesSet() {
        User user = new User("test@example.com", "password123");
        Set<Role> roles = user.getRoles();

        assertThrows(UnsupportedOperationException.class, () -> {
            roles.add(Role.ADMIN); // Intenta "colar" un rol desde afuera
        });
    }

}
