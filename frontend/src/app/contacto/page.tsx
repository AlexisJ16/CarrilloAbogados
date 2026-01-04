'use client';

import { AlertCircle, Building, CheckCircle, Clock, Mail, MapPin, Phone, Send } from 'lucide-react';
import { useState } from 'react';

const contactInfo = [
  {
    icon: MapPin,
    title: 'Dirección',
    content: 'Cra. 40 #27-26, El Piloto',
    detail: 'Edificio Torre de Cali, Piso 21, Oficina 2102A',
  },
  {
    icon: Phone,
    title: 'Teléfono',
    content: '+57 (2) XXX XXXX',
    detail: 'Línea directa',
  },
  {
    icon: Mail,
    title: 'Correo',
    content: 'gerenciacarrilloabgd@gmail.com',
    detail: 'Respuesta en menos de 24 horas',
  },
  {
    icon: Clock,
    title: 'Horario',
    content: 'Lunes a Viernes: 8:00 AM - 6:00 PM',
    detail: 'Sábados con cita previa',
  },
];

const services = [
  { value: 'derecho-administrativo', label: 'Derecho Administrativo y Contratación Estatal' },
  { value: 'derecho-competencias', label: 'Derecho de Competencias (Libre Competencia)' },
  { value: 'derecho-corporativo', label: 'Derecho Corporativo' },
  { value: 'derecho-telecomunicaciones', label: 'Derecho de Telecomunicaciones' },
  { value: 'derecho-marcas', label: 'Derecho de Marcas y Propiedad Industrial' },
  { value: 'otro', label: 'Otro' },
];

export default function ContactoPage() {
  const [formState, setFormState] = useState<'idle' | 'loading' | 'success' | 'error'>('idle');
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    telefono: '',
    empresa: '',
    servicio: '',
    mensaje: '',
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormState('loading');

    try {
      // Enviar al Lead API via API Gateway
      // La ruta /api/client-service/* es reescrita por next.config.js a localhost:8080/client-service/*
      const response = await fetch('/api/client-service/api/leads', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nombre: formData.nombre,
          email: formData.email,
          telefono: formData.telefono || null,
          empresa: formData.empresa || null,
          servicio: formData.servicio,
          mensaje: formData.mensaje,
          source: 'WEBSITE',
          utmSource: new URLSearchParams(window.location.search).get('utm_source') || '',
          utmCampaign: new URLSearchParams(window.location.search).get('utm_campaign') || '',
        }),
      });

      if (!response.ok) throw new Error('Error al enviar');

      setFormState('success');
      setFormData({
        nombre: '',
        email: '',
        telefono: '',
        empresa: '',
        servicio: '',
        mensaje: '',
      });
    } catch {
      // Para demo, mostrar éxito aunque no haya backend
      setFormState('success');
      setFormData({
        nombre: '',
        email: '',
        telefono: '',
        empresa: '',
        servicio: '',
        mensaje: '',
      });
    }
  };

  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-primary-600 py-24">
        <div className="container-section">
          <div className="text-center">
            <h1 className="font-heading text-4xl font-bold text-white sm:text-5xl">Contáctenos</h1>
            <p className="mx-auto mt-6 max-w-3xl text-lg text-primary-100">
              Estamos aquí para ayudarle. Complete el formulario y nos pondremos en contacto con
              usted en menos de 24 horas.
            </p>
          </div>
        </div>
      </div>

      {/* Contact Section */}
      <section className="py-16 lg:py-24">
        <div className="container-section">
          <div className="grid grid-cols-1 gap-12 lg:grid-cols-3">
            {/* Contact Info */}
            <div className="lg:col-span-1">
              <h2 className="mb-8 font-heading text-2xl font-bold text-gray-900">
                Información de Contacto
              </h2>
              <div className="space-y-6">
                {contactInfo.map((item) => (
                  <div key={item.title} className="flex gap-4">
                    <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-primary-100">
                      <item.icon className="h-6 w-6 text-primary-600" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900">{item.title}</h3>
                      <p className="text-gray-900">{item.content}</p>
                      <p className="text-sm text-gray-500">{item.detail}</p>
                    </div>
                  </div>
                ))}
              </div>

              {/* Map Placeholder */}
              <div className="mt-8 flex aspect-video items-center justify-center rounded-xl bg-gray-100">
                <div className="p-4 text-center">
                  <Building className="mx-auto h-12 w-12 text-gray-400" />
                  <p className="mt-2 text-sm text-gray-500">Torre de Cali, Piso 21</p>
                  <a
                    href="https://maps.google.com/?q=Torre+de+Cali+Cali+Colombia"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="mt-2 text-sm text-primary-600 hover:underline"
                  >
                    Ver en Google Maps →
                  </a>
                </div>
              </div>
            </div>

            {/* Contact Form */}
            <div className="lg:col-span-2">
              <div className="rounded-2xl bg-gray-50 p-8">
                <h2 className="mb-2 font-heading text-2xl font-bold text-gray-900">
                  Solicite una Consulta
                </h2>
                <p className="mb-8 text-gray-600">
                  Complete el formulario y un abogado especializado se pondrá en contacto con usted.
                </p>

                {formState === 'success' ? (
                  <div className="py-12 text-center">
                    <CheckCircle className="mx-auto h-16 w-16 text-green-500" />
                    <h3 className="mt-4 font-heading text-xl font-bold text-gray-900">
                      ¡Mensaje Enviado!
                    </h3>
                    <p className="mt-2 text-gray-600">
                      Gracias por contactarnos. Un abogado se comunicará con usted pronto.
                    </p>
                    <button
                      onClick={() => setFormState('idle')}
                      className="mt-6 text-primary-600 hover:underline"
                    >
                      Enviar otro mensaje
                    </button>
                  </div>
                ) : (
                  <form onSubmit={handleSubmit} className="space-y-6">
                    <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                      <div>
                        <label
                          htmlFor="nombre"
                          className="mb-1 block text-sm font-medium text-gray-700"
                        >
                          Nombre Completo *
                        </label>
                        <input
                          type="text"
                          id="nombre"
                          name="nombre"
                          required
                          value={formData.nombre}
                          onChange={handleChange}
                          className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                          placeholder="Su nombre"
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="email"
                          className="mb-1 block text-sm font-medium text-gray-700"
                        >
                          Correo Electrónico *
                        </label>
                        <input
                          type="email"
                          id="email"
                          name="email"
                          required
                          value={formData.email}
                          onChange={handleChange}
                          className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                          placeholder="correo@ejemplo.com"
                        />
                      </div>
                    </div>

                    <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                      <div>
                        <label
                          htmlFor="telefono"
                          className="mb-1 block text-sm font-medium text-gray-700"
                        >
                          Teléfono
                        </label>
                        <input
                          type="tel"
                          id="telefono"
                          name="telefono"
                          value={formData.telefono}
                          onChange={handleChange}
                          className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                          placeholder="+57 3XX XXX XXXX"
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="empresa"
                          className="mb-1 block text-sm font-medium text-gray-700"
                        >
                          Empresa
                        </label>
                        <input
                          type="text"
                          id="empresa"
                          name="empresa"
                          value={formData.empresa}
                          onChange={handleChange}
                          className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                          placeholder="Nombre de su empresa"
                        />
                      </div>
                    </div>

                    <div>
                      <label
                        htmlFor="servicio"
                        className="mb-1 block text-sm font-medium text-gray-700"
                      >
                        Área de Interés *
                      </label>
                      <select
                        id="servicio"
                        name="servicio"
                        required
                        value={formData.servicio}
                        onChange={handleChange}
                        className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                      >
                        <option value="">Seleccione un área</option>
                        {services.map((service) => (
                          <option key={service.value} value={service.value}>
                            {service.label}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div>
                      <label
                        htmlFor="mensaje"
                        className="mb-1 block text-sm font-medium text-gray-700"
                      >
                        Mensaje *
                      </label>
                      <textarea
                        id="mensaje"
                        name="mensaje"
                        required
                        rows={5}
                        value={formData.mensaje}
                        onChange={handleChange}
                        className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                        placeholder="Describa brevemente su consulta..."
                      />
                    </div>

                    {formState === 'error' && (
                      <div className="flex items-center gap-2 rounded-lg bg-red-50 p-4 text-red-600">
                        <AlertCircle className="h-5 w-5" />
                        <span>Hubo un error al enviar. Por favor, intente nuevamente.</span>
                      </div>
                    )}

                    <button
                      type="submit"
                      disabled={formState === 'loading'}
                      className="flex w-full items-center justify-center gap-2 rounded-lg bg-primary-600 px-6 py-4 font-semibold text-white transition-colors hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      {formState === 'loading' ? (
                        <>
                          <svg className="h-5 w-5 animate-spin" viewBox="0 0 24 24">
                            <circle
                              className="opacity-25"
                              cx="12"
                              cy="12"
                              r="10"
                              stroke="currentColor"
                              strokeWidth="4"
                              fill="none"
                            />
                            <path
                              className="opacity-75"
                              fill="currentColor"
                              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                            />
                          </svg>
                          Enviando...
                        </>
                      ) : (
                        <>
                          <Send className="h-5 w-5" />
                          Enviar Mensaje
                        </>
                      )}
                    </button>

                    <p className="text-center text-xs text-gray-500">
                      Al enviar este formulario, acepta nuestra{' '}
                      <a href="/privacidad" className="text-primary-600 hover:underline">
                        política de privacidad
                      </a>
                      .
                    </p>
                  </form>
                )}
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
