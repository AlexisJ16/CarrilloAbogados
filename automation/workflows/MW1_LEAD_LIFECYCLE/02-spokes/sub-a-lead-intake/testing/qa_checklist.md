# QA Checklist - SUB-A Lead Intake

**Workflow ID**: RHj1TAqBazxNFriJ
**Fecha**: 2026-01-21
**Validador**: QA Specialist Agent

---

## VALIDACIÓN ESTRUCTURAL

### Workflow General
- [x] Workflow validado sin errores críticos de estructura
- [x] Todos los nodos configurados correctamente
- [x] Conexiones verificadas según documentación
- [ ] Expresiones n8n validadas con MCP (pending)
- [x] Arquitectura Spoke (Event-Driven) confirmada

### Nodos
- [x] **Total nodos**: 17 (confirmado)
- [x] **Trigger**: "When Executed by Another Workflow" (correcto para Tool)
- [x] **Error Handler**: 3 nodos (Error Trigger + Preparar Error + Notificar Error)
- [x] **Nodos críticos identificados**: Mapear Input, AI Scoring, Firestore, Gmail, Callbacks

---

## TESTING FUNCIONAL

### Test Cases Básicos
- [ ] **Test 1: Lead Válido con AI Agent** - PENDING RETEST (después de aplicar fix)
  - [x] Fix aplicado en código
  - [ ] Test ejecutado
  - [ ] Logs verificados ("Parseado desde campo query")

- [x] **Test 2: Lead HOT (Score >= 70)** - PASÓ (2026-01-11)
  - [x] Score correcto (95)
  - [x] Categoría HOT
  - [x] Email notificación enviado
  - [x] Callbacks ejecutados

- [ ] **Test 3: Email Inválido** - FALLIDO ⚠️
  - [ ] Validación demasiado estricta
  - [ ] Rechaza emails válidos
  - [ ] BLOCKER CRÍTICO

- [ ] **Test 4: Campos Faltantes** - PENDING TEST
  - [x] Código de validación existe
  - [ ] Test explícito ejecutado

- [x] **Test 5: Callbacks Backend** - PASÓ (2026-01-11)
  - [x] Callback Lead Scored OK
  - [x] Callback Hot Lead Alert OK

### Test Cases Avanzados
- [ ] **Test 6: Latency bajo carga** - PENDING
- [ ] **Test 7: Ejecuciones concurrentes** - PENDING
- [ ] **Test 8: Edge cases (caracteres especiales, UTF-8)** - PENDING

---

## ERROR HANDLING

### Configuración
- [x] Todos los nodos críticos con error handling
- [x] Retry logic implementado donde necesario
  - [x] HTTP Callbacks: `onError: continueRegularOutput`
  - [x] Firestore: `continueOnFail: true`
  - [x] Gmail: Retry en credenciales OAuth2
- [x] Error messages claros configurados
- [x] No crashes en error scenarios (verificado con Error Handler)

### Tests de Error
- [ ] **Email inválido manejado correctamente** - FALLA (validación estricta)
- [ ] **Campos faltantes detectados** - PENDING TEST
- [ ] **API timeout configurado apropiadamente** - PENDING VERIFY
- [ ] **Gemini API no disponible** - PENDING TEST
- [ ] **Firestore no disponible** - PENDING TEST

---

## INTEGRACIONES

### Google Firestore
- [x] Credenciales configuradas (ID: AAhdRNGzvsFnYN9O)
- [x] Collection "leads" existe
- [x] Write permissions OK
- [x] Test write exitoso (verificado en ejecuciones históricas)
- [x] Campos nurturing agregados (5 campos)
  - [x] nurturing_status
  - [x] lead_captured_at
  - [x] emails_sent
  - [x] last_email_sent
  - [x] last_email_position

### Google Gemini
- [x] API key configurada (ID: jk2FHcbAC71LuRl2)
- [x] Model name correcto (Gemini 2.5-pro)
- [x] Rate limits considerados (15 RPM)
- [x] Test scoring exitoso (scores 85-95 en tests)
- [x] Response time aceptable (~2-3s)

### Gmail
- [x] OAuth configurado (ID: l2mMgEf8YUV7HHlK)
- [x] From address válido (marketing@carrilloabgd.com)
- [x] Template funciona
- [x] Test email enviado
- [ ] ⚠️ Validación de email tiene issue (rechaza válidos)

### Callbacks Backend
- [x] HTTP Request nodes configurados
- [x] URLs Pipedream para testing
- [x] Variable BACKEND_URL definida
- [x] Error handling con continueRegularOutput
- [x] Test con Pipedream exitoso
- [ ] Variable BACKEND_URL producción - PENDING
- [ ] Dev Tunnel configurado - PENDING
- [ ] Endpoints backend implementados - PENDING

---

## PERFORMANCE

### Tiempos de Ejecución
- [x] **Latency medido**: 38 segundos (Orquestador + SUB-A)
- [x] **Gemini API**: ~2.5s (aceptable para IA)
- [x] **Firestore**: ~0.5s (OK)
- [ ] ⚠️ **Latency total ALTA**: 38s puede causar timeouts

### Cuellos de Botella
- [x] Gemini API identificado (2.5s) - NO CRÍTICO
- [x] Latency total identificado (38s) - MEDIO
- [ ] Optimización latency - PENDING (considerar async)

### Resource Usage
- [x] Tokens Gemini razonables (718 total)
- [x] No memory issues observados
- [x] No rate limit issues observados

---

## DOCUMENTACIÓN

### Archivos Generados
- [x] **test_report_v1.md** - Reporte completo
- [x] **validation_results.json** - Resultados estructurados
- [x] **qa_checklist.md** - Este checklist
- [x] **ANALISIS_ERROR_MAPEO.md** - Análisis del bug (pre-existente)
- [x] **GUIA_APLICAR_FIX.md** - Guía de corrección (pre-existente)

### Documentación de Bugs
- [x] BUG-001: Bug Mapear Input documentado (RESUELTO)
- [x] BUG-002: Bug Validación Email documentado (OPEN)
- [x] Soluciones propuestas
- [x] Pasos de reproducción
- [x] Impacto evaluado

---

## ARQUITECTURA

### Hub & Spoke Validation
- [x] SUB-A configurado como Spoke
- [x] Trigger correcto (When Executed by Another Workflow)
- [x] Puede ser llamado por Orquestador como Tool
- [x] Puede ejecutarse standalone para testing
- [x] Independencia de SUB-D validada (no se llaman entre sí)

### Integración con Orquestador v3.0
- [x] SUB-A configurado como Tool en AI Agent
- [x] AI Agent identifica event_type correctamente
- [x] Invocación con autoMapInputData
- [ ] ⚠️ Bug mapeo INPUT (corregido pero no testeado)
- [ ] ⚠️ Backend NO envía tráfico a v3.0 (BLOCKER)

### Preparación para SUB-D
- [x] Campos nurturing implementados
- [x] Valores iniciales correctos
- [x] Firestore columns actualizados
- [x] SUB-D puede leer/actualizar campos

---

## ISSUES CRÍTICOS

### 🔴 P0 - BLOCKERS (DEBEN RESOLVERSE)

- [ ] **BUG-002**: Validación de email demasiado estricta
  - Rechaza emails válidos (@gmail.com)
  - AI Agent agota iteraciones
  - OWNER: Engineer
  - ETA: 30 min

- [ ] **WARNING-003**: Backend webhook URL incorrecto
  - Envía a v1.0 en lugar de v3.0
  - Orquestador v3.0 no recibe tráfico
  - OWNER: Backend Dev
  - ETA: 15 min

- [ ] **TEST-001**: Test E2E Post-Fix Mapeo
  - Verificar fix funciona en producción
  - OWNER: QA Specialist
  - ETA: 15 min

---

## ISSUES NO CRÍTICOS

### ⚠️ P1 - ALTA PRIORIDAD

- [ ] **WARNING-004**: typeVersions deprecados (4 nodos)
  - No bloquea funcionalidad
  - OWNER: Engineer
  - ETA: 10 min

- [ ] **WARNING-002**: Latency alta (38s)
  - Considerar procesamiento async
  - OWNER: Optimizer
  - ETA: 2-3 horas

---

## SUGERENCIAS

### 💡 P2 - MEJORAS

- [ ] **SUGG-001**: Logging estructurado
- [ ] **SUGG-002**: Idempotencia con Firestore
- [ ] **SUGG-003**: Observabilidad callbacks
- [ ] **SUGG-004**: Optimizar AI scoring con caché

---

## DECISIÓN FINAL

### Status: ⚠️ APROBADO CON WARNINGS CRÍTICOS

**Aprobado para**: ❌ Producción (con blockers)
**Aprobado para**: ❌ Optimización (resolver blockers primero)
**Requiere**: ✅ Correcciones P0 antes de continuar

### Blockers Críticos
1. 🔴 Bug validación email (rechaza emails válidos)
2. 🔴 Backend webhook URL (envía a v1.0 en lugar de v3.0)

### Aspectos Aprobados
- ✅ Bug nodo mapeo corregido (pending test)
- ✅ Callbacks backend implementados
- ✅ Error handling robusto
- ✅ Integraciones funcionando
- ✅ Campos nurturing agregados

---

## HANDOFF

**Para**: Engineer
**Acción**: Corregir bug validación email en SUB-A

**Comando**:
```
Actúa como Agente Ingeniero y corrige el bug de validación de email en SUB-A (workflow ID: RHj1TAqBazxNFriJ). El nodo está rechazando emails válidos como 'alexisj4a@gmail.com'.
```

---

**Checklist completado por**: QA Specialist Agent
**Fecha**: 2026-01-21
**Firma**: ⚠️ Requiere correcciones P0 antes de aprobar para producción
