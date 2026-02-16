# 🔥 FIRESTORE SETUP GUIDE - MW#3 Content Factory

**Propósito:** Configurar las collections de Firestore necesarias para SUB-L Content Writer
**Tiempo estimado:** 15-20 minutos
**Prerequisito:** Acceso a Firebase Console con proyecto `carrillo-marketing-core`

---

## 📋 RESUMEN DE LO QUE VAS A CREAR

| Elemento | Qué es | Para qué sirve |
|----------|--------|----------------|
| **Collection: keywords_pipeline** | Lista de keywords para generar contenido | SUB-L lee de aquí para saber qué artículo escribir |
| **Collection: content_drafts** | Borradores generados por IA | SUB-L guarda aquí los artículos para tu revisión |
| **Índice compuesto** | Acelera queries de búsqueda | Permite encontrar keywords pendientes rápidamente |

---

## PASO 1: ABRIR FIREBASE CONSOLE

1. Ve a: https://console.firebase.google.com
2. Selecciona el proyecto: **carrillo-marketing-core**
3. En el menú lateral, haz clic en **Firestore Database**
4. Si ves un botón "Crear base de datos", haz clic (modo producción)
   - Si ya existe la base de datos, continúa al Paso 2

---

## PASO 2: CREAR COLLECTION `keywords_pipeline`

### 2.1 Crear la Collection

1. En Firestore, haz clic en **Iniciar colección** (o **Add collection**)
2. ID de la colección: `keywords_pipeline`
3. Haz clic en **Siguiente**

### 2.2 Agregar el Primer Documento (Keyword de Prueba)

Firebase requiere al menos 1 documento para crear la collection.

**ID del documento:** `kw_test_001`

**Campos a agregar:**

| Campo | Tipo | Valor |
|-------|------|-------|
| `keyword_id` | string | `kw_test_001` |
| `keyword_text` | string | `como registrar marca software colombia` |
| `volume` | number | `320` |
| `kd` | number | `22` |
| `cpc` | number | `2.5` |
| `priority_score` | number | `85` |
| `category` | string | `registro de marca` |
| `status` | string | `pendiente` |
| `created_at` | timestamp | *Haz clic en el ícono del reloj y selecciona "Ahora"* |

**Cómo agregar cada campo:**
1. Haz clic en **Agregar campo** (Add field)
2. Escribe el nombre del campo
3. Selecciona el tipo (string, number, timestamp)
4. Escribe el valor
5. Repite para todos los campos de la tabla

### 2.3 Guardar

1. Haz clic en **Guardar** (Save)
2. Deberías ver el documento `kw_test_001` en la collection `keywords_pipeline`

✅ **Collection `keywords_pipeline` creada**

---

## PASO 3: CREAR COLLECTION `content_drafts`

### 3.1 Crear la Collection

1. En Firestore, haz clic en **Iniciar colección** (o **Add collection**)
2. ID de la colección: `content_drafts`
3. Haz clic en **Siguiente**

### 3.2 Agregar Documento Placeholder

Firebase requiere al menos 1 documento. Este será eliminado después del primer draft real.

**ID del documento:** `_placeholder`

**Campos a agregar:**

| Campo | Tipo | Valor |
|-------|------|-------|
| `content_id` | string | `_placeholder` |
| `status` | string | `placeholder` |
| `created_at` | timestamp | *Haz clic en el ícono del reloj y selecciona "Ahora"* |

### 3.3 Guardar

1. Haz clic en **Guardar** (Save)
2. Deberías ver el documento `_placeholder` en la collection `content_drafts`

✅ **Collection `content_drafts` creada**

---

## PASO 4: CREAR ÍNDICE COMPUESTO (CRÍTICO)

Este índice permite que SUB-L busque keywords pendientes ordenadas por prioridad rápidamente.

### 4.1 Acceder a Índices

1. En Firestore, haz clic en la pestaña **Índices** (Indexes) en la parte superior
2. Haz clic en **Crear índice** (Create index)

### 4.2 Configurar el Índice

**Collection ID:** `keywords_pipeline`

**Campos del índice (en este orden):**

| Campo | Modo de consulta | Orden |
|-------|------------------|-------|
| `status` | Ascending | - |
| `priority_score` | Descending | - |

**Opciones de consulta:** Collection

### 4.3 Crear

1. Haz clic en **Crear** (Create)
2. Firebase mostrará "Creando índice..." (esto toma 2-5 minutos)
3. Cuando esté listo, el estado cambiará a **Habilitado** (Enabled)

⏳ **IMPORTANTE:** Espera a que el índice esté habilitado antes de ejecutar SUB-L

✅ **Índice compuesto creado**

---

## PASO 5: AGREGAR MÁS KEYWORDS DE PRUEBA (Opcional)

Si quieres agregar más keywords para probar, repite el proceso de PASO 2.2 con diferentes valores.

**Ejemplos de keywords para PI colombiana:**

| keyword_text | volume | kd | category |
|--------------|--------|----|----------|
| `requisitos registro marca colombia` | 280 | 25 | registro de marca |
| `cuanto cuesta registrar marca sic` | 450 | 18 | registro de marca |
| `proteccion propiedad intelectual startups` | 190 | 28 | general |
| `registro patente software colombia` | 160 | 32 | patentes |

**Tip:** Copia la estructura de `kw_test_001` y cambia solo:
- `keyword_id` (ej: `kw_test_002`, `kw_test_003`)
- `keyword_text`
- `volume`, `kd`, `priority_score`

---

## ✅ CHECKLIST FINAL - VERIFICACIÓN

Antes de ejecutar SUB-L, verifica que tienes:

- [ ] Collection `keywords_pipeline` existe
- [ ] Al menos 1 keyword con `status: "pendiente"` en `keywords_pipeline`
- [ ] Collection `content_drafts` existe
- [ ] Índice compuesto `status ASC + priority_score DESC` está **Habilitado**
- [ ] Google Sheet `MW3_ContentWriter_Logs` existe (ver siguiente guía)

---

## 🚀 PRÓXIMO PASO: CREAR GOOGLE SHEET

Después de completar esto, crea el Google Sheet con la guía:
**`GOOGLE_SHEETS_SETUP_GUIDE.md`**

---

## 📊 ESTRUCTURA ESPERADA EN FIRESTORE

Después de completar este setup:

```
carrillo-marketing-core (Firebase Project)
└── Firestore Database
    ├── keywords_pipeline (Collection)
    │   └── kw_test_001 (Document)
    │       ├── keyword_id: "kw_test_001"
    │       ├── keyword_text: "como registrar marca software colombia"
    │       ├── volume: 320
    │       ├── kd: 22
    │       ├── cpc: 2.5
    │       ├── priority_score: 85
    │       ├── category: "registro de marca"
    │       ├── status: "pendiente"
    │       └── created_at: [timestamp]
    │
    ├── content_drafts (Collection)
    │   └── _placeholder (Document - se elimina después)
    │       ├── content_id: "_placeholder"
    │       ├── status: "placeholder"
    │       └── created_at: [timestamp]
    │
    └── Indexes (Tab)
        └── keywords_pipeline
            └── status (ASC) + priority_score (DESC) ✅ Enabled
```

---

## ❓ TROUBLESHOOTING

### Error: "Missing index"

**Síntoma:** SUB-L falla con error "The query requires an index"

**Solución:**
1. Ve a Firestore > Índices
2. Verifica que el índice está **Habilitado** (no "Creando")
3. Si aún está creando, espera 5 minutos más
4. Si no existe, repite PASO 4

### Error: "Collection not found"

**Síntoma:** SUB-L falla con error "Collection keywords_pipeline does not exist"

**Solución:**
1. Ve a Firestore > Data
2. Verifica que ves `keywords_pipeline` en la lista de collections
3. Si no existe, repite PASO 2

### No hay keywords pendientes

**Síntoma:** SUB-L ejecuta pero termina sin generar nada

**Solución:**
1. Ve a Firestore > keywords_pipeline
2. Verifica que al menos 1 documento tiene `status: "pendiente"`
3. Si todos están `en_progreso`, cambia uno manualmente a `pendiente`

---

## 📞 SOPORTE

Si tienes problemas:
1. Toma screenshot del error en n8n
2. Toma screenshot de Firestore mostrando las collections
3. Comparte en el chat

---

**Fin de la guía**
**Tiempo total:** 15-20 minutos
**Siguiente:** `GOOGLE_SHEETS_SETUP_GUIDE.md`
