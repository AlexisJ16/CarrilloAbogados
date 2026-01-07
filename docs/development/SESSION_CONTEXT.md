# 📋 CONTEXTO DE SESIONES - Carrillo Abogados

**Propósito**: Documento para mantener contexto entre sesiones de desarrollo con IA.  
**Última Actualización**: 8 de Enero, 2026

---

## 🎯 ESTADO ACTUAL DEL SISTEMA

### Infraestructura Operativa ✅

| Componente | Estado | Detalles |
|------------|--------|----------|
| **Docker Compose** | ✅ 18 contenedores | Todos healthy |
| **PostgreSQL** | ✅ Operativo | 7 schemas, 3 usuarios de prueba |
| **NATS** | ✅ Operativo | Mensajería asíncrona |
| **Frontend** | ✅ Next.js 14 | Puerto 3000, 15 páginas |
| **API Gateway** | ✅ Spring Cloud Gateway | Puerto 8080 |
| **Microservicios** | ✅ 8 servicios | Puertos 8200-8800 |
| **Observabilidad** | ✅ Grafana LGTM | Prometheus, Loki, Tempo, Mimir |

### Usuarios de Prueba Disponibles

| Rol | Email | Password | Permisos |
|-----|-------|----------|----------|
| **Cliente** | cliente.prueba@example.com | Cliente123! | 5 permisos básicos |
| **Abogado** | abogado.prueba@carrilloabgd.com | Cliente123! | 10 permisos, isStaff=true |
| **Admin** | admin.prueba@carrilloabgd.com | Cliente123! | 9 wildcard permisos |

### URLs de Acceso

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Frontend** | http://localhost:3000 | - |
| **API Gateway** | http://localhost:8080 | - |
| **Grafana** | http://localhost:3100 | admin / carrillo2025 |
| **Prometheus** | http://localhost:9090 | - |

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
