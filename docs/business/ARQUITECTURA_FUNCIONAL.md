# 🏗️ ARQUITECTURA FUNCIONAL - Carrillo Abogados Legal Tech Platform

**Versión**: 1.1  
**Fecha**: 11 de Enero, 2026  
**Estado**: ✅ Documento Actualizado - FASE 10

---

## 📋 PROPÓSITO

Este documento define el **mapeo funcional** entre los requerimientos de negocio y la arquitectura técnica de microservicios. Cada microservicio tiene un propósito claro derivado de las necesidades del bufete Carrillo Abogados.

---

## 🌐 VISIÓN GENERAL DE LA ARQUITECTURA

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         ARQUITECTURA FUNCIONAL                               │
│                     Carrillo Abogados Legal Tech                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                       FRONTEND (Next.js 16)                          │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────────────────────┐ │   │
│  │  │Portal Público│ │Portal Cliente│ │     Panel Interno            │ │   │
│  │  │(Visitantes)  │ │(Clientes)    │ │  (Abogados + Admins)         │ │   │
│  │  └──────────────┘ └──────────────┘ └──────────────────────────────┘ │   │
│  └──────────────────────────────────┬──────────────────────────────────┘   │
│                                     │                                       │
│                                     ▼                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                     API GATEWAY (Puerto 8080)                        │   │
│  │  • Enrutamiento de solicitudes                                       │   │
│  │  • Autenticación OAuth2 (Google Workspace)                          │   │
│  │  • Rate limiting, CORS                                              │   │
│  │  • Circuit breaker                                                   │   │
│  └──────────────────────────────┬──────────────────────────────────────┘   │
│                                 │                                           │
│       ┌─────────────────────────┼─────────────────────────┐                │
│       │                         │                         │                │
│       ▼                         ▼                         ▼                │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐     │
│  │ client  │   │  case   │   │ payment │   │document │   │calendar │     │
│  │ service │   │ service │   │ service │   │ service │   │ service │     │
│  │  8200   │   │  8300   │   │  8400   │   │  8500   │   │  8600   │     │
│  └────┬────┘   └────┬────┘   └────┬────┘   └────┬────┘   └────┬────┘     │
│       │             │             │             │             │            │
│       └─────────────┴──────┬──────┴─────────────┴─────────────┘            │
│                            │                                                │
│                            ▼                                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        NATS (Mensajería)                             │   │
│  │  Eventos: caso.creado, cliente.registrado, pago.procesado           │   │
│  └──────────────────────────────┬──────────────────────────────────────┘   │
│                                 │                                           │
│       ┌─────────────────────────┼─────────────────────────┐                │
│       ▼                         ▼                                          │
│  ┌───────────────┐       ┌───────────────┐                                 │
│  │ notification  │       │     n8n       │                                 │
│  │   service     │       │ integration   │                                 │
│  │    8700       │       │    8800       │                                 │
│  └───────┬───────┘       └───────┬───────┘                                 │
│          │                       │                                          │
│          ▼                       ▼                                          │
│  ┌───────────────┐       ┌───────────────┐                                 │
│  │ Gmail API     │       │   N8N Pro     │                                 │
│  │ (Notif email) │       │  (Workflows)  │                                 │
│  └───────────────┘       └───────────────┘                                 │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      PostgreSQL (Schemas)                            │   │
│  │  ┌─────────┐ ┌───────┐ ┌────────┐ ┌─────────┐ ┌────────┐ ┌───────┐  │   │
│  │  │ clients │ │ cases │ │payments│ │documents│ │calendar│ │notifs │  │   │
│  │  └─────────┘ └───────┘ └────────┘ └─────────┘ └────────┘ └───────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔌 MICROSERVICIOS - PROPÓSITO DE NEGOCIO

### 1. API Gateway (Puerto 8080)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Punto de entrada único para todas las solicitudes |
| **Usuarios** | Todos (Visitantes, Clientes, Abogados, Admins) |
| **Responsabilidades** | Enrutamiento, autenticación, autorización, rate limiting |

**Funciones de Negocio:**
- ✅ Validar que abogados usen email @carrilloabgd.com
- ✅ Dirigir solicitudes al microservicio correcto
- ✅ Proteger endpoints sensibles
- ✅ Implementar circuit breaker para resiliencia

**Tecnología:**
- Spring Cloud Gateway
- OAuth2 Resource Server
- Resilience4j Circuit Breaker

---

### 2. Client Service (Puerto 8200)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Gestión de clientes del bufete y usuarios del portal |
| **Usuarios** | Abogados, Administradores, Clientes |
| **Schema BD** | `clients` |

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Registro de clientes | Crear clientes personas naturales/jurídicas | MUST |
| CRUD clientes | Consultar, actualizar, eliminar clientes | MUST |
| Búsqueda avanzada | Por nombre, NIT, cédula, email | MUST |
| Portal cliente | Acceso para que clientes vean sus casos | MUST |
| Mensajería | Comunicación cliente-abogado | SHOULD |
| Historial | Ver todos los casos de un cliente | MUST |

**Modelo de Datos Principal:**

```
Client
├── id: UUID
├── clientType: NATURAL | JURIDICA
├── documentType: CC | NIT | CE | PASAPORTE
├── documentNumber: String
├── firstName / lastName (Natural)
├── companyName / legalRepresentative (Jurídica)
├── email: String
├── phone: String
├── address: String
├── city: String
├── status: ACTIVE | INACTIVE
├── createdAt: Timestamp
└── updatedAt: Timestamp
```

**Eventos que Emite:**
- `cliente.creado` → n8n-integration-service
- `cliente.actualizado` → notification-service

**Estado de Implementación:** 90% ✅

---

### 3. Case Service (Puerto 8300)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Gestión integral de casos legales |
| **Usuarios** | Abogados, Clientes (lectura) |
| **Schema BD** | `cases` |

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Crear caso | Registrar nuevo caso vinculado a cliente | MUST |
| Clasificación | Por área de práctica (5 áreas del bufete) | MUST |
| Estados | Workflow: Abierto → En Progreso → Cerrado | MUST |
| Actividades | Línea de tiempo de actuaciones | MUST |
| Términos | Fechas límite con alertas | MUST |
| Asignación | Abogado principal + colaboradores | MUST |
| Contraparte | Datos de parte contraria | SHOULD |

**Áreas de Práctica (Enum):**

```java
enum PracticeArea {
    ADMINISTRATIVE_LAW,      // Derecho Administrativo
    COMPETITION_LAW,         // Derecho de Competencias
    CORPORATE_LAW,           // Derecho Corporativo
    TELECOMMUNICATIONS_LAW,  // Derecho de Telecomunicaciones
    TRADEMARK_LAW            // Derecho de Marcas
}
```

**Modelo de Datos Principal:**

```
LegalCase
├── id: UUID
├── caseNumber: String (auto-generated)
├── title: String
├── description: String
├── clientId: UUID (FK)
├── practiceArea: Enum
├── caseType: JUDICIAL | ADMINISTRATIVE | CONSULTANCY
├── status: OPEN | IN_PROGRESS | SUSPENDED | CLOSED
├── assignedLawyerId: UUID
├── collaboratorIds: List<UUID>
├── filingNumber: String (radicado)
├── court: String (juzgado)
├── opposingParty: OpposingParty
├── activities: List<CaseActivity>
├── createdAt: Timestamp
└── updatedAt: Timestamp

CaseActivity
├── id: UUID
├── caseId: UUID (FK)
├── activityType: Enum
├── description: String
├── activityDate: LocalDate
├── visibleToClient: Boolean
├── attachments: List<String>
└── createdBy: UUID
```

**Eventos que Emite:**
- `caso.creado` → notification-service, n8n-integration
- `caso.actualizado` → notification-service
- `termino.creado` → calendar-service, notification-service

**Estado de Implementación:** 95% ✅

---

### 4. Payment Service (Puerto 8400)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Gestión de pagos a entidades gubernamentales |
| **Usuarios** | Abogados, Administradores |
| **Schema BD** | `payments` |

**Contexto de Negocio:**

Los abogados de Carrillo ABGD realizan pagos a entidades gubernamentales durante procesos legales:
- **Tasas judiciales** - Juzgados
- **Aranceles notariales** - Notarías
- **Registro de marcas** - SIC (Superintendencia de Industria y Comercio)
- **Impuestos de registro** - Cámara de Comercio
- **Trámites MinTIC** - Para servicios de telecomunicaciones

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Registrar pago | Crear orden de pago con concepto y monto | MUST |
| Vincular a caso | Asociar pago a caso legal específico | MUST |
| Comprobante | Subir comprobante de pago (PDF/imagen) | MUST |
| Estados | Pendiente → Pagado → Verificado | MUST |
| Reportes | Por período, caso, cliente | SHOULD |

**Modelo de Datos:**

```
Payment
├── id: UUID
├── caseId: UUID (FK)
├── paymentType: JUDICIAL_FEE | NOTARIAL_FEE | SIC_REGISTRATION | OTHER
├── concept: String
├── amount: BigDecimal
├── recipientEntity: String
├── paymentDate: LocalDate
├── referenceNumber: String
├── receiptDocumentId: UUID (FK a document-service)
├── status: PENDING | PAID | VERIFIED
├── verifiedBy: UUID
├── createdAt: Timestamp
└── updatedAt: Timestamp
```

**Eventos que Emite:**
- `pago.registrado` → notification-service
- `pago.verificado` → case-service (agregar actividad)

**Estado de Implementación:** 5% ⏳

---

### 5. Document Service (Puerto 8500)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Almacenamiento y gestión de documentos legales |
| **Usuarios** | Abogados, Clientes, Administradores |
| **Schema BD** | `documents` |

**Contexto de Negocio:**

Documentos legales críticos que maneja el bufete:
- **Contratos** - Borradores, versiones finales, anexos
- **Demandas/Contestaciones** - Documentos procesales
- **Poderes** - Representación legal
- **Recursos** - Reposición, apelación, casación
- **Escrituras públicas** - Documentos notariales
- **Registros SIC** - Solicitudes de marcas, certificados

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Upload | Subir archivos hasta 50MB | MUST |
| Clasificación | Por tipo, caso, cliente | MUST |
| Versionamiento | Control de versiones | SHOULD |
| Búsqueda | Por nombre, fecha, tipo | MUST |
| Compartir | Con cliente vía portal | SHOULD |
| Plantillas | Biblioteca de templates | COULD |
| Google Drive | Sincronización | SHOULD |

**Modelo de Datos:**

```
Document
├── id: UUID
├── filename: String
├── originalFilename: String
├── mimeType: String
├── size: Long
├── storagePath: String (GCS o local)
├── documentType: CONTRACT | LAWSUIT | POWER | RECEIPT | OTHER
├── caseId: UUID (FK, opcional)
├── clientId: UUID (FK, opcional)
├── uploadedBy: UUID
├── sharedWithClient: Boolean
├── version: Integer
├── parentDocumentId: UUID (para versiones)
├── createdAt: Timestamp
└── updatedAt: Timestamp
```

**Integraciones:**
- Google Cloud Storage (producción)
- Local filesystem (desarrollo)
- Google Drive API (sincronización)

**Eventos que Emite:**
- `documento.subido` → notification-service (si cliente sube)
- `documento.compartido` → notification-service

**Estado de Implementación:** 5% ⏳

---

### 6. Calendar Service (Puerto 8600)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Gestión de calendario y sincronización con Google |
| **Usuarios** | Abogados, Clientes (lectura de citas), Visitantes (booking) |
| **Schema BD** | `calendar` |

**Contexto de Negocio:**

Los abogados necesitan gestionar:
- **Audiencias** - Fechas de juzgados y entidades
- **Términos legales** - Vencimientos procesales críticos
- **Citas con clientes** - Consultas y reuniones
- **Plazos internos** - Entrega de documentos

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Crear evento | Audiencias, reuniones, términos | MUST |
| Google Sync | Bidireccional con Google Calendar | MUST |
| Recordatorios | Alertas configurables | MUST |
| Disponibilidad | Mostrar slots disponibles | SHOULD |
| Booking online | Reserva desde portal público | MUST |
| Confirmación | Email automático de cita | MUST |

**Modelo de Datos:**

```
CalendarEvent
├── id: UUID
├── title: String
├── description: String
├── eventType: HEARING | DEADLINE | MEETING | APPOINTMENT | OTHER
├── startDateTime: LocalDateTime
├── endDateTime: LocalDateTime
├── location: String (físico o Meet link)
├── caseId: UUID (FK, opcional)
├── lawyerId: UUID
├── clientId: UUID (opcional)
├── googleEventId: String (sincronización)
├── status: CONFIRMED | TENTATIVE | CANCELLED
├── reminders: List<Reminder>
├── createdAt: Timestamp
└── updatedAt: Timestamp

Reminder
├── id: UUID
├── eventId: UUID (FK)
├── minutesBefore: Integer
├── method: EMAIL | PUSH | SMS
└── sent: Boolean
```

**Integraciones:**
- Google Calendar API
- Google Meet (links automáticos para reuniones virtuales)

**Eventos que Emite:**
- `cita.creada` → notification-service
- `cita.recordatorio` → notification-service
- `termino.vencimiento` → notification-service

**Estado de Implementación:** 5% ⏳

---

### 7. Notification Service (Puerto 8700)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Envío de notificaciones multicanal |
| **Usuarios** | Sistema (interno) |
| **Schema BD** | `notifications` |

**Contexto de Negocio:**

Comunicación proactiva con clientes y equipo:
- **Clientes**: Actualizaciones de casos, citas, documentos
- **Abogados**: Términos próximos, nuevos documentos de clientes
- **Leads**: Confirmación de contacto, seguimiento

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| Email | Envío via Gmail API | MUST |
| Templates | Plantillas HTML configurables | MUST |
| Preferencias | Usuario decide qué recibir | SHOULD |
| Log | Historial de envíos | MUST |
| SMS | Para alertas críticas | SHOULD |
| WhatsApp | Business API (futuro) | COULD |

**Tipos de Notificación:**

| Evento | Destinatario | Canal |
|--------|--------------|-------|
| Caso creado | Cliente | Email |
| Estado cambiado | Cliente | Email |
| Documento compartido | Cliente | Email |
| Cita confirmada | Cliente, Abogado | Email |
| Término próximo (7d, 3d, 1d) | Abogado | Email, Push |
| Nuevo lead | Admin | Email |
| Documento subido por cliente | Abogado | Email |
| Mensaje de cliente | Abogado | Email |

**Modelo de Datos:**

```
Notification
├── id: UUID
├── recipientId: UUID
├── recipientEmail: String
├── notificationType: CASE_UPDATE | APPOINTMENT | DEADLINE | MESSAGE | LEAD
├── channel: EMAIL | SMS | PUSH | WHATSAPP
├── templateId: String
├── subject: String
├── body: String
├── status: PENDING | SENT | FAILED | DELIVERED
├── sentAt: Timestamp
├── errorMessage: String (si falló)
├── createdAt: Timestamp
└── relatedEntityId: UUID (caso, cita, etc.)

NotificationTemplate
├── id: UUID
├── name: String
├── notificationType: Enum
├── channel: Enum
├── subject: String (con variables {{variable}})
├── bodyHtml: String
├── bodyText: String
└── active: Boolean
```

**Integraciones:**
- Gmail API (OAuth2 @carrilloabgd.com)
- Twilio (SMS - futuro)
- WhatsApp Business API (futuro)

**Eventos que Escucha:**
- `caso.*` de case-service
- `documento.*` de document-service
- `cita.*` de calendar-service
- `pago.*` de payment-service

**Estado de Implementación:** 5% ⏳

---

### 8. N8N Integration Service (Puerto 8800)

| Aspecto | Descripción |
|---------|-------------|
| **Propósito** | Bridge bidireccional entre plataforma web y n8n Cloud |
| **Usuarios** | Sistema, Administrador (configuración), Marketing (workflows) |
| **Schema BD** | `n8n_integration` |

**Contexto de Negocio:**

n8n Cloud es el **motor de automatización de marketing** que ejecuta 17 workflows organizados en **3 MEGA-WORKFLOWS**:

| MEGA-WORKFLOW | Propósito | Workflows | Nodos | Estado |
|---------------|-----------|-----------|-------|--------|
| **MW#1: Captura** | Lead → Cliente | 7 | 108 | 28% implementado |
| **MW#2: Retención** | Cliente → Recompra (Flywheel) | 5 | 72 | Q2 2026 |
| **MW#3: SEO** | Tráfico → Lead (Content Factory) | 5 | 60 | Q2-Q3 2026 |

**Ver documentación completa:** [ESTRATEGIA_AUTOMATIZACION.md](./ESTRATEGIA_AUTOMATIZACION.md)

**Funciones de Negocio:**

| Función | Descripción | Prioridad |
|---------|-------------|-----------|
| **NATS → n8n** | Escuchar eventos y enviar a webhooks n8n | MUST |
| **n8n → API** | Recibir callbacks y ejecutar acciones | MUST |
| **Lead Sync** | Sincronizar leads entre Firestore y PostgreSQL | MUST |
| **Logs** | Auditoría de ejecuciones | SHOULD |

**Webhooks Expuestos (n8n → Plataforma):**

| Endpoint | Método | Origen n8n | Acción |
|----------|--------|------------|--------|
| `/webhook/lead-scored` | POST | SUB-A | Actualizar leadScore en client-service |
| `/webhook/lead-hot` | POST | SUB-B | Notificar abogado, crear tarea urgente |
| `/webhook/upsell-detected` | POST | SUB-J | Crear oportunidad en case-service |
| `/webhook/content-ready` | POST | SUB-L | Publicar en blog (document-service) |

**Webhooks que Dispara (Plataforma → n8n):**

| Evento NATS | Webhook n8n | MEGA-WORKFLOW |
|-------------|-------------|---------------|
| `lead.capturado` | `/lead-events` | MW#1 → SUB-A |
| `cita.agendada` | `/meeting-events` | MW#1 → SUB-F |
| `cliente.inactivo` | `/client-events` | MW#2 → SUB-I |
| `caso.cerrado` | `/case-events` | MW#2 → SUB-G |

**Sub-Workflows MW#1 (Captura y Conversión):**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     MW#1: LEAD LIFECYCLE MANAGER                            │
│                     (7 Sub-Workflows, 108 Nodos)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  SUB-A: Lead Intake (Scoring)    ──► Lead capturado → scoring → clasificar │
│  SUB-B: Hot Lead Notifier        ──► HOT (≥70) → notificar Dr. Carrillo    │
│  SUB-C: AI Email Responder       ──► Respuesta IA < 1 minuto (Gemini)      │
│  SUB-D: Lead Nurturing           ──► Secuencia 12 emails (60 días)         │
│  SUB-E: Email Engagement Tracker ──► Opens, clicks → actualizar score      │
│  SUB-F: Meeting Scheduler        ──► Calendly → confirmar cita             │
│  MAIN:  Orquestador Hub          ──► Coordina todos los sub-workflows      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Lead Scoring (Calculado por n8n, almacenado en Firestore + PostgreSQL):**

| Criterio | Puntos | Descripción |
|----------|--------|-------------|
| Base | 30 | Todo lead inicia con 30 pts |
| Servicio "marca" o "litigio" | +20 | Alta intención comercial |
| Mensaje > 50 caracteres | +10 | Interés detallado |
| Tiene teléfono | +10 | Contactabilidad |
| Tiene empresa | +10 | Posible cliente B2B |
| Email corporativo | +10 | No @gmail, @hotmail |
| Cargo C-Level | +20 | Decisor |
| **HOT** | ≥70 | Notificar inmediatamente |
| **WARM** | 40-69 | Nurturing automatizado |
| **COLD** | <40 | Respuesta genérica |

**Modelo de Datos:**

```
Lead (en client-service)
├── id: UUID
├── nombre, email, telefono, empresa, cargo
├── servicio: String (área de interés)
├── mensaje: String
├── leadScore: Integer (0-100)
├── leadCategory: HOT | WARM | COLD
├── leadStatus: NUEVO | NURTURING | MQL | SQL | CONVERTIDO | CHURNED
├── emailsSent, emailsOpened, emailsClicked: Integer
├── lastEngagement: Timestamp
├── clientId: UUID (si se convierte)
└── source: WEBSITE | REFERRAL | LINKEDIN | EVENTO

WorkflowExecution
├── id: UUID
├── megaWorkflow: MW1 | MW2 | MW3
├── subWorkflow: String (SUB-A, SUB-B, etc.)
├── triggerEvent: String
├── inputData: JSON
├── outputData: JSON
├── status: RUNNING | SUCCESS | FAILED
├── executionTimeMs: Long
├── startedAt, finishedAt: Timestamp
└── errorMessage: String
```

**Integraciones n8n Configuradas:**

| Servicio | Estado | Uso |
|----------|--------|-----|
| Gmail OAuth2 | ✅ Activo | Envío emails, respuestas |
| Firestore | ✅ Activo | Almacenamiento leads (n8n) |
| Google Gemini | ✅ Activo | IA para emails personalizados |
| Mailersend | ⏳ Pendiente | Email marketing (MW#2) |
| Calendly | ⏳ Pendiente | Booking citas (SUB-F) |
| WordPress REST | ⏳ Pendiente | Publicación blog (MW#3) |
| Google Search Console | ⏳ Pendiente | Tracking SEO (MW#3) |

**Estado de Implementación:** 15% 🔄

---

## 🔄 FLUJO DE DATOS ENTRE SERVICIOS

### Flujo: Crear Nuevo Caso

```
┌──────────────────────────────────────────────────────────────────┐
│                    FLUJO: CREAR NUEVO CASO                       │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Abogado → API Gateway → case-service                        │
│     POST /case-service/api/cases                                │
│                                                                  │
│  2. case-service valida cliente existe                          │
│     GET client-service/api/clients/{id}                         │
│                                                                  │
│  3. case-service crea caso                                      │
│     INSERT INTO cases.legal_cases                               │
│                                                                  │
│  4. case-service emite evento                                   │
│     NATS: caso.creado { caseId, clientId, lawyerId }           │
│                                                                  │
│  5. notification-service escucha                                │
│     → Envía email al cliente                                    │
│                                                                  │
│  6. n8n-integration-service escucha                             │
│     → Dispara workflow de nuevo caso                            │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Flujo: Cliente Sube Documento

```
┌──────────────────────────────────────────────────────────────────┐
│                 FLUJO: CLIENTE SUBE DOCUMENTO                    │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Cliente → API Gateway → document-service                    │
│     POST /document-service/api/documents/upload                 │
│     (multipart/form-data)                                       │
│                                                                  │
│  2. document-service valida cliente tiene acceso al caso        │
│     GET case-service/api/cases/{id}                             │
│                                                                  │
│  3. document-service almacena archivo                           │
│     → Google Cloud Storage (producción)                         │
│     → Local filesystem (desarrollo)                             │
│                                                                  │
│  4. document-service registra metadata                          │
│     INSERT INTO documents.documents                             │
│                                                                  │
│  5. document-service emite evento                               │
│     NATS: documento.subido { docId, caseId, uploadedBy }       │
│                                                                  │
│  6. notification-service escucha                                │
│     → Envía email al abogado asignado                          │
│                                                                  │
│  7. case-service escucha                                        │
│     → Agrega actividad automática "Cliente subió documento"     │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📊 MATRIZ DE RESPONSABILIDADES (RACI)

```
┌────────────────────┬─────────┬─────────┬─────────┬─────────┬─────────┬─────────┬─────────┬─────────┐
│ Función            │ Gateway │ Client  │  Case   │ Payment │  Doc    │ Calendar│ Notif   │   N8N   │
├────────────────────┼─────────┼─────────┼─────────┼─────────┼─────────┼─────────┼─────────┼─────────┤
│ Autenticación      │    R    │    I    │    I    │    I    │    I    │    I    │    I    │    I    │
│ Autorización       │   R/A   │    C    │    C    │    C    │    C    │    C    │         │         │
│ Gestión clientes   │         │   R/A   │    C    │    I    │    C    │    C    │    I    │    I    │
│ Gestión casos      │         │    C    │   R/A   │    C    │    C    │    C    │    I    │    I    │
│ Gestión pagos      │         │         │    C    │   R/A   │    C    │         │    I    │    I    │
│ Gestión documentos │         │    C    │    C    │    C    │   R/A   │         │    I    │    I    │
│ Gestión calendario │         │    C    │    C    │         │         │   R/A   │    I    │    I    │
│ Envío notificac.   │         │         │    I    │    I    │    I    │    I    │   R/A   │    C    │
│ Automatizaciones   │         │    I    │    I    │    I    │    I    │    I    │    C    │   R/A   │
└────────────────────┴─────────┴─────────┴─────────┴─────────┴─────────┴─────────┴─────────┴─────────┘

R = Responsable (ejecuta)
A = Accountable (rinde cuentas)
C = Consultado
I = Informado
```

---

## 🚀 PLAN DE IMPLEMENTACIÓN POR PRIORIDAD

### Fase 1: Core (MVP - Marzo 2026)

| Servicio | Funcionalidad | Semanas |
|----------|---------------|---------|
| client-service | CRUD completo clientes | ✅ Hecho |
| case-service | CRUD completo casos | ✅ Hecho |
| calendar-service | Google Calendar sync + booking | 4 |
| notification-service | Email transaccional | 3 |
| document-service | Upload/download básico | 3 |

### Fase 2: Portal Cliente (Q2 2026)

| Servicio | Funcionalidad | Semanas |
|----------|---------------|---------|
| client-service | Portal cliente (login, ver casos) | 3 |
| document-service | Compartir con cliente | 2 |
| notification-service | Preferencias usuario | 2 |

### Fase 3: Automatizaciones (Q3 2026)

| Servicio | Funcionalidad | Semanas |
|----------|---------------|---------|
| n8n-integration-service | Bridge completo | 4 |
| payment-service | Gestión pagos | 3 |
| notification-service | SMS, plantillas avanzadas | 2 |

---

## 🔗 DOCUMENTOS RELACIONADOS

- [MODELO_NEGOCIO.md](./MODELO_NEGOCIO.md) - Contexto de negocio
- [REQUERIMIENTOS.md](./REQUERIMIENTOS.md) - Requerimientos funcionales
- [ROLES_USUARIOS.md](./ROLES_USUARIOS.md) - Definición de roles
- [CASOS_USO.md](./CASOS_USO.md) - Flujos detallados

---

*Documento generado para la plataforma Carrillo Abogados Legal Tech*
