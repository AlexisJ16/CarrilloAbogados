/**
 * API hooks para gestión de Casos Legales
 * Se conecta con case-service en /case-service/api/cases
 */

import type { CaseStatus, CreateCaseRequest, LegalCase, PracticeArea } from '@/types/case';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from './client';

const CASES_ENDPOINT = '/case-service/api/cases';

// Backend response types
interface BackendCaseDto {
  caseId: number;
  caseNumber: string;
  title: string;
  description?: string;
  clientId: number;
  responsibleLawyer?: string;
  caseType: {
    caseTypeId: number;
    caseTypeName: string;
    practiceArea: PracticeArea;
    description?: string;
  };
  status: CaseStatus;
  priority?: 'HIGH' | 'MEDIUM' | 'LOW';
  startDate: string;
  endDate?: string;
  courtName?: string;
  judgeName?: string;
  estimatedDurationDays?: number;
  totalAmount?: number;
  nextHearingDate?: string;
  notes?: string;
  activities?: BackendActivityDto[];
  caseDocuments?: BackendDocumentDto[];
  createdAt: string;
  updatedAt: string;
}

interface BackendActivityDto {
  activityId: number;
  activityType: string;
  description: string;
  activityDate: string;
  performedBy?: string;
  createdAt: string;
}

interface BackendDocumentDto {
  documentId: number;
  documentName: string;
  documentType: string;
  uploadedAt: string;
}

/**
 * Maps backend DTO to frontend LegalCase interface
 */
function mapBackendToFrontend(dto: BackendCaseDto): LegalCase {
  return {
    id: dto.caseId.toString(),
    caseNumber: dto.caseNumber,
    title: dto.title,
    description: dto.description,
    clientId: dto.clientId.toString(),
    assignedLawyerId: dto.responsibleLawyer || '',
    practiceArea: dto.caseType?.practiceArea || 'CORPORATE_LAW',
    caseType: mapCaseTypeName(dto.caseType?.caseTypeName),
    status: dto.status,
    filingNumber: undefined, // Not in backend DTO
    court: dto.courtName,
    activities: dto.activities?.map((a) => ({
      id: a.activityId.toString(),
      caseId: dto.caseId.toString(),
      activityType: mapActivityType(a.activityType),
      description: a.description,
      activityDate: a.activityDate,
      visibleToClient: true,
      createdBy: a.performedBy || '',
      createdAt: a.createdAt,
    })),
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
  };
}

function mapCaseTypeName(name?: string): 'JUDICIAL' | 'ADMINISTRATIVE' | 'CONSULTANCY' {
  if (!name) return 'CONSULTANCY';
  const upper = name.toUpperCase();
  if (upper.includes('JUDICIAL') || upper.includes('LITIGIO')) return 'JUDICIAL';
  if (upper.includes('ADMIN')) return 'ADMINISTRATIVE';
  return 'CONSULTANCY';
}

function mapActivityType(type: string): import('@/types/case').CaseActivityType {
  const typeMap: Record<string, import('@/types/case').CaseActivityType> = {
    CREATION: 'CREATION',
    STATUS_CHANGE: 'STATUS_CHANGE',
    DOCUMENT_ADDED: 'DOCUMENT_ADDED',
    NOTE: 'NOTE',
    HEARING: 'HEARING',
    FILING: 'FILING',
    DEADLINE: 'DEADLINE',
  };
  return typeMap[type] || 'NOTE';
}

// Query keys
export const caseKeys = {
  all: ['cases'] as const,
  lists: () => [...caseKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...caseKeys.lists(), filters] as const,
  details: () => [...caseKeys.all, 'detail'] as const,
  detail: (id: string) => [...caseKeys.details(), id] as const,
  byClient: (clientId: string) => [...caseKeys.all, 'client', clientId] as const,
  byLawyer: (lawyerId: string) => [...caseKeys.all, 'lawyer', lawyerId] as const,
};

/**
 * Hook para obtener lista de casos
 */
export function useCases(filters?: { status?: CaseStatus; clientId?: string }) {
  return useQuery({
    queryKey: caseKeys.list(filters || {}),
    queryFn: async (): Promise<LegalCase[]> => {
      let url = CASES_ENDPOINT;
      const params: Record<string, string> = {};

      if (filters?.status) {
        params.status = filters.status;
      }
      if (filters?.clientId) {
        url = `${CASES_ENDPOINT}/client/${filters.clientId}`;
      }

      const response = await api.get<BackendCaseDto[]>(url, params);
      return response.map(mapBackendToFrontend);
    },
  });
}

/**
 * Hook para obtener casos paginados
 */
export function useCasesPaginated(
  page: number = 0,
  size: number = 10,
  sortBy: string = 'startDate',
  sortDir: 'asc' | 'desc' = 'desc'
) {
  return useQuery({
    queryKey: [...caseKeys.lists(), { page, size, sortBy, sortDir }] as const,
    queryFn: async () => {
      const response = await api.get<{
        content: BackendCaseDto[];
        totalElements: number;
        totalPages: number;
        number: number;
      }>(`${CASES_ENDPOINT}/page`, { page, size, sortBy, sortDir });

      return {
        cases: response.content.map(mapBackendToFrontend),
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        currentPage: response.number,
      };
    },
  });
}

/**
 * Hook para obtener un caso por ID
 */
export function useCase(id: string) {
  return useQuery({
    queryKey: caseKeys.detail(id),
    queryFn: async (): Promise<LegalCase> => {
      const response = await api.get<BackendCaseDto>(`${CASES_ENDPOINT}/${id}`);
      return mapBackendToFrontend(response);
    },
    enabled: !!id,
  });
}

/**
 * Hook para obtener casos de un cliente
 */
export function useCasesByClient(clientId: string) {
  return useQuery({
    queryKey: caseKeys.byClient(clientId),
    queryFn: async (): Promise<LegalCase[]> => {
      const response = await api.get<BackendCaseDto[]>(`${CASES_ENDPOINT}/client/${clientId}`);
      return response.map(mapBackendToFrontend);
    },
    enabled: !!clientId,
  });
}

/**
 * Hook para crear un nuevo caso
 */
export function useCreateCase() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateCaseRequest): Promise<LegalCase> => {
      // Map frontend request to backend format
      const backendData = {
        caseNumber: `CASE-${Date.now()}`, // Auto-generate case number
        title: data.title,
        description: data.description,
        clientId: parseInt(data.clientId),
        responsibleLawyer: data.assignedLawyerId,
        caseType: {
          practiceArea: data.practiceArea,
          caseTypeName: data.caseType,
        },
        status: 'OPEN',
        priority: 'MEDIUM',
        startDate: new Date().toISOString().split('T')[0],
        courtName: data.court,
      };

      const response = await api.post<BackendCaseDto>(CASES_ENDPOINT, backendData);
      return mapBackendToFrontend(response);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: caseKeys.all });
    },
  });
}

/**
 * Hook para actualizar un caso
 */
export function useUpdateCase() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      id,
      data,
    }: {
      id: string;
      data: Partial<LegalCase>;
    }): Promise<LegalCase> => {
      const response = await api.put<BackendCaseDto>(`${CASES_ENDPOINT}/${id}`, data);
      return mapBackendToFrontend(response);
    },
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: caseKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: caseKeys.lists() });
    },
  });
}

/**
 * Hook para actualizar estado de un caso
 */
export function useUpdateCaseStatus() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, status }: { id: string; status: CaseStatus }): Promise<LegalCase> => {
      const response = await api.patch<BackendCaseDto>(
        `${CASES_ENDPOINT}/${id}/status`,
        undefined,
        { status }
      );
      return mapBackendToFrontend(response);
    },
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: caseKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: caseKeys.lists() });
    },
  });
}

/**
 * Hook para obtener métricas de casos
 */
export function useCaseMetrics() {
  return useQuery({
    queryKey: [...caseKeys.all, 'metrics'] as const,
    queryFn: async () => {
      return api.get<{
        totalCases: number;
        openCases: number;
        closedCases: number;
        avgDuration: number;
      }>(`${CASES_ENDPOINT}/metrics`);
    },
  });
}
