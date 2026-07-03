package com.marco.cloud_ecommerce_api.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta cada petición HTTP entrante.
 * Extrae y valida el token JWT del encabezado 'Authorization'.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer el encabezado Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Validar si el encabezado existe y contiene el prefijo correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Aislar el string del token puro (restando los 7 caracteres de 'Bearer ')
        final String token = authHeader.substring(7);
        final String email = jwtService.extractEmail(token);

        // 4. Si el token tiene un email válido y el usuario no está ya autenticado en el contexto actual
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el UserDetails desde la DB usando el email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // Verificar matemáticamente la firma y la expiración con la secretKey
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Conceder el acceso al hilo de ejecución de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar el viaje hacia el Controller o el siguiente filtro
        filterChain.doFilter(request, response);
    }
}