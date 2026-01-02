/**
 * API hooks para gestión de Leads
 * Se conecta con client-service en /client-service/api/leads
 */

import type { CreateLeadRequest, Lead } from '@/types/lead';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from './client';

const LEADS_ENDPOINT = '/client-service/api/leads';

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
    mutationFn: (data: CreateLeadRequest) => 
      api.post<Lead>(LEADS_ENDPOINT, data),
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
    queryFn: () => api.get<Lead[]>(LEADS_ENDPOINT, filters),
  });
}

/**
 * Hook para obtener un lead por ID
 */
export function useLead(id: string) {
  return useQuery({
    queryKey: leadKeys.detail(id),
    queryFn: () => api.get<Lead>(`${LEADS_ENDPOINT}/${id}`),
    enabled: !!id,
  });
}

/**
 * Hook para actualizar un lead
 */
export function useUpdateLead() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Lead> }) =>
      api.patch<Lead>(`${LEADS_ENDPOINT}/${id}`, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: leadKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: leadKeys.lists() });
    },
  });
}
