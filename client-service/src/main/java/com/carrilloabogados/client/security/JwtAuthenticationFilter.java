package com.carrilloabogados.client.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.carrilloabogados.client.domain.RoleBasedAuthority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro que intercepta todas las requests y valida el token JWT.
 * Si el token es válido, establece la autenticación en el SecurityContext.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // Verificar que sea un token de acceso (no refresh)
                if (jwtTokenProvider.isAccessToken(token)) {
                    UUID accountId = jwtTokenProvider.getAccountIdFromToken(token);
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    RoleBasedAuthority role = jwtTokenProvider.getRoleFromToken(token);

                    // Crear objeto de autenticación con el rol
                    var authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(role.name()));

                    // Crear principal con información del usuario
                    var principal = new JwtUserPrincipal(accountId, email, role);

                    var authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null, // credentials no necesarias (ya validamos el token)
                            authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Set authentication for user: {} with role: {}", email, role);
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
