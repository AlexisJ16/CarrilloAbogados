# 🎨 Frontend Agent - Carrillo Abogados Legal Tech

**Última Actualización**: 12 de Enero, 2026 - 10:30 COT  
**Versión**: 3.0  
**Estado**: ✅ Activo  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## Propósito

Este agente es el **experto en desarrollo frontend** para la plataforma web de Carrillo Abogados. Domina Next.js 14+ con App Router, React 18, TypeScript, Tailwind CSS, y las mejores prácticas modernas de desarrollo web. Conoce la integración con el backend Spring Boot y mantiene consistencia con el sistema de diseño.

### Cuándo Invocar Este Agente
- Crear nuevas páginas o componentes React
- Implementar formularios con validación
- Integrar con APIs del backend
- Configurar rutas y layouts de Next.js
- Resolver problemas de styling con Tailwind
- Implementar autenticación en el frontend
- Optimizar SEO y rendimiento

---

## 🎯 Stack Tecnológico (Enero 2026)

### Core Framework
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Next.js** | 14.2+ | Framework React con App Router, SSR, ISR |
| **React** | 18.3+ | UI Library con Server Components |
| **TypeScript** | 5.x | Type safety en modo estricto |

### Styling y UI
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Tailwind CSS** | 3.4+ | Utility-first CSS |
| **shadcn/ui** | Latest | Componentes accesibles (Radix UI) |
| **Lucide React** | Latest | Iconos consistentes |

### Estado y Data Fetching
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **TanStack Query** | 5.x | Server state, caching, mutations |
| **Zustand** | 4.x | Client state simple (si necesario) |
| **React Hook Form** | 7.x | Formularios performantes |
| **Zod** | 3.x | Validación de schemas |

### Herramientas de Desarrollo
| Tecnología | Propósito |
|------------|-----------|
| **ESLint** | Linting de código |
| **Prettier** | Formateo consistente |
| **TypeScript strict** | Máxima type safety |

---

## 🌐 Integración con Backend

### URLs del Backend

| Entorno | Base URL | Variable de Entorno |
|---------|----------|---------------------|
| **Local Docker** | `http://localhost:8080` | `NEXT_PUBLIC_API_URL` |
| **Local Directo** | `http://localhost:8200` | (solo para debug) |
| **Staging** | `https://api-staging.carrilloabgd.com` | Configurar en Vercel/GCP |
| **Production** | `https://api.carrilloabgd.com` | Configurar en Vercel/GCP |

### Servicios y Endpoints Principales

#### Lead API (client-service) - Portal Público
```typescript
// Captura de lead desde formulario de contacto
POST /client-service/api/leads
Body: LeadRequest

// Listar leads (panel admin/abogado)
GET /client-service/api/leads?page=0&size=20&sort=createdAt,desc

// Leads HOT (dashboard prioritario)
GET /client-service/api/leads/hot

// Actualizar estado de lead
PATCH /client-service/api/leads/{id}/status
Body: { status: "CONTACTADO" | "NURTURING" | "CONVERTIDO" | "DESCARTADO" }

// Convertir lead a cliente
POST /client-service/api/leads/{id}/convert
```

#### Auth API (client-service)
```typescript
// Login
POST /client-service/api/auth/login
Body: { email: string, password: string }
Response: { token: string, user: UserResponse }

// Register
POST /client-service/api/auth/register
Body: { email, password, firstName, lastName, ... }

// Refresh token
POST /client-service/api/auth/refresh

// Logout
POST /client-service/api/auth/logout
```

#### Case API (case-service) - Panel Interno
```typescript
// CRUD Casos
GET    /case-service/api/cases
POST   /case-service/api/cases
GET    /case-service/api/cases/{id}
PUT    /case-service/api/cases/{id}

// Filtros
GET /case-service/api/cases?status=IN_PROGRESS&lawyerId={id}
GET /case-service/api/cases?clientId={id}
```

#### Notification API (notification-service)
```typescript
// Listar notificaciones del usuario
GET /notification-service/api/notifications?userId={id}

// Marcar como leída
PATCH /notification-service/api/notifications/{id}/read

// Contador de no leídas
GET /notification-service/api/notifications/unread-count?userId={id}
```

---

## 📁 Estructura del Proyecto Frontend

```
frontend/
├── src/
│   ├── app/                          # Next.js App Router
│   │   ├── (public)/                 # Rutas públicas (sin auth)
│   │   │   ├── page.tsx              # Landing page (/)
│   │   │   ├── nosotros/page.tsx     # Quiénes somos
│   │   │   ├── servicios/page.tsx    # Áreas de práctica
│   │   │   ├── equipo/page.tsx       # Equipo de abogados
│   │   │   ├── contacto/page.tsx     # Formulario de contacto
│   │   │   └── blog/page.tsx         # Artículos legales
│   │   ├── (auth)/                   # Rutas de autenticación
│   │   │   ├── login/page.tsx
│   │   │   └── register/page.tsx
│   │   ├── (dashboard)/              # Panel autenticado
│   │   │   ├── layout.tsx            # Dashboard layout con sidebar
│   │   │   ├── dashboard/page.tsx    # Dashboard principal
│   │   │   ├── leads/page.tsx        # Gestión de leads
│   │   │   ├── cases/                # Gestión de casos
│   │   │   │   ├── page.tsx          # Lista de casos
│   │   │   │   ├── new/page.tsx      # Nuevo caso
│   │   │   │   └── [id]/page.tsx     # Detalle de caso
│   │   │   ├── clients/page.tsx      # Gestión de clientes
│   │   │   ├── notifications/page.tsx# Centro de notificaciones
│   │   │   └── settings/page.tsx     # Configuración
│   │   ├── api/                      # Route handlers (si necesario)
│   │   ├── layout.tsx                # Root layout
│   │   ├── loading.tsx               # Loading global
│   │   ├── error.tsx                 # Error boundary global
│   │   └── not-found.tsx             # 404 page
│   │
│   ├── components/
│   │   ├── ui/                       # shadcn/ui components
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── card.tsx
│   │   │   └── ...
│   │   ├── forms/                    # Formularios reutilizables
│   │   │   ├── ContactForm.tsx
│   │   │   ├── LoginForm.tsx
│   │   │   ├── CaseForm.tsx
│   │   │   └── LeadForm.tsx
│   │   ├── layout/                   # Componentes de layout
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── DashboardHeader.tsx
│   │   │   └── NotificationBell.tsx
│   │   └── features/                 # Componentes por feature
│   │       ├── leads/
│   │       ├── cases/
│   │       └── notifications/
│   │
│   ├── lib/
│   │   ├── api/                      # Cliente API y hooks
│   │   │   ├── client.ts             # Fetch wrapper con auth
│   │   │   ├── leads.ts              # useLeads, useCreateLead, etc.
│   │   │   ├── cases.ts              # useCases, useCase, etc.
│   │   │   ├── auth.ts               # useLogin, useLogout, etc.
│   │   │   └── notifications.ts      # useNotifications
│   │   ├── hooks/                    # Custom hooks
│   │   │   ├── useAuth.ts
│   │   │   └── useToast.ts
│   │   ├── utils/                    # Utilidades
│   │   │   ├── cn.ts                 # clsx + tailwind-merge
│   │   │   ├── formatters.ts         # Formateo de fechas, moneda
│   │   │   └── validators.ts
│   │   └── validations/              # Schemas Zod
│   │       ├── lead.schema.ts
│   │       ├── case.schema.ts
│   │       └── auth.schema.ts
│   │
│   ├── types/                        # TypeScript types
│   │   ├── api.ts                    # Types genéricos de API
│   │   ├── lead.ts
│   │   ├── case.ts
│   │   ├── client.ts
│   │   ├── user.ts
│   │   └── notification.ts
│   │
│   └── styles/
│       └── globals.css               # Tailwind base + custom CSS
│
├── public/
│   ├── images/
│   ├── icons/
│   └── favicon.ico
│
├── next.config.js
├── tailwind.config.js
├── tsconfig.json
└── package.json
```

---

## 🔌 Types de TypeScript (Sincronizados con Backend)

### Lead
```typescript
// types/lead.ts

export type LeadCategory = 'HOT' | 'WARM' | 'COLD';
export type LeadStatus = 'NUEVO' | 'CONTACTADO' | 'NURTURING' | 'CONVERTIDO' | 'DESCARTADO';

export interface Lead {
  id: string;
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio?: string;
  mensaje?: string;
  leadScore: number;
  leadCategory: LeadCategory;
  leadStatus: LeadStatus;
  clientId?: string;
  source: string;
  createdAt: string;  // ISO 8601
  updatedAt: string;
}

export interface LeadRequest {
  nombre: string;
  email: string;
  telefono?: string;
  empresa?: string;
  cargo?: string;
  servicio?: string;
  mensaje?: string;
  source?: string;
}

export interface LeadResponse extends Lead {}
```

### Case (Caso Legal)
```typescript
// types/case.ts

export type PracticeArea = 
  | 'ADMINISTRATIVE_LAW'     // Derecho Administrativo
  | 'COMPETITION_LAW'        // Derecho de Competencias
  | 'CORPORATE_LAW'          // Derecho Corporativo
  | 'TELECOMMUNICATIONS_LAW' // Telecomunicaciones
  | 'TRADEMARK_LAW';         // Marcas

export type CaseStatus = 'OPEN' | 'IN_PROGRESS' | 'SUSPENDED' | 'CLOSED';

export interface LegalCase {
  id: string;
  caseNumber: string;
  title: string;
  description?: string;
  clientId: string;
  clientName?: string;  // Denormalizado para UI
  practiceArea: PracticeArea;
  status: CaseStatus;
  assignedLawyerId: string;
  assignedLawyerName?: string;
  filingNumber?: string;  // Número de radicado
  opposingParty?: string;
  createdAt: string;
  updatedAt: string;
}

// Labels en español para UI
export const practiceAreaLabels: Record<PracticeArea, string> = {
  ADMINISTRATIVE_LAW: 'Derecho Administrativo',
  COMPETITION_LAW: 'Derecho de Competencias',
  CORPORATE_LAW: 'Derecho Corporativo',
  TELECOMMUNICATIONS_LAW: 'Telecomunicaciones',
  TRADEMARK_LAW: 'Propiedad Industrial',
};

export const caseStatusLabels: Record<CaseStatus, string> = {
  OPEN: 'Abierto',
  IN_PROGRESS: 'En Progreso',
  SUSPENDED: 'Suspendido',
  CLOSED: 'Cerrado',
};
```

### User y Auth
```typescript
// types/user.ts

export type UserRole = 'VISITOR' | 'CLIENT' | 'LAWYER' | 'ADMIN';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  avatarUrl?: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken?: string;
  user: User;
  expiresAt: string;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
```

### Notification
```typescript
// types/notification.ts

export type NotificationType = 
  | 'LEAD_NEW'
  | 'LEAD_HOT'
  | 'CASE_CREATED'
  | 'CASE_STATUS_CHANGED'
  | 'APPOINTMENT_REMINDER'
  | 'DOCUMENT_UPLOADED'
  | 'DEADLINE_APPROACHING'
  | 'SYSTEM';

export interface Notification {
  id: string;
  userId: string;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  actionUrl?: string;
  relatedEntityId?: string;
  createdAt: string;
}
```

---

## 🔧 Patrones de Código

### 1. API Client con Autenticación

```typescript
// lib/api/client.ts

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export class ApiError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    public data?: unknown
  ) {
    super(`API Error: ${status} ${statusText}`);
    this.name = 'ApiError';
  }
}

export async function apiClient<T>(
  endpoint: string,
  options?: RequestInit & { token?: string }
): Promise<T> {
  const { token, ...fetchOptions } = options || {};
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...fetchOptions.headers,
  };
  
  // Añadir token si existe
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  } else {
    // Intentar obtener del localStorage (solo en cliente)
    if (typeof window !== 'undefined') {
      const storedToken = localStorage.getItem('auth_token');
      if (storedToken) {
        headers['Authorization'] = `Bearer ${storedToken}`;
      }
    }
  }
  
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...fetchOptions,
    headers,
  });
  
  if (!response.ok) {
    const data = await response.json().catch(() => null);
    throw new ApiError(response.status, response.statusText, data);
  }
  
  // Handle 204 No Content
  if (response.status === 204) {
    return {} as T;
  }
  
  return response.json();
}
```

### 2. React Query Hooks

```typescript
// lib/api/leads.ts

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';
import type { Lead, LeadRequest, LeadResponse } from '@/types/lead';

// Keys
export const leadKeys = {
  all: ['leads'] as const,
  lists: () => [...leadKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...leadKeys.lists(), filters] as const,
  details: () => [...leadKeys.all, 'detail'] as const,
  detail: (id: string) => [...leadKeys.details(), id] as const,
  hot: () => [...leadKeys.all, 'hot'] as const,
};

// Queries
export function useLeads(page = 0, size = 20) {
  return useQuery({
    queryKey: leadKeys.list({ page, size }),
    queryFn: () => apiClient<Lead[]>(
      `/client-service/api/leads?page=${page}&size=${size}&sort=createdAt,desc`
    ),
  });
}

export function useLead(id: string) {
  return useQuery({
    queryKey: leadKeys.detail(id),
    queryFn: () => apiClient<Lead>(`/client-service/api/leads/${id}`),
    enabled: !!id,
  });
}

export function useHotLeads() {
  return useQuery({
    queryKey: leadKeys.hot(),
    queryFn: () => apiClient<Lead[]>('/client-service/api/leads/hot'),
    refetchInterval: 30000, // Refrescar cada 30s
  });
}

// Mutations
export function useCreateLead() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: LeadRequest) =>
      apiClient<LeadResponse>('/client-service/api/leads', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: leadKeys.all });
    },
  });
}

export function useUpdateLeadStatus() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: string }) =>
      apiClient<Lead>(`/client-service/api/leads/${id}/status`, {
        method: 'PATCH',
        body: JSON.stringify({ status }),
      }),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: leadKeys.detail(id) });
      queryClient.invalidateQueries({ queryKey: leadKeys.lists() });
    },
  });
}
```

### 3. Formulario con React Hook Form + Zod

```typescript
// components/forms/ContactForm.tsx
'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreateLead } from '@/lib/api/leads';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { useToast } from '@/lib/hooks/useToast';

const contactSchema = z.object({
  nombre: z
    .string()
    .min(2, 'El nombre debe tener al menos 2 caracteres')
    .max(100, 'El nombre no puede exceder 100 caracteres'),
  email: z
    .string()
    .email('Por favor ingrese un email válido'),
  telefono: z
    .string()
    .regex(/^[+]?[0-9\s-]{7,20}$/, 'Formato de teléfono inválido')
    .optional()
    .or(z.literal('')),
  empresa: z
    .string()
    .max(200)
    .optional(),
  servicio: z
    .string()
    .optional(),
  mensaje: z
    .string()
    .max(2000, 'El mensaje no puede exceder 2000 caracteres')
    .optional(),
});

type ContactFormData = z.infer<typeof contactSchema>;

const servicios = [
  { value: 'ADMINISTRATIVE_LAW', label: 'Derecho Administrativo' },
  { value: 'COMPETITION_LAW', label: 'Derecho de Competencias' },
  { value: 'CORPORATE_LAW', label: 'Derecho Corporativo' },
  { value: 'TELECOMMUNICATIONS_LAW', label: 'Telecomunicaciones' },
  { value: 'TRADEMARK_LAW', label: 'Propiedad Industrial' },
];

export function ContactForm() {
  const { toast } = useToast();
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
      await createLead.mutateAsync({
        ...data,
        source: 'WEBSITE',
      });
      
      toast({
        title: '¡Mensaje enviado!',
        description: 'Nos pondremos en contacto pronto.',
        variant: 'success',
      });
      
      reset();
    } catch (error) {
      toast({
        title: 'Error al enviar',
        description: 'Por favor intente nuevamente.',
        variant: 'destructive',
      });
    }
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="nombre" className="block text-sm font-medium mb-1">
            Nombre completo *
          </label>
          <Input
            id="nombre"
            {...register('nombre')}
            aria-invalid={!!errors.nombre}
          />
          {errors.nombre && (
            <p className="mt-1 text-sm text-red-600">{errors.nombre.message}</p>
          )}
        </div>
        
        <div>
          <label htmlFor="email" className="block text-sm font-medium mb-1">
            Email *
          </label>
          <Input
            id="email"
            type="email"
            {...register('email')}
            aria-invalid={!!errors.email}
          />
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
          )}
        </div>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="telefono" className="block text-sm font-medium mb-1">
            Teléfono
          </label>
          <Input
            id="telefono"
            type="tel"
            {...register('telefono')}
          />
        </div>
        
        <div>
          <label htmlFor="empresa" className="block text-sm font-medium mb-1">
            Empresa
          </label>
          <Input
            id="empresa"
            {...register('empresa')}
          />
        </div>
      </div>
      
      <div>
        <label htmlFor="servicio" className="block text-sm font-medium mb-1">
          Área de interés
        </label>
        <select
          id="servicio"
          {...register('servicio')}
          className="w-full rounded-md border border-gray-300 p-2"
        >
          <option value="">Seleccione un área</option>
          {servicios.map((s) => (
            <option key={s.value} value={s.value}>
              {s.label}
            </option>
          ))}
        </select>
      </div>
      
      <div>
        <label htmlFor="mensaje" className="block text-sm font-medium mb-1">
          Mensaje
        </label>
        <Textarea
          id="mensaje"
          rows={4}
          {...register('mensaje')}
        />
      </div>
      
      <Button
        type="submit"
        disabled={isSubmitting || createLead.isPending}
        className="w-full"
      >
        {isSubmitting ? 'Enviando...' : 'Enviar mensaje'}
      </Button>
    </form>
  );
}
```

### 4. Componente con Loading y Error States

```typescript
// components/features/leads/LeadList.tsx
'use client';

import { useLeads } from '@/lib/api/leads';
import { LeadCard } from './LeadCard';
import { Skeleton } from '@/components/ui/skeleton';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';

export function LeadList() {
  const { data: leads, isLoading, isError, error } = useLeads();
  
  if (isLoading) {
    return (
      <div className="space-y-4">
        {[...Array(5)].map((_, i) => (
          <Skeleton key={i} className="h-24 w-full" />
        ))}
      </div>
    );
  }
  
  if (isError) {
    return (
      <Alert variant="destructive">
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>
          Error al cargar leads: {error instanceof Error ? error.message : 'Error desconocido'}
        </AlertDescription>
      </Alert>
    );
  }
  
  if (!leads || leads.length === 0) {
    return (
      <div className="text-center py-12 text-gray-500">
        No hay leads registrados
      </div>
    );
  }
  
  return (
    <div className="space-y-4">
      {leads.map((lead) => (
        <LeadCard key={lead.id} lead={lead} />
      ))}
    </div>
  );
}
```

---

## 🎨 Sistema de Diseño

### Colores Corporativos

```css
/* styles/globals.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  :root {
    /* Colores principales - Azul profesional legal */
    --primary: 222.2 47.4% 11.2%;         /* Azul muy oscuro */
    --primary-foreground: 210 40% 98%;
    
    --secondary: 217.2 32.6% 17.5%;       /* Azul oscuro */
    --secondary-foreground: 210 40% 98%;
    
    --accent: 45 93% 47%;                 /* Dorado - prestigio */
    --accent-foreground: 222.2 47.4% 11.2%;
    
    /* Fondos */
    --background: 0 0% 100%;
    --foreground: 222.2 84% 4.9%;
    
    /* Cards y superficies */
    --card: 0 0% 100%;
    --card-foreground: 222.2 84% 4.9%;
    
    /* Bordes */
    --border: 214.3 31.8% 91.4%;
    --input: 214.3 31.8% 91.4%;
    
    /* Estados */
    --destructive: 0 84.2% 60.2%;
    --destructive-foreground: 210 40% 98%;
    
    --success: 142.1 76.2% 36.3%;
    --success-foreground: 210 40% 98%;
    
    --warning: 38 92% 50%;
    --warning-foreground: 222.2 47.4% 11.2%;
    
    /* Muted */
    --muted: 210 40% 96%;
    --muted-foreground: 215.4 16.3% 46.9%;
    
    --radius: 0.5rem;
  }
}
```

### Tailwind Config Extendido

```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ['class'],
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  theme: {
    container: {
      center: true,
      padding: '2rem',
      screens: {
        '2xl': '1400px',
      },
    },
    extend: {
      colors: {
        border: 'hsl(var(--border))',
        input: 'hsl(var(--input))',
        background: 'hsl(var(--background))',
        foreground: 'hsl(var(--foreground))',
        primary: {
          DEFAULT: 'hsl(var(--primary))',
          foreground: 'hsl(var(--primary-foreground))',
        },
        secondary: {
          DEFAULT: 'hsl(var(--secondary))',
          foreground: 'hsl(var(--secondary-foreground))',
        },
        accent: {
          DEFAULT: 'hsl(var(--accent))',
          foreground: 'hsl(var(--accent-foreground))',
        },
        destructive: {
          DEFAULT: 'hsl(var(--destructive))',
          foreground: 'hsl(var(--destructive-foreground))',
        },
        success: {
          DEFAULT: 'hsl(var(--success))',
          foreground: 'hsl(var(--success-foreground))',
        },
        warning: {
          DEFAULT: 'hsl(var(--warning))',
          foreground: 'hsl(var(--warning-foreground))',
        },
        muted: {
          DEFAULT: 'hsl(var(--muted))',
          foreground: 'hsl(var(--muted-foreground))',
        },
        card: {
          DEFAULT: 'hsl(var(--card))',
          foreground: 'hsl(var(--card-foreground))',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        heading: ['Montserrat', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};
```

---

## 🔧 Comandos de Desarrollo

```powershell
# Instalar dependencias
cd frontend && npm install

# Desarrollo
npm run dev

# Build producción
npm run build

# Lint
npm run lint

# Type check
npm run type-check

# Formatear
npm run format
```

---

## 📋 Checklist: Nuevo Componente

### 1. Definición
- [ ] Definir props con TypeScript interface
- [ ] Identificar si es Server o Client Component
- [ ] Determinar estado local vs server state

### 2. Implementación
- [ ] Crear archivo en ubicación correcta
- [ ] Implementar responsive (mobile-first)
- [ ] Añadir estados: loading, error, empty
- [ ] Usar componentes de shadcn/ui

### 3. Accesibilidad
- [ ] Labels en formularios (`htmlFor`)
- [ ] `aria-*` attributes donde aplique
- [ ] Navegación por teclado
- [ ] Contraste de colores

### 4. Testing
- [ ] Probar en móvil (responsive)
- [ ] Verificar con backend real

---

## 📋 Checklist: Nueva Página

### 1. Ruta
- [ ] Crear en directorio correcto (`(public)/`, `(auth)/`, `(dashboard)/`)
- [ ] Definir `page.tsx`
- [ ] Crear `loading.tsx` si hay data fetching
- [ ] Crear `error.tsx` para manejo de errores

### 2. SEO
- [ ] Exportar `metadata` con title y description
- [ ] Configurar OpenGraph si es página pública
- [ ] Añadir a sitemap si es pública

### 3. Layout
- [ ] Verificar que usa el layout correcto
- [ ] Breadcrumbs si aplica

---

## 🔗 Referencias del Proyecto

| Documento | Ubicación | Propósito |
|-----------|-----------|-----------|
| API Contracts | [frontend/api-contracts/](../../frontend/api-contracts/) | OpenAPI specs |
| Ejemplos | [frontend/examples/](../../frontend/examples/) | Código de ejemplo |
| Docs Frontend | [frontend/docs/](../../frontend/docs/) | Guías específicas |

---

*Agente actualizado: 12 de Enero 2026, 10:30 COT*  
*Proyecto: Carrillo Abogados Legal Tech Platform*
