/**
 * Auth API client for Carrillo Abogados platform.
 * Handles authentication operations with the backend.
 */

import type {
  AuthResponse,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest,
} from '@/types/auth';
import { apiClient, handleApiError } from './client';

const AUTH_BASE_PATH = '/client-service/api/auth';

/**
 * Register a new client account.
 */
export async function registerUser(request: RegisterRequest): Promise<AuthResponse> {
  try {
    const response = await apiClient.post<AuthResponse>(`${AUTH_BASE_PATH}/register`, request);
    return response.data;
  } catch (error) {
    throw handleApiError(error, 'Error al registrar usuario');
  }
}

/**
 * Login with email and password.
 */
export async function loginUser(request: LoginRequest): Promise<AuthResponse> {
  try {
    const response = await apiClient.post<AuthResponse>(`${AUTH_BASE_PATH}/login`, request);
    return response.data;
  } catch (error) {
    throw handleApiError(error, 'Error al iniciar sesión');
  }
}

/**
 * Refresh the access token using a refresh token.
 */
export async function refreshToken(request: RefreshTokenRequest): Promise<AuthResponse> {
  try {
    const response = await apiClient.post<AuthResponse>(`${AUTH_BASE_PATH}/refresh`, request);
    return response.data;
  } catch (error) {
    throw handleApiError(error, 'Error al renovar sesión');
  }
}

/**
 * Get the current authenticated user.
 */
export async function getCurrentUser(): Promise<AuthResponse> {
  try {
    const response = await apiClient.get<AuthResponse>(`${AUTH_BASE_PATH}/me`);
    return response.data;
  } catch (error) {
    throw handleApiError(error, 'Error al obtener usuario');
  }
}

/**
 * Logout the current user.
 */
export async function logoutUser(): Promise<void> {
  try {
    await apiClient.post(`${AUTH_BASE_PATH}/logout`);
  } catch (error) {
    // Ignore logout errors, just clear local state
    console.warn('Logout request failed:', error);
  }
}

/**
 * Initiate Google OAuth2 login.
 * Redirects to Google authorization page.
 */
export function initiateGoogleLogin(redirectUri?: string): void {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const redirect = redirectUri || window.location.origin + '/auth/callback';
  window.location.href = `${baseUrl}${AUTH_BASE_PATH}/oauth2/google?redirect_uri=${encodeURIComponent(redirect)}`;
}

/**
 * Send password reset email.
 */
export async function forgotPassword(email: string): Promise<void> {
  try {
    await apiClient.post(`${AUTH_BASE_PATH}/forgot-password`, { email });
  } catch (error) {
    throw handleApiError(error, 'Error al enviar email de recuperación');
  }
}

/**
 * Reset password with token.
 */
export async function resetPassword(token: string, newPassword: string): Promise<void> {
  try {
    await apiClient.post(`${AUTH_BASE_PATH}/reset-password`, { token, newPassword });
  } catch (error) {
    throw handleApiError(error, 'Error al restablecer contraseña');
  }
}

/**
 * Change password for authenticated user.
 */
export async function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  try {
    await apiClient.post(`${AUTH_BASE_PATH}/change-password`, { currentPassword, newPassword });
  } catch (error) {
    throw handleApiError(error, 'Error al cambiar contraseña');
  }
}

// Re-export all auth functions
export const authApi = {
  register: registerUser,
  login: loginUser,
  refresh: refreshToken,
  getCurrentUser,
  logout: logoutUser,
  initiateGoogleLogin,
  forgotPassword,
  resetPassword,
  changePassword,
};
