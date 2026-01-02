// Notification Types for Carrillo Abogados Frontend

export type NotificationType =
  | 'LEAD_CAPTURED'
  | 'LEAD_HOT'
  | 'LEAD_ASSIGNED'
  | 'CASE_CREATED'
  | 'CASE_UPDATED'
  | 'CASE_STATUS_CHANGED'
  | 'CASE_ASSIGNED'
  | 'APPOINTMENT_SCHEDULED'
  | 'APPOINTMENT_REMINDER'
  | 'DEADLINE_REMINDER'
  | 'DOCUMENT_UPLOADED'
  | 'DOCUMENT_SHARED'
  | 'PAYMENT_RECEIVED'
  | 'PAYMENT_DUE'
  | 'SYSTEM_ALERT'
  | 'WELCOME'
  | 'PASSWORD_RESET';

export type NotificationChannel = 'IN_APP' | 'EMAIL' | 'SMS' | 'PUSH';

export type NotificationStatus = 'PENDING' | 'SENT' | 'DELIVERED' | 'FAILED' | 'CANCELLED';

export interface Notification {
  id: string;
  recipientId: string;
  recipientEmail?: string;
  notificationType: NotificationType;
  channel: NotificationChannel;
  title: string;
  message?: string;
  relatedEntityType?: string;
  relatedEntityId?: string;
  actionUrl?: string;
  read: boolean;
  readAt?: string;
  status: NotificationStatus;
  sentAt?: string;
  createdAt: string;
}

export interface CreateNotificationRequest {
  recipientId: string;
  recipientEmail?: string;
  notificationType: NotificationType;
  channel: NotificationChannel;
  title: string;
  message?: string;
  relatedEntityType?: string;
  relatedEntityId?: string;
  actionUrl?: string;
}

export interface UnreadCountResponse {
  unreadCount: number;
}

export interface MarkAsReadResponse {
  success: boolean;
}

export interface MarkAllAsReadResponse {
  success: boolean;
  count: number;
}

// Helper function to get notification icon based on type
export function getNotificationIcon(type: NotificationType): string {
  const icons: Record<NotificationType, string> = {
    LEAD_CAPTURED: '📥',
    LEAD_HOT: '🔥',
    LEAD_ASSIGNED: '👤',
    CASE_CREATED: '📁',
    CASE_UPDATED: '📝',
    CASE_STATUS_CHANGED: '🔄',
    CASE_ASSIGNED: '⚖️',
    APPOINTMENT_SCHEDULED: '📅',
    APPOINTMENT_REMINDER: '⏰',
    DEADLINE_REMINDER: '⚠️',
    DOCUMENT_UPLOADED: '📄',
    DOCUMENT_SHARED: '📤',
    PAYMENT_RECEIVED: '💰',
    PAYMENT_DUE: '💳',
    SYSTEM_ALERT: '🔔',
    WELCOME: '👋',
    PASSWORD_RESET: '🔐',
  };
  return icons[type] || '📌';
}

// Helper function to get notification color based on type
export function getNotificationColor(type: NotificationType): {
  bg: string;
  text: string;
} {
  const colors: Record<NotificationType, { bg: string; text: string }> = {
    LEAD_CAPTURED: { bg: 'bg-blue-100', text: 'text-blue-800' },
    LEAD_HOT: { bg: 'bg-red-100', text: 'text-red-800' },
    LEAD_ASSIGNED: { bg: 'bg-purple-100', text: 'text-purple-800' },
    CASE_CREATED: { bg: 'bg-green-100', text: 'text-green-800' },
    CASE_UPDATED: { bg: 'bg-yellow-100', text: 'text-yellow-800' },
    CASE_STATUS_CHANGED: { bg: 'bg-orange-100', text: 'text-orange-800' },
    CASE_ASSIGNED: { bg: 'bg-indigo-100', text: 'text-indigo-800' },
    APPOINTMENT_SCHEDULED: { bg: 'bg-teal-100', text: 'text-teal-800' },
    APPOINTMENT_REMINDER: { bg: 'bg-amber-100', text: 'text-amber-800' },
    DEADLINE_REMINDER: { bg: 'bg-red-100', text: 'text-red-800' },
    DOCUMENT_UPLOADED: { bg: 'bg-cyan-100', text: 'text-cyan-800' },
    DOCUMENT_SHARED: { bg: 'bg-sky-100', text: 'text-sky-800' },
    PAYMENT_RECEIVED: { bg: 'bg-emerald-100', text: 'text-emerald-800' },
    PAYMENT_DUE: { bg: 'bg-rose-100', text: 'text-rose-800' },
    SYSTEM_ALERT: { bg: 'bg-gray-100', text: 'text-gray-800' },
    WELCOME: { bg: 'bg-violet-100', text: 'text-violet-800' },
    PASSWORD_RESET: { bg: 'bg-slate-100', text: 'text-slate-800' },
  };
  return colors[type] || { bg: 'bg-gray-100', text: 'text-gray-800' };
}
