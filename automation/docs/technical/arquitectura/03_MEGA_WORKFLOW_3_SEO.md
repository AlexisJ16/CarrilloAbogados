# 🔍 MEGA-WORKFLOW #3: FÁBRICA DE CONTENIDO SEO

**Versión:** 5.0
**Última actualización:** 2026-01-26
**Estado:** 🟡 EN DESARROLLO (SUB-L v2.0 DEPLOYED)
**Prioridad:** CRÍTICO - Habilitador de Pipeline de Contenido
**Status Tracking:** [STATUS.md](../../../workflows/MW3_SEO_CONTENT_FACTORY/STATUS.md)

> **IMPORTANTE v5.0:** Arquitectura actualizada para reflejar el **doble propósito de SUB-K** (Investigación para MAES + Producción para SUB-L) y su integración bidireccional con la Metodología Ágil de Estrategia SEO (MAES).

---

## 📋 TABLA DE CONTENIDOS

0. [Cambios Críticos v5.0](#0-cambios-críticos-v50-2026-01-26)
1. [Visión General](#1-visión-general)
2. [Análisis Crítico](#2-análisis-crítico)
3. [Arquitectura del MEGA-WORKFLOW](#3-arquitectura-del-mega-workflow)
4. [SUB-K: Keyword Research (Arquitectura Bidireccional)](#4-sub-k-keyword-research-arquitectura-bidireccional)
5. [SUB-L: Content Writer AI](#5-sub-l-content-writer-ai)
6. [SUB-M: Content Publisher](#6-sub-m-content-publisher)
7. [SUB-N: SEO Performance Tracker](#7-sub-n-seo-performance-tracker)
8. [Flujo de Datos (v5.0 Bidireccional)](#8-flujo-de-datos-v50---arquitectura-bidireccional)
9. [Plan de Implementación](#9-plan-de-implementación)
10. [Métricas de Éxito](#10-métricas-de-éxito)
11. [Documentos Relacionados](#-documentos-relacionados)

---

## 0. CAMBIOS CRÍTICOS v5.0 (2026-01-26)

> [!IMPORTANT]
> Esta versión actualiza la arquitectura de SUB-K para reflejar su **doble propósito bidireccional** en el ecosistema SEO.

### 0.1 SUB-K: Arquitectura Bidireccional (NUEVO)

**Problema identificado:**
- La documentación anterior asumía que SUB-K solo alimentaba SUB-L (flujo unidireccional)
- Se ignoraba el valor de DataForSEO API para la fase estratégica (MAES)
- Riesgo de duplicación: humano investigando manualmente lo que la API podría proveer

**Solución aprobada:**

SUB-K tiene **DOS PROPÓSITOS** (bidireccional):

| Uso | Propósito | Frecuencia | Input | Output | Consumidor |
|-----|-----------|------------|-------|--------|------------|
| **USO 1: INVESTIGACIÓN** | Alimentar análisis estratégico MAES | Ad-hoc (manual) | Seed keywords + competidores | Google Sheets temporal con 200+ keywords raw | **MAES (Notion)** |
| **USO 2: PRODUCCIÓN** | Ejecutar estrategia definida | Automático (semanal) | Keywords_Master Sheet (curado por MAES) | Firestore + trigger SUB-L | **SUB-L** |

### 0.2 Ciclo Completo MAES ↔ SUB-K

```
┌──────────────────────────────────────────────────────────────────┐
│                    CICLO BIDIRECCIONAL                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  [DataForSEO API]                                                │
│         ↓                                                        │
│  [SUB-K INVESTIGACIÓN] (ad-hoc, manual trigger)                  │
│         ↓                                                        │
│  Google Sheets "MAES_RawData" (200+ keywords)                    │
│         ↓                                                        │
│  [MAES - Metodología Ágil SEO] (Notion)                          │
│   • Fase 4: Análisis Competencia (usa datos API)                │
│   • Fase 5: Posicionamiento Actual                              │
│   • Fase 6: Planeación Estratégica                              │
│   • Juan + Claude AI → Decisiones estratégicas                  │
│         ↓                                                        │
│  Keywords_Master Sheet (curado, 50-100 keywords enabled=TRUE)    │
│         ↓                                                        │
│  [SUB-K PRODUCCIÓN] (automático, schedule semanal)               │
│         ↓                                                        │
│  Firestore keywords_pipeline (enabled=TRUE only)                 │
│         ↓                                                        │
│  [SUB-L Content Writer]                                          │
│         ↓                                                        │
│  Contenido alineado con estrategia MAES                          │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

**Beneficio clave:** DataForSEO se maximiza en ambas fases (investigación + producción). MAES toma decisiones estratégicas informadas por datos, no por intuición.

### 0.3 Integración con MAES (Metodología Ágil de Estrategia SEO)

**MAES:** Framework estratégico en 8 fases ejecutado en Notion por Juan + Claude AI.

**Timeline:**
- MAES: 15 horas (una vez) → Entrega Keywords_Master
- MW#3: Ciclo semanal continuo → Ejecuta estrategia MAES

**Documentación:**
- Integración detallada: [MAES_INTEGRATION.md](../../business/MAES_INTEGRATION.md)
- Fases de MAES: Kick-off, Estrategia, Objetivos, Competencia, Posicionamiento, Planeación, Contenidos, Verificación

**Separación de responsabilidades:**
- **MAES (humano + IA):** QUÉ crear y POR QUÉ (estrategia)
- **MW#3 (automatizado):** CÓMO ejecutar (producción)

> ⚠️ **INVESTIGACIÓN PENDIENTE:** SEO Programático - evaluar si complementa o reemplaza parte de este flujo (Fase 2 del proyecto)

### 0.4 Cambios de Arquitectura previos (v3.0 - v4.0)

Ver sección completa al final del documento: "Historial de Cambios v3.0-v4.0"

---

## 1. VISIÓN GENERAL

### 1.1 Propósito Estratégico

El **MEGA-WORKFLOW #3: Fábrica de Contenido SEO** implementa la estrategia **Inbound Marketing**:

> *"Enfoque reactivo que funciona respondiendo a las búsquedas activas de los usuarios mediante la creación de contenido específico. Ideal para captar demanda existente y posicionarse como autoridad."*

**Este workflow es una máquina de atracción que:**
1. **Investiga** qué buscan las PyMEs en Google sobre PI (via SUB-K Investigación)
2. **Estrategiza** con MAES qué contenido crear y por qué
3. **Ejecuta** la creación de contenido optimizado (via SUB-K Producción → SUB-L)
4. **Publica** automáticamente en el blog (SUB-M)
5. **Monitorea** el rendimiento y ajusta (SUB-N)

### 1.2 El Embudo Inbound

```
GOOGLE: "cómo registrar marca colombia"
                    │
                    ▼
    ┌───────────────────────────────┐
    │   ARTÍCULO SEO EN BLOG        │
    │   Guía completa de registro   │
    └───────────────┬───────────────┘
                    │
                    │  CTA: "Consulta gratis"
                    ▼
    ┌───────────────────────────────┐
    │       LEAD CAPTURADO          │  ◄── MW#1 toma el control
    └───────────────────────────────┘
```

### 1.3 KPIs Objetivo

| Métrica | Target Mes 3 | Target Mes 12 |
|---------|--------------|---------------|
| Artículos publicados | 12 | 50+ |
| Keywords en Top 10 | 5 | 25+ |
| Tráfico orgánico | 500/mes | 3,000+/mes |
| Leads desde SEO | 10/mes | 50+/mes |

### 1.4 Integración con MAES (Capa Estratégica)

**MAES (Metodología Ágil de Estrategia SEO):** Framework de 8 fases ejecutado en Notion.

**Rol de MAES en el ecosistema:**
- Define QUÉ keywords atacar (basado en objetivos de negocio, no solo métricas)
- Establece clusters temáticos y arquitectura de contenido
- Prioriza según "Motor Futuro" (PyMEs tech sin protección IP)
- Alimenta Keywords_Master Sheet como "contrato" con MW#3

**Flujo de integración:**
```
[MAES: Pensamiento estratégico] → [Keywords_Master: Contrato] → [MW#3: Ejecución]
         (Una vez + trimestral)                                    (Continuo)
```

**Documento detallado:** [MAES_INTEGRATION.md](../../business/MAES_INTEGRATION.md)

---

## 2. ANÁLISIS CRÍTICO

### 2.1 Riesgos y Mitigaciones

#### Riesgo 1: Contenido IA de baja calidad

**Mitigación:**
- Artículos de 2,000+ palabras con estructura SEO real
- Revisión humana OBLIGATORIA antes de publicar
- Datos específicos de PI colombiana (no genéricos)
- Briefs estratégicos de MAES guían el contenido

#### Riesgo 2: Keywords incorrectas

**Mitigación:**
- **MAES valida keywords ANTES** de entrar a MW#3
- Priorizar long-tail (3-5 palabras)
- Solo keywords con KD < 30 (baja competencia)
- Solo intención informacional/transaccional
- Column `enabled` en Keywords_Master = control humano

#### Riesgo 3: Publicar sin medir

**Mitigación:**
- SUB-N trackea cada artículo individualmente
- Dashboard semanal de rendimiento
- Optimizar o deprecar artículos que no funcionan
- Retroalimentación trimestral a MAES

#### Riesgo 4: Estrategia desalineada del negocio

**Mitigación:**
- MAES asegura alineación con "Motor Futuro"
- Don Omar aprueba estrategia ANTES de automatizar
- Keywords_Master documenta el "POR QUÉ" de cada keyword

### 2.2 Decisión de Arquitectura Clave

**¿Por qué separar MAES (estrategia) de MW#3 (ejecución)?**

```
INCORRECTO (confusión de responsabilidades):
[MW#3 con lógica estratégica embebida] → ❌ Complejidad, debugging imposible

CORRECTO (separación clara):
[MAES: Decisiones] → [Keywords_Master: Contrato] → [MW#3: Ejecución]
   (Humano + IA)           (Documento vivo)            (Automatizado)
```

**Ventajas:**
- Debugging simple: Problema estratégico → revisar MAES; Problema técnico → revisar MW#3
- Escalabilidad: Agregar keywords no requiere modificar workflows
- Trazabilidad: Keywords_Master documenta decisiones
- Control de calidad: Don Omar aprueba estrategia ANTES de gastar recursos

**¿Por qué el humano revisa ANTES de publicar?**

```
INCORRECTO:
[Generar] → [Publicar automático] → ❌ Contenido malo en producción

CORRECTO (nuestro diseño):
[Generar borrador] → [COLA REVISIÓN] → [Humano aprueba] → [Publicar]
```

El workflow genera **borradores en cola**. La publicación solo ocurre después de aprobación.

---

## 3. ARQUITECTURA DEL MEGA-WORKFLOW

### 3.1 Diagrama de Componentes (v5.0 - Arquitectura Bidireccional)

```
┌─────────────────────────────────────────────────────────────────────┐
│         MEGA-WORKFLOW #3: FÁBRICA DE CONTENIDO SEO v5.0             │
│                (Arquitectura Bidireccional con MAES)                 │
└─────────────────────────────────────────────────────────────────────┘

    ┌─────────────────────────────────────────────────────────┐
    │        CAPA ESTRATÉGICA (Fuera de n8n)                  │
    │                                                         │
    │   ┌──────────────────────────────────────────────┐     │
    │   │  MAES (Notion)                               │     │
    │   │  • 8 fases de estrategia SEO                 │     │
    │   │  • Ejecutado por Juan + Claude AI            │     │
    │   │  • Frecuencia: Una vez + trimestral          │     │
    │   └─────────────────┬────────────────────────────┘     │
    │                     │                                   │
    │                     │ Alimenta/Consume                  │
    │                     │                                   │
    └─────────────────────┼───────────────────────────────────┘
                          │
           ┌──────────────┴──────────────┐
           │                             │
           ▼                             ▼
    ┌─────────────┐             ┌─────────────┐
    │  SUB-K      │◄────────────│ Keywords_   │
    │  INVESTIG.  │  ad-hoc     │ Master      │
    │             │             │ (Google     │
    │ DataForSEO  │             │ Sheets)     │
    │ API         │             │             │
    │             │─────────────►│ Source of   │
    │ Output:     │  Sheets raw │ Truth       │
    │ MAES_RawData│             │             │
    └─────────────┘             └──────┬──────┘
                                       │
                          ┌────────────┴───────────────┐
                          │                            │
    ┌─────────────────────────────────────────────────────────────┐
    │            CAPA OPERATIVA (n8n automatizado)                │
    │                                                             │
    │                    ┌─────────────────────────────────┐     │
    │                    │  ORCHESTRATOR v2.0 (AI Agent)   │     │
    │                    │  Gemini 2.0 Flash               │     │
    │                    │  NOT IMPLEMENTED ⏳              │     │
    │                    └───────────────┬─────────────────┘     │
    │                                    │                       │
    │        ┌───────────────────────────┼──────────────┐        │
    │        │                           │              │        │
    │        ▼                           ▼              ▼        │
    │ ┌───────────────┐           ┌───────────────┐ ┌────────┐  │
    │ │    SUB-K      │           │    SUB-L      │ │ SUB-M  │  │
    │ │  PRODUCCIÓN   │           │   Content     │ │Content │  │
    │ │               │           │  Writer AI    │ │Publisher│ │
    │ │ NOT IMPL ⏳   │     →     │ DEPLOYED ✅   │→│BLOCKED❌│ │
    │ │               │           │               │ │        │  │
    │ │ Input:        │           │ Gemini 2.0    │ │ blog-  │  │
    │ │ Keywords_     │           │ Flash         │ │service │  │
    │ │ Master        │           │               │ │ API    │  │
    │ │ (enabled=TRUE)│           │ Google Drive  │ │        │  │
    │ │               │           │ Google Docs   │ │(await) │  │
    │ └───────────────┘           └───────────────┘ └────────┘  │
    │        │                           │              │        │
    │        │  Firestore cache          │              │        │
    │        │  (enabled=TRUE only)      │              │        │
    │        └───────────────────────────┼──────────────┘        │
    │                                    │                       │
    │                                    ▼                       │
    │                            ┌───────────────┐               │
    │                            │    SUB-N      │               │
    │                            │ SEO Tracker   │               │
    │                            │               │               │
    │                            │ NOT STARTED ❌│               │
    │                            │               │               │
    │                            │ GSC + GA4 API │               │
    │                            └───────┬───────┘               │
    │                                    │                       │
    └────────────────────────────────────┼───────────────────────┘
                                         │
                                         │ Métricas trimestrales
                                         │
                                         ▼
                                ┌─────────────────┐
                                │  Retroalimenta  │
                                │  a MAES         │
                                │  (Quarterly)    │
                                └─────────────────┘
```

### 3.2 Arquitectura de Datos Híbrida

| Componente | Storage Principal | Storage Secundario | Propósito |
|------------|-------------------|-------------------|-----------|
| **Keywords Estratégicos** | Google Sheets "Keywords_Master" | Firestore `keywords_pipeline` | Sheets=control humano + MAES, Firestore=cache operativo |
| **Keywords Investigación** | Google Sheets "MAES_RawData" | - | Datos raw de DataForSEO para análisis MAES |
| **Drafts** | Google Drive/Docs | - | WYSIWYG editing, colaboración |
| **Tracking** | Google Sheets "Content_Pipeline" | - | Visibilidad de pipeline |
| **Logs** | Google Sheets | - | Observabilidad |
| **Performance** | Firestore `content_performance` | - | Métricas SEO (futuro) |

### 3.3 Ciclo de Producción

| Fase | Frecuencia | Workflow/Proceso | Output |
|------|------------|------------------|--------|
| **Setup Estratégico** | Una vez (15h) | MAES en Notion | Keywords_Master Sheet |
| **Investigación** | Ad-hoc (durante MAES) | SUB-K Investigación | Datos raw para análisis |
| **Selección** | Una vez post-MAES | Juan marca enabled=TRUE/FALSE | Keywords validadas |
| **Sincronización** | Semanal | SUB-K Producción | Firestore actualizado |
| **Escritura** | Semanal | SUB-L | Borrador en Google Docs |
| **Revisión** | Asíncrona | HUMANO | Aprobado/Rechazado |
| **Publicación** | On-demand | SUB-M | Artículo en blog |
| **Monitoreo** | Diario | SUB-N | Métricas actualizadas |
| **Retroalimentación** | Trimestral | MAES revisión | Estrategia ajustada |

---

## 4. SUB-K: KEYWORD RESEARCH (ARQUITECTURA BIDIRECCIONAL)

### 4.1 Doble Propósito de SUB-K

**Innovación arquitectónica:** SUB-K opera en **DOS MODOS** con propósitos distintos.

**Motivación:**
- DataForSEO API ($50-100 USD) es inversión inicial
- Maximizar ROI usando API tanto para investigación como para producción
- MAES requiere datos actualizados de competencia/gaps (no solo intuición)
- Evitar trabajo manual repetitivo que la API puede automatizar

**Comparación de modos:**

| Dimensión | USO 1: INVESTIGACIÓN | USO 2: PRODUCCIÓN |
|-----------|---------------------|-------------------|
| **Consumidor** | MAES (Notion) | SUB-L (n8n) |
| **Propósito** | Enriquecer análisis estratégico | Ejecutar estrategia definida |
| **Trigger** | Manual (ad-hoc) | Automático (schedule semanal) |
| **Input** | Seed keywords + competidores | Keywords_Master (enabled=TRUE) |
| **Output** | Google Sheets "MAES_RawData" (200+ keywords raw) | Firestore + trigger SUB-L |
| **Filtros** | Mínimos (exploración amplia) | Estrictos (solo validados por MAES) |
| **Frecuencia** | Durante MAES + revisiones trimestrales | Continuo (semanal) |
| **Tiempo humano** | 2-3 horas análisis | 0 (automatizado) |

### 4.2 USO 1: INVESTIGACIÓN (para MAES)

**Contexto:** Se ejecuta durante las **Fases 4-6 de MAES** (Competencia, Posicionamiento, Planeación).

#### 4.2.1 Flujo Técnico - Modo Investigación

```
[Trigger: Manual - Webhook o botón en n8n]
         │
         │  Input (POST body):
         │  {
         │    "mode": "investigation",
         │    "seed_keywords": ["registro de marca", "propiedad intelectual"],
         │    "competitors": ["example.com", "competitor.co"],
         │    "location_code": 2170,  # Colombia
         │    "language_code": "es"
         │  }
         │
         ▼
[1. DataForSEO API - Keywords for Site]
    Endpoint: /v3/dataforseo_labs/google/keywords_for_site/live
    Para cada competidor: extraer keywords rankeando
         │
         ▼
[2. DataForSEO API - Related Keywords]
    Endpoint: /v3/dataforseo_labs/google/related_keywords/live
    Para cada seed keyword: expandir a relacionadas
         │
         ▼
[3. DataForSEO API - Keyword Ideas]
    Endpoint: /v3/dataforseo_labs/google/keyword_ideas/live
    Descubrir nuevas oportunidades
         │
         ▼
[4. Merge & Deduplicate]
    Combinar resultados de 3 endpoints
    Eliminar duplicados por keyword_text
         │
         ▼
[5. Filtros Mínimos (exploración amplia)]
    • Volumen >= 50/mes (más permisivo que producción)
    • KD <= 50 (más permisivo)
    • País = Colombia
         │
         ▼
[6. Calcular métricas adicionales]
    • priority_score = (volumen/10) - kd + (cpc*5)
    • keyword_gaps (keywords de competidores que nosotros NO tenemos)
    • serp_features (snippets, PAA, etc.)
         │
         ▼
[7. Guardar en Google Sheets: "MAES_RawData"]
    Estructura:
    • keyword_text, volume, kd, cpc, trend
    • serp_features, competitor_presence
    • priority_score, keyword_gap
    • Timestamp, source
         │
         ▼
[8. Notificar a Juan]
    Email: "SUB-K Investigación completado. 237 keywords exportadas.
           Link: [Google Sheets MAES_RawData]"
```

#### 4.2.2 Estructura de MAES_RawData Sheet

| Columna | Tipo | Descripción | Fuente |
|---------|------|-------------|--------|
| `keyword_text` | String | Keyword exacta | DataForSEO |
| `volume` | Integer | Búsquedas mensuales | DataForSEO |
| `kd` | Integer | Keyword Difficulty (0-100) | DataForSEO |
| `cpc` | Float | Costo por clic USD | DataForSEO |
| `trend` | String | Tendencia (↑↓→) | DataForSEO |
| `serp_features` | String (CSV) | Featured snippet, PAA, etc. | DataForSEO |
| `competitor_presence` | String (CSV) | Competidores rankeando | DataForSEO |
| `keyword_gap` | Boolean | TRUE si competidor la tiene y nosotros no | Calculado |
| `priority_score` | Integer | Score calculado | Calculado |
| `timestamp` | Datetime | Cuándo se extrajo | Sistema |
| `source` | String | Investigación | Sistema |

**Uso posterior:** Juan + Claude AI analizan esta data en **Fase 4 de MAES** para identificar gaps y oportunidades.

### 4.3 USO 2: PRODUCCIÓN (para SUB-L)

**Contexto:** Se ejecuta **semanalmente** para alimentar la fábrica de contenido. Lee keywords **YA validadas** por MAES.

#### 4.3.1 Flujo Técnico - Modo Producción

```
[Trigger: Schedule - Cron cada lunes 8:00 AM]
         │
         ▼
[1. Leer Keywords_Master (Google Sheets)]
    WHERE enabled = TRUE
      AND status = "pendiente"
    ORDER BY priority_score DESC
    LIMIT 3  # Keywords para la semana
         │
         ▼
[2. Para cada keyword seleccionada]
    │
    ├─► [2.1 Verificar si ya existe en Firestore]
    │   IF exists: skip
    │   ELSE: continuar
    │
    ├─► [2.2 (Opcional) Refresh métricas con DataForSEO]
    │   Verificar si volumen/KD sigue siendo válido
    │   (Opcional porque datos ya fueron validados por MAES)
    │
    ├─► [2.3 Preparar payload para SUB-L]
    │   {
    │     "keyword_id": "kw_001",
    │     "keyword_text": "cómo registrar marca software colombia",
    │     "cluster_name": "Registro de Marcas",
    │     "brief": {
    │       "titulo_propuesto": "...",
    │       "h2_sugeridos": [...],
    │       "palabras_target": "2000-2500",
    │       "tono": "Profesional educativo",
    │       "cta": "..."
    │     },
    │     "keywords_secundarias": ["registro marca app", ...],
    │     "url_target": "/blog/registro-marca-software"
    │   }
    │
    ├─► [2.4 Guardar en Firestore: keywords_pipeline]
    │   {
    │     "keyword_id": "kw_001",
    │     "keyword_text": "...",
    │     "status": "queued_for_content",
    │     "source": "keywords_master_production",
    │     "created_at": "2026-02-01T08:00:00Z"
    │   }
    │
    └─► [2.5 Actualizar Keywords_Master Sheet]
        SET status = "en_progreso"
        SET fecha_inicio = NOW()
         │
         ▼
[3. Trigger SUB-L para cada keyword]
    HTTP Request POST a SUB-L webhook
    Body: payload construido en 2.3
         │
         ▼
[4. Log en Google Sheets: MW3_Orchestrator_Logs]
    {
      "timestamp": "...",
      "event": "sub-k-production",
      "keywords_processed": 3,
      "status": "success"
    }
         │
         ▼
[5. Notificar a Juan]
    Email: "SUB-K Producción: 3 keywords enviadas a SUB-L.
           Keywords: [lista]"
```

#### 4.3.2 Estructura de Keywords_Master Sheet

**Sheet 1: Keywords Master (16 columnas)**

| Col | Campo | Tipo | Descripción | Fuente | Editado por |
|-----|-------|------|-------------|--------|-------------|
| A | `keyword_id` | String | ID único | Sistema | Sistema |
| B | `cluster_name` | String | Cluster temático | MAES | Juan |
| C | `keyword_text` | String | Keyword específica | DataForSEO | Juan |
| D | `tipo` | Enum | Head / Long-tail | MAES | Juan |
| E | `intencion` | Enum | Informacional / Transaccional / Comercial / Navegacional | MAES | Juan |
| F | `volume` | Integer | Búsquedas/mes | DataForSEO | Sistema |
| G | `kd` | Integer | Keyword Difficulty | DataForSEO | Sistema |
| H | `cpc` | Float | CPC en USD | DataForSEO | Sistema |
| I | **`enabled`** | **Boolean** | **TRUE/FALSE** - **CONTROL HUMANO** | Manual | **Juan** |
| J | `priority_score` | Formula | `=F/10-G+H*5` | Calculado | Sistema |
| K | `status` | Enum | pendiente / en_progreso / publicado | Sistema | Sistema |
| L | `url_target` | String | URL donde vivirá | MAES | Juan |
| M | `brief_notes` | Text | Notas del brief | MAES | Juan |
| N | `content_id` | String | ID del borrador | SUB-L | Sistema |
| O | `fecha_creacion` | Date | Cuándo se agregó | Sistema | Sistema |
| P | `fecha_publicacion` | Date | Cuándo se publicó | SUB-M | Sistema |

**Columna crítica `enabled` (Columna I):**
- **TRUE:** Keyword aprobada por MAES, sincroniza a Firestore, SUB-L la procesará
- **FALSE:** Keyword rechazada, NO sincroniza, permanece en Sheet para referencia

**Sheet 2: Briefs (detalle de clusters)**
- cluster_id, titulo_propuesto, h2_sugeridos, palabras_target, tono, cta, fuentes_referencia

**Sheet 3: Dashboard (vista ejecutiva)**
- Total keywords, por status, por cluster, performance

### 4.4 Flujo Técnico Unificado (Un solo workflow, dos configuraciones)

**Implementación técnica:** Un único workflow SUB-K en n8n con bifurcación según parámetro `mode`.

```
[Trigger Node]
├─ Webhook (para modo investigation)
└─ Schedule (para modo production)
         │
         ▼
[Switch: Detectar modo]
         │
    ┌────┴────┐
    │         │
    ▼         ▼
[INVESTIGATION]  [PRODUCTION]
(Nodos 1-8)      (Nodos 1-5)
    │              │
    └──────┬───────┘
           │
           ▼
    [Nodo común: Error Handler]
```

**Nodos totales estimados:** 13-15 (compartiendo error handling, Firestore logging)

---

## 5. SUB-L: CONTENT WRITER AI

### 5.1 Propósito

Generar borradores de artículos SEO de 2,000+ palabras que un humano pueda aprobar en minutos.

**Input mejorado (v5.0):** SUB-L ahora recibe no solo la keyword, sino el **brief estratégico de MAES**, asegurando alineación con estrategia.

### 5.2 Estructura del Artículo

```
ESTRUCTURA ESTÁNDAR SEO:

<title> Keyword Principal + Año
<meta description> 150-160 caracteres con keyword

<h1> Título con keyword

[Introducción: 150-200 palabras]

<h2> ¿Qué es [tema] y por qué importa?
<h2> Requisitos / Pasos
  <h3> Paso 1...
  <h3> Paso 2...
<h2> Costos [si aplica]
<h2> Errores comunes
<h2> Preguntas frecuentes (FAQ Schema)
<h2> Conclusión + CTA

[Total: 2,000-2,500 palabras]
```

### 5.3 Flujo Técnico (v2.0 - Google Workspace)

```
[Execute Workflow Trigger]
         │
         │  Input (desde SUB-K Production):
         │  {
         │    "keyword_text": "...",
         │    "brief": { ... },  # ← NUEVO en v5.0
         │    "keywords_secundarias": [...],
         │    "url_target": "..."
         │  }
         │
         ▼
[1. Validar Input]
    Verificar que brief tiene todos los campos requeridos
         │
         ▼
[2. Content Generator Agent (Gemini 2.0 Flash)]
    System Prompt enriquecido con brief de MAES:
    - Título propuesto: {brief.titulo_propuesto}
    - Estructura H2: {brief.h2_sugeridos}
    - Tono: {brief.tono}
    - CTA: {brief.cta}
    - Palabras target: {brief.palabras_target}
         │
         ▼
[3. Parse AI Output]
    Extraer: title, meta_description, content_html, url_slug
         │
         ▼
[4. Create Google Doc in "MW3_Drafts" folder]
    Título del doc: "[BORRADOR] {keyword_text}"
    Contenido: HTML formateado
         │
         ▼
[5. Update Google Sheets "Content_Pipeline"]
    Tab "Drafts":
    - content_id, keyword_text, google_doc_url
    - status: "pendiente_revision"
    - timestamp
         │
         ▼
[6. Update Keywords_Master Sheet]
    SET content_id = {content_id}
    SET status = "borrador_generado"
         │
         ▼
[7. Log to MW3_ContentWriter_Logs]
         │
         ▼
[8. Notify Juan - Success (Gmail)]
    Email: "Borrador listo para revisión: {keyword_text}
           Link a Google Doc: [URL]
           Brief aplicado: {brief.titulo_propuesto}"
```

**Workflow ID actual:** `ZcaEG8VDm1IcG3LF` (ACTIVE en n8n Cloud)
**Nodos:** 16
**Estado:** DEPLOYED ✅ (v2.0 Google Workspace)

### 5.4 Prompt de Generación (Mejorado v5.0)

```
Eres un experto en propiedad intelectual colombiana con 15 años
de experiencia en la SIC. Escribe un artículo de blog optimizado
para SEO sobre: "[KEYWORD]"

CONTEXTO ESTRATÉGICO (proporcionado por MAES):
- Cluster temático: {cluster_name}
- Audiencia objetivo: {brief.audiencia}  # PyMEs tech sin protección IP
- Propósito del contenido: {brief.proposito}

REQUISITOS TÉCNICOS:
- Longitud: {brief.palabras_target} palabras
- Tono: {brief.tono}
- Título propuesto: "{brief.titulo_propuesto}"
- Estructura H2 OBLIGATORIA:
  {brief.h2_sugeridos}

INCLUIR OBLIGATORIAMENTE:
1. Datos actualizados de costos SIC 2026
2. Tiempos reales del proceso (6-12 meses)
3. Al menos 2 errores comunes con ejemplos
4. Sección de FAQ con 5 preguntas frecuentes
5. CTA final: "{brief.cta}"

NO INCLUIR:
- Información genérica que aplique a cualquier país
- Promesas de resultados garantizados
- {brief.evitar}

KEYWORDS SECUNDARIAS (integrar naturalmente):
{keywords_secundarias}

El artículo debe posicionar a Carrillo Abogados como experto
sin ser un "infomercial".
```

**Mejora clave v5.0:** El brief de MAES asegura que el contenido está alineado con estrategia, no solo con métricas SEO.

---

## 6. SUB-M: CONTENT PUBLISHER

### 6.1 Propósito

Publicar artículos aprobados en el CMS (WordPress/blog-service) con toda la optimización SEO.

**Estado:** BLOCKED ❌ (esperando decisión blog-service)

### 6.2 Flujo Técnico

```
[Trigger: Keyword_Master status cambió a "aprobado"]
         │
         ▼
[1. Leer contenido aprobado de Google Doc]
         │
         ▼
[2. Formatear para CMS]
    - Convertir a HTML
    - Agregar headings con IDs
    - Insertar imágenes placeholder
    - Agregar internal links
    - Agregar external links (fuentes)
         │
         ▼
[3. Preparar SEO técnico]
    - Title tag
    - Meta description
    - URL slug (sin tildes, minúsculas)
    - Open Graph tags
    - FAQ Schema markup
         │
         ▼
[4. Publicar via API del CMS]
    WordPress: wp-json/wp/v2/posts
    blog-service: /api/blog/posts
         │
         ▼
[5. Actualizar Keywords_Master]
    SET status = "publicado"
    SET url_publicado = "[URL]"
    SET fecha_publicacion = NOW()
         │
         ▼
[6. Notificar éxito]
    "Artículo publicado: [URL]"
         │
         ▼
[7. Submit a Google Search Console]
    Solicitar indexación del nuevo URL
```

### 6.3 Checklist de Publicación

| Elemento | Validación |
|----------|------------|
| Title tag | 50-60 caracteres, incluye keyword |
| Meta description | 150-160 chars, incluye keyword, tiene CTA |
| URL | Sin tildes, sin mayúsculas, incluye keyword |
| H1 | Solo 1, incluye keyword |
| Imágenes | Alt text con keyword, comprimidas |
| Internal links | Mínimo 2-3 a otros artículos |
| External links | Mínimo 1-2 a fuentes (SIC, OMPI) |
| CTA | Presente al final del artículo |
| FAQ Schema | Implementado si hay sección FAQ |

---

## 7. SUB-N: SEO PERFORMANCE TRACKER

### 7.1 Propósito

Monitorear el rendimiento de cada artículo y detectar oportunidades de optimización.

**Estado:** NOT STARTED ❌

### 7.2 Métricas Trackeadas

| Métrica | Fuente | Frecuencia |
|---------|--------|------------|
| Posición promedio | Google Search Console | Diario |
| Impresiones | Google Search Console | Diario |
| Clicks | Google Search Console | Diario |
| CTR | Calculado | Diario |
| Tráfico por artículo | Google Analytics | Semanal |
| Conversiones (leads) | Firestore (MW#1) | Semanal |

### 7.3 Flujo Técnico

```
[Schedule: Diario 6:00 AM]
         │
         ▼
[1. Query Google Search Console API]
    Obtener datos de últimos 7 días
         │
         ▼
[2. Loop por cada artículo publicado]
    │
    │   Para cada URL en Keywords_Master (status=publicado):
    │   • Buscar métricas de GSC para esa URL
    │   • Calcular cambios vs semana anterior
    │
         ▼
[3. Guardar en Firestore: content_performance]
    {
      "content_id": "...",
      "url": "...",
      "fecha": "2026-02-15",
      "posicion_promedio": 8.5,
      "impresiones": 1200,
      "clicks": 95,
      "ctr": 7.9,
      "cambio_posicion": -2  // mejoró 2 posiciones
    }
         │
         ▼
[4. Detectar alertas]
    │
    IF posicion mejoró > 5 posiciones:
    │   └── Alerta positiva "🎉 [Artículo] subió a posición X"
    │
    IF posicion empeoró > 5 posiciones:
    │   └── Alerta negativa "⚠️ [Artículo] cayó, revisar"
    │
    IF nuevo keyword en Top 10:
        └── Oportunidad "💡 Nuevo ranking para [keyword]"
         │
         ▼
[5. Generar reporte trimestral (para MAES)]
    Email a Juan:
    • Top 5 artículos por tráfico
    • Artículos que subieron/bajaron
    • Keywords nuevas rankeando
    • Recomendaciones para ajustar estrategia MAES
         │
         ▼
[6. Actualizar Keywords_Master Sheet]
    Agregar columnas de performance:
    - posicion_actual, trafico_ultimo_mes, leads_generados
```

### 7.4 Lógica de Optimización Automática

| Señal | Diagnóstico | Recomendación |
|-------|-------------|---------------|
| Posición 11-20, CTR bajo | Casi en primera página pero no clickean | Optimizar title y meta description |
| Posición 1-3, CTR bajo | En top pero no clickean | Revisar si el snippet es atractivo |
| Posición cayendo | Contenido desactualizado o competencia | Actualizar contenido, agregar secciones |
| Impresiones altas, clicks bajos | Aparece pero no convence | Mejorar hook del title |

### 7.5 Retroalimentación Trimestral a MAES

**Propósito:** Cerrar el ciclo bidireccional MW#3 → MAES.

```
CADA 3 MESES:
├─ SUB-N genera reporte consolidado
│  • ¿Qué clusters performaron mejor?
│  • ¿Qué keywords lograron Top 10?
│  • ¿Cuántos leads generó SEO?
│  • ¿Qué contenido tiene alto CTR pero bajo ranking? (oportunidad)
│
├─ Juan + Claude AI revisan en Notion MAES
│  • Actualizar prioridades de clusters en Keywords_Master
│  • Agregar nuevas keywords descubiertas
│  • Deprecar keywords que no funcionaron (enabled=FALSE)
│  • Refinar briefs basado en lo que funciona
│
└─ Ejecutar SUB-K Investigación (opcional)
   • Buscar nuevas oportunidades con datos frescos
   • Actualizar MAES_RawData Sheet
```

---

## 8. FLUJO DE DATOS (v5.0 - ARQUITECTURA BIDIRECCIONAL)

### 8.1 Storage Layer Completo

```
┌─────────────────────────────────────────────────────────────────┐
│                  CAPA ESTRATÉGICA (Notion + Sheets)              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Notion: MAES Database                                           │
│  • 8 fases de estrategia SEO                                     │
│  • Análisis competencia, buyer personas, arquitectura URLs       │
│  • Decisiones documentadas (QUÉ y POR QUÉ)                       │
│                                                                  │
│  Google Sheets: "MAES_RawData" (TEMPORAL)                        │
│  • Output de SUB-K Investigación                                 │
│  • 200+ keywords raw con métricas completas                      │
│  • Para análisis humano en Fase 4-6 de MAES                      │
│  • Se archiva después de completar MAES                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ Contrato (bidireccional)
                            │
┌─────────────────────────────────────────────────────────────────┐
│                    PUENTE: Keywords_Master Sheet                 │
│                         (Source of Truth)                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Google Sheets: "Keywords_Master"                                │
│  • 16 columnas (A-P)                                             │
│  • Columna I: `enabled` (TRUE/FALSE) ← JUAN CONTROLA            │
│  • Alimentado por: MAES + SUB-K Investigación                    │
│  • Consumido por: SUB-K Producción                               │
│  • Actualizado por: SUB-L, SUB-M, SUB-N                          │
│                                                                  │
│  Tabs:                                                           │
│  1. Keywords Master (lista plana de keywords)                    │
│  2. Briefs (instrucciones detalladas por cluster)                │
│  3. Dashboard (resumen ejecutivo)                                │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                            │
                            │ Sincronización (enabled=TRUE only)
                            │
┌─────────────────────────────────────────────────────────────────┐
│                    CAPA OPERATIVA (n8n + Firestore)              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Firestore: keywords_pipeline (CACHE OPERATIVO)                  │
│  • Solo keywords con enabled=TRUE se sincronizan                 │
│  • keyword_id, keyword_text, status                              │
│  • Usado por SUB-L para queue de trabajo                         │
│  • NOT authoritative (Keywords_Master es la fuente)              │
│                                                                  │
│  Google Drive: "MW3_Drafts" folder                               │
│  • Borradores generados por SUB-L                                │
│  • Google Docs (WYSIWYG editing)                                 │
│                                                                  │
│  Google Sheets: "Content_Pipeline"                               │
│  • Tab "Keywords": Input tracking                                │
│  • Tab "Drafts": Output tracking                                 │
│                                                                  │
│  Google Sheets: "MW3_Orchestrator_Logs" / "MW3_ContentWriter_Logs" │
│  • Observabilidad completa                                       │
│                                                                  │
│  Firestore: content_performance (FUTURO - SUB-N)                 │
│  • Métricas de rendimiento por artículo                          │
│  • Alimenta retroalimentación trimestral a MAES                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.2 Flujo Completo Bidireccional Visualizado

```
┌─────────────────────────────────────────────────────────────────┐
│                    CICLO COMPLETO (v5.0)                         │
└─────────────────────────────────────────────────────────────────┘

FASE 1: SETUP ESTRATÉGICO (Una vez, ~15 horas)
═══════════════════════════════════════════════════════════════════

[1. Juan ejecuta Fases 1-3 de MAES en Notion]
    • Kick-off, Estrategia SEO, Objetivos + Audiencia
    • Define "Motor Futuro" (PyMEs tech sin PI)
          ↓
[2. Juan ejecuta SUB-K INVESTIGACIÓN (ad-hoc)]
    • Input: Seed keywords + competidores
    • DataForSEO API → MAES_RawData Sheet (200+ keywords)
          ↓
[3. Juan ejecuta Fases 4-6 de MAES con datos API]
    • Fase 4: Análisis Competencia (usa MAES_RawData)
    • Fase 5: Posicionamiento Actual
    • Fase 6: Planeación Estratégica
    • Define clusters, prioridades, briefs
          ↓
[4. Juan crea/actualiza Keywords_Master Sheet]
    • Consolida keywords validadas (50-100)
    • Asigna clusters, briefs, URLs target
    • Marca enabled=TRUE para keywords aprobadas
    • Marca enabled=FALSE para keywords rechazadas
          ↓
[5. Validación con Don Omar]
    • Presentar estrategia MAES
    • Aprobar Keywords_Master
          ↓
[6. Activar MW#3 en modo producción]
    • SUB-K Production schedule activado
    • Keywords_Master → Firestore sync
          ↓

FASE 2: OPERACIÓN CONTINUA (Semanal, automatizado)
═══════════════════════════════════════════════════════════════════

[LUNES 8:00 AM - Automático]
    SUB-K PRODUCCIÓN
    • Lee Keywords_Master (enabled=TRUE, status=pendiente)
    • Selecciona 2-3 keywords prioritarias
    • Sincroniza a Firestore keywords_pipeline
    • Trigger SUB-L para cada keyword
          ↓
[LUNES-MARTES - Automático]
    SUB-L Content Writer
    • Lee keyword + brief de Keywords_Master
    • Gemini genera artículo (guiado por brief MAES)
    • Crea Google Doc en MW3_Drafts/
    • Actualiza Keywords_Master: status=borrador_generado
    • Notifica a Juan
          ↓
[MIÉRCOLES-JUEVES - Manual]
    REVISIÓN HUMANA (Juan)
    • Edita en Google Docs (WYSIWYG)
    • Aprueba: Marca en Keywords_Master status=aprobado
          ↓
[VIERNES - Automático]
    SUB-M Publisher (FUTURO)
    • Detecta Keywords_Master status=aprobado
    • Publica en blog-service via API
    • Actualiza Keywords_Master: status=publicado, url_publicado
          ↓
[DIARIO - Automático]
    SUB-N Performance Tracker (FUTURO)
    • Query Google Search Console
    • Actualiza Firestore content_performance
    • Actualiza Keywords_Master con métricas
          ↓

FASE 3: RETROALIMENTACIÓN (Trimestral, ~3 horas)
═══════════════════════════════════════════════════════════════════

[CADA 3 MESES]
    1. SUB-N genera reporte consolidado
       • Top performers, keywords en Top 10, leads generados
          ↓
    2. Juan + Claude AI revisan en MAES (Notion)
       • Actualizar prioridades de clusters
       • Identificar nuevas oportunidades
       • Deprecar lo que no funciona
          ↓
    3. Juan actualiza Keywords_Master Sheet
       • Agregar nuevas keywords (enabled=TRUE)
       • Deprecar keywords (enabled=FALSE)
       • Refinar briefs basado en performance
          ↓
    4. (Opcional) Ejecutar SUB-K INVESTIGACIÓN
       • Refresh de datos competencia
       • Nuevas oportunidades con DataForSEO
          ↓
    5. Validación con Don Omar
       • Presentar ajustes de estrategia
          ↓
    6. MW#3 continúa con estrategia actualizada
```

### 8.3 Flujo de Keywords: Bidireccional

```
┌──────────────────────────────────────────────────────────────┐
│                  KEYWORDS FLOW (Bidireccional)                │
└──────────────────────────────────────────────────────────────┘

    ┌────────────────┐
    │  DataForSEO    │
    │  API           │
    └────────┬───────┘
             │
             │ SUB-K Investigación
             │
             ▼
    ┌────────────────────┐
    │  MAES_RawData      │  ← Análisis humano
    │  (Google Sheets)   │
    └────────┬───────────┘
             │
             │ MAES Fases 4-6
             │ (Notion)
             │
             ▼
    ┌─────────────────────────────┐
    │  Keywords_Master (Sheets)   │ ← Source of Truth
    │  enabled = TRUE/FALSE       │    (Juan controla)
    └──────┬──────────────┬───────┘
           │              │
           │              │ Performance feedback
           │              │ (SUB-N trimestral)
           │              │
           │              ▼
           │      ┌───────────────────┐
           │      │  MAES Revisión    │
           │      │  (Notion)         │
           │      └───────────────────┘
           │
           │ enabled=TRUE keywords only
           │
           ▼
    ┌────────────────────┐
    │  Firestore         │ ← Cache operativo
    │  keywords_pipeline │    (NOT authoritative)
    └────────┬───────────┘
             │
             │ SUB-L reads
             │
             ▼
    ┌────────────────────┐
    │  Contenido         │
    │  Publicado         │
    └────────────────────┘
```

**Puntos clave:**
1. **Keywords_Master es el contrato** entre estrategia (MAES) y ejecución (MW#3)
2. **enabled column** = control manual de Juan sobre qué se procesa
3. **Firestore es cache**, Keywords_Master es la fuente autoritativa
4. **Bidireccional:** SUB-N retroalimenta a MAES trimestralmente

---

## 9. PLAN DE IMPLEMENTACIÓN

### 9.1 Fases de Desarrollo

| Fase | Período | Componentes | Horas | Prerrequisitos | Status |
|------|---------|-------------|-------|----------------|--------|
| **0** | Completado | SUB-L v2.0 | 15 | Google Workspace | ✅ DEPLOYED |
| **1** | Sem 1-2 | MAES Setup + SUB-K Investigación | 15 | - MAES Notion template<br>- DataForSEO API<br>- MAES_RawData Sheet | ⏳ PENDING |
| **2** | Sem 3 | Keywords_Master creación | 3 | MAES completado | ⏳ PENDING |
| **3** | Sem 4 | SUB-K Producción v2.0 | 4 | Keywords_Master existente | ⏳ PENDING |
| **4** | Sem 5 | Orchestrator v2.0 | 5 | SUB-K Production ready | ⏳ PENDING |
| **5** | Sem 6 | E2E Testing | 3 | Todos los componentes | ⏳ PENDING |
| **6** | Sem 7-8 | SUB-M Publisher | 12 | blog-service API | ❌ BLOCKED |
| **7** | Sem 9-10 | SUB-N Tracker | 10 | Google Search Console | ⏳ PENDING |
| **TOTAL** | 10 semanas | - | **67 horas** | - | 22% Complete |

### 9.2 Dependencias Externas

| Dependencia | Opciones | Costo | Estado |
|-------------|----------|-------|--------|
| **API Keywords** | DataForSEO (pay-per-use) | $50-100 USD total | ✅ APPROVED |
| **SEO Tool Humano** | SEMrush Pro | $5.75M COP/año | ⚪ PENDING |
| **MAES Template** | Notion (existente) | $0 | ✅ READY |
| **CMS con API** | blog-service (Spring Boot) o WordPress | $0 o $14/mes | ⚪ PENDING DECISION |
| **Google Search Console** | Gratis | $0 | ⚪ CONFIGURAR |
| **Google Analytics 4** | Gratis | $0 | ⚪ CONFIGURAR |

### 9.3 Criterios de Éxito por Fase

| Fase | Criterio de Éxito |
|------|-------------------|
| 1 | MAES completado en Notion + MAES_RawData Sheet con 200+ keywords |
| 2 | Keywords_Master Sheet con 50+ keywords enabled=TRUE + briefs completos |
| 3 | SUB-K Production sincroniza correctamente a Firestore |
| 4 | Orchestrator decide correctamente entre SUB-K, SUB-L |
| 5 | Flujo completo funciona: keyword → borrador → Google Doc |
| 6 | Publicación automática sin errores |
| 7 | Dashboard muestra métricas de todos los artículos |

---

## 10. MÉTRICAS DE ÉXITO

### 10.1 KPIs por Fase del Embudo

| Fase | Métrica | Target Mes 3 | Target Mes 12 |
|------|---------|--------------|---------------|
| **Setup Estratégico** | MAES completado | 1 | 1 (+3 revisiones) |
| **Producción** | Artículos publicados | 12 | 50+ |
| **Indexación** | Artículos indexados | 12 | 50+ |
| **Ranking** | Keywords en Top 10 | 5 | 25+ |
| **Tráfico** | Visitas orgánicas/mes | 500 | 3,000+ |
| **Conversión** | Leads desde SEO/mes | 10 | 50+ |

### 10.2 ROI Proyectado

```
INVERSIÓN MENSUAL:
• DataForSEO API: ~$1.50/mes (amortizado)
• SEMrush Pro: ~$480K COP/mes
• Tiempo Juan (revisión): 4 horas/mes
• n8n Cloud: $0 (dentro del plan)
TOTAL: ~$500K COP + 4h

INVERSIÓN INICIAL (UNA VEZ):
• MAES setup: 15 horas Juan
• Desarrollo MW#3: 67 horas (agentes IA)
TOTAL: ~82 horas

RETORNO (Mes 12):
• 50 leads/mes desde SEO
• Si 20% convierten = 10 clientes
• Ticket promedio: $8M COP
• Revenue: $80M COP/mes

ROI: $80M / $6M anual = 13x
```

### 10.3 Señales de Alerta

| Señal | Umbral | Acción |
|-------|--------|--------|
| 0 artículos en Top 10 después de 3 meses | Crítico | Revisar calidad de contenido + briefs MAES |
| Tráfico cayendo mes a mes | Alerta | Actualizar contenido, revisar keywords |
| 0 leads desde SEO en 2 meses | Alerta | Revisar CTAs en artículos |
| Tiempo de revisión > 1 hora/artículo | Alerta | Mejorar prompts de SUB-L + briefs MAES |
| Keywords_Master con >50% enabled=FALSE | Alerta | Revisar filtros de SUB-K Investigación |

---

## APÉNDICE: HISTORIAL DE CAMBIOS v3.0-v4.0

### Cambios v3.0 (2026-01-21)

#### A. API de Keywords: DataForSEO (NO SEMrush)

**Problema identificado:**
- SEMrush Pro ($5.75M COP/año) NO incluye acceso API
- SEMrush Business con API cuesta ~$500 USD/mes
- Esto excede el presupuesto total de marketing

**Solución aprobada:**
| Uso | Herramienta | Costo |
|-----|-------------|-------|
| Humano (investigación manual) | SEMrush Pro | $5.75M/año |
| Robot n8n (automatización) | DataForSEO API | $50-100 USD total |

#### B. Arquitectura: AI Agent Orchestrator

**Cambio de Hub clásico a AI Agent** (metodología Nate Herk):
- Orquestador ahora es AI Agent con Tools (como MW#1 v3.0)
- Permite agregar SUB-workflows editando solo el System Prompt
- Mejor observabilidad y logging

#### C. SUB-M Publisher: BLOQUEADO

**Dependencia:** Requiere decisión de backend
- Opción A: blog-service (Spring Boot) - Requiere Alexis
- Opción B: WordPress REST API - Backup plan

**Status:** Esperando decisión de Don Omar + Alexis

#### D. Mejoras Metodología Nate Herk (AI Systems Pyramid)

> Basado en documento "Automatización y Agentes de IA con n8n" (Nate Herk)

##### Principio 1: Prompting Reactivo > Prompting Proactivo
- **NO** escribir prompts masivos desde el inicio
- **SÍ** empezar con prompt mínimo, probar, iterar
- Agregar guardarraíles SOLO donde el sistema falle

##### Principio 2: Workflow vs Agente
| Tipo | Cuándo usar | Ejemplo MW#3 |
|------|-------------|--------------|
| Workflow | Proceso predecible, secuencia fija | SUB-K, SUB-M, SUB-N |
| Agente | Necesita razonamiento, decisiones | SUB-L (Content Writer) |

##### Principio 3: Sistemas Multiagente
- Un agente orquestador delega a subagentes especializados
- Cada subagente tiene un único propósito
- Permite usar diferentes LLMs por costo/calidad

##### Principio 4: Human in the Loop (Ya implementado)
```
[Generar borrador] → [COLA REVISIÓN] → [Humano aprueba] → [Publicar]
```

##### Principio 5: Observabilidad LLM
- Activar "Return Intermediate Steps" en AI Agent nodes
- Registrar tokens + costos en Google Sheets
- Analizar para optimizar modelos

### Cambios v4.0 (2026-01-24)

#### E. SUB-L v2.0: Migración a Google Workspace (DEPLOYED)

> **ACTUALIZACIÓN 2026-01-24:** SUB-L v2.0 está **ACTIVO** en producción.

##### Problema con v1.0 (Firestore)
- Firebase Console no es user-friendly para editar contenido
- Sin colaboración nativa
- Sin WYSIWYG

##### Solución v2.0 (Google Workspace)
| Componente | Antes (v1.0) | Ahora (v2.0) |
|------------|--------------|--------------|
| Draft storage | Firestore `content_drafts` | **Google Docs** |
| Tracking | Firestore queries | **Google Sheets** |
| Editing | JSON en Firebase Console | **WYSIWYG en Docs** |
| Collaboration | Ninguna | **Nativa (comments)** |
| Notification | Link a Firebase | **Link a Google Doc** |

##### Flujo SUB-L v2.0 Actual
```
[Execute Workflow Trigger]
         │
[Read Keyword from Google Sheets "Content_Pipeline"]
         │
[Gemini 2.0 Flash genera artículo]
         │
[Create Google Doc in "MW3_Drafts" folder]
         │
[Update Google Sheets tracking]
         │
[Gmail notification with Doc link]
```

**Workflow ID:** `ZcaEG8VDm1IcG3LF` (ACTIVE)
**Nodes:** 16
**Spec detallado:** `workflows/MW3_SEO_CONTENT_FACTORY/02-spokes/sub-l-content-writer/DESIGN_SPEC.md`

#### F. SUB-L: Evolución a Sistema Multiagente (FUTURO - Fase 1)

##### Fase 0 (ACTUAL - DEPLOYED): Workflow Simple
```
[Get keyword] → [Gemini 2.0 Flash genera artículo] → [Google Doc]
```
- 1 solo agente IA
- Prompt básico
- Iterar basado en calidad de output

##### Fase 1 (Semana 2-3): Sistema Multiagente
```
┌─────────────────────────────────────────────────────────────┐
│     AGENTE ORQUESTADOR: "Content Creation Manager"          │
│     (Decide qué subagente ejecutar según contexto)          │
└─────────────────────────────────────────────────────────────┘
                            │
    ┌───────────┬───────────┼───────────┬───────────┐
    ▼           ▼           ▼           ▼           ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│Planner │ │Research│ │ Writer │ │ Editor │ │Evaluate│
│        │ │        │ │        │ │        │ │        │
│Gemini  │ │Perplex-│ │Gemini  │ │Claude  │ │Gemini  │
│Flash   │ │ity API │ │2.0Flash│ │3.5     │ │Flash   │
│        │ │        │ │        │ │Sonnet  │ │        │
│$0.001  │ │$0.005  │ │$0.001  │ │$0.003  │ │$0.001  │
└────────┘ └────────┘ └────────┘ └────────┘ └────────┘
    │           │           │           │           │
    ▼           ▼           ▼           ▼           ▼
 Define      Busca       Genera     Revisa      Califica
 estructura  datos       texto      tono/SEO    calidad
 del post    actuales    completo   gramática   (0-100)
```

**Flujo Multiagente:**
1. **Planner** (Gemini Flash): Lee keyword → Define estructura (guía/checklist/FAQ)
2. **Researcher** (Perplexity API): Busca datos actualizados (costos SIC 2026, tiempos)
3. **Writer** (Gemini 2.0 Flash): Genera borrador basado en plan + research
4. **Editor** (Claude 3.5 Sonnet): Revisa tono, gramática, SEO on-page
5. **Evaluator** (Gemini Flash): Califica según criterios → Si score <80 → volver a Writer

**Beneficios del sistema multiagente:**
- Cada agente especializado = mejor calidad
- Modelos diferentes por costo (Flash para tareas simples)
- Ciclo de auto-mejora antes de revisión humana
- Depuración más fácil (identificar qué agente falló)

---

## 📚 DOCUMENTOS RELACIONADOS

### Arquitectura (esta carpeta)
| Documento | Ubicación |
|-----------|-----------|
| Arquitectura General | `00_ARQUITECTURA_GENERAL.md` |
| MW#1 Captura | `01_MEGA_WORKFLOW_1_CAPTURA.md` |
| MW#2 Retención | `02_MEGA_WORKFLOW_2_RETENCION.md` |

### Specs de Implementación MW#3
| Documento | Ubicación |
|-----------|-----------|
| STATUS (Single Source of Truth) | `workflows/MW3_SEO_CONTENT_FACTORY/STATUS.md` |
| Orchestrator v2.0 Design Spec | `workflows/MW3_SEO_CONTENT_FACTORY/01-orchestrator/DESIGN_SPEC.md` |
| Google Sheets Keywords_Master | `workflows/MW3_SEO_CONTENT_FACTORY/01-orchestrator/GOOGLE_SHEETS_KEYWORDS_MASTER.md` |
| SUB-L v2.0 Design Spec | `workflows/MW3_SEO_CONTENT_FACTORY/02-spokes/sub-l-content-writer/DESIGN_SPEC.md` |

### Business Context
| Documento | Ubicación |
|-----------|-----------|
| **MAES Integration** | `docs/business/MAES_INTEGRATION.md` |

### Workflows en n8n Cloud
| Workflow | ID | Estado |
|----------|-----|--------|
| SUB-L Content Writer v2.0 | `ZcaEG8VDm1IcG3LF` | ✅ ACTIVE |
| Orchestrator v2.0 | - | ⏳ NOT IMPLEMENTED |
| SUB-K Keyword Research v2.0 | - | ⏳ NOT IMPLEMENTED |

---

**Última actualización:** 2026-01-26 | **Versión:** 5.0 | **Cambio principal:** Arquitectura bidireccional SUB-K ↔ MAES
