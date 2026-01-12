# Validation Checklist - SUB-D: Nurturing Sequence Engine

**Workflow ID**: PZboUEnAxm5A7Lub
**Fecha**: 11 de Enero, 2026
**Versión**: v1.0

---

## PRE-PRODUCTION CHECKLIST

### 1. CONFIGURACIÓN DE CREDENCIALES

- [x] **Google Firestore** (AAhdRNGzvsFnYN9O)
  - [x] Proyecto: carrillo-marketing-core
  - [x] Collection "leads" existe
  - [ ] Índice compuesto creado: (nurturing_status ASC, last_email_sent ASC)
  - [ ] Permisos de lectura/escritura verificados

- [x] **Gmail OAuth2** (l2mMgEf8YUV7HHlK)
  - [x] From address: marketing@carrilloabgd.com
  - [x] OAuth2 flow completado
  - [ ] Test email enviado exitosamente
  - [ ] Rate limits verificados (500/día)

- [x] **Google Gemini** (jk2FHcbAC71LuRl2)
  - [x] Model: Gemini 2.5 Pro
  - [ ] Test de personalización ejecutado
  - [ ] Rate limits verificados (60 req/min)

---

### 2. NODOS CRÍTICOS

#### Schedule Trigger
- [x] Frecuencia: Cada 6 horas
- [ ] Timezone configurado: America/Bogota
- [ ] Estado inicial: INACTIVO (para testing)

#### Query Firestore
- [ ] **CRÍTICO**: FIX WARNING-001 aplicado
  - [ ] Filtro cambiado a: `nurturing_status IN ['active', 'paused']`
  - [ ] Query testeado manualmente en Firestore Console
  - [ ] Retorna leads esperados

#### Code: Calculate Email Position
- [ ] Lógica de timing verificada
- [ ] Variables de salida correctas:
  - [ ] `should_send` (boolean)
  - [ ] `next_email_position` (number)
  - [ ] `days_since_last` (number)
- [ ] Manejo de nulls en `last_email_sent`
- [ ] Secuencia de 12 días correcta: [0, 3, 7, 10, 14, 21, 28, 35, 42, 49, 56, 90]

#### IF: Should Send?
- [x] Condición: `{{$json.should_send}} === true`
- [x] Rutas configuradas: true → Get Template, false → No Action

#### Get Email Template
- [ ] Source: Database/File/Variable?
- [ ] 12 templates creados:
  - [ ] Template 1: Bienvenida (Día 0)
  - [ ] Template 2: Educativo (Día 3)
  - [ ] Template 3: Case Study (Día 7)
  - [ ] Template 4: Checklist (Día 10)
  - [ ] Template 5: Urgencia (Día 14)
  - [ ] Template 6: Autoridad (Día 21)
  - [ ] Template 7: Oferta (Día 28)
  - [ ] Template 8: Re-engagement (Día 35)
  - [ ] Template 9: Tendencias (Día 42)
  - [ ] Template 10: Last chance (Día 49)
  - [ ] Template 11: Break-up (Día 56)
  - [ ] Template 12: Win-back (Día 90)

#### AI Personalization (Gemini)
- [ ] System Prompt configurado
- [ ] Variables de input mapeadas:
  - [ ] `{{$json.nombre}}`
  - [ ] `{{$json.empresa}}`
  - [ ] `{{$json.servicio}}`
  - [ ] `{{$json.category}}`
  - [ ] `{{$json.email_template}}`
- [ ] Output esperado: `personalized_content`
- [ ] Max tokens: 1000
- [ ] Temperature: 0.7

#### Enviar Email con Gmail
- [x] Credencial: Gmail OAuth2 (l2mMgEf8YUV7HHlK)
- [ ] From: marketing@carrilloabgd.com
- [ ] To: `{{$json.email}}`
- [ ] Subject: `{{$json.email_subject}}`
- [ ] Body: `{{$json.personalized_content}}`
- [ ] Format: HTML
- [ ] Headers adicionales:
  - [ ] List-Unsubscribe: `<https://carrilloabgd.com/unsubscribe?id={{lead_id}}>`
  - [ ] X-Campaign: nurturing-sequence
  - [ ] X-Lead-ID: `{{$json.lead_id}}`

#### Actualizar Firestore
- [x] Collection: leads
- [x] UpdateKey: lead_id
- [ ] Campos mapeados correctamente:
  - [ ] `emails_sent`: `{{$json.emails_sent + 1}}`
  - [ ] `last_email_sent`: `{{$now}}`
  - [ ] `last_email_position`: `{{$json.next_email_position}}`
  - [ ] `nurturing_status`: Lógica condicional (completed si position = 12)

---

### 3. ERROR HANDLING

#### Error Trigger
- [ ] Configurado y activo
- [ ] Conectado a todos los nodos críticos

#### Error Notification
- [ ] Método: Gmail
- [ ] To: ingenieria@carrilloabgd.com
- [ ] Subject: "[ERROR] SUB-D Nurturing - {{$workflow.name}}"
- [ ] Body incluye:
  - [ ] Error message
  - [ ] Workflow execution ID
  - [ ] Lead ID afectado
  - [ ] Timestamp

#### Retry Logic
- [ ] Gmail Send: Retry 3 veces, wait 30s
- [ ] Firestore Update: Retry 2 veces, wait 10s
- [ ] Gemini API: Retry 2 veces, wait 5s

#### ContinueOnFail
- [ ] Query Firestore: continueOnFail = false (debe fallar si no hay conexión)
- [ ] AI Personalization: continueOnFail = true (usar template sin personalizar)
- [ ] Enviar Email: continueOnFail = true (loggear error, continuar con otros leads)
- [ ] Actualizar Firestore: continueOnFail = false (crítico para tracking)

---

### 4. LOGGING Y OBSERVABILIDAD

#### Success Logger
- [ ] Nodo configurado: Google Sheets / HTTP Request
- [ ] Datos loggeados:
  - [ ] execution_id
  - [ ] lead_id
  - [ ] email_sent
  - [ ] email_position
  - [ ] timestamp
  - [ ] personalization_time (ms)

#### Error Logger
- [ ] Nodo configurado en Error Trigger
- [ ] Datos loggeados:
  - [ ] execution_id
  - [ ] lead_id
  - [ ] error_message
  - [ ] node_name
  - [ ] timestamp

#### Skipped Logger
- [ ] Nodo en ruta false de "IF: Should Send?"
- [ ] Datos loggeados:
  - [ ] lead_id
  - [ ] reason: "timing_not_met"
  - [ ] days_since_last
  - [ ] required_days
  - [ ] timestamp

---

### 5. TESTING FUNCIONAL

#### Pre-Test Setup
- [ ] 8 leads de prueba creados en Firestore (ver test_data.json)
- [ ] Workflow en modo INACTIVO
- [ ] Monitoring tools listos (n8n execution logs, Firestore console)

#### Test Execution
- [ ] TC-001: Lead Nuevo - Primer Email (PRIORITY: HIGH)
  - [ ] Lead aparece en query
  - [ ] should_send = true
  - [ ] Email enviado exitosamente
  - [ ] Firestore actualizado: emails_sent=1

- [ ] TC-002: Lead en Secuencia - Email #5 (PRIORITY: HIGH)
  - [ ] Lead aparece en query
  - [ ] should_send = true
  - [ ] Email personalizado con urgencia
  - [ ] Firestore actualizado: emails_sent=5

- [ ] TC-003: Lead Completado - No Enviar (PRIORITY: HIGH)
  - [ ] Lead NO aparece en query
  - [ ] Workflow salta este lead

- [ ] TC-004: Lead Unsubscribed - No Enviar (PRIORITY: CRITICAL)
  - [ ] Lead NO aparece en query
  - [ ] Workflow salta este lead

- [ ] TC-005: Lead con Timing Incorrecto - Skip (PRIORITY: MEDIUM)
  - [ ] Lead aparece en query
  - [ ] should_send = false
  - [ ] Firestore NO se actualiza

- [ ] TC-006: Lead Paused - No Enviar (PRIORITY: MEDIUM)
  - [ ] Lead aparece en query
  - [ ] Lógica de pausa implementada

- [ ] TC-007: Lead HOT - Personalización Mejorada (PRIORITY: MEDIUM)
  - [ ] Gemini usa tone agresivo
  - [ ] Ejemplos corporativos incluidos

- [ ] TC-008: Lead Email #12 - Completar Secuencia (PRIORITY: HIGH)
  - [ ] Email final enviado
  - [ ] nurturing_status cambia a "completed"

#### Post-Test Validation
- [ ] Todos los emails recibidos en marketing@carrilloabgd.com
- [ ] Firestore actualizado correctamente para cada lead
- [ ] No hay errores en n8n execution logs
- [ ] Tiempos de ejecución aceptables (<5s por lead)

---

### 6. PERFORMANCE

#### Benchmarks
- [ ] Tiempo promedio por lead: < 4 segundos
- [ ] Gemini response time: < 3 segundos
- [ ] Gmail send time: < 1 segundo
- [ ] Firestore update time: < 0.5 segundos

#### Load Testing (300 leads)
- [ ] Tiempo total de ejecución: < 20 minutos
- [ ] Sin rate limit errors de Gmail (max 500/día)
- [ ] Sin rate limit errors de Gemini (max 60/min)
- [ ] Sin timeout errors de Firestore

#### Optimizaciones Aplicadas
- [ ] SUGG-001: Batch processing implementado (opcional)
- [ ] SUGG-002: Template caching implementado (opcional)
- [ ] SUGG-003: Gemini batching implementado (opcional)

---

### 7. COMPLIANCE Y SEGURIDAD

#### Email Best Practices
- [ ] List-Unsubscribe header presente
- [ ] From address verificado en Gmail
- [ ] SPF/DKIM configurados en dominio carrilloabgd.com
- [ ] Privacy policy link en footer de emails

#### Data Protection
- [ ] Leads en Firestore con permisos restrictivos
- [ ] No se loggean emails de usuarios en plaintext
- [ ] Credenciales en n8n Cloud environment variables

#### Unsubscribe Flow
- [ ] Endpoint `/unsubscribe?id={{lead_id}}` existe en backend
- [ ] Actualiza nurturing_status a "unsubscribed"
- [ ] Confirmación enviada al usuario

---

### 8. DOCUMENTACIÓN

#### Archivos Creados
- [x] `test_report_v1.md`
- [x] `test_data.json`
- [x] `validation_checklist.md` (este archivo)
- [ ] `implementation_notes.md` (Ingeniero)
- [ ] `workflow_spec.md` (Arquitecto)

#### Diagramas
- [ ] Flowchart del workflow
- [ ] Secuencia de 12 emails visualizada
- [ ] Error handling diagram

---

### 9. DEPLOYMENT TO PRODUCTION

#### Pre-Deployment
- [ ] WARNING-001 resuelto y verificado
- [ ] Todos los tests funcionales pasados (8/8)
- [ ] Logging configurado y funcionando
- [ ] Error notifications testeadas

#### Deployment Steps
1. [ ] Verificar workflow ID: PZboUEnAxm5A7Lub
2. [ ] Cambiar Schedule Trigger de INACTIVO a ACTIVO
3. [ ] Configurar schedule: cada 6 horas (00:00, 06:00, 12:00, 18:00 America/Bogota)
4. [ ] Monitorear primeras 3 ejecuciones automáticas
5. [ ] Validar logs en Google Sheets
6. [ ] Confirmar no hay errores en n8n executions

#### Post-Deployment Monitoring (Primera Semana)
- [ ] Día 1: Verificar ejecución cada 6 horas (4 checks)
- [ ] Día 2: Revisar emails enviados vs esperados
- [ ] Día 3: Validar rate limits no excedidos
- [ ] Día 7: Análisis de métricas:
  - [ ] Open rate de emails
  - [ ] Click rate (si hay tracking)
  - [ ] Unsubscribe rate
  - [ ] Error rate

---

### 10. ROLLBACK PLAN

#### Si hay errores críticos en producción:

1. **Desactivar Workflow** (Inmediato)
   ```bash
   # En n8n Cloud UI:
   # SUB-D Workflow → Settings → Status: INACTIVE
   ```

2. **Identificar Leads Afectados**
   ```javascript
   // Query Firestore para leads con last_email_sent en última hora
   db.collection('leads')
     .where('last_email_sent', '>', Date.now() - 3600000)
     .get()
   ```

3. **Revertir Firestore** (si necesario)
   ```javascript
   // Script de rollback para leads afectados
   affectedLeads.forEach(lead => {
     lead.ref.update({
       emails_sent: lead.data().emails_sent - 1,
       last_email_position: lead.data().last_email_position - 1,
       last_email_sent: lead.data().previous_email_sent // necesita campo adicional
     });
   });
   ```

4. **Notificar al Equipo**
   - Email a ingenieria@carrilloabgd.com con detalles
   - Slack/WhatsApp (si configurado)

5. **Fix y Re-Deploy**
   - Corregir error en workflow
   - Ejecutar validation checklist completo
   - Re-activar workflow

---

## SIGN-OFF

### Testing Completado Por:
- **QA Specialist**: _________________ Fecha: _______
- **Ingeniero**: _________________ Fecha: _______
- **Arquitecto**: _________________ Fecha: _______

### Aprobación para Producción:
- **Director de Marketing**: _________________ Fecha: _______

### Estado Final:
- [ ] ✅ APROBADO - Listo para producción
- [ ] ⚠️ APROBADO CON WARNINGS - Activar con monitoreo intensivo
- [ ] ❌ RECHAZADO - Requiere correcciones

---

**Última Actualización**: 11 de Enero, 2026
**Próxima Revisión**: Después de primera semana en producción
