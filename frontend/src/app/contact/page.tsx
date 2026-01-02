'use client';

import { useCreateLead } from '@/lib/api/leads';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

// Áreas de práctica del bufete
const PRACTICE_AREAS = [
  { value: 'derecho-administrativo', label: 'Derecho Administrativo y Contratación Estatal' },
  { value: 'derecho-competencias', label: 'Derecho de Competencias (Libre Competencia)' },
  { value: 'derecho-corporativo', label: 'Derecho Corporativo' },
  { value: 'derecho-telecomunicaciones', label: 'Derecho de Telecomunicaciones' },
  { value: 'derecho-marcas', label: 'Derecho de Marcas y Propiedad Industrial' },
  { value: 'otro', label: 'Otro / No estoy seguro' },
] as const;

// Validation schema
const contactSchema = z.object({
  nombre: z
    .string()
    .min(1, 'El nombre es requerido')
    .min(3, 'El nombre debe tener al menos 3 caracteres')
    .max(100, 'El nombre no puede exceder 100 caracteres'),
  email: z.string().min(1, 'El email es requerido').email('Ingrese un email válido'),
  telefono: z
    .string()
    .optional()
    .refine(
      (val) => !val || /^[+]?[\d\s-()]{7,20}$/.test(val),
      'Ingrese un número de teléfono válido'
    ),
  empresa: z
    .string()
    .max(100, 'El nombre de la empresa no puede exceder 100 caracteres')
    .optional(),
  cargo: z.string().max(50, 'El cargo no puede exceder 50 caracteres').optional(),
  servicio: z.string().min(1, 'Seleccione un área de interés'),
  mensaje: z
    .string()
    .min(1, 'El mensaje es requerido')
    .min(20, 'El mensaje debe tener al menos 20 caracteres')
    .max(2000, 'El mensaje no puede exceder 2000 caracteres'),
  acceptPrivacy: z
    .boolean()
    .refine((val) => val === true, 'Debe aceptar la política de privacidad'),
});

type ContactFormData = z.infer<typeof contactSchema>;

export default function ContactPage() {
  const [isSubmitted, setIsSubmitted] = useState(false);
  const createLead = useCreateLead();

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ContactFormData>({
    resolver: zodResolver(contactSchema),
    defaultValues: {
      nombre: '',
      email: '',
      telefono: '',
      empresa: '',
      cargo: '',
      servicio: '',
      mensaje: '',
      acceptPrivacy: false,
    },
  });

  const onSubmit = async (data: ContactFormData) => {
    try {
      await createLead.mutateAsync({
        nombre: data.nombre,
        email: data.email,
        telefono: data.telefono || undefined,
        empresa: data.empresa || undefined,
        cargo: data.cargo || undefined,
        servicio: data.servicio,
        mensaje: data.mensaje,
      });
      setIsSubmitted(true);
      reset();
    } catch (error) {
      console.error('Error submitting contact form:', error);
    }
  };

  if (isSubmitted) {
    return (
      <div className="min-h-screen bg-gray-50">
        {/* Header */}
        <header className="bg-white shadow-sm">
          <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
            <div className="flex items-center justify-between">
              <Link href="/" className="flex items-center">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-600">
                  <span className="text-lg font-bold text-white">CA</span>
                </div>
                <span className="ml-3 text-xl font-semibold text-gray-900">Carrillo Abogados</span>
              </Link>
            </div>
          </div>
        </header>

        {/* Success Message */}
        <main className="mx-auto max-w-3xl px-4 py-20 text-center sm:px-6 lg:px-8">
          <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
            <svg
              className="h-10 w-10 text-green-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>
          <h1 className="mt-6 text-3xl font-extrabold text-gray-900">¡Gracias por contactarnos!</h1>
          <p className="mt-4 text-lg text-gray-600">
            Hemos recibido su mensaje y uno de nuestros abogados se pondrá en contacto con usted en
            menos de <strong>24 horas hábiles</strong>.
          </p>
          <p className="mt-2 text-gray-500">
            Si su asunto es urgente, puede llamarnos directamente.
          </p>

          <div className="mt-8 space-y-4">
            <div className="rounded-lg bg-white p-6 shadow-sm">
              <h3 className="text-lg font-medium text-gray-900">Mientras tanto...</h3>
              <ul className="mt-4 space-y-2 text-left text-gray-600">
                <li className="flex items-start">
                  <svg
                    className="mr-2 mt-0.5 h-5 w-5 text-green-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                  Revisaremos su caso y le asignaremos al especialista adecuado
                </li>
                <li className="flex items-start">
                  <svg
                    className="mr-2 mt-0.5 h-5 w-5 text-green-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                  Le enviaremos un correo de confirmación a su email
                </li>
                <li className="flex items-start">
                  <svg
                    className="mr-2 mt-0.5 h-5 w-5 text-green-500"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M5 13l4 4L19 7"
                    />
                  </svg>
                  Prepararemos una propuesta personalizada para su situación
                </li>
              </ul>
            </div>

            <div className="flex justify-center space-x-4">
              <Link
                href="/"
                className="inline-flex items-center rounded-md border border-gray-300 bg-white px-6 py-3 text-base font-medium text-gray-700 shadow-sm hover:bg-gray-50"
              >
                Volver al inicio
              </Link>
              <button
                onClick={() => setIsSubmitted(false)}
                className="inline-flex items-center rounded-md border border-transparent bg-primary-600 px-6 py-3 text-base font-medium text-white shadow-sm hover:bg-primary-700"
              >
                Enviar otro mensaje
              </button>
            </div>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <Link href="/" className="flex items-center">
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary-600">
                <span className="text-lg font-bold text-white">CA</span>
              </div>
              <span className="ml-3 text-xl font-semibold text-gray-900">Carrillo Abogados</span>
            </Link>
            <div className="hidden items-center space-x-6 md:flex">
              <Link href="/login" className="text-sm font-medium text-gray-600 hover:text-gray-900">
                Iniciar Sesión
              </Link>
            </div>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
        <div className="lg:grid lg:grid-cols-2 lg:gap-16">
          {/* Left Column - Info */}
          <div className="mb-12 lg:mb-0">
            <h1 className="text-4xl font-extrabold text-gray-900 sm:text-5xl">Contáctenos</h1>
            <p className="mt-4 text-xl text-gray-600">
              ¿Tiene un asunto legal? Cuéntenos su caso y uno de nuestros especialistas se pondrá en
              contacto con usted.
            </p>

            {/* Contact Info */}
            <div className="mt-12 space-y-8">
              <div className="flex items-start">
                <div className="flex-shrink-0">
                  <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary-100">
                    <svg
                      className="h-6 w-6 text-primary-600"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                      />
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                      />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-medium text-gray-900">Dirección</h3>
                  <p className="mt-1 text-gray-600">
                    Cra. 40 #27-26, El Piloto
                    <br />
                    Edificio Torre de Cali, Piso 21
                    <br />
                    Oficina 2102A, Cali, Colombia
                  </p>
                </div>
              </div>

              <div className="flex items-start">
                <div className="flex-shrink-0">
                  <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary-100">
                    <svg
                      className="h-6 w-6 text-primary-600"
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
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-medium text-gray-900">Email</h3>
                  <p className="mt-1 text-gray-600">gerenciacarrilloabgd@gmail.com</p>
                </div>
              </div>

              <div className="flex items-start">
                <div className="flex-shrink-0">
                  <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary-100">
                    <svg
                      className="h-6 w-6 text-primary-600"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                      />
                    </svg>
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-lg font-medium text-gray-900">Horario de Atención</h3>
                  <p className="mt-1 text-gray-600">
                    Lunes a Viernes: 8:00 AM - 6:00 PM
                    <br />
                    Sábado: 9:00 AM - 1:00 PM
                  </p>
                </div>
              </div>
            </div>

            {/* Trust Badges */}
            <div className="mt-12">
              <h3 className="text-sm font-semibold uppercase tracking-wide text-gray-500">
                Más de 23 años de experiencia
              </h3>
              <div className="mt-4 flex flex-wrap gap-4">
                <div className="flex items-center text-sm text-gray-600">
                  <svg
                    className="mr-2 h-5 w-5 text-green-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  Especialistas en SIC
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <svg
                    className="mr-2 h-5 w-5 text-green-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  Trato personalizado
                </div>
                <div className="flex items-center text-sm text-gray-600">
                  <svg
                    className="mr-2 h-5 w-5 text-green-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      fillRule="evenodd"
                      d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                      clipRule="evenodd"
                    />
                  </svg>
                  Resultados comprobados
                </div>
              </div>
            </div>
          </div>

          {/* Right Column - Form */}
          <div className="rounded-2xl bg-white p-8 shadow-xl">
            <h2 className="mb-6 text-2xl font-bold text-gray-900">Envíenos un mensaje</h2>

            {/* Error Alert */}
            {createLead.isError && (
              <div className="mb-6 rounded-md bg-red-50 p-4">
                <div className="flex">
                  <div className="flex-shrink-0">
                    <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                      <path
                        fillRule="evenodd"
                        d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                        clipRule="evenodd"
                      />
                    </svg>
                  </div>
                  <div className="ml-3">
                    <p className="text-sm font-medium text-red-800">
                      Hubo un error al enviar el formulario. Por favor intente de nuevo.
                    </p>
                  </div>
                </div>
              </div>
            )}

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              {/* Nombre */}
              <div>
                <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">
                  Nombre completo *
                </label>
                <input
                  id="nombre"
                  type="text"
                  autoComplete="name"
                  {...register('nombre')}
                  className={`mt-1 block w-full border px-4 py-3 ${
                    errors.nombre ? 'border-red-300' : 'border-gray-300'
                  } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                  placeholder="Juan Pérez"
                />
                {errors.nombre && (
                  <p className="mt-1 text-sm text-red-600">{errors.nombre.message}</p>
                )}
              </div>

              {/* Email y Teléfono */}
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                    Email *
                  </label>
                  <input
                    id="email"
                    type="email"
                    autoComplete="email"
                    {...register('email')}
                    className={`mt-1 block w-full border px-4 py-3 ${
                      errors.email ? 'border-red-300' : 'border-gray-300'
                    } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                    placeholder="juan@empresa.com"
                  />
                  {errors.email && (
                    <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
                  )}
                </div>
                <div>
                  <label htmlFor="telefono" className="block text-sm font-medium text-gray-700">
                    Teléfono
                  </label>
                  <input
                    id="telefono"
                    type="tel"
                    autoComplete="tel"
                    {...register('telefono')}
                    className={`mt-1 block w-full border px-4 py-3 ${
                      errors.telefono ? 'border-red-300' : 'border-gray-300'
                    } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                    placeholder="+57 300 123 4567"
                  />
                  {errors.telefono && (
                    <p className="mt-1 text-sm text-red-600">{errors.telefono.message}</p>
                  )}
                </div>
              </div>

              {/* Empresa y Cargo */}
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div>
                  <label htmlFor="empresa" className="block text-sm font-medium text-gray-700">
                    Empresa
                  </label>
                  <input
                    id="empresa"
                    type="text"
                    autoComplete="organization"
                    {...register('empresa')}
                    className={`mt-1 block w-full border px-4 py-3 ${
                      errors.empresa ? 'border-red-300' : 'border-gray-300'
                    } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                    placeholder="Mi Empresa SAS"
                  />
                  {errors.empresa && (
                    <p className="mt-1 text-sm text-red-600">{errors.empresa.message}</p>
                  )}
                </div>
                <div>
                  <label htmlFor="cargo" className="block text-sm font-medium text-gray-700">
                    Cargo
                  </label>
                  <input
                    id="cargo"
                    type="text"
                    autoComplete="organization-title"
                    {...register('cargo')}
                    className={`mt-1 block w-full border px-4 py-3 ${
                      errors.cargo ? 'border-red-300' : 'border-gray-300'
                    } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                    placeholder="Gerente General"
                  />
                  {errors.cargo && (
                    <p className="mt-1 text-sm text-red-600">{errors.cargo.message}</p>
                  )}
                </div>
              </div>

              {/* Servicio */}
              <div>
                <label htmlFor="servicio" className="block text-sm font-medium text-gray-700">
                  Área de Interés *
                </label>
                <select
                  id="servicio"
                  {...register('servicio')}
                  className={`mt-1 block w-full border px-4 py-3 ${
                    errors.servicio ? 'border-red-300' : 'border-gray-300'
                  } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                >
                  <option value="">Seleccione un área...</option>
                  {PRACTICE_AREAS.map((area) => (
                    <option key={area.value} value={area.value}>
                      {area.label}
                    </option>
                  ))}
                </select>
                {errors.servicio && (
                  <p className="mt-1 text-sm text-red-600">{errors.servicio.message}</p>
                )}
              </div>

              {/* Mensaje */}
              <div>
                <label htmlFor="mensaje" className="block text-sm font-medium text-gray-700">
                  Cuéntenos su caso *
                </label>
                <textarea
                  id="mensaje"
                  rows={5}
                  {...register('mensaje')}
                  className={`mt-1 block w-full border px-4 py-3 ${
                    errors.mensaje ? 'border-red-300' : 'border-gray-300'
                  } rounded-lg shadow-sm focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500`}
                  placeholder="Describa brevemente su situación legal y cómo podemos ayudarle..."
                />
                {errors.mensaje && (
                  <p className="mt-1 text-sm text-red-600">{errors.mensaje.message}</p>
                )}
              </div>

              {/* Privacy Accept */}
              <div className="flex items-start">
                <input
                  id="acceptPrivacy"
                  type="checkbox"
                  {...register('acceptPrivacy')}
                  className="mt-0.5 h-4 w-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                />
                <label htmlFor="acceptPrivacy" className="ml-2 block text-sm text-gray-600">
                  He leído y acepto la{' '}
                  <Link href="/privacy" className="text-primary-600 hover:text-primary-500">
                    política de privacidad
                  </Link>{' '}
                  y autorizo el tratamiento de mis datos personales. *
                </label>
              </div>
              {errors.acceptPrivacy && (
                <p className="text-sm text-red-600">{errors.acceptPrivacy.message}</p>
              )}

              {/* Submit Button */}
              <div>
                <button
                  type="submit"
                  disabled={isSubmitting || createLead.isPending}
                  className="flex w-full justify-center rounded-lg border border-transparent bg-primary-600 px-4 py-3 text-base font-medium text-white shadow-sm transition-colors hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isSubmitting || createLead.isPending ? (
                    <>
                      <svg
                        className="-ml-1 mr-3 h-5 w-5 animate-spin text-white"
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
                      Enviando...
                    </>
                  ) : (
                    'Enviar Mensaje'
                  )}
                </button>
              </div>

              {/* Disclaimer */}
              <p className="text-center text-xs text-gray-500">
                Este formulario no crea una relación abogado-cliente. La información proporcionada
                será tratada de manera confidencial.
              </p>
            </form>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="mt-20 bg-gray-900">
        <div className="mx-auto max-w-7xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <div className="flex h-8 w-8 items-center justify-center rounded bg-white">
                <span className="text-sm font-bold text-gray-900">CA</span>
              </div>
              <span className="ml-2 font-medium text-white">Carrillo Abogados</span>
            </div>
            <p className="text-sm text-gray-400">
              © {new Date().getFullYear()} Carrillo ABGD SAS. Todos los derechos reservados.
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}
