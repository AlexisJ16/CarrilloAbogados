# 🤖 Automatizaciones n8n - Carrillo Abogados

**Rama**: `automation`  
**Última Actualización**: 4 de Enero, 2026  
**Estado**: ✅ Integrado desde n8n-antigravity

---

## 📋 Descripción

Este directorio contiene todo el sistema de automatización de marketing integrado con n8n Cloud para Carrillo Abogados. El proyecto fue migrado desde el repositorio separado `n8n-antigravity` para centralizar toda la infraestructura del bufete.

## 🏗️ Estructura del Directorio

```text
automation/
├── README.md                    # Este archivo
├── .claude/
│   └── agents/                  # Agentes Claude para desarrollo
│       ├── architect.md         # Diseño de arquitectura n8n
│       ├── engineer.md          # Implementación de workflows
│       ├── optimizer.md         # Optimización de workflows
│       ├── qa-specialist.md     # Calidad y testing
│       └── validator.md         # Validación de configuraciones
│
└── n8n-workflows/
    ├── CLAUDE.md                # Contexto para Claude AI
    ├── README.md                # Documentación original del proyecto
    │
    ├── context/                 # Contexto del proyecto
    │   ├── business/            # Documentos de negocio
    │   │   ├── DOFA_Analisis.pdf
    │   │   ├── Framework_Marketing.pdf
    │   │   └── Presupuesto_Marketing.pdf
    │   └── technical/           # Documentación técnica
    │       ├── n8n_mcp_guide.md     # Guía MCP de n8n
    │       ├── NODE_STANDARDS.md    # Estándares de nodos
    │       └── arquitectura/        # Arquitectura de MEGA-WORKFLOWS
    │           ├── 00_ARQUITECTURA_GENERAL.md
    │           ├── 01_MEGA_WORKFLOW_1_CAPTURA.md
    │           ├── 02_MEGA_WORKFLOW_2_RETENCION.md
    │           └── 03_MEGA_WORKFLOW_3_SEO.md
    │
    ├── agents/                  # Definición de agentes
    │   └── architect/           # Agente arquitecto
    │
    ├── workflows/               # Workflows implementados
    │   └── MEGA_WORKFLOW_1_LEAD_LIFECYCLE/
    │       ├── 01-orchestrator/     # Orquestador Hub
    │       ├── 02-spokes/           # Sub-workflows
    │       ├── artifacts/           # Artefactos generados
    │       ├── testing/             # Tests
    │       └── versions/            # Versiones
    │
    ├── templates/               # Templates reutilizables
    └── outputs/                 # Workflows listos para producción
```

## 🎯 Los 3 MEGA-WORKFLOWS

### Arquitectura Hub & Spoke

Cada MEGA-WORKFLOW sigue un patrón de **Orquestador (Hub)** y **Sub-workflows especializados (Spokes)**:

| MEGA-WORKFLOW       | Propósito                        | Workflows | Nodos | Estado        |
| ------------------- | -------------------------------- | --------- | ----- | ------------- |
| **MW#1: Captura**   | Lead → Cliente (<1 min)          | 7         | 108   | 🔄 28%        |
| **MW#2: Retención** | Cliente → Recompra (Flywheel)    | 5         | 72    | 📋 Q2 2026    |
| **MW#3: SEO**       | Tráfico → Lead (Content Factory) | 5         | 60    | 📋 Q2-Q3 2026 |

**Total**: 17 workflows, 240 nodos planificados

### MW#1: Lead Lifecycle Manager (En Desarrollo)

```text
Formulario Web → NATS → n8n-integration-service → n8n Cloud
                                                       │
                    ┌──────────────────────────────────┘
                    ▼
           ┌───────────────┐
           │ Orquestador   │ ◄─── Hub principal
           │ Lead Lifecycle│
           └───────┬───────┘
                   │
      ┌────────────┼────────────────────────┐
      ▼            ▼            ▼           ▼
   SUB-A        SUB-B       SUB-C       SUB-D...
   Intake     Hot Alert   Response    Nurturing
```

## 🔌 Integración con la Plataforma

### Flujo de Eventos

```text
Portal Web (Frontend)
        │
        ▼
client-service (Java/Spring)
        │
        ├─── Emit: lead.capturado → NATS
        │
n8n-integration-service (Puerto 8800)
        │
        ├─── Subscribe: NATS topics
        ├─── Forward: Webhooks n8n Cloud
        │
        ▼
n8n Cloud (carrilloabgd.app.n8n.cloud)
        │
        ├─── Procesa con AI (Gemini)
        ├─── Guarda en Firestore
        ├─── Envía email (Gmail API)
        │
        └─── Callback: /webhook/lead-scored
                        │
                        ▼
             client-service (actualiza BD)
```

### Eventos NATS

| Evento             | Origen           | Destino n8n | Acción                 |
| ------------------ | ---------------- | ----------- | ---------------------- |
| `lead.capturado`   | client-service   | MW#1 SUB-A  | Scoring + Respuesta IA |
| `cita.agendada`    | calendar-service | MW#1 SUB-F  | Confirmación           |
| `caso.cerrado`     | case-service     | MW#2        | Follow-up satisfacción |
| `cliente.inactivo` | client-service   | MW#2        | Reactivación           |

## 🔧 Agentes Claude

Los agentes Claude están diseñados para asistir en el desarrollo de workflows:

| Agente           | Propósito                            |
| ---------------- | ------------------------------------ |
| `architect`      | Diseño de arquitectura de workflows  |
| `engineer`       | Implementación de nodos y conexiones |
| `qa-specialist`  | Testing y validación                 |
| `optimizer`      | Optimización de rendimiento          |
| `validator`      | Validación de configuraciones        |

## 📊 Métricas Objetivo

| Métrica          | Actual | Objetivo | Impacto           |
| ---------------- | -----: | -------: | ----------------- |
| Leads/mes        |     20 |    300+  | 15x crecimiento   |
| Tiempo respuesta |  4-24h |  < 1 min | 1440x más rápido  |
| Conversión       |    ~5% |    15%+  | 3x mejora         |

## 🔗 Documentación Relacionada

- [Arquitectura General](n8n-workflows/context/technical/arquitectura/00_ARQUITECTURA_GENERAL.md)
- [MW#1 Captura](n8n-workflows/context/technical/arquitectura/01_MEGA_WORKFLOW_1_CAPTURA.md)
- [MW#2 Retención](n8n-workflows/context/technical/arquitectura/02_MEGA_WORKFLOW_2_RETENCION.md)
- [MW#3 SEO](n8n-workflows/context/technical/arquitectura/03_MEGA_WORKFLOW_3_SEO.md)
- [Guía MCP n8n](n8n-workflows/context/technical/n8n_mcp_guide.md)
- [Estrategia de Automatización](../docs/business/ESTRATEGIA_AUTOMATIZACION.md)

## ⚠️ Notas Importantes

### Acceso a n8n Cloud

- **URL**: <https://carrilloabgd.app.n8n.cloud>
- **Versión**: 1.120.4
- **Plan**: Pro (Enterprise features)

### Credenciales Configuradas

- ✅ Gmail OAuth2 (carrilloabgd.com)
- ✅ Firestore (GCP)
- ✅ Google Gemini AI
- ⏳ Mailersend (pendiente)
- ⏳ Calendly (pendiente)

---

## 📅 Historial de Integración

| Fecha       | Acción                                    | Commit |
| ----------- | ----------------------------------------- | ------ |
| 4 Ene 2026  | Integración inicial desde n8n-antigravity | -      |

---

> Documentación generada como parte de la integración del proyecto n8n-antigravity a CarrilloAbogados
