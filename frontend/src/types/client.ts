/**
 * Tipos para Client - basados en el backend client-service
 */

export type ClientType = 'NATURAL' | 'JURIDICA';
export type DocumentType = 'CC' | 'NIT' | 'CE' | 'PASAPORTE';
export type ClientStatus = 'ACTIVE' | 'INACTIVE';

export interface Client {
  id: string;
  clientType: ClientType;
  documentType: DocumentType;
  documentNumber: string;
  
  // Persona Natural
  firstName?: string;
  lastName?: string;
  
  // Persona Jurídica
  companyName?: string;
  legalRepresentative?: string;
  
  // Contacto
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  
  // Estado
  status: ClientStatus;
  
  // Metadata
  createdAt: string;
  updatedAt: string;
}

export interface CreateClientRequest {
  clientType: ClientType;
  documentType: DocumentType;
  documentNumber: string;
  firstName?: string;
  lastName?: string;
  companyName?: string;
  legalRepresentative?: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
}
