/**
 * API Contracts - Carrillo Abogados Frontend
 * 
 * Barrel export para todos los tipos y clientes de API
 */

// Types
export * from './types/lead.types';

// API Clients
export { ApiError, createLeadApiClient, leadApi } from './clients/lead-api-client';
export type { ApiConfig } from './clients/lead-api-client';

