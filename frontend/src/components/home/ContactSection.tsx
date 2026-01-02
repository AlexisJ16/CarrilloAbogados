'use client';

import { useCreateLead } from '@/lib/api/leads';
import { zodResolver } from '@hookform/resolvers/zod';
import { AlertCircle, CheckCircle, Loader2, Send } from 'lucide-react';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';

const contactSchema = z.object({
  nombre: z.string().min(2, 'El nombre debe tener al menos 2 caracteres'),
  email: z.string().email('Ingrese un correo electrónico válido'),
  telefono: z.string().optional(),
  empresa: z.string().optional(),
  servicio: z.string().min(1, 'Seleccione un servicio de interés'),
  mensaje: z.string().min(10, 'El mensaje debe tener al menos 10 caracteres'),
});

type ContactFormData = z.infer<typeof contactSchema>;

const servicios = [
  { value: 'administrativo', label: 'Derecho Administrativo' },
  { value: 'competencias', label: 'Derecho de Competencias' },
  { value: 'corporativo', label: 'Derecho Corporativo' },
  { value: 'telecomunicaciones', label: 'Derecho de Telecomunicaciones' },
  { value: 'marcas', label: 'Derecho de Marcas' },
  { value: 'otro', label: 'Otro / No estoy seguro' },
];

export function ContactSection() {
  const [submitStatus, setSubmitStatus] = useState<'idle' | 'success' | 'error'>('idle');
  const createLead = useCreateLead();
  
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<ContactFormData>({
    resolver: zodResolver(contactSchema),
  });

  const onSubmit = async (data: ContactFormData) => {
    try {
      await createLead.mutateAsync(data);
      setSubmitStatus('success');
      reset();
      setTimeout(() => setSubmitStatus('idle'), 5000);
    } catch (error) {
      console.error('Error al enviar formulario:', error);
      setSubmitStatus('error');
      setTimeout(() => setSubmitStatus('idle'), 5000);
    }
  };

  return (
    <section id="contacto" className="py-20 lg:py-28 bg-gray-50">
      <div className="container-section">
        <div className="grid lg:grid-cols-2 gap-12">
          {/* Content */}
          <div>
            <span className="text-accent-500 font-semibold text-sm uppercase tracking-wider">
              Contáctenos
            </span>
            <h2 className="text-3xl sm:text-4xl font-heading font-bold text-primary-500 mt-2 mb-6">
              ¿Necesita asesoría legal?
            </h2>
            <p className="text-gray-600 text-lg mb-8">
              Complete el formulario y uno de nuestros abogados se pondrá en contacto 
              con usted en menos de 24 horas para agendar una consulta inicial sin costo.
            </p>
            
            {/* Quick info */}
            <div className="space-y-4">
              <div className="flex items-center gap-4 p-4 bg-white rounded-lg shadow-sm">
                <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
                  <span className="text-2xl">⏱️</span>
                </div>
                <div>
                  <h4 className="font-semibold text-primary-500">Respuesta rápida</h4>
                  <p className="text-sm text-gray-600">Le respondemos en menos de 24 horas</p>
                </div>
              </div>
              <div className="flex items-center gap-4 p-4 bg-white rounded-lg shadow-sm">
                <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
                  <span className="text-2xl">💼</span>
                </div>
                <div>
                  <h4 className="font-semibold text-primary-500">Consulta inicial gratuita</h4>
                  <p className="text-sm text-gray-600">Evaluamos su caso sin compromiso</p>
                </div>
              </div>
              <div className="flex items-center gap-4 p-4 bg-white rounded-lg shadow-sm">
                <div className="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
                  <span className="text-2xl">🔒</span>
                </div>
                <div>
                  <h4 className="font-semibold text-primary-500">Confidencialidad garantizada</h4>
                  <p className="text-sm text-gray-600">Sus datos están protegidos</p>
                </div>
              </div>
            </div>
          </div>
          
          {/* Form */}
          <div className="card">
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
              {/* Nombre */}
              <div>
                <label htmlFor="nombre" className="label-field">
                  Nombre completo *
                </label>
                <input
                  id="nombre"
                  type="text"
                  className={`input-field ${errors.nombre ? 'border-red-500' : ''}`}
                  placeholder="Ej: Juan Carlos Pérez"
                  {...register('nombre')}
                />
                {errors.nombre && (
                  <p className="text-red-500 text-sm mt-1">{errors.nombre.message}</p>
                )}
              </div>
              
              {/* Email */}
              <div>
                <label htmlFor="email" className="label-field">
                  Correo electrónico *
                </label>
                <input
                  id="email"
                  type="email"
                  className={`input-field ${errors.email ? 'border-red-500' : ''}`}
                  placeholder="correo@empresa.com"
                  {...register('email')}
                />
                {errors.email && (
                  <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
                )}
              </div>
              
              {/* Teléfono y Empresa */}
              <div className="grid sm:grid-cols-2 gap-4">
                <div>
                  <label htmlFor="telefono" className="label-field">
                    Teléfono
                  </label>
                  <input
                    id="telefono"
                    type="tel"
                    className="input-field"
                    placeholder="+57 300 123 4567"
                    {...register('telefono')}
                  />
                </div>
                <div>
                  <label htmlFor="empresa" className="label-field">
                    Empresa
                  </label>
                  <input
                    id="empresa"
                    type="text"
                    className="input-field"
                    placeholder="Nombre de su empresa"
                    {...register('empresa')}
                  />
                </div>
              </div>
              
              {/* Servicio */}
              <div>
                <label htmlFor="servicio" className="label-field">
                  Área de interés *
                </label>
                <select
                  id="servicio"
                  className={`input-field ${errors.servicio ? 'border-red-500' : ''}`}
                  {...register('servicio')}
                >
                  <option value="">Seleccione un área...</option>
                  {servicios.map((s) => (
                    <option key={s.value} value={s.value}>
                      {s.label}
                    </option>
                  ))}
                </select>
                {errors.servicio && (
                  <p className="text-red-500 text-sm mt-1">{errors.servicio.message}</p>
                )}
              </div>
              
              {/* Mensaje */}
              <div>
                <label htmlFor="mensaje" className="label-field">
                  ¿Cómo podemos ayudarle? *
                </label>
                <textarea
                  id="mensaje"
                  rows={4}
                  className={`input-field resize-none ${errors.mensaje ? 'border-red-500' : ''}`}
                  placeholder="Describa brevemente su caso o consulta..."
                  {...register('mensaje')}
                />
                {errors.mensaje && (
                  <p className="text-red-500 text-sm mt-1">{errors.mensaje.message}</p>
                )}
              </div>
              
              {/* Submit */}
              <button
                type="submit"
                disabled={isSubmitting}
                className="btn-primary w-full flex items-center justify-center gap-2 disabled:opacity-70"
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="h-5 w-5 animate-spin" />
                    Enviando...
                  </>
                ) : (
                  <>
                    <Send className="h-5 w-5" />
                    Enviar mensaje
                  </>
                )}
              </button>
              
              {/* Status messages */}
              {submitStatus === 'success' && (
                <div className="flex items-center gap-2 text-green-600 bg-green-50 p-3 rounded-lg">
                  <CheckCircle className="h-5 w-5" />
                  <span>¡Mensaje enviado! Nos pondremos en contacto pronto.</span>
                </div>
              )}
              {submitStatus === 'error' && (
                <div className="flex items-center gap-2 text-red-600 bg-red-50 p-3 rounded-lg">
                  <AlertCircle className="h-5 w-5" />
                  <span>Hubo un error. Por favor intente de nuevo.</span>
                </div>
              )}
              
              {/* Privacy */}
              <p className="text-xs text-gray-500 text-center">
                Al enviar este formulario, acepta nuestra{' '}
                <a href="/privacidad" className="underline hover:text-primary-500">
                  política de privacidad
                </a>
                .
              </p>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
}
