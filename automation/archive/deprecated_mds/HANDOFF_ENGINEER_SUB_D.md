# 🛠️ HANDOFF PARA ENGINEER - Implementación SUB-D

**Fecha**: 6 de Enero, 2026
**De**: Arquitecto n8n
**Para**: @engineer
**Estimado**: 6 horas de implementación

---

## 📋 TU MISIÓN

Implementar el **SUB-D: Nurturing Sequence Engine** según las especificaciones arquitectónicas completas en [ARQUITECTURA_MW1_V3_NATE_HERK.md](ARQUITECTURA_MW1_V3_NATE_HERK.md).

---

## ✅ PRE-REQUISITOS (Verificar ANTES de empezar)

### Cuentas y Credenciales

- [ ] Cuenta Mailersend creada (free tier: 3,000 emails/mes)
- [ ] Dominio `carrilloabgd.com` verificado en Mailersend
- [ ] API Key de Mailersend obtenida
- [ ] Credencial Mailersend configurada en n8n Cloud
- [ ] Credencial Google Gemini 2.5 Pro activa (ya existe: `jk2FHcbAC71LuRl2`)
- [ ] Credencial Google Firestore activa (ya existe: `AAhdRNGzvsFnYN9O`)

### Variable de Entorno

- [ ] Variable `BACKEND_URL` configurada en n8n Cloud Settings → Variables
  - Desarrollo: `http://localhost:8800`
  - Producción: `https://api.carrilloabgd.com/n8n-integration-service`

### Testing

- [ ] Lead de prueba en Firestore con estructura correcta
- [ ] Email de prueba configurado (tu email personal para recibir test)

---

## 🏗️ IMPLEMENTACIÓN PASO A PASO

### PASO 1: Crear Workflow Base (15 min)

1. En n8n Cloud, crear nuevo workflow:
   - Nombre: `SUB-D: Nurturing Sequence Engine`
   - Folder: `MEGA-WORKFLOW-1`
   - Tags: `PRODUCCION`, `MEGA-WORKFLOW-1`, `NURTURING`

2. Configurar settings del workflow:
   - Timezone: `America/Bogota`
   - Error Workflow: (usar mismo error handler del Orquestador)
   - Execution Order: `v1`

### PASO 2: Nodos 1-5 (Setup y Query) - 45 min

#### Nodo 1: Schedule Trigger

```json
{
  "name": "Schedule Trigger: Every 6h",
  "type": "n8n-nodes-base.scheduleTrigger",
  "parameters": {
    "rule": {
      "interval": [
        {
          "field": "hours",
          "hoursInterval": 6
        }
      ]
    }
  },
  "position": [240, 300]
}
```

#### Nodo 2: Calcular Timestamp

```json
{
  "name": "Calcular Timestamp Actual",
  "type": "n8n-nodes-base.set",
  "parameters": {
    "mode": "manual",
    "duplicateItem": false,
    "assignments": {
      "assignments": [
        {
          "name": "now",
          "type": "string",
          "value": "={{ new Date().toISOString() }}"
        }
      ]
    }
  },
  "position": [460, 300]
}
```

#### Nodo 3: Query Firestore

**IMPORTANTE**: Esta es la query más crítica del workflow.

```json
{
  "name": "Query Firestore: Leads para Nurturing",
  "type": "n8n-nodes-base.googleFirestore",
  "parameters": {
    "operation": "getAll",
    "projectId": "carrillo-marketing-core",
    "collection": "leads",
    "filters": {
      "filters": [
        {
          "field": "status",
          "operator": "in",
          "value": ["nuevo", "nurturing"]
        },
        {
          "field": "emails_sent",
          "operator": "<",
          "value": 12
        },
        {
          "field": "next_email_date",
          "operator": "<=",
          "value": "={{ $json.now }}"
        }
      ]
    },
    "sort": {
      "field": "next_email_date",
      "direction": "ASCENDING"
    },
    "limit": 10,
    "simplify": true
  },
  "position": [680, 300]
}
```

**Troubleshooting**:
- Si error "permission denied": Verificar credencial Firestore
- Si no retorna datos: Verificar que existan leads con `next_email_date` en el pasado

#### Nodo 4: IF ¿Hay Leads?

```json
{
  "name": "IF: ¿Hay Leads?",
  "type": "n8n-nodes-base.if",
  "typeVersion": 2.3,
  "parameters": {
    "conditions": {
      "options": {
        "caseSensitive": true,
        "leftValue": "",
        "typeValidation": "strict"
      },
      "conditions": [
        {
          "leftValue": "={{ $json.data.length }}",
          "rightValue": 0,
          "operator": {
            "type": "number",
            "operation": "gt"
          }
        }
      ],
      "combinator": "and"
    }
  },
  "position": [900, 300]
}
```

**Conexiones**:
- TRUE → Continuar a Nodo 5
- FALSE → Saltar a Nodo 16 (Exit)

#### Nodo 5: Loop - Split In Batches

```json
{
  "name": "Loop: Split In Batches",
  "type": "n8n-nodes-base.splitInBatches",
  "parameters": {
    "batchSize": 1,
    "options": {
      "reset": false
    }
  },
  "position": [1120, 300]
}
```

---

### PASO 3: Nodos 6-8 (Preparación de Email) - 1 hora

#### Nodo 6: Extraer Datos Lead

```json
{
  "name": "Extraer Datos Lead",
  "type": "n8n-nodes-base.set",
  "parameters": {
    "mode": "manual",
    "duplicateItem": false,
    "assignments": {
      "assignments": [
        {
          "name": "lead_id",
          "type": "string",
          "value": "={{ $json.lead_id }}"
        },
        {
          "name": "nombre",
          "type": "string",
          "value": "={{ $json.nombre }}"
        },
        {
          "name": "email",
          "type": "string",
          "value": "={{ $json.email }}"
        },
        {
          "name": "empresa",
          "type": "string",
          "value": "={{ $json.empresa || 'su empresa' }}"
        },
        {
          "name": "servicio",
          "type": "string",
          "value": "={{ $json.servicio || 'servicios legales' }}"
        },
        {
          "name": "emails_sent",
          "type": "number",
          "value": "={{ $json.emails_sent }}"
        },
        {
          "name": "created_at",
          "type": "string",
          "value": "={{ $json.created_at }}"
        }
      ]
    }
  },
  "position": [1340, 300]
}
```

#### Nodo 7: Calcular Posición en Secuencia

**CRÍTICO**: Este JavaScript es el corazón del sistema de nurturing.

```javascript
// ===================================================================
// Nodo 7: Calcular Posición en Secuencia
// Propósito: Determinar qué email enviar según días transcurridos
// ===================================================================

const lead = $input.item.json;

// Parsear fecha de creación
const createdAt = new Date(lead.created_at);
const now = new Date();

// Calcular días transcurridos desde captura
const diffMs = now - createdAt;
const daysSinceCapture = Math.floor(diffMs / (1000 * 60 * 60 * 24));

// Mapeo de días a posición en secuencia (1-12)
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

// Si no encontró posición pero ya pasaron 90+ días y emails_sent < 12
if (!position && daysSinceCapture >= 90 && lead.emails_sent < 12) {
  position = 12;
}

// Si no debe enviar email hoy, marcar skip
const shouldSend = position !== null && lead.emails_sent < position;

// Calcular siguiente fecha de email
const nextEmailDelays = {
  1: 3, 2: 4, 3: 3, 4: 4, 5: 7, 6: 7,
  7: 7, 8: 7, 9: 7, 10: 7, 11: 34, 12: null
};

const delay = nextEmailDelays[position];
const nextEmailDate = delay
  ? new Date(Date.now() + delay * 24 * 60 * 60 * 1000).toISOString()
  : null;

// Return
return {
  json: {
    ...lead,
    position: position || lead.emails_sent + 1,
    days_since_capture: daysSinceCapture,
    should_send: shouldSend,
    next_email_date: nextEmailDate
  }
};
```

**Testing**:
- Lead con created_at hace 3 días → position = 2
- Lead con created_at hace 7 días → position = 3
- Lead con created_at hace 100 días → position = 12

#### Nodo 8: Cargar Template Email

**CRÍTICO**: Aquí están los 12 templates de la secuencia.

```javascript
// ===================================================================
// Nodo 8: Cargar Template Email
// Propósito: Obtener estructura del email según posición
// ===================================================================

const position = $json.position;
const nombre = $json.nombre;
const empresa = $json.empresa;

const templates = {
  1: {
    subject: `Gracias por contactarnos, ${nombre}`,
    objective: "Bienvenida",
    structure: "Saludo personalizado + Presentación firma (15 años SIC) + Valor que ofrecemos + CTA: Agendar llamada exploratoria",
    max_words: 200
  },
  2: {
    subject: `¿Por qué proteger tu marca ${empresa}?`,
    objective: "Educativo",
    structure: "Riesgos de no registrar marca + Casos reales de pérdida de marca + Beneficios del registro + CTA: Descargar checklist gratuito",
    max_words: 250
  },
  3: {
    subject: `Cómo ayudamos a empresas como ${empresa}`,
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
    subject: `3 riesgos que enfrenta ${empresa} sin registro`,
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
    subject: `Consulta inicial GRATIS esta semana, ${nombre}`,
    objective: "Oferta",
    structure: "Oferta exclusiva: 30 min consulta gratuita + Qué cubrimos + Valor de la consulta + CTA: Agendar ahora",
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
    subject: `Última oportunidad: Consulta gratuita para ${empresa}`,
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
    subject: `Han pasado 3 meses, ${nombre}... ¿hablamos?`,
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

---

### PASO 4: Nodos 9-10 (Personalización IA) - 45 min

#### Nodo 9: Gemini Personalización

**Configuración del nodo Google Gemini**:

```json
{
  "name": "Personalizar Email con Gemini",
  "type": "n8n-nodes-base.googleGemini",
  "parameters": {
    "model": "gemini-2.5-pro",
    "temperature": 0.7,
    "maxTokens": 300,
    "systemMessage": "Eres un copywriter experto en email marketing legal para Carrillo Abogados, un bufete especializado en Propiedad Intelectual en Colombia.",
    "prompt": "=Genera un email de nurturing para el siguiente lead:\n\n**Datos del Lead:**\n- Nombre: {{ $json.nombre }}\n- Empresa: {{ $json.empresa }}\n- Servicio de interés: {{ $json.servicio }}\n- Posición en secuencia: {{ $json.position }} de 12\n- Días desde captura: {{ $json.days_since_capture }}\n\n**Template a seguir:**\n- Subject: {{ $json.template.subject }}\n- Objetivo: {{ $json.template.objective }}\n- Estructura: {{ $json.template.structure }}\n- Máximo palabras: {{ $json.template.max_words }}\n\n**Instrucciones:**\n1. Personaliza el email usando el nombre y empresa del lead\n2. Mantén un tono profesional pero cercano\n3. Incluye un CTA claro y accionable\n4. NO uses emojis\n5. Firma: \"Dr. Omar Carrillo\\nCarrillo Abogados\\nTel: +57 2 XXX XXXX\"\n\n**Output esperado (JSON):**\n{\n  \"subject\": \"...\",\n  \"body\": \"...\"\n}",
    "options": {
      "timeout": 30000
    }
  },
  "position": [1560, 300],
  "credentials": {
    "googleGeminiApi": {
      "id": "jk2FHcbAC71LuRl2",
      "name": "Google Gemini API"
    }
  }
}
```

**Retry configuration** (en nodo settings):
- Max retries: 2
- Wait between retries: 5000ms

#### Nodo 10: Validar Output Gemini

```javascript
// ===================================================================
// Nodo 10: Validar Output Gemini
// Propósito: Asegurar que Gemini generó email válido
// ===================================================================

const geminiOutput = $json;

// Intentar parsear si viene como string
let emailData;
try {
  emailData = typeof geminiOutput === 'string'
    ? JSON.parse(geminiOutput)
    : geminiOutput;
} catch (e) {
  throw new Error('Gemini output no es JSON válido');
}

// Validar campos requeridos
if (!emailData.subject || emailData.subject.trim() === '') {
  throw new Error('Gemini no generó subject');
}

if (!emailData.body || emailData.body.trim() === '') {
  throw new Error('Gemini no generó body');
}

// Retornar datos limpios
return {
  json: {
    ...$('Extraer Datos Lead').item.json,
    subject: emailData.subject,
    body: emailData.body,
    generated_by: 'gemini',
    generated_at: new Date().toISOString()
  }
};
```

---

### PASO 5: Nodos 11-12 (Envío Email) - 1 hora

#### Nodo 11: Mailersend HTTP Request

**IMPORTANTE**: Este nodo requiere configuración precisa de Mailersend API.

```json
{
  "name": "Enviar Email con Mailersend",
  "type": "n8n-nodes-base.httpRequest",
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
        },
        {
          "name": "X-Requested-With",
          "value": "XMLHttpRequest"
        }
      ]
    },
    "sendBody": true,
    "specifyBody": "json",
    "jsonBody": "={\n  \"from\": {\n    \"email\": \"marketing@carrilloabgd.com\",\n    \"name\": \"Dr. Omar Carrillo - Carrillo Abogados\"\n  },\n  \"to\": [\n    {\n      \"email\": \"{{ $json.email }}\",\n      \"name\": \"{{ $json.nombre }}\"\n    }\n  ],\n  \"subject\": \"{{ $json.subject }}\",\n  \"html\": \"{{ $json.body }}\",\n  \"tags\": [\n    \"nurturing\",\n    \"position-{{ $json.position }}\",\n    \"mw1-sub-d\"\n  ],\n  \"settings\": {\n    \"track_clicks\": true,\n    \"track_opens\": true,\n    \"track_content\": false\n  }\n}",
    "options": {
      "timeout": 30000,
      "retry": {
        "enabled": true,
        "maxTries": 3,
        "waitBetweenTries": 1000
      }
    }
  },
  "position": [1780, 300]
}
```

**Troubleshooting Mailersend**:
- Error 401: API Key incorrecta
- Error 403: Dominio no verificado
- Error 422: Email inválido en payload

#### Nodo 12: IF ¿Envío Exitoso?

```json
{
  "name": "IF: ¿Envío Exitoso?",
  "type": "n8n-nodes-base.if",
  "typeVersion": 2.3,
  "parameters": {
    "conditions": {
      "options": {
        "caseSensitive": true,
        "leftValue": "",
        "typeValidation": "strict"
      },
      "conditions": [
        {
          "leftValue": "={{ $json.statusCode }}",
          "rightValue": "200,202",
          "operator": {
            "type": "string",
            "operation": "equals"
          }
        }
      ],
      "combinator": "and"
    }
  },
  "position": [2000, 300]
}
```

---

### PASO 6: Nodos 13-14 (Actualizar BD) - 45 min

#### Nodo 13: Firestore Update

```json
{
  "name": "Actualizar Firestore",
  "type": "n8n-nodes-base.googleFirestore",
  "parameters": {
    "operation": "update",
    "projectId": "carrillo-marketing-core",
    "collection": "leads",
    "documentId": "={{ $('Extraer Datos Lead').item.json.lead_id }}",
    "updateFields": {
      "fields": [
        {
          "name": "emails_sent",
          "value": "={{ $('Extraer Datos Lead').item.json.emails_sent + 1 }}"
        },
        {
          "name": "last_contact",
          "value": "={{ new Date().toISOString() }}"
        },
        {
          "name": "status",
          "value": "nurturing"
        },
        {
          "name": "nurturing_position",
          "value": "={{ $('Calcular Posición en Secuencia').item.json.position }}"
        },
        {
          "name": "next_email_date",
          "value": "={{ $('Calcular Posición en Secuencia').item.json.next_email_date }}"
        },
        {
          "name": "updated_at",
          "value": "={{ new Date().toISOString() }}"
        }
      ]
    }
  },
  "position": [2220, 240]
}
```

#### Nodo 14: Callback a Backend (OPCIONAL)

```json
{
  "name": "Callback: Nurturing Sent",
  "type": "n8n-nodes-base.httpRequest",
  "parameters": {
    "method": "POST",
    "url": "={{ $env.BACKEND_URL }}/webhook/nurturing-sent",
    "sendBody": true,
    "specifyBody": "json",
    "jsonBody": "={\n  \"lead_id\": \"{{ $('Extraer Datos Lead').item.json.lead_id }}\",\n  \"position\": {{ $('Calcular Posición en Secuencia').item.json.position }},\n  \"sent_at\": \"{{ new Date().toISOString() }}\",\n  \"status\": \"sent\"\n}",
    "options": {
      "timeout": 5000
    }
  },
  "onError": "continueRegularOutput",
  "position": [2220, 360]
}
```

**Nota**: `onError: continueRegularOutput` asegura que si el backend no está disponible, el workflow continúa.

---

### PASO 7: Nodos 15-16 (Finalización) - 15 min

#### Nodo 15: Consolidar Resultados

```json
{
  "name": "Consolidar Resultados",
  "type": "n8n-nodes-base.set",
  "parameters": {
    "mode": "manual",
    "assignments": {
      "assignments": [
        {
          "name": "total_leads_processed",
          "type": "number",
          "value": "={{ $('Loop: Split In Batches').params.total }}"
        },
        {
          "name": "execution_time",
          "type": "string",
          "value": "={{ new Date().toISOString() }}"
        },
        {
          "name": "success",
          "type": "boolean",
          "value": true
        }
      ]
    }
  },
  "position": [2440, 300]
}
```

#### Nodo 16: Exit Workflow

```json
{
  "name": "Exit Workflow",
  "type": "n8n-nodes-base.noOp",
  "parameters": {},
  "position": [2660, 300]
}
```

---

## 🧪 TESTING

### Test 1: Lead de Prueba en Firestore

Crear documento en Firestore collection `leads`:

```json
{
  "lead_id": "test-nurturing-2026-01-06",
  "nombre": "Test Dev",
  "email": "TU_EMAIL_AQUI@gmail.com",
  "empresa": "Test Corp",
  "servicio": "Registro de Marca",
  "categoria": "WARM",
  "score": 55,
  "status": "nuevo",
  "emails_sent": 0,
  "created_at": "2026-01-03T00:00:00Z",
  "next_email_date": "2026-01-06T00:00:00Z",
  "processed_at": "2026-01-03T00:00:00Z"
}
```

**Expected result**:
- position = 2 (días transcurridos = 3)
- Email posición 2 enviado: "¿Por qué proteger tu marca Test Corp?"
- Firestore actualizado: emails_sent = 1, next_email_date = 2026-01-10

### Test 2: Trigger Manual

1. En n8n, abrir SUB-D
2. Click "Execute Workflow"
3. Verificar en logs cada nodo
4. Revisar tu email (debería llegar en < 2 min)

### Test 3: Validar Firestore Update

```bash
# En Firestore Console
1. Ir a collection `leads`
2. Buscar documento `test-nurturing-2026-01-06`
3. Verificar campos actualizados:
   - emails_sent: 1
   - last_contact: (timestamp reciente)
   - nurturing_position: 2
   - next_email_date: 2026-01-10T00:00:00Z
```

---

## ⚠️ TROUBLESHOOTING COMÚN

### Error: "Gemini timeout"

**Solución**:
- Verificar credencial Gemini activa
- Reducir max_words en template (de 300 a 200)
- Aumentar timeout a 60 segundos

### Error: "Mailersend 401 Unauthorized"

**Solución**:
- Regenerar API Key en Mailersend dashboard
- Actualizar credencial en n8n Cloud
- Verificar que API Key tiene permisos de envío

### Error: "Firestore permission denied"

**Solución**:
- Verificar credencial Firestore
- Verificar Firestore Rules permiten write en collection `leads`
- Regenerar Service Account Key si es necesario

### No se envían emails

**Diagnóstico**:
1. Verificar query Firestore retorna leads (Nodo 3)
2. Verificar `next_email_date` está en el pasado
3. Verificar `should_send = true` en Nodo 7
4. Verificar logs de Mailersend para bounces

---

## ✅ CHECKLIST FINAL

Antes de marcar como completo:

- [ ] Workflow SUB-D creado con 16 nodos
- [ ] Todos los nodos conectados correctamente
- [ ] JavaScript de nodos 7 y 8 validado (sin errores de sintaxis)
- [ ] Credenciales Gemini, Firestore y Mailersend configuradas
- [ ] Variable entorno BACKEND_URL configurada
- [ ] Test manual ejecutado exitosamente
- [ ] Email de prueba recibido y verificado
- [ ] Firestore actualizado correctamente después de test
- [ ] Workflow guardado y activado (pero schedule desactivado hasta aprobar)
- [ ] Screenshots tomados para documentación

---

## 📤 ENTREGABLES

Cuando termines, crear carpeta:
`automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/artifacts/SUB-D/`

Incluir:
1. `SUB-D_WORKFLOW.json` (export del workflow completo)
2. `TEST_RESULTS.md` (resultados de testing)
3. `SCREENSHOTS/` (capturas de pantalla de cada test)
4. `MAILERSEND_CONFIG.md` (detalles de configuración Mailersend)

---

## 🚀 PRÓXIMO PASO

Una vez completado SUB-D, notificar a:
- @qa-specialist para testing exhaustivo
- @arquitecto para revisión final
- @orchestrator para coordinación con backend

---

**Preguntas o blockers? Contactar a @arquitecto inmediatamente.**

¡Éxito con la implementación! 🎯
