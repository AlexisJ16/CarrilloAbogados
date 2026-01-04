# 🔄 PROMPT DE CONTINUACIÓN - Carrillo Abogados

**Fecha**: 3 de Enero, 2026  
**Rama activa**: `automation`  
**Última sesión**: Documentación n8n + Web Integration actualizada

---

## 📋 CONTEXTO RÁPIDO

Plataforma legal cloud-native con 8 microservicios Spring Boot. Integración pendiente con n8n Cloud para automatización de marketing (captura de leads).

### Estado Actual
- ✅ Documentación actualizada con datos reales de n8n MCP
- ✅ Rama `automation` sincronizada con `dev`
- ⏳ Correcciones n8n pendientes (P0-P1)
- ⏳ Integración web pendiente (P1.5-P1.9)

---

## 🔴 CHAT 1: Correcciones n8n Cloud

### Prompt sugerido:

```
Estoy en la rama `automation` del proyecto CarrilloAbogados.

Necesito corregir los workflows de n8n Cloud según el documento:
automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ACCION_REQUERIDA.md

Tareas P0-P1:
1. Corregir error webhook Orquestador (ID: bva1Kc1USbbITEAw)
   - Agregar onError: continueRegularOutput al nodo Webhook
2. Actualizar typeVersions obsoletas en ambos workflows
3. Agregar Error Trigger para manejo de errores

Usa el MCP de n8n para hacer las correcciones directamente.
Después valida los workflows y activa el Orquestador.
```

### Datos clave n8n:
| Campo | Valor |
|-------|-------|
| **Instancia** | https://carrilloabgd.app.n8n.cloud |
| **Versión** | v1.120.4 |
| **Orquestador ID** | `bva1Kc1USbbITEAw` |
| **SUB-A ID** | `RHj1TAqBazxNFriJ` |
| **Webhook URL** | `https://carrilloabgd.app.n8n.cloud/webhook/lead-events` |

### Error a corregir:
```
Nodo: "Webhook Principal Lead Events"
Problema: responseMode=responseNode sin onError configurado
Solución: Agregar "onError": "continueRegularOutput" en options
```

---

## 🔵 CHAT 2: Integración Backend + Frontend

### Prompt sugerido:

```
Estoy en la rama `automation` del proyecto CarrilloAbogados.

Necesito implementar la integración web con n8n según:
automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ACCION_REQUERIDA.md

Tareas P1.5-P1.9:

1. Configurar n8n-integration-service:
   - Agregar URL webhook en application.yml
   - Actualizar N8nWebhookService.java

2. Implementar WebhookController callbacks:
   - POST /webhook/lead-scored → actualizar score en BD
   - POST /webhook/lead-hot → notificación urgente

3. Configurar Frontend Next.js:
   - API proxy en next.config.mjs
   - Actualizar formulario /contacto para usar ruta correcta

4. Verificar flujo NATS:
   - NatsEventListener escuchando eventos
   - client-service emitiendo eventos al crear lead

Después probar E2E con Docker Compose.
```

### Archivos a modificar:

| Archivo | Cambio |
|---------|--------|
| `n8n-integration-service/src/main/resources/application.yml` | Agregar URL webhook |
| `n8n-integration-service/.../WebhookController.java` | Implementar callbacks |
| `n8n-integration-service/.../N8nWebhookService.java` | Configurar URL |
| `frontend/next.config.mjs` | API proxy rewrites |
| `frontend/src/app/contacto/page.tsx` | Ruta API correcta |

### Endpoints n8n → Platform:
| Endpoint | Payload esperado |
|----------|------------------|
| `/webhook/lead-scored` | `{leadId, score, category, analysisNotes}` |
| `/webhook/lead-hot` | `{leadId, urgency, assignedTo}` |

---

## 📁 ESTRUCTURA RELEVANTE

```
CarrilloAbogados/
├── automation/
│   └── n8n-workflows/
│       ├── WEB_INTEGRATION.md          # Arquitectura integración
│       └── workflows/
│           └── MEGA_WORKFLOW_1_LEAD_LIFECYCLE/
│               ├── ACCION_REQUERIDA.md # ⭐ Plan de acción
│               └── STATUS.md           # Estado actual
├── n8n-integration-service/
│   └── src/main/java/.../
│       ├── WebhookController.java      # Callbacks n8n
│       ├── NatsEventListener.java      # Escucha NATS
│       └── N8nWebhookService.java      # Cliente HTTP
├── client-service/
│   └── src/main/java/.../
│       ├── LeadResource.java           # API leads
│       └── LeadService.java            # Lógica + eventos
└── frontend/
    ├── next.config.mjs                 # API proxy
    └── src/app/contacto/page.tsx       # Formulario
```

---

## 🔗 FLUJO DE INTEGRACIÓN

```
Frontend (/contacto)
    │ POST /api/client-service/api/leads
    ▼
API Gateway (8080)
    │
    ▼
client-service (8200)
    │ Crea lead + emite NATS event
    ▼
NATS ("carrillo.events.lead.created")
    │
    ▼
n8n-integration-service (8800)
    │ HTTP POST al webhook
    ▼
n8n Cloud (Orquestador)
    │ Ejecuta SUB-A (scoring + IA)
    ▼
n8n Cloud (callback)
    │ POST /webhook/lead-scored
    ▼
n8n-integration-service
    │ Actualiza BD
    ▼
Lead procesado + Email enviado
```

---

## 🧪 TEST E2E

```bash
# Levantar entorno
docker-compose up -d

# Crear lead de prueba
curl -X POST http://localhost:8080/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Integración",
    "email": "test@empresa.com",
    "telefono": "3001234567",
    "empresa": "Test Corp",
    "servicio": "marca",
    "mensaje": "Prueba E2E"
  }'

# Verificar logs
docker logs -f carrillo-n8n-integration-service
```

---

## 📞 CREDENCIALES n8n (Solo referencia)

| Credential | ID | Servicio |
|------------|-----|----------|
| Google Gemini | `jk2FHcbAC71LuRl2` | IA para scoring/respuestas |
| Gmail OAuth2 | `l2mMgEf8YUV7HHlK` | Envío emails |
| Firestore | `AAhdRNGzvsFnYN9O` | Base de datos leads |

---

*Generado: 2026-01-03 - Rama: automation*
