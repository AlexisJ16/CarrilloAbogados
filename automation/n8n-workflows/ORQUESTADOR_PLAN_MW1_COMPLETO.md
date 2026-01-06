# 🎯 PLAN ORQUESTADOR - Completar MW#1 Lead Lifecycle

**Rol**: Orquestador Principal  
**Fecha**: 5 de Enero, 2026  
**Objetivo**: Implementar los 3 sub-workflows faltantes de MW#1 con integración completa  
**Tiempo Total**: 18 horas (6h n8n + 12h Backend)  
**Estado Actual**: MW#1 28% → Objetivo: 100%

---

## 📊 ANÁLISIS DEL ESTADO ACTUAL

### ✅ Lo que FUNCIONA (Verificado)

| Componente | Estado | Evidencia |
|------------|--------|-----------|
| **Orquestador (Hub)** | ✅ 100% | ID: `bva1Kc1USbbITEAw`, 8 nodos, ACTIVO |
| **SUB-A: Lead Intake** | ✅ 100% | ID: `RHj1TAqBazxNFriJ`, 13 nodos, Gemini IA, LISTO |
| **n8n Webhook** | ✅ 100% | `/webhook/lead-events` recibiendo peticiones |
| **Firestore** | ✅ 100% | Guardando leads, proyecto `carrillo-marketing-core` |
| **Gmail API** | ✅ 100% | Enviando emails desde marketing@carrilloabgd.com |
| **Gemini 2.5 Pro** | ✅ 100% | Scoring + respuesta IA |

### ❌ Lo que FALTA (Bloqueadores MW#1 100%)

| Sub-Workflow | Propósito | Estado | Bloqueador |
|--------------|-----------|--------|------------|
| **SUB-D: Nurturing** | Secuencia 8-12 emails para WARM/COLD | 🔴 0% | No existe en n8n |
| **SUB-E: Engagement** | Tracking opens/clicks de Mailersend | 🔴 0% | No existe en n8n |
| **SUB-F: Meeting** | Sincronizar citas de Calendly/Google | 🔴 0% | No existe en n8n |

### 🚨 Integraciones Backend CRÍTICAS (Sin esto, nada funciona E2E)

| Integración | Estado | Bloqueador |
|-------------|--------|------------|
| **Frontend → client-service** | ✅ OK | Ya conectado |
| **client-service → NATS** | 🔴 0% | NO emite `lead.capturado` |
| **NATS → n8n-integration-service** | 🔴 0% | Sin listener |
| **n8n-integration-service → n8n** | 🔴 0% | No llama webhook |
| **n8n → client-service (callback)** | 🔴 0% | Sin webhooks `/webhook/lead-scored` |

---

## 🎯 ESTRATEGIA DE IMPLEMENTACIÓN

### Principio: **Backend Primero, n8n Después**

**Razón**: Los sub-workflows de n8n NO pueden probarse sin:
1. Datos reales fluyendo desde la plataforma web
2. Callbacks funcionando para actualizar PostgreSQL
3. Integración NATS completamente operativa

### Fases de Trabajo

```
FASE 1: BACKEND CRÍTICO (12h)          ← PRIORIDAD MÁXIMA
  ├── client-service: Evento NATS (2h)
  ├── n8n-integration-service: Listener NATS (2h)
  ├── n8n-integration-service: Webhooks Callbacks (3h)
  ├── Testing E2E básico (2h)
  └── Documentación y ajustes (3h)
              │
              ▼
FASE 2: CALLBACKS EN SUB-A (2h)        ← MARKETING DEV
  ├── Agregar nodo HTTP Request en SUB-A
  ├── POST /webhook/lead-scored
  └── POST /webhook/lead-hot (solo HOT)
              │
              ▼
FASE 3: SUB-D NURTURING (4h)           ← MARKETING DEV
  ├── Workflow con Schedule trigger
  ├── Query Firestore (leads en nurturing)
  ├── Loop + Posición en secuencia
  ├── Gemini genera email personalizado
  ├── Mailersend envía (tracking activado)
  └── Actualizar Firestore
              │
              ▼
FASE 4: SUB-E ENGAGEMENT (2h)          ← MARKETING DEV (Opcional)
  ├── Webhook Mailersend events
  ├── Parsear opens/clicks
  ├── Actualizar score en Firestore
  └── Trigger SUB-B si cambio a HOT
              │
              ▼
FASE 5: SUB-F MEETING (2h)             ← MARKETING DEV (Opcional)
  ├── Webhook Calendly
  ├── Buscar lead por email
  ├── Actualizar status "reunion_agendada"
  └── Notificar equipo + Confirmación lead
```

---

## 📋 TAREAS DETALLADAS

### 🔧 FASE 1: BACKEND CRÍTICO (12 horas)

#### Tarea 1.1: client-service - Evento NATS (2h)

**Responsable**: Backend Dev  
**Archivos**: `client-service/src/main/java/com/carrillo/clients/`

**Pasos**:

1. **Crear `event/LeadCapturedEvent.java`** (15 min)
   ```java
   @Data
   public class LeadCapturedEvent implements Serializable {
       private String leadId;
       private String nombre;
       private String email;
       private String telefono;
       private String empresa;
       private String servicio;
       private String mensaje;
       private String source;
       private Instant timestamp;
   }
   ```

2. **Modificar `service/LeadService.java`** (30 min)
   - Inyectar `NatsTemplate`
   - En `createLead()`: Después de `save()`, publicar evento NATS
   - Subject: `"lead.capturado"`
   - Manejar errores sin fallar transacción

3. **Agregar endpoint PATCH `/{id}/score`** (30 min)
   - DTO: `UpdateLeadScoreDTO` (score, categoria)
   - Validaciones: score 0-100, categoria HOT/WARM/COLD
   - Actualizar `lead.score`, `lead.categoria`, `lead.estado = QUALIFIED`

4. **Configurar NATS** (15 min)
   - `application.yml`: `spring.nats.server: nats://localhost:4222`
   - Docker profile: `nats://nats:4222`
   - `pom.xml`: Dependencia `spring-nats:0.5.5`

5. **Testing local** (30 min)
   - Verificar: POST /leads → Log "Published lead.capturado"
   - Suscribirse con nats-box: `nats sub "lead.capturado"`

**Entregable**: client-service publica eventos NATS correctamente

---

#### Tarea 1.2: n8n-integration-service - NATS Listener (2h)

**Responsable**: Backend Dev  
**Archivos**: `n8n-integration-service/src/main/java/com/carrillo/n8n/`

**Pasos**:

1. **Copiar `LeadCapturedEvent.java`** (5 min)
   - Mismo package que client-service

2. **Crear `listener/LeadEventListener.java`** (45 min)
   - `@NatsListener(subject = "lead.capturado")`
   - Método `handleLeadCapturado(LeadCapturedEvent event)`
   - Transformar a formato n8n (Map con `event_type`, `lead_id`, etc.)
   - **Mapeo crítico**: `servicio` → `servicio_interes` (n8n espera este nombre)

3. **Método `sendToN8n()`** (30 min)
   - `RestTemplate.postForEntity(n8nWebhookUrl, payload, String.class)`
   - Headers: `Content-Type: application/json`
   - `@Retryable`: 3 intentos, backoff exponencial
   - Log success/error

4. **Configurar n8n webhook URL** (15 min)
   ```yaml
   n8n:
     webhook:
       lead-events: https://carrilloabgd.app.n8n.cloud/webhook/lead-events
   ```

5. **Testing** (30 min)
   - Crear lead en client-service
   - Verificar log "Successfully sent lead to n8n"
   - Verificar ejecución en n8n dashboard

**Entregable**: n8n-integration-service escucha NATS y llama n8n

---

#### Tarea 1.3: n8n-integration-service - Webhooks Callbacks (3h)

**Responsable**: Backend Dev  
**Archivos**: `n8n-integration-service/src/main/java/com/carrillo/n8n/`

**Pasos**:

1. **Crear DTOs** (20 min)
   ```java
   // dto/LeadScoredCallbackDTO.java
   @Data
   public class LeadScoredCallbackDTO {
       private String leadId;
       private Integer score;
       private String categoria;
   }

   // dto/HotLeadCallbackDTO.java
   @Data
   public class HotLeadCallbackDTO {
       private String leadId;
       private String nombre;
       private String email;
       private Integer score;
       private String urgency;
   }
   ```

2. **Crear `controller/N8nWebhookController.java`** (1h)
   - `POST /webhook/lead-scored`
     - Recibe `LeadScoredCallbackDTO`
     - Llama `clientServiceClient.updateLeadScore()`
     - Retorna 200 OK
   
   - `POST /webhook/lead-hot`
     - Recibe `HotLeadCallbackDTO`
     - Llama `notificationService.notifyHotLead()`
     - Crear tarea en case-service (opcional)
     - Retorna 200 OK

3. **Crear `client/ClientServiceClient.java`** (40 min)
   - Interface Feign o RestTemplate
   - `PATCH /client-service/api/leads/{id}/score`

4. **Crear `service/NotificationService.java`** (40 min)
   - Email a marketing@carrilloabgd.com
   - Subject: "🔥 LEAD HOT: [Nombre] - [Empresa]"
   - Body HTML con datos del lead

5. **Testing callbacks** (30 min)
   - curl POST /webhook/lead-scored con JSON
   - Verificar actualización en PostgreSQL
   - curl POST /webhook/lead-hot
   - Verificar email recibido

**Entregable**: n8n puede llamar callbacks y actualizar plataforma

---

#### Tarea 1.4: Testing E2E Completo (2h)

**Responsable**: Backend Dev + Marketing Dev

**Escenario de Prueba**:

```
1. Frontend: POST /client-service/api/leads
   Payload: {
     "nombre": "Test E2E",
     "email": "test@techcorp.co",
     "telefono": "+57 300 123 4567",
     "empresa": "TechCorp SAS",
     "servicio": "derecho-marcas",
     "mensaje": "Necesito registrar marca urgente para producto SaaS"
   }

2. Verificar: client-service guarda en PostgreSQL
   - Lead con estado=NEW, score=null

3. Verificar: client-service publica NATS
   - Log: "Published lead.capturado event for leadId: xxx"

4. Verificar: n8n-integration-service recibe NATS
   - Log: "Received lead.capturado event for leadId: xxx"

5. Verificar: n8n-integration-service llama n8n
   - Log: "Successfully sent lead to n8n. LeadId: xxx"

6. Verificar: n8n Orquestador ejecuta SUB-A
   - Dashboard n8n: Ejecución exitosa

7. Verificar: SUB-A analiza con Gemini
   - Score: 80 (empresa tech + mensaje detallado)
   - Categoría: HOT

8. Verificar: SUB-A guarda en Firestore
   - Documento con leadId existe

9. Verificar: SUB-A envía email al lead
   - Respuesta IA personalizada recibida

10. Verificar: SUB-A llama callback /webhook/lead-scored
    - n8n-integration-service recibe callback

11. Verificar: n8n-integration-service actualiza client-service
    - PATCH /api/leads/{id}/score

12. Verificar: PostgreSQL actualizado
    - Lead con score=80, categoria=HOT, estado=QUALIFIED

13. Verificar: Notificación HOT lead
    - Email a marketing@carrilloabgd.com recibido

CRITERIO DE ÉXITO: Todo el flujo en < 1 minuto
```

**Métricas de Validación**:
- Latencia total: < 60 segundos ✅
- Tasa de error: 0% ✅
- Lead en PostgreSQL con score: ✅
- Lead en Firestore: ✅
- Emails enviados (2): ✅

---

### 🎨 FASE 2: CALLBACKS EN SUB-A (2 horas)

**Responsable**: Marketing Dev (n8n)  
**Ubicación**: n8n Cloud, workflow SUB-A `RHj1TAqBazxNFriJ`

**Objetivo**: SUB-A debe llamar a la plataforma después de procesar el lead

#### Modificaciones en SUB-A

**ANTES** (Estado actual):
```
[6. Enviar Respuesta Lead (Gmail)]
    ↓
[FINAL. Resultado del Sub-Workflow]
```

**DESPUÉS** (Con callbacks):
```
[6. Enviar Respuesta Lead (Gmail)]
    ↓
[7. Callback: Lead Scored]  ← NUEVO
    HTTP Request POST
    URL: {{$env.BACKEND_URL}}/webhook/lead-scored
    Body: {
      "leadId": "{{$json.lead_id}}",
      "score": {{$json.score}},
      "categoria": "{{$json.categoria}}"
    }
    ↓
[8. IF: Es Lead HOT?]  ← NUEVO
    IF categoria === "HOT"
    ↓ [TRUE]
[9. Callback: Hot Lead]  ← NUEVO
    HTTP Request POST
    URL: {{$env.BACKEND_URL}}/webhook/lead-hot
    Body: {
      "leadId": "{{$json.lead_id}}",
      "nombre": "{{$json.nombre}}",
      "email": "{{$json.email}}",
      "score": {{$json.score}},
      "urgency": "high"
    }
    ↓
[FINAL. Resultado del Sub-Workflow]
```

#### Pasos de Implementación

1. **Configurar variable de entorno en n8n** (10 min)
   - Ir a Settings → Variables
   - Crear: `BACKEND_URL` = `https://api.carrilloabgd.com/n8n-integration-service`
   - Dev: `http://localhost:8800`

2. **Agregar nodo "7. Callback: Lead Scored"** (30 min)
   - Tipo: HTTP Request
   - Method: POST
   - URL: `{{$env.BACKEND_URL}}/webhook/lead-scored`
   - Body Type: JSON
   - Body:
     ```json
     {
       "leadId": "={{$('1. Validar y Clasificar').item.json.lead_id}}",
       "score": {{$('1. Validar y Clasificar').item.json.score}},
       "categoria": "={{$('1. Validar y Clasificar').item.json.categoria}}"
     }
     ```
   - Timeout: 10s
   - Retry: 2 veces

3. **Agregar nodo "8. IF: Es Lead HOT?"** (20 min)
   - Tipo: IF
   - Condition: 
     ```javascript
     {{$('1. Validar y Clasificar').item.json.categoria === "HOT"}}
     ```

4. **Agregar nodo "9. Callback: Hot Lead"** (30 min)
   - Conectar desde salida TRUE del IF
   - Tipo: HTTP Request
   - Method: POST
   - URL: `{{$env.BACKEND_URL}}/webhook/lead-hot`
   - Body:
     ```json
     {
       "leadId": "={{$('1. Validar y Clasificar').item.json.lead_id}}",
       "nombre": "={{$('0. Mapear Input').item.json.nombre}}",
       "email": "={{$('0. Mapear Input').item.json.email}}",
       "score": {{$('1. Validar y Clasificar').item.json.score}},
       "urgency": "high"
     }
     ```

5. **Actualizar nodo "FINAL"** (10 min)
   - Conectar desde salida FALSE del IF y desde nodo 9

6. **Testing** (30 min)
   - Ejecutar workflow con lead de prueba
   - Verificar logs en n8n
   - Verificar logs en n8n-integration-service
   - Verificar actualización en PostgreSQL

**Entregable**: SUB-A actualiza plataforma después de procesar

---

### 🔄 FASE 3: SUB-D NURTURING (4 horas)

**Responsable**: Marketing Dev (n8n)  
**Estado**: Nuevo workflow desde cero  
**Trigger**: Schedule (cada 6 horas)

#### Especificación Completa

**Nombre**: `SUB-D: Nurturing Sequence Engine`  
**Descripción**: Envía secuencia de 8-12 emails a leads WARM/COLD no convertidos

#### Flujo de Nodos (16 nodos estimados)

```
[1. Schedule Trigger]
    Every 6 hours (00:00, 06:00, 12:00, 18:00)
    ↓
[2. Query Firestore: Leads para Nurturing]
    Collection: leads
    Filters:
      - status IN ["new", "nurturing", "warm", "cold"]
      - status != "convertido"
      - status != "perdido"
      - emails_sent < 12
      - last_contact < NOW() - interval_days
    ↓
[3. IF: Hay leads para procesar?]
    IF items.length > 0
    ↓ [TRUE]
[4. Split In Batches]
    Batch size: 10
    ↓
[5. For Each Lead (Loop)]
    ↓
[6. Calcular Posición en Secuencia]
    Code node:
    - emails_sent + 1 = posición (1-12)
    - Calcular días desde captura
    - Verificar si debe enviarse según tabla de días
    ↓
[7. IF: Debe enviarse hoy?]
    Comparar días transcurridos con tabla de secuencia
    ↓ [TRUE]
[8. Obtener Template de Email]
    Code node: Retorna template según posición
    Subjects y estructuras predefinidas
    ↓
[9. Gemini: Generar Contenido Personalizado]
    Prompt: "Eres abogado experto en PI. Email {posición} de nurturing.
             Template: {template}
             Lead: {nombre}, {empresa}, {servicio_interes}
             Genera email personalizado, máximo 200 palabras"
    ↓
[10. Mailersend: Enviar Email]
    API: Mailersend
    From: marketing@carrilloabgd.com
    To: {{lead.email}}
    Subject: {{template.subject}}
    HTML: {{gemini_output}}
    Tracking: opens=true, clicks=true
    Tags: ["nurturing", "position-{{position}}", "{{categoria}}"]
    ↓
[11. Actualizar Firestore]
    Update document:
      - emails_sent: +1
      - last_contact: NOW()
      - status: "nurturing"
    ↓
[12. Registrar Log]
    Insert into collection: email_logs
    ↓
[Loop continúa para siguiente lead]
    ↓
[13. Consolidar Resultados]
    Set node: Cuenta enviados exitosos y fallidos
    ↓
[14. IF: Hubo errores?]
    IF failed_count > 0
    ↓ [TRUE]
[15. Notificar Errores]
    Email a marketing@: Resumen de errores
    ↓
[16. FINAL: Return Success]
    Status: success
    Sent: {{success_count}}
    Failed: {{failed_count}}
```

#### Tabla de Secuencia de Nurturing (12 emails)

| Posición | Día | Objetivo | Subject | Contenido |
|----------|-----|----------|---------|-----------|
| 1 | 0 | Bienvenida | "Gracias por contactarnos, {Nombre}" | Presentación firma + valor |
| 2 | 3 | Educativo | "¿Por qué proteger tu marca {Empresa}?" | Riesgos de no registrar |
| 3 | 7 | Case Study | "Cómo ayudamos a {similar_company}" | Historia éxito relevante |
| 4 | 10 | Recurso | "Checklist gratuito: Registro de marcas" | PDF descargable |
| 5 | 14 | Urgencia | "3 riesgos que enfrentas sin registro" | Casos reales negativos |
| 6 | 21 | Autoridad | "Dr. Carrillo en la SIC: 15 años" | Video testimonial |
| 7 | 28 | Oferta | "Consulta inicial GRATIS esta semana" | CTA claro |
| 8 | 35 | Re-engagement | "¿Sigues interesado en proteger tu marca?" | Pregunta directa |
| 9 | 42 | Tendencias | "Propiedad Intelectual en 2026: Lo que debes saber" | Contenido futuro |
| 10 | 49 | Last Chance | "Última oportunidad: Consulta gratuita" | Urgencia alta |
| 11 | 56 | Break-up | "¿Nos despedimos? (Por ahora)" | Email de despedida |
| 12 | 90 | Win-back | "Han pasado 3 meses... ¿Hablamos?" | Reactivación |

#### Código del Nodo 6 (Calcular Posición)

```javascript
const leads = $input.all();
const output = [];

const sequenceDays = [0, 3, 7, 10, 14, 21, 28, 35, 42, 49, 56, 90];

for (const lead of leads) {
  const emailsSent = lead.json.emails_sent || 0;
  const position = emailsSent + 1; // Próximo email a enviar
  
  if (position > 12) {
    // Lead completó secuencia
    continue;
  }
  
  // Calcular días desde captura
  const createdAt = new Date(lead.json.created_at);
  const now = new Date();
  const daysSinceCapture = Math.floor((now - createdAt) / (1000 * 60 * 60 * 24));
  
  // Día programado para este email
  const scheduledDay = sequenceDays[position - 1];
  
  // ¿Debe enviarse hoy? (tolerancia +/- 1 día)
  const shouldSendToday = Math.abs(daysSinceCapture - scheduledDay) <= 1;
  
  output.push({
    json: {
      ...lead.json,
      position: position,
      scheduled_day: scheduledDay,
      days_since_capture: daysSinceCapture,
      should_send_today: shouldSendToday
    }
  });
}

return output;
```

#### Código del Nodo 8 (Templates)

```javascript
const templates = {
  1: {
    subject: "Gracias por contactarnos, {{nombre}}",
    objective: "Bienvenida",
    structure: "Párrafo 1: Agradecer contacto\nPárrafo 2: Presentar firma\nPárrafo 3: Cómo podemos ayudar\nCTA: Agendar llamada"
  },
  2: {
    subject: "¿Por qué proteger tu marca {{empresa}}?",
    objective: "Educativo",
    structure: "Párrafo 1: Pregunta retórica\nPárrafo 2: 3 riesgos principales\nPárrafo 3: Beneficios del registro\nCTA: Descargar guía"
  },
  // ... más templates
};

const position = $json.position;
const template = templates[position] || templates[1];

return [{
  json: {
    ...$json,
    template_subject: template.subject,
    template_objective: template.objective,
    template_structure: template.structure
  }
}];
```

#### Configuración Mailersend

1. **Crear cuenta Mailersend** (si no existe)
   - https://www.mailersend.com/
   - Plan gratuito: 3,000 emails/mes

2. **Configurar dominio** (carrilloabgd.com)
   - Verificar DNS records
   - DKIM, SPF, DMARC

3. **Obtener API Key**
   - Dashboard → API Tokens → Create Token
   - Scope: Email Send + Email Analytics

4. **Configurar en n8n**
   - Credentials → New → Mailersend API
   - Pegar API Key

#### Testing SUB-D

```bash
# 1. Crear lead de prueba en Firestore con fechas antiguas
{
  "lead_id": "test-nurturing-001",
  "nombre": "Test Nurturing",
  "email": "tudev@gmail.com",
  "empresa": "Test Corp",
  "servicio_interes": "derecho-marcas",
  "categoria": "WARM",
  "status": "new",
  "score": 50,
  "emails_sent": 0,
  "created_at": "2026-01-01T00:00:00Z",  // ← 4 días atrás
  "last_contact": null
}

# 2. Ejecutar SUB-D manualmente en n8n
# Expected: Envía email posición 2 (día 3)

# 3. Verificar:
- Email recibido con contenido personalizado
- Firestore actualizado: emails_sent=1, last_contact=now
- email_logs tiene entrada
```

**Entregable**: SUB-D envía nurturing automatizado cada 6h

---

### 📊 FASE 4: SUB-E ENGAGEMENT TRACKER (2 horas - Opcional)

**Responsable**: Marketing Dev  
**Prioridad**: MEDIA (puede esperar a Fase 2 del proyecto)

**Objetivo**: Actualizar score del lead cuando abre/hace click en emails

#### Flujo Simplificado

```
[Webhook: Mailersend Events]
    POST /webhook-test/mailersend-events
    ↓
[Parse Event]
    Extraer: event_type, recipient_email, lead_id (custom variable)
    ↓
[Switch: Tipo de Evento]
    Cases: email.opened, email.clicked, email.bounced
    ↓
[Buscar Lead en Firestore]
    Query por email
    ↓
[Actualizar Métricas]
    opened: email_opens +1, score +5
    clicked: email_clicks +1, score +10
    ↓
[Recalcular Categoría]
    Si score >= 70 → cambiar a HOT
    ↓
[IF: Cambió a HOT?]
    ↓ [TRUE]
[Trigger SUB-B: Notificar HOT]
    Execute Workflow (futuro SUB-B)
    ↓
[Guardar en Firestore]
    Update lead + Insert engagement_log
```

**Entregable**: Tracking de engagement actualiza lead score

---

### 📅 FASE 5: SUB-F MEETING SCHEDULER (2 horas - Opcional)

**Responsable**: Marketing Dev  
**Prioridad**: MEDIA

**Objetivo**: Sincronizar cuando lead agenda cita en Calendly

#### Flujo Simplificado

```
[Webhook: Calendly Invitee Created]
    POST /webhook-test/calendly-events
    ↓
[Parse Calendly Payload]
    Extraer: email, start_time, meeting_link, name
    ↓
[Buscar Lead en Firestore]
    Query por email
    ↓
[Actualizar Lead]
    status: "reunion_agendada"
    meeting_scheduled_at: start_time
    score: +30 (bonus por agendar)
    ↓
[Preparar Detalles]
    Fecha: {{format_date(start_time)}}
    Link: {{meeting_link}}
    ↓
[Fork: 2 acciones paralelas]
    ├─► [Email a Dr. Carrillo]
    │   Subject: "🔥 LEAD HOT agendó reunión"
    │   Body: Datos del lead + fecha/hora
    │
    └─► [Email de Confirmación al Lead]
        Subject: "Confirmación reunión con Carrillo Abogados"
        Body: Detalles + agregar a calendar
    ↓
[Guardar en Firestore]
```

**Entregable**: Citas agendadas actualizan lead y notifican equipo

---

## 📊 MÉTRICAS DE ÉXITO GLOBAL MW#1

### KPIs Técnicos

| Métrica | Objetivo | Medición |
|---------|----------|----------|
| **Latencia E2E** | < 60 seg | Formulario → BD con score |
| **Tasa éxito Orquestador** | > 95% | Dashboard n8n |
| **Tasa éxito SUB-A** | > 90% | Dashboard n8n |
| **Tasa entrega email** | > 98% | Mailersend analytics |
| **Uptime n8n** | > 99% | n8n Cloud SLA |
| **Uptime NATS** | > 99.9% | Kubernetes metrics |

### KPIs de Negocio

| Métrica | Actual | Objetivo Post-MW#1 | Mejora |
|---------|-------:|-----------------:|-------:|
| Leads procesados/mes | 20 | 300+ | 15x |
| Tiempo primera respuesta | 4-24h | < 1 min | 1440x |
| Tasa conversión lead→cliente | 5% | 15% | 3x |
| Horas semanales gestión leads | 20+ | 5 | 4x menos |

---

## 🗓️ CRONOGRAMA SUGERIDO (1 Semana)

### Semana 1: Implementación Completa

| Día | Fase | Responsable | Entregables |
|-----|------|-------------|-------------|
| **Lunes** | FASE 1.1-1.2 | Backend Dev | client-service NATS + n8n-integration listener |
| **Martes** | FASE 1.3 | Backend Dev | Webhooks callbacks + testing parcial |
| **Miércoles** | FASE 1.4 + 2 | Ambos | Testing E2E + Callbacks SUB-A |
| **Jueves** | FASE 3 | Marketing Dev | SUB-D Nurturing completo |
| **Viernes** | Testing + Docs | Ambos | Validación final + documentación |

**Weekend**: FASE 4 y 5 opcionales si hay tiempo

---

## 🚨 RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| **n8n Cloud downtime** | Baja | Alto | Retry logic + queue en n8n-integration-service |
| **NATS desconectado** | Media | Alto | Health checks + alertas + reconexión automática |
| **Gemini API límite** | Media | Medio | Rate limiting + fallback a respuesta template |
| **Mailersend fallos** | Baja | Medio | Retry + log errors + alertas |
| **Payloads incompatibles** | Alta | Alto | Testing temprano + schemas validados |
| **Firestore cuota** | Baja | Bajo | Monitoreo + plan upgrade si necesario |

---

## ✅ CHECKLIST DE COMPLETITUD

### Backend

- [ ] client-service emite NATS `lead.capturado`
- [ ] client-service tiene endpoint PATCH `/leads/{id}/score`
- [ ] n8n-integration-service escucha NATS
- [ ] n8n-integration-service llama webhook n8n
- [ ] n8n-integration-service expone `/webhook/lead-scored`
- [ ] n8n-integration-service expone `/webhook/lead-hot`
- [ ] Testing E2E completo (formulario → PostgreSQL con score)
- [ ] Notificación email HOT leads funcionando
- [ ] Documentación actualizada (PROYECTO_ESTADO.md)

### n8n Workflows

- [ ] SUB-A llama callback `/webhook/lead-scored`
- [ ] SUB-A llama callback `/webhook/lead-hot` (solo HOT)
- [ ] SUB-D: Nurturing creado y activo (Schedule cada 6h)
- [ ] SUB-D: 12 templates de email definidos
- [ ] SUB-D: Mailersend configurado y enviando
- [ ] SUB-D: Firestore actualizándose correctamente
- [ ] SUB-E: Engagement Tracker (opcional)
- [ ] SUB-F: Meeting Scheduler (opcional)

### Testing y Validación

- [ ] Test unitario: client-service NATS publisher
- [ ] Test unitario: n8n-integration-service NATS listener
- [ ] Test integración: NATS → n8n webhook
- [ ] Test E2E: Formulario → n8n → PostgreSQL (< 1 min)
- [ ] Test E2E: Lead HOT → Notificación recibida
- [ ] Test SUB-D: Envío nurturing funcional
- [ ] Validación n8n: 0 errores en workflows
- [ ] Validación Firestore: Datos persistiendo correctamente

### Monitoreo

- [ ] Logs estructurados en todos los servicios
- [ ] Dashboard n8n: Executions monitoreadas
- [ ] Mailersend: Analytics configurado
- [ ] Alertas: Errores críticos notificando a marketing@
- [ ] Grafana: Dashboards NATS + microservicios (opcional)

---

## 📞 PRÓXIMOS PASOS INMEDIATOS

### Para Backend Dev (AHORA)

1. **Revisar código**: Leer `BACKEND_DEV_TASKS.md` completo
2. **Setup local**: Levantar NATS + PostgreSQL en Docker
3. **Implementar Tarea 1.1**: Evento NATS en client-service (2h)
4. **Testing parcial**: Verificar evento publicado correctamente
5. **Comunicar progreso**: Actualizar en chat cuando listo para Tarea 1.2

### Para Marketing Dev (AHORA)

1. **Revisar workflows actuales**: n8n Cloud dashboard
2. **Debuggear errores**: Investigar ejecuciones fallidas (50% tasa)
3. **Validar credenciales**: Gemini, Firestore, Gmail todas activas
4. **Preparar templates**: Escribir 12 subjects + estructuras para SUB-D
5. **Esperar backend**: Callbacks SUB-A requieren endpoints funcionando

### Para Coordinación (Día Miércoles)

- **Reunión sincronización**: 1 hora para testing conjunto
- **Validar payloads**: Asegurar compatibilidad n8n ↔ backend
- **Testing E2E**: Escenario completo con datos reales
- **Ajustes finales**: Corregir cualquier incompatibilidad encontrada

---

## 🎯 CRITERIO FINAL DE ÉXITO MW#1

> **MW#1 está 100% completo cuando**:
> 
> 1. ✅ Un visitante llena formulario `/contacto` en el sitio web
> 2. ✅ Lead se guarda en PostgreSQL con estado `NEW`
> 3. ✅ Evento NATS `lead.capturado` es publicado
> 4. ✅ n8n-integration-service recibe evento y llama n8n
> 5. ✅ n8n Orquestador ejecuta SUB-A
> 6. ✅ Gemini analiza y calcula score (0-100)
> 7. ✅ Lead guardado en Firestore con categoría (HOT/WARM/COLD)
> 8. ✅ Si HOT: Email notificación a marketing@carrilloabgd.com
> 9. ✅ Email respuesta IA personalizado enviado al lead
> 10. ✅ n8n llama callback `/webhook/lead-scored`
> 11. ✅ PostgreSQL actualizado con score y categoría
> 12. ✅ **TODO EN < 1 MINUTO** desde captura
> 13. ✅ SUB-D envía nurturing automático cada 6h a leads WARM/COLD
> 14. ✅ Tasa de error < 5%
> 15. ✅ Documentación actualizada

---

**Cuando esto funcione, habremos construido el motor de captura y conversión más avanzado que existe para un bufete legal en Colombia** 🚀

¿Listo para empezar? 🎯
