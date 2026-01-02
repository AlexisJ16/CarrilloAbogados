// Notifications API Hooks for Carrillo Abogados
import type {
  CreateNotificationRequest,
  MarkAllAsReadResponse,
  MarkAsReadResponse,
  Notification,
  UnreadCountResponse,
} from '@/types/notification';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from './client';

// Backend response types
interface BackendNotificationDto {
  notificationId: string;
  recipientId: string;
  recipientEmail?: string;
  notificationType: string;
  channel: string;
  title: string;
  message?: string;
  relatedEntityType?: string;
  relatedEntityId?: string;
  actionUrl?: string;
  read: boolean;
  readAt?: string;
  status: string;
  sentAt?: string;
  createdAt: string;
}

interface DtoCollectionResponse<T> {
  collection: T[];
  totalElements: number;
  page: number;
  size: number;
}

// Mapper function
function mapBackendToFrontend(dto: BackendNotificationDto): Notification {
  return {
    id: dto.notificationId,
    recipientId: dto.recipientId,
    recipientEmail: dto.recipientEmail,
    notificationType: dto.notificationType as Notification['notificationType'],
    channel: dto.channel as Notification['channel'],
    title: dto.title,
    message: dto.message,
    relatedEntityType: dto.relatedEntityType,
    relatedEntityId: dto.relatedEntityId,
    actionUrl: dto.actionUrl,
    read: dto.read,
    readAt: dto.readAt,
    status: dto.status as Notification['status'],
    sentAt: dto.sentAt,
    createdAt: dto.createdAt,
  };
}

const NOTIFICATIONS_BASE = '/notification-service/api/notifications';

// Query Keys
export const notificationKeys = {
  all: ['notifications'] as const,
  user: (userId: string) => [...notificationKeys.all, 'user', userId] as const,
  unread: (userId: string) => [...notificationKeys.user(userId), 'unread'] as const,
  count: (userId: string) => [...notificationKeys.user(userId), 'count'] as const,
  recent: (userId: string) => [...notificationKeys.user(userId), 'recent'] as const,
  detail: (id: string) => [...notificationKeys.all, id] as const,
};

// Get notifications for user
export function useNotifications(userId: string | undefined, page = 0, size = 20) {
  return useQuery({
    queryKey: [...notificationKeys.user(userId || ''), page, size],
    queryFn: async () => {
      const response = await api.get<DtoCollectionResponse<BackendNotificationDto>>(
        `${NOTIFICATIONS_BASE}/user/${userId}?page=${page}&size=${size}`
      );
      return {
        notifications: response.collection.map(mapBackendToFrontend),
        totalElements: response.totalElements,
        page: response.page,
        size: response.size,
      };
    },
    enabled: !!userId,
  });
}

// Get unread notifications
export function useUnreadNotifications(userId: string | undefined) {
  return useQuery({
    queryKey: notificationKeys.unread(userId || ''),
    queryFn: async () => {
      const response = await api.get<DtoCollectionResponse<BackendNotificationDto>>(
        `${NOTIFICATIONS_BASE}/user/${userId}/unread`
      );
      return response.collection.map(mapBackendToFrontend);
    },
    enabled: !!userId,
  });
}

// Get unread count
export function useUnreadCount(userId: string | undefined) {
  return useQuery({
    queryKey: notificationKeys.count(userId || ''),
    queryFn: async () => {
      const response = await api.get<UnreadCountResponse>(
        `${NOTIFICATIONS_BASE}/user/${userId}/count`
      );
      return response.unreadCount;
    },
    enabled: !!userId,
    refetchInterval: 30000, // Refetch every 30 seconds
  });
}

// Get recent notifications (for dropdown)
export function useRecentNotifications(userId: string | undefined, limit = 5) {
  return useQuery({
    queryKey: [...notificationKeys.recent(userId || ''), limit],
    queryFn: async () => {
      const response = await api.get<DtoCollectionResponse<BackendNotificationDto>>(
        `${NOTIFICATIONS_BASE}/user/${userId}/recent?limit=${limit}`
      );
      return response.collection.map(mapBackendToFrontend);
    },
    enabled: !!userId,
    refetchInterval: 30000, // Refetch every 30 seconds
  });
}

// Get single notification
export function useNotification(id: string | undefined) {
  return useQuery({
    queryKey: notificationKeys.detail(id || ''),
    queryFn: async () => {
      const response = await api.get<BackendNotificationDto>(`${NOTIFICATIONS_BASE}/${id}`);
      return mapBackendToFrontend(response);
    },
    enabled: !!id,
  });
}

// Mark notification as read
export function useMarkAsRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (notificationId: string) => {
      const response = await api.patch<MarkAsReadResponse>(
        `${NOTIFICATIONS_BASE}/${notificationId}/read`
      );
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.all });
    },
  });
}

// Mark all notifications as read
export function useMarkAllAsRead() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (userId: string) => {
      const response = await api.patch<MarkAllAsReadResponse>(
        `${NOTIFICATIONS_BASE}/user/${userId}/read-all`
      );
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.all });
    },
  });
}

// Create notification (for system use)
export function useCreateNotification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateNotificationRequest) => {
      const response = await api.post<BackendNotificationDto>(NOTIFICATIONS_BASE, data);
      return mapBackendToFrontend(response);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.all });
    },
  });
}

// Delete notification
export function useDeleteNotification() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (notificationId: string) => {
      await api.delete(`${NOTIFICATIONS_BASE}/${notificationId}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.all });
    },
  });
}
