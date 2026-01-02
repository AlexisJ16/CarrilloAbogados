'use client';

import { useCreateCase } from '@/lib/api/cases';
import { AuthGuard } from '@/lib/auth';
import type { CaseType, PracticeArea } from '@/types/case';
import { caseTypeLabels, practiceAreaLabels } from '@/types/case';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

// Validation schema
const createCaseSchema = z.object({
  title: z
    .string()
    .min(1, 'El título es requerido')
    .min(5, 'El título debe tener al menos 5 caracteres')
    .max(200, 'El título no puede exceder 200 caracteres'),
  description: z.string().max(2000, 'La descripción no puede exceder 2000 caracteres').optional(),
  clientId: z.string().min(1, 'Debe seleccionar un cliente'),
  assignedLawyerId: z.string().min(1, 'Debe asignar un abogado responsable'),
  practiceArea: z.enum([
    'ADMINISTRATIVE_LAW',
    'COMPETITION_LAW',
    'CORPORATE_LAW',
    'TELECOMMUNICATIONS_LAW',
    'TRADEMARK_LAW',
  ] as const),
  caseType: z.enum(['JUDICIAL', 'ADMINISTRATIVE', 'CONSULTANCY'] as const),
  court: z.string().max(150, 'El nombre del tribunal no puede exceder 150 caracteres').optional(),
  filingNumber: z
    .string()
    .max(50, 'El número de radicado no puede exceder 50 caracteres')
    .optional(),
});

type CreateCaseFormData = z.infer<typeof createCaseSchema>;

// Mock data for lawyers (will be replaced with API call)
const MOCK_LAWYERS = [
  { id: '1', name: 'Dr. Omar Carrillo' },
  { id: '2', name: 'Dra. María García' },
  { id: '3', name: 'Dr. Carlos Rodríguez' },
];

function CreateCaseForm() {
  const router = useRouter();
  const createCase = useCreateCase();
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<CreateCaseFormData>({
    resolver: zodResolver(createCaseSchema),
    defaultValues: {
      title: '',
      description: '',
      clientId: '',
      assignedLawyerId: '',
      practiceArea: 'CORPORATE_LAW',
      caseType: 'CONSULTANCY',
      court: '',
      filingNumber: '',
    },
  });

  const onSubmit = async (data: CreateCaseFormData) => {
    try {
      await createCase.mutateAsync({
        title: data.title,
        description: data.description || undefined,
        clientId: data.clientId,
        assignedLawyerId: data.assignedLawyerId,
        practiceArea: data.practiceArea as PracticeArea,
        caseType: data.caseType as CaseType,
        court: data.court || undefined,
        filingNumber: data.filingNumber || undefined,
      });
      setSuccessMessage('Caso creado exitosamente');
      setTimeout(() => {
        router.push('/cases');
      }, 2000);
    } catch (error) {
      console.error('Error creating case:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {/* Success Message */}
      {successMessage && (
        <div className="rounded-md bg-green-50 p-4">
          <div className="flex">
            <svg
              className="h-5 w-5 text-green-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <p className="ml-3 text-sm font-medium text-green-800">{successMessage}</p>
          </div>
        </div>
      )}

      {/* Error Message */}
      {createCase.isError && (
        <div className="rounded-md bg-red-50 p-4">
          <div className="flex">
            <svg
              className="h-5 w-5 text-red-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <p className="ml-3 text-sm font-medium text-red-800">Error al crear el caso</p>
          </div>
        </div>
      )}

      {/* Basic Info */}
      <div className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-medium text-gray-900">Información Básica</h2>
        <div className="grid gap-6 md:grid-cols-2">
          {/* Title */}
          <div className="md:col-span-2">
            <label htmlFor="title" className="block text-sm font-medium text-gray-700">
              Título del Caso *
            </label>
            <input
              {...register('title')}
              type="text"
              id="title"
              placeholder="Ej: Registro de marca comercial ACME"
              className={`mt-1 block w-full rounded-md border ${
                errors.title ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            />
            {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title.message}</p>}
          </div>

          {/* Description */}
          <div className="md:col-span-2">
            <label htmlFor="description" className="block text-sm font-medium text-gray-700">
              Descripción
            </label>
            <textarea
              {...register('description')}
              id="description"
              rows={4}
              placeholder="Describa brevemente el caso..."
              className={`mt-1 block w-full rounded-md border ${
                errors.description ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            />
            {errors.description && (
              <p className="mt-1 text-sm text-red-600">{errors.description.message}</p>
            )}
          </div>

          {/* Client ID */}
          <div>
            <label htmlFor="clientId" className="block text-sm font-medium text-gray-700">
              Cliente *
            </label>
            <input
              {...register('clientId')}
              type="text"
              id="clientId"
              placeholder="ID del cliente"
              className={`mt-1 block w-full rounded-md border ${
                errors.clientId ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            />
            {errors.clientId && (
              <p className="mt-1 text-sm text-red-600">{errors.clientId.message}</p>
            )}
            <p className="mt-1 text-xs text-gray-500">
              Ingrese el ID del cliente o busque en la lista
            </p>
          </div>

          {/* Assigned Lawyer */}
          <div>
            <label htmlFor="assignedLawyerId" className="block text-sm font-medium text-gray-700">
              Abogado Responsable *
            </label>
            <select
              {...register('assignedLawyerId')}
              id="assignedLawyerId"
              className={`mt-1 block w-full rounded-md border ${
                errors.assignedLawyerId ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            >
              <option value="">Seleccione un abogado</option>
              {MOCK_LAWYERS.map((lawyer) => (
                <option key={lawyer.id} value={lawyer.id}>
                  {lawyer.name}
                </option>
              ))}
            </select>
            {errors.assignedLawyerId && (
              <p className="mt-1 text-sm text-red-600">{errors.assignedLawyerId.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* Classification */}
      <div className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-medium text-gray-900">Clasificación</h2>
        <div className="grid gap-6 md:grid-cols-2">
          {/* Practice Area */}
          <div>
            <label htmlFor="practiceArea" className="block text-sm font-medium text-gray-700">
              Área de Práctica *
            </label>
            <select
              {...register('practiceArea')}
              id="practiceArea"
              className={`mt-1 block w-full rounded-md border ${
                errors.practiceArea ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            >
              {Object.entries(practiceAreaLabels).map(([value, label]) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
            {errors.practiceArea && (
              <p className="mt-1 text-sm text-red-600">{errors.practiceArea.message}</p>
            )}
          </div>

          {/* Case Type */}
          <div>
            <label htmlFor="caseType" className="block text-sm font-medium text-gray-700">
              Tipo de Caso *
            </label>
            <select
              {...register('caseType')}
              id="caseType"
              className={`mt-1 block w-full rounded-md border ${
                errors.caseType ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            >
              {Object.entries(caseTypeLabels).map(([value, label]) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
            {errors.caseType && (
              <p className="mt-1 text-sm text-red-600">{errors.caseType.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* Proceeding Details */}
      <div className="rounded-lg border border-gray-200 bg-white p-6">
        <h2 className="mb-4 text-lg font-medium text-gray-900">Datos Procesales (Opcional)</h2>
        <div className="grid gap-6 md:grid-cols-2">
          {/* Court */}
          <div>
            <label htmlFor="court" className="block text-sm font-medium text-gray-700">
              Tribunal / Entidad
            </label>
            <input
              {...register('court')}
              type="text"
              id="court"
              placeholder="Ej: Juzgado 1° Civil del Circuito de Cali"
              className={`mt-1 block w-full rounded-md border ${
                errors.court ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            />
            {errors.court && <p className="mt-1 text-sm text-red-600">{errors.court.message}</p>}
          </div>

          {/* Filing Number */}
          <div>
            <label htmlFor="filingNumber" className="block text-sm font-medium text-gray-700">
              Número de Radicado
            </label>
            <input
              {...register('filingNumber')}
              type="text"
              id="filingNumber"
              placeholder="Ej: 2024-00001"
              className={`mt-1 block w-full rounded-md border ${
                errors.filingNumber ? 'border-red-300' : 'border-gray-300'
              } px-3 py-2 shadow-sm focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500`}
            />
            {errors.filingNumber && (
              <p className="mt-1 text-sm text-red-600">{errors.filingNumber.message}</p>
            )}
          </div>
        </div>
      </div>

      {/* Actions */}
      <div className="flex justify-end gap-4">
        <Link
          href="/cases"
          className="rounded-md border border-gray-300 bg-white px-6 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
        >
          Cancelar
        </Link>
        <button
          type="submit"
          disabled={isSubmitting || createCase.isPending}
          className="inline-flex items-center rounded-md bg-primary-600 px-6 py-2 text-sm font-medium text-white hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-50"
        >
          {isSubmitting || createCase.isPending ? (
            <>
              <svg
                className="mr-2 h-4 w-4 animate-spin"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
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
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                />
              </svg>
              Creando...
            </>
          ) : (
            'Crear Caso'
          )}
        </button>
      </div>
    </form>
  );
}

export default function NewCasePage() {
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
                <nav className="ml-4 flex items-center">
                  <Link href="/cases" className="text-sm text-gray-500 hover:text-gray-700">
                    Casos
                  </Link>
                  <span className="mx-2 text-gray-300">/</span>
                  <span className="text-sm font-medium text-gray-700">Nuevo Caso</span>
                </nav>
              </div>
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main className="mx-auto max-w-3xl px-4 py-8 sm:px-6 lg:px-8">
          <div className="mb-6">
            <h1 className="text-2xl font-bold text-gray-900">Crear Nuevo Caso</h1>
            <p className="mt-1 text-sm text-gray-500">
              Complete la información para registrar un nuevo caso legal.
            </p>
          </div>

          <CreateCaseForm />
        </main>
      </div>
    </AuthGuard>
  );
}
