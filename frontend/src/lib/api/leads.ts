/**
 * API hooks para gestión de Leads
 * Se conecta con client-service en /client-service/api/leads
 */

import type { CreateLeadRequest, Lead, LeadCategory, LeadStatus } from '@/types/lead';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from './client';

const LEADS_ENDPOINT = '/client-service/api/leads';

// Backend response wrapper type
interface DtoCollectionResponse<T> {
  collection: T[];
}

// Backend Lead DTO (uses leadId instead of id)
interface BackendLeadDto {
  leadId: string;
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio: string;
  mensaje: string;
  leadScore?: number;
  leadCategory?: LeadCategory;
  leadStatus?: LeadStatus;
  emailsSent?: number;
  emailsOpened?: number;
  emailsClicked?: number;
  lastContact?: string;
  lastEngagement?: string;
  source?: string;
  clientId?: number;
  convertedAt?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Maps backend DTO to frontend Lead interface
 */
function mapBackendToFrontend(dto: BackendLeadDto): Lead {
  return {
    id: dto.leadId,
    nombre: dto.nombre,
    email: dto.email,
    telefono: dto.telefono,
    empresa: dto.empresa,
    cargo: dto.cargo,
    servicio: dto.servicio,
    mensaje: dto.mensaje,
    leadScore: dto.leadScore,
    category: dto.leadCategory,
    status: dto.leadStatus || 'NUEVO',
    source: (dto.source as Lead['source']) || 'WEBSITE',
    emailsSent: dto.emailsSent,
    emailsOpened: dto.emailsOpened,
    emailsClicked: dto.emailsClicked,
    lastContact: dto.lastContact,
    lastEngagement: dto.lastEngagement,
    clientId: dto.clientId?.toString(),
    convertedAt: dto.convertedAt,
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
  };
}

// Query keys
export const leadKeys = {
  all: ['leads'] as const,
  lists: () => [...leadKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...leadKeys.lists(), filters] as const,
  details: () => [...leadKeys.all, 'detail'] as const,
  detail: (id: string) => [...leadKeys.details(), id] as const,
};

/**
 * Hook para crear un nuevo lead (formulario de contacto)
 */
export function useCreateLead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateLeadRequest): Promise<Lead> => {
      const response = await api.post<BackendLeadDto>(LEADS_ENDPOINT, data);
      return mapBackendToFrontend(response);
    },
    onSuccess: () => {
      // Invalidar cache de leads
      queryClient.invalidateQueries({ queryKey: leadKeys.all });
    },
  });
}

/**
 * Hook para obtener lista de leads (uso interno/admin)
 */
export function useLeads(filters?: { status?: string; category?: string }) {
  return useQuery({
    queryKey: leadKeys.list(filters || {}),
    queryFn: async (): Promise<Lead[]> => {
      // Build URL with filters
      let url = LEADS_ENDPOINT;
      if (filters?.status) {
        url = `${LEADS_ENDPOINT}/status/${filters.status}`;
      } else if (filters?.category) {
        url = `${LEADS_ENDPOINT}/category/${filters.category}`;
      }

      const response = await api.get<DtoCollectionResponse<BackendLeadDto>>(url);
      return response.collection.map(mapBackendToFrontend);
    },
  });
}

/**
 * Hook para obtener un lead por ID
 */
export function useLead(id: string) {
  return useQuery({
    queryKey: leadKeys.detail(id),
    queryFn: async (): Promise<Lead> => {
      const response = await api.get<BackendLeadDto>(`${LEADS_ENDPOINT}/${id}`);
      return mapBackendToFrontend(response);
    },
    enabled: !!id,
  });
}

/**
 * Hook para actualizar estado de un lead
 */
export function useUpdateLead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: Partial<Lead> }): Promise<Lead> => {
      // Use the status endpoint for status updates
      if (data.status) {
        const response = await api.patch<BackendLeadDto>(
          `${LEADS_ENDPOINT}/${id}/status`,
          undefined,
          { status: data.status }
        );
        return mapBackendToFrontend(response);
      }
      // General update
      const response = await api.put<BackendLeadDto>(`${LEADS_ENDPOINT}/${id}`, data);
      return mapBackendToFrontend(response);
    },
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: leadKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: leadKeys.lists() });
    },
  });
}

/**
 * Hook para obtener leads HOT pendientes
 */
export function useHotLeads() {
  return useQuery({
    queryKey: [...leadKeys.all, 'hot'] as const,
    queryFn: async (): Promise<Lead[]> => {
      const response = await api.get<DtoCollectionResponse<BackendLeadDto>>(
        `${LEADS_ENDPOINT}/hot`
      );
      return response.collection.map(mapBackendToFrontend);
    },
  });
}
