/**
 * Tipos para Lead - basados en el backend client-service
 */

export type LeadCategory = 'HOT' | 'WARM' | 'COLD';
export type LeadStatus = 'NUEVO' | 'NURTURING' | 'MQL' | 'SQL' | 'CONVERTIDO' | 'CHURNED';
export type LeadSource = 'WEBSITE' | 'REFERRAL' | 'LINKEDIN' | 'EVENTO';

export interface Lead {
  id: string;
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio: string;
  mensaje: string;
  
  // Scoring (calculado por n8n)
  leadScore?: number;
  category?: LeadCategory;
  status: LeadStatus;
  source: LeadSource;
  
  // Tracking
  emailsSent?: number;
  emailsOpened?: number;
  emailsClicked?: number;
  lastContact?: string;
  lastEngagement?: string;
  
  // Conversión
  clientId?: string;
  convertedAt?: string;
  
  // Metadata
  createdAt: string;
  updatedAt: string;
}

export interface CreateLeadRequest {
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio: string;
  mensaje: string;
}

export interface LeadScoreUpdateRequest {
  leadScore: number;
  category: LeadCategory;
}
