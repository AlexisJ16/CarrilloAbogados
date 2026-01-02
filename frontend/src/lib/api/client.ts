/**
 * Cliente API base para comunicación con el backend.
 * Soporta autenticación JWT con refresh automático.
 */

import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Token storage keys
const ACCESS_TOKEN_KEY = 'carrillo_access_token';
const REFRESH_TOKEN_KEY = 'carrillo_refresh_token';

// Cookie helpers for middleware compatibility
const setCookie = (name: string, value: string, days: number = 30): void => {
  if (typeof document === 'undefined') return;
  const expires = new Date();
  expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
  // SameSite=Lax for security, secure in production
  const secure = window.location.protocol === 'https:' ? '; Secure' : '';
  document.cookie = `${name}=${value}; expires=${expires.toUTCString()}; path=/${secure}; SameSite=Lax`;
};

const deleteCookie = (name: string): void => {
  if (typeof document === 'undefined') return;
  document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
};

// ========== Token Storage ==========

export const tokenStorage = {
  getAccessToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  getRefreshToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  setTokens: (accessToken: string, refreshToken: string): void => {
    if (typeof window === 'undefined') return;
    // Store in localStorage for client-side access
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    // Also store in cookies for middleware (server-side) access
    setCookie('access_token', accessToken, 1); // 1 day for access token
    setCookie('refresh_token', refreshToken, 30); // 30 days for refresh token
  },

  clearTokens: (): void => {
    if (typeof window === 'undefined') return;
    // Clear localStorage
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    // Clear cookies
    deleteCookie('access_token');
    deleteCookie('refresh_token');
  },

  hasTokens: (): boolean => {
    return !!tokenStorage.getAccessToken();
  },
};

// ========== API Error ==========

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public data?: unknown
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

// ========== Axios Instance ==========

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: Add auth token
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenStorage.getAccessToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: Handle 401 and refresh token
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Skip refresh for auth endpoints
    if (originalRequest.url?.includes('/api/auth/')) {
      return Promise.reject(error);
    }

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue request while refreshing
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${token}`;
          }
          return apiClient(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = tokenStorage.getRefreshToken();
      if (!refreshToken) {
        tokenStorage.clearTokens();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      try {
        const response = await axios.post(`${API_BASE_URL}/client-service/api/auth/refresh`, {
          refreshToken,
        });

        const { accessToken, refreshToken: newRefreshToken } = response.data;
        tokenStorage.setTokens(accessToken, newRefreshToken);

        processQueue(null, accessToken);

        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        }
        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        tokenStorage.clearTokens();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

// ========== Error Handler ==========

export function handleApiError(error: unknown, defaultMessage: string): ApiError {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string }>;
    const status = axiosError.response?.status || 0;
    const message = axiosError.response?.data?.message || axiosError.message || defaultMessage;
    return new ApiError(message, status, axiosError.response?.data);
  }

  if (error instanceof ApiError) {
    return error;
  }

  return new ApiError(error instanceof Error ? error.message : defaultMessage, 0);
}

// ========== Legacy API (for backward compatibility) ==========

export const api = {
  get: <T>(endpoint: string, params?: Record<string, unknown>) =>
    apiClient.get<T>(endpoint, { params }).then((r) => r.data),

  post: <T>(endpoint: string, data?: unknown) =>
    apiClient.post<T>(endpoint, data).then((r) => r.data),

  put: <T>(endpoint: string, data?: unknown) =>
    apiClient.put<T>(endpoint, data).then((r) => r.data),

  patch: <T>(endpoint: string, data?: unknown, params?: Record<string, unknown>) =>
    apiClient.patch<T>(endpoint, data, { params }).then((r) => r.data),

  delete: <T>(endpoint: string) => apiClient.delete<T>(endpoint).then((r) => r.data),
};

export default api;
