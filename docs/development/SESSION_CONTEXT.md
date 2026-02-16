# 📋 CONTEXTO DE SESIONES - Carrillo Abogados

**Propósito**: Documento para mantener contexto entre sesiones de desarrollo con IA.  
**Última Actualización**: 14 de Febrero, 2026

---

## 🎯 ESTADO ACTUAL DEL SISTEMA

### Infraestructura Operativa ✅

| Componente | Estado | Detalles |
|------------|--------|----------|
| **Docker Compose** | ✅ 11 contenedores | Todos healthy (Google Jib build) |
| **PostgreSQL** | ✅ Operativo | 6 schemas, 3 usuarios de prueba |
| **NATS** | ✅ Operativo | Mensajería asíncrona |
| **Frontend** | ✅ Next.js 16 | Puerto 3000, 16 páginas |
| **API Gateway** | ✅ Spring Cloud Gateway | Puerto 8080, CORS corregido |
| **Microservicios** | ✅ 8 servicios | Puertos 8200-8800 |
| **Observabilidad** | ✅ Grafana LGTM | Prometheus, Loki, Tempo, Mimir |
| **Autenticación** | ✅ JWT + CORS | Login funcional desde frontend |

### Usuarios de Prueba Disponibles

| Rol | Email | Password | Permisos |
|-----|-------|----------|----------|
| **Cliente** | alexisj4a@gmail.com | Cliente123! | 5 permisos básicos |
| **Abogado** | abogado.test@gmail.com | Cliente123! | 10 permisos, isStaff=true |
| **Admin** | admin.test@gmail.com | Cliente123! | 9 wildcard permisos |

### URLs de Acceso

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Frontend** | http://localhost:3000 | - |
| **API Gateway** | http://localhost:8080 | - |
| **Grafana** | http://localhost:3100 | admin / carrillo2025 |
| **Prometheus** | http://localhost:9090 | - |
| **n8n Cloud** | https://carrilloabgd.app.n8n.cloud | (ver n8n dashboard) |

### 🔗 Integración n8n Cloud - PRODUCCIÓN ✅

**Estado**: Backend 100% funcional, workflow activo en n8n Cloud

| Componente | Estado | Detalles |
|------------|--------|----------|
| **Webhook URL** | ✅ Configurado | `/webhook-test/lead-events-v3` |
| **NATS Events** | ✅ Publicando | `carrillo.events.lead.captured` |
| **n8n-integration-service** | ✅ Operativo | Puerto 8800 |
| **Workflow n8n** | ✅ Activo | Modo producción |
| **Callback Endpoint** | ✅ Probado | `/webhook/lead-scored` funcional |
| **Spring Security** | ✅ Configurado | PATCH `/api/leads/*/score` permitAll |
| **PostgreSQL Updates** | ✅ Verificado | Score y category actualizan correctamente |

**Dev Tunnel**: El puerto 8800 debe exponerse públicamente para callbacks de n8n Cloud.

**Test Realizado (14 Ene 2026)**:
- Lead ID: `61ccdfec-4d47-4cc2-9c83-787d3665c06e`
- Resultado: score 0→90, category COLD→HOT ✅
- Database updated_at: 2026-01-14 13:20:31

**Próximo Paso**: Test E2E desde formulario web → n8n → callback → database

---

## 📊 TESTS VERIFICADOS

### E2E Frontend (8 Ene 2026)
- ✅ 15 páginas probadas (8 públicas, 7 protegidas)
- ✅ Login API verificado para 3 roles
- ✅ Registration API funcional
- ✅ Profile endpoint `/api/auth/me` operativo

### Observabilidad (8 Ene 2026)
- ✅ 13/13 Prometheus targets UP
- ✅ Grafana dashboard con datos
- ✅ Métricas JVM fluyendo
- ✅ 33 series HTTP métricas

---

## 🔧 COMANDOS FRECUENTES

### Levantar Infraestructura
```powershell
# Backend + Frontend
docker-compose up -d

# Observabilidad
cd monitoring
docker-compose -f docker-compose.observability.yml up -d
```

### Verificar Estado
```powershell
# Contenedores
docker ps --filter "name=carrillo" --format "table {{.Names}}\t{{.Status}}"

# Health checks
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) { 
    try { Invoke-WebRequest "http://localhost:$p/actuator/health" -TimeoutSec 2 | Out-Null; Write-Host "$p UP" -ForegroundColor Green } 
    catch { Write-Host "$p DOWN" -ForegroundColor Red } 
}
```

### Login de Prueba
```powershell
$body = @{ username = "abogado.prueba@carrilloabgd.com"; password = "Cliente123!" } | ConvertTo-Json
$response = Invoke-WebRequest -Uri "http://localhost:8080/client-service/api/auth/login" -Method POST -Body $body -ContentType "application/json"
$response.Content | ConvertFrom-Json
```

---

## 📋 PRÓXIMAS TAREAS PENDIENTES

### Prioridad Alta
1. **Configurar SonarCloud** - Análisis de código en CI/CD
2. **Deploy a GCP Cloud Run** - Ambiente staging
3. **Integrar n8n Cloud** - Webhooks de marketing

### Prioridad Media
4. **Google Calendar API** - calendar-service
5. **Templates de email** - notification-service
6. **Document upload** - document-service

### Prioridad Baja
7. **Payment CRUD completo** - payment-service
8. **OAuth2 Google Workspace** - Login con Google

---

## 🚨 PROBLEMAS CONOCIDOS Y SOLUCIONES

### CORS Frontend ↔ API Gateway (RESUELTO 11 Ene 2026)
**Problema**: Login desde frontend retornaba "Network Error" - CORS bloqueando preflight.
**Causa**: `application.yml` solo permitía `localhost:4200` (Angular legacy).
**Solución**: Actualizado `api-gateway/src/main/resources/application.yml`:
```yaml
allowed-origins:
  - "${CLIENT_HOST:http://localhost:3000}"
  - "http://localhost:4200"
  - "http://localhost:3000"
```

### Header sin botón de Login (RESUELTO 11 Ene 2026)
**Problema**: El componente Header.tsx no tenía UI de autenticación.
**Solución**: Reescrito `frontend/src/components/layout/Header.tsx` con:
- Botón "Iniciar Sesión" para usuarios no autenticados
- Menú dropdown con nombre de usuario para autenticados
- Links a dashboard y opción de logout

### BCrypt en PowerShell
**Problema**: El símbolo `$` en hashes BCrypt se escapa incorrectamente.
**Solución**: Usar pipe para insertar:
```powershell
'UPDATE clients.credentials SET password = ''$2a$12$hash...'' WHERE username = ''email@example.com'';' | docker exec -i postgresql psql -U carrillo -d carrillo_legal_tech
```

### Mimir Healthcheck
**Problema**: Imagen distroless no tiene wget/curl.
**Solución**: Healthcheck deshabilitado, monitoreo via Prometheus.

### Prometheus Targets
**Problema**: Algunos servicios usan context-path, otros no.
**Solución**: Jobs individuales con `metrics_path` específico por servicio.

---

## 📁 ARCHIVOS DE DOCUMENTACIÓN CLAVE

| Archivo | Propósito |
|---------|-----------|
| [PROYECTO_ESTADO.md](../../PROYECTO_ESTADO.md) | Estado actual del proyecto |
| [CLAUDE.md](../../CLAUDE.md) | Contexto para Claude AI |
| [TEST_USERS.md](./TEST_USERS.md) | Usuarios de prueba |
| [OBSERVABILITY_GUIDE.md](../operations/OBSERVABILITY_GUIDE.md) | Guía de observabilidad |
| [copilot-instructions.md](../../.github/copilot-instructions.md) | Instrucciones para Copilot |

---

*Este documento se actualiza al final de cada sesión de desarrollo.*
