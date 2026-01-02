'use client';

import { authApi, tokenStorage } from '@/lib/api';
import {
  AuthContextType,
  AuthResponse,
  AuthState,
  LoginRequest,
  RegisterRequest,
  User,
} from '@/types/auth';
import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';

// ========== Initial State ==========

const initialState: AuthState = {
  isAuthenticated: false,
  isLoading: true,
  user: null,
  accessToken: null,
  refreshToken: null,
  permissions: [],
  error: null,
};

// ========== Context ==========

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// ========== Helper Functions ==========

function mapAuthResponseToUser(response: AuthResponse): User {
  return {
    accountId: response.accountId,
    email: response.email,
    firstName: response.firstName,
    lastName: response.lastName,
    fullName:
      response.fullName ||
      `${response.firstName || ''} ${response.lastName || ''}`.trim() ||
      response.email,
    avatarUrl: response.avatarUrl,
    phone: undefined,
    role: response.role,
    isStaff: response.isStaff,
    isActive: true,
    isEmailVerified: true,
    authProvider: 'LOCAL',
    lastLoginAt: response.lastLoginAt,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  };
}

// ========== Provider Component ==========

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>(initialState);

  // Initialize auth state from localStorage
  useEffect(() => {
    const initializeAuth = async () => {
      const hasTokens = tokenStorage.hasTokens();

      if (!hasTokens) {
        setState((prev) => ({ ...prev, isLoading: false }));
        return;
      }

      try {
        // Validate token by fetching current user
        const response = await authApi.getCurrentUser();
        const user = mapAuthResponseToUser(response);

        setState({
          isAuthenticated: true,
          isLoading: false,
          user,
          accessToken: tokenStorage.getAccessToken(),
          refreshToken: tokenStorage.getRefreshToken(),
          permissions: response.permissions || [],
          error: null,
        });
      } catch (error) {
        console.error('Failed to restore auth session:', error);
        tokenStorage.clearTokens();
        setState({
          ...initialState,
          isLoading: false,
        });
      }
    };

    initializeAuth();
  }, []);

  // Login handler
  const login = useCallback(async (request: LoginRequest) => {
    setState((prev) => ({ ...prev, isLoading: true, error: null }));

    try {
      const response = await authApi.login(request);

      if (response.accessToken && response.refreshToken) {
        tokenStorage.setTokens(response.accessToken, response.refreshToken);
      }

      const user = mapAuthResponseToUser(response);

      setState({
        isAuthenticated: true,
        isLoading: false,
        user,
        accessToken: response.accessToken || null,
        refreshToken: response.refreshToken || null,
        permissions: response.permissions || [],
        error: null,
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Error al iniciar sesión';
      setState((prev) => ({
        ...prev,
        isLoading: false,
        error: message,
      }));
      throw error;
    }
  }, []);

  // Register handler
  const register = useCallback(async (request: RegisterRequest) => {
    setState((prev) => ({ ...prev, isLoading: true, error: null }));

    try {
      const response = await authApi.register(request);

      if (response.accessToken && response.refreshToken) {
        tokenStorage.setTokens(response.accessToken, response.refreshToken);
      }

      const user = mapAuthResponseToUser(response);

      setState({
        isAuthenticated: true,
        isLoading: false,
        user,
        accessToken: response.accessToken || null,
        refreshToken: response.refreshToken || null,
        permissions: response.permissions || [],
        error: null,
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Error al registrar usuario';
      setState((prev) => ({
        ...prev,
        isLoading: false,
        error: message,
      }));
      throw error;
    }
  }, []);

  // Logout handler
  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } finally {
      tokenStorage.clearTokens();
      setState({
        ...initialState,
        isLoading: false,
      });
    }
  }, []);

  // Refresh auth
  const refreshAuth = useCallback(async () => {
    const refreshTokenValue = tokenStorage.getRefreshToken();
    if (!refreshTokenValue) {
      throw new Error('No refresh token available');
    }

    try {
      const response = await authApi.refresh({ refreshToken: refreshTokenValue });

      if (response.accessToken && response.refreshToken) {
        tokenStorage.setTokens(response.accessToken, response.refreshToken);
      }

      const user = mapAuthResponseToUser(response);

      setState((prev) => ({
        ...prev,
        user,
        accessToken: response.accessToken || null,
        refreshToken: response.refreshToken || null,
        permissions: response.permissions || [],
      }));
    } catch (error) {
      tokenStorage.clearTokens();
      setState({
        ...initialState,
        isLoading: false,
      });
      throw error;
    }
  }, []);

  // Clear error
  const clearError = useCallback(() => {
    setState((prev) => ({ ...prev, error: null }));
  }, []);

  const value: AuthContextType = {
    ...state,
    login,
    register,
    logout,
    refreshAuth,
    clearError,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// ========== Hook ==========

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

// ========== Guard Components ==========

type UserRole = 'ROLE_CLIENT' | 'ROLE_LAWYER' | 'ROLE_ADMIN';

interface AuthGuardProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
  requiredRole?: UserRole;
  requiredRoles?: UserRole[];
  requireStaff?: boolean;
}

export function AuthGuard({
  children,
  fallback,
  requiredRole,
  requiredRoles,
  requireStaff,
}: AuthGuardProps) {
  const { isAuthenticated, isLoading, user } = useAuth();

  if (isLoading) {
    return fallback || <div>Cargando...</div>;
  }

  if (!isAuthenticated) {
    return fallback || null;
  }

  // Check single required role
  if (requiredRole && user?.role !== requiredRole) {
    return fallback || null;
  }

  // Check multiple allowed roles
  if (requiredRoles && requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.includes(user?.role as UserRole);
    if (!hasRequiredRole) {
      return fallback || null;
    }
  }

  if (requireStaff && !user?.isStaff) {
    return fallback || null;
  }

  return <>{children}</>;
}

// ========== Permission Hook ==========

export function usePermission(permission: string): boolean {
  const { permissions } = useAuth();

  // Check exact match
  if (permissions.includes(permission)) {
    return true;
  }

  // Check wildcard permissions
  const parts = permission.split(':');
  if (parts.length >= 1) {
    const wildcardPermission = `${parts[0]}:*`;
    if (permissions.includes(wildcardPermission)) {
      return true;
    }
  }

  return false;
}

export default AuthContext;
