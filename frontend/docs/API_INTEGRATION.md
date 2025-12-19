# 🔌 Guía de Integración API - Carrillo Abogados

Esta guía explica cómo integrar el frontend con el backend de microservicios de Carrillo Abogados.

---

## 📋 Índice

1. [Configuración del Entorno](#configuración-del-entorno)
2. [Autenticación](#autenticación)
3. [Lead API - Formulario de Contacto](#lead-api---formulario-de-contacto)
4. [Manejo de Errores](#manejo-de-errores)
5. [CORS](#cors)
6. [Testing](#testing)

---

## 1. Configuración del Entorno

### URLs por Ambiente

```typescript
const API_CONFIG = {
  development: {
    baseUrl: 'http://localhost:8080',
    timeout: 10000,
  },
  staging: {
    baseUrl: 'https://staging-api.carrilloabgd.com',
    timeout: 15000,
  },
  production: {
    baseUrl: 'https://api.carrilloabgd.com',
    timeout: 15000,
  },
};

const env = process.env.NODE_ENV || 'development';
const config = API_CONFIG[env];
```

### Prefijos de Servicio

| Servicio | Prefijo | Descripción |
|----------|---------|-------------|
| client-service | `/client-service` | Gestión de clientes y leads |
| case-service | `/case-service` | Gestión de casos legales |
| document-service | `/document-service` | Documentos y archivos |
| calendar-service | `/calendar-service` | Calendario y citas |

---

## 2. Autenticación

### Estado Actual (MVP)
El sistema actualmente acepta requests sin autenticación para endpoints públicos como el formulario de contacto.

### Futuro (OAuth2)
```typescript
// Cuando OAuth2 esté habilitado
const headers = {
  'Authorization': `Bearer ${accessToken}`,
  'Content-Type': 'application/json',
};
```

### Endpoints Públicos (sin auth)
- `POST /client-service/api/leads` - Captura de leads
- `GET /client-service/actuator/health` - Health check

### Endpoints Protegidos (requieren auth)
- `GET /client-service/api/leads` - Listar leads
- `PATCH /client-service/api/leads/{id}/*` - Modificar leads
- Todos los endpoints de gestión interna

---

## 3. Lead API - Formulario de Contacto

### 3.1 Captura de Lead (Formulario Público)

Este es el endpoint principal para el formulario de contacto del sitio web.

```typescript
// Ejemplo con fetch
async function captureLead(formData: LeadCaptureRequest): Promise<LeadDto> {
  const response = await fetch(`${config.baseUrl}/client-service/api/leads`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(formData),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new ApiError(response.status, error);
  }

  return response.json();
}
```

### 3.2 Validación de Campos

```typescript
// Validación frontend antes de enviar
function validateLeadForm(data: LeadCaptureRequest): ValidationResult {
  const errors: Record<string, string> = {};

  // Nombre requerido
  if (!data.nombre || data.nombre.trim().length === 0) {
    errors.nombre = 'El nombre es requerido';
  } else if (data.nombre.length > 150) {
    errors.nombre = 'El nombre no puede exceder 150 caracteres';
  }

  // Email requerido y válido
  if (!data.email) {
    errors.email = 'El email es requerido';
  } else if (!isValidEmail(data.email)) {
    errors.email = 'Ingrese un email válido';
  } else if (data.email.length > 255) {
    errors.email = 'El email no puede exceder 255 caracteres';
  }

  // Teléfono opcional pero con formato
  if (data.telefono && data.telefono.length > 20) {
    errors.telefono = 'El teléfono no puede exceder 20 caracteres';
  }

  // Empresa opcional
  if (data.empresa && data.empresa.length > 200) {
    errors.empresa = 'La empresa no puede exceder 200 caracteres';
  }

  // Cargo opcional
  if (data.cargo && data.cargo.length > 100) {
    errors.cargo = 'El cargo no puede exceder 100 caracteres';
  }

  // Mensaje opcional
  if (data.mensaje && data.mensaje.length > 2000) {
    errors.mensaje = 'El mensaje no puede exceder 2000 caracteres';
  }

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
}

function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}
```

### 3.3 Formulario React Completo

```tsx
import { useState } from 'react';
import { LeadCaptureRequest, SERVICIOS_DISPONIBLES } from '../types/lead.types';

export function ContactForm() {
  const [formData, setFormData] = useState<LeadCaptureRequest>({
    nombre: '',
    email: '',
    telefono: '',
    empresa: '',
    cargo: '',
    servicio: '',
    mensaje: '',
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitResult, setSubmitResult] = useState<'success' | 'error' | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setSubmitResult(null);

    try {
      const response = await fetch('http://localhost:8080/client-service/api/leads', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setSubmitResult('success');
        // Reset form
        setFormData({
          nombre: '', email: '', telefono: '',
          empresa: '', cargo: '', servicio: '', mensaje: ''
        });
      } else {
        setSubmitResult('error');
      }
    } catch (error) {
      setSubmitResult('error');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {/* ... campos del formulario ... */}
      <button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Enviando...' : 'Enviar'}
      </button>
      
      {submitResult === 'success' && (
        <p>¡Gracias! Nos pondremos en contacto pronto.</p>
      )}
      {submitResult === 'error' && (
        <p>Error al enviar. Por favor intente nuevamente.</p>
      )}
    </form>
  );
}
```

---

## 4. Manejo de Errores

### Códigos HTTP Comunes

| Código | Significado | Acción Recomendada |
|--------|-------------|-------------------|
| 200 | OK | Éxito |
| 201 | Created | Lead capturado exitosamente |
| 400 | Bad Request | Validar campos del formulario |
| 404 | Not Found | Recurso no existe |
| 409 | Conflict | Email ya registrado |
| 500 | Server Error | Reintentar o mostrar error genérico |

### Estructura de Error

```typescript
interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

// Ejemplo de respuesta de error
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El email ya está registrado",
  "path": "/client-service/api/leads"
}
```

### Clase de Error Personalizada

```typescript
export class ApiError extends Error {
  constructor(
    public status: number,
    public body: any
  ) {
    super(body.message || `Error ${status}`);
    this.name = 'ApiError';
  }

  isValidationError(): boolean {
    return this.status === 400;
  }

  isNotFound(): boolean {
    return this.status === 404;
  }

  isConflict(): boolean {
    return this.status === 409;
  }

  isServerError(): boolean {
    return this.status >= 500;
  }
}
```

---

## 5. CORS

El API Gateway está configurado para permitir CORS desde orígenes configurados.

### Desarrollo Local
- El frontend debe correr en `http://localhost:3000` o similar
- El backend acepta requests desde localhost

### Headers CORS Permitidos
- `Content-Type`
- `Authorization`
- `X-Requested-With`

### Si hay errores de CORS
1. Verificar que el API Gateway esté corriendo
2. Verificar que el origen esté en la lista de permitidos
3. En desarrollo, se puede usar un proxy

```javascript
// package.json (Create React App)
{
  "proxy": "http://localhost:8080"
}
```

```javascript
// vite.config.ts (Vite)
export default {
  server: {
    proxy: {
      '/client-service': 'http://localhost:8080'
    }
  }
}
```

---

## 6. Testing

### Verificar Backend Disponible

```typescript
async function checkBackendHealth(): Promise<boolean> {
  try {
    const response = await fetch(
      'http://localhost:8080/client-service/actuator/health',
      { method: 'GET', timeout: 5000 }
    );
    const data = await response.json();
    return data.status === 'UP';
  } catch {
    return false;
  }
}
```

### Mock para Tests

```typescript
// __mocks__/leadApi.ts
export const mockLeadApi = {
  capture: jest.fn().mockResolvedValue({
    leadId: 'mock-uuid',
    nombre: 'Test User',
    email: 'test@example.com',
    leadScore: 30,
    leadCategory: 'COLD',
    leadStatus: 'NUEVO',
    createdAt: new Date().toISOString(),
  }),
};
```

### Cypress E2E Test

```javascript
// cypress/e2e/contact-form.cy.js
describe('Formulario de Contacto', () => {
  it('debe enviar lead exitosamente', () => {
    cy.visit('/contacto');
    
    cy.get('input[name="nombre"]').type('Juan Pérez');
    cy.get('input[name="email"]').type('juan@empresa.com');
    cy.get('select[name="servicio"]').select('Registro de Marcas');
    cy.get('textarea[name="mensaje"]').type('Necesito registrar mi marca...');
    
    cy.get('button[type="submit"]').click();
    
    cy.contains('¡Gracias!').should('be.visible');
  });
});
```

---

## 📚 Recursos Adicionales

- **OpenAPI Spec**: `http://localhost:8200/client-service/v3/api-docs`
- **Swagger UI**: `http://localhost:8200/client-service/swagger-ui.html`
- **Tipos TypeScript**: `frontend/api-contracts/types/lead.types.ts`
