'use client';

import { useLeads, useUpdateLead } from '@/lib/api/leads';
import { AuthGuard } from '@/lib/auth';
import type { Lead, LeadCategory, LeadStatus } from '@/types/lead';
import Link from 'next/link';
import { useState } from 'react';

// Status badge colors
const statusColors: Record<LeadStatus, { bg: string; text: string }> = {
  NUEVO: { bg: 'bg-blue-100', text: 'text-blue-800' },
  NURTURING: { bg: 'bg-purple-100', text: 'text-purple-800' },
  MQL: { bg: 'bg-yellow-100', text: 'text-yellow-800' },
  SQL: { bg: 'bg-orange-100', text: 'text-orange-800' },
  CONVERTIDO: { bg: 'bg-green-100', text: 'text-green-800' },
  CHURNED: { bg: 'bg-gray-100', text: 'text-gray-800' },
};

// Category badge colors
const categoryColors: Record<LeadCategory, { bg: string; text: string }> = {
  HOT: { bg: 'bg-red-100', text: 'text-red-800' },
  WARM: { bg: 'bg-yellow-100', text: 'text-yellow-800' },
  COLD: { bg: 'bg-blue-100', text: 'text-blue-800' },
};

// Status labels in Spanish
const statusLabels: Record<LeadStatus, string> = {
  NUEVO: 'Nuevo',
  NURTURING: 'En Nurturing',
  MQL: 'MQL',
  SQL: 'SQL',
  CONVERTIDO: 'Convertido',
  CHURNED: 'Perdido',
};

function LeadCard({ lead }: { lead: Lead }) {
  const updateLead = useUpdateLead();
  const [isExpanded, setIsExpanded] = useState(false);

  const handleStatusChange = async (newStatus: LeadStatus) => {
    try {
      await updateLead.mutateAsync({
        id: lead.id,
        data: { status: newStatus },
      });
    } catch (error) {
      console.error('Error updating lead status:', error);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-md">
      {/* Header */}
      <div className="border-b border-gray-100 p-4">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <h3 className="font-semibold text-gray-900">{lead.nombre}</h3>
              {lead.category && (
                <span
                  className={`inline-flex items-center rounded px-2 py-0.5 text-xs font-medium ${categoryColors[lead.category].bg} ${categoryColors[lead.category].text}`}
                >
                  {lead.category}
                  {lead.leadScore && ` (${lead.leadScore})`}
                </span>
              )}
            </div>
            <p className="mt-1 text-sm text-gray-500">{lead.email}</p>
          </div>
          <span
            className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${statusColors[lead.status].bg} ${statusColors[lead.status].text}`}
          >
            {statusLabels[lead.status]}
          </span>
        </div>
      </div>

      {/* Quick Info */}
      <div className="grid grid-cols-3 gap-4 bg-gray-50 px-4 py-3 text-sm">
        <div>
          <span className="text-gray-500">Servicio</span>
          <p className="truncate font-medium text-gray-900">{lead.servicio}</p>
        </div>
        <div>
          <span className="text-gray-500">Empresa</span>
          <p className="font-medium text-gray-900">{lead.empresa || '-'}</p>
        </div>
        <div>
          <span className="text-gray-500">Recibido</span>
          <p className="font-medium text-gray-900">{formatDate(lead.createdAt)}</p>
        </div>
      </div>

      {/* Expandable Content */}
      {isExpanded && (
        <div className="space-y-4 border-t border-gray-100 p-4">
          {/* Contact Info */}
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-500">Teléfono</span>
              <p className="font-medium text-gray-900">{lead.telefono || 'No proporcionado'}</p>
            </div>
            <div>
              <span className="text-gray-500">Cargo</span>
              <p className="font-medium text-gray-900">{lead.cargo || 'No proporcionado'}</p>
            </div>
          </div>

          {/* Message */}
          <div>
            <span className="text-sm text-gray-500">Mensaje</span>
            <p className="mt-1 rounded-lg bg-gray-50 p-3 text-gray-900">{lead.mensaje}</p>
          </div>

          {/* Tracking Info */}
          {(lead.emailsSent || lead.emailsOpened || lead.emailsClicked) && (
            <div className="grid grid-cols-3 gap-4 rounded-lg bg-blue-50 p-3 text-sm">
              <div>
                <span className="text-blue-600">Emails Enviados</span>
                <p className="font-semibold text-blue-900">{lead.emailsSent || 0}</p>
              </div>
              <div>
                <span className="text-blue-600">Abiertos</span>
                <p className="font-semibold text-blue-900">{lead.emailsOpened || 0}</p>
              </div>
              <div>
                <span className="text-blue-600">Clicks</span>
                <p className="font-semibold text-blue-900">{lead.emailsClicked || 0}</p>
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="flex items-center justify-between pt-2">
            <div className="flex items-center gap-2">
              <label className="text-sm text-gray-500">Cambiar estado:</label>
              <select
                value={lead.status}
                onChange={(e) => handleStatusChange(e.target.value as LeadStatus)}
                disabled={updateLead.isPending}
                className="rounded-md border border-gray-300 px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="NUEVO">Nuevo</option>
                <option value="NURTURING">En Nurturing</option>
                <option value="MQL">MQL</option>
                <option value="SQL">SQL</option>
                <option value="CONVERTIDO">Convertido</option>
                <option value="CHURNED">Perdido</option>
              </select>
            </div>
            <div className="flex items-center gap-2">
              {lead.telefono && (
                <a
                  href={`tel:${lead.telefono}`}
                  className="inline-flex items-center rounded-md border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50"
                >
                  <svg
                    className="mr-1.5 h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                    />
                  </svg>
                  Llamar
                </a>
              )}
              <a
                href={`mailto:${lead.email}`}
                className="inline-flex items-center rounded-md border border-transparent bg-primary-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-primary-700"
              >
                <svg
                  className="mr-1.5 h-4 w-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </svg>
                Enviar Email
              </a>
            </div>
          </div>
        </div>
      )}

      {/* Toggle Button */}
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="flex w-full items-center justify-center gap-1 px-4 py-2 text-sm text-gray-500 transition-colors hover:bg-gray-50 hover:text-gray-700"
      >
        {isExpanded ? (
          <>
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 15l7-7 7 7"
              />
            </svg>
            Menos detalles
          </>
        ) : (
          <>
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M19 9l-7 7-7-7"
              />
            </svg>
            Ver detalles
          </>
        )}
      </button>
    </div>
  );
}

function LeadsContent() {
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [categoryFilter, setCategoryFilter] = useState<string>('');

  const {
    data: leads,
    isLoading,
    error,
  } = useLeads({
    status: statusFilter || undefined,
    category: categoryFilter || undefined,
  });

  // Stats calculation
  const stats = {
    total: leads?.length || 0,
    hot: leads?.filter((l) => l.category === 'HOT').length || 0,
    warm: leads?.filter((l) => l.category === 'WARM').length || 0,
    cold: leads?.filter((l) => l.category === 'COLD').length || 0,
    new: leads?.filter((l) => l.status === 'NUEVO').length || 0,
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="text-center">
          <svg
            className="mx-auto h-10 w-10 animate-spin text-primary-600"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          <p className="mt-4 text-gray-500">Cargando leads...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 p-6 text-center">
        <svg
          className="mx-auto h-12 w-12 text-red-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
          />
        </svg>
        <h3 className="mt-4 text-lg font-medium text-red-800">Error al cargar leads</h3>
        <p className="mt-2 text-red-600">Por favor intente nuevamente más tarde.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 gap-4 md:grid-cols-5">
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Total Leads</p>
          <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
        </div>
        <div className="rounded-lg border border-red-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-red-600">HOT 🔥</p>
          <p className="text-2xl font-bold text-red-700">{stats.hot}</p>
        </div>
        <div className="rounded-lg border border-yellow-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-yellow-600">WARM</p>
          <p className="text-2xl font-bold text-yellow-700">{stats.warm}</p>
        </div>
        <div className="rounded-lg border border-blue-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-blue-600">COLD</p>
          <p className="text-2xl font-bold text-blue-700">{stats.cold}</p>
        </div>
        <div className="rounded-lg border border-green-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-green-600">Nuevos</p>
          <p className="text-2xl font-bold text-green-700">{stats.new}</p>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap items-center gap-4 rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-gray-700">Estado:</label>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="rounded-md border border-gray-300 px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">Todos</option>
            <option value="NUEVO">Nuevo</option>
            <option value="NURTURING">En Nurturing</option>
            <option value="MQL">MQL</option>
            <option value="SQL">SQL</option>
            <option value="CONVERTIDO">Convertido</option>
            <option value="CHURNED">Perdido</option>
          </select>
        </div>
        <div className="flex items-center gap-2">
          <label className="text-sm font-medium text-gray-700">Categoría:</label>
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="rounded-md border border-gray-300 px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          >
            <option value="">Todas</option>
            <option value="HOT">HOT 🔥</option>
            <option value="WARM">WARM</option>
            <option value="COLD">COLD</option>
          </select>
        </div>
        {(statusFilter || categoryFilter) && (
          <button
            onClick={() => {
              setStatusFilter('');
              setCategoryFilter('');
            }}
            className="text-sm text-gray-500 underline hover:text-gray-700"
          >
            Limpiar filtros
          </button>
        )}
      </div>

      {/* Leads List */}
      {leads && leads.length > 0 ? (
        <div className="grid gap-4">
          {leads.map((lead) => (
            <LeadCard key={lead.id} lead={lead} />
          ))}
        </div>
      ) : (
        <div className="rounded-lg border border-gray-200 bg-white p-12 text-center shadow-sm">
          <svg
            className="mx-auto h-12 w-12 text-gray-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
            />
          </svg>
          <h3 className="mt-4 text-lg font-medium text-gray-900">No hay leads</h3>
          <p className="mt-2 text-gray-500">
            {statusFilter || categoryFilter
              ? 'No se encontraron leads con los filtros seleccionados.'
              : 'Los leads aparecerán aquí cuando lleguen del formulario de contacto.'}
          </p>
        </div>
      )}
    </div>
  );
}

// Page Layout with Navigation
function LeadsLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-gray-100">
      {/* Top Navigation */}
      <nav className="bg-white shadow-sm">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 justify-between">
            <div className="flex items-center">
              <Link href="/dashboard" className="flex items-center">
                <div className="flex h-8 w-8 items-center justify-center rounded bg-primary-600">
                  <span className="text-sm font-bold text-white">CA</span>
                </div>
                <span className="ml-2 text-lg font-semibold text-gray-900">Carrillo Abogados</span>
              </Link>
              <span className="ml-6 text-gray-400">/</span>
              <span className="ml-2 font-medium text-gray-900">Leads</span>
            </div>
            <div className="flex items-center">
              <Link href="/dashboard" className="text-sm text-gray-500 hover:text-gray-700">
                ← Volver al Dashboard
              </Link>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Gestión de Leads</h1>
          <p className="mt-1 text-gray-500">
            Administre y haga seguimiento a los leads capturados desde el formulario de contacto.
          </p>
        </div>
        {children}
      </main>
    </div>
  );
}

export default function LeadsPage() {
  return (
    <AuthGuard requiredRoles={['ROLE_LAWYER', 'ROLE_ADMIN']}>
      <LeadsLayout>
        <LeadsContent />
      </LeadsLayout>
    </AuthGuard>
  );
}
