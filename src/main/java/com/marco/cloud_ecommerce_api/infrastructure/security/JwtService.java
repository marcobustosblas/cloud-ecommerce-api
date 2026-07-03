package com.marco.cloud_ecommerce_api.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Extrae el email (username) del token JWT
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // method genérico para extraer cualquier información (Claim) del interior del token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Abre y parsea el token validando la firma criptográfica.
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Sintaxis actualizada para JJWT 0.12
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Transforma la clave secreta en formato Base64 de las propiedades a una SecretKey segura
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera un token JWT estándar solo con los detalles mínimos del usuario.
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Genera un token JWT incluyendo claims (datos) adicionales como los Roles del usuario.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Extraigo los roles para meterlos en el token y evitar consultas extra a la DB en el futuro
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .toList());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) // El email del usuario
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Criptografía actualizada para JJWT 0.12
                .compact();
    }

    // Valida si el token pertenece al usuario y si no ha expirado.

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
