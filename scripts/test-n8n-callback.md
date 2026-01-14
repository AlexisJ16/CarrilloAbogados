# 🧪 Script de Prueba Manual - Callback n8n

**Propósito**: Simular callback de n8n Cloud al backend para verificar actualización de score.

## Test 1: Webhook Test (Verificar Conectividad)

```powershell
# PowerShell
$headers = @{"Content-Type"="application/json"}
$body = @{"test"="connectivity_check"} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8800/n8n-integration-service/webhook/test" -Method POST -Headers $headers -Body $body
```

**Resultado Esperado**:
```json
{
  "status": "success",
  "message": "Webhook endpoint is reachable",
  "received_payload": {"test": "connectivity_check"},
  "timestamp": "2026-01-14T..."
}
```

---

## Test 2: Lead Scored Callback (Actualizar Score)

```powershell
# PowerShell
$leadId = "61ccdfec-4d47-4cc2-9c83-787d3665c06e"  # Usar lead_id real de PostgreSQL

$headers = @{"Content-Type"="application/json"}
$body = @{
    event_type = "lead_scored"
    timestamp = Get-Date -Format "o"
    lead_id = $leadId
    score = 75
    category = "HOT"
    score_breakdown = @{
        base = 30
        high_value_service = 20
        corp_email = 10
        message_length = 10
        c_level = 5
    }
    processed_at = Get-Date -Format "o"
    response_sent = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8800/n8n-integration-service/webhook/lead-scored" -Method POST -Headers $headers -Body $body
```

**Resultado Esperado**:
```json
{
  "status": "success",
  "lead_id": "61ccdfec-4d47-4cc2-9c83-787d3665c06e",
  "score_received": 75,
  "updated_in_db": true
}
```

---

## Test 3: Verificar Actualización en PostgreSQL

```bash
docker exec -it carrillo-postgresql psql -U carrillo -d carrillo_legal_tech -c "SELECT lead_id, nombre, lead_score, lead_category FROM clients.leads WHERE lead_id = '61ccdfec-4d47-4cc2-9c83-787d3665c06e';"
```

**Resultado Esperado**:
```
 lead_id                              | nombre        | lead_score | lead_category
--------------------------------------+---------------+------------+---------------
 61ccdfec-4d47-4cc2-9c83-787d3665c06e | Carlos Mendez |     75     | HOT
```

---

## Test 4: Hot Lead Alert

```powershell
$leadId = "61ccdfec-4d47-4cc2-9c83-787d3665c06e"

$headers = @{"Content-Type"="application/json"}
$body = @{
    event_type = "hot_lead_alert"
    timestamp = Get-Date -Format "o"
    lead_id = $leadId
    score = 75
    urgency = "HIGH"
    lead_data = @{
        nombre = "Carlos Mendez"
        email = "carlos@startup.io"
        telefono = "3003333333"
        empresa = "Startup Tech SAS"
        servicio = "Registro de Marca"
    }
} | ConvertTo-Json -Depth 3

Invoke-RestMethod -Uri "http://localhost:8800/n8n-integration-service/webhook/lead-hot" -Method POST -Headers $headers -Body $body
```

---

## Monitoreo de Logs en Tiempo Real

```bash
# Backend logs
docker logs -f carrillo-n8n-integration-service

# Client Service logs (actualización de score)
docker logs -f carrillo-client-service

# PostgreSQL queries
docker exec -it carrillo-postgresql psql -U carrillo -d carrillo_legal_tech -c "SELECT lead_id, nombre, lead_score, lead_category, updated_at FROM clients.leads ORDER BY updated_at DESC LIMIT 5;"
```

---

## Resultado Completo de Tests

### ✅ Checklist de Verificación

- [ ] Webhook /test responde 200 OK
- [ ] Logs muestran `🔔 ==> WEBHOOK CALLBACK RECEIVED: /lead-scored`
- [ ] Score se actualiza en PostgreSQL (0 → 75)
- [ ] Category se actualiza en PostgreSQL (COLD → HOT)
- [ ] Client-service logs muestran `✅ Lead score updated`
- [ ] Logs de hot-lead muestran `🔥 HOT LEAD ALERT`

---

## Troubleshooting

### Error: "Connection refused"
- Verificar que n8n-integration-service esté corriendo: `docker ps | grep n8n`
- Verificar puerto 8800 expuesto: `netstat -ano | findstr 8800`

### Error: "404 Not Found"
- Verificar path correcto: `/n8n-integration-service/webhook/lead-scored`
- Verificar context-path en application.yml

### Error: "Failed to update lead score"
- Verificar que client-service esté corriendo
- Verificar endpoint existe: `GET http://localhost:8200/client-service/actuator/mappings`
- Ver logs de client-service para error específico

