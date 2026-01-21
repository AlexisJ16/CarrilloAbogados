# Reporte de Testing: SUB-A Lead Intake

**Fecha**: 21 de Enero, 2026
**Workflow ID**: RHj1TAqBazxNFriJ
**Testeador**: QA Specialist Agent
**Estado General**: ❌ RECHAZADO - FIX NO APLICADO

---

## RESUMEN EJECUTIVO

- **Validación Estructural**: ✅ Workflow válido
- **Bug Crítico Conocido**: ❌ NO CORREGIDO
- **Tests Funcionales**: ⏸️ NO EJECUTADOS (bug bloqueante)
- **Estado del Fix**: ❌ Código corregido NO aplicado en n8n Cloud

---

## 1. VALIDACIÓN ESTRUCTURAL

### Validación de Workflow
✅ **Status**: Workflow estructuralmente válido
- **Nodos**: 17 nodos configurados
- **Conexiones**: Todas verificadas correctamente
- **Estado**: Inactivo (se invoca como Tool desde Orquestador v3.0)

### Información del Workflow
```json
{
  "id": "RHj1TAqBazxNFriJ",
  "name": "SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)",
  "active": false,
  "nodes": 17,
  "connections": "OK",
  "createdAt": "2026-01-08T04:54:46.618Z",
  "updatedAt": "2026-01-14T18:23:03.752Z"
}
```

---

## 2. BUG CRÍTICO DETECTADO

### 🔴 BUG-001: Nodo de Mapeo NO Corregido

**Severidad**: 🔴 CRÍTICO
**Ubicación**: Nodo "0. Mapear Input del Orquestador1"
**Reproducible**: Sí (100% de ejecuciones desde Orquestador v3.0)

#### Descripción
El nodo de mapeo de input NO contiene el fix para parsear el campo `query` que envía el AI Agent del Orquestador v3.0. Esto causa que TODOS los campos del lead se extraigan como vacíos.

#### Causa Raíz
Cuando el Orquestador v3.0 (AI Agent) invoca el Tool "SUB-A Lead Intake" con `autoMapInputData`, n8n serializa los datos en un campo `query` como JSON string. El código actual del nodo de mapeo NO incluye un caso para parsear este campo.

#### Datos de Entrada Esperados
```json
{
  "query": "{\"timestamp\":\"...\",\"source\":\"portal_web\",\"payload\":{\"nombre\":\"...\",\"email\":\"...\",...}}"
}
```

#### Comportamiento Actual
El código busca datos en:
1. `raw.payload`
2. `raw.input` (string o objeto)
3. `raw.body`
4. `raw.nombre` (directo)
5. Fallback genérico

**FALTA**: Caso para `raw.query`

#### Comportamiento Esperado
El código DEBE incluir (al inicio, como prioridad):
```javascript
// CASO NUEVO: Datos vienen en campo 'query' como JSON string
if (raw.query && typeof raw.query === 'string') {
  try {
    data = JSON.parse(raw.query);
    console.log('✅ Parseado desde campo query (JSON string)');
  } catch(e) {
    console.log('❌ Error parseando query:', e.message);
    data = raw;
  }
}
```

#### Error Message
No hay mensaje de error explícito, pero el output del nodo muestra:
```json
{
  "lead_id": "",
  "nombre": "",
  "email": "",
  "telefono": "",
  "empresa": "",
  "cargo": "",
  "servicio_interes": "",
  "mensaje": ""
}
```

#### Impacto
**CRÍTICO** - El workflow SUB-A NO puede procesar leads enviados desde el Orquestador v3.0:
- ❌ No se puede validar el email (campo vacío)
- ❌ No se puede calcular el AI score (sin datos del lead)
- ❌ No se puede enviar respuesta personalizada (sin contexto)
- ❌ No se puede guardar el lead en Firestore (datos vacíos)
- ❌ No se puede enviar callbacks al backend (sin información)

#### Solución Aplicar
**Archivo con Fix**: `c:\CarrilloAbogados\automation\workflows\MW1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\FIXED_MAPEAR_INPUT.js`

**Pasos**:
1. Abrir n8n Cloud: https://carrilloabgd.app.n8n.cloud
2. Editar workflow "SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)"
3. Abrir nodo "0. Mapear Input del Orquestador1"
4. Reemplazar el código JavaScript con el contenido de FIXED_MAPEAR_INPUT.js
5. Guardar workflow
6. Ejecutar test E2E desde Orquestador v3.0
7. Verificar logs del nodo muestran: "✅ Parseado desde campo query (JSON string)"

---

## 3. TESTING FUNCIONAL

### Tests NO Ejecutados

❌ **Tests bloqueados por BUG-001**

Los siguientes tests NO pueden ejecutarse hasta que se corrija el bug de mapeo:
- Test Case 1: Lead válido con scoring >= 70 (HOT)
- Test Case 2: Lead válido con scoring 40-69 (WARM)
- Test Case 3: Lead válido con scoring < 40 (COLD)
- Test Case 4: Email inválido (validación)
- Test Case 5: Campos faltantes (validación)
- Test Case 6: Detección de spam

**Razón**: Si el nodo de mapeo extrae campos vacíos, TODOS los tests posteriores fallarán.

---

## 4. ERROR HANDLING

### Nodos con Error Handling (No Validado)
⏸️ **Status**: Validación pendiente hasta corregir BUG-001

Los siguientes nodos DEBERÍAN tener error handling, pero no se puede verificar sin un flujo funcional:
- [ ] Nodo validación email
- [ ] Nodo Gemini AI scoring
- [ ] Nodo Firestore write
- [ ] Nodos HTTP callbacks al backend
- [ ] Nodo Gmail send

---

## 5. VALIDACIÓN DE INTEGRACIONES

### Credenciales Configuradas
✅ **Google Gemini API**: Credential ID `jk2FHcbAC71LuRl2`
✅ **Gmail OAuth2**: Credential ID `l2mMgEf8YUV7HHlK`
✅ **Google Firestore**: Credential ID `AAhdRNGzvsFnYN9O`

**NOTA**: Aunque las credenciales están configuradas, no se pueden probar las integraciones hasta corregir el bug de mapeo.

---

## 6. ANÁLISIS DE EJECUCIONES RECIENTES

### Última Ejecución Registrada
- **Fecha**: 2026-01-14 18:24 UTC
- **Lead**: Andres Felipe Gutierrez (alexisj4@gmail.com)
- **Resultado**: ❌ FALLÓ - Campos extraídos vacíos
- **Evidencia**: Documentada en `ANALISIS_ERROR_MAPEO.md`

### Pattern Detectado
100% de ejecuciones desde Orquestador v3.0 AI Agent fallan en el nodo de mapeo.

---

## DECISIÓN FINAL

### ❌ RECHAZADO - BUG CRÍTICO NO CORREGIDO

El workflow SUB-A NO está listo para producción. El bug de mapeo de input es bloqueante.

**Severidad**: 🔴 CRÍTICO
**Bloquea**: Toda la funcionalidad del workflow
**Impacto en Usuario**: Leads no procesados, sin respuesta automatizada
**Prioridad**: P0 - INMEDIATO

---

## PRÓXIMOS PASOS OBLIGATORIOS

### 1. APLICAR FIX (P0 - CRÍTICO)
**Responsable**: Ingeniero de n8n
**Tiempo Estimado**: 5 minutos
**Acción**:
```bash
# Archivo con fix:
c:\CarrilloAbogados\automation\workflows\MW1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\FIXED_MAPEAR_INPUT.js

# Guía de aplicación:
c:\CarrilloAbogados\automation\workflows\MW1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\GUIA_APLICAR_FIX.md
```

### 2. RE-TEST COMPLETO (P0)
Una vez aplicado el fix, ejecutar:
- Test manual desde Orquestador v3.0
- Verificar logs del nodo: "✅ Parseado desde campo query"
- Validar campos extraídos con valores correctos
- Test E2E completo: Webhook → Orquestador → SUB-A → Backend

### 3. RE-VALIDACIÓN QA (P1)
Después de aplicar fix y test inicial:
- Ejecutar suite completa de tests funcionales (6 test cases)
- Validar error handling
- Verificar integraciones (Gemini, Firestore, Gmail)
- Análisis de performance

---

## ARCHIVOS GENERADOS

- ✅ `test_report_v1.md` (este archivo)
- ✅ `validation_results.json` (pendiente de generar post-fix)
- ⏸️ `test_data.json` (pendiente - bloqueado por bug)
- ⏸️ `bug_report_v1.md` (no necesario - bug ya documentado)

---

## HANDOFF A INGENIERO

**Workflow**: SUB-A Lead Intake
**Status**: ❌ Rechazado - Bug crítico
**Bug ID**: BUG-001 (nodo mapeo input)
**Fix Disponible**: ✅ Código corregido en `FIXED_MAPEAR_INPUT.js`

**Acción Requerida**:
1. Aplicar fix en n8n Cloud (5 min)
2. Test manual de validación (2 min)
3. Notificar a QA para re-validación completa

**Comando para Usuario**:
```
ACCIÓN INMEDIATA: Aplicar fix en nodo "0. Mapear Input del Orquestador1"
Ver guía: automation/workflows/MW1_LEAD_LIFECYCLE/02-spokes/sub-a-lead-intake/GUIA_APLICAR_FIX.md
```

---

**Testeado por**: QA Specialist Agent
**Firma**: ❌ Rechazado hasta corregir BUG-001
**Fecha**: 2026-01-21
**Workflow ID**: RHj1TAqBazxNFriJ
