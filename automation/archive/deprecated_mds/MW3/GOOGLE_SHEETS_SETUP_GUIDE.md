# 📊 GOOGLE SHEETS SETUP GUIDE - MW#3 Content Factory

**Propósito:** Crear el Google Sheet para logging de SUB-L Content Writer
**Tiempo estimado:** 10 minutos
**Prerequisito:** Cuenta Google con acceso a Google Sheets

---

## 📋 RESUMEN

Vas a crear 1 Google Sheet con 2 tabs:
- **Tab "Logs":** Registro de ejecuciones exitosas
- **Tab "Errors":** Registro de errores

Este sheet es usado por SUB-L para registrar métricas cada vez que genera un artículo.

---

## PASO 1: CREAR EL GOOGLE SHEET

1. Ve a: https://sheets.google.com
2. Haz clic en el botón **+ Blank** (crear hoja en blanco)
3. Renombra la hoja:
   - Haz clic en "Untitled spreadsheet" arriba a la izquierda
   - Escribe: `MW3_ContentWriter_Logs`
   - Presiona Enter

---

## PASO 2: CONFIGURAR TAB "LOGS"

### 2.1 Renombrar el Tab

1. La primera tab se llama "Sheet1" por defecto
2. Haz clic derecho en "Sheet1" en la parte inferior
3. Selecciona **Rename**
4. Escribe: `Logs`
5. Presiona Enter

### 2.2 Crear el Header (Fila 1)

Copia y pega esta fila en las celdas A1 a H1:

| A | B | C | D | E | F | G | H |
|---|---|---|---|---|---|---|---|
| **Timestamp** | **Keyword ID** | **Keyword Text** | **Content ID** | **Word Count** | **Status** | **Result** | **Duration (ms)** |

**Cómo hacerlo:**
1. Haz clic en celda A1
2. Escribe: `Timestamp`
3. Presiona Tab para ir a B1
4. Escribe: `Keyword ID`
5. Repite para todas las columnas hasta H1

### 2.3 Formatear el Header

1. Selecciona el rango A1:H1 (todas las celdas del header)
2. Haz clic en el botón **Bold** (B) en la barra de herramientas
3. Haz clic en el ícono de **Fill color** y selecciona un color claro (ej: gris claro)
4. Opcional: **Format > Text wrapping > Wrap** para que el texto no se desborde

### 2.4 Congelar el Header

Esto mantiene el header visible cuando haces scroll.

1. Haz clic en la celda A2 (primera celda debajo del header)
2. Ve a **View > Freeze > 1 row**

✅ **Tab "Logs" configurado**

---

## PASO 3: CONFIGURAR TAB "ERRORS"

### 3.1 Crear el Tab

1. Haz clic en el botón **+** en la parte inferior izquierda (al lado de "Logs")
2. Se creará un nuevo tab llamado "Sheet2"
3. Haz clic derecho en "Sheet2"
4. Selecciona **Rename**
5. Escribe: `Errors`
6. Presiona Enter

### 3.2 Crear el Header (Fila 1)

Copia y pega esta fila en las celdas A1 a G1:

| A | B | C | D | E | F | G |
|---|---|---|---|---|---|---|
| **Timestamp** | **Keyword ID** | **Keyword Text** | **Error Type** | **Error Message** | **Execution ID** | **Resolution Status** |

### 3.3 Formatear el Header

1. Selecciona el rango A1:G1
2. Haz clic en **Bold** (B)
3. Haz clic en **Fill color** y selecciona un color (ej: rojo claro para errores)
4. Opcional: **Format > Text wrapping > Wrap**

### 3.4 Congelar el Header

1. Haz clic en la celda A2
2. Ve a **View > Freeze > 1 row**

✅ **Tab "Errors" configurado**

---

## PASO 4: OBTENER EL DOCUMENT ID

Necesitas el ID del Google Sheet para configurarlo en n8n.

### 4.1 Copiar el ID de la URL

1. Mira la URL del Google Sheet en tu navegador
2. La URL tiene este formato:
   ```
   https://docs.google.com/spreadsheets/d/[DOCUMENT_ID]/edit
   ```
3. Copia la parte `[DOCUMENT_ID]` (es una cadena larga de caracteres)

**Ejemplo:**
```
URL: https://docs.google.com/spreadsheets/d/1abc123XYZ-def456_ghi789/edit
Document ID: 1abc123XYZ-def456_ghi789
```

### 4.2 Guardar el Document ID

**⚠️ IMPORTANTE:** Anota este ID, lo necesitarás en el siguiente paso.

```
Tu Document ID: ___________________________________
```

---

## PASO 5: COMPARTIR CON n8n (IMPORTANTE)

Para que n8n pueda escribir en este sheet, debes compartirlo.

### 5.1 Abrir Configuración de Compartir

1. Haz clic en el botón **Share** en la esquina superior derecha
2. En "Add people and groups", escribe el email de la cuenta Google conectada a n8n:
   - **Email:** (el mismo que usas para Google Sheets OAuth2 en n8n)
   - **Si no sabes cuál es:** `marketing@carrilloabgd.com` (tu cuenta principal)

### 5.2 Configurar Permisos

1. Selecciona **Editor** en el dropdown de permisos
2. Haz clic en **Send** o **Share**

✅ **Sheet compartido con n8n**

---

## PASO 6: ACTUALIZAR SUB-L EN N8N CLOUD

Ahora que tienes el Document ID, necesitas actualizar el workflow.

### 6.1 Abrir SUB-L en n8n Cloud

1. Ve a: https://carrilloabgd.app.n8n.cloud
2. En la lista de workflows, busca: **SUB-L: Content Writer AI (v1.0)**
3. Haz clic para abrir

### 6.2 Actualizar el Nodo "Log Metrics to Sheets"

1. Busca el nodo llamado **Log Metrics to Sheets**
2. Haz clic en el nodo para editarlo
3. En el campo **Document**, selecciona:
   - **Mode:** By ID
   - **Document ID:** Pega el Document ID que copiaste en PASO 4.1
4. En el campo **Sheet**, selecciona:
   - **Mode:** By Name
   - **Sheet Name:** `Logs`
5. Haz clic en **Execute node** para probar (opcional)
6. Cierra el panel

### 6.3 Actualizar el Nodo "Log Error to Sheets"

1. Busca el nodo llamado **Log Error to Sheets**
2. Haz clic en el nodo para editarlo
3. En el campo **Document**, selecciona:
   - **Mode:** By ID
   - **Document ID:** Pega el mismo Document ID
4. En el campo **Sheet**, selecciona:
   - **Mode:** By Name
   - **Sheet Name:** `Errors`
5. Cierra el panel

### 6.4 Guardar el Workflow

1. Haz clic en el botón **Save** en la esquina superior derecha
2. Espera la confirmación "Workflow saved"

✅ **SUB-L actualizado con el Google Sheet**

---

## ✅ CHECKLIST FINAL - VERIFICACIÓN

Antes de ejecutar SUB-L, verifica que tienes:

- [ ] Google Sheet `MW3_ContentWriter_Logs` creado
- [ ] Tab "Logs" con 8 columnas (A-H)
- [ ] Tab "Errors" con 7 columnas (A-G)
- [ ] Headers formateados y congelados
- [ ] Sheet compartido con `marketing@carrilloabgd.com` como Editor
- [ ] Document ID copiado
- [ ] Nodos "Log Metrics to Sheets" y "Log Error to Sheets" actualizados en n8n
- [ ] Workflow guardado en n8n

---

## 📊 ESTRUCTURA FINAL ESPERADA

Después de completar este setup:

```
MW3_ContentWriter_Logs (Google Sheet)
├── Tab: Logs
│   └── A1:H1 (Header)
│       ├── A: Timestamp
│       ├── B: Keyword ID
│       ├── C: Keyword Text
│       ├── D: Content ID
│       ├── E: Word Count
│       ├── F: Status
│       ├── G: Result
│       └── H: Duration (ms)
│
└── Tab: Errors
    └── A1:G1 (Header)
        ├── A: Timestamp
        ├── B: Keyword ID
        ├── C: Keyword Text
        ├── D: Error Type
        ├── E: Error Message
        ├── F: Execution ID
        └── G: Resolution Status
```

**Después de la primera ejecución exitosa:**
```
Tab: Logs
├── A1:H1 (Header)
└── A2:H2 (Primera ejecución)
    ├── A2: 2026-01-24 10:30:15
    ├── B2: kw_test_001
    ├── C2: como registrar marca software colombia
    ├── D2: draft_1738401015123
    ├── E2: 2350
    ├── F2: pending_revision
    ├── G2: SUCCESS
    └── H2: 45000
```

---

## 🚀 PRÓXIMO PASO: TEST MANUAL

Después de completar esto, estás listo para:
1. Ir a n8n Cloud
2. Abrir SUB-L
3. Hacer clic en **Execute Workflow**
4. Ver el resultado en:
   - Firestore > content_drafts (nuevo draft)
   - Google Sheet > Logs (nueva fila)
   - Email (notificación a marketing@)

---

## ❓ TROUBLESHOOTING

### Error: "Insufficient permissions"

**Síntoma:** n8n no puede escribir en el sheet

**Solución:**
1. Verifica que compartiste el sheet con el email correcto
2. Verifica que el permiso es **Editor** (no Viewer)
3. Prueba compartir con "Anyone with the link" como Editor (temporal para debug)

### Error: "Document not found"

**Síntoma:** n8n no encuentra el sheet

**Solución:**
1. Verifica que copiaste el Document ID completo (sin espacios)
2. Verifica que el Document ID está en el formato correcto
3. Abre el sheet en tu navegador y copia el ID de nuevo

### Las filas no se agregan

**Síntoma:** El workflow ejecuta sin errores pero no hay filas nuevas

**Solución:**
1. Verifica que el nombre del tab es exactamente `Logs` (con L mayúscula)
2. Verifica que el nodo está configurado en modo "Append" (no "Update")
3. Verifica que el header está en la fila 1

---

## 📞 SOPORTE

Si tienes problemas:
1. Toma screenshot del Google Sheet mostrando tabs y headers
2. Toma screenshot del nodo en n8n mostrando la configuración del Document ID
3. Comparte en el chat

---

**Fin de la guía**
**Tiempo total:** 10 minutos
**Siguiente:** Test manual de SUB-L
