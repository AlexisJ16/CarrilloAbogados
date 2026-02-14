# 🚀 PRÓXIMOS DESARROLLOS - Carrillo Abogados Legal Tech

**Última Actualización**: 14 de Febrero, 2026  
**Fase Actual**: FASE 14 - Infraestructura Depurada  
**MVP Target**: 27 de Marzo, 2026

---

## 📊 ESTADO ACTUAL DEL PROYECTO

### ✅ Completado (FASE 14)

| Componente | Estado | Detalles |
|------------|--------|----------|
| **Frontend Público** | ✅ 100% | 16 rutas, 5 páginas en español |
| **client-service** | ✅ 100% | 105 tests, Lead API completa |
| **case-service** | ✅ 95% | CRUD, timeline, estados |
| **n8n-integration-service** | ✅ 95% | Bridge NATS ↔ Webhooks |
| **notification-service** | ✅ 80% | Backend completo |
| **Docker Compose** | ✅ 100% | 11/11 contenedores healthy (Jib) |
| **CI/CD Pipeline** | ✅ 100% | 8 imágenes en ghcr.io |
| **CORS + Auth Frontend** | ✅ 100% | Login/Logout funcional desde frontend |
| **Header con Auth UI** | ✅ 100% | Botón login/menú usuario implementado |
| **Infra Depurada** | ✅ 100% | user-service eliminado, Jib, configs limpias |

### 🔄 En Progreso

| Componente | Estado | Bloqueador |
|------------|--------|------------|
| **n8n Marketing Integration** | ⏳ 0% | Esperando marketing dev |
| **GCP Deploy** | ⏳ 0% | Requiere merge a main |

---

## 🎯 FEATURES POR IMPLEMENTAR

### P0: Crítico para MVP (Enero 2026)

#### 1. Sync Branches + Deploy Staging
```
Tiempo: 4-6 horas
Dependencias: CI/CD passing ✅
```

**Tareas:**
- [ ] Merge dev → main
- [ ] Configurar Cloud Run en GCP
- [ ] Configurar Cloud SQL PostgreSQL
- [ ] Desplegar servicios core (api-gateway, client-service, case-service)
- [ ] Configurar dominio api.carrilloabgd.com

#### 2. Integración n8n Cloud
```
Tiempo: 8-12 horas (+ marketing dev)
Dependencias: Acceso n8n Cloud, webhooks configurados
```

**Tareas:**
- [ ] Marketing dev configura webhooks en n8n Cloud
- [ ] Conectar POST `/api/leads` → n8n webhook
- [ ] Implementar callback `/webhook/lead-scored`
- [ ] Implementar callback `/webhook/lead-hot`
- [ ] Probar flujo completo: formulario → scoring → respuesta

---

### P1: Importante (Enero-Febrero 2026)

#### 3. Google Calendar Integration
```
Servicio: calendar-service
Tiempo: 8-12 horas
Dependencias: Credenciales OAuth2 Google
```

**Modelo de Datos:**
```java
@Entity
public class CalendarEvent {
    private UUID id;
    private String title;
    private String description;
    private EventType type; // HEARING, DEADLINE, MEETING, APPOINTMENT
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private UUID caseId; // opcional
    private UUID lawyerId;
    private UUID clientId; // opcional
    private String googleEventId; // sync
    private EventStatus status; // CONFIRMED, TENTATIVE, CANCELLED
}
```

**Endpoints:**
```
GET    /api/calendar/events
GET    /api/calendar/events/{id}
POST   /api/calendar/events
PUT    /api/calendar/events/{id}
DELETE /api/calendar/events/{id}
GET    /api/calendar/availability?lawyerId={id}&date={date}
POST   /api/calendar/sync  # Sync con Google
```

**Funcionalidades:**
- [ ] CRUD de eventos
- [ ] Sincronización bidireccional con Google Calendar
- [ ] Booking público para citas (integrar con frontend `/contacto`)
- [ ] Recordatorios automáticos (1 día, 1 hora antes)
- [ ] Vista de disponibilidad por abogado

---

#### 4. Email Templates (notification-service)
```
Servicio: notification-service
Tiempo: 4-6 horas
Dependencias: Gmail API OAuth2
```

**Templates a crear:**
```
templates/
├── lead-confirmation.html       # Confirmación de contacto
├── lead-hot-alert.html          # Alerta lead HOT para abogado
├── appointment-confirmation.html # Confirmación de cita
├── appointment-reminder.html    # Recordatorio de cita
├── case-update.html             # Actualización de caso
├── document-shared.html         # Documento compartido
└── deadline-reminder.html       # Recordatorio de término
```

**Variables comunes:**
```
{{clientName}}
{{lawyerName}}
{{caseNumber}}
{{appointmentDate}}
{{firmName}} = "Carrillo ABGD SAS"
{{firmPhone}} = "+57 2 XXX XXXX"
{{firmAddress}} = "Torre de Cali, Piso 21"
```

---

### P2: Necesario (Febrero 2026)

#### 5. Document Service Completo
```
Servicio: document-service
Tiempo: 6-8 horas
Dependencias: Google Cloud Storage (opcional, puede ser local)
```

**Modelo de Datos:**
```java
@Entity
public class Document {
    private UUID id;
    private String filename;
    private String originalFilename;
    private String mimeType;
    private Long size;
    private String storagePath;
    private DocumentType type; // CONTRACT, LAWSUIT, POWER, RECEIPT, OTHER
    private UUID caseId;
    private UUID clientId;
    private UUID uploadedBy;
    private Boolean sharedWithClient;
    private Integer version;
    private UUID parentDocumentId; // para versiones
}
```

**Endpoints:**
```
POST   /api/documents/upload
GET    /api/documents/{id}
GET    /api/documents/{id}/download
GET    /api/documents/case/{caseId}
GET    /api/documents/client/{clientId}
DELETE /api/documents/{id}
PUT    /api/documents/{id}/share  # Compartir con cliente
```

**Funcionalidades:**
- [ ] Upload multipart (hasta 50MB)
- [ ] Clasificación por tipo
- [ ] Vinculación a caso/cliente
- [ ] Versionamiento básico
- [ ] Compartir con cliente (portal)
- [ ] Storage: Local (dev) / GCS (prod)

---

#### 6. Payment Service Completo
```
Servicio: payment-service
Tiempo: 6-8 horas
Dependencias: Ninguna
```

**Modelo de Datos:**
```java
@Entity
public class Payment {
    private UUID id;
    private UUID caseId;
    private PaymentType type; // JUDICIAL_FEE, NOTARIAL_FEE, SIC_REGISTRATION, OTHER
    private String concept;
    private BigDecimal amount;
    private String recipientEntity;
    private LocalDate paymentDate;
    private String referenceNumber;
    private UUID receiptDocumentId; // FK a document-service
    private PaymentStatus status; // PENDING, PAID, VERIFIED
    private UUID verifiedBy;
}
```

**Endpoints:**
```
GET    /api/payments
GET    /api/payments/{id}
POST   /api/payments
PUT    /api/payments/{id}
DELETE /api/payments/{id}
GET    /api/payments/case/{caseId}
PUT    /api/payments/{id}/verify
GET    /api/payments/report?from={date}&to={date}
```

**Funcionalidades:**
- [ ] CRUD de pagos
- [ ] Vinculación a casos
- [ ] Subir comprobante (via document-service)
- [ ] Estados: PENDING → PAID → VERIFIED
- [ ] Reportes por período

---

### P3: Deseable (Marzo 2026)

#### 7. Portal Cliente
```
Servicios: client-service, frontend
Tiempo: 12-16 horas
Dependencias: Autenticación OAuth2 completa
```

**Funcionalidades:**
- [ ] Login OAuth2 para clientes
- [ ] Dashboard cliente (ver sus casos)
- [ ] Ver timeline de actividades de sus casos
- [ ] Ver documentos compartidos
- [ ] Subir documentos
- [ ] Mensajería con abogado asignado
- [ ] Ver citas programadas
- [ ] Notificaciones

---

#### 8. Autenticación OAuth2 Google Workspace
```
Servicio: api-gateway
Tiempo: 8-12 horas
Dependencias: Google Cloud Console configurado
```

**Funcionalidades:**
- [ ] Login con @carrilloabgd.com para abogados
- [ ] Login con cualquier Google para clientes
- [ ] Roles automáticos basados en dominio
- [ ] JWT tokens con claims de roles
- [ ] Refresh token flow

---

## 📐 ARQUITECTURA DE INTEGRACIÓN n8n

### Flujo Lead Capture (MW#1)

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│  Frontend   │     │  client-service  │     │    NATS     │
│  /contacto  │────►│  POST /api/leads │────►│lead.captured│
└─────────────┘     └──────────────────┘     └──────┬──────┘
                                                    │
                    ┌──────────────────┐            │
                    │n8n-integration-  │◄───────────┘
                    │    service       │
                    └────────┬─────────┘
                             │ HTTP POST
                             ▼
                    ┌──────────────────┐
                    │   n8n Cloud      │
                    │  /lead-events    │
                    └────────┬─────────┘
                             │ Scoring + AI Response
                             ▼
                    ┌──────────────────┐
                    │n8n-integration-  │
                    │/webhook/lead-    │
                    │   scored         │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  client-service  │
                    │ Update leadScore │
                    └──────────────────┘
```

### Webhooks Expuestos (n8n → Plataforma)

| Endpoint | Método | Payload | Acción |
|----------|--------|---------|--------|
| `/webhook/lead-scored` | POST | `{leadId, score, category}` | Actualiza score en BD |
| `/webhook/lead-hot` | POST | `{leadId, urgency}` | Notifica abogado |
| `/webhook/upsell-detected` | POST | `{clientId, opportunity}` | Crea oportunidad |
| `/webhook/content-ready` | POST | `{articleId, content}` | Publica en blog |

### Eventos NATS Emitidos (Plataforma → n8n)

| Topic | Trigger | Payload |
|-------|---------|---------|
| `lead.captured` | POST /api/leads | Lead completo |
| `appointment.scheduled` | Cita creada | Evento calendario |
| `case.closed` | Caso cerrado | Case + Client info |
| `client.inactive` | 30 días sin actividad | Client info |

---

## 🔧 CONFIGURACIÓN REQUERIDA

### Variables de Entorno Producción

```bash
# GCP
GOOGLE_CLOUD_PROJECT=carrillo-abogados
GCP_REGION=us-central1

# Database
POSTGRES_HOST=cloud-sql-ip
POSTGRES_DB=carrillo_legal_tech
POSTGRES_USER=carrillo
POSTGRES_PASSWORD=${SECRET}

# n8n
N8N_WEBHOOK_URL=https://your-n8n-instance.app.n8n.cloud

# Google APIs
GOOGLE_CLIENT_ID=${SECRET}
GOOGLE_CLIENT_SECRET=${SECRET}
GOOGLE_CALENDAR_ID=primary
```

### GitHub Secrets Adicionales

```
N8N_WEBHOOK_URL
GOOGLE_CALENDAR_CREDENTIALS
GMAIL_SERVICE_ACCOUNT_KEY
```

---

## 📅 TIMELINE SUGERIDO

```
ENERO 2026                    FEBRERO 2026                   MARZO 2026
W1      W2      W3      W4    W1      W2      W3      W4     W1      W2      W3      W4
├───────┼───────┼───────┼─────┼───────┼───────┼───────┼──────┼───────┼───────┼───────┼──────►
│       │       │       │     │       │       │       │      │       │       │       │
│ Sync  │ GCP   │ n8n   │ Cal │ Email │ Docs  │ Pay   │ Test │Portal │OAuth2 │ QA    │ MVP🚀
│ main  │Deploy │Integr │ API │ Templ │ Svc   │ Svc   │ E2E  │Client │Google │ Final │
│       │       │       │     │       │       │       │      │       │       │       │
└───────┴───────┴───────┴─────┴───────┴───────┴───────┴──────┴───────┴───────┴───────┴──────┘
```

---

## 📚 DOCUMENTACIÓN RELACIONADA

| Documento | Ubicación |
|-----------|-----------|
| Estado del Proyecto | [PROYECTO_ESTADO.md](../../PROYECTO_ESTADO.md) |
| Arquitectura | [docs/architecture/ARCHITECTURE.md](../architecture/ARCHITECTURE.md) |
| Modelo de Negocio | [docs/business/MODELO_NEGOCIO.md](../business/MODELO_NEGOCIO.md) |
| Requerimientos | [docs/business/REQUERIMIENTOS.md](../business/REQUERIMIENTOS.md) |
| Deploy GCP | [docs/operations/DEPLOY_GCP.md](../operations/DEPLOY_GCP.md) |
| Guía Marketing | [docs/development/GUIA_INTEGRACION_MARKETING.md](GUIA_INTEGRACION_MARKETING.md) |
| Automation n8n | [automation/README.md](../../automation/README.md) |

---

*Documento creado: 3 de Enero, 2026*  
*Última actualización: 14 de Febrero, 2026*
