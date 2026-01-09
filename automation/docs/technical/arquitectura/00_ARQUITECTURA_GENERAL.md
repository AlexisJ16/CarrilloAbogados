# 🏗️ ARQUITECTURA GENERAL: SISTEMA DE AUTOMATIZACIÓN CARRILLO ABOGADOS

**Versión:** 2.1  
**Última actualización:** 2025-12-19  
**Autor:** Equipo de Estrategia Digital  
**Estado:** ACTIVO - En implementación

---

## 📋 TABLA DE CONTENIDOS

1. [Visión General](#1-visión-general)
2. [Filosofía de Diseño](#2-filosofía-de-diseño)
3. [Los 3 MEGA-WORKFLOWS](#3-los-3-mega-workflows)
4. [Arquitectura Hub & Spoke](#4-arquitectura-hub--spoke)
5. [Estado Actual de Implementación](#5-estado-actual-de-implementación)
6. [Infraestructura Técnica](#6-infraestructura-técnica)
7. [Estándares y Convenciones](#7-estándares-y-convenciones)
8. [Roadmap de Implementación](#8-roadmap-de-implementación)

---

## 1. VISIÓN GENERAL

### 1.1 Objetivo del Sistema

Construir un ecosistema de automatización que permita a Carrillo Abogados:
- Escalar de **20 leads/mes** (gestión manual) a **300+ leads/mes** (automatizado)
- Captar **100+ clientes nuevos/año** con ingresos proyectados de **$350M+ COP**
- Reducir tiempo de gestión de marketing de **20+ horas** a **5 horas/semana**
- Implementar estrategia **Flywheel** para ciclo perpetuo de engagement

### 1.2 Contexto de Negocio

| Aspecto | Detalle |
|---------|---------|
| **Cliente** | Carrillo Abogados (Firma legal colombiana) |
| **Especialización** | Propiedad Intelectual, Registro de Marcas, Contratación Estatal |
| **Mercado Objetivo** | 1,678 PyMEs tecnológicas en Colombia |
| **Diferenciador** | Dr. Omar Carrillo - 15 años en SIC |
| **Budget Anual** | ~$68-70M COP |
| **Timeline** | Lanzamiento oficial: Marzo 2026 |

### 1.3 Modelo Estratégico: Motor Futuro + Flywheel

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     ESTRATEGIA FLYWHEEL + INBOUND                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                          ┌─────────────┐                                    │
│                          │   CLIENTE   │                                    │
│                          │  EN CICLO   │                                    │
│                          └──────┬──────┘                                    │
│                                 │                                           │
│           ┌─────────────────────┼─────────────────────┐                     │
│           │                     │                     │                     │
│           ▼                     ▼                     ▼                     │
│    ┌─────────────┐       ┌─────────────┐       ┌─────────────┐             │
│    │  CONTENIDO  │       │  SERVICIOS  │       │  REFERIDOS  │             │
│    │    SEO      │◄─────►│   LEGALES   │◄─────►│  & RECOMPRA │             │
│    │  (MW#3)     │       │   (MW#1)    │       │   (MW#2)    │             │
│    └─────────────┘       └─────────────┘       └─────────────┘             │
│           │                     ▲                     │                     │
│           │                     │                     │                     │
│           └─────────────────────┴─────────────────────┘                     │
│                                 │                                           │
│                          ┌──────┴──────┐                                    │
│                          │  ATRACCIÓN  │                                    │
│                          │   ORGÁNICA  │                                    │
│                          └─────────────┘                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

MOTOR FUTURO (100% de los 3 MEGA-WORKFLOWS):
• MW#1: Captura y conversión de leads (pauta + formularios)
• MW#2: Retención y reactivación (email marketing + flywheel)  
• MW#3: Fábrica de contenido SEO (tráfico orgánico)
```

**Principio Flywheel aplicado:**
> El cliente está **constantemente siendo nutrido y educado**, manteniéndolo en un **ciclo de compromiso perpetuo**. Esto facilita la decisión en ciclos de venta largos (típico de servicios legales B2B).

---

## 2. FILOSOFÍA DE DISEÑO

### 2.1 Principio Rector

> **"Agrupa por COHESIÓN funcional, separa por FRAGILIDAD técnica"**

Este principio guía todas las decisiones arquitectónicas:
- ✅ **Visibilidad de negocio:** 3 MEGA-WORKFLOWS conceptuales (visión estratégica)
- ✅ **Pragmatismo técnico:** Sub-workflows modulares (realidad de n8n)

### 2.2 Reglas de Diseño: ¿Cuándo Separar vs. Agrupar?

#### ✅ SEPARA en sub-workflow si:

| Criterio | Ejemplo | Razón |
|----------|---------|-------|
| Tiene webhook trigger propio | Mailersend eventos (SUB-E) | Necesita endpoint dedicado |
| Puede fallar sin detener proceso crítico | Newsletter envío | Falla aislada no mata el flujo principal |
| Se ejecuta en horarios diferentes | SEO tracking (schedule) vs Captura (real-time) | Mixing triggers es complejo |
| Requiere retry logic sofisticada | AI Content Writer | Retry en monolito causa duplicados |
| Es candidato a cambios frecuentes | Prompts de IA para contenido | Cambios aislados reducen riesgo |

#### ⛔ AGRUPA en mismo workflow si:

| Criterio | Ejemplo | Razón |
|----------|---------|-------|
| Siempre se ejecutan en secuencia | Normalizar → Validar → Clasificar | No hay beneficio en separarlos |
| Comparten el mismo contexto de datos | Agentes operando sobre mismo lead | Pasar datos entre workflows genera latencia |
| Fallan juntos o pasan juntos | Si no puedes normalizar, no puedes clasificar | Cohesión alta |
| Son rápidos (<5 segundos) | Validación de email | Overhead de sub-workflow no justifica |
| No tienen lógica condicional compleja | Secuencia lineal A→B→C | Debuggear secuencia simple es fácil |

---

## 3. LOS 3 MEGA-WORKFLOWS

### 3.1 Mapa Conceptual Estratégico

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    ECOSISTEMA DE AUTOMATIZACIÓN                             │
│                      CARRILLO ABOGADOS 2026                                 │
└─────────────────────────────────────────────────────────────────────────────┘

     ┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
     │  MEGA-WORKFLOW   │      │  MEGA-WORKFLOW   │      │  MEGA-WORKFLOW   │
     │       #1         │      │       #2         │      │       #3         │
     │                  │      │                  │      │                  │
     │   CAPTURA Y      │      │   RETENCIÓN Y    │      │   FÁBRICA DE     │
     │   CONVERSIÓN     │      │   REACTIVACIÓN   │      │  CONTENIDO SEO   │
     │                  │      │                  │      │                  │
     │   7 workflows    │      │   5 workflows    │      │   5 workflows    │
     │   108 nodos      │      │   72 nodos       │      │   60 nodos       │
     └────────┬─────────┘      └────────┬─────────┘      └────────┬─────────┘
              │                         │                         │
              │                         │                         │
    ┌─────────▼─────────┐    ┌─────────▼─────────┐    ┌─────────▼─────────┐
    │ Lead → Cliente    │    │ Cliente → Recompra│    │ Tráfico → Lead    │
    │ Formularios/Pauta │    │ Email Marketing   │    │ Contenido Orgánico│
    │ Respuesta inmediata│   │ Flywheel perpetuo │    │ SEO + Blog        │
    └───────────────────┘    └───────────────────┘    └───────────────────┘
              │                         │                         │
              │                         │                         │
              └─────────────────────────┴─────────────────────────┘
                                        │
                              GRAN TOTAL: 17 workflows
                                       240 nodos
```

### 3.2 Detalle de MEGA-WORKFLOWS

| # | Nombre | Objetivo Principal | Estrategia | Timeline | Estado |
|---|--------|-------------------|------------|----------|--------|
| **1** | Captura y Conversión | Lead → Cliente | Inbound + Pauta | Q4 2025 - Q1 2026 | 🟡 En progreso |
| **2** | Retención y Reactivación | Cliente → Recompra/Referido | Flywheel + Email Mkt | Q2 2026 | ⚪ Planificado |
| **3** | Fábrica de Contenido SEO | Tráfico → Lead | Content Marketing | Q2-Q3 2026 | ⚪ Planificado |

### 3.3 Interacción entre MEGA-WORKFLOWS (Ciclo Flywheel)

```
                              ┌─────────────────────┐
                              │    VISITANTE WEB    │
                              │   (tráfico orgánico)│
                              └──────────┬──────────┘
                                         │
                    MW#3 genera          │  Contenido SEO atrae
                    contenido ───────────┤  visitantes cualificados
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │        LEAD         │
                              │  (formulario/pauta) │
                              └──────────┬──────────┘
                                         │
                    MW#1 convierte       │  Captura, clasifica,
                    el lead ─────────────┤  responde, nurturing
                                         │
                                         ▼
                              ┌─────────────────────┐
                              │       CLIENTE       │
                              │   (servicio legal)  │
                              └──────────┬──────────┘
                                         │
                    MW#2 retiene         │  Email marketing,
                    y reactiva ──────────┤  newsletters, valor
                                         │
                              ┌──────────┴──────────┐
                              │                     │
                              ▼                     ▼
                    ┌─────────────────┐   ┌─────────────────┐
                    │    RECOMPRA     │   │    REFERIDO     │
                    │   (upsell/     │   │   (nuevo lead   │
                    │   cross-sell)   │   │    orgánico)    │
                    └────────┬────────┘   └────────┬────────┘
                             │                     │
                             └──────────┬──────────┘
                                        │
                                        ▼
                              ┌─────────────────────┐
                              │   CICLO PERPETUO    │
                              │      FLYWHEEL       │
                              └─────────────────────┘
```

---

## 4. ARQUITECTURA HUB & SPOKE

### 4.1 Patrón General

Cada MEGA-WORKFLOW sigue el mismo patrón arquitectónico:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         MEGA-WORKFLOW #X                                │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────────┐
                    │     ORQUESTADOR         │
                    │        (HUB)            │
                    │                         │
                    │  • Recibe eventos       │
                    │  • Clasifica intención  │
                    │  • Enruta a spokes      │
                    │  • Consolida respuestas │
                    └───────────┬─────────────┘
                                │
            ┌───────────────────┼───────────────────┐
            │                   │                   │
            ▼                   ▼                   ▼
    ┌───────────────┐   ┌───────────────┐   ┌───────────────┐
    │    SUB-X      │   │    SUB-Y      │   │    SUB-Z      │
    │   (SPOKE)     │   │   (SPOKE)     │   │   (SPOKE)     │
    │               │   │               │   │               │
    │  Función      │   │  Función      │   │  Función      │
    │  especializada│   │  especializada│   │  especializada│
    └───────────────┘   └───────────────┘   └───────────────┘
```

### 4.2 Inventario Completo de Workflows

#### MEGA-WORKFLOW #1: CAPTURA Y CONVERSIÓN
| ID | Nombre | Nodos | Función |
|----|--------|------:|---------|
| **A** | Lead Lifecycle Manager | 20 | Orquestador principal |
| **SUB-A** | Lead Intake & Enrichment | 15 | Captura y clasificación |
| **SUB-B** | Hot Lead Notification | 8 | Notificación multi-canal |
| **SUB-C** | Instant Response Email | 12 | Respuesta IA inmediata |
| **SUB-D** | Nurturing Sequence Engine | 25 | Secuencia 8-12 emails |
| **SUB-E** | Engagement Tracker | 10 | Opens/clicks tracking |
| **SUB-F** | Meeting Scheduler | 18 | Integración Calendly |
| **TOTAL** | - | **108** | - |

#### MEGA-WORKFLOW #2: RETENCIÓN Y REACTIVACIÓN
| ID | Nombre | Nodos | Función |
|----|--------|------:|---------|
| **B** | Client Relationship Manager | 15 | Orquestador Flywheel |
| **SUB-G** | Client Segmentation | 10 | Segmentación por comportamiento |
| **SUB-H** | Newsletter Generator | 12 | Motor de contenido de valor |
| **SUB-I** | Dormant Reactivation | 20 | Recuperación de inactivos |
| **SUB-J** | Upsell Detector | 15 | Identificación de oportunidades |
| **TOTAL** | - | **72** | - |

#### MEGA-WORKFLOW #3: FÁBRICA DE CONTENIDO SEO
| ID | Nombre | Nodos | Función |
|----|--------|------:|---------|
| **C** | Content Factory Manager | 12 | Orquestador SEO |
| **SUB-K** | Keyword Research | 8 | Investigación de palabras clave |
| **SUB-L** | Content Writer AI | 18 | Agente redactor IA |
| **SUB-M** | Content Publisher | 10 | Publicación en CMS |
| **SUB-N** | SEO Performance Tracker | 12 | Monitoreo de ranking |
| **TOTAL** | - | **60** | - |

**GRAN TOTAL: 17 workflows, 240 nodos**

---

## 5. ESTADO ACTUAL DE IMPLEMENTACIÓN

### 5.1 Workflows en n8n Cloud (2025-12-19)

| ID | Nombre | Estado | Nodos | MEGA-WF |
|----|--------|--------|------:|---------|
| `7yRMJecj0TaIdinU` | WORKFLOW A: Lead Lifecycle Manager | 🟢 ACTIVO | 5 | #1 |
| `RHj1TAqBazxNFriJ` | SUB-A: Lead Intake & Enrichment (v2) | 🟡 Validado | 9 | #1 |
| `tpCV4mNjRiuOjeM8` | SUB-A: Lead Intake (PRUEBA) | ⬛ Legacy | 12 | #1 |

### 5.2 Progreso por MEGA-WORKFLOW

| MEGA-WORKFLOW | Componentes | Implementados | % Avance |
|---------------|------------:|---------------|----------|
| #1 Captura y Conversión | 7 | 2 | 28% |
| #2 Retención y Reactivación | 5 | 0 | 0% |
| #3 Fábrica de Contenido SEO | 5 | 0 | 0% |
| **TOTAL** | **17** | **2** | **12%** |

---

## 6. INFRAESTRUCTURA TÉCNICA

### 6.1 Stack Tecnológico

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         STACK TECNOLÓGICO                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ORQUESTACIÓN           ALMACENAMIENTO        COMUNICACIÓN              │
│  ┌─────────────┐        ┌─────────────┐       ┌─────────────┐          │
│  │   n8n       │        │  Firestore  │       │   Gmail     │          │
│  │   Cloud     │        │   (GCP)     │       │   API       │          │
│  └─────────────┘        └─────────────┘       └─────────────┘          │
│                                                                         │
│  INTELIGENCIA           EMAIL MARKETING       CONTENIDO                 │
│  ┌─────────────┐        ┌─────────────┐       ┌─────────────┐          │
│  │   Google    │        │ Mailersend  │       │  WordPress  │          │
│  │   Gemini    │        │  (futuro)   │       │   / CMS     │          │
│  └─────────────┘        └─────────────┘       └─────────────┘          │
│                                                                         │
│  SEO / ANALYTICS        AGENDAMIENTO          INVESTIGACIÓN             │
│  ┌─────────────┐        ┌─────────────┐       ┌─────────────┐          │
│  │   Google    │        │  Calendly   │       │   Ahrefs    │          │
│  │   Search    │        │  (futuro)   │       │ / SEMrush   │          │
│  └─────────────┘        └─────────────┘       └─────────────┘          │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 6.2 Credenciales Configuradas

| Servicio | ID Credencial | Estado | Usado en |
|----------|--------------|--------|----------|
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | ✅ Activo | MW#1, MW#2 |
| Firestore | `AAhdRNGzvsFnYN9O` | ✅ Activo | Todos |
| Google Gemini | `jk2FHcbAC71LuRl2` | ✅ Activo | MW#1, MW#3 |
| Mailersend | - | ⚪ Pendiente | MW#2 |
| WordPress REST | - | ⚪ Pendiente | MW#3 |
| Google Search Console | - | ⚪ Pendiente | MW#3 |

---

## 7. ESTÁNDARES Y CONVENCIONES

### 7.1 Nomenclatura de Workflows

| Tipo | Formato | Ejemplo |
|------|---------|---------|
| Orquestador MW#1 | `WORKFLOW A: [Nombre]` | WORKFLOW A: Lead Lifecycle Manager |
| Orquestador MW#2 | `WORKFLOW B: [Nombre]` | WORKFLOW B: Client Relationship Manager |
| Orquestador MW#3 | `WORKFLOW C: [Nombre]` | WORKFLOW C: Content Factory Manager |
| Sub-workflow | `SUB-[LETRA]: [Nombre]` | SUB-G: Client Segmentation |

### 7.2 Estructura de Carpetas del Proyecto

```
n8n-antigravity/
├── 02-context/
│   └── technical/
│       └── arquitectura/           # ← DOCUMENTACIÓN ACTUAL
│           ├── 00_ARQUITECTURA_GENERAL.md
│           ├── 01_MEGA_WORKFLOW_1_CAPTURA.md
│           ├── 02_MEGA_WORKFLOW_2_RETENCION.md
│           └── 03_MEGA_WORKFLOW_3_SEO.md
├── 04-workflows/
│   ├── MEGA_WORKFLOW_1_LEAD_LIFECYCLE/
│   ├── MEGA_WORKFLOW_2_RETENTION/      # ← Por crear
│   └── MEGA_WORKFLOW_3_SEO_CONTENT/    # ← Por crear
```

### 7.3 Estados de Workflows

| Emoji | Estado | Descripción |
|-------|--------|-------------|
| ⚪ | Planificado | Definido pero no iniciado |
| 📋 | Especificado | Spec completa, pendiente implementación |
| 🔨 | En desarrollo | Implementación en progreso |
| 🧪 | En testing | Testing y validación |
| 🟡 | Listo | Validado, pendiente activación |
| 🟢 | Activo | En producción |
| 🔴 | Error | Requiere intervención |
| ⬛ | Deprecated | Versión antigua, no usar |

---

## 8. ROADMAP DE IMPLEMENTACIÓN

### 8.1 Timeline General

```
2025                                    2026
Q4                                      Q1            Q2            Q3
├──────────────────────────────────────┼─────────────┼─────────────┼─────────────►
│                                      │             │             │
│ Nov-Dic: Pre-lanzamiento             │ Mar: LAUNCH │             │
│ • MW#1 Captura (MVP)                 │ • MW#1 100% │ • MW#2 100% │ • MW#3 100%
│                                      │             │ • MW#3 50%  │
│                                      │             │             │
└──────────────────────────────────────┴─────────────┴─────────────┴─────────────

Prioridad: MW#1 → MW#2 → MW#3 (secuencial por dependencias de negocio)
```

### 8.2 Dependencias entre MEGA-WORKFLOWS

```
MW#1 (Captura) ─────────────────────► MW#2 (Retención)
     │                                      │
     │  Genera clientes que                 │  Genera referidos que
     │  alimentan MW#2                      │  vuelven a MW#1
     │                                      │
     └──────────────────────────────────────┘
                      │
                      │  MW#3 genera tráfico
                      │  que alimenta MW#1
                      │
                      ▼
              MW#3 (SEO Content)
```

**Conclusión:** MW#1 es prerrequisito para MW#2 (necesitas clientes para retener). MW#3 puede desarrollarse en paralelo pero su valor se maximiza cuando MW#1 está funcionando.

---

## 📚 DOCUMENTOS RELACIONADOS

| Documento | Ubicación | Descripción |
|-----------|-----------|-------------|
| MEGA-WORKFLOW #1 | `01_MEGA_WORKFLOW_1_CAPTURA.md` | Captura y Conversión de Leads |
| MEGA-WORKFLOW #2 | `02_MEGA_WORKFLOW_2_RETENCION.md` | Retención y Reactivación (Flywheel) |
| MEGA-WORKFLOW #3 | `03_MEGA_WORKFLOW_3_SEO.md` | Fábrica de Contenido SEO |
| Guía MCP | `n8n_mcp_guide.md` | Herramientas MCP disponibles |
| CLAUDE.md | `../../../CLAUDE.md` | Configuración del proyecto |

---

**Última actualización:** 2025-12-19 | **Versión:** 2.1
