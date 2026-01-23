# MW#3: SEO Content Factory - STATUS

**Version:** 1.0
**Last Updated:** 2026-01-21
**Overall Status:** PLANNING (Phase 0)
**Target Completion:** Phase 0 - 24 January 2026

---

## QUICK REFERENCE

| Component | Status | n8n Workflow ID | Notes |
|-----------|--------|-----------------|-------|
| Orchestrator v2.0 (AI Agent) | NOT STARTED | - | Must use AI Agent architecture |
| SUB-K: Keyword Research | NOT STARTED | - | DataForSEO API (NOT SEMrush) |
| SUB-L: Content Writer AI | NOT STARTED | - | Gemini 2.0 Flash |
| SUB-M: Publisher | BLOCKED | - | Requires blog-service or CMS |
| SUB-N: SEO Tracker | NOT STARTED | - | Google Search Console API |

---

## CRITICAL DECISIONS (2026-01-21)

### DECISION 1: SEMrush API is NOT viable

**Problem:**
- SEMrush Pro ($5.75M COP/year) does NOT include API access
- SEMrush Business with API costs ~$500 USD/month
- This exceeds our entire marketing budget

**Solution Approved:**
- **Human use:** SEMrush Pro (manual research, site audits)
- **Robot use (n8n):** DataForSEO API (pay-as-you-go)
  - Cost: $50-100 USD = thousands of queries
  - Same data: Volume, KD, SERP analysis
  - n8n integration: HTTP Request node

### DECISION 2: blog-service depends on Alexis

**Problem:**
- Current frontend has hardcoded blog (`/blog` page)
- No backend API exists for blog posts
- SUB-M (Publisher) has nowhere to publish

**Options:**
| Option | Pros | Cons | Decision |
|--------|------|------|----------|
| A: WordPress | Zero dev, SEMrush integrates | Separate auth, inconsistent architecture | NO |
| B: blog-service (Spring Boot) | Consistent architecture, unified auth | Requires Alexis (8-10h) | PENDING |
| C: Webflow | Easy CMS, API available | External dependency, monthly cost | BACKUP |

**Action:** Escalate to Don Omar for decision with Alexis availability.

### DECISION 3: Architecture must match MW#1 v3.0

**Requirement:** Orchestrator must be AI Agent with Tools (Nate Herk methodology)
- NOT code-based Hub & Spoke (outdated)
- AI Agent decides which SUB-workflow to execute
- Easier to add new SUB-workflows in the future

---

## ARCHITECTURE v2.0 (APPROVED)

```
                    ┌─────────────────────────────────┐
                    │  ORCHESTRATOR: AI Agent         │
                    │  (Gemini 2.0 Flash)             │
                    │                                 │
                    │  Tools:                         │
                    │  - execute_keyword_research     │
                    │  - execute_content_writer       │
                    │  - execute_publisher            │
                    │  - execute_tracker              │
                    └─────────────┬───────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
        ▼                         ▼                         ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│    SUB-K      │         │    SUB-L      │         │    SUB-M      │
│   Keyword     │         │   Content     │         │   Content     │
│  Research    │         │  Writer AI    │         │  Publisher    │
│               │         │               │         │               │
│ DataForSEO   │    →    │ Gemini 2.0    │    →    │ blog-service  │
│ API          │         │ Flash         │         │ API           │
└───────────────┘         └───────────────┘         └───────────────┘
        │                         │                         │
        └─────────────────────────┼─────────────────────────┘
                                  │
                                  ▼
                          ┌───────────────┐
                          │    SUB-N      │
                          │ SEO Tracker   │
                          │               │
                          │ GSC + GA4 API │
                          └───────────────┘
```

---

## PHASE 0 TASKS (Due: 24 January 2026)

### TRACK A: Documentation & Structure (Marketing - Juan)

| ID | Task | Status | Owner | Notes |
|----|------|--------|-------|-------|
| A1 | Create folder structure per Agent Protocols | DONE | Juan | MW3_SEO_CONTENT_FACTORY/ |
| A2 | Create STATUS.md (this file) | DONE | Juan | Following protocols |
| A3 | Update CLAUDE.md with MW#3 info | NOT STARTED | Juan | Add to global memory |
| A4 | Create DataForSEO integration spec | DONE | Juan | [DATAFORSEO_INTEGRATION.md](../../docs/technical/DATAFORSEO_INTEGRATION.md) |
| A5 | Design SUB-K workflow (diagram + nodes) | NOT STARTED | Juan | JSON spec |
| A6 | Design SUB-L workflow (diagram + nodes) | NOT STARTED | Juan | JSON spec |
| A7 | Design Orchestrator v2.0 (AI Agent) | NOT STARTED | Juan | Copy MW#1 pattern |

### TRACK B: External Decisions (Requires Don Omar)

| ID | Task | Status | Owner | Notes |
|----|------|--------|-------|-------|
| B1 | Pay SEMrush Pro ($5.75M COP) | PENDING | Don Omar | Manual research only |
| B2 | Approve DataForSEO budget ($50-100 USD) | PENDING | Don Omar | For n8n automation |
| B3 | Decide blog backend (blog-service vs WordPress) | PENDING | Don Omar + Alexis | Blocking SUB-M |

### TRACK C: Backend Development (Requires Alexis)

| ID | Task | Status | Owner | Notes |
|----|------|--------|-------|-------|
| C1 | Create blog-service skeleton | BLOCKED | Alexis | Depends on B3 decision |
| C2 | BlogPost entity + CRUD API | BLOCKED | Alexis | Depends on C1 |
| C3 | Connect frontend /blog to API | BLOCKED | Alexis | Depends on C2 |

---

## SUB-WORKFLOW SPECS

### SUB-K: Keyword Research

**Trigger:** Schedule (1st of month) or Manual
**API:** DataForSEO (NOT SEMrush)
**Output:** Firestore `keywords_pipeline` collection

**DataForSEO Endpoints to Use:**
```
POST /v3/dataforseo_labs/google/keyword_ideas/live
POST /v3/dataforseo_labs/google/bulk_keyword_difficulty/live
POST /v3/serp/google/organic/live/advanced
```

**Filtering Criteria:**
- Volume >= 100/month
- Keyword Difficulty <= 30
- Words >= 3 (long-tail)
- Intent: informational OR transactional

**Priority Score Formula:**
```javascript
priority_score = (volume / 10) - kd + (cpc * 5)
```

### SUB-L: Content Writer AI

**Trigger:** Called by Orchestrator when keywords available
**Output:** Firestore `content_drafts` collection with status `pending_revision`

#### EVOLUCIÓN PLANIFICADA (Metodología Nate Herk)

**Fase 0 (Semana 1) - Workflow Simple:**
```
[Get next keyword] → [Gemini 2.0 Flash genera artículo] → [Save draft] → [Notify Juan]
```
- 1 solo agente IA
- Prompt básico (prompting reactivo)
- Iterar basado en calidad de output

**Fase 1 (Semana 2-3) - Sistema Multiagente:**
```
┌─────────────────────────────────────────────────────┐
│  AGENTE ORQUESTADOR: "Content Creation Manager"    │
└─────────────────────────────────────────────────────┘
                        │
    ┌───────────────────┼───────────────────┐
    ▼                   ▼                   ▼
┌────────┐        ┌────────┐         ┌────────┐
│Planner │   →    │ Writer │    →    │Evaluate│
│Gemini  │        │Gemini  │         │Gemini  │
│Flash   │        │2.0Flash│         │Flash   │
└────────┘        └────────┘         └────────┘
                        │
                        ▼
                  ┌────────┐
                  │Research│ (opcional - Perplexity API)
                  └────────┘
```

**Subagentes y sus roles:**
| Subagente | LLM | Costo/1K tokens | Función |
|-----------|-----|-----------------|---------|
| Planner | Gemini Flash | $0.001 | Define estructura (guía/checklist/FAQ) |
| Researcher | Perplexity API | $0.005 | Busca datos actuales (costos SIC 2026) |
| Writer | Gemini 2.0 Flash | $0.001 | Genera texto completo |
| Editor | Claude 3.5 Sonnet | $0.003 | Revisa tono/gramática/SEO |
| Evaluator | Gemini Flash | $0.001 | Califica (0-100), si <80 → retry |

**Criterio de transición Fase 0 → Fase 1:**
- Fase 0 funciona y genera borradores
- Tiempo de revisión humana > 30 min/artículo
- Calidad de primeros borradores < 70% aceptable

### SUB-M: Publisher

**Trigger:** When content_drafts.status = "aprobado"
**Destination:** blog-service API OR WordPress REST API
**Output:** Published URL + GSC indexing request

**BLOCKED:** Waiting for backend decision (B3)

### SUB-N: SEO Tracker

**Trigger:** Daily 6:00 AM
**APIs:** Google Search Console, Google Analytics 4
**Output:** Firestore `content_performance` + Google Sheets alerts

---

## FIRESTORE COLLECTIONS

### keywords_pipeline
```json
{
  "keyword_id": "kw_001",
  "keyword_text": "como registrar marca software colombia",
  "volume": 320,
  "kd": 22,
  "cpc": 2.50,
  "priority_score": 85,
  "category": "registro de marca",
  "source": "dataforseo",
  "status": "pendiente | en_progreso | publicado",
  "created_at": "2026-01-21T07:00:00Z"
}
```

### content_drafts
```json
{
  "content_id": "draft_001",
  "keyword_id": "kw_001",
  "title": "Como Registrar una Marca de Software en Colombia 2026",
  "meta_description": "Guia completa para registrar tu marca...",
  "slug": "registrar-marca-software-colombia",
  "content_markdown": "...",
  "word_count": 2150,
  "status": "pendiente_revision | aprobado | rechazado | publicado",
  "reviewer_notes": "",
  "published_url": null,
  "created_at": "2026-01-21T08:00:00Z",
  "approved_at": null,
  "approved_by": null
}
```

### content_performance
```json
{
  "performance_id": "perf_001",
  "content_id": "draft_001",
  "date": "2026-02-15",
  "position_avg": 8.5,
  "impressions": 1200,
  "clicks": 95,
  "ctr": 7.9,
  "position_change": -2,
  "alerts": ["Subio a Top 10"]
}
```

---

## INTEGRATION POINTS

### With MW#1 (Lead Capture)
```
Blog Article (MW#3)
    → CTA "Consulta Gratis"
    → Contact Form
    → MW#1 Lead Intake (SUB-A)
```

### With Backend Platform
```
SUB-M Publisher
    → HTTP POST /api/blog/posts
    → blog-service
    → PostgreSQL (blog schema)
    → Frontend fetches via API Gateway
```

### With Google APIs
```
SUB-N Tracker
    → Google Search Console API (rankings)
    → Google Analytics 4 API (traffic)
    → Firestore (store metrics)
    → Google Sheets (alerts dashboard)
```

---

## COST ANALYSIS

### Option A: DataForSEO (RECOMMENDED)

| Item | Cost | Frequency |
|------|------|-----------|
| Keyword Ideas API | ~$0.05 per 100 keywords | Monthly |
| Keyword Difficulty | ~$0.02 per keyword | Monthly |
| SERP Analysis | ~$0.01 per query | As needed |
| **Estimated Monthly** | **$20-50 USD** | - |

### Option B: SEMrush Business (NOT VIABLE)

| Item | Cost | Notes |
|------|------|-------|
| Business Plan | $499.95 USD/month | Required for API |
| API Units | Additional cost | Per-query charges |
| **Total** | **$500+ USD/month** | Exceeds budget |

---

## RISKS & MITIGATIONS

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| DataForSEO data quality | Low | Medium | Compare with SEMrush manual for validation |
| blog-service delayed | Medium | High | Fallback to WordPress REST API |
| Content quality issues | Medium | Medium | Human review mandatory before publish |
| API rate limits | Low | Low | Implement backoff and queuing |

---

## DELEGATION TICKETS (Para otros chats)

> [!NOTE]
> Estas tareas están listas para ser delegadas a chats separados.
> Cada ticket tiene contexto completo para trabajar de forma independiente.

### TICKET-MW3-001: DataForSEO Integration Spec - COMPLETED

**Prioridad:** P0 - Bloqueante para SUB-K
**Estado:** COMPLETADO (2026-01-22)
**Documento:** [DATAFORSEO_INTEGRATION.md](../../docs/technical/DATAFORSEO_INTEGRATION.md)

**Entregables completados:**
- [x] Documento tecnico `DATAFORSEO_INTEGRATION.md` en `docs/technical/`
- [x] Endpoints especificos documentados:
  - `POST /v3/dataforseo_labs/google/keyword_ideas/live`
  - `POST /v3/dataforseo_labs/google/bulk_keyword_difficulty/live`
  - `POST /v3/serp/google/organic/live/advanced`
- [x] Configuracion HTTP Request node para n8n (3 nodos JSON completos)
- [x] Mapping de response JSON a estructura Firestore
- [x] Ejemplos de request/response para cada endpoint
- [x] Estimacion de costos: ~$0.12/mes conservador, $50-100 USD cubre 6-12 meses
- [x] Code node para transformacion y calculo de priority_score
- [x] Checklist de implementacion

**Resumen de costos:**
| Endpoint | Costo estimado |
|----------|----------------|
| Keyword Ideas | ~$0.01-0.02/request |
| Bulk KD | ~$0.01/100 keywords |
| SERP Analysis | ~$0.002/query |

**Siguiente paso:** TICKET-MW3-002 (SUB-K Workflow Design)

---

### TICKET-MW3-002: SUB-K Workflow Design

**Prioridad:** P0 - Primera pieza del pipeline
**Dependencia:** TICKET-MW3-001 (DataForSEO spec)
**Entregables:**
- [ ] JSON del workflow para n8n Cloud
- [ ] Nodos requeridos:
  - Schedule Trigger (1st of month)
  - HTTP Request (DataForSEO API)
  - Code node (filtrar y calcular priority_score)
  - Firestore node (guardar en keywords_pipeline)
  - Gmail node (notificar resultados)
- [ ] Test data samples en `02-spokes/sub-k-keyword-research/test-data/`
- [ ] Documentar variables de entorno requeridas

**Lógica de filtrado:**
```javascript
// Criterios de filtrado
volume >= 100 AND kd <= 30 AND words >= 3

// Priority score
priority_score = (volume / 10) - kd + (cpc * 5)
```

---

### TICKET-MW3-003: SUB-L Workflow Design (Fase 0)

**Prioridad:** P1 - Después de SUB-K
**Contexto:** Implementar versión SIMPLE primero (prompting reactivo)
**Entregables:**
- [ ] JSON del workflow para n8n Cloud
- [ ] Nodos requeridos:
  - Execute Workflow Trigger (llamado por Orchestrator)
  - Firestore node (obtener siguiente keyword pendiente)
  - AI Agent node (Gemini 2.0 Flash)
  - Firestore node (guardar draft con status pending_revision)
  - Gmail node (notificar a Juan)
- [ ] System Prompt BÁSICO para Gemini (no masivo)
- [ ] Estructura de output esperada

**Prompt inicial (prompting reactivo):**
```
Eres experto en propiedad intelectual colombiana.
Escribe un artículo SEO de 2000+ palabras sobre: [KEYWORD]
Incluye: introducción, pasos/requisitos, costos, errores comunes, FAQ, conclusión con CTA.
Audiencia: dueños de PyMEs tecnológicas en Colombia.
```

**Evolución futura documentada en:** Sección 0.5 de `03_MEGA_WORKFLOW_3_SEO.md`

---

### TICKET-MW3-004: Orchestrator v2.0 Design

**Prioridad:** P1 - Integra todos los SUB-workflows
**Dependencia:** TICKET-MW3-002, TICKET-MW3-003
**Referencia:** Copiar patrón de MW#1 v3.0 (workflow ID: `68DDbpQzOEIweiBF`)
**Entregables:**
- [ ] JSON del workflow para n8n Cloud
- [ ] Arquitectura AI Agent con Tools:
  - Tool: execute_keyword_research → SUB-K
  - Tool: execute_content_writer → SUB-L
  - Tool: execute_publisher → SUB-M (placeholder)
  - Tool: execute_tracker → SUB-N (placeholder)
- [ ] System Prompt del orquestador
- [ ] Google Sheets logger (como MW#1)
- [ ] Webhook trigger path: `/webhook/content-factory`

**System Prompt base:**
```
Eres el Content Factory Manager de Carrillo Abogados.
Tu trabajo es coordinar la producción de contenido SEO.

Tienes estas herramientas:
- execute_keyword_research: Ejecuta investigación de keywords (1x/mes)
- execute_content_writer: Genera borradores de artículos
- execute_publisher: Publica contenido aprobado (NO DISPONIBLE AÚN)
- execute_tracker: Monitorea rendimiento SEO (NO DISPONIBLE AÚN)

Decide qué herramienta usar según el contexto del mensaje recibido.
```

---

### TICKET-MW3-005: SUB-L Multiagente Design (Fase 1)

**Prioridad:** P2 - Mejora futura
**Dependencia:** TICKET-MW3-003 funcionando en producción
**Criterio de inicio:**
- Fase 0 genera borradores
- Tiempo revisión humana > 30 min/artículo
- Calidad < 70% aceptable

**Entregables:**
- [ ] Arquitectura de 5 subagentes documentada
- [ ] JSON de cada subagente workflow
- [ ] Integración con Perplexity API (Researcher)
- [ ] Lógica de retry si Evaluator score < 80
- [ ] Comparativa de costos vs Fase 0

**Subagentes:**
| Subagente | LLM | Función |
|-----------|-----|---------|
| Planner | Gemini Flash | Define estructura del artículo |
| Researcher | Perplexity API | Busca datos actualizados |
| Writer | Gemini 2.0 Flash | Genera texto completo |
| Editor | Claude 3.5 Sonnet | Revisa tono/gramática/SEO |
| Evaluator | Gemini Flash | Califica calidad (0-100) |

---

## NEXT STEPS (Immediate)

1. **Juan (Hoy):** Delegar TICKET-MW3-001 a chat separado
2. **Juan (Hoy):** Delegar TICKET-MW3-002 a chat separado
3. **Don Omar (Esta semana):** Aprobar presupuesto DataForSEO ($50-100 USD)
4. **Don Omar + Alexis (Esta semana):** Decidir blog backend
5. **Juan (Post-aprobación):** Implementar SUB-K en n8n Cloud

---

## CHANGELOG

| Date | Version | Changes |
|------|---------|---------|
| 2026-01-22 | 1.1 | TICKET-MW3-001 completed: DataForSEO integration spec |
| 2026-01-21 | 1.0 | Initial STATUS.md created following Agent Protocols |

---

**Next Review:** 2026-01-24 (End of Phase 0)
