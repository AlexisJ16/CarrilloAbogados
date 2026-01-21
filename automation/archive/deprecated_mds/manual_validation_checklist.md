# Checklist de Validación Manual: SUB-A ↔ SUB-D

**Fecha**: 11 de Enero, 2026
**Propósito**: Guía paso a paso para validar integración de datos

---

## 📋 PREPARACIÓN

### Información de Workflows
- **SUB-A Lead Intake**: ID `RHj1TAqBazxNFriJ`
- **SUB-D Nurturing**: ID `PZboUEnAxm5A7Lub`
- **Firestore Collection**: `leads`
- **Firestore Credential**: `AAhdRNGzvsFnYN9O`

---

## ✅ PASO 1: VALIDAR SUB-A (15 min)

### 1.1 Acceder a n8n Cloud
```
URL: https://carrilloabgd.app.n8n.cloud
Workflow: SUB-A Lead Intake (RHj1TAqBazxNFriJ)
```

### 1.2 Verificar Nodo "Save Lead to Firestore"
- [ ] Click en el nodo "Save Lead to Firestore"
- [ ] Verificar Credential ID: `AAhdRNGzvsFnYN9O`
- [ ] Verificar Collection: `leads`
- [ ] Verificar Operation: `Create a document`

### 1.3 Verificar Campos de Nurturing (CRÍTICO)
En el nodo "Save Lead to Firestore", verificar que estos campos estén configurados:

| Campo | Valor Configurado | Check |
|-------|-------------------|-------|
| nurturing_status | "active" | [ ] |
| lead_captured_at | `{{ $now.toISO() }}` o similar | [ ] |
| emails_sent | 0 | [ ] |
| last_email_sent | null | [ ] |
| last_email_position | 0 | [ ] |

**Importante**: Los nombres de campos deben ser EXACTAMENTE como se muestran (case-sensitive).

### 1.4 Validar Workflow SUB-A
- [ ] Click en "Test workflow" (botón arriba a la derecha)
- [ ] Si hay errores, documentarlos
- [ ] Si hay warnings, documentarlos

---

## ✅ PASO 2: VALIDAR SUB-D (20 min)

### 2.1 Acceder a SUB-D
```
Workflow: SUB-D Nurturing Sequence (PZboUEnAxm5A7Lub)
```

### 2.2 Verificar Nodo "Query Active Leads"
- [ ] Click en el nodo "Query Active Leads"
- [ ] Verificar Credential ID: `AAhdRNGzvsFnYN9O` (mismo que SUB-A)
- [ ] Verificar Collection: `leads` (mismo que SUB-A)
- [ ] Verificar Operation: `Query documents`

### 2.3 Verificar Filtros de Query
El nodo debe tener estos filtros configurados:

| Filter | Operador | Valor | Check |
|--------|----------|-------|-------|
| nurturing_status | == | "active" | [ ] |
| emails_sent | < | 12 | [ ] |

**Importante**: Verificar que los nombres de campos coincidan EXACTAMENTE con SUB-A.

### 2.4 Verificar Nodo de Actualización
- [ ] Buscar nodo que actualiza Firestore (probablemente "Update Nurturing Status")
- [ ] Verificar Operation: `Update a document`
- [ ] Verificar que actualiza estos campos:

| Campo | Actualización | Check |
|-------|---------------|-------|
| emails_sent | Incrementa en 1 | [ ] |
| last_email_sent | Timestamp actual | [ ] |
| last_email_position | Posición del email (1-12) | [ ] |
| nurturing_status | "completed" si emails_sent = 12 | [ ] |

### 2.5 Validar Workflow SUB-D
- [ ] Click en "Test workflow"
- [ ] Si hay errores, documentarlos
- [ ] Si hay warnings, documentarlos

---

## ✅ PASO 3: TESTING FUNCIONAL (30 min)

### 3.1 Preparar Lead de Prueba

Crear un archivo `test_lead.json` con este contenido:
```json
{
  "email": "qa.test@techstartup.co",
  "nombre": "QA Test User",
  "empresa": "Test Company",
  "telefono": "+57 300 123 4567",
  "interes": "Registro de Marca",
  "mensaje": "Este es un lead de prueba para validar integración SUB-A ↔ SUB-D. Por favor ignorar."
}
```

### 3.2 Enviar Lead a SUB-A

**Opción A: Via Orquestador (Recomendado)**
```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d @test_lead.json
```

**Opción B: Via SUB-A Directo (Manual)**
- [ ] Abrir SUB-A en n8n Cloud
- [ ] Click en "Execute Workflow"
- [ ] Pegar el JSON de prueba en el trigger
- [ ] Click en "Execute"

### 3.3 Verificar en Firestore

- [ ] Acceder a Google Cloud Console
- [ ] Navegar a Firestore Database
- [ ] Buscar el documento del lead (email: qa.test@techstartup.co)
- [ ] Verificar que el documento tiene TODOS estos campos:

```json
{
  "email": "qa.test@techstartup.co",
  "nombre": "QA Test User",
  "nurturing_status": "active",        // ← CRÍTICO
  "lead_captured_at": "2026-01-11...", // ← CRÍTICO
  "emails_sent": 0,                    // ← CRÍTICO
  "last_email_sent": null,             // ← CRÍTICO
  "last_email_position": 0             // ← CRÍTICO
}
```

- [ ] Copiar el Document ID del lead (lo necesitarás para seguimiento)

**Document ID**: ___________________________

### 3.4 Ejecutar SUB-D Manualmente

- [ ] Abrir SUB-D en n8n Cloud
- [ ] Click en "Test workflow" (arriba a la derecha)
- [ ] Esperar a que termine la ejecución
- [ ] Verificar que NO hubo errores

### 3.5 Verificar que SUB-D Seleccionó el Lead

- [ ] En la ejecución de SUB-D, click en el nodo "Query Active Leads"
- [ ] Verificar en el output que el lead qa.test@techstartup.co está presente
- [ ] Si NO está presente, hay un problema con los filtros (ir a Troubleshooting)

### 3.6 Verificar que el Email se Envió

**Si Mailersend está configurado**:
- [ ] Revisar inbox de qa.test@techstartup.co
- [ ] Verificar que recibió el email #1 (Bienvenida)
- [ ] Verificar que el contenido es correcto

**Si Mailersend NO está configurado**:
- [ ] Verificar que el nodo de envío de email ejecutó sin errores
- [ ] Verificar en los logs que el email se "envió" (aunque no llegue realmente)

### 3.7 Verificar Actualización en Firestore

- [ ] Volver a Google Cloud Console → Firestore
- [ ] Buscar el mismo documento (Document ID: _______________)
- [ ] Verificar que los campos se actualizaron:

```json
{
  "email": "qa.test@techstartup.co",
  "nurturing_status": "active",        // ← Debe seguir "active"
  "emails_sent": 1,                    // ← Debe ser 1 (incrementado)
  "last_email_sent": "2026-01-11...",  // ← Debe tener timestamp
  "last_email_position": 1             // ← Debe ser 1
}
```

- [ ] Si los valores NO se actualizaron, hay un problema (ir a Troubleshooting)

---

## ✅ PASO 4: VERIFICAR COMPATIBILIDAD DE CAMPOS

### 4.1 Comparación de Nombres de Campos

Crear una tabla comparando SUB-A y SUB-D:

| Campo | SUB-A (Escribe) | SUB-D (Lee) | SUB-D (Actualiza) | Match? |
|-------|-----------------|-------------|-------------------|--------|
| nurturing_status | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No |
| lead_captured_at | [ ] Si [ ] No | [ ] Si [ ] No | [ ] N/A | [ ] Si [ ] No |
| emails_sent | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No |
| last_email_sent | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No |
| last_email_position | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No | [ ] Si [ ] No |

**Si algún campo no coincide (Match = No), documentar la diferencia**:
- Campo en SUB-A: ___________________________
- Campo en SUB-D: ___________________________

### 4.2 Verificar Collection Name

- [ ] SUB-A Collection: ___________________________
- [ ] SUB-D Query Collection: ___________________________
- [ ] SUB-D Update Collection: ___________________________
- [ ] Todos coinciden: [ ] Si [ ] No

### 4.3 Verificar Credential ID

- [ ] SUB-A Credential: ___________________________
- [ ] SUB-D Query Credential: ___________________________
- [ ] SUB-D Update Credential: ___________________________
- [ ] Todos coinciden: [ ] Si [ ] No

---

## ✅ PASO 5: DOCUMENTAR RESULTADOS

### 5.1 Resumen de Validación

**SUB-A**:
- [ ] Workflow válido (0 errores críticos)
- [ ] Campos de nurturing configurados
- [ ] Lead de prueba guardado correctamente

**SUB-D**:
- [ ] Workflow válido (0 errores críticos)
- [ ] Query selecciona leads correctamente
- [ ] Email se envía correctamente
- [ ] Firestore se actualiza correctamente

**Integración**:
- [ ] Nombres de campos coinciden
- [ ] Collection names coinciden
- [ ] Credentials coinciden
- [ ] Testing E2E exitoso

### 5.2 Errores Encontrados

Si hay errores, documentarlos aquí:

**Error 1**:
- Ubicación: ___________________________
- Descripción: ___________________________
- Severidad: [ ] Crítico [ ] Alto [ ] Medio [ ] Bajo
- Solución propuesta: ___________________________

**Error 2**:
- Ubicación: ___________________________
- Descripción: ___________________________
- Severidad: [ ] Crítico [ ] Alto [ ] Medio [ ] Bajo
- Solución propuesta: ___________________________

### 5.3 Warnings Encontrados

Si hay warnings, documentarlos aquí:

**Warning 1**:
- Ubicación: ___________________________
- Descripción: ___________________________
- Impacto: ___________________________

**Warning 2**:
- Ubicación: ___________________________
- Descripción: ___________________________
- Impacto: ___________________________

---

## ✅ DECISIÓN FINAL

Basándose en los resultados de la validación, marcar UNA de estas opciones:

### [ ] ✅ APROBAR PARA PRODUCCIÓN

**Condiciones cumplidas**:
- 0 errores críticos
- Todos los campos coinciden
- Testing E2E exitoso
- 0 warnings de alto impacto

**Próximo paso**: Activar schedule de SUB-D (cada 6 horas)

---

### [ ] ⚠️ APROBAR CON WARNINGS

**Condiciones cumplidas**:
- 0 errores críticos
- Testing E2E exitoso
- Warnings documentados con plan de remediación

**Warnings identificados**: ___________________________

**Plan de remediación**:
1. ___________________________
2. ___________________________
3. ___________________________

**Próximo paso**: Activar con monitoreo intensivo primeras 48 horas

---

### [ ] ❌ RECHAZAR - REQUIERE CORRECCIONES

**Razones**:
- [ ] 1+ errores críticos encontrados
- [ ] Campos no coinciden
- [ ] Testing E2E fallido
- [ ] Integración no funciona

**Errores críticos**:
1. ___________________________
2. ___________________________
3. ___________________________

**Próximo paso**: Corregir errores críticos y re-validar

---

## 🆘 TROUBLESHOOTING

### Problema: Lead no aparece en Query de SUB-D

**Posibles causas**:
1. Campo nurturing_status no se guardó en SUB-A
2. Nombre de campo no coincide (ej: "nurtuting_status" vs "nurturing_status")
3. Valor no coincide (ej: "Active" vs "active")
4. Filtro de SUB-D está mal configurado

**Solución**:
1. Verificar en Firestore Console que el campo existe
2. Comparar nombre exacto del campo (case-sensitive)
3. Verificar que el filtro de SUB-D use el operador correcto (==)

---

### Problema: Firestore no se actualiza después de SUB-D

**Posibles causas**:
1. Nodo de actualización no está conectado
2. Document ID no se está pasando correctamente
3. Campos de actualización tienen nombres incorrectos
4. Credential no tiene permisos de escritura

**Solución**:
1. Verificar flujo de nodos en SUB-D
2. Verificar que el Document ID se obtiene de la query
3. Comparar nombres de campos con lo que está en Firestore
4. Verificar permisos de la Service Account de Firestore

---

### Problema: Errores de validación en n8n

**Posibles causas**:
1. Expresiones n8n inválidas
2. Nodos no conectados
3. Campos requeridos faltantes
4. Credenciales no configuradas

**Solución**:
1. Leer el mensaje de error completo
2. Verificar la expresión en el nodo específico
3. Verificar que todos los nodos tengan conexiones
4. Verificar que las credenciales están activas

---

## 📋 ENTREGABLES

Después de completar este checklist, generar:

1. **Screenshot de SUB-A**:
   - Nodo "Save Lead to Firestore" mostrando campos configurados
   - Guardar como: `SUB-A_firestore_config.png`

2. **Screenshot de SUB-D**:
   - Nodo "Query Active Leads" mostrando filtros
   - Guardar como: `SUB-D_query_config.png`

3. **Screenshot de Firestore**:
   - Documento del lead de prueba mostrando todos los campos
   - Guardar como: `firestore_lead_document.png`

4. **Screenshot de Ejecución de SUB-D**:
   - Ejecución exitosa mostrando output
   - Guardar como: `SUB-D_execution_success.png`

5. **Este checklist completado**:
   - Con todas las casillas marcadas
   - Con errores/warnings documentados
   - Con decisión final marcada

---

## 📞 CONTACTO

Si encuentras problemas críticos durante la validación:
- **Email**: ingenieria@carrilloabgd.com
- **Escalación**: Documentar en `integration_validation_report_v1.md`

---

**Tiempo estimado total**: 60-90 minutos
**Completado por**: ___________________________
**Fecha**: ___________________________
**Decisión**: [ ] Aprobar [ ] Aprobar con Warnings [ ] Rechazar
