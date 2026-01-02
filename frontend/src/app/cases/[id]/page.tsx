'use client';

import { useCase, useUpdateCaseStatus } from '@/lib/api/cases';
import { AuthGuard } from '@/lib/auth';
import { CaseStatus, CaseType, PracticeArea } from '@/types/case';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { useState } from 'react';

const statusColors: Record<CaseStatus, { bg: string; text: string; label: string }> = {
  OPEN: { bg: 'bg-blue-100', text: 'text-blue-800', label: 'Abierto' },
  IN_PROGRESS: { bg: 'bg-yellow-100', text: 'text-yellow-800', label: 'En Progreso' },
  SUSPENDED: { bg: 'bg-orange-100', text: 'text-orange-800', label: 'Suspendido' },
  CLOSED: { bg: 'bg-gray-100', text: 'text-gray-800', label: 'Cerrado' },
};

const practiceAreaLabels: Record<PracticeArea, string> = {
  ADMINISTRATIVE_LAW: 'Derecho Administrativo',
  COMPETITION_LAW: 'Derecho de Competencias',
  CORPORATE_LAW: 'Derecho Corporativo',
  TELECOMMUNICATIONS_LAW: 'Derecho de Telecomunicaciones',
  TRADEMARK_LAW: 'Derecho de Marcas',
};

const caseTypeLabels: Record<CaseType, string> = {
  JUDICIAL: 'Judicial',
  ADMINISTRATIVE: 'Administrativo',
  CONSULTANCY: 'Consultoría',
};

function CaseDetailContent() {
  const params = useParams();
  const router = useRouter();
  const caseId = params.id as string;

  const { data: caseData, isLoading, error } = useCase(caseId);
  const updateStatus = useUpdateCaseStatus();

  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<CaseStatus | null>(null);

  const handleStatusChange = async () => {
    if (!selectedStatus || !caseData) return;

    try {
      await updateStatus.mutateAsync({ id: caseData.id, status: selectedStatus });
      setShowStatusModal(false);
      setSelectedStatus(null);
    } catch (err) {
      console.error('Error updating status:', err);
    }
  };

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary-600 border-t-transparent"></div>
      </div>
    );
  }

  if (error || !caseData) {
    return (
      <div className="rounded-lg bg-red-50 p-6 text-center">
        <h3 className="text-lg font-medium text-red-800">Error al cargar el caso</h3>
        <p className="mt-2 text-red-600">No se pudo encontrar el caso solicitado.</p>
        <Link
          href="/cases"
          className="mt-4 inline-block rounded-md bg-red-600 px-4 py-2 text-white hover:bg-red-700"
        >
          Volver a Casos
        </Link>
      </div>
    );
  }

  const statusConfig = statusColors[caseData.status];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <div className="flex items-center gap-2">
            <Link href="/cases" className="text-gray-500 hover:text-gray-700">
              ← Volver
            </Link>
          </div>
          <h1 className="mt-2 text-2xl font-bold text-gray-900">{caseData.title}</h1>
          <p className="mt-1 text-sm text-gray-500">Caso #{caseData.caseNumber}</p>
        </div>
        <div className="flex items-center gap-3">
          <span
            className={`rounded-full px-3 py-1 text-sm font-medium ${statusConfig.bg} ${statusConfig.text}`}
          >
            {statusConfig.label}
          </span>
          <button
            onClick={() => setShowStatusModal(true)}
            className="rounded-md bg-gray-100 px-3 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200"
          >
            Cambiar Estado
          </button>
        </div>
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        {/* Left Column - Case Details */}
        <div className="space-y-6 lg:col-span-2">
          {/* Description Card */}
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-semibold text-gray-900">Descripción</h2>
            <p className="mt-3 whitespace-pre-wrap text-gray-600">
              {caseData.description || 'Sin descripción disponible.'}
            </p>
          </div>

          {/* Timeline / Activities */}
          <div className="rounded-lg bg-white p-6 shadow">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">Línea de Tiempo</h2>
              <button className="rounded-md bg-primary-600 px-3 py-1.5 text-sm font-medium text-white hover:bg-primary-700">
                + Agregar Actividad
              </button>
            </div>

            <div className="mt-6">
              {caseData.activities && caseData.activities.length > 0 ? (
                <div className="relative">
                  <div className="absolute left-4 top-0 h-full w-0.5 bg-gray-200"></div>
                  <div className="space-y-6">
                    {caseData.activities.map((activity, index) => (
                      <div key={activity.id || index} className="relative flex gap-4">
                        <div className="relative z-10 flex h-8 w-8 items-center justify-center rounded-full bg-primary-100 text-primary-600">
                          <svg
                            className="h-4 w-4"
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
                        </div>
                        <div className="flex-1 rounded-lg bg-gray-50 p-4">
                          <div className="flex items-center justify-between">
                            <span className="font-medium text-gray-900">
                              {activity.activityType}
                            </span>
                            <span className="text-sm text-gray-500">
                              {new Date(activity.activityDate).toLocaleDateString('es-CO')}
                            </span>
                          </div>
                          <p className="mt-1 text-gray-600">{activity.description}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <div className="rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
                  <svg
                    className="mx-auto h-12 w-12 text-gray-400"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={1.5}
                      d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
                    />
                  </svg>
                  <p className="mt-2 text-gray-500">No hay actividades registradas</p>
                  <p className="text-sm text-gray-400">Las actividades del caso aparecerán aquí</p>
                </div>
              )}
            </div>
          </div>

          {/* Documents Section */}
          <div className="rounded-lg bg-white p-6 shadow">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">Documentos</h2>
              <button className="rounded-md bg-gray-100 px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-200">
                + Subir Documento
              </button>
            </div>
            <div className="mt-4 rounded-lg border-2 border-dashed border-gray-300 p-8 text-center">
              <svg
                className="mx-auto h-12 w-12 text-gray-400"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                />
              </svg>
              <p className="mt-2 text-gray-500">No hay documentos adjuntos</p>
              <p className="text-sm text-gray-400">Arrastra archivos aquí o haz clic para subir</p>
            </div>
          </div>
        </div>

        {/* Right Column - Info Cards */}
        <div className="space-y-6">
          {/* Case Info */}
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-semibold text-gray-900">Información del Caso</h2>
            <dl className="mt-4 space-y-4">
              <div>
                <dt className="text-sm font-medium text-gray-500">Área de Práctica</dt>
                <dd className="mt-1 text-gray-900">
                  {caseData.practiceArea
                    ? practiceAreaLabels[caseData.practiceArea]
                    : 'No especificada'}
                </dd>
              </div>
              <div>
                <dt className="text-sm font-medium text-gray-500">Tipo de Caso</dt>
                <dd className="mt-1 text-gray-900">
                  {caseData.caseType ? caseTypeLabels[caseData.caseType] : 'No especificado'}
                </dd>
              </div>
              {caseData.court && (
                <div>
                  <dt className="text-sm font-medium text-gray-500">Juzgado / Autoridad</dt>
                  <dd className="mt-1 text-gray-900">{caseData.court}</dd>
                </div>
              )}
              {caseData.filingNumber && (
                <div>
                  <dt className="text-sm font-medium text-gray-500">Número de Radicado</dt>
                  <dd className="mt-1 font-mono text-gray-900">{caseData.filingNumber}</dd>
                </div>
              )}
              <div>
                <dt className="text-sm font-medium text-gray-500">Fecha de Creación</dt>
                <dd className="mt-1 text-gray-900">
                  {caseData.createdAt
                    ? new Date(caseData.createdAt).toLocaleDateString('es-CO', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                      })
                    : 'No disponible'}
                </dd>
              </div>
            </dl>
          </div>

          {/* Client Info */}
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-semibold text-gray-900">Cliente</h2>
            <div className="mt-4">
              {caseData.clientId ? (
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-200 text-gray-600">
                    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">ID: {caseData.clientId}</p>
                    <Link
                      href={`/clients/${caseData.clientId}`}
                      className="text-sm text-primary-600 hover:underline"
                    >
                      Ver perfil →
                    </Link>
                  </div>
                </div>
              ) : (
                <p className="text-gray-500">No hay cliente asignado</p>
              )}
            </div>
          </div>

          {/* Assigned Lawyer */}
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-semibold text-gray-900">Abogado Asignado</h2>
            <div className="mt-4">
              {caseData.assignedLawyerId ? (
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-100 text-primary-600">
                    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                      />
                    </svg>
                  </div>
                  <div>
                    <p className="font-medium text-gray-900">
                      Abogado ID: {caseData.assignedLawyerId}
                    </p>
                    <p className="text-sm text-gray-500">Responsable del caso</p>
                  </div>
                </div>
              ) : (
                <p className="text-gray-500">No hay abogado asignado</p>
              )}
            </div>
          </div>

          {/* Quick Actions */}
          <div className="rounded-lg bg-white p-6 shadow">
            <h2 className="text-lg font-semibold text-gray-900">Acciones Rápidas</h2>
            <div className="mt-4 space-y-2">
              <button className="flex w-full items-center gap-2 rounded-md bg-gray-50 px-4 py-2 text-left text-gray-700 hover:bg-gray-100">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
                Programar Audiencia
              </button>
              <button className="flex w-full items-center gap-2 rounded-md bg-gray-50 px-4 py-2 text-left text-gray-700 hover:bg-gray-100">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </svg>
                Notificar Cliente
              </button>
              <button className="flex w-full items-center gap-2 rounded-md bg-gray-50 px-4 py-2 text-left text-gray-700 hover:bg-gray-100">
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"
                  />
                </svg>
                Generar Reporte
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Status Change Modal */}
      {showStatusModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl">
            <h3 className="text-lg font-semibold text-gray-900">Cambiar Estado del Caso</h3>
            <p className="mt-2 text-sm text-gray-500">Selecciona el nuevo estado para este caso.</p>

            <div className="mt-4 space-y-2">
              {(Object.keys(statusColors) as CaseStatus[]).map((status) => (
                <button
                  key={status}
                  onClick={() => setSelectedStatus(status)}
                  className={`flex w-full items-center justify-between rounded-md border-2 px-4 py-3 text-left transition-colors ${
                    selectedStatus === status
                      ? 'border-primary-600 bg-primary-50'
                      : 'border-gray-200 hover:border-gray-300'
                  }`}
                >
                  <span className="font-medium">{statusColors[status].label}</span>
                  <span
                    className={`rounded-full px-2 py-0.5 text-sm ${statusColors[status].bg} ${statusColors[status].text}`}
                  >
                    {status}
                  </span>
                </button>
              ))}
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => {
                  setShowStatusModal(false);
                  setSelectedStatus(null);
                }}
                className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-50"
              >
                Cancelar
              </button>
              <button
                onClick={handleStatusChange}
                disabled={!selectedStatus || updateStatus.isPending}
                className="rounded-md bg-primary-600 px-4 py-2 text-white hover:bg-primary-700 disabled:opacity-50"
              >
                {updateStatus.isPending ? 'Guardando...' : 'Guardar Cambio'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default function CaseDetailPage() {
  return (
    <AuthGuard requiredRoles={['ROLE_LAWYER', 'ROLE_ADMIN', 'ROLE_CLIENT']}>
      <div className="min-h-screen bg-gray-50">
        <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <CaseDetailContent />
        </div>
      </div>
    </AuthGuard>
  );
}
