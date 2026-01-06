# 🎯 RESUMEN: Callbacks SUB-A - Pruebas Completadas

**Fecha**: 6 de Enero, 2026
**Responsable**: Marketing Dev (n8n)
**Estado**: ✅ **CALLBACKS FUNCIONANDO - Probados con Pipedream**

---

## 📋 OBJETIVO

Documentar el estado actual de los **callbacks** implementados en SUB-A para
notificar al backend cuando un lead es procesado y scored por IA.

---

## ✅ RESUMEN EJECUTIVO

Los callbacks fueron implementados y **probados exitosamente** usando webhooks
de Pipedream (herramienta de testing). Los datos se envían correctamente y el
flujo funciona como se esperaba.

### ⚠️ IMPORTANTE

**Las URLs actuales son de TESTING**. Se usaron webhooks de Pipedream que NO
pertenecen a la plataforma de Carrillo Abogados. El propósito fue **validar la
funcionalidad** antes de integrar con el backend real.

---

## 🔄 ARQUITECTURA IMPLEMENTADA

### Flujo de Callbacks

```
ORQUESTADOR
    ↓ Invoca SUB-A
    ↓
SUB-A
    ├─→ [Flujo Principal] (Firestore + Email)
    │       ↓
    │   [Procesa lead completo]
    │       ↓
    │   [FINAL - Retorna al Orquestador]
    │
    └─→ [Flujo Callbacks] (Paralelo)
            ↓
        [1. Validar y Clasificar]
            ├─→ [7. Callback Lead Scored] ✅
            │       POST a webhook
            │       SIEMPRE se ejecuta
            │       ↓
            └─→ [8. Es Lead HOT?] ✅
                    IF categoria === "HOT"
                    ↓ [TRUE]
                [9. Callback Hot Lead Alert] ✅
                    POST a webhook
                    SOLO para leads HOT
```

---

## 📊 CALLBACKS CONFIGURADOS

### Callback 1: Lead Scored

**Propósito**: Notificar que el lead fue procesado y tiene un score asignado.

**URL Testing**: `https://eoc4ipe73sd9y75.m.pipedream.net` (Pipedream)
**URL Producción**: `/n8n-integration-service/webhook/lead-scored` (Pendiente)

**Cuándo se ejecuta**: SIEMPRE (todos los leads, sin importar el score)

**Payload Enviado**:

```json
{
  "lead_id": "2026-01-06T06:37:23.503Z-carolina-at-techventures.io",
  "score": 95,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "is_spam": false,
    "analysis_reason": "Lead de alto valor proveniente...",
    "calculated_score": 95,
    "category": "HOT"
  },
  "processed_at": "2026-01-06T06:37:23.503Z"
}
```

**Acción esperada del backend**:

1. Recibir callback en `POST /webhook/lead-scored`
2. Actualizar lead en PostgreSQL con score y categoría
3. Cambiar estado del lead a `QUALIFIED`

---

### Callback 2: Hot Lead Alert

**Propósito**: Alerta inmediata de que se detectó un lead HOT.

**URL Testing**: `https://eoyvly7sjxiim05.m.pipedream.net` (Pipedream)
**URL Producción**: `/n8n-integration-service/webhook/lead-hot` (Pendiente)

**Cuándo se ejecuta**: SOLO si `categoria === "HOT"` (score ≥ 70)

**Payload Enviado**:

```json
{
  "lead_id": "2026-01-06T06:37:23.503Z-carolina-at-techventures.io",
  "score": 95,
  "categoria": "HOT",
  "notified_at": "2026-01-06T06:37:40.000Z",
  "email_sent_to": "marketing@carrilloabgd.com"
}
```

**Acción esperada del backend**:

1. Recibir callback en `POST /webhook/lead-hot`
2. Registrar alerta en logs
3. (Opcional) Crear tarea de seguimiento en case-service
4. (Opcional) Notificación adicional (Slack, SMS, etc.)

---

## 🧪 PRUEBAS REALIZADAS

### Test 1: Lead HOT (Carolina Gomez)

**Entrada**:

- Nombre: Carolina Gomez
- Empresa: TechVentures Capital
- Cargo: Managing Partner
- Servicio: Marcas
- Mensaje: "Fondo de inversión con portfolio de 20 startups..."

**Resultado**:

- ✅ Gemini Score: 95 (HOT)
- ✅ Callback 1 ejecutado en 404ms
- ✅ Callback 2 ejecutado en 392ms
- ✅ Datos recibidos correctamente en Pipedream
- ✅ Duración total: ~33 segundos

---

### Test 2: Lead HOT (Pedro Ramirez)

**Entrada**:

- Nombre: Pedro Ramirez
- Empresa: StartupTech Solutions
- Cargo: CTO
- Servicio: Patentes
- Mensaje: "Startup fintech con nueva tecnología blockchain..."

**Resultado**:

- ✅ Gemini Score: 95 (HOT)
- ✅ Ambos callbacks ejecutados correctamente
- ✅ Duración total: ~28 segundos
- ✅ Sin errores

---

## 📊 COMPARACIÓN DE CALLBACKS

| Aspecto | Callback 1 (Scored) | Callback 2 (HOT) |
|---------|---------------------|------------------|
| **Frecuencia** | Todos los leads | Solo HOT (score ≥70) |
| **Datos** | lead_id, score, categoria, ai_analysis | lead_id, score,
categoria, notified_at |
| **Propósito** | Actualizar BD | Alerta urgente |
| **Prioridad** | Normal | Alta |

---

## 📌 INFORMACIÓN PARA BACKEND DEV

### DTOs Esperados

#### LeadScoredDTO.java

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadScoredDTO {
    private String lead_id;  // UUID del lead en PostgreSQL
    private Integer score;   // 0-100
    private String categoria; // "HOT", "WARM", "COLD"
    private Map<String, Object> ai_analysis; // Análisis completo de Gemini
    private String processed_at; // ISO 8601 timestamp
}
```

#### HotLeadDTO.java

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotLeadDTO {
    private String lead_id;
    private Integer score;
    private String categoria;
    private String notified_at; // ISO 8601 timestamp
    private String email_sent_to; // "marketing@carrilloabgd.com"
}
```

---

### Endpoints a Implementar

#### 1. POST /webhook/lead-scored

```java
@RestController
@RequestMapping("/webhook")
public class N8nWebhookController {

    @PostMapping("/lead-scored")
    public ResponseEntity<Void> handleLeadScored(
        @RequestBody LeadScoredDTO dto
    ) {
        log.info("Received lead scored callback: {}", dto.getLead_id());

        // 1. Actualizar lead en client-service
        clientServiceClient.updateLeadScore(
            dto.getLead_id(),
            dto.getScore(),
            dto.getCategoria()
        );

        return ResponseEntity.ok().build();
    }
}
```

#### 2. POST /webhook/lead-hot

```java
@PostMapping("/lead-hot")
public ResponseEntity<Void> handleHotLead(@RequestBody HotLeadDTO dto) {
    log.info("Received HOT lead alert: {}", dto.getLead_id());

    // 1. Log alerta
    alertService.logHotLeadAlert(dto);

    // 2. Opcional: Crear tarea en case-service
    // caseService.createFollowUpTask(dto.getLead_id());

    return ResponseEntity.ok().build();
}
```

---

### Actualización en client-service

Necesitas un endpoint en **client-service** para actualizar el score:

```java
// client-service/controller/LeadController.java
@PatchMapping("/api/leads/{id}/score")
public ResponseEntity<Lead> updateLeadScore(
    @PathVariable UUID id,
    @RequestBody UpdateScoreDTO dto
) {
    Lead updated = leadService.updateScore(id, dto.getScore(),
dto.getCategoria());
    return ResponseEntity.ok(updated);
}
```

---

## 🔄 PRÓXIMOS PASOS

### Para Backend Dev

1. ✅ **Revisar DTOs**: Confirmar estructura de datos
2. ⏳ **Implementar endpoints** en `n8n-integration-service`:
   - POST `/webhook/lead-scored`
   - POST `/webhook/lead-hot`
3. ⏳ **Implementar endpoint** en `client-service`:
   - PATCH `/api/leads/{id}/score`
4. ⏳ **Testing local**: Probar con curl/Postman
5. ⏳ **Desplegar a dev**: Exponer URLs públicas
6. ⏳ **Comunicar URLs**: Enviar URLs a Marketing Dev

---

### Para Marketing Dev (Después del Backend)

1. ⏳ **Configurar variable** `BACKEND_URL` en n8n
2. ⏳ **Actualizar URLs** en nodos 7 y 9 de SUB-A
3. ⏳ **Testing E2E**: Probar flujo completo con backend real
4. ⏳ **Validar datos**: Confirmar que PostgreSQL se actualiza
5. ⏳ **Activar Orquestador**: Ponerlo en producción

---

## 📄 REFERENCIAS

- [STATUS.md](workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/02-spokes/sub-a-lead-intake/STATUS.md)
  - Estado detallado de SUB-A
- [WEB_INTEGRATION.md](WEB_INTEGRATION.md) - Especificación completa
- [BACKEND_DEV_TASKS.md](BACKEND_DEV_TASKS.md) - Tareas pendientes backend

---

## ✅ CONCLUSIÓN

Los callbacks están **implementados y funcionando correctamente**. Se probaron
con webhooks externos (Pipedream) para validar la funcionalidad.

El siguiente paso crítico es que **Backend Dev implemente los endpoints** en
`n8n-integration-service` para recibir estos callbacks y actualizar PostgreSQL.

Una vez implementados los endpoints, Marketing Dev actualizará las URLs en n8n
y se realizará testing E2E completo.

---

**Documento creado**: 6 de Enero, 2026
**Autor**: Marketing Dev
**Validado con**: Pipedream webhooks (testing)
