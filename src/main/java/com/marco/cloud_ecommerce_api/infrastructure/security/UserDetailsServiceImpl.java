package com.marco.cloud_ecommerce_api.infrastructure.security;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Devuelvo el usuario activo por email
        return userRepository.findActiveByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email
                ));
    }
}
