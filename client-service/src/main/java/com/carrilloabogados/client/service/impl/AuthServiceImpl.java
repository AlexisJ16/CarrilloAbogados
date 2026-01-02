package com.carrilloabogados.client.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrilloabogados.client.domain.RoleBasedAuthority;
import com.carrilloabogados.client.domain.UserAccount;
import com.carrilloabogados.client.domain.UserAccount.AuthProvider;
import com.carrilloabogados.client.dto.UserAccountDto;
import com.carrilloabogados.client.dto.auth.AuthResponse;
import com.carrilloabogados.client.dto.auth.LoginRequest;
import com.carrilloabogados.client.dto.auth.RegisterRequest;
import com.carrilloabogados.client.exception.ResourceNotFoundException;
import com.carrilloabogados.client.helper.UserAccountMappingHelper;
import com.carrilloabogados.client.repository.UserAccountRepository;
import com.carrilloabogados.client.security.JwtTokenProvider;
import com.carrilloabogados.client.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // Token expiration times
    private static final long ACCESS_TOKEN_VALIDITY = 24 * 60 * 60; // 24 hours
    private static final long REFRESH_TOKEN_VALIDITY = 30 * 24 * 60 * 60; // 30 days
    private static final long REMEMBER_ME_VALIDITY = 30 * 24 * 60 * 60; // 30 days

    // ========== Autenticación LOCAL (clientes) ==========

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("*** AuthService: Registering new client - email: {} *", request.getEmail());

        // Validar que el email no exista
        if (userAccountRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar que acepte términos
        if (request.getAcceptTerms() == null || !request.getAcceptTerms()) {
            throw new IllegalArgumentException("Debe aceptar los términos y condiciones");
        }

        if (request.getAcceptPrivacy() == null || !request.getAcceptPrivacy()) {
            throw new IllegalArgumentException("Debe aceptar la política de privacidad");
        }

        // Validar que no sea dominio corporativo (staff debe usar OAuth2)
        if (request.getEmail().toLowerCase().endsWith("@carrilloabgd.com")) {
            throw new IllegalArgumentException(
                    "Los usuarios del dominio @carrilloabgd.com deben registrarse con Google Workspace");
        }

        // Crear cuenta
        UserAccount account = UserAccountMappingHelper.mapFromRegister(request);
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        UserAccount savedAccount = userAccountRepository.save(account);
        log.info("*** AuthService: Client registered successfully - id: {} *", savedAccount.getAccountId());

        // TODO: Enviar email de verificación
        // sendVerificationEmail(savedAccount.getAccountId());

        return buildAuthResponse(savedAccount, ACCESS_TOKEN_VALIDITY);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("*** AuthService: Login attempt - email: {} *", request.getEmail());

        // Buscar cuenta
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        // Verificar que sea auth LOCAL
        if (account.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException(
                    "Esta cuenta usa autenticación de Google. Por favor, inicie sesión con Google.");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            log.warn("*** AuthService: Invalid password for email: {} *", request.getEmail());
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Verificar que la cuenta esté activa
        if (!account.getIsActive()) {
            throw new IllegalArgumentException("La cuenta está desactivada");
        }

        // Actualizar último login
        account.setLastLoginAt(Instant.now());
        userAccountRepository.save(account);

        long tokenValidity = (request.getRememberMe() != null && request.getRememberMe())
                ? REMEMBER_ME_VALIDITY
                : ACCESS_TOKEN_VALIDITY;

        log.info("*** AuthService: Login successful - id: {} *", account.getAccountId());
        return buildAuthResponse(account, tokenValidity);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("*** AuthService: Refreshing token *");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Token de refresco inválido o expirado");
        }

        UUID accountId = jwtTokenProvider.getAccountIdFromToken(refreshToken);
        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        if (!account.getIsActive()) {
            throw new IllegalArgumentException("La cuenta está desactivada");
        }

        return buildAuthResponse(account, ACCESS_TOKEN_VALIDITY);
    }

    // ========== Autenticación OAuth2 (staff) ==========

    @Override
    public AuthResponse handleOAuth2Callback(String code) {
        log.info("*** AuthService: Processing OAuth2 callback *");

        // TODO: Implementar flujo OAuth2 completo con Google
        // Por ahora, este es un placeholder que será implementado cuando
        // se configure Spring Security OAuth2

        throw new UnsupportedOperationException(
                "OAuth2 callback no implementado. Configure Spring Security OAuth2 Client.");
    }

    @Override
    public String getOAuth2AuthorizationUrl(String redirectUri) {
        // TODO: Implementar URL de autorización OAuth2
        // Esto se manejará a través de Spring Security OAuth2
        throw new UnsupportedOperationException(
                "OAuth2 authorization URL se maneja via Spring Security OAuth2.");
    }

    // ========== Gestión de sesión ==========

    @Override
    public void logout(UUID accountId, String token) {
        log.info("*** AuthService: Logout - accountId: {} *", accountId);
        // TODO: Agregar token a lista negra o invalidar en cache
        // Por ahora JWT es stateless, el logout se maneja en el cliente
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDto getCurrentUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return null;
        }

        UUID accountId = jwtTokenProvider.getAccountIdFromToken(token);
        return userAccountRepository.findById(accountId)
                .map(UserAccountMappingHelper::map)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser(UUID accountId) {
        log.info("*** AuthService: Getting current user - accountId: {} *", accountId);

        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        // Devuelve AuthResponse sin generar nuevos tokens
        return AuthResponse.builder()
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .fullName(account.getFullName())
                .avatarUrl(account.getAvatarUrl())
                .role(account.getRole())
                .isStaff(account.isStaff())
                .lastLoginAt(account.getLastLoginAt())
                .permissions(getPermissionsForRole(account.getRole()))
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    // ========== Verificación de email ==========

    @Override
    public void sendVerificationEmail(UUID accountId) {
        log.info("*** AuthService: Sending verification email - accountId: {} *", accountId);
        // TODO: Integrar con notification-service
    }

    @Override
    public boolean verifyEmail(String token) {
        log.info("*** AuthService: Verifying email with token *");
        // TODO: Implementar verificación de email
        return false;
    }

    // ========== Recuperación de contraseña ==========

    @Override
    public void forgotPassword(String email) {
        log.info("*** AuthService: Password recovery requested - email: {} *", email);
        // TODO: Integrar con notification-service para enviar email
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        log.info("*** AuthService: Password reset attempt *");
        // TODO: Implementar reset de contraseña
        return false;
    }

    @Override
    public boolean changePassword(UUID accountId, String currentPassword, String newPassword) {
        log.info("*** AuthService: Password change - accountId: {} *", accountId);

        UserAccount account = userAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        if (account.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("Esta cuenta usa autenticación de Google");
        }

        if (!passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        userAccountRepository.save(account);

        log.info("*** AuthService: Password changed successfully - accountId: {} *", accountId);
        return true;
    }

    // ========== Métodos auxiliares ==========

    private AuthResponse buildAuthResponse(UserAccount account, long tokenValidity) {
        String accessToken = jwtTokenProvider.createToken(account, tokenValidity);
        String refreshToken = jwtTokenProvider.createRefreshToken(account, REFRESH_TOKEN_VALIDITY);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(tokenValidity)
                .refreshToken(refreshToken)
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .fullName(account.getFullName())
                .avatarUrl(account.getAvatarUrl())
                .role(account.getRole())
                .isStaff(account.isStaff())
                .lastLoginAt(account.getLastLoginAt())
                .permissions(getPermissionsForRole(account.getRole()))
                .build();
    }

    private String[] getPermissionsForRole(RoleBasedAuthority role) {
        return switch (role) {
            case ROLE_ADMIN -> new String[] {
                    "admin:*", "lawyer:*", "client:*", "lead:*", "case:*",
                    "document:*", "calendar:*", "notification:*", "settings:*"
            };
            case ROLE_LAWYER -> new String[] {
                    "lawyer:read", "lawyer:write",
                    "client:read", "client:write",
                    "case:read", "case:write",
                    "document:read", "document:write",
                    "calendar:read", "calendar:write"
            };
            case ROLE_CLIENT -> new String[] {
                    "client:read:own", "client:write:own",
                    "case:read:own",
                    "document:read:own", "document:write:own"
            };
            case ROLE_VISITOR -> new String[] {
                    "lead:create", "public:read"
            };
        };
    }
}
