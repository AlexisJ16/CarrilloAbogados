# ✅ MW#1 Lead Lifecycle - ACTIVO Y FUNCIONANDO

**Estado**: ✅ **PRODUCCIÓN READY**  
**Última verificación**: 2026-01-04 00:27 COT  
**Webhook URL**: `https://carrilloabgd.app.n8n.cloud/webhook/lead-events`

---

## 📋 Estado de Workflows

| Workflow | ID | Estado | Nodos | Validación |
|----------|-----|--------|-------|------------|
| **Orquestador** | `bva1Kc1USbbITEAw` | ✅ **ACTIVO** | 8 | ✅ 0 errores |
| **SUB-A Lead Intake** | `RHj1TAqBazxNFriJ` | ✅ Listo | 13 | ✅ 0 errores |

---

## ✅ Correcciones Completadas (4 Enero 2026)

### 1. Error Webhook `onError` - CORREGIDO ✅
- **Problema**: Nodo webhook sin `onError: continueRegularOutput`
- **Solución**: Agregado via n8n MCP
- **Resultado**: Workflow puede activarse correctamente

### 2. Error Handlers Agregados - COMPLETADO ✅

**Orquestador:**
- `Error Handler` (Error Trigger)
- `Preparar Datos Error` (Set node)
- `Notificar Error Email` (Gmail → ingenieria@carrilloabgd.com)

**SUB-A:**
- `Error Handler` (Error Trigger)
- `Preparar Error` (Set node)
- `Notificar Error` (Gmail → ingenieria@carrilloabgd.com)

### 3. Test E2E Exitoso ✅
```json
{
  "success": true,
  "message": "Lead procesado exitosamente por SUB-A (AI Powered)",
  "score": 90,
  "categoria": "HOT",
  "ai_analysis": {
    "normalized_interest": "Marcas",
    "analysis_reason": "Lead de alta calidad...",
    "calculated_score": 90
  }
}
```

---

## 🔧 Backend Platform

| Componente | Estado | Endpoint |
|------------|--------|----------|
| client-service Lead API | ✅ Ready | `POST /api/leads` |
| n8n-integration-service | ✅ Ready | Webhooks configurados |
| Frontend /contacto | ✅ Ready | Envía a API Gateway |
| NATS Messaging | ✅ Ready | Operativo |

---

## 🟡 P2 - Mejoras Opcionales (No bloquean producción)

### TypeVersions Obsoletas (Warnings)

| Workflow | Nodo | Actual | Recomendada |
|----------|------|--------|-------------|
| Orquestador | Notificar Error Email | 2.1 | 2.2 |
| SUB-A | If Node | 2 | 2.3 |
| SUB-A | Gmail (x2) | 2.1 | 2.2 |
| SUB-A | Notificar Error | 2.1 | 2.2 |

**Acción**: En n8n UI, click "Update" en nodos con banner amarillo.

---

## 📊 Flujo de Datos

```
Frontend /contacto
       │
       ▼
POST /api/client-service/api/leads
       │
       ▼ (futuro: NATS → n8n-integration-service)
       │
POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events
       │
       ▼
┌──────────────────────────────────────┐
│ ORQUESTADOR (bva1Kc1USbbITEAw)       │
│ ├─ Webhook Principal Lead Events    │
│ ├─ Identify (clasificar evento)     │
│ ├─ SubA (Execute Workflow)          │
│ ├─ Consolidate                      │
│ └─ Respond (respuesta webhook)      │
│                                      │
│ Error Handler:                       │
│ ├─ Error Handler → Preparar → Gmail │
└──────────────────────────────────────┘
       │
       ▼
┌──────────────────────────────────────┐
│ SUB-A (RHj1TAqBazxNFriJ)             │
│ ├─ When Executed by Another Workflow│
│ ├─ 0. Mapear Input                  │
│ ├─ 0.5. Analizar Lead (Gemini AI)   │
│ ├─ 1. Validar y Clasificar          │
│ ├─ 2. Guardar en Firestore          │
│ ├─ 3. Es Lead HOT? (If)             │
│ │   ├─ HOT → 4. Notificar Equipo    │
│ │   └─ WARM/COLD → continúa         │
│ ├─ 5. Generar Respuesta (Gemini)    │
│ ├─ 6. Enviar Respuesta Lead         │
│ └─ FINAL. Resultado                 │
│                                      │
│ Error Handler:                       │
│ ├─ Error Handler → Preparar → Gmail │
└──────────────────────────────────────┘
       │
       ▼
Lead procesado: Score calculado, guardado en Firestore,
emails enviados (notificación equipo + respuesta lead)
```

---

## 🧪 Comando de Prueba

```bash
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "new_lead",
    "nombre": "Test Lead",
    "email": "test@example.com",
    "telefono": "+573001234567",
    "empresa": "Test Company",
    "cargo": "CEO",
    "servicio_interes": "Registro de Marca",
    "mensaje": "Necesito proteger mi marca urgentemente.",
    "utm_source": "test",
    "utm_campaign": "manual"
  }'
```

---

*Documento actualizado: 4 Enero 2026 - Sistema en producción*
