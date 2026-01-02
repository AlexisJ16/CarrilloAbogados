'use client';

import {
  useDeleteNotification,
  useMarkAllAsRead,
  useMarkAsRead,
  useNotifications,
} from '@/lib/api/notifications';
import { AuthGuard, useAuth } from '@/lib/auth';
import type { Notification, NotificationType } from '@/types/notification';
import { getNotificationColor, getNotificationIcon } from '@/types/notification';
import Link from 'next/link';
import { useState } from 'react';

function formatDateTime(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return 'Ahora mismo';
  if (diffInSeconds < 3600) return `Hace ${Math.floor(diffInSeconds / 60)} minutos`;
  if (diffInSeconds < 86400) return `Hace ${Math.floor(diffInSeconds / 3600)} horas`;
  if (diffInSeconds < 604800) return `Hace ${Math.floor(diffInSeconds / 86400)} días`;

  return date.toLocaleDateString('es-CO', {
    day: 'numeric',
    month: 'long',
    year: date.getFullYear() !== now.getFullYear() ? 'numeric' : undefined,
    hour: '2-digit',
    minute: '2-digit',
  });
}

function getTypeLabel(type: NotificationType): string {
  const labels: Record<NotificationType, string> = {
    LEAD_CAPTURED: 'Lead Capturado',
    LEAD_HOT: 'Lead Caliente',
    LEAD_ASSIGNED: 'Lead Asignado',
    CASE_CREATED: 'Caso Creado',
    CASE_UPDATED: 'Caso Actualizado',
    CASE_STATUS_CHANGED: 'Cambio de Estado',
    CASE_ASSIGNED: 'Caso Asignado',
    DOCUMENT_UPLOADED: 'Documento Subido',
    DOCUMENT_SHARED: 'Documento Compartido',
    APPOINTMENT_SCHEDULED: 'Cita Agendada',
    APPOINTMENT_REMINDER: 'Recordatorio de Cita',
    DEADLINE_REMINDER: 'Recordatorio de Término',
    PAYMENT_DUE: 'Pago Pendiente',
    PAYMENT_RECEIVED: 'Pago Recibido',
    SYSTEM_ALERT: 'Alerta del Sistema',
    WELCOME: 'Bienvenida',
    PASSWORD_RESET: 'Cambio de Contraseña',
  };
  return labels[type] || type;
}

type FilterType = 'all' | 'unread' | 'leads' | 'cases' | 'appointments' | 'system';

const filterOptions: { value: FilterType; label: string }[] = [
  { value: 'all', label: 'Todas' },
  { value: 'unread', label: 'No leídas' },
  { value: 'leads', label: 'Leads' },
  { value: 'cases', label: 'Casos' },
  { value: 'appointments', label: 'Citas' },
  { value: 'system', label: 'Sistema' },
];

function filterNotifications(notifications: Notification[], filter: FilterType): Notification[] {
  switch (filter) {
    case 'unread':
      return notifications.filter((n) => !n.read);
    case 'leads':
      return notifications.filter((n) =>
        ['LEAD_CAPTURED', 'LEAD_HOT', 'LEAD_ASSIGNED'].includes(n.notificationType)
      );
    case 'cases':
      return notifications.filter((n) =>
        ['CASE_CREATED', 'CASE_UPDATED', 'CASE_STATUS_CHANGED', 'CASE_ASSIGNED'].includes(
          n.notificationType
        )
      );
    case 'appointments':
      return notifications.filter((n) =>
        ['APPOINTMENT_SCHEDULED', 'APPOINTMENT_REMINDER', 'DEADLINE_REMINDER'].includes(
          n.notificationType
        )
      );
    case 'system':
      return notifications.filter((n) =>
        ['SYSTEM_ALERT', 'WELCOME', 'PASSWORD_RESET'].includes(n.notificationType)
      );
    default:
      return notifications;
  }
}

function NotificationCard({
  notification,
  onMarkAsRead,
  onDelete,
}: {
  notification: Notification;
  onMarkAsRead: (id: string) => void;
  onDelete: (id: string) => void;
}) {
  const colors = getNotificationColor(notification.notificationType);
  const icon = getNotificationIcon(notification.notificationType);
  const [showActions, setShowActions] = useState(false);

  return (
    <div
      className={`rounded-lg border transition-all ${
        notification.read ? 'border-gray-200 bg-white' : 'border-blue-200 bg-blue-50/50'
      }`}
      onMouseEnter={() => setShowActions(true)}
      onMouseLeave={() => setShowActions(false)}
    >
      <div className="flex gap-4 p-4">
        {/* Icon */}
        <div
          className={`flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full ${colors.bg}`}
        >
          <span className="text-xl">{icon}</span>
        </div>

        {/* Content */}
        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <h3
                  className={`text-sm font-semibold ${notification.read ? 'text-gray-700' : 'text-gray-900'}`}
                >
                  {notification.title}
                </h3>
                {!notification.read && <span className="h-2 w-2 rounded-full bg-blue-600"></span>}
              </div>
              <span className={`text-xs ${colors.text}`}>
                {getTypeLabel(notification.notificationType)}
              </span>
            </div>

            {/* Actions */}
            <div
              className={`flex items-center gap-2 transition-opacity ${showActions ? 'opacity-100' : 'opacity-0'}`}
            >
              {!notification.read && (
                <button
                  onClick={() => onMarkAsRead(notification.id)}
                  className="rounded-lg p-1.5 text-gray-400 hover:bg-gray-100 hover:text-gray-600"
                  title="Marcar como leída"
                >
                  <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                </button>
              )}
              <button
                onClick={() => onDelete(notification.id)}
                className="rounded-lg p-1.5 text-gray-400 hover:bg-red-50 hover:text-red-600"
                title="Eliminar"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                  />
                </svg>
              </button>
            </div>
          </div>

          {notification.message && (
            <p className="mt-2 text-sm text-gray-600">{notification.message}</p>
          )}

          <div className="mt-3 flex items-center justify-between">
            <span className="text-xs text-gray-400">{formatDateTime(notification.createdAt)}</span>
            {notification.actionUrl && (
              <Link
                href={notification.actionUrl}
                className="text-xs font-medium text-primary-600 hover:text-primary-700"
              >
                Ver detalles →
              </Link>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function NotificationsContent() {
  const { user } = useAuth();
  const [filter, setFilter] = useState<FilterType>('all');
  const [page, setPage] = useState(0);
  const itemsPerPage = 20;

  const {
    data: notificationsData,
    isLoading,
    error,
  } = useNotifications(user?.accountId, page, itemsPerPage);
  const markAsRead = useMarkAsRead();
  const markAllAsRead = useMarkAllAsRead();
  const deleteNotification = useDeleteNotification();

  const notifications = notificationsData?.notifications || [];
  const totalElements = notificationsData?.totalElements || 0;
  const totalPages = Math.ceil(totalElements / itemsPerPage);

  const filteredNotifications = filterNotifications(notifications, filter);
  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleMarkAsRead = (id: string) => {
    markAsRead.mutate(id);
  };

  const handleMarkAllAsRead = () => {
    if (user?.accountId) {
      markAllAsRead.mutate(user.accountId);
    }
  };

  const handleDelete = (id: string) => {
    if (confirm('¿Estás seguro de eliminar esta notificación?')) {
      deleteNotification.mutate(id);
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-center text-red-600">
        Error al cargar las notificaciones. Por favor, intenta de nuevo.
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Notificaciones</h1>
            <p className="mt-1 text-sm text-gray-500">
              {totalElements} notificaciones en total
              {unreadCount > 0 && ` · ${unreadCount} sin leer`}
            </p>
          </div>
          {unreadCount > 0 && (
            <button
              onClick={handleMarkAllAsRead}
              disabled={markAllAsRead.isPending}
              className="inline-flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:opacity-50"
            >
              <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
              Marcar todas como leídas
            </button>
          )}
        </div>

        {/* Filters */}
        <div className="mt-6 flex flex-wrap gap-2">
          {filterOptions.map((option) => (
            <button
              key={option.value}
              onClick={() => setFilter(option.value)}
              className={`rounded-full px-4 py-1.5 text-sm font-medium transition-colors ${
                filter === option.value
                  ? 'bg-primary-600 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>
      </div>

      {/* Notifications List */}
      {filteredNotifications.length > 0 ? (
        <div className="space-y-4">
          {filteredNotifications.map((notification) => (
            <NotificationCard
              key={notification.id}
              notification={notification}
              onMarkAsRead={handleMarkAsRead}
              onDelete={handleDelete}
            />
          ))}
        </div>
      ) : (
        <div className="rounded-lg border border-gray-200 bg-white py-16 text-center">
          <svg
            className="mx-auto h-16 w-16 text-gray-300"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
            />
          </svg>
          <h3 className="mt-4 text-lg font-medium text-gray-900">No hay notificaciones</h3>
          <p className="mt-2 text-sm text-gray-500">
            {filter === 'all'
              ? 'No tienes notificaciones aún.'
              : 'No hay notificaciones que coincidan con este filtro.'}
          </p>
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-8 flex items-center justify-between border-t border-gray-200 pt-6">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="inline-flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 19l-7-7 7-7"
              />
            </svg>
            Anterior
          </button>
          <span className="text-sm text-gray-500">
            Página {page + 1} de {totalPages}
          </span>
          <button
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            disabled={page >= totalPages - 1}
            className="inline-flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 shadow-sm hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            Siguiente
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      )}
    </div>
  );
}

export default function NotificationsPage() {
  return (
    <AuthGuard>
      <NotificationsContent />
    </AuthGuard>
  );
}
