'use client';

import { useCases, useUpdateCaseStatus } from '@/lib/api/cases';
import { AuthGuard } from '@/lib/auth';
import type { CaseStatus, LegalCase } from '@/types/case';
import { caseStatusLabels, practiceAreaLabels } from '@/types/case';
import Link from 'next/link';
import { useState } from 'react';

// Status badge colors
const statusColors: Record<CaseStatus, { bg: string; text: string; dot: string }> = {
  OPEN: { bg: 'bg-blue-100', text: 'text-blue-800', dot: 'bg-blue-400' },
  IN_PROGRESS: { bg: 'bg-yellow-100', text: 'text-yellow-800', dot: 'bg-yellow-400' },
  SUSPENDED: { bg: 'bg-orange-100', text: 'text-orange-800', dot: 'bg-orange-400' },
  CLOSED: { bg: 'bg-gray-100', text: 'text-gray-800', dot: 'bg-gray-400' },
};

function CaseCard({ legalCase }: { legalCase: LegalCase }) {
  const updateStatus = useUpdateCaseStatus();
  const [isExpanded, setIsExpanded] = useState(false);

  const handleStatusChange = async (newStatus: CaseStatus) => {
    try {
      await updateStatus.mutateAsync({ id: legalCase.id, status: newStatus });
    } catch (error) {
      console.error('Error updating case status:', error);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  };

  return (
    <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-md">
      {/* Header */}
      <div className="border-b border-gray-100 p-4">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <span className="text-sm font-medium text-primary-600">{legalCase.caseNumber}</span>
              <span
                className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${statusColors[legalCase.status].bg} ${statusColors[legalCase.status].text}`}
              >
                <span
                  className={`mr-1.5 h-1.5 w-1.5 rounded-full ${statusColors[legalCase.status].dot}`}
                />
                {caseStatusLabels[legalCase.status]}
              </span>
            </div>
            <h3 className="mt-1 font-semibold text-gray-900">{legalCase.title}</h3>
            <p className="mt-0.5 text-sm text-gray-500">
              {practiceAreaLabels[legalCase.practiceArea]}
            </p>
          </div>
        </div>
      </div>

      {/* Quick Info */}
      <div className="grid grid-cols-3 gap-4 bg-gray-50 px-4 py-3 text-sm">
        <div>
          <span className="text-gray-500">Cliente ID</span>
          <p className="font-medium text-gray-900">{legalCase.clientId}</p>
        </div>
        <div>
          <span className="text-gray-500">Tribunal</span>
          <p className="truncate font-medium text-gray-900">{legalCase.court || '-'}</p>
        </div>
        <div>
          <span className="text-gray-500">Inicio</span>
          <p className="font-medium text-gray-900">{formatDate(legalCase.createdAt)}</p>
        </div>
      </div>

      {/* Expandable Content */}
      {isExpanded && (
        <div className="space-y-4 border-t border-gray-100 p-4">
          {/* Description */}
          {legalCase.description && (
            <div>
              <span className="text-sm text-gray-500">Descripción</span>
              <p className="mt-1 rounded-lg bg-gray-50 p-3 text-gray-900">
                {legalCase.description}
              </p>
            </div>
          )}

          {/* Case Details */}
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-500">Número de Radicado</span>
              <p className="font-medium text-gray-900">{legalCase.filingNumber || 'No asignado'}</p>
            </div>
            <div>
              <span className="text-gray-500">Abogado Responsable</span>
              <p className="font-medium text-gray-900">
                {legalCase.assignedLawyerId || 'No asignado'}
              </p>
            </div>
          </div>

          {/* Activities */}
          {legalCase.activities && legalCase.activities.length > 0 && (
            <div>
              <span className="text-sm text-gray-500">Últimas Actuaciones</span>
              <ul className="mt-2 space-y-2">
                {legalCase.activities.slice(0, 3).map((activity) => (
                  <li
                    key={activity.id}
                    className="flex items-start gap-2 rounded-lg bg-gray-50 p-2 text-sm"
                  >
                    <span className="mt-1 h-2 w-2 flex-shrink-0 rounded-full bg-primary-400" />
                    <div>
                      <p className="font-medium text-gray-900">{activity.description}</p>
                      <p className="text-gray-500">{formatDate(activity.activityDate)}</p>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Actions */}
          <div className="flex items-center justify-between border-t border-gray-100 pt-4">
            <div className="flex items-center gap-2">
              <label className="text-sm text-gray-500">Cambiar estado:</label>
              <select
                value={legalCase.status}
                onChange={(e) => handleStatusChange(e.target.value as CaseStatus)}
                disabled={updateStatus.isPending}
                className="rounded-md border border-gray-300 px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="OPEN">Abierto</option>
                <option value="IN_PROGRESS">En Progreso</option>
                <option value="SUSPENDED">Suspendido</option>
                <option value="CLOSED">Cerrado</option>
              </select>
            </div>
            <Link
              href={`/cases/${legalCase.id}`}
              className="inline-flex items-center rounded-md border border-transparent bg-primary-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-primary-700"
            >
              Ver Detalles
              <svg className="ml-1.5 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M9 5l7 7-7 7"
                />
              </svg>
            </Link>
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

function CasesContent() {
  const [statusFilter, setStatusFilter] = useState<CaseStatus | ''>('');

  const {
    data: cases,
    isLoading,
    error,
  } = useCases({
    status: statusFilter || undefined,
  });

  // Stats calculation
  const stats = {
    total: cases?.length || 0,
    open: cases?.filter((c) => c.status === 'OPEN').length || 0,
    inProgress: cases?.filter((c) => c.status === 'IN_PROGRESS').length || 0,
    suspended: cases?.filter((c) => c.status === 'SUSPENDED').length || 0,
    closed: cases?.filter((c) => c.status === 'CLOSED').length || 0,
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
          <p className="mt-4 text-gray-500">Cargando casos...</p>
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
        <h3 className="mt-4 text-lg font-medium text-red-800">Error al cargar casos</h3>
        <p className="mt-2 text-red-600">Por favor intente nuevamente más tarde.</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 gap-4 md:grid-cols-5">
        <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
          <p className="text-sm text-gray-500">Total Casos</p>
          <p className="text-2xl font-bold text-gray-900">{stats.total}</p>
        </div>
        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
          <p className="text-sm text-blue-600">Abiertos</p>
          <p className="text-2xl font-bold text-blue-900">{stats.open}</p>
        </div>
        <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4">
          <p className="text-sm text-yellow-600">En Progreso</p>
          <p className="text-2xl font-bold text-yellow-900">{stats.inProgress}</p>
        </div>
        <div className="rounded-lg border border-orange-200 bg-orange-50 p-4">
          <p className="text-sm text-orange-600">Suspendidos</p>
          <p className="text-2xl font-bold text-orange-900">{stats.suspended}</p>
        </div>
        <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
          <p className="text-sm text-gray-600">Cerrados</p>
          <p className="text-2xl font-bold text-gray-900">{stats.closed}</p>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap items-center gap-4">
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as CaseStatus | '')}
          className="rounded-md border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
        >
          <option value="">Todos los estados</option>
          <option value="OPEN">Abiertos</option>
          <option value="IN_PROGRESS">En Progreso</option>
          <option value="SUSPENDED">Suspendidos</option>
          <option value="CLOSED">Cerrados</option>
        </select>

        <Link
          href="/cases/new"
          className="ml-auto inline-flex items-center rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
        >
          <svg className="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Nuevo Caso
        </Link>
      </div>

      {/* Cases List */}
      {cases && cases.length > 0 ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {cases.map((legalCase) => (
            <CaseCard key={legalCase.id} legalCase={legalCase} />
          ))}
        </div>
      ) : (
        <div className="rounded-lg border border-gray-200 bg-white p-12 text-center">
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
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            />
          </svg>
          <h3 className="mt-4 text-lg font-medium text-gray-900">No hay casos</h3>
          <p className="mt-2 text-gray-500">
            {statusFilter
              ? 'No se encontraron casos con este filtro.'
              : 'Comience creando un nuevo caso legal.'}
          </p>
          <Link
            href="/cases/new"
            className="mt-6 inline-flex items-center rounded-md bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700"
          >
            <svg className="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 4v16m8-8H4"
              />
            </svg>
            Crear Primer Caso
          </Link>
        </div>
      )}
    </div>
  );
}

export default function CasesPage() {
  return (
    <AuthGuard requiredRoles={['ROLE_LAWYER', 'ROLE_ADMIN']}>
      <div className="min-h-screen bg-gray-50">
        {/* Header */}
        <header className="bg-white shadow-sm">
          <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <Link href="/dashboard" className="flex items-center">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-600">
                    <span className="text-lg font-bold text-white">CA</span>
                  </div>
                  <span className="ml-3 text-xl font-semibold text-gray-900">
                    Carrillo Abogados
                  </span>
                </Link>
                <span className="ml-4 text-gray-300">|</span>
                <h1 className="ml-4 text-lg font-medium text-gray-700">Gestión de Casos</h1>
              </div>
              <nav className="flex items-center gap-4">
                <Link
                  href="/leads"
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  Leads
                </Link>
                <Link
                  href="/dashboard"
                  className="text-sm font-medium text-gray-600 hover:text-primary-600"
                >
                  Dashboard
                </Link>
              </nav>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <CasesContent />
        </main>
      </div>
    </AuthGuard>
  );
}
