# 🚀 PROMPT DE CONTINUACIÓN - Desarrollo de Microservicios

**Fecha**: 19 de Diciembre, 2025  
**Rama**: `dev`  
**Último Commit**: `b7557b0` - docs: integrate marketing automation strategy with n8n workflows

---

## CONTEXTO PARA EL NUEVO CHAT

Copia y pega este bloque en un nuevo chat de GitHub Copilot:

---

### 📋 PROMPT:

```
Soy Alexis, desarrollador del proyecto Carrillo Abogados Legal Tech Platform.

## ESTADO ACTUAL DEL PROYECTO

Plataforma cloud-native de gestión legal con 8 microservicios Spring Boot sobre Docker/Kubernetes para el bufete Carrillo ABGD SAS de Cali, Colombia.

### Microservicios y Estado de Implementación

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| api-gateway | 8080 | ✅ 95% | Spring Cloud Gateway + OAuth2 |
| client-service | 8200 | ✅ 90% | CRUD clientes, falta Lead entity |
| case-service | 8300 | ✅ 95% | CRUD casos legales completo |
| payment-service | 8400 | ⏳ 5% | Solo skeleton |
| document-service | 8500 | ⏳ 5% | Solo skeleton |
| calendar-service | 8600 | ⏳ 5% | Solo skeleton |
| notification-service | 8700 | ⏳ 5% | Solo skeleton |
| n8n-integration-service | 8800 | ⏳ 15% | Bridge NATS↔n8n pendiente |

### Docker Compose: ✅ 10/10 contenedores HEALTHY

### Documentación Completada

La documentación de negocio está en `docs/business/`:
- MODELO_NEGOCIO.md - Contexto del bufete, 5 áreas de práctica, métricas marketing
- REQUERIMIENTOS.md - 76 RF + 23 RNF (incluyendo 12 RF-N8N nuevos)
- ESTRATEGIA_AUTOMATIZACION.md - Integración con n8n Cloud (3 MEGA-WORKFLOWS)
- ARQUITECTURA_FUNCIONAL.md - Mapeo microservicio → función de negocio
- ROLES_USUARIOS.md - 4 roles: Admin, Abogado, Cliente, Visitante
- CASOS_USO.md - Flujos detallados por actor

### Integración n8n (Marketing Automation)

El sistema se integra con n8n Cloud para automatizar marketing:
- MW#1: Captura (Lead → Cliente en < 1 min) - 7 workflows, 108 nodos - 28% implementado
- MW#2: Retención (Flywheel) - 5 workflows, 72 nodos - Q2 2026
- MW#3: SEO (Content Factory) - 5 workflows, 60 nodos - Q2-Q3 2026

La documentación detallada de los workflows está en `docs/business/Analizar_Ya/`:
- 00_ARQUITECTURA_GENERAL.md
- 01_MEGA_WORKFLOW_1_CAPTURA.md
- 02_MEGA_WORKFLOW_2_RETENCION.md
- 03_MEGA_WORKFLOW_3_SEO.md

### Métricas Objetivo

| Métrica | Actual | Objetivo |
|---------|--------|----------|
| Leads/mes | 20 | 300+ |
| Tiempo respuesta | 4-24h | < 1 min |
| Conversión | ~5% | 15%+ |
| Clientes nuevos/año | ~15 | 100+ |

## SIGUIENTE FASE: DESARROLLO DE FUNCIONALIDADES

Necesito implementar las funcionalidades core de los microservicios. Las prioridades son:

### PRIORIDAD 1 - Lead Capture (client-service + n8n-integration-service)
1. Crear entidad `Lead` en client-service con campos para scoring
2. Endpoint `POST /leads` para captura desde formulario web
3. Emitir evento `lead.capturado` a NATS
4. n8n-integration-service: escuchar NATS y enviar a webhook n8n
5. Webhook `POST /webhook/lead-scored` para recibir score de n8n

Campos del Lead (según ESTRATEGIA_AUTOMATIZACION.md):
- nombre, email, telefono, empresa, cargo
- servicio (área de interés: TRADEMARK_LAW, etc.)
- mensaje (texto libre)
- leadScore (0-100, calculado por n8n)
- leadCategory (HOT ≥70, WARM 40-69, COLD <40)
- leadStatus (NUEVO, NURTURING, MQL, SQL, CONVERTIDO, CHURNED)
- emailsSent, emailsOpened, emailsClicked
- lastEngagement, source

### PRIORIDAD 2 - Calendar + Booking (calendar-service)
1. Entidad `CalendarEvent` con tipos: HEARING, DEADLINE, MEETING, APPOINTMENT
2. Integración Google Calendar API (OAuth2)
3. Sistema de booking online para citas
4. Integración con Calendly (webhook)

### PRIORIDAD 3 - Notificaciones (notification-service)
1. Entidad `Notification` con estados y canales
2. Templates de email configurables
3. Integración Gmail API para envío
4. Escuchar eventos NATS para auto-envío

### Convenciones del Proyecto

- Java 21 + Spring Boot 3.3.13
- Package: `com.carrilloabogados.<service>`
- Estructura: controller/, service/, repository/, model/, dto/, config/
- PostgreSQL con schemas separados por servicio (schema `clients` para client-service)
- NATS para eventos: `carrillo.events.<domain>.<action>`
- Context-path por servicio: `/<service-name>/`
- Flyway deshabilitado, usar `ddl-auto: update`

## MI PREGUNTA

¿Por dónde empezamos? Sugiero iniciar con la entidad Lead en client-service ya que es crítica para el MW#1 de captura de leads. ¿Puedes ayudarme a implementar:
1. La entidad Lead con todos los campos necesarios para scoring
2. El DTO LeadRequest/LeadResponse y el endpoint POST /leads  
3. El servicio y repositorio correspondientes
4. La emisión del evento a NATS cuando se capture un lead

Lee los archivos CLAUDE.md y docs/business/ESTRATEGIA_AUTOMATIZACION.md para el contexto completo.
```

---

## ARCHIVOS CLAVE PARA LEER AL INICIAR

El agente debe leer estos archivos para contexto:

1. `CLAUDE.md` - Contexto técnico completo
2. `.github/copilot-instructions.md` - Instrucciones de desarrollo
3. `docs/business/ESTRATEGIA_AUTOMATIZACION.md` - Integración n8n
4. `docs/business/ARQUITECTURA_FUNCIONAL.md` - Funciones por microservicio
5. `docs/business/REQUERIMIENTOS.md` - RF-CLI, RF-N8N
6. `client-service/src/main/java/com/carrilloabogados/clientservice/model/` - Modelos existentes
7. `client-service/src/main/resources/application.yaml` - Configuración actual

---

## DECISIONES TÉCNICAS YA TOMADAS

| Decisión | Valor |
|----------|-------|
| Base de datos | PostgreSQL 16 con schemas separados |
| Messaging | NATS (dev/staging), Google Pub/Sub (prod) |
| OAuth2 | Google Workspace (@carrilloabgd.com) |
| Contenedores | Docker Compose local, Kubernetes prod |
| Flyway | Deshabilitado temporalmente (usar `ddl-auto: update`) |
| Service Discovery | Kubernetes DNS nativo |
| Java | 21 LTS |
| Spring Boot | 3.3.13 |

---

## PRÓXIMOS COMMITS ESPERADOS

1. `feat(client-service): add Lead entity with scoring fields`
2. `feat(client-service): add lead capture endpoint POST /leads`
3. `feat(client-service): emit lead.capturado event to NATS`
4. `feat(n8n-integration): add NATS listener and n8n webhook bridge`
5. `feat(n8n-integration): add /webhook/lead-scored endpoint`
6. `feat(calendar-service): add CalendarEvent entity and Google Calendar integration`
7. `feat(notification-service): add Notification entity and Gmail API integration`

---

## COMANDOS ÚTILES

```powershell
# Levantar ambiente local
docker-compose up -d

# Ver logs de un servicio
docker logs carrillo-client-service --tail 50 -f

# Probar endpoint via Gateway
Invoke-RestMethod http://localhost:8080/client-service/actuator/health

# Build de un servicio
.\mvnw package -DskipTests -pl client-service

# Reconstruir y reiniciar servicio
docker-compose up -d --build client-service
```

---

*Generado automáticamente el 19 de Diciembre, 2025*
