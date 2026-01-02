/**
 * Lead API Types - Carrillo Abogados Legal Tech
 * 
 * Tipos TypeScript generados desde OpenAPI spec
 * Última actualización: Enero 2025
 * 
 * @see http://localhost:8200/client-service/v3/api-docs
 */

// ============================================================
// ENUMS
// ============================================================

/**
 * Categoría del lead basada en scoring
 * Determina la urgencia de atención
 */
export enum LeadCategory {
  /** Score >= 70: Notificar abogado inmediatamente */
  HOT = 'HOT',
  /** Score 40-69: Nurturing automatizado con IA */
  WARM = 'WARM',
  /** Score < 40: Respuesta genérica */
  COLD = 'COLD'
}

/**
 * Estado del lead en el funnel de ventas
 */
export enum LeadStatus {
  /** Recién capturado, pendiente de scoring */
  NUEVO = 'NUEVO',
  /** En secuencia de nurturing (emails automatizados) */
  NURTURING = 'NURTURING',
  /** Marketing Qualified Lead - listo para contacto comercial */
  MQL = 'MQL',
  /** Sales Qualified Lead - en negociación */
  SQL = 'SQL',
  /** Convertido a cliente */
  CONVERTIDO = 'CONVERTIDO',
  /** Perdido/abandonado */
  CHURNED = 'CHURNED'
}

/**
 * Fuente de origen del lead
 */
export enum LeadSource {
  /** Formulario del sitio web */
  WEBSITE = 'WEBSITE',
  /** Referido por cliente existente */
  REFERRAL = 'REFERRAL',
  /** LinkedIn */
  LINKEDIN = 'LINKEDIN',
  /** Evento presencial */
  EVENTO = 'EVENTO',
  /** Google Ads */
  GOOGLE_ADS = 'GOOGLE_ADS',
  /** Llamada telefónica */
  TELEFONO = 'TELEFONO',
  /** Otro origen */
  OTRO = 'OTRO'
}

// ============================================================
// DTOs
// ============================================================

/**
 * DTO completo de Lead
 * Usado en respuestas de la API
 */
export interface LeadDto {
  /** UUID del lead */
  leadId?: string;
  
  /** Nombre completo del lead (requerido) */
  nombre: string;
  
  /** Email del lead (requerido) */
  email: string;
  
  /** Teléfono de contacto */
  telefono?: string;
  
  /** Nombre de la empresa */
  empresa?: string;
  
  /** Cargo en la empresa */
  cargo?: string;
  
  /** Servicio de interés */
  servicio?: string;
  
  /** Mensaje del formulario */
  mensaje?: string;
  
  /** Score calculado (0-100) */
  leadScore?: number;
  
  /** Categoría basada en score */
  leadCategory?: LeadCategory;
  
  /** Estado en el funnel */
  leadStatus?: LeadStatus;
  
  /** Cantidad de emails enviados */
  emailsSent?: number;
  
  /** Cantidad de emails abiertos */
  emailsOpened?: number;
  
  /** Cantidad de clicks en emails */
  emailsClicked?: number;
  
  /** Último contacto realizado */
  lastContact?: string; // ISO 8601 datetime
  
  /** Último engagement registrado */
  lastEngagement?: string; // ISO 8601 datetime
  
  /** Fuente de origen */
  source?: LeadSource;
  
  /** ID del cliente si fue convertido */
  clientId?: number;
  
  /** Fecha de conversión a cliente */
  convertedAt?: string; // ISO 8601 datetime
  
  /** Fecha de creación */
  createdAt?: string; // ISO 8601 datetime
  
  /** Fecha de última actualización */
  updatedAt?: string; // ISO 8601 datetime
}

/**
 * DTO para captura de lead (formulario de contacto)
 * Solo campos que el visitante puede proporcionar
 */
export interface LeadCaptureRequest {
  /** Nombre completo (requerido) */
  nombre: string;
  
  /** Email (requerido) */
  email: string;
  
  /** Teléfono de contacto */
  telefono?: string;
  
  /** Nombre de la empresa */
  empresa?: string;
  
  /** Cargo en la empresa */
  cargo?: string;
  
  /** Servicio de interés */
  servicio?: string;
  
  /** Mensaje del formulario */
  mensaje?: string;
}

/**
 * Respuesta de colección de leads
 */
export interface LeadCollectionResponse {
  collection: LeadDto[];
}

/**
 * Respuesta paginada de leads
 */
export interface PagedLeadResponse {
  content: LeadDto[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * Parámetros de paginación
 */
export interface PageableParams {
  page?: number;
  size?: number;
  sort?: string[];
}

// ============================================================
// REQUEST TYPES
// ============================================================

/**
 * Parámetros para actualizar scoring (callback de n8n)
 */
export interface UpdateScoringParams {
  leadId: string;
  score: number;
  category: LeadCategory;
}

/**
 * Parámetros para actualizar estado
 */
export interface UpdateStatusParams {
  leadId: string;
  status: LeadStatus;
}

/**
 * Parámetros para actualizar engagement
 */
export interface UpdateEngagementParams {
  leadId: string;
  sent?: number;
  opened?: number;
  clicked?: number;
}

/**
 * Parámetros para convertir lead a cliente
 */
export interface ConvertLeadParams {
  leadId: string;
  clientId: number;
}

// ============================================================
// API CLIENT INTERFACE
// ============================================================

/**
 * Interface del cliente API de Leads
 * Implementar según el framework (fetch, axios, etc.)
 */
export interface LeadApiClient {
  // Captura (POST)
  capture(data: LeadCaptureRequest): Promise<LeadDto>;
  
  // Lectura (GET)
  findAll(): Promise<LeadCollectionResponse>;
  findById(leadId: string): Promise<LeadDto>;
  findByEmail(email: string): Promise<LeadDto>;
  findByCategory(category: LeadCategory): Promise<LeadCollectionResponse>;
  findByStatus(status: LeadStatus): Promise<LeadCollectionResponse>;
  findHotPending(): Promise<LeadCollectionResponse>;
  findInactive(days?: number): Promise<LeadCollectionResponse>;
  search(query: string): Promise<LeadCollectionResponse>;
  existsByEmail(email: string): Promise<boolean>;
  
  // Paginación
  findAllPaged(params: PageableParams): Promise<PagedLeadResponse>;
  findByCategoryPaged(category: LeadCategory, params: PageableParams): Promise<PagedLeadResponse>;
  findByStatusPaged(status: LeadStatus, params: PageableParams): Promise<PagedLeadResponse>;
  
  // Actualización (PUT/PATCH)
  update(leadId: string, data: LeadDto): Promise<LeadDto>;
  updateScoring(params: UpdateScoringParams): Promise<LeadDto>;
  updateStatus(params: UpdateStatusParams): Promise<LeadDto>;
  updateEngagement(params: UpdateEngagementParams): Promise<LeadDto>;
  
  // Conversión (POST)
  convertToClient(params: ConvertLeadParams): Promise<LeadDto>;
  
  // Eliminación (DELETE)
  delete(leadId: string): Promise<void>;
}

// ============================================================
// SERVICIOS DE INTERÉS (para dropdown)
// ============================================================

/**
 * Opciones de servicio para el formulario de contacto
 */
export const SERVICIOS_DISPONIBLES = [
  { value: 'marca', label: 'Registro de Marcas', scoring: '+20' },
  { value: 'administrativo', label: 'Derecho Administrativo', scoring: 'base' },
  { value: 'corporativo', label: 'Derecho Corporativo', scoring: 'base' },
  { value: 'competencia', label: 'Derecho de Competencia', scoring: 'base' },
  { value: 'telecomunicaciones', label: 'Derecho de Telecomunicaciones', scoring: 'base' },
  { value: 'litigio', label: 'Litigio', scoring: '+20' },
  { value: 'general', label: 'Consulta General', scoring: 'base' },
] as const;

/**
 * Cargos C-Level que dan +20 puntos en scoring
 */
export const CARGOS_CLEVEL = [
  'CEO', 'CTO', 'CFO', 'COO', 'CMO', 'CIO',
  'Gerente General', 'Director General', 'Presidente',
  'Vicepresidente', 'Director Ejecutivo', 'Socio',
  'Partner', 'Fundador', 'Co-fundador', 'Owner'
] as const;

// ============================================================
// UTILIDADES
// ============================================================

/**
 * Verifica si un email es corporativo (no Gmail, Hotmail, etc.)
 * Emails corporativos dan +10 puntos en scoring
 */
export function isEmailCorporativo(email: string): boolean {
  const dominiosPersonales = [
    'gmail.com', 'hotmail.com', 'outlook.com', 'yahoo.com',
    'live.com', 'icloud.com', 'protonmail.com', 'mail.com'
  ];
  const dominio = email.split('@')[1]?.toLowerCase();
  return dominio ? !dominiosPersonales.includes(dominio) : false;
}

/**
 * Verifica si un cargo es C-Level
 */
export function isCargoClevel(cargo: string): boolean {
  const cargoLower = cargo.toLowerCase();
  return CARGOS_CLEVEL.some(c => cargoLower.includes(c.toLowerCase()));
}

/**
 * Estima el score de un lead antes de enviar
 * Nota: El score real es calculado por n8n
 */
export function estimateLeadScore(data: LeadCaptureRequest): number {
  let score = 30; // Base
  
  if (data.servicio?.toLowerCase().includes('marca') || 
      data.servicio?.toLowerCase().includes('litigio')) {
    score += 20;
  }
  
  if (data.mensaje && data.mensaje.length > 50) {
    score += 10;
  }
  
  if (data.telefono) {
    score += 10;
  }
  
  if (data.empresa) {
    score += 10;
  }
  
  if (data.email && isEmailCorporativo(data.email)) {
    score += 10;
  }
  
  if (data.cargo && isCargoClevel(data.cargo)) {
    score += 20;
  }
  
  return Math.min(score, 100);
}

/**
 * Determina la categoría esperada según el score
 */
export function getCategoryFromScore(score: number): LeadCategory {
  if (score >= 70) return LeadCategory.HOT;
  if (score >= 40) return LeadCategory.WARM;
  return LeadCategory.COLD;
}
