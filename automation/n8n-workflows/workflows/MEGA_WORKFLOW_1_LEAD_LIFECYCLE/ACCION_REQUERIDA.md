# 🚨 ACCIONES REQUERIDAS - MW#1 Lead Lifecycle + Integración Web

**Estado**: ⚠️ WORKFLOWS INACTIVOS - PENDIENTE CORRECCIONES  
**Última verificación**: 2026-01-03 (via n8n MCP)  
**Próxima revisión**: Antes de lanzamiento MVP (27 Marzo 2026)

---

## 📋 Resumen de Estado

### n8n Cloud

| Workflow | ID | Estado | Validación |
|----------|-----|--------|------------|
| Orquestador | `bva1Kc1USbbITEAw` | ⚪ INACTIVO | ❌ 1 error, 5 warnings |
| SUB-A Lead Intake | `RHj1TAqBazxNFriJ` | ⚪ INACTIVO | ⚠️ 7 warnings |

### Backend Platform

| Componente | Estado | Notas |
|------------|--------|-------|
| client-service Lead API | ✅ Ready | POST /api/leads funcional |
| n8n-integration-service | ⚠️ 80% | Falta configurar URL webhook |
| Frontend /contacto | ✅ Ready | Formulario implementado |
| NATS Messaging | ✅ Ready | Operativo en Docker |

---

## 🔴 P0 - CRÍTICO (Bloquea activación)

### 1. Corregir Error en Webhook del Orquestador

**Problema**: El nodo "Webhook Principal Lead Events" usa `responseMode: responseNode` pero no tiene configurado `onError`.

**Impacto**: El workflow falla al validar y no puede activarse correctamente.

**Solución**:

```bash
# Opción A: Usar n8n MCP para actualizar
mcp_n8n_n8n_update_workflow --id bva1Kc1USbbITEAw --changes '
{
  "nodes": [
    {
      "name": "Webhook Principal Lead Events",
      "parameters": {
        "httpMethod": "POST",
        "path": "lead-events",
        "responseMode": "responseNode",
        "options": {
          "onError": "continueRegularOutput"
        }
      }
    }
  ]
}'
```

```bash
# Opción B: Editar manualmente en n8n UI
1. Abrir https://carrilloabgd.app.n8n.cloud
2. Editar workflow "WORKFLOW A: Lead Lifecycle Manager"
3. Hacer click en nodo "Webhook Principal Lead Events"
4. En Settings → Add Option → On Error → Continue with Regular Output
5. Guardar workflow
```

**Verificación**:
```bash
# Validar con MCP después de corregir
mcp_n8n_n8n_validate_workflow --id bva1Kc1USbbITEAw
# Esperado: 0 errors, N warnings
```

---

## 🟡 P1 - IMPORTANTE (Antes de producción)

### 2. Actualizar TypeVersions Obsoletas

Los siguientes nodos tienen versiones antiguas:

| Workflow | Nodo | Versión Actual | Versión Recomendada |
|----------|------|----------------|---------------------|
| Orquestador | Execute Workflow | 1.2 | 1.3 |
| Orquestador | Respond to Webhook | 1.1 | 1.5 |
| SUB-A | If Node | 2 | 2.3 |
| SUB-A | Gmail Send Email | 2.1 | 2.2 |
| SUB-A | Gmail Notificación | 2.1 | 2.2 |

**Solución**: 
- Abrir cada workflow en n8n UI
- El editor mostrará un banner "Upgrade available" en nodos obsoletos
- Click en "Update" para cada nodo

### 3. Agregar Error Handling

**Problema**: Ninguno de los workflows tiene nodo "Error Trigger" configurado.

**Impacto**: Cuando falla un nodo, no hay notificación automática ni registro de error.

**Solución**:
1. En cada workflow, agregar nodo "Error Trigger"
2. Conectar a un nodo Gmail que envíe notificación a `ingenieria@carrilloabgd.com`
3. Template de email:
   ```
   Subject: [n8n ERROR] Workflow {{$workflow.name}} falló
   Body: 
   - Error: {{$error.message}}
   - Nodo: {{$error.node}}
   - Timestamp: {{$now}}
   - Execution ID: {{$executionId}}
   ```

### 4. Mejorar Tasa de Éxito SUB-A

**Problema**: 40% success rate (4/10 ejecuciones exitosas)

**Diagnóstico requerido**:
1. Revisar logs de ejecuciones fallidas
2. Verificar validación de payload de entrada
3. Confirmar conectividad con Firestore y Gmail

**Comando para revisar**:
```bash
mcp_n8n_n8n_executions --workflowId RHj1TAqBazxNFriJ --status error --limit 5
```

---

## � P1.5 - INTEGRACIÓN PLATAFORMA WEB (Paralelo a n8n)

> ⚡ **IMPORTANTE**: Estas tareas pueden hacerse en paralelo con las correcciones de n8n

### 5. Configurar n8n-integration-service

**Archivo**: `n8n-integration-service/src/main/resources/application.yml`

**Agregar configuración**:
```yaml
n8n:
  webhooks:
    lead-events: https://carrilloabgd.app.n8n.cloud/webhook/lead-events
    # NOTA: Solo funciona cuando Orquestador está ACTIVO
  
  api:
    url: https://carrilloabgd.app.n8n.cloud
    # key: ${N8N_API_KEY} # Opcional para debugging
```

**Archivo**: `N8nWebhookService.java`

```java
// Actualizar URL del webhook:
@Value("${n8n.webhooks.lead-events}")
private String leadEventsWebhookUrl;
```

### 6. Implementar WebhookController Callbacks

**Archivo**: `WebhookController.java`

| Endpoint | Implementar | Código |
|----------|-------------|--------|
| `/webhook/lead-scored` | Actualizar lead en BD | Ver abajo |
| `/webhook/lead-hot` | Notificación + Crear tarea | Ver abajo |

```java
@PostMapping("/webhook/lead-scored")
public ResponseEntity<?> handleLeadScored(@RequestBody LeadScoredPayload payload) {
    log.info("Lead scored: {} with score {}", payload.getLeadId(), payload.getScore());
    
    // TODO: Llamar a client-service para actualizar score
    // webClient.patch()
    //   .uri("http://client-service:8200/api/leads/{id}/score", payload.getLeadId())
    //   .bodyValue(Map.of("leadScore", payload.getScore(), "category", payload.getCategory()))
    //   .retrieve()
    //   .bodyToMono(Void.class)
    //   .block();
    
    return ResponseEntity.ok().build();
}

@PostMapping("/webhook/lead-hot")
public ResponseEntity<?> handleHotLead(@RequestBody HotLeadPayload payload) {
    log.info("🔥 HOT LEAD detected: {}", payload.getLeadId());
    
    // TODO: Crear notificación urgente
    // natsTemplate.publish("carrillo.notifications.urgent", payload);
    
    // TODO: Enviar a notification-service
    
    return ResponseEntity.ok().build();
}
```

### 7. Configurar Frontend API Proxy

**Archivo**: `frontend/next.config.mjs`

```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: process.env.NEXT_PUBLIC_API_URL 
          ? `${process.env.NEXT_PUBLIC_API_URL}/:path*`
          : 'http://localhost:8080/:path*', // API Gateway
      },
    ];
  },
};

export default nextConfig;
```

**Archivo**: `frontend/.env.local`

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
# En producción: https://api.carrilloabgd.com
```

### 8. Actualizar Formulario de Contacto

**Archivo**: `frontend/src/app/contacto/page.tsx`

```tsx
// Cambiar línea ~63:
const response = await fetch('/api/leads', { ... });

// Por:
const response = await fetch('/api/client-service/api/leads', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    nombre: formData.nombre,
    email: formData.email,
    telefono: formData.telefono,
    empresa: formData.empresa,
    servicio: formData.servicio,
    mensaje: formData.mensaje,
    source: 'WEBSITE',
    utmSource: new URLSearchParams(window.location.search).get('utm_source') || '',
    utmCampaign: new URLSearchParams(window.location.search).get('utm_campaign') || '',
  }),
});
```

---

## 🔵 P1.7 - VERIFICAR FLUJO NATS (Después de P1.5)

### 9. Verificar NatsEventListener

**Archivo**: `NatsEventListener.java`

Confirmar que escucha los eventos correctos:
```java
@NatsListener(subjects = {
    "carrillo.events.lead.created",  // ← Emitido por client-service
    "carrillo.events.case.closed",
    "carrillo.events.appointment.scheduled"
})
```

### 10. Verificar client-service emite eventos

**Archivo**: `LeadService.java`

Confirmar que al crear lead emite evento:
```java
public Lead createLead(LeadDTO dto) {
    Lead lead = leadRepository.save(mapToEntity(dto));
    
    // ¿Existe esta línea?
    eventPublisher.publish("carrillo.events.lead.created", LeadCreatedEvent.from(lead));
    
    return lead;
}
```

---

## 🟣 P1.9 - TEST E2E LOCAL

### 11. Levantar Entorno Completo

```bash
# 1. Iniciar todos los servicios
docker-compose up -d

# 2. Verificar todos healthy
docker-compose ps

# 3. Ver logs de n8n-integration-service
docker logs -f carrillo-n8n-integration-service
```

### 12. Probar Flujo Completo

```bash
# Paso 1: Crear lead via API (simula formulario)
curl -X POST http://localhost:8080/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Integración",
    "email": "test@empresa.com",
    "telefono": "3001234567",
    "empresa": "Test Corp S.A.S",
    "servicio": "marca",
    "mensaje": "Necesito registrar mi marca para proteger mi negocio. Es urgente."
  }'

# Paso 2: Verificar logs de n8n-integration-service
# Debe mostrar: "Forwarding lead.created event to n8n webhook"

# Paso 3: Verificar en n8n Cloud
# Dashboard → Executions → Debe aparecer nueva ejecución

# Paso 4: Verificar email recibido (test@empresa.com)
```

---

## �🟢 P2 - MEJORAS (Post-lanzamiento)

### 7. Dashboard de Monitoreo

- Crear dashboard Grafana para métricas de leads
- Alertas ante:
  - Tasa de error > 20%
  - Tiempo de respuesta > 60 segundos
  - Leads HOT sin atender > 5 minutos

---

## ✅ Checklist COMPLETO Pre-Activación

### n8n Cloud
- [ ] 1. Corregir error webhook Orquestador (P0)
- [ ] 2. Validar workflow con `mcp_n8n_n8n_validate_workflow`
- [ ] 3. Actualizar typeVersions obsoletas (P1)
- [ ] 4. Agregar Error Trigger a ambos workflows (P1)
- [ ] 5. Probar manualmente con payload de prueba
- [ ] 6. Verificar credenciales activas (Gmail, Gemini, Firestore)
- [ ] 7. Activar Orquestador en n8n UI
- [ ] 8. Confirmar webhook público accesible

### Backend Platform
- [ ] 9. Configurar `N8N_WEBHOOK_URL` en n8n-integration-service
- [ ] 10. Implementar lógica en WebhookController callbacks
- [ ] 11. Configurar Next.js API route proxy a backend
- [ ] 12. Verificar NATS events funcionando
- [ ] 13. Desplegar servicios en Docker Compose

### E2E Testing
- [ ] 14. Probar flujo: Formulario → Backend → NATS → n8n
- [ ] 15. Verificar email de respuesta IA recibido
- [ ] 16. Confirmar lead guardado en Firestore
- [ ] 17. Probar lead HOT → notificación equipo

---

## 📞 Contacto

| Rol | Email | Responsabilidad |
|-----|-------|-----------------|
| Desarrollo | alexis (dev) | Correcciones técnicas |
| Marketing | marketing@carrilloabgd.com | Pruebas E2E |
| Admin | ingenieria@carrilloabgd.com | Notificaciones de errores |

---

*Documento generado con datos de n8n Cloud MCP - 2026-01-03*
