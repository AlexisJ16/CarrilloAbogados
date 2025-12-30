/**
 * Tipos para LegalCase - basados en el backend case-service
 */

export type PracticeArea = 
  | 'ADMINISTRATIVE_LAW'
  | 'COMPETITION_LAW'
  | 'CORPORATE_LAW'
  | 'TELECOMMUNICATIONS_LAW'
  | 'TRADEMARK_LAW';

export type CaseType = 'JUDICIAL' | 'ADMINISTRATIVE' | 'CONSULTANCY';
export type CaseStatus = 'OPEN' | 'IN_PROGRESS' | 'SUSPENDED' | 'CLOSED';
export type CaseActivityType = 
  | 'CREATION'
  | 'STATUS_CHANGE'
  | 'DOCUMENT_ADDED'
  | 'NOTE'
  | 'HEARING'
  | 'FILING'
  | 'DEADLINE';

export interface OpposingParty {
  name: string;
  documentNumber?: string;
  representative?: string;
  contactInfo?: string;
}

export interface CaseActivity {
  id: string;
  caseId: string;
  activityType: CaseActivityType;
  description: string;
  activityDate: string;
  visibleToClient: boolean;
  attachments?: string[];
  createdBy: string;
  createdAt: string;
}

export interface LegalCase {
  id: string;
  caseNumber: string;
  title: string;
  description?: string;
  
  // Relaciones
  clientId: string;
  assignedLawyerId: string;
  collaboratorIds?: string[];
  
  // Clasificación
  practiceArea: PracticeArea;
  caseType: CaseType;
  status: CaseStatus;
  
  // Datos procesales
  filingNumber?: string;
  court?: string;
  opposingParty?: OpposingParty;
  
  // Actividades
  activities?: CaseActivity[];
  
  // Metadata
  createdAt: string;
  updatedAt: string;
}

export interface CreateCaseRequest {
  title: string;
  description?: string;
  clientId: string;
  assignedLawyerId: string;
  practiceArea: PracticeArea;
  caseType: CaseType;
  filingNumber?: string;
  court?: string;
  opposingParty?: OpposingParty;
}

// Labels para UI
export const practiceAreaLabels: Record<PracticeArea, string> = {
  ADMINISTRATIVE_LAW: 'Derecho Administrativo',
  COMPETITION_LAW: 'Derecho de Competencias',
  CORPORATE_LAW: 'Derecho Corporativo',
  TELECOMMUNICATIONS_LAW: 'Derecho de Telecomunicaciones',
  TRADEMARK_LAW: 'Derecho de Marcas',
};

export const caseStatusLabels: Record<CaseStatus, string> = {
  OPEN: 'Abierto',
  IN_PROGRESS: 'En Progreso',
  SUSPENDED: 'Suspendido',
  CLOSED: 'Cerrado',
};

export const caseTypeLabels: Record<CaseType, string> = {
  JUDICIAL: 'Judicial',
  ADMINISTRATIVE: 'Administrativo',
  CONSULTANCY: 'Consultoría',
};
