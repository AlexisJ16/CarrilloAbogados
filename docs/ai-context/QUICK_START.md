# 🚀 AI CONTEXT - Carrillo Abogados Legal Tech

> **Propósito**: Archivo de contexto rápido para iniciar nuevas sesiones de IA.
> **Última actualización**: 30 de Diciembre, 2025

---

## 📌 RESUMEN EJECUTIVO

**Proyecto**: Plataforma legal cloud-native para bufete Carrillo Abogados (Cali, Colombia)
**Stack**: Java 21 + Spring Boot 3.3.13 + PostgreSQL 16 + Kubernetes
**Estado**: Fase 1 completa (client-service 100%, case-service 95%)
**MVP Target**: 27 Marzo 2026

---

## 🏗️ ARQUITECTURA EN 30 SEGUNDOS

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (8080)                       │
│                    Spring Cloud Gateway                      │
└─────────────────────────┬───────────────────────────────────┘
                          │
     ┌────────────────────┼────────────────────┐
     │                    │                    │
┌────┴────┐  ┌────────────┴───────┐  ┌────────┴────────┐
│ client  │  │     case           │  │   payment       │
│ service │  │    service         │  │   service       │
│  8200   │  │     8300           │  │    8400         │
│  ✅100% │  │     ✅95%          │  │    🔄15%        │
└─────────┘  └────────────────────┘  └─────────────────┘

┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────────────┐
│document │  │calendar │  │ notif.  │  │ n8n-integration │
│ service │  │ service │  │ service │  │    service      │
│  8500   │  │  8600   │  │  8700   │  │     8800        │
│  🔄15%  │  │  🔄15%  │  │  🔄15%  │  │     🔄20%       │
└─────────┘  └─────────┘  └─────────┘  └─────────────────┘

      │              │             │
      └──────────────┴─────────────┘
                     │
            ┌────────┴────────┐
            │   PostgreSQL    │    ┌───────────┐
            │   (schemas)     │    │   NATS    │
            │   clients       │    │  (events) │
            │   cases         │    └───────────┘
            │   payments...   │
            └─────────────────┘
```

---

## ⚡ COMANDOS RÁPIDOS

### Desarrollo Local (Docker Compose)
```powershell
docker-compose up -d          # Levantar todo
docker-compose ps             # Ver estado
docker-compose logs -f client-service  # Logs
```

### Build
```powershell
.\mvnw clean package -DskipTests -T 1C   # Build rápido
.\mvnw test -pl client-service            # Tests de un servicio
```

### Kubernetes (via WSL)
```powershell
wsl bash -c "kubectl get pods -n carrillo-dev"
wsl bash -c "minikube start"
```

---

## 📁 ARCHIVOS CLAVE

| Archivo | Propósito |
|---------|-----------|
| `CLAUDE.md` | Contexto completo para Claude AI |
| `PROYECTO_ESTADO.md` | Estado actual y próximos pasos |
| `.github/copilot-instructions.md` | Instrucciones principales |
| `.github/copilot-agents/` | Agentes especializados |
| `docs/business/REQUERIMIENTOS.md` | RF y RNF completos |

---

## 🎯 PRÓXIMOS PASOS PRIORITARIOS

1. **Frontend**: Iniciar proyecto Next.js en `/frontend`
2. **calendar-service**: Implementar integración Google Calendar
3. **notification-service**: Configurar envío de emails
4. **n8n-integration**: Completar bridge para automatizaciones

---

## 🤖 AGENTES DISPONIBLES

| Agente | Uso |
|--------|-----|
| `backend-agent` | Spring Boot, microservicios, API REST |
| `frontend-agent` | React, Next.js, TypeScript, Tailwind |
| `devops-agent` | Docker, Kubernetes, CI/CD |
| `testing-agent` | Tests unitarios, integración, seguridad |
| `documentation-agent` | Actualizar docs del proyecto |
| `project-manager-agent` | Estado, priorización, métricas |

---

## ⚠️ REGLAS CRÍTICAS

1. **WSL**: Todos los comandos K8s van via `wsl bash -c "..."`
2. **Jackson**: SIEMPRE crear `JacksonConfig.java` con `JavaTimeModule`
3. **NATS**: Usar `@Nullable` en constructor de connection
4. **Tests**: Usar profile `test` con schema H2: `INIT=CREATE SCHEMA IF NOT EXISTS...`

---

*Para contexto completo, ver `CLAUDE.md` o `docs/business/ARQUITECTURA_FUNCIONAL.md`*
