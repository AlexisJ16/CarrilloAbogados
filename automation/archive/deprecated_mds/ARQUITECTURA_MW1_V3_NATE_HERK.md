# 🏗️ ARQUITECTURA MW#1 - Metodología Nate Herk

**Fecha**: 6 de Enero, 2026
**Versión**: 3.0 (Rediseño arquitectónico)
**Metodología**: Nate Herk AI Systems Pyramid + Orchestrator Pattern
**Estado**: 🔴 REQUIERE REDISEÑO CRÍTICO

---

## 🚨 DECISIÓN ARQUITECTÓNICA CRÍTICA

### Problema Identificado

El **Orquestador actual** (bva1Kc1USbbITEAw) NO utiliza un **AI Agent Node** para routing inteligente. Está usando un nodo `Code` para identificación de eventos, lo cual es:

- ❌ **Poco escalable**: Cada nuevo tipo de evento requiere modificar código
- ❌ **Frágil**: Payload inesperados pueden romper la lógica
- ❌ **No inteligente**: No puede manejar ambigüedad o nuevos patrones

### Solución: Arquitectura Orchestrator con AI Agent

```
ANTES (Actual - Nivel 2: Workflow Automation)
────────────────────────────────────────────────
[Webhook] → [Code: Identify] → [Execute SUB-A] → [Respond]
              ❌ Lógica rígida

DESPUÉS (Nuevo - Nivel 4: AI Agent Orchestrator)
────────────────────────────────────────────────
[Webhook] → [AI Agent: Orchestrator] → [Dynamic Routing] → [Respond]
              ✅ Razonamiento flexible
              ✅ Tools: SUB-A, SUB-D, SUB-E, SUB-F
```

---

## 📊 PIRÁMIDE AI SYSTEMS - APLICADO A MW#1

### Clasificación de Componentes

| Componente | Nivel AI | Tipo | Justificación |
|------------|----------|------|---------------|
| **Orquestador** | **Nivel 4: AI Agent** | Orchestrator Agent | Debe decidir dinámicamente qué sub-workflow ejecutar según el contexto del evento |
| **SUB-A** | **Nivel 3: AI Workflow** | Deterministic + AI nodes | Flujo fijo: Validar → Scoring (Gemini) → Guardar → Notificar → Responder |
| **SUB-D** | **Nivel 3: AI Workflow** | Deterministic + AI nodes | Flujo fijo: Query Firestore → Calcular posición → Personalizar (Gemini) → Enviar |
| **SUB-E** | **Nivel 2: Workflow Automation** | Pure Logic | Flujo fijo: Webhook → Parse → Update Firestore → Callback |
| **SUB-F** | **Nivel 2: Workflow Automation** | Pure Logic | Flujo fijo: Webhook → Parse → Update Firestore → Notificar |

### ¿Por qué SUB-D es Nivel 3 y NO Nivel 4?

| Criterio | SUB-D Reality | Conclusión |
|----------|---------------|------------|
| **Secuencia de pasos** | FIJA: Query → Loop → Personalizar → Enviar → Update | ✅ Determinista |
| **Decisiones dinámicas** | Solo calcular posición en secuencia (1-12) | ❌ No requiere razonamiento |
| **Herramientas variables** | Siempre usa: Firestore + Gemini + Mailersend | ❌ No decide herramientas |
| **Costo** | Gemini solo para personalización (1 llamada/lead) | ✅ Bajo costo |
| **Mantenibilidad** | Alta (flujo predecible) | ✅ Fácil debug |

**Decisión**: SUB-D debe ser un **AI Workflow** (Nivel 3), NO un AI Agent (Nivel 4).

---

## 🎯 REDISEÑO DEL ORQUESTADOR

### Arquitectura Orchestrator (Parent-Child)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    ORQUESTADOR (AI Agent - Padre)                       │
│                                                                         │
│  [Webhook]                                                              │
│      ↓                                                                  │
│  [AI Agent Node]                                                        │
│   - LLM: Google Gemini 2.5 Pro (mejor integración ecosistema)          │
│   - System Prompt: "Eres un router inteligente de eventos..."          │
│   - Tools (Herramientas conectadas):                                    │
│      • SUB-A: Lead Intake (Execute Workflow Tool)                       │
│      • SUB-D: Nurturing Engine (Execute Workflow Tool)                  │
│      • SUB-E: Engagement Tracker (Execute Workflow Tool)                │
│      • SUB-F: Meeting Scheduler (Execute Workflow Tool)                 │
│   - Memory: Window Buffer Memory (últimos 5 eventos)                    │
│   - Options:                                                            │
│      • returnIntermediateSteps: true (observabilidad)                   │
│      • maxIterations: 3 (evitar bucles)                                 │
│      ↓                                                                  │
│  [Respond to Webhook]                                                   │
│      ↓                                                                  │
│  [Logger: Google Sheets] (Track: tokens, tool_used, latency)           │
└─────────────────────────────────────────────────────────────────────────┘
          │                  │                  │                  │
          ▼                  ▼                  ▼                  ▼
    ┌──────────┐      ┌──────────┐      ┌──────────┐      ┌──────────┐
    │  SUB-A   │      │  SUB-D   │      │  SUB-E   │      │  SUB-F   │
    │  Intake  │      │ Nurturing│      │Engagement│      │ Meeting  │
    └──────────┘      └──────────┘      └──────────┘      └──────────┘
```

### System Prompt del AI Agent Orchestrator

```markdown
# Rol
Eres el **Lead Lifecycle Orchestrator** de Carrillo Abogados. Tu única función es identificar el tipo de evento entrante y ejecutar el sub-workflow correspondiente.

# Contexto de Negocio
Carrillo Abogados es un bufete legal especializado en Propiedad Intelectual. Procesamos 300+ leads/mes.

# Herramientas Disponibles
1. **SUB-A: Lead Intake** - Ejecutar cuando llega un nuevo lead desde el formulario web
2. **SUB-D: Nurturing Engine** - Ejecutar manualmente para procesar batch de nurturing
3. **SUB-E: Engagement Tracker** - Ejecutar cuando Mailersend reporta open/click
4. **SUB-F: Meeting Scheduler** - Ejecutar cuando Google Calendar reporta nueva reunión

# Reglas de Decisión
- Si el payload contiene `event_type: "new_lead"` → Ejecutar SUB-A
- Si el payload contiene `event_type: "email_opened" | "email_clicked"` → Ejecutar SUB-E
- Si el payload contiene `event_type: "meeting_booked"` → Ejecutar SUB-F
- Si el payload contiene `event_type: "nurturing_manual_trigger"` → Ejecutar SUB-D
- Si el payload es ambiguo o desconocido → Responder error claro al webhook

# Observabilidad
SIEMPRE devuelve en tu respuesta:
- `tool_used`: Nombre del sub-workflow ejecutado
- `decision_reason`: 1 frase explicando por qué elegiste ese tool
- `execution_status`: "success" | "error"
```

### Nodos del Orquestador (v3.0)

| # | Nodo | Tipo | Propósito |
|---|------|------|-----------|
| 1 | **Webhook Principal** | `webhook` | Recibe POST /lead-events |
| 2 | **AI Agent Orchestrator** | `n8n-nodes-langchain.agent` | Router inteligente con Claude/GPT |
| 3 | **Respond to Webhook** | `respondToWebhook` | HTTP 200 con resultado |
| 4 | **Logger (Google Sheets)** | `googleSheets` | Append: timestamp, event_type, tool_used, tokens, latency |
| 5 | **Error Handler** | `errorTrigger` | Captura errores y notifica a ingenieria@ |

**Total nodos**: 5 (vs 8 actuales - más simple)

### Tools del AI Agent

Cada sub-workflow se conecta como una **herramienta** usando el nodo `Execute Workflow`:

```json
{
  "node": "n8n-nodes-base.executeWorkflow",
  "name": "SUB-A: Lead Intake",
  "parameters": {
    "workflowId": "RHj1TAqBazxNFriJ",
    "waitForCompletion": true,
    "source": {
      "parameter": "workflowId"
    }
  },
  "description": "Procesa nuevo lead: validación, scoring IA, guardado Firestore, notificación HOT, respuesta automática"
}
```

**Configuración clave**:
- `waitForCompletion: true` → Esperar resultado antes de responder webhook
- `description` → El AI Agent usa esto para decidir cuándo invocar

---

## 🔧 REDISEÑO DE SUB-D: NURTURING SEQUENCE

### Decisión: AI Workflow (Nivel 3), NO AI Agent

**Arquitectura**: Flujo determinista con nodo Gemini para personalización.

### Flujo de Nodos (16 nodos total)

```
[1. Schedule Trigger: Every 6h]
    ↓
[2. Calcular Timestamp Actual]
    Code: const now = new Date().toISOString()
    ↓
[3. Query Firestore: Leads para Nurturing]
    Collection: leads
    Where: status IN ["nuevo", "nurturing"]
      AND emails_sent < 12
      AND next_email_date <= NOW
    Limit: 10
    ↓
[4. ¿Hay Leads?]
    IF: {{ $json.data.length > 0 }}
    ├─ TRUE → Continuar
    └─ FALSE → [16. Exit: No hay leads]
    ↓
[5. Loop: Split In Batches]
    Batch Size: 1 (procesar de a uno)
    ↓
    ┌───────────────────────────────────────┐
    │  DENTRO DEL LOOP (por cada lead)      │
    │                                       │
    │  [6. Extraer Datos Lead]              │
    │      Set: lead_id, nombre, email,     │
    │           empresa, servicio,          │
    │           emails_sent, created_at     │
    │      ↓                                │
    │  [7. Calcular Posición en Secuencia]  │
    │      Code: JavaScript (ver abajo)     │
    │      Output: position (1-12),         │
    │               days_since_capture      │
    │      ↓                                │
    │  [8. Cargar Template Email]           │
    │      Code: templates[position]        │
    │      Output: subject_template,        │
    │               content_structure       │
    │      ↓                                │
    │  [9. Personalizar con Gemini]         │
    │      Google Gemini 2.5 Pro            │
    │      Prompt: (ver abajo)              │
    │      Max tokens: 300                  │
    │      ↓                                │
    │  [10. Validar Output Gemini]          │
    │      Code: Verificar que email tenga  │
    │            subject + body             │
    │      ↓                                │
    │  [11. Enviar Email con Mailersend]    │
    │      To: {{ $json.email }}            │
    │      Subject: {{ $json.subject }}     │
    │      Body: {{ $json.body }}           │
    │      Tracking: opens=true, clicks=true│
    │      Tags: ["nurturing", "pos-X"]     │
    │      ↓                                │
    │  [12. ¿Envío Exitoso?]                │
    │      IF: statusCode === 200           │
    │      ├─ TRUE → Actualizar Firestore   │
    │      └─ FALSE → Registrar Error       │
    │      ↓                                │
    │  [13. Actualizar Firestore]           │
    │      Update document lead_id:         │
    │        emails_sent: +1                │
    │        last_contact: NOW              │
    │        status: "nurturing"            │
    │        nurturing_position: position   │
    │        next_email_date: NOW + X días  │
    │      ↓                                │
    │  [14. Callback a Backend]             │
    │      HTTP POST /webhook/nurturing-sent│
    │      Payload: { lead_id, position,    │
    │                 sent_at, status }     │
    │      onError: continueRegularOutput   │
    └───────────────────────────────────────┘
    ↓
[15. Consolidar Resultados]
    Set: total_processed, total_sent, errors
    ↓
[16. Exit Workflow]
```

### JavaScript: Calcular Posición en Secuencia

```javascript
// Nodo 7: Calcular Posición en Secuencia
const lead = $input.item.json;

const createdAt = new Date(lead.created_at);
const now = new Date();
const daysSinceCapture = Math.floor((now - createdAt) / (1000 * 60 * 60 * 24));

// Mapeo de días a posición en secuencia
const emailSchedule = [
  { position: 1, minDay: 0, maxDay: 2 },      // Día 0 (inmediato)
  { position: 2, minDay: 3, maxDay: 5 },      // Día 3
  { position: 3, minDay: 7, maxDay: 9 },      // Día 7
  { position: 4, minDay: 10, maxDay: 13 },    // Día 10
  { position: 5, minDay: 14, maxDay: 17 },    // Día 14
  { position: 6, minDay: 21, maxDay: 24 },    // Día 21
  { position: 7, minDay: 28, maxDay: 31 },    // Día 28
  { position: 8, minDay: 35, maxDay: 38 },    // Día 35
  { position: 9, minDay: 42, maxDay: 45 },    // Día 42
  { position: 10, minDay: 49, maxDay: 52 },   // Día 49
  { position: 11, minDay: 56, maxDay: 59 },   // Día 56
  { position: 12, minDay: 90, maxDay: 999 }   // Día 90
];

// Encontrar posición según días transcurridos
let position = null;
for (const schedule of emailSchedule) {
  if (daysSinceCapture >= schedule.minDay && daysSinceCapture <= schedule.maxDay) {
    position = schedule.position;
    break;
  }
}

// Si ya pasaron más de 90 días y emails_sent < 12, enviar email 12
if (!position && daysSinceCapture >= 90 && lead.emails_sent < 12) {
  position = 12;
}

// Si no debe enviar email hoy, marcar skip
const shouldSend = position !== null && lead.emails_sent < position;

return {
  json: {
    lead_id: lead.lead_id,
    nombre: lead.nombre,
    email: lead.email,
    empresa: lead.empresa,
    servicio: lead.servicio_interes,
    position: position || lead.emails_sent + 1,
    days_since_capture: daysSinceCapture,
    emails_sent: lead.emails_sent,
    should_send: shouldSend
  }
};
```

### Templates de Email (12 posiciones)

```javascript
// Nodo 8: Cargar Template Email
const position = $json.position;

const templates = {
  1: {
    subject: "Gracias por contactarnos, {{nombre}}",
    objective: "Bienvenida",
    structure: "Saludo personalizado + Presentación firma (15 años SIC) + Valor que ofrecemos + CTA: Agendar llamada exploratoria",
    max_words: 200
  },
  2: {
    subject: "¿Por qué proteger tu marca {{empresa}}?",
    objective: "Educativo",
    structure: "Riesgos de no registrar marca + Casos reales de pérdida de marca + Beneficios del registro + CTA: Descargar checklist gratuito",
    max_words: 250
  },
  3: {
    subject: "Cómo ayudamos a empresas como {{empresa}}",
    objective: "Case Study",
    structure: "Historia de cliente similar (tech/startup) + Problema que tenían + Solución Carrillo + Resultado cuantificable + CTA: Ver más casos de éxito",
    max_words: 300
  },
  4: {
    subject: "Checklist gratuito: Registro de marcas en Colombia",
    objective: "Recurso de valor",
    structure: "Introducción del checklist + 5 pasos principales + Link descarga + CTA: ¿Necesitas ayuda con algún paso?",
    max_words: 200
  },
  5: {
    subject: "3 riesgos que enfrenta {{empresa}} sin registro",
    objective: "Urgencia",
    structure: "Riesgo 1: Demandas + Riesgo 2: Pérdida de mercado + Riesgo 3: Inversión perdida en branding + CTA: Protege tu marca ahora",
    max_words: 250
  },
  6: {
    subject: "Dr. Carrillo en la SIC: 15 años de experiencia",
    objective: "Autoridad",
    structure: "Trayectoria Dr. Omar Carrillo + Experiencia en SIC + Casos ganados + Testimoniales + CTA: Agenda una consulta",
    max_words: 300
  },
  7: {
    subject: "Consulta inicial GRATIS esta semana, {{nombre}}",
    objective: "Oferta",
    structure: "Oferta exclusiva: 30 min consulta gratuita + Qué cubrimos + Valor de la consulta ($XXX) + CTA: Agendar ahora (link Calendly)",
    max_words: 200
  },
  8: {
    subject: "¿Sigues interesado en proteger tu marca?",
    objective: "Re-engagement",
    structure: "Recordar contacto inicial + Entender si sigue siendo prioridad + Ofrecer ayuda específica + CTA: Responder este email",
    max_words: 150
  },
  9: {
    subject: "Propiedad Intelectual en 2026: Lo que debes saber",
    objective: "Tendencias",
    structure: "Cambios legislativos 2026 + Nuevas oportunidades PI + Riesgos emergentes + CTA: Mantente protegido",
    max_words: 300
  },
  10: {
    subject: "Última oportunidad: Consulta gratuita para {{empresa}}",
    objective: "Last Chance",
    structure: "Última oportunidad de agendar gratis + Recordar beneficios + Fecha límite + CTA urgente: Agendar hoy",
    max_words: 200
  },
  11: {
    subject: "¿Nos despedimos? (Por ahora)",
    objective: "Break-up",
    structure: "Reconocer que no ha habido respuesta + Respetar decisión + Dejar puerta abierta + CTA: Si cambias de opinión, estamos aquí",
    max_words: 150
  },
  12: {
    subject: "Han pasado 3 meses, {{nombre}}... ¿hablamos?",
    objective: "Win-back",
    structure: "Recordar contacto + Nueva oferta o recurso + Entender si situación cambió + CTA: Reconectar",
    max_words: 200
  }
};

return {
  json: {
    ...($json),
    template: templates[position]
  }
};
```

### Prompt Gemini para Personalización

```markdown
# Nodo 9: Personalizar con Gemini

## System Message (en nodo Gemini)
Eres un copywriter experto en email marketing legal para Carrillo Abogados (bufete especializado en Propiedad Intelectual en Colombia).

## User Message (en nodo Gemini)
Genera un email de nurturing para el siguiente lead:

**Datos del Lead:**
- Nombre: {{ $json.nombre }}
- Empresa: {{ $json.empresa }}
- Servicio de interés: {{ $json.servicio }}
- Posición en secuencia: {{ $json.position }} de 12
- Días desde captura: {{ $json.days_since_capture }}

**Template a seguir:**
- Subject: {{ $json.template.subject }}
- Objetivo: {{ $json.template.objective }}
- Estructura: {{ $json.template.structure }}
- Máximo palabras: {{ $json.template.max_words }}

**Instrucciones:**
1. Personaliza el email usando el nombre y empresa del lead
2. Mantén un tono profesional pero cercano
3. Incluye un CTA claro y accionable
4. NO uses emojis
5. Firma: "Dr. Omar Carrillo\nCarrillo Abogados\nTel: +57 2 XXX XXXX"

**Output esperado (JSON):**
{
  "subject": "...",
  "body": "..."
}
```

### Configuración Mailersend (Nodo 11)

```json
{
  "node": "n8n-nodes-base.httpRequest",
  "name": "Enviar Email con Mailersend",
  "parameters": {
    "method": "POST",
    "url": "https://api.mailersend.com/v1/email",
    "authentication": "predefinedCredentialType",
    "nodeCredentialType": "mailersendApi",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {
          "name": "Content-Type",
          "value": "application/json"
        }
      ]
    },
    "sendBody": true,
    "bodyParameters": {
      "parameters": [
        {
          "name": "from",
          "value": {
            "email": "marketing@carrilloabgd.com",
            "name": "Dr. Omar Carrillo - Carrillo Abogados"
          }
        },
        {
          "name": "to",
          "value": [
            {
              "email": "={{ $json.email }}",
              "name": "={{ $json.nombre }}"
            }
          ]
        },
        {
          "name": "subject",
          "value": "={{ $json.subject }}"
        },
        {
          "name": "html",
          "value": "={{ $json.body }}"
        },
        {
          "name": "tags",
          "value": [
            "nurturing",
            "position-={{ $json.position }}",
            "mw1-sub-d"
          ]
        },
        {
          "name": "settings",
          "value": {
            "track_clicks": true,
            "track_opens": true,
            "track_content": false
          }
        }
      ]
    },
    "options": {
      "timeout": 30000,
      "retry": {
        "enabled": true,
        "maxTries": 3,
        "waitBetweenTries": 1000
      }
    }
  }
}
```

---

## 📋 PLAN DE MIGRACIÓN

### Fase 1: Rediseñar Orquestador (4 horas)

| Paso | Tarea | Responsable | Tiempo |
|------|-------|-------------|--------|
| 1 | Crear copia de seguridad del Orquestador actual | Engineer | 15 min |
| 2 | Crear nuevo workflow "Orquestador v3.0" | Engineer | 30 min |
| 3 | Configurar nodo AI Agent con Google Gemini 2.5 Pro | Engineer | 1 hora |
| 4 | Conectar SUB-A como Tool (Execute Workflow) | Engineer | 30 min |
| 5 | Configurar System Prompt según specs | Engineer | 45 min |
| 6 | Configurar Logger en Google Sheets | Engineer | 30 min |
| 7 | Testing E2E con payloads de prueba | QA | 30 min |

### Fase 2: Implementar SUB-D (6 horas)

| Paso | Tarea | Responsable | Tiempo |
|------|-------|-------------|--------|
| 1 | Crear workflow SUB-D desde cero | Engineer | 1 hora |
| 2 | Implementar nodos 1-5 (Trigger hasta Loop) | Engineer | 1 hora |
| 3 | Implementar JavaScript de cálculo de posición | Engineer | 1 hora |
| 4 | Configurar templates de 12 emails | Engineer | 1 hora |
| 5 | Configurar nodo Gemini con prompt | Engineer | 30 min |
| 6 | Configurar Mailersend | Engineer | 30 min |
| 7 | Implementar actualización Firestore | Engineer | 30 min |
| 8 | Implementar callback a backend | Engineer | 30 min |
| 9 | Testing con lead de prueba | QA | 30 min |

### Fase 3: Actualizar SUB-A para Firestore (2 horas)

| Paso | Tarea | Responsable | Tiempo |
|------|-------|-------------|--------|
| 1 | Modificar nodo Firestore de SUB-A | Engineer | 30 min |
| 2 | Agregar campos: status, emails_sent, nurturing_position, next_email_date, created_at | Engineer | 30 min |
| 3 | Calcular next_email_date (now + 3 días) | Engineer | 30 min |
| 4 | Testing E2E | QA | 30 min |

### Fase 4: Conectar SUB-D al Orquestador (1 hora)

| Paso | Tarea | Responsable | Tiempo |
|------|-------|-------------|--------|
| 1 | Agregar SUB-D como Tool en AI Agent | Engineer | 20 min |
| 2 | Actualizar System Prompt con reglas SUB-D | Engineer | 20 min |
| 3 | Testing trigger manual de nurturing | QA | 20 min |

---

## 🎯 CRITERIOS DE ÉXITO

### Métricas Técnicas

| Métrica | Target | Medición |
|---------|--------|----------|
| Latencia Orquestador (AI Agent decision) | < 3 seg | Logger Google Sheets |
| Latencia SUB-D (por lead) | < 8 seg | n8n execution time |
| Tasa de éxito envío Mailersend | > 95% | Nodo 12 (IF) |
| Costo promedio por lead procesado | < $0.05 USD | Logger tokens |
| Precisión routing Orquestador | 100% | Testing manual |

### Métricas de Negocio

| Métrica | Target | Fuente |
|---------|--------|--------|
| Emails nurturing enviados/semana | 40-50 | Firestore aggregate |
| Tasa de apertura emails | > 25% | Mailersend dashboard |
| Tasa de click emails | > 5% | Mailersend dashboard |
| Leads WARM → HOT (por engagement) | 3-5/mes | SUB-E (futuro) |

---

## 📚 ANEXOS

### A. Estructura de Datos Firestore (Ampliada)

```json
{
  // Campos existentes de SUB-A
  "lead_id": "uuid",
  "nombre": "string",
  "email": "string",
  "empresa": "string",
  "servicio": "string",
  "score": 75,
  "categoria": "HOT|WARM|COLD",
  "processed_at": "timestamp",

  // Campos NUEVOS para nurturing
  "status": "nuevo",  // nuevo | nurturing | mql | sql | convertido | churned
  "emails_sent": 0,
  "emails_opened": 0,
  "emails_clicked": 0,
  "last_contact": "timestamp|null",
  "last_engagement": "timestamp|null",
  "nurturing_position": 1,
  "next_email_date": "2026-01-09T00:00:00Z",
  "created_at": "2026-01-06T10:30:00Z"
}
```

### B. Webhook Payloads

#### new_lead
```json
{
  "event_type": "new_lead",
  "nombre": "María Test",
  "email": "maria@test.com",
  "empresa": "Test Corp",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesito proteger mi marca"
}
```

#### nurturing_manual_trigger
```json
{
  "event_type": "nurturing_manual_trigger",
  "trigger_by": "admin",
  "timestamp": "2026-01-06T12:00:00Z"
}
```

### C. Referencias

| Documento | Ubicación |
|-----------|-----------|
| Metodología Nate Herk | Este documento - Sección 1 |
| Arquitectura de Datos | `ARQUITECTURA_DATOS_N8N.md` |
| STATUS MW#1 | `STATUS.md` |
| Tareas Marketing Dev | `TAREAS_MARKETING_DEV_MW1.md` |

---

**Autor**: Arquitecto n8n
**Revisión requerida**: @orchestrator, @engineer, @qa-specialist
**Próxima acción**: Aprobar rediseño y comenzar Fase 1
