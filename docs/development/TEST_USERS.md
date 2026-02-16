# 🧪 Usuarios de Prueba E2E - Carrillo Abogados

**Última Actualización**: 14 de Febrero, 2026  
**Fase Proyecto**: FASE 14 - Infraestructura Depurada  
**Estado**: ✅ Todos los usuarios verificados y funcionando

---

## 📋 Usuarios de Prueba

Estos usuarios están creados en la base de datos PostgreSQL para pruebas E2E y desarrollo local.

### Usuarios Reales (verificados en producción local)

| Rol | Email | Contraseña | Estado |
|-----|-------|------------|--------|
| **Cliente** | alexisj4a@gmail.com | Cliente123! | ✅ Verificado |
| **Abogado** | abogado.test@gmail.com | Cliente123! | ✅ Verificado |
| **Admin** | admin.test@gmail.com | Cliente123! | ✅ Verificado |

### Usuarios Genéricos (plantilla para nuevas instancias)

| Rol | Email | Contraseña | Estado |
|-----|-------|------------|--------|
| **Cliente** | cliente.prueba@example.com | Cliente123! | ✅ Verificado |
| **Abogado** | abogado.prueba@carrilloabgd.com | Cliente123! | ✅ Verificado |
| **Admin** | admin.prueba@carrilloabgd.com | Cliente123! | ✅ Verificado |

---

## 🔑 Permisos por Rol

### ROLE_CLIENT
```json
["client:read", "case:read", "document:read", "calendar:read"]
```

### ROLE_LAWYER
```json
["lawyer:read", "lawyer:write", "client:read", "client:write", 
 "case:read", "case:write", "document:read", "document:write", 
 "calendar:read", "calendar:write"]
```

### ROLE_ADMIN
```json
["admin:*", "lawyer:*", "client:*", "lead:*", "case:*", 
 "document:*", "calendar:*", "notification:*", "settings:*"]
```

---

## 🖥️ Endpoints de Autenticación

### Login
```http
POST http://localhost:8080/client-service/api/auth/login
Content-Type: application/json

{
  "email": "cliente.prueba@example.com",
  "password": "Cliente123!"
}
```

### Registro (requiere aceptar términos)
```http
POST http://localhost:8080/client-service/api/auth/register
Content-Type: application/json

{
  "firstName": "Nombre",
  "lastName": "Apellido",
  "email": "nuevo@example.com",
  "password": "Password123!",
  "acceptTerms": true,
  "acceptPrivacy": true
}
```

---

## 🗄️ Información de Base de Datos

### Conexión
```bash
docker exec -it carrillo-postgresql psql -U carrillo -d carrillo_legal_tech
```

### Tabla de Usuarios
```sql
SELECT account_id, email, first_name, last_name, role, is_active 
FROM clients.user_accounts 
WHERE email LIKE '%prueba%';
```

### Recrear Usuarios (si es necesario)
```sql
-- Primero obtener el hash de un usuario existente creado vía API
-- Luego insertar los demás con el mismo hash

INSERT INTO clients.user_accounts (
  account_id, email, first_name, last_name, 
  password_hash, role, is_active, is_email_verified, 
  auth_provider, accept_terms, accept_privacy, created_at, updated_at
) VALUES (
  gen_random_uuid(), 'abogado.prueba@carrilloabgd.com', 'Abogado', 'Prueba',
  '$2a$12$...hash...', 'ROLE_LAWYER', true, true,
  'LOCAL', true, true, NOW(), NOW()
);
```

---

## 🌐 Acceso al Frontend

- **URL Login**: http://localhost:3000/login
- **URL Register**: http://localhost:3000/register
- **Dashboard**: http://localhost:3000/dashboard (requiere autenticación)

---

## 📊 Stack de Observabilidad

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Grafana** | http://localhost:3100 | admin / carrillo2025 |
| **Prometheus** | http://localhost:9090 | - |
| **Loki** | http://localhost:3101 | - |
| **Tempo** | http://localhost:3102 | - |
| **Alertmanager** | http://localhost:9093 | - |

---

## 🔧 Comandos Útiles

### Verificar Health de Servicios
```powershell
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) { 
  try { 
    $r = Invoke-WebRequest "http://localhost:$p/actuator/health" -TimeoutSec 2 -UseBasicParsing
    Write-Host "$p : UP" -ForegroundColor Green 
  } catch { 
    Write-Host "$p : DOWN" -ForegroundColor Red 
  }
}
```

### Ver Logs de Servicio
```powershell
docker logs carrillo-client-service --tail 100 -f
```

### Probar Login vía API
```powershell
$body = '{"email":"cliente.prueba@example.com","password":"Cliente123!"}'
Invoke-RestMethod -Uri "http://localhost:8080/client-service/api/auth/login" `
  -Method POST -Body $body -ContentType "application/json"
```

---

*Documento generado automáticamente - 7 de Enero 2026*
