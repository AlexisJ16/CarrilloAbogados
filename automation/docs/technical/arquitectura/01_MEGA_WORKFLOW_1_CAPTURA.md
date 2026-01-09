# 🎯 MEGA-WORKFLOW #1: CAPTURA Y CONVERSIÓN DE LEADS

**Versión:** 2.0  
**Última actualización:** 2025-12-18  
**Estado:** 🟡 En implementación (Fase 1 completa)  
**Prioridad:** CRÍTICO - Motor Futuro (60%)

---

## 📋 TABLA DE CONTENIDOS

1. [Visión General](#1-visión-general)
2. [Objetivos de Negocio](#2-objetivos-de-negocio)
3. [Arquitectura del MEGA-WORKFLOW](#3-arquitectura-del-mega-workflow)
4. [Componentes Detallados](#4-componentes-detallados)
5. [Flujo de Datos](#5-flujo-de-datos)
6. [Estado Actual](#6-estado-actual)
7. [Plan de Implementación](#7-plan-de-implementación)
8. [Testing y Validación](#8-testing-y-validación)
9. [Métricas de Éxito](#9-métricas-de-éxito)

---

## 1. VISIÓN GENERAL

### 1.1 Propósito

El **MEGA-WORKFLOW #1: Captura y Conversión de Leads** es el núcleo del "Motor Futuro" de Carrillo Abogados. Su objetivo es automatizar completamente el ciclo de vida de leads desde la captura inicial hasta el agendamiento de reuniones.

### 1.2 Alcance Funcional

```
FORMULARIO WEB ──► CAPTURA ──► CLASIFICACIÓN ──► NURTURING ──► CONVERSIÓN
                      │              │               │              │
                      ▼              ▼               ▼              ▼
                  Firestore     Score/Categoría   Secuencia     Reunión
                  (persistir)   (HOT/WARM/COLD)   de emails     agendada
```

### 1.3 KPIs Objetivo

| Métrica | Actual (Manual) | Objetivo (Automatizado) | Mejora |
|---------|----------------:|------------------------:|-------:|
| Leads procesados/mes | 20 | 300+ | 15x |
| Tiempo de primera respuesta | 4-24 horas | < 1 minuto | 1440x |
| Tasa de conversión | ~5% | 15%+ | 3x |
| Horas semanales dedicadas | 20+ | 5 | 4x menos |

---

## 2. OBJETIVOS DE NEGOCIO

### 2.1 Objetivos Primarios

1. **Captura 24/7:** Recibir y procesar leads en cualquier momento sin intervención humana
2. **Respuesta instantánea:** Email personalizado generado por IA en < 60 segundos
3. **Clasificación inteligente:** Scoring automatizado con criterios de negocio
4. **Notificación HOT:** Dr. Carrillo notificado inmediatamente para leads de alto valor
5. **Nurturing automatizado:** Secuencia de 8-12 emails para leads WARM/COLD

### 2.2 Objetivos Secundarios

6. **Trazabilidad completa:** Cada interacción registrada en Firestore
7. **Optimización continua:** Métricas para ajustar scoring y nurturing
8. **Integración web:** API webhook para cualquier formulario
9. **Escalabilidad:** Arquitectura que soporte 1,000+ leads/mes si es necesario

### 2.3 Segmento Objetivo

| Criterio | Valor |
|----------|-------|
| **Industria** | Tecnología, Software, Fintech, E-commerce |
| **Tamaño** | PyMEs (10-200 empleados) |
| **Ubicación** | Colombia (principalmente Bogotá, Medellín, Cali) |
| **Necesidad** | Protección de PI, Registro de Marcas, Asesoría legal |
| **Características HOT** | C-Level, empresa tech, necesidad urgente, presupuesto disponible |

---

## 3. ARQUITECTURA DEL MEGA-WORKFLOW

### 3.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    MEGA-WORKFLOW #1: CAPTURA Y CONVERSIÓN                       │
│                         (Motor Futuro - 60%)                                    │
└─────────────────────────────────────────────────────────────────────────────────┘

                              EVENTOS EXTERNOS
                    ┌──────────────────────────────────┐
                    │  • Formulario web (nuevo lead)   │
                    │  • Mailersend (email abierto)    │
                    │  • Calendly (reunión agendada)   │
                    └──────────────┬───────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                      WORKFLOW A: LEAD LIFECYCLE MANAGER                         │
│                              (ORQUESTADOR / HUB)                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │  Webhook        Identificar        Router           Consolidar          │   │
│  │  /lead-events → Tipo Evento →  por Evento  →       Respuesta           │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                   │                                             │
│         ┌─────────────────────────┼─────────────────────────┐                  │
│         │                         │                         │                  │
│         ▼                         ▼                         ▼                  │
│  ┌─────────────┐          ┌─────────────┐          ┌─────────────┐           │
│  │ new_lead    │          │ email_event │          │meeting_event│           │
│  └──────┬──────┘          └──────┬──────┘          └──────┬──────┘           │
└─────────┼────────────────────────┼────────────────────────┼─────────────────────┘
          │                        │                        │
          ▼                        ▼                        ▼
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│     SUB-A        │      │     SUB-E        │      │     SUB-F        │
│  Lead Intake &   │      │   Engagement     │      │    Meeting       │
│   Enrichment     │      │    Tracker       │      │   Scheduler      │
│                  │      │                  │      │                  │
│ • Normalizar     │      │ • Opens/Clicks   │      │ • Sincronizar    │
│ • Validar        │      │ • Update score   │      │ • Preparar       │
│ • Clasificar     │      │ • Re-categorizar │      │ • Notificar      │
│ • Guardar        │      │ • Trigger SUB-D  │      │ • Actualizar     │
│ • Responder      │      └──────────────────┘      └──────────────────┘
└────────┬─────────┘
         │
         │ Si score >= 80 (HOT)
         ▼
┌──────────────────┐
│     SUB-D        │
│   Nurturing      │
│    Sequence      │
│    Engine        │
│ • 8-12 emails    │
│ • Scheduling     │
│ • A/B testing    │
└──────────────────┘

NOTA: SUB-B (Notificación HOT) y SUB-C (Respuesta Instantánea) fueron
      integrados en SUB-A para simplificar la arquitectura.
```

### 3.2 Tabla de Componentes

| ID | Componente | Tipo | Trigger | Función Principal | Criticidad | Estado |
|----|------------|------|---------|-------------------|------------|--------|
| **A** | Lead Lifecycle Manager | Orquestador (HUB) | Webhook `/lead-events` | Recibir y enrutar eventos | CRÍTICO | ✅ Activo |
| **SUB-A** | Lead Intake & Enrichment | Spoke | Execute Workflow | Capturar, validar, clasificar, notificar, responder | CRÍTICO | ✅ Completo |
| ~~**SUB-B**~~ | ~~Hot Lead Notification~~ | ~~Spoke~~ | ~~Execute Workflow~~ | ~~Notificar equipo HOT~~ | ~~ALTO~~ | ❌ Integrado en SUB-A |
| ~~**SUB-C**~~ | ~~Instant Response~~ | ~~Spoke~~ | ~~Execute Workflow~~ | ~~Email personalizado IA~~ | ~~ALTO~~ | ❌ Integrado en SUB-A |
| **SUB-D** | Nurturing Sequence | Spoke | Schedule o Execute | Secuencia de emails para WARM/COLD | MEDIO | ⚪ Planificado |
| **SUB-E** | Engagement Tracker | Spoke | Webhook Mailersend | Tracking opens/clicks | BAJO | ⚪ Futuro |
| **SUB-F** | Meeting Scheduler | Spoke | Webhook Google Calendar | Sincronizar reuniones agendadas | MEDIO | ⚪ Futuro |

### 3.3 Decisiones de Diseño

#### ¿Por qué SUB-A agrupa validación + scoring + respuesta?

| Factor | Decisión |
|--------|----------|
| **Cohesión** | Las 3 funciones SIEMPRE se ejecutan juntas para cada lead |
| **Contexto** | Comparten el mismo objeto lead |
| **Latencia** | Evita overhead de pasar datos entre 3 workflows |
| **Fallo** | Si la validación falla, no tiene sentido clasificar o responder |

#### ¿Por qué SUB-B y SUB-C se integraron en SUB-A?

| Factor | Decisión |
|--------|----------|
| **Simplicidad** | Menos workflows = menos overhead = más rápido |
| **Cohesión** | Notificación y respuesta siempre ocurren juntos |
| **Latencia** | Evita overhead de llamar múltiples sub-workflows |
| **MVP** | Para email simple, no necesitamos SMS/WhatsApp todavía |
| **Futuro** | Si se necesita SMS/WhatsApp, se puede agregar nodo en SUB-A |

#### ¿Por qué SUB-D (Nurturing) es separado?

| Factor | Decisión |
|--------|----------|
| **Trigger diferente** | Schedule (cada 6h) vs Webhook (real-time) |
| **Ejecución independiente** | Puede fallar sin afectar captura |
| **Complejidad** | Lógica de secuencias es compleja |
| **Escalabilidad** | Procesa muchos leads en batch |

---

## 4. COMPONENTES DETALLADOS

### 4.1 WORKFLOW A: Lead Lifecycle Manager (Orquestador)

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | `7yRMJecj0TaIdinU` |
| **Estado** | 🟢 ACTIVO |
| **Nodos** | 5 |
| **URL Webhook** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |

#### Flujo de Nodos

```
[1. Webhook Principal]
    POST /lead-events
    Body: { event_type, payload }
         │
         ▼
[2. Identificar Tipo de Evento]
    Code node - Analiza event_type
    Determina target_workflow_id
         │
         ▼
[3. Router por Evento]
    Switch node
    ├── "new_lead" → Execute SUB-A
    ├── "email_opened" → Execute SUB-E
    ├── "meeting_booked" → Execute SUB-F
    └── fallback → Error Handler
         │
         ▼
[4. Consolidar Respuesta]
    Set node - Estructura respuesta estándar
         │
         ▼
[5. Responder Webhook]
    Respond to Webhook - HTTP 200/500
```

#### Tipos de Evento Soportados

| event_type | Descripción | Workflow Destino |
|------------|-------------|------------------|
| `new_lead` | Nuevo lead desde formulario web | SUB-A |
| `email_opened` | Lead abrió email (Mailersend webhook) | SUB-E |
| `email_clicked` | Lead hizo click en email | SUB-E |
| `meeting_booked` | Lead agendó reunión (Calendly webhook) | SUB-F |
| `nurturing_trigger` | Trigger manual de nurturing | SUB-D |

---

### 4.2 SUB-A: Lead Intake & Enrichment

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | `RHj1TAqBazxNFriJ` |
| **Estado** | 🟡 Validado (inactivo hasta testing E2E) |
| **Nodos** | 9 |
| **Versión** | v2 - Hub & Spoke |

#### Flujo de Nodos

```
[Execute Workflow Trigger]
    Recibe datos del Orquestador
         │
         ▼
[0. Mapear Input]
    Set node - Reorganiza datos para compatibilidad
         │
         ▼
[1. Validar y Clasificar]
    Code node - Validación email + Scoring
    ├── Validar formato email (regex)
    ├── Calcular score (0-100):
    │   ├── Base: 30 pts
    │   ├── Servicio "marca"/"litigio": +20 pts
    │   ├── Mensaje > 50 chars: +10 pts
    │   ├── Tiene teléfono: +10 pts
    │   └── Tiene empresa: +10 pts
    └── Categorizar: HOT (≥70) / WARM (40-69) / COLD (<40)
         │
         ▼
[2. Guardar en Firestore]
    Google Firestore node
    Collection: leads
    Campos: lead_id, nombre, email, empresa, score, category, processed_at
         │
         ▼
[3. Es Lead HOT?]
    IF node - category === "HOT"
    ├── TRUE: → Notificar + Generar Respuesta
    └── FALSE: → Solo Generar Respuesta
         │
         ├── [4. Notificar Equipo (HOT)]
         │   Gmail node - Email a marketing@carrilloabgd.com
         │
         ▼
[5. Generar Respuesta (Gemini)]
    Google Gemini node - Email personalizado
         │
         ▼
[6. Enviar Respuesta Lead]
    Gmail node - Email al lead
         │
         ▼
[FINAL. Resultado del Sub-Workflow]
    Set node - Estructura respuesta para Orquestador
```

#### Criterios de Scoring

| Criterio | Puntos | Ejemplo |
|----------|-------:|---------|
| Base (lead capturado) | +30 | Todos los leads |
| Servicio incluye "marca" o "litigio" | +20 | "Registro de Marca" |
| Mensaje > 50 caracteres | +10 | Descripción detallada |
| Tiene teléfono | +10 | +573001234567 |
| Tiene empresa | +10 | "TechStartup SAS" |
| Email corporativo (no gmail/hotmail) | +10 | maria@techcorp.co |
| Cargo C-Level | +20 | CEO, CTO, Fundador |
| **MÁXIMO** | **100** | Lead ideal |

#### Categorías

| Categoría | Score | Acción |
|-----------|------:|--------|
| **HOT** 🔥 | ≥ 70 | Notificación inmediata + Respuesta IA |
| **WARM** 🟡 | 40-69 | Respuesta IA + Nurturing |
| **COLD** ⚪ | < 40 | Respuesta genérica + Nurturing básico |

---

### 4.3 SUB-B: Hot Lead Notification

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | Pendiente creación |
| **Estado** | 📋 Especificado |
| **Especificación** | `02-spokes/sub-b-hot-lead-notification/specs/workflow_spec.md` |

#### Flujo Planificado

```
[Execute Workflow Trigger]
    Recibe lead HOT desde SUB-A
         │
         ▼
[1. Validar Input]
    Verificar score >= 80, email válido
         │
         ▼
[2. Enriquecer Contexto]
    Agregar timestamp, urgency, SLA
         │
         ▼
[3. Formatear Mensajes]
    ├── HTML para email
    ├── Texto para SMS
    ├── Markdown para WhatsApp
    └── Blocks para Slack
         │
         ▼
[4. Router Paralelo]
    ├── Email Gmail → marketing@carrilloabgd.com
    ├── SMS Twilio → +57300... (OPCIONAL)
    ├── WhatsApp Twilio → ... (OPCIONAL)
    └── Slack Webhook → #leads-hot (OPCIONAL)
         │
         ▼
[5. Consolidar Resultados]
    Verificar al menos 1 canal exitoso
         │
         ▼
[6. Registrar en Firestore]
    Collection: notifications_log
         │
         ▼
[FINAL. Retornar Resultado]
```

#### Canales de Notificación (Prioridad)

| Canal | Prioridad | Costo | Estado |
|-------|-----------|-------|--------|
| Email (Gmail) | CRÍTICO | Gratis | MVP |
| Firestore Log | ALTO | Gratis | MVP |
| SMS (Twilio) | MEDIO | $0.05/SMS | Fase 2 |
| WhatsApp (Twilio) | BAJO | $0.005/msg | Fase 2 |
| Slack | BAJO | Gratis | Fase 2 |

---

### 4.4 SUB-C: Instant Response (Integrado en SUB-A)

**Nota:** Actualmente esta funcionalidad está integrada en SUB-A. Se evaluará si separar en futuro para mayor flexibilidad.

---

### 4.5 SUB-D: Nurturing Sequence Engine

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | Pendiente creación |
| **Estado** | ⚪ Planificado (Fase 3) |
| **Trigger** | Schedule (cada 6 horas) |

#### Flujo Planificado

```
[Schedule Trigger: Every 6h]
         │
         ▼
[1. Query Leads para Nurturing]
    Firestore query:
    - status: "nuevo" o "nurturing"
    - last_contact > 24h
    - categoria: WARM o COLD
         │
         ▼
[2. Loop por cada Lead]
    Split In Batches
         │
    ┌────┴────┐
    │         │
    ▼         ▼
[3. Determinar Posición en Secuencia]
    emails_sent: 0 → Email 1
    emails_sent: 1 → Email 2
    ... (hasta Email 12)
         │
         ▼
[4. Generar Contenido (Gemini)]
    Prompt según posición y perfil
         │
         ▼
[5. Enviar Email (Mailersend)]
    Con tracking de opens/clicks
         │
         ▼
[6. Actualizar Firestore]
    last_contact: now()
    emails_sent: +1
    status: "nurturing"
```

#### Secuencia de Nurturing (12 emails)

| # | Día | Objetivo | Contenido |
|---|-----|----------|-----------|
| 1 | 0 | Bienvenida | Presentación firma + Valor |
| 2 | 3 | Educativo | ¿Por qué proteger tu marca? |
| 3 | 7 | Case Study | Historia de éxito cliente |
| 4 | 10 | Valor agregado | Checklist PI gratuito |
| 5 | 14 | Urgencia | Riesgos de no proteger |
| 6 | 21 | Testimonial | Video Dr. Carrillo |
| 7 | 28 | Oferta | Consulta inicial gratuita |
| 8 | 35 | Re-engagement | ¿Sigues interesado? |
| 9 | 42 | Educativo | Tendencias PI 2026 |
| 10 | 49 | Last chance | Última oportunidad |
| 11 | 56 | Break-up | Email de despedida |
| 12 | 90 | Win-back | Re-activación |

---

### 4.6 SUB-E: Engagement Tracker

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | Pendiente creación |
| **Estado** | ⚪ Planificado (Fase 4) |
| **Trigger** | Webhook desde Mailersend |

#### Flujo Planificado

```
[Webhook: Mailersend Events]
    /email-events
    Events: opened, clicked, bounced, unsubscribed
         │
         ▼
[1. Parsear Evento]
    Extraer: lead_id, event_type, timestamp
         │
         ▼
[2. Buscar Lead en Firestore]
    Query por lead_id
         │
         ▼
[3. Actualizar Métricas]
    email_opens: +1
    email_clicks: +1
    last_engagement: now()
         │
         ▼
[4. Re-evaluar Score]
    Si opens >= 3 AND clicks >= 1:
    - Incrementar score +15
    - Si nuevo score >= 70: WARM → HOT
         │
         ▼
[5. Trigger Notificación?]
    Si cambio a HOT → Execute SUB-B
         │
         ▼
[6. Registrar Evento]
    Firestore: engagement_log
```

---

### 4.7 SUB-F: Meeting Scheduler

#### Información General

| Atributo | Valor |
|----------|-------|
| **ID n8n** | Pendiente creación |
| **Estado** | ⚪ Planificado (Fase 5) |
| **Trigger** | Webhook desde Calendly |

#### Flujo Planificado

```
[Webhook: Calendly Events]
    /booking-events
    Events: invitee.created, invitee.cancelled
         │
         ▼
[1. Parsear Evento Calendly]
    Extraer: email, meeting_start, meeting_link
         │
         ▼
[2. Buscar Lead por Email]
    Firestore query
         │
         ▼
[3. Actualizar Lead]
    status: "reunion_agendada"
    meeting_date: ISO timestamp
    meeting_link: Calendly URL
         │
         ▼
[4. Preparar Recordatorio]
    Crear evento en Google Calendar (opcional)
         │
         ▼
[5. Notificar Dr. Carrillo]
    Email: "Lead HOT [Nombre] agendó reunión [Fecha]"
    WhatsApp: Mensaje rápido (opcional)
         │
         ▼
[6. Enviar Confirmación Lead]
    Email con detalles de la reunión
```

---

## 5. FLUJO DE DATOS

### 5.1 Estructura de Lead en Firestore

```json
{
  "lead_id": "2026-01-15T10:30:00.000Z-maria-techstartup-co",
  "timestamp": "2026-01-15T10:30:00.000Z",
  
  // Datos básicos
  "nombre": "María Rodríguez",
  "email": "maria@techstartup.co",
  "telefono": "+573101234567",
  "empresa": "TechStartup SAS",
  "cargo": "CEO",
  
  // Datos del formulario
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesitamos proteger nuestra marca de software",
  "utm_source": "google",
  "utm_campaign": "registro-marca-q1-2026",
  "form_url": "https://carrilloabogados.com/contacto",
  
  // Datos normalizados (IA)
  "normalized_role": "C-Level",
  "normalized_industry": "Technology",
  
  // Scoring
  "score": 95,
  "categoria": "HOT",
  "score_breakdown": {
    "base": 30,
    "servicio": 20,
    "mensaje": 10,
    "telefono": 10,
    "empresa": 10,
    "cargo": 15
  },
  
  // Estado del lead
  "status": "nuevo",  // nuevo, nurturing, reunion_agendada, convertido, perdido
  "assigned_to": null,
  "processed_at": "2026-01-15T10:30:05.000Z",
  
  // Métricas de engagement
  "emails_sent": 0,
  "email_opens": 0,
  "email_clicks": 0,
  "last_contact": "2026-01-15T10:30:05.000Z",
  "last_engagement": null,
  
  // Reunión (si aplica)
  "meeting_date": null,
  "meeting_link": null,
  
  // Auditoría
  "created_at": "2026-01-15T10:30:00.000Z",
  "updated_at": "2026-01-15T10:30:05.000Z"
}
```

### 5.2 Ciclo de Vida del Lead

```
                   NUEVO                    NURTURING                REUNIÓN
                     │                          │                        │
                     │ Score calculado          │ 8-12 emails           │ Agendó
                     │ Email automático         │ Opens/Clicks          │ reunión
                     │                          │                        │
     ┌───────────────┼──────────────────────────┼────────────────────────┼───────┐
     │               │                          │                        │       │
     ▼               ▼                          ▼                        ▼       ▼
┌─────────┐    ┌─────────┐              ┌─────────┐              ┌─────────┐ ┌─────────┐
│  NUEVO  │───►│NURTURING│─────────────►│   HOT   │─────────────►│CONVERTIDO│ │ PERDIDO │
│         │    │         │              │         │              │         │ │         │
│ Score   │    │ Emails  │              │ Reunión │              │ Cliente │ │ Sin     │
│ asignado│    │ enviados│              │ agendada│              │ firmado │ │ respuesta│
└─────────┘    └─────────┘              └─────────┘              └─────────┘ └─────────┘
     │               │                       │                        │           │
     │               │                       │                        │           │
     └───────────────┴───────────────────────┴────────────────────────┴───────────┘
                                       │
                                 Puede moverse
                               entre estados según
                                  engagement
```

---

## 6. ESTADO ACTUAL

### 6.1 Resumen de Progreso

| Componente | Estado | Completado | Notas |
|------------|--------|------------|-------|
| WORKFLOW A (Orquestador) | 🟢 Activo | 95% | Funcional, falta agregar Switch |
| SUB-A: Lead Intake + Notification + Response | 🟢 Activo | 100% | Incluye funciones de SUB-B y SUB-C |
| ~~SUB-B: Hot Notification~~ | ❌ Cancelado | - | Integrado en SUB-A |
| ~~SUB-C: Instant Response~~ | ❌ Cancelado | - | Integrado en SUB-A |
| SUB-D: Nurturing | ⚪ Planificado | 5% | Pendiente diseño e implementación |
| SUB-E: Engagement Tracker | ⚪ Planificado | 5% | Concepto |
| SUB-F: Meeting Scheduler | ⚪ Planificado | 5% | Concepto |

### 6.2 Credenciales Activas

| Servicio | Estado | Usado en |
|----------|--------|----------|
| Gmail OAuth2 | ✅ Activo | SUB-A (emails) |
| Firestore | ✅ Activo | SUB-A (storage) |
| Google Gemini | ✅ Activo | SUB-A (IA) |
| Twilio SMS | ⚪ Pendiente | SUB-B (futuro) |
| Twilio WhatsApp | ⚪ Pendiente | SUB-B (futuro) |
| Mailersend | ⚪ Pendiente | SUB-D, SUB-E (futuro) |
| Calendly | ⚪ Pendiente | SUB-F (futuro) |

### 6.3 Próximos Pasos Inmediatos

1. ✅ **Completado:** Validación SUB-A (report final aprobado 2025-12-18)
2. ⏳ **En progreso:** Testing E2E Orquestador → SUB-A
3. 📋 **Siguiente:** Implementar SUB-B (Hot Lead Notification)
4. ⚪ **Futuro:** Configurar Mailersend y Calendly

---

## 7. PLAN DE IMPLEMENTACIÓN

### 7.1 Fases de Desarrollo

| Fase | Nombre | Período | Componentes | Horas | Estado |
|------|--------|---------|-------------|------:|--------|
| **1** | MVP | Dic 2025 | Orquestador + SUB-A | 10 | ✅ Completo |
| **2** | Notificaciones | Ene 2026 | SUB-B + mejoras SUB-A | 10 | 📋 Siguiente |
| **3** | Nurturing | Feb 2026 | SUB-D + Mailersend | 12 | ⚪ Planificado |
| **4** | Tracking | Feb 2026 | SUB-E | 8 | ⚪ Planificado |
| **5** | Agendamiento | Mar 2026 | SUB-F + Calendly | 10 | ⚪ Planificado |

### 7.2 Detalle Fase 2: Notificaciones

**Duración:** 2 semanas  
**Horas estimadas:** 10 horas

| Día | Tarea | Descripción |
|-----|-------|-------------|
| 1-2 | Crear SUB-B | Estructura básica + Email notification |
| 3 | Firestore logging | Collection notifications_log |
| 4 | Error handling | Fallbacks y consolidación |
| 5 | Integración SUB-A | Llamar SUB-B cuando score >= 80 |
| 6-7 | Testing completo | Todos los escenarios |
| 8-10 | Buffer/Ajustes | Correcciones y optimizaciones |

### 7.3 Criterios de Avance de Fase

| Criterio | Descripción |
|----------|-------------|
| **Testing E2E** | Todos los escenarios principales funcionan |
| **Error handling** | Errores manejados sin caída de sistema |
| **Documentación** | Specs y artifacts actualizados |
| **Métricas** | Latencia < 10 seg, tasa éxito > 95% |

---

## 8. TESTING Y VALIDACIÓN

### 8.1 Plan de Testing E2E

#### Test 1: Lead HOT (Camino Feliz)

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "María Test",
    "email": "maria.test@techcorp.co",
    "telefono": "+573101234567",
    "empresa": "TechCorp Test SAS",
    "cargo": "CEO",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesitamos proteger nuestra marca de software urgentemente",
    "utm_source": "google",
    "utm_campaign": "test-hot-lead"
  }'
```

**Validaciones:**
- [ ] Orquestador responde 200
- [ ] SUB-A ejecuta exitosamente
- [ ] Lead guardado en Firestore con score >= 70
- [ ] Email notificación a equipo (si SUB-B activo)
- [ ] Email respuesta al lead
- [ ] Latencia total < 10 segundos

#### Test 2: Lead WARM

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Carlos Test",
    "email": "carlos@gmail.com",
    "empresa": "PyME Test",
    "servicio_interes": "Consulta General",
    "mensaje": "Información",
    "utm_source": "facebook"
  }'
```

**Validaciones:**
- [ ] Score entre 40-69
- [ ] NO notificación HOT
- [ ] Email respuesta al lead

#### Test 3: Email Inválido (Error)

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Invalid Test",
    "email": "not-an-email"
  }'
```

**Validaciones:**
- [ ] Error de validación
- [ ] No se guarda en Firestore
- [ ] Respuesta de error clara

---

## 9. MÉTRICAS DE ÉXITO

### 9.1 KPIs Técnicos

| Métrica | Target | Frecuencia |
|---------|--------|------------|
| Latencia promedio (Orquestador → Respuesta) | < 10 seg | Continuo |
| Tasa de éxito de ejecución | > 99% | Semanal |
| Leads procesados sin error | > 95% | Semanal |
| Tiempo primera respuesta | < 60 seg | Cada lead |

### 9.2 KPIs de Negocio

| Métrica | Target | Frecuencia |
|---------|--------|------------|
| Leads capturados | 300+/mes | Mensual |
| Tasa HOT leads | 15-20% | Mensual |
| Tasa apertura emails | > 25% | Semanal |
| Leads → Reunión | > 10% | Mensual |
| Reunión → Cliente | > 25% | Mensual |

### 9.3 Dashboard de Monitoreo

**Ubicación recomendada:** Looker Studio conectado a Firestore

**Visualizaciones:**
- Leads por día/semana/mes
- Distribución por categoría (HOT/WARM/COLD)
- Funnel de conversión
- Tiempo promedio de respuesta
- Fuentes de leads (UTM)
- Rendimiento de campañas

---

## 📚 DOCUMENTOS RELACIONADOS

| Documento | Ubicación |
|-----------|-----------|
| Arquitectura General | `00_ARQUITECTURA_GENERAL.md` |
| Spec SUB-B | `04-workflows/.../sub-b-hot-lead-notification/specs/workflow_spec.md` |
| Guía Implementación SUB-A | `guia_implementacion_workflow_a.md` (Project Knowledge) |
| Report Validación SUB-A | `04-workflows/.../sub-a-lead-intake/testing/validation_report_FINAL.md` |

---

**Última actualización:** 2025-12-18 | **Versión:** 2.0
