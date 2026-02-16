# Script para Actualizar URLs de Callback en n8n Cloud

**Fecha**: 21 de Enero, 2026
**Propósito**: Configurar los callbacks desde SUB-A hacia el backend local vía Dev Tunnel

## 🎯 URLs a Actualizar en n8n Cloud

### Workflow: SUB-A Lead Intake (v5)
**ID del Workflow**: `RHj1TAqBazxNFriJ`

#### Nodo "7. Callback Lead Scored"
- **Tipo**: HTTP Request
- **URL Anterior**: `https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-scored` (EXPIRADA)
- **✅ URL CORRECTA**: `https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-scored`

#### Nodo "9. Callback Hot Lead Alert"
- **Tipo**: HTTP Request
- **URL Anterior**: `https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-hot` (EXPIRADA)
- **✅ URL CORRECTA**: `https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-hot`

## 📋 Pasos para Actualizar

### 1. Obtener la URL del Dev Tunnel

```powershell
# En VS Code:
# 1. Ctrl+Shift+P → "Ports: Focus on Ports View"
# 2. Add Port → 8800
# 3. Right click → Port Visibility → Public
# 4. Copiar la URL "Forwarded Address"
```

### 2. Acceder a n8n Cloud

1. Ir a: https://carrilloabgd.app.n8n.cloud
2. Login con: `marketing@carrilloabgd.com`
3. Buscar workflow: "SUB-A: Lead Intake (v5 - AI POWERED - NATIVE)"

### 3. Editar el Workflow

#### Nodo "7. Callback Lead Scored"
1. Click en el nodo "7. Callback Lead Scored"
2. En el campo "URL", actualizar a:
   ```
   https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-scored
   ```
3. Verificar que el método sea: `POST`
4. Verificar el body JSON:
   ```json
   {
     "lead_id": "{{ $('1. Validar y Clasificar1').item.json.lead_id }}",
     "score": "{{ $('1. Validar y Clasificar1').item.json.score }}",
     "categoria": "{{ $('1. Validar y Clasificar1').item.json.categoria }}",
     "ai_analysis": "{{ $('1. Validar y Clasificar1').item.json.ai_analysis }}",
     "processed_at": "{{ $('1. Validar y Clasificar1').item.json.processed_at }}"
   }
   ```

#### Nodo "9. Callback Hot Lead Alert"
1. Click en el nodo "9. Callback Hot Lead Alert"
2. En el campo "URL", actualizar a:
   ```
   https://kq8wr4w1-8800.use2.devtunnels.ms/n8n-integration-service/webhook/lead-hot
   ```
3. Verificar que el método sea: `POST`
4. Verificar el body JSON:
   ```json
   {
     "lead_id": "{{ $('1. Validar y Clasificar1').item.json.lead_id }}",
     "score": "{{ $('1. Validar y Clasificar1').item.json.score }}",
     "categoria": "{{ $('1. Validar y Clasificar1').item.json.categoria }}",
     "notified_at": "{{ new Date().toISOString() }}",
     "email_sent_to": "marketing@carrilloabgd.com"
   }
   ```

### 4. Guardar y Activar

1. Click en "Save" (guardar)
2. Verificar que el workflow esté "Active" (activado)
3. Si no está activo, toggle el switch a "Active"

## 🔍 Verificación de Endpoints Backend

Antes de probar, verificar que los endpoints existan:

```powershell
# Verificar endpoint lead-scored
Invoke-WebRequest -Uri "http://localhost:8800/n8n-integration-service/webhook/lead-scored" -Method OPTIONS

# Verificar endpoint lead-hot
Invoke-WebRequest -Uri "http://localhost:8800/n8n-integration-service/webhook/lead-hot" -Method OPTIONS
```

## ⚠️ PROBLEMA DEL LOOP INFINITO

### Diagnóstico
El loop infinito ocurre porque:
1. Lead se crea → Publica evento NATS
2. n8n procesa → Intenta hacer callback
3. **Callback falla** (URL expirada/timeout)
4. n8n retorna error al backend
5. **Backend recibe error pero NO hace nada**
6. NATS re-entrega el mensaje por timeout
7. **LOOP INFINITO** ∞

### Solución Temporal
Deshabilitar el retry automático de NATS añadiendo `maxDeliver: 1`:

```java
// En NatsEventListener.java
PushSubscribeOptions options = PushSubscribeOptions.builder()
    .deliverSubject(deliverSubject)
    .maxDeliver(1)  // ← AGREGAR ESTO
    .build();
```

## 📊 Test E2E del Flujo

### Escenario 1: Lead COLD
```json
POST http://localhost:8080/client-service/api/leads
{
  "nombre": "Test Lead Cold",
  "email": "test-cold-{{timestamp}}@example.com",
  "telefono": "+573001234567",
  "servicio": "consulta",
  "mensaje": "Consulta general corta",
  "empresa": "Test Corp",
  "cargo": "Empleado"
}
```

**Resultado Esperado**:
- Score: 30-50 (COLD)
- No se envía notificación al equipo
- Se envía email de respuesta al lead
- Callback actualiza score en PostgreSQL

### Escenario 2: Lead HOT
```json
POST http://localhost:8080/client-service/api/leads
{
  "nombre": "Carlos Mendez CEO",
  "email": "carlos.mendez@startup.io",
  "telefono": "+573001234567",
  "servicio": "marca",
  "mensaje": "Necesito registrar la marca de mi startup urgentemente. Tenemos presupuesto asignado y queremos iniciar el proceso esta semana con un bufete especializado.",
  "empresa": "Startup Technologies SAS",
  "cargo": "CEO"
}
```

**Resultado Esperado**:
- Score: 85-95 (HOT)
- ✅ Se envía email a marketing@carrilloabgd.com
- ✅ Se envía email de respuesta al lead
- ✅ Callback actualiza score en PostgreSQL
- ✅ Lead aparece en Firestore

## 📝 Checklist de Validación

- [ ] Dev Tunnel puerto 8800 configurado como PUBLIC
- [ ] URL del Dev Tunnel copiada
- [ ] n8n Cloud SUB-A nodo "7" actualizado
- [ ] n8n Cloud SUB-A nodo "9" actualizado
- [ ] Workflow SUB-A guardado y activo
- [ ] Endpoints backend verificados
- [ ] Servicios Docker running (docker-compose ps)
- [ ] Test con lead COLD exitoso
- [ ] Test con lead HOT exitoso
- [ ] Email recibido en alexisj4a@gmail.com
- [ ] Email HOT recibido en marketing@carrilloabgd.com
- [ ] Score actualizado en PostgreSQL
- [ ] NO hay loop infinito
