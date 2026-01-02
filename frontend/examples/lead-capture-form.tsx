/**
 * Lead Capture Form - Ejemplo de Integración
 * 
 * Formulario de contacto para el sitio web público de Carrillo Abogados.
 * Este componente captura leads y los envía al backend.
 * 
 * @example
 * ```tsx
 * import { LeadCaptureForm } from './examples/lead-capture-form';
 * 
 * function ContactPage() {
 *   return (
 *     <div>
 *       <h1>Contáctenos</h1>
 *       <LeadCaptureForm />
 *     </div>
 *   );
 * }
 * ```
 */

import { ChangeEvent, FormEvent, useState } from 'react';
import {
    LeadCaptureRequest,
    LeadDto,
    SERVICIOS_DISPONIBLES,
    estimateLeadScore,
    getCategoryFromScore,
} from '../api-contracts/types/lead.types';

// ============================================================
// CONFIGURACIÓN
// ============================================================

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
const LEAD_ENDPOINT = `${API_BASE_URL}/client-service/api/leads`;

// ============================================================
// TIPOS INTERNOS
// ============================================================

interface FormErrors {
  nombre?: string;
  email?: string;
  telefono?: string;
  empresa?: string;
  general?: string;
}

interface FormState {
  isSubmitting: boolean;
  isSuccess: boolean;
  errors: FormErrors;
}

// ============================================================
// COMPONENTE PRINCIPAL
// ============================================================

export function LeadCaptureForm(): JSX.Element {
  // Estado del formulario
  const [formData, setFormData] = useState<LeadCaptureRequest>({
    nombre: '',
    email: '',
    telefono: '',
    empresa: '',
    cargo: '',
    servicio: '',
    mensaje: '',
  });

  // Estado de UI
  const [state, setState] = useState<FormState>({
    isSubmitting: false,
    isSuccess: false,
    errors: {},
  });

  // Score estimado (visual)
  const estimatedScore = estimateLeadScore(formData);
  const estimatedCategory = getCategoryFromScore(estimatedScore);

  // ============================================================
  // HANDLERS
  // ============================================================

  const handleChange = (
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    
    // Limpiar error del campo al editar
    if (state.errors[name as keyof FormErrors]) {
      setState((prev) => ({
        ...prev,
        errors: { ...prev.errors, [name]: undefined },
      }));
    }
  };

  const validateForm = (): boolean => {
    const errors: FormErrors = {};

    // Nombre requerido
    if (!formData.nombre.trim()) {
      errors.nombre = 'El nombre es requerido';
    }

    // Email requerido y válido
    if (!formData.email.trim()) {
      errors.email = 'El email es requerido';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Ingrese un email válido';
    }

    setState((prev) => ({ ...prev, errors }));
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setState((prev) => ({ ...prev, isSubmitting: true, errors: {} }));

    try {
      const response = await fetch(LEAD_ENDPOINT, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}`);
      }

      const createdLead: LeadDto = await response.json();
      console.log('Lead capturado:', createdLead);

      // Éxito - resetear formulario
      setState({ isSubmitting: false, isSuccess: true, errors: {} });
      setFormData({
        nombre: '',
        email: '',
        telefono: '',
        empresa: '',
        cargo: '',
        servicio: '',
        mensaje: '',
      });

      // Analytics (si está configurado)
      if (typeof window !== 'undefined' && (window as any).gtag) {
        (window as any).gtag('event', 'lead_capture', {
          event_category: 'form',
          event_label: formData.servicio || 'general',
          value: estimatedScore,
        });
      }
    } catch (error) {
      console.error('Error capturando lead:', error);
      setState((prev) => ({
        ...prev,
        isSubmitting: false,
        errors: {
          general:
            error instanceof Error
              ? error.message
              : 'Error al enviar el formulario. Por favor intente nuevamente.',
        },
      }));
    }
  };

  // ============================================================
  // RENDER
  // ============================================================

  // Vista de éxito
  if (state.isSuccess) {
    return (
      <div className="lead-form-success" role="alert">
        <div className="success-icon">✓</div>
        <h3>¡Gracias por contactarnos!</h3>
        <p>
          Hemos recibido su mensaje y un miembro de nuestro equipo se pondrá en
          contacto con usted a la brevedad.
        </p>
        <button
          type="button"
          onClick={() => setState((prev) => ({ ...prev, isSuccess: false }))}
          className="btn-secondary"
        >
          Enviar otro mensaje
        </button>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="lead-capture-form" noValidate>
      {/* Error general */}
      {state.errors.general && (
        <div className="form-error-banner" role="alert">
          {state.errors.general}
        </div>
      )}

      {/* Nombre */}
      <div className="form-group">
        <label htmlFor="nombre">
          Nombre completo <span className="required">*</span>
        </label>
        <input
          type="text"
          id="nombre"
          name="nombre"
          value={formData.nombre}
          onChange={handleChange}
          placeholder="Ej: Juan Pérez"
          maxLength={150}
          required
          aria-invalid={!!state.errors.nombre}
          aria-describedby={state.errors.nombre ? 'nombre-error' : undefined}
        />
        {state.errors.nombre && (
          <span id="nombre-error" className="field-error">
            {state.errors.nombre}
          </span>
        )}
      </div>

      {/* Email */}
      <div className="form-group">
        <label htmlFor="email">
          Correo electrónico <span className="required">*</span>
        </label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          placeholder="Ej: juan@empresa.com"
          maxLength={255}
          required
          aria-invalid={!!state.errors.email}
          aria-describedby={state.errors.email ? 'email-error' : undefined}
        />
        {state.errors.email && (
          <span id="email-error" className="field-error">
            {state.errors.email}
          </span>
        )}
      </div>

      {/* Teléfono */}
      <div className="form-group">
        <label htmlFor="telefono">Teléfono</label>
        <input
          type="tel"
          id="telefono"
          name="telefono"
          value={formData.telefono}
          onChange={handleChange}
          placeholder="Ej: +57 300 123 4567"
          maxLength={20}
        />
      </div>

      {/* Empresa */}
      <div className="form-group">
        <label htmlFor="empresa">Empresa</label>
        <input
          type="text"
          id="empresa"
          name="empresa"
          value={formData.empresa}
          onChange={handleChange}
          placeholder="Ej: Mi Empresa SAS"
          maxLength={200}
        />
      </div>

      {/* Cargo */}
      <div className="form-group">
        <label htmlFor="cargo">Cargo</label>
        <input
          type="text"
          id="cargo"
          name="cargo"
          value={formData.cargo}
          onChange={handleChange}
          placeholder="Ej: Gerente General"
          maxLength={100}
        />
      </div>

      {/* Servicio de interés */}
      <div className="form-group">
        <label htmlFor="servicio">Servicio de interés</label>
        <select
          id="servicio"
          name="servicio"
          value={formData.servicio}
          onChange={handleChange}
        >
          <option value="">Seleccione un servicio...</option>
          {SERVICIOS_DISPONIBLES.map((servicio) => (
            <option key={servicio.value} value={servicio.label}>
              {servicio.label}
            </option>
          ))}
        </select>
      </div>

      {/* Mensaje */}
      <div className="form-group">
        <label htmlFor="mensaje">Mensaje</label>
        <textarea
          id="mensaje"
          name="mensaje"
          value={formData.mensaje}
          onChange={handleChange}
          placeholder="Cuéntenos sobre su consulta legal..."
          maxLength={2000}
          rows={4}
        />
        <span className="char-count">
          {formData.mensaje?.length || 0}/2000
        </span>
      </div>

      {/* Indicador de score (opcional - para demo) */}
      {process.env.NODE_ENV === 'development' && (
        <div className="score-indicator">
          <span>Score estimado: {estimatedScore}</span>
          <span className={`category category-${estimatedCategory.toLowerCase()}`}>
            {estimatedCategory}
          </span>
        </div>
      )}

      {/* Submit */}
      <button
        type="submit"
        className="btn-primary"
        disabled={state.isSubmitting}
      >
        {state.isSubmitting ? (
          <>
            <span className="spinner" aria-hidden="true" />
            Enviando...
          </>
        ) : (
          'Enviar mensaje'
        )}
      </button>

      {/* Nota de privacidad */}
      <p className="privacy-note">
        Al enviar este formulario, acepta nuestra{' '}
        <a href="/politica-privacidad" target="_blank" rel="noopener noreferrer">
          Política de Privacidad
        </a>
        .
      </p>
    </form>
  );
}

// ============================================================
// ESTILOS (para referencia - usar CSS/Tailwind en producción)
// ============================================================

export const formStyles = `
.lead-capture-form {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.required {
  color: #dc3545;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #1a365d;
  box-shadow: 0 0 0 3px rgba(26, 54, 93, 0.1);
}

.form-group input[aria-invalid="true"] {
  border-color: #dc3545;
}

.field-error {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  display: block;
}

.form-error-banner {
  background: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.btn-primary {
  width: 100%;
  padding: 1rem;
  background: #1a365d;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #2c5282;
}

.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 0.75rem 1.5rem;
  background: transparent;
  color: #1a365d;
  border: 2px solid #1a365d;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.lead-form-success {
  text-align: center;
  padding: 3rem 2rem;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #48bb78;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 1.5rem;
}

.privacy-note {
  font-size: 0.875rem;
  color: #666;
  text-align: center;
  margin-top: 1rem;
}

.privacy-note a {
  color: #1a365d;
  text-decoration: underline;
}

.char-count {
  font-size: 0.75rem;
  color: #666;
  text-align: right;
  display: block;
  margin-top: 0.25rem;
}

.score-indicator {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  background: #f7fafc;
  border-radius: 4px;
  margin-bottom: 1rem;
  font-size: 0.875rem;
}

.category {
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-weight: 600;
}

.category-hot {
  background: #fed7d7;
  color: #c53030;
}

.category-warm {
  background: #feebc8;
  color: #c05621;
}

.category-cold {
  background: #e2e8f0;
  color: #4a5568;
}

.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid white;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
`;

export default LeadCaptureForm;
