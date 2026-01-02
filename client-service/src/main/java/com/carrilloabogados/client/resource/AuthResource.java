package com.carrilloabogados.client.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.client.dto.auth.AuthResponse;
import com.carrilloabogados.client.dto.auth.LoginRequest;
import com.carrilloabogados.client.dto.auth.RefreshTokenRequest;
import com.carrilloabogados.client.dto.auth.RegisterRequest;
import com.carrilloabogados.client.security.JwtUserPrincipal;
import com.carrilloabogados.client.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST API para autenticación y registro de usuarios.
 * 
 * Endpoints:
 * - POST /api/auth/register - Registro de nuevos clientes
 * - POST /api/auth/login - Inicio de sesión
 * - POST /api/auth/refresh - Renovar token
 * - GET /api/auth/me - Obtener usuario actual
 * - POST /api/auth/logout - Cerrar sesión (client-side)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro")
public class AuthResource {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo cliente", description = "Permite el auto-registro de clientes potenciales. Los abogados y admins se crean internamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ya registrado")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for email: {}", request.getEmail());

        AuthResponse response = authService.register(request);

        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve tokens JWT de acceso y refresco")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "Cuenta deshabilitada o no verificada")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        AuthResponse response = authService.login(request);

        log.info("User logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acceso", description = "Usa el refresh token para obtener un nuevo access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request received");

        AuthResponse response = authService.refreshToken(request.getRefreshToken());

        log.debug("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Información del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Get current user: {}", principal.getEmail());

        AuthResponse response = authService.getCurrentUser(principal.getAccountId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalida la sesión actual. El cliente debe eliminar los tokens localmente.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<Void> logout(@AuthenticationPrincipal JwtUserPrincipal principal) {
        if (principal != null) {
            log.info("User logged out: {}", principal.getEmail());
            // En una implementación más completa, aquí se podría:
            // 1. Agregar el token a una blacklist
            // 2. Invalidar refresh tokens en BD
            // Por ahora, el logout es client-side (eliminar tokens)
        }
        return ResponseEntity.ok().build();
    }
}
