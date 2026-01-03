# 🚨 ACCIONES REQUERIDAS - MW#1 Lead Lifecycle

**Estado**: ⚠️ WORKFLOWS INACTIVOS - PENDIENTE CORRECCIONES  
**Última verificación**: 2026-01-03 (via n8n MCP)  
**Próxima revisión**: Antes de lanzamiento MVP (27 Marzo 2026)

---

## 📋 Resumen de Estado

| Workflow | ID | Estado | Validación |
|----------|-----|--------|------------|
| Orquestador | `bva1Kc1USbbITEAw` | ⚪ INACTIVO | ❌ 1 error, 5 warnings |
| SUB-A Lead Intake | `RHj1TAqBazxNFriJ` | ⚪ INACTIVO | ⚠️ 7 warnings |

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

## 🟢 P2 - MEJORAS (Post-lanzamiento)

### 5. Conectar Integración Web

**Estado actual**: El `n8n-integration-service` está listo pero no conectado.

**Pasos**:
1. Actualizar `NatsEventListener.java` con URL del webhook:
   ```java
   private static final String N8N_WEBHOOK_URL = 
       "https://carrilloabgd.app.n8n.cloud/webhook/lead-events";
   ```

2. Desplegar `n8n-integration-service` en Cloud Run

3. Probar flujo completo:
   ```bash
   curl -X POST http://localhost:8200/client-service/api/leads \
     -H "Content-Type: application/json" \
     -d '{
       "nombre": "Test Lead",
       "email": "test@example.com",
       "servicio": "marca",
       "mensaje": "Prueba integración E2E"
     }'
   ```

### 6. Implementar Callbacks de n8n

**Endpoints pendientes en WebhookController.java**:

| Endpoint | Implementación | Estado |
|----------|----------------|--------|
| `/webhook/lead-scored` | Actualizar `leadScore` en BD | ⬜ TODO |
| `/webhook/lead-hot` | Crear notificación urgente | ⬜ TODO |
| `/webhook/meeting-confirmed` | Sincronizar con calendar-service | ⬜ TODO |

### 7. Dashboard de Monitoreo

- Crear dashboard Grafana para métricas de leads
- Alertas ante:
  - Tasa de error > 20%
  - Tiempo de respuesta > 60 segundos
  - Leads HOT sin atender > 5 minutos

---

## ✅ Checklist Pre-Activación

- [ ] Corregir error webhook Orquestador (P0)
- [ ] Validar workflow con `mcp_n8n_n8n_validate_workflow`
- [ ] Actualizar typeVersions obsoletas (P1)
- [ ] Agregar Error Trigger a ambos workflows (P1)
- [ ] Probar manualmente con payload de prueba
- [ ] Verificar credenciales activas (Gmail, Gemini, Firestore)
- [ ] Activar Orquestador en n8n UI
- [ ] Confirmar webhook público accesible

---

## 📞 Contacto

| Rol | Email | Responsabilidad |
|-----|-------|-----------------|
| Desarrollo | alexis (dev) | Correcciones técnicas |
| Marketing | marketing@carrilloabgd.com | Pruebas E2E |
| Admin | ingenieria@carrilloabgd.com | Notificaciones de errores |

---

*Documento generado con datos de n8n Cloud MCP - 2026-01-03*
