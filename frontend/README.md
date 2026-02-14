# 🎨 Frontend - Carrillo Abogados Legal Tech Platform

**Estado**: ✅ MVP Completo (16 rutas) + Auth UI  
**Framework**: Next.js 16 + React 18 + TypeScript  
**Estilos**: Tailwind CSS 3.4  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa  

---

## 🚀 Quick Start

```bash
cd frontend
npm install
npm run dev
```

Abrir [http://localhost:3000](http://localhost:3000)

---

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── layout.tsx          # Layout principal
│   │   ├── page.tsx            # Landing page
│   │   ├── globals.css         # Estilos Tailwind
│   │   └── providers.tsx       # React Query
│   ├── components/
│   │   ├── home/               # HeroSection, ServicesSection, etc.
│   │   ├── layout/             # Header, Footer
│   │   └── ui/                 # Button, Input, Card
│   ├── lib/
│   │   ├── api/                # Cliente API + hooks
│   │   └── utils.ts            # cn() utility
│   └── types/                  # TypeScript types
├── api-contracts/              # OpenAPI specs + tipos legacy
├── package.json
├── tsconfig.json
├── next.config.js
├── tailwind.config.js
└── postcss.config.js
```

---

## 🔌 Integración con Backend

### URLs del Backend (Docker Compose Local)

| Servicio | URL | Propósito |
|----------|-----|-----------|
| API Gateway | `http://localhost:8080` | Punto de entrada unificado |
| client-service (directo) | `http://localhost:8200` | Acceso directo (desarrollo) |
| Swagger UI | `http://localhost:8200/client-service/swagger-ui.html` | Documentación interactiva |
| OpenAPI JSON | `http://localhost:8200/client-service/v3/api-docs` | Spec para generadores |

### Endpoints Principales de Lead

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/client-service/api/leads` | **Captura de lead** (formulario contacto) |
| `GET` | `/client-service/api/leads` | Listar todos los leads |
| `GET` | `/client-service/api/leads/{id}` | Obtener lead por ID |
| `GET` | `/client-service/api/leads/hot` | Leads HOT pendientes |
| `PATCH` | `/client-service/api/leads/{id}/scoring` | Actualizar score (n8n) |
| `PATCH` | `/client-service/api/leads/{id}/status` | Cambiar estado |
| `POST` | `/client-service/api/leads/{id}/convert` | Convertir a cliente |

---

## 🔌 Integración con Lovable

### 1. Importar el archivo OpenAPI
Usa el archivo `api-contracts/openapi/client-service.json` para generar un cliente API automáticamente.

### 2. Configurar Base URL
```typescript
const API_BASE_URL = process.env.NODE_ENV === 'production' 
  ? 'https://api.carrilloabgd.com'  // Producción (futuro)
  : 'http://localhost:8080';         // Desarrollo local
```

### 3. Headers requeridos
```typescript
const headers = {
  'Content-Type': 'application/json',
  // OAuth2 token cuando esté configurado:
  // 'Authorization': `Bearer ${token}`
};
```

---

## 📝 Uso del Formulario de Contacto

El formulario de contacto del sitio web público debe enviar datos al endpoint de captura de leads:

### Endpoint
```
POST http://localhost:8080/client-service/api/leads
Content-Type: application/json
```

### Payload mínimo
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com"
}
```

### Payload completo (recomendado para mejor scoring)
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "telefono": "+57 300 123 4567",
  "empresa": "Tech Solutions SAS",
  "cargo": "Gerente General",
  "servicio": "Registro de Marcas",
  "mensaje": "Necesito registrar mi marca en Colombia..."
}
```

### Respuesta exitosa (201 Created)
```json
{
  "leadId": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan Pérez",
  "email": "juan@empresa.com",
  "leadScore": 30,
  "leadCategory": "COLD",
  "leadStatus": "NUEVO",
  "createdAt": "2025-01-15T10:30:00Z"
}
```

---

## 🎯 Campos del Formulario de Contacto

| Campo | Obligatorio | Tipo | Max Length | Impacto en Scoring |
|-------|-------------|------|------------|-------------------|
| `nombre` | ✅ Sí | string | 150 | Base |
| `email` | ✅ Sí | email | 255 | +10 si corporativo |
| `telefono` | ❌ No | string | 20 | +10 pts |
| `empresa` | ❌ No | string | 200 | +10 pts |
| `cargo` | ❌ No | string | 100 | +20 si C-Level |
| `servicio` | ❌ No | string | 100 | +20 si "marca"/"litigio" |
| `mensaje` | ❌ No | string | 2000 | +10 si >50 chars |

### Servicios Sugeridos (para dropdown)
- Registro de Marcas
- Derecho Administrativo
- Derecho Corporativo
- Derecho de Competencia
- Derecho de Telecomunicaciones
- Consulta General

---

## ⚡ Eventos NATS (Backend)

Cuando un lead es capturado exitosamente, el backend publica automáticamente un evento NATS:

```
Topic: carrillo.events.lead.capturado
Payload: {
  "leadId": "uuid",
  "email": "email@example.com",
  "nombre": "Nombre",
  "servicio": "Registro de Marcas",
  "source": "WEBSITE",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

Este evento es procesado por **n8n** para:
1. Calcular el lead score
2. Categorizar (HOT/WARM/COLD)
3. Enviar respuesta automática con IA
4. Notificar al abogado si es HOT

---

## 🧪 Testing Local

### Verificar que el backend está corriendo
```bash
curl http://localhost:8080/client-service/actuator/health
```

### Enviar lead de prueba
```bash
curl -X POST http://localhost:8080/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Lovable",
    "email": "test@lovable.dev",
    "servicio": "Registro de Marcas"
  }'
```

---

## 📚 Documentación Adicional

- [Tipos TypeScript](./api-contracts/types/lead.types.ts)
- [Guía de Integración](./docs/API_INTEGRATION.md)
- [Ejemplo de Formulario React](./examples/lead-capture-form.tsx)
- [OpenAPI Spec](./api-contracts/openapi/client-service.json)
