package com.carrilloabogados.client.service;

import java.util.UUID;

import com.carrilloabogados.client.dto.UserAccountDto;
import com.carrilloabogados.client.dto.auth.AuthResponse;
import com.carrilloabogados.client.dto.auth.LoginRequest;
import com.carrilloabogados.client.dto.auth.RegisterRequest;

/**
 * Servicio de autenticación para el sistema Carrillo Abogados.
 * 
 * Soporta dos tipos de autenticación:
 * - LOCAL: Email/Password para clientes del bufete
 * - OAUTH2_GOOGLE: Google Workspace para staff (abogados/admin)
 */
public interface AuthService {

    // ========== Autenticación LOCAL (clientes) ==========

    /**
     * Registra un nuevo cliente con email/password.
     * 
     * @param request Datos de registro
     * @return Respuesta con token JWT
     * @throws IllegalArgumentException si el email ya existe
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Inicia sesión con email/password.
     * 
     * @param request Credenciales
     * @return Respuesta con token JWT
     * @throws IllegalArgumentException si credenciales inválidas
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresca el token de acceso.
     * 
     * @param refreshToken Token de refresco
     * @return Nueva respuesta con token JWT
     */
    AuthResponse refreshToken(String refreshToken);

    // ========== Autenticación OAuth2 (staff) ==========

    /**
     * Procesa el callback de OAuth2 Google.
     * Crea o actualiza la cuenta según el email.
     * Solo permite dominios @carrilloabgd.com para staff.
     * 
     * @param code Código de autorización de Google
     * @return Respuesta con token JWT
     */
    AuthResponse handleOAuth2Callback(String code);

    /**
     * Obtiene la URL de autorización de Google OAuth2.
     * 
     * @param redirectUri URI de redirección después de auth
     * @return URL para redirigir al usuario
     */
    String getOAuth2AuthorizationUrl(String redirectUri);

    // ========== Gestión de sesión ==========

    /**
     * Cierra sesión e invalida el token.
     * 
     * @param accountId ID del usuario
     * @param token     Token a invalidar
     */
    void logout(UUID accountId, String token);

    /**
     * Obtiene el usuario actual desde el token.
     * 
     * @param token Token JWT
     * @return Datos del usuario o null si inválido
     */
    UserAccountDto getCurrentUser(String token);

    /**
     * Obtiene el usuario actual por ID.
     * 
     * @param accountId ID del usuario
     * @return AuthResponse con datos del usuario
     */
    AuthResponse getCurrentUser(UUID accountId);

    /**
     * Verifica si un token es válido.
     * 
     * @param token Token JWT
     * @return true si el token es válido
     */
    boolean validateToken(String token);

    // ========== Verificación de email ==========

    /**
     * Envía email de verificación.
     * 
     * @param accountId ID del usuario
     */
    void sendVerificationEmail(UUID accountId);

    /**
     * Verifica el email con el token.
     * 
     * @param token Token de verificación
     * @return true si se verificó correctamente
     */
    boolean verifyEmail(String token);

    // ========== Recuperación de contraseña ==========

    /**
     * Inicia el proceso de recuperación de contraseña.
     * 
     * @param email Email del usuario
     */
    void forgotPassword(String email);

    /**
     * Resetea la contraseña con el token.
     * 
     * @param token       Token de reset
     * @param newPassword Nueva contraseña
     * @return true si se reseteó correctamente
     */
    boolean resetPassword(String token, String newPassword);

    // ========== Cambio de contraseña ==========

    /**
     * Cambia la contraseña del usuario autenticado.
     * 
     * @param accountId       ID del usuario
     * @param currentPassword Contraseña actual
     * @param newPassword     Nueva contraseña
     * @return true si se cambió correctamente
     */
    boolean changePassword(UUID accountId, String currentPassword, String newPassword);
}
