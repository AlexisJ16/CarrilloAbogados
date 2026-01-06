# 🎯 TAREAS MARKETING DEV - Completar MW#1

**Rol**: Marketing Dev (n8n)  
**Fecha**: 5 de Enero, 2026  
**Contexto**: Backend NO disponible ahora - Trabajamos solo en n8n workflows  
**Objetivo**: Preparar MW#1 100% para que cuando backend esté listo, todo funcione

---

## 📊 ESTADO ACTUAL VERIFICADO

### ✅ Lo que YA funciona en n8n Cloud

| Workflow | Estado | ID n8n |
|----------|--------|--------|
| **Orquestador (Hub)** | ✅ ACTIVO | `bva1Kc1USbbITEAw` |
| **SUB-A: Lead Intake IA** | ✅ ACTIVO | `RHj1TAqBazxNFriJ` |
| **Gemini 2.5 Pro** | ✅ Configurado | Scoring + respuesta IA |
| **Firestore** | ✅ Operativo | Proyecto: carrillo-marketing-core |
| **Gmail API** | ✅ Activo | marketing@carrilloabgd.com |

### ❌ Lo que FALTA (Tu trabajo)

| Sub-Workflow | Propósito | Estado |
|--------------|-----------|--------|
| **SUB-A Callbacks** | Enviar score a backend SIEMPRE (HOT/WARM/COLD) | 🔴 Falta agregar nodos |
| **SUB-D: Nurturing** | Secuencia 12 emails automatizada | 🔴 No existe |
| **SUB-E: Engagement** | Tracking opens/clicks Mailersend | 🔴 No existe (Opcional) |
| **SUB-F: Meeting Sync** | Sincronizar Google Calendar | 🔴 No existe (Opcional) |

---

## 🎯 TUS 4 TAREAS (Prioridad)

### TAREA 1: Modificar SUB-A - Agregar Callbacks ✅ COMPLETADO

**Tiempo estimado**: 1.5 horas  
**Prioridad**: P0 - Sin esto, backend no puede actualizar PostgreSQL
**Estado**: ✅ **COMPLETADO** (2026-01-05)

#### Cambios Aplicados

**3 nodos nuevos agregados al SUB-A** (ahora 16 nodos total):

1. **Nodo 7: Callback Lead Scored** (HTTP Request)
   - URL: `$env.BACKEND_URL/webhook/lead-scored` (fallback: localhost:8800)
   - Método: POST
   - Se ejecuta **SIEMPRE** (HOT/WARM/COLD)
   - Payload: `{ leadId, score, categoria }`

2. **Nodo 8: Es Lead HOT (Callback)?** (IF)
   - Condición: `categoria === "HOT"`
   - Usa typeVersion 2.3

3. **Nodo 9: Callback Hot Lead Alert** (HTTP Request)
   - URL: `$env.BACKEND_URL/webhook/lead-hot`
   - Método: POST
   - Se ejecuta **SOLO si HOT**
   - Payload: `{ leadId, nombre, email, score, urgency: "high" }`

**Conexiones agregadas**:
- `6. Enviar Respuesta Lead1` → `7. Callback Lead Scored`
- `7. Callback Lead Scored` → `8. Es Lead HOT (Callback)?`
- `8. Es Lead HOT (Callback)?` [TRUE] → `9. Callback Hot Lead Alert`

**Error Handling**: `onError: continueRegularOutput` (no bloquea el flujo si backend no disponible)

#### Variable de Entorno (Pendiente configurar en n8n Cloud UI)

- `BACKEND_URL`: 
  - Desarrollo: `http://localhost:8800`
  - Producción: `https://api.carrilloabgd.com/n8n-integration-service`

#### Requerimientos

**ANTES** (actual):
```
SUB-A termina en:
[6. Enviar Respuesta Lead (Gmail)] → [FINAL]
```

**DESPUÉS** (necesario):
```
[6. Enviar Respuesta Lead] 
    ↓
[7. Callback: Lead Scored] ← NUEVO - SIEMPRE enviar (HOT/WARM/COLD)
    ↓
[8. IF: Es Lead HOT?] ← NUEVO
    ↓ [TRUE]
[9. Callback: Hot Lead Alert] ← NUEVO - Solo HOT
    ↓
[FINAL]
```

#### Especificación Técnica

**Nodo 7: HTTP Request "Callback Lead Scored"**
- URL: `{{$env.BACKEND_URL}}/webhook/lead-scored`
- Method: POST
- Body:
  ```json
  {
    "leadId": "{{$json.lead_id}}",
    "score": {{$json.score}},
    "categoria": "{{$json.categoria}}"
  }
  ```
- **IMPORTANTE**: Este callback se envía **SIEMPRE** (HOT, WARM, COLD)

**Nodo 8: IF "Es Lead HOT?"**
- Condition: `categoria === "HOT"`

**Nodo 9: HTTP Request "Hot Lead Alert"** (solo si HOT)
- URL: `{{$env.BACKEND_URL}}/webhook/lead-hot`
- Body:
  ```json
  {
    "leadId": "{{$json.lead_id}}",
    "nombre": "{{$json.nombre}}",
    "email": "{{$json.email}}",
    "score": {{$json.score}},
    "urgency": "high"
  }
  ```

#### Variable de Entorno n8n

Configurar en n8n Cloud → Settings → Variables:
- **Desarrollo**: `BACKEND_URL = http://localhost:8800`
- **Producción**: `BACKEND_URL = https://api.carrilloabgd.com/n8n-integration-service`

#### Entregable

- [x] SUB-A modificado con 3 nodos nuevos
- [x] Testing manual: Estructura validada con MCP n8n (16 nodos, 15 conexiones válidas)
- [x] Documento: STATUS.md actualizado con cambios
- [ ] Pendiente: Configurar variable BACKEND_URL en n8n Cloud UI

#### Para el Arquitecto

**@Arquitecto**: ✅ TAREA 1 COMPLETADA - Nodos implementados según spec.

---

### TAREA 2: Crear SUB-D Nurturing Sequence ⭐ CRÍTICO

**Tiempo estimado**: 4 horas  
**Prioridad**: P1 - Core del "Motor Futuro"

#### Requerimientos de Negocio

**Objetivo**: Enviar secuencia automatizada de 8-12 emails a leads WARM/COLD para convertirlos en clientes sin intervención manual.

**Trigger**: Schedule cada 6 horas (00:00, 06:00, 12:00, 18:00)

**Fuente de datos**: Firestore collection `leads`

**Query Firestore**:
- `status` IN: `["new", "nurturing", "warm", "cold"]`
- `status` NOT IN: `["convertido", "perdido"]`
- `emails_sent` < 12
- `last_contact` < (NOW - días según posición)

**Secuencia de Emails** (días desde captura):

| # | Día | Subject | Objetivo |
|---|-----|---------|----------|
| 1 | 0 | "Gracias por contactarnos, {{nombre}}" | Bienvenida |
| 2 | 3 | "¿Por qué proteger tu marca {{empresa}}?" | Educativo |
| 3 | 7 | "Cómo ayudamos a {{similar_company}}" | Case Study |
| 4 | 10 | "Checklist gratuito: Registro de marcas" | Recurso |
| 5 | 14 | "3 riesgos que enfrentas sin registro" | Urgencia |
| 6 | 21 | "Dr. Carrillo en la SIC: 15 años" | Autoridad |
| 7 | 28 | "Consulta inicial GRATIS esta semana" | Oferta |
| 8 | 35 | "¿Sigues interesado en proteger tu marca?" | Re-engagement |
| 9 | 42 | "PI en 2026: Lo que debes saber" | Tendencias |
| 10 | 49 | "Última oportunidad: Consulta gratuita" | Last Chance |
| 11 | 56 | "¿Nos despedimos? (Por ahora)" | Break-up |
| 12 | 90 | "Han pasado 3 meses... ¿Hablamos?" | Win-back |

#### Componentes Necesarios

**1. Lógica de Posición**:
- Calcular días transcurridos desde `created_at`
- Determinar posición en secuencia (1-12)
- Verificar si debe enviarse HOY (tolerancia ±1 día)

**2. Templates de Email**:
- 12 structures predefinidos (subject + estructura)
- Variables: `{{nombre}}`, `{{empresa}}`, `{{servicio_interes}}`

**3. IA Personalización**:
- Gemini genera contenido único basado en:
  - Template de posición
  - Perfil del lead (empresa, servicio, score)
  - Máximo 200 palabras

**4. Envío con Tracking**:
- **Proveedor**: Mailersend (NO Gmail)
- Tracking: opens=true, clicks=true
- Tags: `["nurturing", "position-X", "categoria"]`

**5. Actualización Firestore**:
- `emails_sent`: +1
- `last_contact`: NOW()
- `status`: "nurturing"

#### Credenciales Requeridas

| Servicio | ¿Tienes cuenta? | Acción |
|----------|----------------|--------|
| **Mailersend** | ⚠️ Verificar | Si no: Crear cuenta gratis (3,000 emails/mes) |
| **Gemini** | ✅ Ya activo | Usar credential existente |
| **Firestore** | ✅ Ya activo | Usar credential existente |

#### Testing

**Crear lead de prueba en Firestore**:
```json
{
  "lead_id": "test-nurturing-001",
  "nombre": "Test Dev",
  "email": "tudev@gmail.com",
  "empresa": "Test Corp",
  "servicio_interes": "derecho-marcas",
  "categoria": "WARM",
  "score": 55,
  "status": "new",
  "emails_sent": 0,
  "created_at": "2026-01-02T00:00:00Z",
  "last_contact": null
}
```

**Expected**: Email posición 2 enviado (día 3)

#### Entregable

- [ ] Workflow SUB-D creado en n8n Cloud
- [ ] Schedule configurado (cada 6h)
- [ ] Cuenta Mailersend configurada
- [ ] Testing: 1 email de prueba enviado y recibido
- [ ] Firestore actualizado correctamente

#### Para el Arquitecto

**@Arquitecto**: Necesito diseño completo de SUB-D con:
- Flujo de nodos optimizado (mínimo ~16 nodos)
- Código JavaScript para calcular posición y días
- Estructura de templates (12 subjects + prompts)
- Configuración nodos Firestore (query + update)
- Configuración nodo Mailersend
- Manejo de errores y batch processing

**Referencias críticas**:
- Evitar queries pesadas (batch size: 10 leads)
- Timeout Gemini: 30 segundos
- Retry Mailersend: 2 intentos

---

### TAREA 3: Crear SUB-E Engagement Tracker 🔵 OPCIONAL

**Tiempo estimado**: 2 horas  
**Prioridad**: P2 - Mejora, no bloqueante

#### Requerimientos

**Trigger**: Webhook desde Mailersend
- URL: `/webhook-test/mailersend-events`

**Eventos a trackear**:
- `email.opened` → +5 puntos score
- `email.clicked` → +10 puntos score
- `email.bounced` → marcar lead como inválido

**Acción**:
1. Parsear evento Mailersend
2. Extraer email del lead
3. Buscar en Firestore
4. Actualizar métricas: `email_opens++`, `email_clicks++`, `score += puntos`
5. Recalcular categoría (si score ≥ 70 → HOT)
6. Si cambió a HOT → llamar callback `/webhook/lead-hot`

#### Entregable

- [ ] Workflow SUB-E creado (solo si hay tiempo)
- [ ] Testing con evento simulado

#### Para el Arquitecto

**@Arquitecto**: Si Marketing Dev tiene tiempo, diseñar flujo simplificado de SUB-E con manejo de eventos Mailersend webhook.

---

### TAREA 4: Crear SUB-F Google Calendar Sync 🔵 OPCIONAL

**Tiempo estimado**: 2 horas  
**Prioridad**: P2 - Mejora, no bloqueante

#### Requerimientos

**Trigger**: Webhook desde Google Calendar
- **IMPORTANTE**: Usar Google Calendar, NO Calendly
- URL: `/webhook-test/calendar-events`

**Evento**: Reunión agendada con lead

**Acción**:
1. Parsear evento Google Calendar
2. Extraer email del invitado
3. Buscar lead en Firestore
4. Actualizar: 
   - `status`: "reunion_agendada"
   - `meeting_scheduled_at`: fecha/hora
   - `score`: +30 puntos bonus
5. Fork paralelo:
   - Email a Dr. Carrillo: "Lead {{nombre}} agendó reunión"
   - Email confirmación al lead

#### Configuración Google Calendar Webhook

**Nota**: Google Calendar NO tiene webhooks nativos. Opciones:
1. **Opción A**: Usar Google Calendar API + polling cada 15 min
2. **Opción B**: Integrar con Calendly como proxy (más fácil)
3. **Opción C**: Zapier/Make.com bridge (más rápido setup)

#### Entregable

- [ ] Workflow SUB-F creado (solo si hay tiempo)
- [ ] Configuración Google Calendar API
- [ ] Testing con cita de prueba

#### Para el Arquitecto

**@Arquitecto**: Si Marketing Dev tiene tiempo, diseñar flujo con Google Calendar API polling o webhook alternativo. Evaluar mejor opción técnica.

---

## 📅 CRONOGRAMA SUGERIDO

### DÍA 1 (HOY): TAREA 1 - Callbacks SUB-A

**Morning** (2h):
- Revisar SUB-A actual en n8n
- Configurar variable entorno `BACKEND_URL`
- Agregar 3 nodos nuevos

**Afternoon** (1h):
- Testing manual (simular payloads)
- Documentar cambios
- Captura workflow actualizado

**Output**: SUB-A listo para backend

---

### DÍA 2: TAREA 2 Parte 1 - Setup SUB-D

**Morning** (2h):
- Crear cuenta Mailersend (si no existe)
- Configurar dominio carrilloabgd.com
- Obtener API Key
- Crear workflow nuevo en n8n

**Afternoon** (2h):
- Delegar diseño a Arquitecto
- Esperar specs detalladas
- Empezar implementación nodos básicos

---

### DÍA 3: TAREA 2 Parte 2 - Completar SUB-D

**Full Day** (4h):
- Implementar diseño del Arquitecto
- Configurar 12 templates
- Testing con lead de prueba
- Ajustes y debugging

**Output**: SUB-D funcional enviando emails

---

### DÍA 4-5: TAREAS 3-4 (Opcionales)

Si hay tiempo y energía:
- SUB-E: Engagement Tracker
- SUB-F: Google Calendar Sync

**Si NO hay tiempo**: Estas pueden esperar a Fase 2 del proyecto

---

## ✅ CRITERIOS DE ÉXITO

### Mínimo Viable (P0 + P1)

- [x] **SUB-A**: Callbacks agregados (TAREA 1)
- [x] **SUB-D**: Nurturing enviando emails cada 6h (TAREA 2)
- [x] **Testing**: 1 email nurturing recibido y lead actualizado en Firestore

### Ideal (P0 + P1 + P2)

- [x] Todo lo anterior +
- [x] **SUB-E**: Tracking engagement funcionando
- [x] **SUB-F**: Google Calendar sincronizando

---

## 🚨 BLOQUEADORES POTENCIALES

| Bloqueador | Probabilidad | Mitigación |
|------------|--------------|------------|
| **Mailersend cuenta no aprobada** | Media | Usar Gmail temporalmente para testing |
| **Google Calendar webhook complejo** | Alta | Evaluar Calendly como alternativa |
| **Gemini API límite** | Baja | Rate limiting en SUB-D |
| **Firestore queries lentas** | Baja | Batch size: 10 leads máximo |

---

## 📞 COORDINACIÓN CON OTROS AGENTES

### Con Arquitecto

**Cuándo delegar**:
- ✅ TAREA 2: Diseño completo SUB-D (16+ nodos)
- ✅ TAREA 3: Diseño SUB-E (si hay tiempo)
- ✅ TAREA 4: Diseño SUB-F (si hay tiempo)

**Qué esperar**:
- Diagrama de flujo de nodos
- Código JavaScript completo
- Configuración de cada nodo
- Estructuras JSON

### Con QA Specialist

**Cuándo delegar**:
- Después de implementar SUB-D
- Testing exhaustivo de secuencia completa
- Validación Mailersend tracking
- Casos edge (lead sin email, empresa null, etc.)

### Con Backend Dev (futuro)

**Qué compartir cuando esté listo**:
- [ ] Payloads JSON de callbacks (estructura exacta)
- [ ] URLs webhook definitivas
- [ ] Credenciales necesarias (si aplica)
- [ ] Documentación de SUB-A modificado

---

## 🎯 TU PRÓXIMA ACCIÓN (AHORA)

1. **Confirmar prioridades**: ¿Solo P0+P1? ¿O intentar P2 también?

2. **Si solo P0+P1** (Recomendado):
   - Empezar TAREA 1 (Callbacks SUB-A) - 1.5h
   - Solicitar diseño SUB-D al Arquitecto
   - Implementar SUB-D cuando tengas specs - 4h

3. **Si incluir P2**:
   - Todo lo anterior +
   - Solicitar diseños SUB-E y SUB-F al Arquitecto
   - Implementar si hay tiempo

**¿Qué prefieres?** 🤔

---

## 📚 DOCUMENTOS DE REFERENCIA

| Documento | Para qué |
|-----------|----------|
| `WEB_INTEGRATION.md` | Ver payloads esperados por backend |
| `01_MEGA_WORKFLOW_1_CAPTURA.md` | Contexto arquitectura MW#1 |
| `STATUS.md` | Estado actual Orquestador + SUB-A |
| `BACKEND_DEV_TASKS.md` | Entender qué hará backend (futuro) |

---

**¿Listo para empezar con TAREA 1?** 🚀
