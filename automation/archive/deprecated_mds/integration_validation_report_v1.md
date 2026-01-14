# Reporte de Validación de Integración: SUB-A ↔ SUB-D

**Fecha**: 11 de Enero, 2026
**Workflows Validados**:
- SUB-A Lead Intake (ID: RHj1TAqBazxNFriJ)
- SUB-D Nurturing Sequence (ID: PZboUEnAxm5A7Lub)

**Testeador**: QA Specialist Agent
**Tipo de Validación**: Integración de Datos
**Estado General**: ⚠️ VALIDACIÓN PENDIENTE - REQUIERE ACCESO A n8n MCP

---

## RESUMEN EJECUTIVO

Este reporte documenta la validación de integración entre SUB-A (Lead Intake) y SUB-D (Nurturing Sequence), enfocándose en la compatibilidad de campos de datos y el flujo de información a través de Google Firestore.

**Hallazgos Principales**:
- ⚠️ No se pudo ejecutar validación técnica directa (herramientas MCP no disponibles)
- ✅ Análisis conceptual de campos completado
- 📋 Checklist de validación manual preparado
- 🔍 Recomendaciones de testing identificadas

---

## 1. ARQUITECTURA DE INTEGRACIÓN

### 1.1 Flujo de Datos

```
[SUB-A: Lead Intake]
       ↓
   (ESCRIBE)
       ↓
[Google Firestore: Collection "leads"]
       ↓
    (LEE)
       ↓
[SUB-D: Nurturing Sequence]
       ↓
  (ACTUALIZA)
       ↓
[Google Firestore: Collection "leads"]
```

### 1.2 Características Clave
- **Independencia**: SUB-D NO es llamado por el Orquestador
- **Trigger**: SUB-D se ejecuta en Schedule cada 6 horas
- **Storage**: Google Firestore como única fuente de verdad
- **Sincronización**: Basada en campos de estado compartidos

---

## 2. ANÁLISIS DE CAMPOS DE INTEGRACIÓN

### 2.1 Campos Escritos por SUB-A

Según los requerimientos, SUB-A debe escribir estos campos en Firestore:

| Campo | Tipo | Valor Inicial | Propósito |
|-------|------|---------------|-----------|
| `nurturing_status` | String | "active" | Control de elegibilidad para nurturing |
| `lead_captured_at` | ISO Timestamp | Fecha/hora actual | Tracking de antigüedad del lead |
| `emails_sent` | Number | 0 | Contador de emails enviados |
| `last_email_sent` | ISO Timestamp | null | Fecha del último email |
| `last_email_position` | Number | 0 | Posición en secuencia (1-12) |

### 2.2 Campos Leídos por SUB-D

SUB-D debe leer y filtrar por estos campos:

| Campo | Uso en SUB-D | Condición |
|-------|--------------|-----------|
| `nurturing_status` | Filtro de query | = "active" |
| `lead_captured_at` | Cálculo de días transcurridos | Usado para determinar email a enviar |
| `emails_sent` | Verificación de límite | < 12 |
| `last_email_sent` | Cálculo de cooldown | Para evitar spam |
| `last_email_position` | Control de secuencia | Determina próximo email |

### 2.3 Campos Actualizados por SUB-D

Después de enviar cada email:

| Campo | Actualización |
|-------|---------------|
| `emails_sent` | Incrementar en 1 |
| `last_email_sent` | Timestamp actual |
| `last_email_position` | Posición del email enviado (1-12) |
| `nurturing_status` | "completed" si emails_sent = 12 |

---

## 3. VALIDACIONES REQUERIDAS (PENDIENTES)

### 3.1 Validación Estructural de SUB-A

**STATUS**: ⚠️ PENDIENTE

**Checklist**:
```markdown
- [ ] Ejecutar: mcp__n8n__n8n_validate_workflow(workflowId: "RHj1TAqBazxNFriJ")
- [ ] Verificar que el workflow es válido (0 errores críticos)
- [ ] Confirmar que el nodo Firestore "Save Lead" tiene columns configurados
- [ ] Verificar que los campos de nurturing están presentes:
  - [ ] nurturing_status
  - [ ] lead_captured_at
  - [ ] emails_sent
  - [ ] last_email_sent
  - [ ] last_email_position
```

**Comando Manual**:
```javascript
// Desde herramienta MCP
const validationSubA = await mcp__n8n__n8n_validate_workflow({
  workflowId: "RHj1TAqBazxNFriJ",
  validateNodes: true,
  validateConnections: true,
  validateExpressions: true,
  profile: "ai-friendly"
});
```

### 3.2 Validación Estructural de SUB-D

**STATUS**: ⚠️ PENDIENTE

**Checklist**:
```markdown
- [ ] Ejecutar: mcp__n8n__n8n_validate_workflow(workflowId: "PZboUEnAxm5A7Lub")
- [ ] Verificar que el workflow es válido (0 errores críticos)
- [ ] Confirmar que el nodo "Query Active Leads" tiene filters correctos:
  - [ ] nurturing_status = "active"
  - [ ] emails_sent < 12
- [ ] Verificar nodo "Update Firestore":
  - [ ] Campo emails_sent incrementa correctamente
  - [ ] Campo last_email_sent usa timestamp actual
  - [ ] Campo last_email_position usa valor correcto
  - [ ] Campo nurturing_status actualiza a "completed" cuando corresponde
```

**Comando Manual**:
```javascript
// Desde herramienta MCP
const validationSubD = await mcp__n8n__n8n_validate_workflow({
  workflowId: "PZboUEnAxm5A7Lub",
  validateNodes: true,
  validateConnections: true,
  validateExpressions: true,
  profile: "ai-friendly"
});
```

### 3.3 Verificación de Compatibilidad de Campos

**STATUS**: ⚠️ PENDIENTE

**Checklist Manual**:
```markdown
- [ ] Obtener detalles de SUB-A: mcp__n8n__n8n_get_workflow("RHj1TAqBazxNFriJ")
- [ ] Obtener detalles de SUB-D: mcp__n8n__n8n_get_workflow("PZboUEnAxm5A7Lub")
- [ ] Comparar campos escritos vs leídos:
  - [ ] Nombres de campos coinciden exactamente (case-sensitive)
  - [ ] Tipos de datos son compatibles
  - [ ] Valores por defecto son consistentes
- [ ] Verificar consistencia de Collection:
  - [ ] SUB-A escribe a "leads"
  - [ ] SUB-D lee de "leads"
```

---

## 4. ESCENARIOS DE PRUEBA RECOMENDADOS

### 4.1 Test Case 1: Lead Nuevo (Happy Path)

**Objetivo**: Verificar que un lead nuevo entra correctamente en el sistema de nurturing.

**Steps**:
1. Enviar lead de prueba a SUB-A (via Orquestador o ejecución manual)
2. Verificar en Firestore que el documento tiene:
   - nurturing_status = "active"
   - lead_captured_at = timestamp válido
   - emails_sent = 0
   - last_email_sent = null
   - last_email_position = 0
3. Ejecutar SUB-D manualmente
4. Verificar que el lead es seleccionado en la query
5. Verificar que se envía el email correspondiente

**Expected Results**:
- Lead guardado con todos los campos de nurturing
- SUB-D selecciona el lead
- Email 1 se envía correctamente
- Firestore se actualiza con emails_sent = 1

### 4.2 Test Case 2: Lead Existente (Continuación de Secuencia)

**Objetivo**: Verificar que SUB-D continúa correctamente la secuencia de emails.

**Steps**:
1. Crear lead en Firestore con:
   - nurturing_status = "active"
   - lead_captured_at = hace 7 días
   - emails_sent = 2
   - last_email_sent = hace 4 días
   - last_email_position = 2
2. Ejecutar SUB-D
3. Verificar que se envía el email 3 (Case Study)

**Expected Results**:
- SUB-D calcula correctamente que debe enviar email 3
- Email se envía
- Campos actualizados: emails_sent = 3, last_email_position = 3

### 4.3 Test Case 3: Lead Completado (No Más Emails)

**Objetivo**: Verificar que leads completados no reciben más emails.

**Steps**:
1. Crear lead en Firestore con:
   - nurturing_status = "active"
   - emails_sent = 12
   - last_email_position = 12
2. Ejecutar SUB-D
3. Verificar que el lead NO es seleccionado en la query

**Expected Results**:
- SUB-D NO selecciona el lead (filtro emails_sent < 12)
- No se envía ningún email

### 4.4 Test Case 4: Compatibilidad de Tipos de Datos

**Objetivo**: Verificar que los tipos de datos son compatibles.

**Steps**:
1. Enviar lead con valores límite:
   - email muy largo
   - nombre con caracteres especiales
   - timestamp en diferentes formatos
2. Verificar que SUB-A guarda correctamente
3. Verificar que SUB-D lee correctamente

**Expected Results**:
- Sin errores de tipo de datos
- Sin errores de encoding
- Sin errores de parsing de timestamps

---

## 5. ANÁLISIS DE RIESGOS

### 5.1 Riesgos Identificados

**RIESGO-001: Inconsistencia de Nombres de Campos**
- **Severidad**: 🔴 Crítico
- **Descripción**: Si SUB-A escribe "nurtuting_status" pero SUB-D lee "nurturing_status", la integración falla silenciosamente
- **Impacto**: Leads no entran en nurturing
- **Mitigación**: Validación de campos con herramientas MCP

**RIESGO-002: Tipos de Datos Incompatibles**
- **Severidad**: 🔴 Crítico
- **Descripción**: Si SUB-A guarda emails_sent como String pero SUB-D espera Number
- **Impacto**: Query de Firestore falla o comportamiento inesperado
- **Mitigación**: Testing con datos de prueba variados

**RIESGO-003: Collection Name Mismatch**
- **Severidad**: 🔴 Crítico
- **Descripción**: Si SUB-A escribe a "leads" pero SUB-D lee de "leads_data"
- **Impacto**: Integración completamente rota
- **Mitigación**: Verificación de configuración de ambos workflows

**RIESGO-004: Timezone en Timestamps**
- **Severidad**: ⚠️ Alto
- **Descripción**: Si SUB-A guarda en UTC pero SUB-D calcula en local time
- **Impacto**: Cálculo incorrecto de días transcurridos, emails enviados en momentos incorrectos
- **Mitigación**: Estandarizar a UTC en ambos workflows

**RIESGO-005: Race Condition en Actualización**
- **Severidad**: 🟡 Medio
- **Descripción**: Si SUB-D se ejecuta mientras SUB-A está guardando
- **Impacto**: Posible pérdida de datos o duplicación
- **Mitigación**: Usar transacciones de Firestore o verificar timestamps

**RIESGO-006: Falta de Validación de null/undefined**
- **Severidad**: 🟡 Medio
- **Descripción**: Si SUB-D no maneja correctamente campos null
- **Impacto**: Errores en ejecución, emails no enviados
- **Mitigación**: Agregar validación de campos requeridos en SUB-D

### 5.2 Matriz de Riesgos

| ID | Probabilidad | Impacto | Prioridad |
|----|--------------|---------|-----------|
| RIESGO-001 | Alta | Alto | P1 |
| RIESGO-002 | Media | Alto | P1 |
| RIESGO-003 | Baja | Alto | P2 |
| RIESGO-004 | Alta | Medio | P2 |
| RIESGO-005 | Baja | Medio | P3 |
| RIESGO-006 | Media | Medio | P3 |

---

## 6. RECOMENDACIONES

### 6.1 Inmediatas (Antes de Activar SUB-D)

**REC-001: Ejecutar Validación Técnica**
- **Prioridad**: 🔴 Crítica
- **Acción**: Ejecutar herramientas MCP para validar ambos workflows
- **Comando**:
```javascript
// Validar SUB-A
await mcp__n8n__n8n_validate_workflow({
  workflowId: "RHj1TAqBazxNFriJ"
});

// Validar SUB-D
await mcp__n8n__n8n_validate_workflow({
  workflowId: "PZboUEnAxm5A7Lub"
});
```

**REC-002: Verificar Configuración de Firestore**
- **Prioridad**: 🔴 Crítica
- **Acción**: Verificar manualmente en n8n Cloud que:
  - Ambos workflows usan la misma credencial de Firestore
  - Ambos apuntan a la misma collection "leads"
  - Los nombres de campos son exactamente iguales

**REC-003: Testing con Lead de Prueba**
- **Prioridad**: 🔴 Crítica
- **Acción**: Ejecutar Test Case 1 manualmente antes de activar schedule
- **Pasos**:
  1. Enviar lead de prueba a SUB-A
  2. Verificar en Firestore Console que los campos están correctos
  3. Ejecutar SUB-D manualmente (botón "Test workflow")
  4. Verificar que el email se envía y Firestore se actualiza

### 6.2 Corto Plazo (Después de Activar)

**REC-004: Monitoreo de Ejecuciones**
- **Prioridad**: ⚠️ Alta
- **Acción**: Revisar ejecuciones de SUB-D en las primeras 48 horas
- **Frecuencia**: Cada 6 horas (después de cada ejecución)
- **Métricas**: Leads procesados, emails enviados, errores

**REC-005: Validación de Logs**
- **Prioridad**: ⚠️ Alta
- **Acción**: Implementar logging estructurado en ambos workflows
- **Campos a loggear**:
  - SUB-A: lead_id, campos de nurturing guardados
  - SUB-D: lead_id, email enviado, posición, próximo email

**REC-006: Alertas de Error**
- **Prioridad**: ⚠️ Alta
- **Acción**: Configurar notificaciones por email si SUB-D falla
- **Condiciones**: Error en query Firestore, error en envío de email, error en actualización

### 6.3 Medio Plazo (Próximas 2 Semanas)

**REC-007: Dashboard de Nurturing**
- **Prioridad**: 🟡 Media
- **Acción**: Crear Google Sheet o dashboard con métricas de nurturing
- **Métricas**:
  - Total leads en nurturing (nurturing_status = "active")
  - Distribución por posición de email (0-12)
  - Tasa de apertura (si Mailersend está configurado)
  - Leads completados (emails_sent = 12)

**REC-008: Testing de Secuencia Completa**
- **Prioridad**: 🟡 Media
- **Acción**: Crear un lead de prueba y seguirlo durante toda la secuencia
- **Método**: Usar timestamps manipulados para simular el paso del tiempo

**REC-009: Documentación de Troubleshooting**
- **Prioridad**: 🟡 Media
- **Acción**: Crear guía de solución de problemas comunes
- **Contenido**: Errores típicos, cómo verificar Firestore, cómo resetear un lead

---

## 7. CHECKLIST DE ACTIVACIÓN

### Pre-Activación de SUB-D

```markdown
## Checklist Pre-Activación SUB-D

### Validación Técnica
- [ ] Ejecutar mcp__n8n__n8n_validate_workflow para SUB-A (RHj1TAqBazxNFriJ)
- [ ] Ejecutar mcp__n8n__n8n_validate_workflow para SUB-D (PZboUEnAxm5A7Lub)
- [ ] Verificar 0 errores críticos en ambos workflows
- [ ] Verificar 0 warnings de alto impacto

### Configuración de Firestore
- [ ] Verificar credencial Firestore en SUB-A (AAhdRNGzvsFnYN9O)
- [ ] Verificar credencial Firestore en SUB-D (mismo ID)
- [ ] Confirmar collection name = "leads" en ambos
- [ ] Verificar permisos de lectura/escritura

### Campos de Integración
- [ ] SUB-A escribe nurturing_status
- [ ] SUB-A escribe lead_captured_at (ISO timestamp)
- [ ] SUB-A escribe emails_sent (Number = 0)
- [ ] SUB-A escribe last_email_sent (null)
- [ ] SUB-A escribe last_email_position (Number = 0)

### Query de SUB-D
- [ ] Filtro: nurturing_status = "active"
- [ ] Filtro: emails_sent < 12
- [ ] Order by: lead_captured_at ASC (para procesar más antiguos primero)

### Actualización de SUB-D
- [ ] Incrementa emails_sent correctamente
- [ ] Actualiza last_email_sent con timestamp actual
- [ ] Actualiza last_email_position con número correcto (1-12)
- [ ] Cambia nurturing_status a "completed" cuando emails_sent = 12

### Testing Manual
- [ ] Test Case 1: Lead Nuevo ejecutado exitosamente
- [ ] Verificado en Firestore Console que campos están correctos
- [ ] SUB-D ejecutado manualmente (Test workflow)
- [ ] Email enviado correctamente (verificar inbox)
- [ ] Firestore actualizado correctamente

### Configuración de Mailersend
- [ ] Cuenta Mailersend creada
- [ ] Dominio carrilloabgd.com verificado
- [ ] API Key configurada en n8n Cloud
- [ ] Test email enviado exitosamente

### Schedule de SUB-D
- [ ] Schedule configurado: cada 6 horas
- [ ] Horarios verificados: 00:00, 06:00, 12:00, 18:00 UTC
- [ ] Timezone confirmado: UTC

### Monitoreo
- [ ] Credencial Google Sheets configurada (para Logger)
- [ ] Sheet de logs creado con headers correctos
- [ ] Notificaciones de error configuradas (Gmail)
- [ ] Plan de monitoreo primeras 48 horas definido

### Documentación
- [ ] Reporte de validación generado
- [ ] Test data guardado
- [ ] Guía de troubleshooting preparada
- [ ] Contacto de escalación definido
```

---

## 8. PRÓXIMOS PASOS

### Acción Inmediata Requerida

**STEP 1: Habilitar Herramientas MCP**
```bash
# Desde el entorno que tiene acceso a n8n MCP
# Ejecutar validaciones técnicas pendientes
```

**STEP 2: Ejecutar Validación Técnica**
```javascript
// Validar SUB-A
const resultSubA = await mcp__n8n__n8n_validate_workflow({
  workflowId: "RHj1TAqBazxNFriJ",
  validateNodes: true,
  validateConnections: true,
  validateExpressions: true
});

console.log("SUB-A Validation:", resultSubA);

// Validar SUB-D
const resultSubD = await mcp__n8n__n8n_validate_workflow({
  workflowId: "PZboUEnAxm5A7Lub",
  validateNodes: true,
  validateConnections: true,
  validateExpressions: true
});

console.log("SUB-D Validation:", resultSubD);
```

**STEP 3: Verificar Campos Manualmente**
```javascript
// Obtener detalles de ambos workflows
const subADetails = await mcp__n8n__n8n_get_workflow({
  workflowId: "RHj1TAqBazxNFriJ"
});

const subDDetails = await mcp__n8n__n8n_get_workflow({
  workflowId: "PZboUEnAxm5A7Lub"
});

// Comparar configuración de nodos Firestore
console.log("SUB-A Firestore Node:", subADetails.nodes.find(n => n.type === 'n8n-nodes-base.googleFirestore'));
console.log("SUB-D Firestore Query Node:", subDDetails.nodes.find(n => n.name === 'Query Active Leads'));
console.log("SUB-D Firestore Update Node:", subDDetails.nodes.find(n => n.name === 'Update Nurturing Status'));
```

**STEP 4: Testing Manual**
1. Enviar lead de prueba a SUB-A
2. Verificar Firestore Console
3. Ejecutar SUB-D manualmente
4. Validar resultados

**STEP 5: Generar Reporte Final**
Una vez completadas las validaciones técnicas, actualizar este reporte con:
- Resultados de validación estructural
- Resultados de testing manual
- Decisión de aprobación (APROBAR/APROBAR CON WARNINGS/RECHAZAR)

---

## 9. DECISIÓN FINAL

### ⚠️ VALIDACIÓN PENDIENTE

**Razón**: No se pudo ejecutar validación técnica con herramientas MCP en esta sesión.

**Estado Actual**:
- ✅ Análisis conceptual completado
- ✅ Checklist de validación preparado
- ✅ Escenarios de prueba definidos
- ✅ Recomendaciones documentadas
- ⚠️ Validación técnica PENDIENTE
- ⚠️ Testing manual PENDIENTE

**Próximo Paso**:
Ejecutar validación técnica con herramientas MCP siguiendo el STEP 1-5 de la sección 8.

**Comando para Usuario**:
```bash
# Una vez que tengas acceso a las herramientas MCP, ejecuta:
# 1. Validar workflows con mcp__n8n__n8n_validate_workflow
# 2. Verificar detalles con mcp__n8n__n8n_get_workflow
# 3. Completar checklist de activación
# 4. Generar reporte final de QA
```

---

## 10. ANEXO: CAMPOS DE INTEGRACIÓN - REFERENCIA RÁPIDA

### Tabla de Compatibilidad

| Campo | SUB-A (Escribe) | SUB-D (Lee) | SUB-D (Actualiza) | Tipo | Validación |
|-------|-----------------|-------------|-------------------|------|------------|
| nurturing_status | "active" | Filter = "active" | "completed" cuando emails_sent=12 | String | Required, Enum |
| lead_captured_at | ISO timestamp | Calcula días | No actualiza | Timestamp | Required, Valid ISO |
| emails_sent | 0 | Filter < 12 | Incrementa +1 | Number | Required, 0-12 |
| last_email_sent | null | Calcula cooldown | Timestamp actual | Timestamp | Nullable |
| last_email_position | 0 | Determina próximo email | Posición enviada (1-12) | Number | Required, 0-12 |

### Ejemplo de Documento en Firestore

**Después de SUB-A**:
```json
{
  "id": "lead_12345",
  "email": "test@techstartup.co",
  "nombre": "Juan Test",
  "empresa": "Test Corp",
  "interes": "Marcas",
  "calculated_score": 85,
  "category": "HOT",
  "is_spam": false,
  "nurturing_status": "active",
  "lead_captured_at": "2026-01-11T10:00:00.000Z",
  "emails_sent": 0,
  "last_email_sent": null,
  "last_email_position": 0,
  "created_at": "2026-01-11T10:00:00.000Z"
}
```

**Después de SUB-D (primer email)**:
```json
{
  "id": "lead_12345",
  "email": "test@techstartup.co",
  "nombre": "Juan Test",
  "empresa": "Test Corp",
  "interes": "Marcas",
  "calculated_score": 85,
  "category": "HOT",
  "is_spam": false,
  "nurturing_status": "active",
  "lead_captured_at": "2026-01-11T10:00:00.000Z",
  "emails_sent": 1,
  "last_email_sent": "2026-01-11T16:00:00.000Z",
  "last_email_position": 1,
  "created_at": "2026-01-11T10:00:00.000Z"
}
```

**Después de SUB-D (email final - posición 12)**:
```json
{
  "id": "lead_12345",
  "email": "test@techstartup.co",
  "nombre": "Juan Test",
  "empresa": "Test Corp",
  "interes": "Marcas",
  "calculated_score": 85,
  "category": "HOT",
  "is_spam": false,
  "nurturing_status": "completed",
  "lead_captured_at": "2026-01-11T10:00:00.000Z",
  "emails_sent": 12,
  "last_email_sent": "2026-04-11T18:00:00.000Z",
  "last_email_position": 12,
  "created_at": "2026-01-11T10:00:00.000Z"
}
```

---

**Documento generado por**: QA Specialist Agent
**Estado**: ⚠️ Validación Técnica Pendiente
**Requiere**: Acceso a herramientas MCP n8n para completar validación
**Próxima Acción**: Ejecutar STEP 1-5 de Sección 8
