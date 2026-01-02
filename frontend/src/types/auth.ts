/**
 * Types for authentication in Carrillo Abogados platform.
 */

// ========== Enums ==========

export type UserRole = 'ROLE_VISITOR' | 'ROLE_CLIENT' | 'ROLE_LAWYER' | 'ROLE_ADMIN';

export type AuthProvider = 'LOCAL' | 'OAUTH2_GOOGLE';

// ========== Request DTOs ==========

export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  acceptTerms: boolean;
  acceptPrivacy: boolean;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

// ========== Response DTOs ==========

export interface AuthResponse {
  // Token info
  accessToken?: string;
  tokenType?: string;
  expiresIn?: number;
  refreshToken?: string;

  // User info
  accountId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  avatarUrl?: string;
  role: UserRole;
  isStaff: boolean;
  lastLoginAt?: string;

  // Permissions
  permissions?: string[];
}

export interface User {
  accountId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName: string;
  avatarUrl?: string;
  phone?: string;
  role: UserRole;
  isStaff: boolean;
  isActive: boolean;
  isEmailVerified: boolean;
  authProvider: AuthProvider;
  lastLoginAt?: string;
  createdAt: string;
  updatedAt: string;
}

// ========== Auth State ==========

export interface AuthState {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  permissions: string[];
  error: string | null;
}

export interface AuthContextType extends AuthState {
  login: (request: LoginRequest) => Promise<void>;
  register: (request: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  refreshAuth: () => Promise<void>;
  clearError: () => void;
}

// ========== Utility Types ==========

export type Permission =
  | 'admin:*'
  | 'lawyer:*'
  | 'client:*'
  | 'lead:*'
  | 'case:*'
  | 'document:*'
  | 'calendar:*'
  | 'notification:*'
  | 'settings:*'
  | 'lawyer:read'
  | 'lawyer:write'
  | 'client:read'
  | 'client:write'
  | 'case:read'
  | 'case:write'
  | 'document:read'
  | 'document:write'
  | 'calendar:read'
  | 'calendar:write'
  | 'client:read:own'
  | 'client:write:own'
  | 'case:read:own'
  | 'document:read:own'
  | 'document:write:own'
  | 'lead:create'
  | 'public:read';

export function isStaffRole(role: UserRole): boolean {
  return role === 'ROLE_LAWYER' || role === 'ROLE_ADMIN';
}

export function getRoleDisplayName(role: UserRole): string {
  switch (role) {
    case 'ROLE_ADMIN':
      return 'Administrador';
    case 'ROLE_LAWYER':
      return 'Abogado';
    case 'ROLE_CLIENT':
      return 'Cliente';
    case 'ROLE_VISITOR':
      return 'Visitante';
    default:
      return 'Usuario';
  }
}
