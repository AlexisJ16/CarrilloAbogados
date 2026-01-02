# 🎨 Frontend Agent - Carrillo Abogados Legal Tech

## Propósito

Este agente está especializado en **desarrollo frontend moderno** para la plataforma web de Carrillo Abogados. Conoce las especificaciones OpenAPI del backend, los patrones de diseño, y las mejores prácticas para React/Next.js con TypeScript.

---

## 🎯 Stack Tecnológico

### Sugerido para el Proyecto
- **Framework**: Next.js 14+ (App Router) o Lovable
- **UI Library**: React 18+
- **Styling**: Tailwind CSS 3.4+
- **Language**: TypeScript 5.x (strict mode)
- **State Management**: TanStack Query (React Query) para server state
- **Forms**: React Hook Form + Zod
- **HTTP Client**: fetch nativo o axios

### Herramientas de Desarrollo
- ESLint + Prettier
- TypeScript strict mode
- Husky + lint-staged

---

## 🌐 Integración con Backend

### URLs del Backend

| Entorno | Base URL | Notas |
|---------|----------|-------|
| Local (Docker) | `http://localhost:8080` | API Gateway |
| Local (Directo) | `http://localhost:8200` | client-service |
| Staging | `https://api-staging.carrilloabgd.com` | GKE |
| Production | `https://api.carrilloabgd.com` | GKE Autopilot |

### Endpoints Principales

#### Lead API (client-service)
```typescript
// Captura de lead (formulario contacto público)
POST /client-service/api/leads
Body: { nombre, email, telefono?, empresa?, servicio?, mensaje? }

// Listar leads (panel admin)
GET /client-service/api/leads?page=0&size=20&sort=createdAt,desc

// Leads HOT (dashboard abogados)
GET /client-service/api/leads/hot

// Actualizar scoring (callback n8n)
PATCH /client-service/api/leads/{id}/scoring
Body: { score: number, category: "HOT"|"WARM"|"COLD" }

// Convertir a cliente
POST /client-service/api/leads/{id}/convert
```

#### Client API
```typescript
// CRUD Clientes
GET    /client-service/api/clients
POST   /client-service/api/clients
GET    /client-service/api/clients/{id}
PUT    /client-service/api/clients/{id}
DELETE /client-service/api/clients/{id}
```

#### Case API (case-service)
```typescript
// CRUD Casos Legales
GET    /case-service/api/cases
POST   /case-service/api/cases
GET    /case-service/api/cases/{id}
PUT    /case-service/api/cases/{id}

// Filtros especiales
GET /case-service/api/cases?status=IN_PROGRESS&lawyerId={id}
GET /case-service/api/cases/urgent  // Casos urgentes
```

---

## 📁 Estructura Recomendada (Next.js App Router)

```
frontend/
├── app/
│   ├── (public)/              # Rutas públicas (sin auth)
│   │   ├── page.tsx           # Landing page
│   │   ├── servicios/
│   │   ├── nosotros/
│   │   ├── contacto/
│   │   └── blog/
│   ├── (dashboard)/           # Panel autenticado
│   │   ├── layout.tsx         # Dashboard layout
│   │   ├── leads/
│   │   ├── clients/
│   │   ├── cases/
│   │   └── calendar/
│   ├── api/                   # Route handlers
│   └── layout.tsx             # Root layout
├── components/
│   ├── ui/                    # Componentes base (shadcn/ui)
│   ├── forms/                 # Formularios reutilizables
│   ├── layouts/               # Layouts compartidos
│   └── features/              # Componentes por feature
├── lib/
│   ├── api/                   # Cliente API + hooks
│   │   ├── client.ts          # Configuración fetch/axios
│   │   ├── leads.ts           # API hooks para leads
│   │   ├── clients.ts         # API hooks para clientes
│   │   └── cases.ts           # API hooks para casos
│   ├── utils/                 # Utilidades
│   └── validations/           # Schemas Zod
├── types/                     # TypeScript types
│   ├── api.ts                 # Types de API responses
│   ├── lead.ts
│   ├── client.ts
│   └── case.ts
└── styles/
    └── globals.css            # Tailwind base
```

---

## 🔌 Tipos TypeScript (del Backend)

### Lead
```typescript
interface Lead {
  id: string;
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio?: string;
  mensaje?: string;
  leadScore: number;
  leadCategory: 'HOT' | 'WARM' | 'COLD';
  leadStatus: 'NUEVO' | 'CONTACTADO' | 'NURTURING' | 'CONVERTIDO' | 'DESCARTADO';
  clientId?: string;
  source: string;
  createdAt: string;
  updatedAt: string;
}

interface LeadRequest {
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio?: string;
  mensaje?: string;
  source?: string;
}
```

### Client
```typescript
type ClientType = 'NATURAL' | 'JURIDICA';
type DocumentType = 'CC' | 'NIT' | 'CE' | 'PASAPORTE';
type ClientStatus = 'ACTIVE' | 'INACTIVE';

interface Client {
  id: string;
  clientType: ClientType;
  documentType: DocumentType;
  documentNumber: string;
  firstName?: string;
  lastName?: string;
  companyName?: string;
  legalRepresentative?: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  status: ClientStatus;
  createdAt: string;
  updatedAt: string;
}
```

### LegalCase
```typescript
type PracticeArea = 
  | 'ADMINISTRATIVE_LAW'
  | 'COMPETITION_LAW'
  | 'CORPORATE_LAW'
  | 'TELECOMMUNICATIONS_LAW'
  | 'TRADEMARK_LAW';

type CaseStatus = 'OPEN' | 'IN_PROGRESS' | 'SUSPENDED' | 'CLOSED';

interface LegalCase {
  id: string;
  caseNumber: string;
  title: string;
  description?: string;
  clientId: string;
  practiceArea: PracticeArea;
  status: CaseStatus;
  assignedLawyerId: string;
  filingNumber?: string;
  createdAt: string;
  updatedAt: string;
}
```

---

## 📝 Componentes Esenciales

### 1. Formulario de Contacto (Portal Público)
```tsx
// components/forms/ContactForm.tsx
'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { createLead } from '@/lib/api/leads';

const schema = z.object({
  nombre: z.string().min(2, 'Nombre muy corto').max(100),
  email: z.string().email('Email inválido'),
  telefono: z.string().optional(),
  empresa: z.string().optional(),
  servicio: z.string().optional(),
  mensaje: z.string().max(2000).optional(),
});

type FormData = z.infer<typeof schema>;

export function ContactForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const mutation = useMutation({
    mutationFn: createLead,
    onSuccess: () => {
      // Mostrar mensaje de éxito
      // Redirigir a página de agradecimiento
    },
  });

  return (
    <form onSubmit={handleSubmit(data => mutation.mutate(data))}>
      {/* campos del formulario */}
    </form>
  );
}
```

### 2. API Client
```typescript
// lib/api/client.ts
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export async function apiClient<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API Error: ${response.status}`);
  }

  return response.json();
}

// lib/api/leads.ts
import { apiClient } from './client';
import type { Lead, LeadRequest } from '@/types/lead';

export async function createLead(data: LeadRequest): Promise<Lead> {
  return apiClient<Lead>('/client-service/api/leads', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export async function getLeads(page = 0, size = 20): Promise<Lead[]> {
  return apiClient<Lead[]>(`/client-service/api/leads?page=${page}&size=${size}`);
}

export async function getHotLeads(): Promise<Lead[]> {
  return apiClient<Lead[]>('/client-service/api/leads/hot');
}
```

---

## 🎨 Guía de Estilo

### Colores Corporativos (por definir con cliente)
```css
:root {
  --primary: #1a365d;      /* Azul oscuro profesional */
  --secondary: #2b6cb0;    /* Azul medio */
  --accent: #d69e2e;       /* Dorado (prestigio) */
  --background: #f7fafc;   /* Gris muy claro */
  --foreground: #1a202c;   /* Negro suave */
}
```

### Tipografía
- **Títulos**: Inter, Montserrat, o Playfair Display
- **Cuerpo**: Inter, Source Sans Pro
- **Monospace**: JetBrains Mono (código)

### Responsive Breakpoints (Tailwind)
- `sm`: 640px (móvil grande)
- `md`: 768px (tablet)
- `lg`: 1024px (laptop)
- `xl`: 1280px (desktop)
- `2xl`: 1536px (pantalla grande)

---

## 📋 Checklist de Desarrollo

### Nuevo Componente
1. [ ] Crear archivo en ubicación correcta
2. [ ] Definir props con TypeScript interface
3. [ ] Implementar responsive (mobile-first)
4. [ ] Añadir estados de loading/error
5. [ ] Conectar con API usando TanStack Query
6. [ ] Manejar errores con try/catch
7. [ ] Añadir accesibilidad (aria-*, roles)

### Nueva Página
1. [ ] Crear route en app/
2. [ ] Definir metadata (SEO)
3. [ ] Implementar loading.tsx
4. [ ] Implementar error.tsx
5. [ ] Conectar con datos del backend
6. [ ] Probar en móvil

---

## 🔗 Referencias

- **OpenAPI Specs**: [frontend/api-contracts/](../../frontend/api-contracts/)
- **Ejemplos**: [frontend/examples/](../../frontend/examples/)
- **Documentación API**: [frontend/docs/API_INTEGRATION.md](../../frontend/docs/API_INTEGRATION.md)
