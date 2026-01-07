# 🔗 ARQUITECTURA DE DATOS: n8n ↔ Plataforma Web

**Fecha**: 6 de Enero, 2026  
**Propósito**: Definir el camino técnico óptimo para conectar SUB-D Nurturing y otros workflows n8n con los datos de clientes/leads

---

## 📊 ANÁLISIS DE OPCIONES

### Las 3 Opciones Disponibles

| Opción | Fuente | Protocolo | Complejidad | Latencia |
|--------|--------|-----------|-------------|----------|
| **A** | Dashboard (Frontend API) | REST via API Gateway | Media | Alta (~500ms) |
| **B** | PostgreSQL Directo | TCP/SQL | Alta | Baja (~50ms) |
| **C** | Firestore (n8n Nativo) | HTTP/Firebase API | Baja | Media (~200ms) |

---

## 🏗️ ARQUITECTURA ACTUAL

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                        FLUJO ACTUAL DE LEADS                                 │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  PORTAL WEB                                                                  │
│  ┌─────────────┐                                                             │
│  │ Formulario  │                                                             │
│  │ /contacto   │                                                             │
│  └──────┬──────┘                                                             │
│         │ POST /client-service/api/leads                                     │
│         ▼                                                                    │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                     client-service (Puerto 8200)                      │   │
│  │                                                                       │   │
│  │   ┌────────────────┐      ┌─────────────────────────────────────┐    │   │
│  │   │  Lead Entity   │─────►│  PostgreSQL (schema: clients)       │    │   │
│  │   │  (Java/JPA)    │      │  Tabla: leads (212 campos)          │    │   │
│  │   └───────┬────────┘      │  ✅ FUENTE DE VERDAD                 │    │   │
│  │           │               └─────────────────────────────────────┘    │   │
│  │           │ Publica evento NATS                                      │   │
│  │           ▼                                                           │   │
│  │   ┌─────────────────┐                                                │   │
│  │   │ "lead.capturado"│                                                │   │
│  │   └───────┬─────────┘                                                │   │
│  └───────────┼──────────────────────────────────────────────────────────┘   │
│              │                                                               │
│              ▼                                                               │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                n8n-integration-service (Puerto 8800)                  │   │
│  │                                                                       │   │
│  │   ┌────────────┐     ┌──────────────────────────────────────────┐    │   │
│  │   │ NATS       │────►│  Webhook a n8n Cloud                     │    │   │
│  │   │ Listener   │     │  POST /webhook/lead-events               │    │   │
│  │   └────────────┘     └────────────────┬─────────────────────────┘    │   │
│  └───────────────────────────────────────┼──────────────────────────────┘   │
│                                          │                                   │
│                                          ▼                                   │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        n8n Cloud                                      │   │
│  │                                                                       │   │
│  │   ┌──────────────────────────────────────────────────────────────┐   │   │
│  │   │ SUB-A: Lead Intake (ID: RHj1TAqBazxNFriJ)                    │   │   │
│  │   │                                                              │   │   │
│  │   │  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐  │   │   │
│  │   │  │ Validar  │──►│ Scoring  │──►│ Guardar  │──►│ Enviar   │  │   │   │
│  │   │  │ Datos    │   │ Gemini   │   │ Firestore│   │ Gmail    │  │   │   │
│  │   │  └──────────┘   └──────────┘   └──────────┘   └──────────┘  │   │   │
│  │   │                                      │                       │   │   │
│  │   │                                      ▼                       │   │   │
│  │   │                        ┌──────────────────────────────┐     │   │   │
│  │   │                        │   Firestore (GCP)            │     │   │   │
│  │   │                        │   Collection: leads          │     │   │   │
│  │   │                        │   ⚠️ COPIA para n8n          │     │   │   │
│  │   │                        └──────────────────────────────┘     │   │   │
│  │   └──────────────────────────────────────────────────────────────┘   │   │
│  │                                                                       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔍 ANÁLISIS DETALLADO DE CADA OPCIÓN

### OPCIÓN A: Conectar via Dashboard/API Gateway

**Flujo**:
```
n8n SUB-D ──HTTP──► API Gateway ──► client-service API ──► PostgreSQL
```

**Ventajas**:
- ✅ Usa infraestructura existente
- ✅ Autenticación y autorización ya implementada
- ✅ Validaciones de negocio aplicadas
- ✅ Logs y auditoría consistentes

**Desventajas**:
- ❌ Latencia alta (~500ms por request)
- ❌ Rate limiting puede afectar batch processing
- ❌ Requiere token de servicio (API Key o JWT)
- ❌ Doble serialización/deserialización

**Endpoints disponibles**:
```
GET  /client-service/api/leads/status/NURTURING/paged
GET  /client-service/api/leads/inactive?days=30
PUT  /client-service/api/leads/{id}/engagement
PUT  /client-service/api/leads/{id}/status
```

**Ideal para**: Operaciones puntuales, integraciones ligeras

---

### OPCIÓN B: Conectar Directo a PostgreSQL

**Flujo**:
```
n8n SUB-D ──SQL──► PostgreSQL (schema: clients)
```

**Ventajas**:
- ✅ Latencia mínima (~50ms)
- ✅ Queries complejos con JOINs
- ✅ Transacciones atómicas
- ✅ Sin dependencia de servicios intermedios

**Desventajas**:
- ❌ **EXPONE CREDENCIALES BD** a n8n Cloud (problema de seguridad)
- ❌ Bypass de validaciones de negocio
- ❌ No genera eventos NATS (pierde sincronización)
- ❌ Requiere IP pública para PostgreSQL en producción
- ❌ Acoplamiento directo al schema (rompe encapsulamiento)

**Query ejemplo para Nurturing**:
```sql
SELECT lead_id, nombre, email, empresa, servicio, 
       emails_sent, created_at, last_contact
FROM clients.leads 
WHERE lead_status IN ('NUEVO', 'NURTURING')
  AND emails_sent < 12
  AND (last_contact IS NULL 
       OR last_contact < NOW() - INTERVAL '3 days')
ORDER BY created_at ASC
LIMIT 10;
```

**Ideal para**: ❌ **NO RECOMENDADO** para producción

---

### OPCIÓN C: Usar Firestore (Ya Implementado en SUB-A)

**Flujo**:
```
SUB-A guarda ──► Firestore ──► SUB-D lee
                    ▲
                    │
n8n-integration-service sincroniza callbacks ──► PostgreSQL
```

**Ventajas**:
- ✅ **YA IMPLEMENTADO** en SUB-A (collection: `leads`)
- ✅ Credencial Firestore ya configurada en n8n Cloud
- ✅ Latencia aceptable (~200ms)
- ✅ Nodo nativo de n8n (sin HTTP custom)
- ✅ Queries simples con índices automáticos
- ✅ Tiempo real (opcional con listeners)
- ✅ Sin exposición de BD principal

**Desventajas**:
- ⚠️ Requiere **sincronización bidireccional** con PostgreSQL
- ⚠️ Posible inconsistencia temporal (eventual consistency)
- ⚠️ Datos duplicados (Firestore + PostgreSQL)

**Estructura actual en Firestore**:
```json
{
  "lead_id": "uuid",
  "nombre": "string",
  "email": "string",
  "empresa": "string",
  "score": 75,
  "categoria": "HOT",
  "status": "nurturing",
  "emails_sent": 3,
  "last_contact": "2026-01-05T10:30:00Z",
  "processed_at": "2026-01-05T10:30:00Z"
}
```

**Ideal para**: ✅ **RECOMENDADO** para n8n workflows

---

## ✅ RECOMENDACIÓN: ARQUITECTURA HÍBRIDA (Opción C + Callbacks)

### Diseño Propuesto

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                   ARQUITECTURA HÍBRIDA RECOMENDADA                           │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   FUENTE DE VERDAD: PostgreSQL (client-service)                              │
│   COPIA OPERACIONAL: Firestore (para n8n)                                    │
│   SINCRONIZACIÓN: Bidireccional via callbacks                                │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                        FLUJO DE ESCRITURA                                │ │
│  │                                                                          │ │
│  │  [Portal Web] ──► [client-service] ──► [PostgreSQL]                     │ │
│  │                          │                                               │ │
│  │                          │ evento NATS: lead.capturado                   │ │
│  │                          ▼                                               │ │
│  │               [n8n-integration-service]                                  │ │
│  │                          │                                               │ │
│  │                          │ webhook                                       │ │
│  │                          ▼                                               │ │
│  │               [n8n SUB-A] ──► [Firestore] ✅ Copia creada                │ │
│  │                                                                          │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                        FLUJO DE LECTURA (SUB-D)                          │ │
│  │                                                                          │ │
│  │  [SUB-D Nurturing] ──► [Firestore Query] ──► leads para nurturing       │ │
│  │         │                                                                │ │
│  │         │ ┌─────────────────────────────────────────────────────────┐   │ │
│  │         │ │ Query Firestore:                                        │   │ │
│  │         │ │   WHERE status IN ["nuevo", "nurturing"]                │   │ │
│  │         │ │     AND emails_sent < 12                                │   │ │
│  │         │ │     AND last_contact < (NOW - X días)                   │   │ │
│  │         │ └─────────────────────────────────────────────────────────┘   │ │
│  │         │                                                                │ │
│  │         ▼                                                                │ │
│  │  [Gemini: Personalizar email]                                           │ │
│  │         │                                                                │ │
│  │         ▼                                                                │ │
│  │  [Mailersend: Enviar email]                                             │ │
│  │         │                                                                │ │
│  │         ├───────────────────────────────────────────┐                   │ │
│  │         ▼                                           ▼                   │ │
│  │  [Firestore: Actualizar]              [Callback: /webhook/nurturing-sent]│ │
│  │   - emails_sent++                             │                          │ │
│  │   - last_contact = NOW                        ▼                          │ │
│  │                                      [n8n-integration-service]           │ │
│  │                                               │                          │ │
│  │                                               ▼                          │ │
│  │                                      [client-service API]                │ │
│  │                                               │                          │ │
│  │                                               ▼                          │ │
│  │                                      [PostgreSQL: Actualizar]            │ │
│  │                                       - emails_sent++                    │ │
│  │                                       - last_contact = NOW               │ │
│  │                                       - status = NURTURING               │ │
│  │                                                                          │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠️ IMPLEMENTACIÓN REQUERIDA

### 1. Campos Necesarios en Firestore

Ampliar el documento de lead para soportar nurturing:

```json
{
  // Existentes (de SUB-A)
  "lead_id": "uuid-string",
  "nombre": "string",
  "email": "string",
  "empresa": "string",
  "servicio": "string",
  "score": 75,
  "categoria": "HOT|WARM|COLD",
  "processed_at": "timestamp",
  
  // Agregar para nurturing
  "status": "nuevo|nurturing|mql|sql|convertido|churned",
  "emails_sent": 0,
  "emails_opened": 0,
  "emails_clicked": 0,
  "last_contact": "timestamp|null",
  "last_engagement": "timestamp|null",
  "nurturing_position": 1,  // Posición en secuencia (1-12)
  "next_email_date": "timestamp",  // Cuándo enviar siguiente
  "created_at": "timestamp"
}
```

### 2. Modificar SUB-A para Guardar Campos Extra

Actualizar nodo "2. Guardar en Firestore" para incluir:
- `status: "nuevo"`
- `emails_sent: 0`
- `nurturing_position: 1`
- `next_email_date: (now + 3 días)`
- `created_at: now`

### 3. Nuevo Webhook en n8n-integration-service

**Endpoint**: `POST /webhook/nurturing-sent`

**Payload**:
```json
{
  "lead_id": "uuid",
  "email_position": 3,
  "emails_sent": 3,
  "sent_at": "2026-01-06T10:00:00Z"
}
```

**Acción**: Actualizar lead en PostgreSQL via client-service

### 4. Query para SUB-D en Firestore

```
Collection: leads
Where: 
  - status IN ["nuevo", "nurturing"]
  - emails_sent < 12
  - next_email_date <= NOW
Order by: next_email_date ASC
Limit: 10 (batch processing)
```

---

## 📅 PLAN DE EJECUCIÓN

### Fase 1: Preparar Firestore (2 horas)

| Tarea | Responsable |
|-------|-------------|
| Actualizar SUB-A para guardar campos extra | Marketing Dev |
| Crear índice en Firestore para queries | Marketing Dev |
| Testing: Verificar estructura de docs | Marketing Dev |

### Fase 2: Crear SUB-D (4 horas)

| Tarea | Responsable |
|-------|-------------|
| Diseño de flujo SUB-D | Arquitecto n8n |
| Implementación nodos | Marketing Dev |
| Configurar Mailersend | Marketing Dev |
| Testing E2E con lead prueba | QA |

### Fase 3: Sincronización Backend (2 horas)

| Tarea | Responsable |
|-------|-------------|
| Crear webhook `/nurturing-sent` | Backend Dev |
| Implementar actualización PostgreSQL | Backend Dev |
| Testing sincronización bidireccional | QA |

---

## ⚠️ CONSIDERACIONES DE CONSISTENCIA

### Eventual Consistency

```
POSTGRESQL ◄──────────────────────────────────────► FIRESTORE
    │                                                    │
    │  [Evento NATS]     [Callback HTTP]                │
    │       │                  │                        │
    └───────┴──────────────────┴────────────────────────┘
              Latencia típica: 500ms - 2s
```

**Mitigaciones**:
1. SUB-D lee de Firestore (datos recientes de n8n)
2. Dashboard lee de PostgreSQL (fuente de verdad para UI)
3. Callbacks actualizan PostgreSQL para métricas dashboard
4. Si callback falla, reintentar 3 veces con backoff

### Conflictos de Escritura

| Escenario | Resolución |
|-----------|------------|
| Lead editado en dashboard MIENTRAS n8n procesa | PostgreSQL gana (last-write-wins + timestamp) |
| Callback falla | Retry queue en n8n + log para reconciliación manual |
| Lead borrado en dashboard | n8n ignora en siguiente batch (doc no existe) |

---

## 📊 COMPARATIVA FINAL

| Criterio | Opción A (API) | Opción B (SQL) | Opción C (Firestore) |
|----------|----------------|----------------|----------------------|
| Seguridad | ✅ Alta | ❌ Baja | ✅ Alta |
| Latencia | ⚠️ 500ms | ✅ 50ms | ⚠️ 200ms |
| Complejidad | ⚠️ Media | ❌ Alta | ✅ Baja |
| Ya implementado | ⚠️ Parcial | ❌ No | ✅ Sí |
| Mantenimiento | ⚠️ Medio | ❌ Alto | ✅ Bajo |
| Consistencia | ✅ Inmediata | ✅ Inmediata | ⚠️ Eventual |
| Escalabilidad | ✅ Alta | ⚠️ Media | ✅ Alta |

**GANADOR**: ✅ **Opción C: Firestore con callbacks**

---

## 🎯 RESUMEN EJECUTIVO

### Decisión Técnica

> **Usar Firestore como capa de datos operacional para n8n, con sincronización bidireccional a PostgreSQL via callbacks.**

### Por qué

1. **Ya está implementado**: SUB-A ya guarda en Firestore
2. **Sin exposición de BD**: PostgreSQL permanece seguro
3. **Nodos nativos n8n**: Sin HTTP requests custom
4. **Escalable**: Firestore escala automáticamente
5. **Económico**: Firestore gratuito hasta 50k reads/día

### Próximos Pasos

1. ✅ Leer este documento
2. Actualizar SUB-A con campos de nurturing
3. Crear índice Firestore
4. Implementar SUB-D con query a Firestore
5. Crear callback `/webhook/nurturing-sent` en backend

---

**Autor**: Arquitecto de Sistema  
**Revisado por**: Pendiente  
**Última actualización**: 6 de Enero, 2026
