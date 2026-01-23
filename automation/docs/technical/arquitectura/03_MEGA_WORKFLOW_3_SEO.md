# 🔍 MEGA-WORKFLOW #3: FÁBRICA DE CONTENIDO SEO

**Versión:** 3.0
**Última actualización:** 2026-01-21
**Estado:** 🟡 EN DESARROLLO (Fase 0 - Setup)
**Prioridad:** CRÍTICO - Habilitador de Pipeline de Contenido
**Status Tracking:** [STATUS.md](../../../workflows/MW3_SEO_CONTENT_FACTORY/STATUS.md)

---

## 📋 TABLA DE CONTENIDOS

1. [Visión General](#1-visión-general)
2. [Análisis Crítico](#2-análisis-crítico)
3. [Arquitectura del MEGA-WORKFLOW](#3-arquitectura-del-mega-workflow)
4. [SUB-K: Keyword Research](#4-sub-k-keyword-research)
5. [SUB-L: Content Writer AI](#5-sub-l-content-writer-ai)
6. [SUB-M: Content Publisher](#6-sub-m-content-publisher)
7. [SUB-N: SEO Performance Tracker](#7-sub-n-seo-performance-tracker)
8. [Flujo de Datos](#8-flujo-de-datos)
9. [Plan de Implementación](#9-plan-de-implementación)
10. [Métricas de Éxito](#10-métricas-de-éxito)

---

## 0. CAMBIOS CRÍTICOS v3.0 (2026-01-21)

> [!IMPORTANT]
> Esta sección documenta cambios de arquitectura aprobados el 21 Ene 2026.

### 0.1 API de Keywords: DataForSEO (NO SEMrush)

**Problema identificado:**
- SEMrush Pro ($5.75M COP/año) NO incluye acceso API
- SEMrush Business con API cuesta ~$500 USD/mes
- Esto excede el presupuesto total de marketing

**Solución aprobada:**
| Uso | Herramienta | Costo |
|-----|-------------|-------|
| Humano (investigación manual) | SEMrush Pro | $5.75M/año |
| Robot n8n (automatización) | DataForSEO API | $50-100 USD total |

### 0.2 Arquitectura: AI Agent Orchestrator

**Cambio de Hub clásico a AI Agent** (metodología Nate Herk):
- Orquestador ahora es AI Agent con Tools (como MW#1 v3.0)
- Permite agregar SUB-workflows editando solo el System Prompt
- Mejor observabilidad y logging

### 0.3 SUB-M Publisher: BLOQUEADO

**Dependencia:** Requiere decisión de backend
- Opción A: blog-service (Spring Boot) - Requiere Alexis
- Opción B: WordPress REST API - Backup plan

**Status:** Esperando decisión de Don Omar + Alexis

### 0.4 Mejoras Metodología Nate Herk (AI Systems Pyramid)

> Basado en documento "Automatización y Agentes de IA con n8n" (Nate Herk)

#### Principio 1: Prompting Reactivo > Prompting Proactivo
- **NO** escribir prompts masivos desde el inicio
- **SÍ** empezar con prompt mínimo, probar, iterar
- Agregar guardarraíles SOLO donde el sistema falle

#### Principio 2: Workflow vs Agente
| Tipo | Cuándo usar | Ejemplo MW#3 |
|------|-------------|--------------|
| Workflow | Proceso predecible, secuencia fija | SUB-K, SUB-M, SUB-N |
| Agente | Necesita razonamiento, decisiones | SUB-L (Content Writer) |

#### Principio 3: Sistemas Multiagente
- Un agente orquestador delega a subagentes especializados
- Cada subagente tiene un único propósito
- Permite usar diferentes LLMs por costo/calidad

#### Principio 4: Human in the Loop (Ya implementado)
```
[Generar borrador] → [COLA REVISIÓN] → [Humano aprueba] → [Publicar]
```

#### Principio 5: Observabilidad LLM
- Activar "Return Intermediate Steps" en AI Agent nodes
- Registrar tokens + costos en Google Sheets
- Analizar para optimizar modelos

### 0.5 SUB-L: Evolución a Sistema Multiagente

#### Fase 0 (Semana 1): Workflow Simple
```
[Get keyword] → [Gemini 2.0 Flash genera artículo] → [Guardar draft]
```
- 1 solo agente IA
- Prompt básico
- Iterar basado en calidad de output

#### Fase 1 (Semana 2-3): Sistema Multiagente
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

## 1. VISIÓN GENERAL

### 1.1 Propósito Estratégico

El **MEGA-WORKFLOW #3: Fábrica de Contenido SEO** implementa la estrategia **Inbound Marketing**:

> *"Enfoque reactivo que funciona respondiendo a las búsquedas activas de los usuarios mediante la creación de contenido específico. Ideal para captar demanda existente y posicionarse como autoridad."*

**Este workflow es una máquina de atracción que:**
1. **Investiga** qué buscan las PyMEs en Google sobre PI
2. **Crea** contenido optimizado que responde esas búsquedas
3. **Publica** automáticamente en el blog
4. **Monitorea** el rendimiento y ajusta

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

---

## 2. ANÁLISIS CRÍTICO

### 2.1 Riesgos y Mitigaciones

#### Riesgo 1: Contenido IA de baja calidad

**Mitigación:**
- Artículos de 2,000+ palabras con estructura SEO real
- Revisión humana OBLIGATORIA antes de publicar
- Datos específicos de PI colombiana (no genéricos)

#### Riesgo 2: Keywords incorrectas

**Mitigación:**
- Priorizar long-tail (3-5 palabras)
- Solo keywords con KD < 30 (baja competencia)
- Solo intención informacional/transaccional

#### Riesgo 3: Publicar sin medir

**Mitigación:**
- SUB-N trackea cada artículo individualmente
- Dashboard semanal de rendimiento
- Optimizar o deprecar artículos que no funcionan

### 2.2 Decisión de Arquitectura Clave

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

### 3.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────┐
│              MEGA-WORKFLOW #3: FÁBRICA DE CONTENIDO SEO             │
└─────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────────┐
                    │ WORKFLOW C: CONTENT     │
                    │ FACTORY MANAGER (HUB)   │
                    └───────────┬─────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐       ┌───────────────┐       ┌───────────────┐
│    SUB-K      │       │    SUB-L      │       │    SUB-M      │
│   Keyword     │       │   Content     │       │   Content     │
│  Research     │       │  Writer AI    │       │  Publisher    │
│               │       │               │       │               │
│ 1x/mes        │       │ 1x/semana     │       │ On-demand     │
└───────────────┘       └───────────────┘       └───────────────┘
        │                       │                       │
        │                       │                       │
        └───────────────────────┼───────────────────────┘
                                │
                                ▼
                        ┌───────────────┐
                        │    SUB-N      │
                        │ SEO Tracker   │
                        │               │
                        │ Diario        │
                        └───────────────┘
```

### 3.2 Ciclo de Producción

| Fase | Frecuencia | Workflow | Output |
|------|------------|----------|--------|
| Investigación | 1x/mes | SUB-K | Lista de 20-30 keywords |
| Escritura | 1x/semana | SUB-L | Borrador en cola |
| Revisión | Asíncrona | HUMANO | Aprobado/Rechazado |
| Publicación | On-demand | SUB-M | Artículo en blog |
| Monitoreo | Diario | SUB-N | Métricas actualizadas |

---

## 4. SUB-K: KEYWORD RESEARCH

### 4.1 Propósito

Identificar las mejores oportunidades de búsqueda con el "sweet spot":
- Volumen > 100/mes
- Dificultad < 30
- Long-tail (3-5 palabras)
- Intención informacional/transaccional

### 4.2 Flujo Técnico

```
[Schedule: 1º del mes]
         │
         ▼
[1. Query API SEO (Ubersuggest/Ahrefs)]
    Categorías: registro marca, propiedad intelectual, patentes
         │
         ▼
[2. Filtrar por criterios]
    volumen >= 100 AND kd <= 30 AND palabras >= 3
         │
         ▼
[3. Calcular priority_score]
    score = volumen/10 - kd + cpc*5
         │
         ▼
[4. Guardar en Firestore: keywords_pipeline]
    status: "pendiente"
         │
         ▼
[5. Notificar: "25 keywords identificadas"]
```

### 4.3 Estructura de Keyword en Firestore

```json
{
  "keyword_id": "kw_001",
  "keyword_text": "cómo registrar marca software colombia",
  "volumen": 320,
  "kd": 22,
  "cpc": 2.50,
  "priority_score": 85,
  "categoria": "registro de marca",
  "status": "pendiente",
  "created_at": "2026-02-01T07:00:00Z"
}
```

---

## 5. SUB-L: CONTENT WRITER AI

### 5.1 Propósito

Generar borradores de artículos SEO de 2,000+ palabras que un humano pueda aprobar en minutos.

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

### 5.3 Flujo Técnico

```
[Schedule: Lunes 8:00 AM]
         │
         ▼
[1. Obtener siguiente keyword del pipeline]
    WHERE status = "pendiente"
    ORDER BY priority_score DESC
    LIMIT 1
         │
         ▼
[2. Generar outline con Gemini]
    Prompt: "Crea outline SEO para: [keyword]"
         │
         ▼
[3. Generar artículo completo]
    Prompt estructurado con:
    - Keyword objetivo
    - Estructura requerida
    - Tono profesional pero accesible
    - Datos específicos de PI Colombia
    - CTAs hacia Carrillo Abogados
         │
         ▼
[4. Generar meta SEO]
    - Title (60 chars)
    - Meta description (155 chars)
    - URL slug
         │
         ▼
[5. Guardar borrador en Firestore: content_drafts]
    status: "pendiente_revision"
         │
         ▼
[6. Actualizar keyword: status = "en_progreso"]
         │
         ▼
[7. Notificar: "Borrador listo para revisión"]
    Email a Juan con link al documento
```

### 5.4 Prompt de Generación (Ejemplo)

```
Eres un experto en propiedad intelectual colombiana con 15 años 
de experiencia en la SIC. Escribe un artículo de blog optimizado 
para SEO sobre: "[KEYWORD]"

REQUISITOS:
- Longitud: 2,000-2,500 palabras
- Audiencia: Dueños de PyMEs tecnológicas en Colombia
- Tono: Profesional pero accesible, evitar jerga legal excesiva
- Estructura: H1 > H2 > H3 según outline proporcionado

INCLUIR OBLIGATORIAMENTE:
1. Datos actualizados de costos SIC 2026
2. Tiempos reales del proceso (6-12 meses)
3. Al menos 2 errores comunes con ejemplos
4. Sección de FAQ con 5 preguntas frecuentes
5. CTA final: "¿Necesitas ayuda? Agenda una consulta gratuita"

NO INCLUIR:
- Información genérica que aplique a cualquier país
- Promesas de resultados garantizados
- Lenguaje de venta agresivo

El artículo debe posicionar a Carrillo Abogados como experto
sin ser un "infomercial".
```

---

## 6. SUB-M: CONTENT PUBLISHER

### 6.1 Propósito

Publicar artículos aprobados en el CMS (WordPress/Webflow) con toda la optimización SEO.

### 6.2 Flujo Técnico

```
[Trigger: status cambió a "aprobado"]
         │
         ▼
[1. Obtener contenido aprobado de Firestore]
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
    Webflow: webflow.com/api/v1/items
         │
         ▼
[5. Actualizar Firestore]
    content_drafts: status = "publicado", url = "[URL]"
    keywords_pipeline: status = "publicado"
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
    │   Para cada URL en content_drafts (status=publicado):
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
[5. Generar reporte semanal (Lunes)]
    Email a Juan:
    • Top 5 artículos por tráfico
    • Artículos que subieron/bajaron
    • Keywords nuevas rankeando
    • Recomendaciones de optimización
```

### 7.4 Lógica de Optimización Automática

| Señal | Diagnóstico | Recomendación |
|-------|-------------|---------------|
| Posición 11-20, CTR bajo | Casi en primera página pero no clickean | Optimizar title y meta description |
| Posición 1-3, CTR bajo | En top pero no clickean | Revisar si el snippet es atractivo |
| Posición cayendo | Contenido desactualizado o competencia | Actualizar contenido, agregar secciones |
| Impresiones altas, clicks bajos | Aparece pero no convence | Mejorar hook del title |

---

## 8. FLUJO DE DATOS

### 8.1 Collections en Firestore

```
Firestore Database
│
├── keywords_pipeline
│   ├── keyword_id
│   ├── keyword_text
│   ├── volumen, kd, cpc
│   ├── priority_score
│   ├── status: pendiente → en_progreso → publicado
│   └── created_at
│
├── content_drafts
│   ├── content_id
│   ├── keyword_id (referencia)
│   ├── titulo, meta_description, slug
│   ├── contenido (markdown)
│   ├── status: pendiente_revision → aprobado → publicado
│   ├── url (después de publicar)
│   ├── aprobado_por, aprobado_at
│   └── created_at
│
└── content_performance
    ├── performance_id
    ├── content_id (referencia)
    ├── fecha
    ├── posicion_promedio
    ├── impresiones, clicks, ctr
    ├── cambio_posicion (vs semana anterior)
    └── alertas []
```

### 8.2 Flujo Completo Visualizado

```
[SUB-K: Research]
    │
    │  keywords_pipeline
    │  status: "pendiente"
    │
    ▼
[SUB-L: Writer]
    │
    │  content_drafts
    │  status: "pendiente_revision"
    │
    ▼
[HUMANO: Revisión]
    │
    │  status: "aprobado" (o "rechazado")
    │
    ▼
[SUB-M: Publisher]
    │
    │  status: "publicado"
    │  url: "https://carrilloabogados.com/blog/..."
    │
    ▼
[SUB-N: Tracker]
    │
    │  content_performance
    │  Métricas diarias
    │
    └──► [Dashboard + Alertas]
```

---

## 9. PLAN DE IMPLEMENTACIÓN

### 9.1 Fases de Desarrollo

| Fase | Período | Componentes | Horas | Prerrequisitos |
|------|---------|-------------|-------|----------------|
| **1** | Sem 1-2 | Orquestador C + SUB-K | 10 | API SEO configurada |
| **2** | Sem 3-4 | SUB-L (Writer) | 15 | Prompts refinados |
| **3** | Sem 5-6 | SUB-M (Publisher) | 12 | CMS con API lista |
| **4** | Sem 7-8 | SUB-N (Tracker) | 10 | Google Search Console |
| **TOTAL** | 8 semanas | - | **47 horas** | - |

### 9.2 Dependencias Externas

| Dependencia | Opciones | Estado |
|-------------|----------|--------|
| API de Keywords | Ubersuggest ($12/mes) o Ahrefs ($99/mes) | ⚪ Pendiente |
| CMS con API | WordPress (gratis) o Webflow ($14/mes) | ⚪ Pendiente |
| Google Search Console | Gratis | ⚪ Configurar |
| Google Analytics 4 | Gratis | ⚪ Configurar |

### 9.3 Criterios de Éxito por Fase

| Fase | Criterio |
|------|----------|
| 1 | Pipeline de 20+ keywords generado automáticamente |
| 2 | Borradores de calidad que requieren <15 min de revisión |
| 3 | Publicación funciona end-to-end sin errores |
| 4 | Dashboard muestra métricas de todos los artículos |

---

## 10. MÉTRICAS DE ÉXITO

### 10.1 KPIs por Fase del Embudo

| Fase | Métrica | Target Mes 3 | Target Mes 12 |
|------|---------|--------------|---------------|
| **Producción** | Artículos publicados | 12 | 50+ |
| **Indexación** | Artículos indexados | 12 | 50+ |
| **Ranking** | Keywords en Top 10 | 5 | 25+ |
| **Tráfico** | Visitas orgánicas/mes | 500 | 3,000+ |
| **Conversión** | Leads desde SEO/mes | 10 | 50+ |

### 10.2 ROI Proyectado

```
INVERSIÓN MENSUAL:
• Herramientas SEO: ~$50/mes
• Tiempo Juan (revisión): 4 horas/mes
• n8n Cloud: $0 (dentro del plan)
TOTAL: ~$50 + 4h

RETORNO (Mes 12):
• 50 leads/mes desde SEO
• Si 20% convierten = 10 clientes
• Ticket promedio: $8M COP
• Revenue: $80M COP/mes

ROI: $80M / $50K = 1,600x
```

### 10.3 Señales de Alerta

| Señal | Umbral | Acción |
|-------|--------|--------|
| 0 artículos en Top 10 después de 3 meses | Crítico | Revisar calidad de contenido |
| Tráfico cayendo mes a mes | Alerta | Actualizar contenido, revisar keywords |
| 0 leads desde SEO en 2 meses | Alerta | Revisar CTAs en artículos |
| Tiempo de revisión > 1 hora/artículo | Alerta | Mejorar prompts de SUB-L |

---

## 📚 DOCUMENTOS RELACIONADOS

| Documento | Ubicación |
|-----------|-----------|
| Arquitectura General | `00_ARQUITECTURA_GENERAL.md` |
| MW#1 Captura | `01_MEGA_WORKFLOW_1_CAPTURA.md` |
| MW#2 Retención | `02_MEGA_WORKFLOW_2_RETENCION.md` |

---

**Última actualización:** 2025-12-19 | **Versión:** 2.0
