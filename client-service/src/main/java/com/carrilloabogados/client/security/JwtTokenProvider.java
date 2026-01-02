package com.carrilloabogados.client.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.carrilloabogados.client.domain.RoleBasedAuthority;
import com.carrilloabogados.client.domain.UserAccount;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Provider para generar y validar tokens JWT.
 * 
 * Claims incluidos en el token:
 * - sub: accountId (UUID)
 * - email: email del usuario
 * - role: rol del usuario
 * - isStaff: si es abogado o admin
 * - iat: fecha de emisión
 * - exp: fecha de expiración
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:CarrilloAbogadosSecretKey2025DefaultSecretKeyMustBe256bits!!}") String secret,
            @Value("${app.jwt.issuer:carrillo-abogados}") String issuer) {
        // Asegurar que la clave tenga al menos 256 bits
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 bytes)");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
    }

    /**
     * Crea un token de acceso para el usuario.
     * 
     * @param account         Usuario
     * @param validitySeconds Segundos de validez
     * @return Token JWT
     */
    public String createToken(UserAccount account, long validitySeconds) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(validitySeconds);

        return Jwts.builder()
                .subject(account.getAccountId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("email", account.getEmail())
                .claim("role", account.getRole().name())
                .claim("isStaff", account.isStaff())
                .claim("firstName", account.getFirstName())
                .claim("lastName", account.getLastName())
                .claim("type", "access")
                .signWith(secretKey)
                .compact();
    }

    /**
     * Crea un token de refresco para el usuario.
     * Solo incluye información mínima.
     * 
     * @param account         Usuario
     * @param validitySeconds Segundos de validez
     * @return Token JWT de refresco
     */
    public String createRefreshToken(UserAccount account, long validitySeconds) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(validitySeconds);

        return Jwts.builder()
                .subject(account.getAccountId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .claim("type", "refresh")
                .signWith(secretKey)
                .compact();
    }

    /**
     * Valida un token JWT.
     * 
     * @param token Token a validar
     * @return true si el token es válido
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el accountId del token.
     */
    public UUID getAccountIdFromToken(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Obtiene el email del token.
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Obtiene el rol del token.
     */
    public RoleBasedAuthority getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        String roleName = claims.get("role", String.class);
        return RoleBasedAuthority.valueOf(roleName);
    }

    /**
     * Verifica si el token es de tipo acceso.
     */
    public boolean isAccessToken(String token) {
        Claims claims = getClaims(token);
        return "access".equals(claims.get("type", String.class));
    }

    /**
     * Verifica si el token es de tipo refresco.
     */
    public boolean isRefreshToken(String token) {
        Claims claims = getClaims(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    /**
     * Obtiene la fecha de expiración del token.
     */
    public Instant getExpirationFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().toInstant();
    }

    /**
     * Verifica si el token está expirado.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
