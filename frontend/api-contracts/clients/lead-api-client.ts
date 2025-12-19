/**
 * Lead API Client - Carrillo Abogados
 * 
 * Cliente HTTP para interactuar con el backend de client-service.
 * Diseñado para uso con React/Lovable.
 * 
 * @example
 * ```typescript
 * import { leadApi } from './api-contracts/clients/lead-api-client';
 * 
 * // Capturar lead desde formulario
 * const lead = await leadApi.capture({
 *   nombre: 'Juan Pérez',
 *   email: 'juan@empresa.com',
 *   servicio: 'Registro de Marcas'
 * });
 * 
 * console.log('Lead creado:', lead.leadId);
 * ```
 */

import {
    ConvertLeadParams,
    LeadApiClient,
    LeadCaptureRequest,
    LeadCategory,
    LeadCollectionResponse,
    LeadDto,
    LeadStatus,
    PageableParams,
    PagedLeadResponse,
    UpdateEngagementParams,
    UpdateScoringParams,
    UpdateStatusParams,
} from '../types/lead.types';

// ============================================================
// CONFIGURACIÓN
// ============================================================

const DEFAULT_CONFIG = {
  baseUrl: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  servicePath: '/client-service/api/leads',
  timeout: 10000,
};

interface ApiConfig {
  baseUrl: string;
  servicePath: string;
  timeout: number;
  authToken?: string;
}

// ============================================================
// ERRORES PERSONALIZADOS
// ============================================================

export class ApiError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    public body?: any
  ) {
    super(body?.message || `HTTP ${status}: ${statusText}`);
    this.name = 'ApiError';
  }

  isValidationError(): boolean {
    return this.status === 400;
  }

  isUnauthorized(): boolean {
    return this.status === 401;
  }

  isForbidden(): boolean {
    return this.status === 403;
  }

  isNotFound(): boolean {
    return this.status === 404;
  }

  isConflict(): boolean {
    return this.status === 409;
  }

  isServerError(): boolean {
    return this.status >= 500;
  }
}

// ============================================================
// UTILIDADES HTTP
// ============================================================

async function request<T>(
  url: string,
  options: RequestInit = {},
  config: ApiConfig = DEFAULT_CONFIG
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (config.authToken) {
    headers['Authorization'] = `Bearer ${config.authToken}`;
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), config.timeout);

  try {
    const response = await fetch(url, {
      ...options,
      headers,
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      const errorBody = await response.json().catch(() => undefined);
      throw new ApiError(response.status, response.statusText, errorBody);
    }

    // Handle 204 No Content
    if (response.status === 204) {
      return undefined as unknown as T;
    }

    return response.json();
  } catch (error) {
    clearTimeout(timeoutId);

    if (error instanceof ApiError) {
      throw error;
    }

    if (error instanceof Error && error.name === 'AbortError') {
      throw new ApiError(408, 'Request Timeout', { message: 'La solicitud excedió el tiempo límite' });
    }

    throw new ApiError(0, 'Network Error', { message: 'Error de conexión con el servidor' });
  }
}

function buildUrl(path: string, params?: Record<string, any>): string {
  const url = new URL(`${DEFAULT_CONFIG.baseUrl}${DEFAULT_CONFIG.servicePath}${path}`);
  
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        if (Array.isArray(value)) {
          value.forEach((v) => url.searchParams.append(key, String(v)));
        } else {
          url.searchParams.set(key, String(value));
        }
      }
    });
  }
  
  return url.toString();
}

// ============================================================
// API CLIENT
// ============================================================

class LeadApiClientImpl implements LeadApiClient {
  private config: ApiConfig;

  constructor(config: Partial<ApiConfig> = {}) {
    this.config = { ...DEFAULT_CONFIG, ...config };
  }

  /**
   * Configura el token de autenticación para requests futuros
   */
  setAuthToken(token: string): void {
    this.config.authToken = token;
  }

  /**
   * Limpia el token de autenticación
   */
  clearAuthToken(): void {
    this.config.authToken = undefined;
  }

  // ============================================================
  // CAPTURA (Formulario público)
  // ============================================================

  /**
   * Captura un nuevo lead desde el formulario de contacto
   * Este es el endpoint principal para el sitio web público
   */
  async capture(data: LeadCaptureRequest): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(''),
      {
        method: 'POST',
        body: JSON.stringify(data),
      },
      this.config
    );
  }

  // ============================================================
  // LECTURA
  // ============================================================

  /**
   * Lista todos los leads
   */
  async findAll(): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(buildUrl(''), { method: 'GET' }, this.config);
  }

  /**
   * Obtiene un lead por ID
   */
  async findById(leadId: string): Promise<LeadDto> {
    return request<LeadDto>(buildUrl(`/${leadId}`), { method: 'GET' }, this.config);
  }

  /**
   * Busca un lead por email
   */
  async findByEmail(email: string): Promise<LeadDto> {
    return request<LeadDto>(buildUrl(`/email/${encodeURIComponent(email)}`), { method: 'GET' }, this.config);
  }

  /**
   * Lista leads por categoría (HOT, WARM, COLD)
   */
  async findByCategory(category: LeadCategory): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(buildUrl(`/category/${category}`), { method: 'GET' }, this.config);
  }

  /**
   * Lista leads por estado
   */
  async findByStatus(status: LeadStatus): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(buildUrl(`/status/${status}`), { method: 'GET' }, this.config);
  }

  /**
   * Lista leads HOT pendientes de atención
   * Útil para el panel de notificaciones del abogado
   */
  async findHotPending(): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(buildUrl('/hot'), { method: 'GET' }, this.config);
  }

  /**
   * Lista leads inactivos (sin engagement reciente)
   * @param days Días de inactividad (default: 30)
   */
  async findInactive(days: number = 30): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(
      buildUrl('/inactive', { days }),
      { method: 'GET' },
      this.config
    );
  }

  /**
   * Búsqueda de leads por texto
   */
  async search(query: string): Promise<LeadCollectionResponse> {
    return request<LeadCollectionResponse>(
      buildUrl('/search', { query }),
      { method: 'GET' },
      this.config
    );
  }

  /**
   * Verifica si un email ya está registrado
   */
  async existsByEmail(email: string): Promise<boolean> {
    return request<boolean>(
      buildUrl('/exists', { email }),
      { method: 'GET' },
      this.config
    );
  }

  // ============================================================
  // PAGINACIÓN
  // ============================================================

  /**
   * Lista todos los leads con paginación
   */
  async findAllPaged(params: PageableParams): Promise<PagedLeadResponse> {
    return request<PagedLeadResponse>(
      buildUrl('/paged', params),
      { method: 'GET' },
      this.config
    );
  }

  /**
   * Lista leads por categoría con paginación
   */
  async findByCategoryPaged(
    category: LeadCategory,
    params: PageableParams
  ): Promise<PagedLeadResponse> {
    return request<PagedLeadResponse>(
      buildUrl(`/category/${category}/paged`, params),
      { method: 'GET' },
      this.config
    );
  }

  /**
   * Lista leads por estado con paginación
   */
  async findByStatusPaged(
    status: LeadStatus,
    params: PageableParams
  ): Promise<PagedLeadResponse> {
    return request<PagedLeadResponse>(
      buildUrl(`/status/${status}/paged`, params),
      { method: 'GET' },
      this.config
    );
  }

  // ============================================================
  // ACTUALIZACIÓN
  // ============================================================

  /**
   * Actualiza un lead completo
   */
  async update(leadId: string, data: LeadDto): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(`/${leadId}`),
      {
        method: 'PUT',
        body: JSON.stringify(data),
      },
      this.config
    );
  }

  /**
   * Actualiza el scoring de un lead
   * Este endpoint es llamado por n8n después de calcular el score
   */
  async updateScoring(params: UpdateScoringParams): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(`/${params.leadId}/scoring`, {
        score: params.score,
        category: params.category,
      }),
      { method: 'PATCH' },
      this.config
    );
  }

  /**
   * Actualiza el estado de un lead en el funnel
   */
  async updateStatus(params: UpdateStatusParams): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(`/${params.leadId}/status`, { status: params.status }),
      { method: 'PATCH' },
      this.config
    );
  }

  /**
   * Actualiza métricas de engagement
   * Este endpoint es llamado por n8n cuando se detectan opens/clicks
   */
  async updateEngagement(params: UpdateEngagementParams): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(`/${params.leadId}/engagement`, {
        sent: params.sent,
        opened: params.opened,
        clicked: params.clicked,
      }),
      { method: 'PATCH' },
      this.config
    );
  }

  // ============================================================
  // CONVERSIÓN
  // ============================================================

  /**
   * Convierte un lead en cliente
   * @param params.leadId ID del lead a convertir
   * @param params.clientId ID del cliente creado en client-service
   */
  async convertToClient(params: ConvertLeadParams): Promise<LeadDto> {
    return request<LeadDto>(
      buildUrl(`/${params.leadId}/convert`, { clientId: params.clientId }),
      { method: 'POST' },
      this.config
    );
  }

  // ============================================================
  // ELIMINACIÓN
  // ============================================================

  /**
   * Elimina un lead
   */
  async delete(leadId: string): Promise<void> {
    return request<void>(
      buildUrl(`/${leadId}`),
      { method: 'DELETE' },
      this.config
    );
  }
}

// ============================================================
// EXPORTACIONES
// ============================================================

/**
 * Instancia singleton del cliente API de leads
 * Usar esta para la mayoría de los casos
 */
export const leadApi = new LeadApiClientImpl();

/**
 * Factory para crear instancias personalizadas
 * Útil para testing o configuraciones diferentes
 */
export function createLeadApiClient(config?: Partial<ApiConfig>): LeadApiClient {
  return new LeadApiClientImpl(config);
}

export type { ApiConfig };
