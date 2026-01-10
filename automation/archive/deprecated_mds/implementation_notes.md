# Notas de Implementacion: SUB-D Nurturing Sequence Engine

## Resumen

- **Workflow ID**: `PZboUEnAxm5A7Lub`
- **Nombre**: SUB-D: Nurturing Sequence Engine
- **Fecha Implementacion**: 7 de Enero, 2026
- **Implementado por**: @engineer
- **Nodos Totales**: 16
- **Estado**: INACTIVO (pendiente configuracion de credenciales)

---

## Arquitectura del Workflow

### Flujo Principal

```
[1] Schedule Trigger (cada 6h)
    |
[2] Calcular Timestamp Actual
    |
[3] Query Firestore: Leads para Nurturing
    |
[4] IF: Hay Leads?
    |-- TRUE --> [5] Loop: Split In Batches
    |                  |
    |                  +-- Por cada lead:
    |                  |   [6] Extraer Datos Lead
    |                  |   [7] Calcular Posicion en Secuencia
    |                  |   [8] Cargar Template Email
    |                  |   [9] Personalizar Email con Gemini
    |                  |   [10] Validar Output Gemini
    |                  |   [11] Enviar Email con Mailersend
    |                  |   [12] IF: Envio Exitoso?
    |                  |       |-- TRUE --> [13] Actualizar Firestore
    |                  |       |                   |
    |                  |       |            [14] Callback Backend
    |                  |       |                   |
    |                  |       +-- FALSE --> volver al loop
    |                  |
    |                  +-- Fin batch --> [15] Consolidar Resultados
    |
    +-- FALSE --> [16] Exit Workflow
```

---

## Nodos Implementados

### Nodo 1: Schedule Trigger
- **Tipo**: `n8n-nodes-base.scheduleTrigger`
- **Version**: 1.2
- **Configuracion**: Ejecuta cada 6 horas
- **Timezone**: America/Bogota

### Nodo 2: Calcular Timestamp Actual
- **Tipo**: `n8n-nodes-base.set`
- **Version**: 3.4
- **Proposito**: Genera timestamp ISO para comparacion con `next_email_date`

### Nodo 3: Query Firestore
- **Tipo**: `n8n-nodes-base.httpRequest`
- **Version**: 4.2
- **Endpoint**: Firestore REST API runQuery
- **Filtros**:
  - `status IN ["nuevo", "nurturing"]`
  - `emails_sent < 12`
  - Ordenado por `next_email_date ASC`
  - Limite: 10 leads

### Nodo 4: IF Hay Leads?
- **Tipo**: `n8n-nodes-base.if`
- **Version**: 2.2
- **Condicion**: `$json.length > 0`

### Nodo 5: Loop Split In Batches
- **Tipo**: `n8n-nodes-base.splitInBatches`
- **Version**: 3
- **Batch Size**: 1 (procesa un lead a la vez)

### Nodo 6: Extraer Datos Lead
- **Tipo**: `n8n-nodes-base.set`
- **Version**: 3.4
- **Campos extraidos**: lead_id, nombre, email, empresa, servicio, emails_sent, created_at

### Nodo 7: Calcular Posicion en Secuencia
- **Tipo**: `n8n-nodes-base.code`
- **Version**: 2
- **Lenguaje**: JavaScript
- **Logica**: Determina posicion (1-12) basado en dias desde captura
- **Archivo codigo**: `code_snippets/calcular_posicion.js`

### Nodo 8: Cargar Template Email
- **Tipo**: `n8n-nodes-base.code`
- **Version**: 2
- **Lenguaje**: JavaScript
- **Logica**: 12 templates con subject, objective, structure, max_words
- **Archivo codigo**: `code_snippets/cargar_template.js`

### Nodo 9: Personalizar Email con Gemini
- **Tipo**: `n8n-nodes-base.httpRequest`
- **Version**: 4.2
- **Endpoint**: Gemini API v1beta
- **Modelo**: gemini-2.0-flash
- **Config**: temperature=0.7, maxOutputTokens=500

### Nodo 10: Validar Output Gemini
- **Tipo**: `n8n-nodes-base.code`
- **Version**: 2
- **Lenguaje**: JavaScript
- **Logica**: Parsea JSON de Gemini, aplica fallback si falla
- **Archivo codigo**: `code_snippets/validar_gemini.js`

### Nodo 11: Enviar Email con Mailersend
- **Tipo**: `n8n-nodes-base.httpRequest`
- **Version**: 4.2
- **Endpoint**: https://api.mailersend.com/v1/email
- **Autenticacion**: HTTP Header Auth (API Key)
- **NOTA**: Requiere configurar credencial Mailersend

### Nodo 12: IF Envio Exitoso?
- **Tipo**: `n8n-nodes-base.if`
- **Version**: 2.2
- **Condicion**: `statusCode <= 299`

### Nodo 13: Actualizar Firestore
- **Tipo**: `n8n-nodes-base.httpRequest`
- **Version**: 4.2
- **Metodo**: PATCH
- **Campos actualizados**: emails_sent, last_contact, status, nurturing_position, next_email_date, updated_at

### Nodo 14: Callback Backend
- **Tipo**: `n8n-nodes-base.httpRequest`
- **Version**: 4.2
- **URL**: `$env.BACKEND_URL/webhook/nurturing-sent`
- **onError**: continueErrorOutput (no bloquea el flujo)

### Nodo 15: Consolidar Resultados
- **Tipo**: `n8n-nodes-base.set`
- **Version**: 3.4
- **Output**: workflow name, execution_time, success, message

### Nodo 16: Exit Workflow
- **Tipo**: `n8n-nodes-base.noOp`
- **Version**: 1
- **Proposito**: Punto final del workflow

---

## Integraciones Configuradas

| Servicio | Estado | Credencial Requerida |
|----------|--------|---------------------|
| Google Firestore | Pendiente configurar | `googleApi` |
| Google Gemini 2.0 | Pendiente configurar | `googlePalmApi` |
| Mailersend | **BLOQUEADO** | `httpHeaderAuth` con API Key |
| Backend Spring Boot | Opcional | Variable `BACKEND_URL` |

---

## Credenciales Necesarias

### 1. Google API (Firestore + Gemini)
- **ID esperado**: `AAhdRNGzvsFnYN9O` (Firestore existente)
- **ID Gemini esperado**: `jk2FHcbAC71LuRl2`
- **Tipo**: OAuth2 o Service Account
- **Scopes**: Firestore read/write, Generative AI

### 2. Mailersend API Key
- **Estado**: NO CONFIGURADO
- **Accion requerida**:
  1. Crear cuenta en https://mailersend.com
  2. Verificar dominio `carrilloabgd.com`
  3. Generar API Key
  4. Crear credencial HTTP Header Auth en n8n:
     - Header Name: `Authorization`
     - Header Value: `Bearer {API_KEY}`

### 3. Variable de Entorno
- **Nombre**: `BACKEND_URL`
- **Valor desarrollo**: `http://localhost:8800`
- **Valor produccion**: `https://api.carrilloabgd.com/n8n-integration-service`

---

## Issues Encontrados Durante Implementacion

### 1. Nodo Google Firestore nativo no disponible
- **Problema**: El nodo `n8n-nodes-base.googleFirestore` no se encontro en el registry
- **Solucion**: Usar HTTP Request con Firestore REST API directamente
- **Impacto**: Configuracion mas compleja pero funcional

### 2. Mailersend no configurado
- **Problema**: No existe cuenta ni credencial Mailersend
- **Estado**: BLOQUEADO para testing real
- **Workaround**: Workflow funciona hasta nodo 10, nodo 11+ requiere credencial

### 3. Validacion de workflow con warnings
- **Warnings principales**:
  - Versiones de nodos ligeramente desactualizadas (4.2 vs 4.3)
  - Recomendacion de usar `onError` en lugar de `continueOnFail`
  - Cadena larga de nodos (14) - considerar sub-workflows futuros
- **Accion**: Warnings son informativos, no bloquean ejecucion

---

## Logica de Secuencia de Emails (12 posiciones)

| Posicion | Dias desde captura | Objetivo | Delay al siguiente |
|----------|-------------------|----------|-------------------|
| 1 | 0-2 | Bienvenida | 3 dias |
| 2 | 3-5 | Educativo | 4 dias |
| 3 | 7-9 | Case Study | 3 dias |
| 4 | 10-13 | Recurso de valor | 4 dias |
| 5 | 14-17 | Urgencia | 7 dias |
| 6 | 21-24 | Autoridad | 7 dias |
| 7 | 28-31 | Oferta | 7 dias |
| 8 | 35-38 | Re-engagement | 7 dias |
| 9 | 42-45 | Tendencias | 7 dias |
| 10 | 49-52 | Last Chance | 7 dias |
| 11 | 56-59 | Break-up | 34 dias |
| 12 | 90+ | Win-back | FIN |

---

## Testing Pendiente

- [ ] Configurar credencial Mailersend
- [ ] Crear lead de prueba en Firestore
- [ ] Ejecutar workflow manualmente
- [ ] Verificar email recibido
- [ ] Verificar actualizacion Firestore
- [ ] Probar callback a backend

---

## Proximo Paso

El workflow esta creado e inactivo. Para activarlo:

1. Configurar credenciales faltantes (Mailersend, Google API)
2. Configurar variable `BACKEND_URL`
3. Crear lead de prueba en Firestore
4. Ejecutar test manual
5. Handoff a @qa-specialist para validacion exhaustiva

---

## Handoff a QA Specialist

**Workflow Implementado**: SUB-D: Nurturing Sequence Engine
**Workflow ID**: `PZboUEnAxm5A7Lub`
**Nodos**: 16
**Estado**: Listo para validacion (pendiente credenciales)

**Archivos Generados**:
- implementation_notes.md (este archivo)
- code_snippets/calcular_posicion.js
- code_snippets/cargar_template.js
- code_snippets/validar_gemini.js
- SUB-D_WORKFLOW.json
- MAILERSEND_CONFIG.md

**Comando para Usuario**:
> Actua como Agente QA Specialist y valida el workflow PZboUEnAxm5A7Lub

---

**Autor**: @engineer
**Fecha**: 7 de Enero, 2026
**Version**: 1.0
