# Prompt para Claude en Excel - Keywords Master

**Uso:** Copiar y pegar este prompt en Claude for Excel (https://claude.com/claude-in-excel)
**Fecha:** 2026-02-06

---

## PROMPT (copiar desde aqui)

```
Necesito que crees un archivo Excel profesional llamado "MW3 Keywords Master" para gestionar el pipeline SEO de una firma de abogados (Carrillo Abogados, Cali, Colombia). El archivo sera la fuente de verdad que conecta la estrategia SEO con la automatizacion de contenido en n8n.

### HOJA 1: "All_Keywords" (hoja principal de datos)

Crear 21 columnas con estos headers exactos en fila 1:

| Col | Header | Tipo | Notas |
|-----|--------|------|-------|
| A | keyword_id | Texto | ID unico. Formato: KW-XXX-001 |
| B | keyword_text | Texto | La keyword. Columna mas ancha (300px) |
| C | cluster_name | Dropdown | Valores: registro-marcas, pi-startups, patentes, derechos-autor, litigio-pi, contratacion-estatal, derecho-corporativo, otro |
| D | is_main_keyword | Checkbox | TRUE = keyword principal del cluster (sera el H1 del articulo). FALSE = keyword secundaria |
| E | intent | Dropdown | Valores: Informational, Transactional, Commercial, Navigational |
| F | volume | Numero | Busquedas mensuales. Color scale: rojo bajo → verde alto |
| G | kd | Numero (0-100) | Keyword Difficulty. Color scale INVERSO: verde bajo → rojo alto (bajo es mejor) |
| H | cpc | Numero (USD, 2 decimales) | Costo por clic |
| I | competitive_density | Numero (0-1, 2 decimales) | Densidad competitiva de ads |
| J | serp_features | Texto | Features de Google: featured_snippet, people_also_ask, etc. |
| K | trend | Texto | Tendencia 12 meses (datos de SEMrush) |
| L | priority_score | Formula | =IF(F2="","",ROUND((F2/10)-G2+(H2*5),0)). Color scale: rojo bajo → verde alto. Aplicar a filas 2-200 |
| M | enabled | Checkbox | TRUE/FALSE. CONTROL HUMANO. Default: TRUE |
| N | status | Dropdown | Valores: pendiente, en_progreso, revision, aprobado, publicado, descartado |
| O | url_target | Texto | URL destino del articulo |
| P | brief_notes | Texto | Notas del brief para el redactor IA. Ancho 250px |
| Q | content_id | Texto | ID del draft generado por IA |
| R | published_url | Texto (URL) | URL publicada del articulo |
| S | source | Dropdown | Valores: semrush, dataforseo, manual, search-console |
| T | created_at | Fecha | Formato: YYYY-MM-DD |
| U | updated_at | Fecha | Formato: YYYY-MM-DD |

**Formato del header (fila 1):**
- Background: azul oscuro (#334589)
- Texto: blanco, bold
- Congelar fila 1 y columnas A-B

**Formatos condicionales en All_Keywords:**
1. Fila completa con fondo rojo claro (#FCE8E6) cuando enabled (col M) = FALSE
2. Col N (status): "publicado" = fondo verde (#34A853), "en_progreso" = fondo amarillo (#FBBC04), "descartado" = fondo gris (#9E9E9E) con tachado
3. Col L (priority_score): color scale rojo-amarillo-verde
4. Col G (kd): color scale verde-amarillo-rojo (inverso, bajo es mejor)
5. Col F (volume): color scale rojo claro a verde (alto es mejor)
6. Fila completa con borde verde y bold cuando priority_score >= 30

**Proteccion:**
- Columnas A, L, Q, R, T, U: proteger (auto-generadas)
- Columnas M, N, O, P: libre edicion (Juan controla)

Incluir 5 filas de datos de ejemplo para verificar que todo funciona:

| keyword_id | keyword_text | cluster | main? | intent | vol | kd | cpc | comp | serp | trend | enabled | status | url_target | source |
|------------|-------------|---------|-------|--------|-----|-----|------|------|------|-------|---------|--------|------------|--------|
| KW-REG-001 | como registrar marca en colombia | registro-marcas | TRUE | Informational | 480 | 25 | 2.35 | 0.15 | people_also_ask, featured_snippet | estable | TRUE | pendiente | /blog/como-registrar-marca-colombia | semrush |
| KW-REG-002 | cuanto cuesta registrar marca colombia 2026 | registro-marcas | FALSE | Transactional | 260 | 18 | 1.95 | 0.08 | people_also_ask | creciente | TRUE | pendiente | /blog/como-registrar-marca-colombia | semrush |
| KW-REG-003 | requisitos registro marca SIC | registro-marcas | FALSE | Informational | 390 | 20 | 2.50 | 0.10 | featured_snippet | estable | TRUE | pendiente | /blog/requisitos-registro-marca-sic | semrush |
| KW-PI-001 | proteger propiedad intelectual startup colombia | pi-startups | TRUE | Commercial | 150 | 12 | 3.20 | 0.06 | - | creciente | TRUE | pendiente | /blog/proteger-pi-startup | semrush |
| KW-CON-001 | requisitos contratacion estatal colombia | contratacion-estatal | TRUE | Informational | 210 | 28 | 1.10 | 0.04 | people_also_ask | estable | FALSE | descartado | - | semrush |

Las fechas created_at y updated_at para los ejemplos: 2026-02-06.

---

### HOJA 2: "URL_Analysis" (Tabla Dinamica - Agrupacion por URL)

Crear una tabla dinamica que agrupe los datos de All_Keywords por url_target:

- Filas: url_target
- Valores: COUNT de keyword_text, SUM de volume, AVG de kd, AVG de priority_score
- Ordenar por SUM de volume descendente

Esto permite ver: "La URL /blog/como-registrar-marca-colombia tiene 3 keywords apuntando con volumen total de 1,130 busquedas/mes".

Si no puedes crear pivot table automatica, crear una tabla manual con formulas COUNTIF/SUMIF referenciando All_Keywords.

---

### HOJA 3: "Cluster_View" (Vista por Cluster)

Crear una vista que agrupe keywords por cluster_name mostrando:
- Nombre del cluster
- Main keyword (is_main_keyword=TRUE) destacada en bold
- Secondary keywords listadas debajo
- Volumen total del cluster
- KD promedio del cluster
- Cantidad de keywords

Para cada cluster, formato visual tipo:

**registro-marcas** (3 keywords | Vol total: 1,130 | KD avg: 21)
  ★ como registrar marca en colombia (480 vol, 25 kd) ← main
    · cuanto cuesta registrar marca colombia 2026 (260 vol, 18 kd)
    · requisitos registro marca SIC (390 vol, 20 kd)

Si no es posible este formato, crear tabla con columnas: cluster_name, total_keywords, main_keyword, total_volume, avg_kd, avg_priority_score.

---

### HOJA 4: "Briefs" (Template SEO On-page por cluster)

Headers:
| cluster_name | main_keyword | secondary_keywords | url_target | formato_contenido | titulo_seo_sugerido | notas_brief |

Incluir 2 filas de ejemplo:

Fila 1:
- cluster: registro-marcas
- main_keyword: como registrar marca en colombia
- secondary: cuanto cuesta registrar marca colombia 2026, requisitos registro marca SIC
- url: /blog/como-registrar-marca-colombia
- formato: Guia paso a paso (2,000-2,500 palabras)
- titulo_seo: Como Registrar una Marca en Colombia 2026: Guia Completa SIC
- notas: Incluir costos actualizados 2026, tiempos reales (6-12 meses), mencionar experiencia Dr. Carrillo en SIC, FAQ Schema con 5 preguntas

Fila 2:
- cluster: pi-startups
- main_keyword: proteger propiedad intelectual startup colombia
- secondary: (por definir)
- url: /blog/proteger-pi-startup
- formato: Articulo educativo (2,000 palabras)
- titulo_seo: Guia de Propiedad Intelectual para Startups en Colombia 2026
- notas: Enfoque en founders tech/fintech, mencionar NDA, derechos de autor de codigo, patentes software

---

### HOJA 5: "Dashboard" (Metricas Ejecutivas)

Crear un dashboard con estas secciones usando formulas que referencian All_Keywords:

**PIPELINE OVERVIEW**
- Total Keywords: COUNTA de keyword_text
- Enabled (TRUE): COUNTIF enabled=TRUE
- Disabled (FALSE): COUNTIF enabled=FALSE

**STATUS BREAKDOWN**
- Pendientes: COUNTIF status="pendiente"
- En Progreso: COUNTIF status="en_progreso"
- Revision: COUNTIF status="revision"
- Aprobados: COUNTIF status="aprobado"
- Publicados: COUNTIF status="publicado"
- Descartados: COUNTIF status="descartado"

**QUALITY METRICS**
- Avg Priority Score: AVERAGE de priority_score
- Avg Volume: AVERAGE de volume
- Avg KD: AVERAGE de kd
- High Priority (>=30): COUNTIFS priority_score>=30

**CLUSTER DISTRIBUTION** (grafico de barras si es posible)
- registro-marcas: COUNTIF
- pi-startups: COUNTIF
- contratacion-estatal: COUNTIF

**INTENT DISTRIBUTION** (grafico pie si es posible)
- Informational, Transactional, Commercial, Navigational: COUNTIF cada uno

**STATUS** (grafico pie)
- Pendiente, En progreso, Publicado, Descartado

Crear graficos de barras y pie charts donde sea posible.

---

### HOJA 6: "Enabled_Queue" (Cola de Trabajo)

Vista filtrada que muestra SOLO keywords con enabled=TRUE y status="pendiente", ordenadas por priority_score descendente.

Columnas: keyword_text, cluster_name, volume, kd, priority_score, intent, brief_notes

Usar formulas FILTER o tabla con filtro automatico.

---

### FORMATO GENERAL:
- Font: Calibri 10pt
- Alternating row colors en All_Keywords (blanco/gris muy claro)
- Bordes finos en todas las tablas
- Pestanas con colores: All_Keywords=azul, URL_Analysis=verde, Cluster_View=naranja, Briefs=morado, Dashboard=rojo, Enabled_Queue=amarillo
```

---

## FIN DEL PROMPT
