# DEMO PREPARATION - Presentación con Abogados

**Fecha**: Miércoles, 14 de Enero, 2026 - 2:00 PM  
**Duración Estimada**: 30-45 minutos  
**Audiencia**: 7 abogados de Carrillo ABGD SAS  
**Objetivo**: Demostrar plataforma funcional + roadmap de integración n8n

---

## 📋 AGENDA PROPUESTA

| Tiempo | Tema | Duración |
|--------|------|----------|
| 2:00 - 2:05 | Introducción y contexto | 5 min |
| 2:05 - 2:15 | **Demo frontend público** | 10 min |
| 2:15 - 2:25 | **Demo autenticación y roles** | 10 min |
| 2:25 - 2:35 | **Demo gestión de leads** | 10 min |
| 2:35 - 2:40 | Roadmap n8n (próximos pasos) | 5 min |
| 2:40 - 2:45 | Q&A y feedback | 5 min |

---

## 🎯 OBJETIVOS DE LA DEMO

### Objetivos Principales
1. ✅ **Mostrar plataforma funcional** - Stack completo operativo
2. ✅ **Demostrar autenticación** - 3 roles (Cliente, Abogado, Admin)
3. ✅ **Validar UX frontend** - Páginas públicas + dashboard
4. ✅ **Presentar gestión de leads** - API funcionando end-to-end
5. 📋 **Explicar integración n8n** - Roadmap MW#1 (Captura de Leads)

### Objetivos Secundarios
- Obtener feedback sobre diseño y usabilidad
- Identificar features prioritarias para próximas iteraciones
- Validar flujos de negocio con abogados

---

## 🖥️ CHECKLIST PRE-DEMO

### Infraestructura (10 min antes)

- [ ] Iniciar Docker Desktop
- [ ] Levantar stack completo: `docker-compose up -d`
- [ ] Verificar 18/18 contenedores healthy
- [ ] Confirmar 8/8 servicios backend UP
- [ ] Frontend accesible en `http://localhost:3000`
- [ ] Grafana abierto en `http://localhost:3100` (tab en background)

**Comandos de verificación**:
```powershell
# Ver estado de contenedores
docker ps --format "table {{.Names}}\t{{.Status}}"

# Health check rápido de servicios
$ports = @('8080','8200','8300','8400','8500','8600','8700','8800')
foreach ($p in $ports) {
    try { 
        Invoke-RestMethod "http://localhost:$p/actuator/health" -TimeoutSec 2 | Out-Null
        Write-Host "$p : UP" -ForegroundColor Green 
    } catch { 
        Write-Host "$p : DOWN" -ForegroundColor Red 
    }
}
```

### Navegador (Preparar antes de iniciar)

- [ ] **Chrome/Edge** con 3 tabs pre-cargadas:
  1. `http://localhost:3000` - Landing page
  2. `http://localhost:3000/login` - Login page
  3. `http://localhost:3100` - Grafana (admin/carrillo2025)

- [ ] **Ventana de incógnito** lista para login con diferentes roles

### Test Users (Credenciales listas)

| Rol | Email | Password |
|-----|-------|----------|
| **Cliente** | cliente.prueba@example.com | Cliente123! |
| **Abogado** | abogado.prueba@carrilloabgd.com | Cliente123! |
| **Admin** | admin.prueba@carrilloabgd.com | Cliente123! |

### Datos de Prueba

- [ ] 3 leads ya creados en DB (ver E2E_VALIDATION_REPORT.md)
- [ ] PostgreSQL operativo con schemas `clients`

---

## 🎬 SCRIPT DE DEMO

### PARTE 1: Introducción (5 min)

**Contexto**:
> "Buenos días. Hoy les voy a presentar el estado actual de la **Plataforma Carrillo Abogados**, que hemos estado desarrollando como sistema de gestión legal cloud-native. Esta es una plataforma empresarial diseñada específicamente para las necesidades del bufete."

**Arquitectura high-level** (slide o verbal):
- **Frontend**: Next.js 16 (React + TypeScript) - Diseño moderno y responsive
- **Backend**: 8 microservicios Spring Boot (Java 21)
- **Base de Datos**: PostgreSQL 16 con schemas separados
- **Infraestructura**: Docker + Kubernetes (preparado para GCP)
- **Observabilidad**: Grafana + Prometheus + Loki

**Estado actual**:
- ✅ FASE 10 completada - Autenticación frontend completa
- ✅ 18 contenedores operativos
- ✅ 8 servicios backend funcionando
- ✅ Frontend con 10 páginas (6 públicas + 4 protegidas)

---

### PARTE 2: Frontend Público (10 min)

**Navegar por las páginas**:

1. **Landing Page** (`/`)
   - Diseño moderno con hero section
   - Call-to-action claro
   - Navegación intuitiva

2. **Nosotros** (`/nosotros`)
   - Historia del bufete (fundado 2001)
   - Diferenciador: Dr. Omar Carrillo (15 años experiencia SIC)

3. **Servicios** (`/servicios`)
   - 5 áreas de práctica legal:
     * Derecho Corporativo
     * Derecho Marcas (especialidad)
     * Derecho Laboral
     * Derecho de Familia
     * Litigios Comerciales

4. **Equipo** (`/equipo`)
   - 7 abogados presentados
   - Información de contacto

5. **Contacto** (`/contacto`)
   - **Formulario de contacto** (demostrar envío)
   - Crear un lead de prueba en vivo:
     ```
     Nombre: Demo Abogados [HH:MM]
     Email: demo@carrilloabgd.com
     Teléfono: +57 300 123 4567
     Servicio: Derecho Marcas
     Mensaje: Lead creado durante demo con el equipo
     ```
   - **Mostrar respuesta API** (success message)

**Puntos a destacar**:
- Responsive design (mostrar en ventana reducida)
- Validación de formularios en tiempo real
- UX moderna y profesional
- SEO optimizado (meta tags, OpenGraph)

---

### PARTE 3: Autenticación y Roles (10 min)

**Demo de Login**:

1. **Login como Cliente** (`/login`)
   - Email: `cliente.prueba@example.com`
   - Password: `Cliente123!`
   - Mostrar redirect a dashboard
   - **Dashboard Cliente**:
     * Vista de "Mis Casos"
     * Sección "Mis Documentos"
     * Sin acceso a gestión de leads

2. **Logout y Login como Abogado** (ventana incógnito)
   - Email: `abogado.prueba@carrilloabgd.com`
   - Password: `Cliente123!`
   - **Dashboard Abogado**:
     * Acceso a gestión de leads
     * Acceso a gestión de casos (todos)
     * Estadísticas de casos

3. **Logout y Login como Admin** (misma ventana incógnito)
   - Email: `admin.prueba@carrilloabgd.com`
   - Password: `Cliente123!`
   - **Dashboard Admin**:
     * Acceso completo
     * Gestión de usuarios (próximamente)
     * Configuración del sistema

**Puntos a destacar**:
- Sistema de roles robusto (RBAC)
- JWT tokens con expiración configurable (24h)
- Protección de rutas en frontend
- AuthGuard automático (redirige a login si no autenticado)

---

### PARTE 4: Gestión de Leads (10 min)

**Navegar a página de Leads** (`/leads` como Admin o Abogado):

1. **Listar Leads** (deberían aparecer 4 leads incluyendo el creado en Parte 2)
   - Mostrar tabla con leads
   - Columnas: Nombre, Email, Teléfono, Servicio, Estado, Fecha
   - Estados: NUEVO, CONTACTADO, CALIFICADO, DESCARTADO

2. **Filtros**:
   - Por estado
   - Por servicio
   - Búsqueda por nombre/email

3. **Ver detalle de un Lead**:
   - Click en un lead
   - Ver información completa
   - Lead Scoring (actualmente en 0, se calculará con n8n)
   - Categoría (COLD, WARM, HOT)

4. **Cambiar estado de Lead**:
   - De NUEVO → CONTACTADO
   - Mostrar actualización en tiempo real

**Verificación Backend**:
- Abrir Grafana en background tab
- Mostrar métricas de API Gateway
- Prometheus: 13/13 targets UP
- Logs en tiempo real (opcional, si hay tiempo)

**Puntos a destacar**:
- API RESTful funcionando
- Autenticación JWT en cada request
- Base de datos PostgreSQL
- Observabilidad completa del sistema

---

### PARTE 5: Roadmap n8n (5 min)

**Contexto**:
> "El siguiente paso es la integración con **n8n** (marketing automation) para automatizar la captura y calificación de leads en menos de 1 minuto."

**MEGA-WORKFLOW #1: Captura de Leads (90% completo)**

**Flujo Automático**:
1. **Lead llena formulario** en `/contacto` → POST a backend
2. **Backend publica evento** en NATS: `carrillo.events.lead.created`
3. **n8n escucha evento** vía webhook
4. **Lead Scoring automático**:
   ```
   Base: 30 pts
   + Servicio "marca"/"litigio": +20
   + Mensaje > 50 chars: +10
   + Tiene teléfono: +10
   + Tiene empresa: +10
   + Email corporativo: +10
   + Cargo C-Level: +20
   ───────────────────
   HOT:  ≥70 pts → Notificación inmediata
   WARM: 40-69 pts → Nurturing IA
   COLD: <40 pts → Respuesta genérica
   ```
5. **Notificación a abogado** (< 1 min):
   - Email automático
   - Push notification (frontend)
   - WhatsApp/SMS (próximamente)

**Beneficios**:
- **Reducción tiempo de respuesta**: 4-24h → **< 1 min**
- **Incremento conversión**: ~5% → **15%+** (objetivo)
- **Leads calificados automáticamente**
- **Abogados solo atienden leads HOT**

**Estado actual n8n**:
- ✅ n8n Cloud configurado (`https://carrilloabgd.app.n8n.cloud`)
- ✅ Webhook endpoint creado
- 🔄 MEGA-WORKFLOW #1 en desarrollo (pendiente marketing dev)

**Próximos 2 MEGA-WORKFLOWS** (Q2 2026):
- **MW#2**: Retención - Cliente → Recompra (automatización seguimiento)
- **MW#3**: SEO - Tráfico → Lead (contenido automático, blog)

---

### PARTE 6: Q&A y Feedback (5 min)

**Preguntas a los abogados**:

1. **¿Qué les parece el diseño del frontend público?**
   - ¿Es claro el mensaje?
   - ¿Falta alguna información?

2. **¿El sistema de roles cubre sus necesidades?**
   - ¿Qué permisos adicionales necesitarían?

3. **¿El flujo de gestión de leads es intuitivo?**
   - ¿Qué campos adicionales necesitarían?
   - ¿Qué filtros serían útiles?

4. **¿Qué features priorizarían para las próximas 2 semanas?**
   - Gestión de casos completa?
   - Calendario integrado con Google Calendar?
   - Notificaciones en tiempo real?
   - Sistema de documentos?

**Tomar notas de feedback** para priorizar backlog.

---

## 🛠️ TROUBLESHOOTING

### Si un servicio no responde

```powershell
# Reiniciar un servicio específico
docker-compose restart client-service

# Ver logs del servicio
docker-compose logs -f client-service
```

### Si autenticación falla

- Verificar que PostgreSQL tiene los test users:
  ```powershell
  docker exec -it postgresql psql -U carrillo -d carrillo_legal_tech -c "SELECT email, role, is_active FROM clients.user_accounts;"
  ```

### Si frontend no carga

```powershell
# Rebuild frontend
docker-compose up -d --build frontend

# Ver logs
docker-compose logs -f frontend
```

### Si todo falla - PLAN B

- Mostrar screenshots de la plataforma funcionando
- Usar E2E_VALIDATION_REPORT.md como evidencia
- Explicar arquitectura con diagrama en pizarra

---

## 📊 MÉTRICAS A DESTACAR

### Estado Actual
- **18 contenedores** operativos
- **8 microservicios** backend
- **10 páginas** frontend (6 públicas + 4 protegidas)
- **3 roles** implementados con RBAC
- **105 tests** unitarios pasando
- **13/13 targets** monitoreados en Prometheus

### Objetivo Comercial (MVP - 27 Marzo 2026)
| Métrica | Actual | Objetivo | Incremento |
|---------|-------:|--------:|------------|
| Leads/mes | ~20 | 300+ | 15x |
| Tiempo respuesta | 4-24h | < 1 min | 1440x |
| Conversión | ~5% | 15%+ | 3x |
| Clientes nuevos/año | ~15 | 100+ | 6.7x |

---

## 📝 POST-DEMO

### Acciones Inmediatas

1. **Documentar feedback** recibido en `DEMO_FEEDBACK.md`
2. **Priorizar features** para próximas 2 semanas
3. **Actualizar roadmap** con input de abogados
4. **Merge PR #32** a main (si no se hizo antes)

### Siguientes Pasos Técnicos

1. **Completar integración n8n** (MW#1)
2. **Deploy a staging** en GCP Cloud Run
3. **Testing con usuarios reales** (1-2 abogados piloto)
4. **Ajustes basados en feedback**

---

**Preparado por**: Desarrollo  
**Fecha de Preparación**: 14 de Enero, 2026  
**Última Revisión**: Pre-demo
