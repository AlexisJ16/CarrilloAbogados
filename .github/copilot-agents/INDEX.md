# 🤖 Copilot Agents - Carrillo Abogados

**Última Actualización**: 12 de Enero, 2026 - 11:00 COT  
**Versión**: 3.0

---

## 📋 Resumen

Este directorio contiene **4 agentes especializados** con conocimiento profundo del proyecto. Cada agente se enfoca en un dominio específico para proporcionar asistencia precisa y eficiente.

---

## 🎯 Agentes Disponibles

| Agente | Archivo | Especialización |
|--------|---------|-----------------|
| **Backend** | [backend-agent.md](./backend-agent.md) | Java 21, Spring Boot, APIs REST, JPA |
| **Frontend** | [frontend-agent.md](./frontend-agent.md) | Next.js 14, React, TypeScript, Tailwind |
| **DevOps** | [devops-agent.md](./devops-agent.md) | Docker, Kubernetes, CI/CD, WSL |
| **Documentation** | [documentation-agent.md](./documentation-agent.md) | Docs, timestamps, auditoría |

---

## 🔄 Cuándo Usar Cada Agente

### Por Tipo de Archivo

| Archivos | Agente |
|----------|--------|
| `*.java`, `pom.xml`, `*-service/` | Backend Agent |
| `*.tsx`, `*.ts`, `frontend/` | Frontend Agent |
| `Dockerfile`, `*.yml`, `helm-charts/`, `.github/workflows/` | DevOps Agent |
| `*.md`, `docs/`, `README` | Documentation Agent |

### Por Tipo de Tarea

| Tarea | Agente |
|-------|--------|
| Crear endpoint REST, validaciones, JPA | Backend |
| Componentes UI, hooks, formularios | Frontend |
| Deploy, troubleshooting K8s, Docker | DevOps |
| Actualizar docs, crear ADRs | Documentation |

---

## 💡 Cómo Invocar un Agente

### Opción 1: Referencia Explícita
```
"Siguiendo backend-agent, implementa el endpoint de pagos..."
"Actúa como devops-agent y resuelve el error de pod..."
```

### Opción 2: Adjuntar el Archivo
Adjunta el agente como contexto en tu chat de Copilot.

### Opción 3: Detección Automática
El AI debería detectar automáticamente el agente apropiado basándose en el contexto de la tarea y los archivos involucrados.

---

## 📁 Estructura

```
.github/copilot-agents/
├── INDEX.md                  # Este archivo
├── backend-agent.md          # Java/Spring Boot (~450 líneas)
├── frontend-agent.md         # Next.js/React (~500 líneas)
├── devops-agent.md           # Docker/K8s (~500 líneas)
├── documentation-agent.md    # Docs/Trazabilidad (~400 líneas)
└── archive/                  # Agentes deprecados (no usar)
```

---

## ⚡ Contenido Clave por Agente

### Backend Agent
- ✅ 5 patrones obligatorios con código completo
- ✅ Mapa de microservicios (puertos, schemas, estado)
- ✅ Convenciones de naming
- ✅ Estructura de paquetes estándar
- ✅ Patrones de testing (Mockito, MockMvc)

### Frontend Agent
- ✅ Tipos TypeScript sincronizados con backend
- ✅ API client con manejo de auth
- ✅ Patrones React Query (hooks)
- ✅ Formularios con React Hook Form + Zod
- ✅ Sistema de diseño (CSS variables, Tailwind)

### DevOps Agent
- ⚠️ **CRÍTICO**: Comandos K8s vía WSL obligatorio
- ✅ Arquitectura Docker Compose completa
- ✅ Comandos Helm y kubectl
- ✅ Stack de observabilidad (Grafana LGTM)
- ✅ Troubleshooting común

### Documentation Agent
- 🕐 **Sistema de timestamps obligatorio**
- ✅ Formato de header estándar
- ✅ Plantillas (ADR, README, Documento técnico)
- ✅ Flujos de actualización
- ✅ Checklists de auditoría

---

## 📚 Regla Fundamental

> **"Documentación sin fecha es documentación sin valor"**

Todo archivo `.md` modificado debe incluir:
```markdown
**Última Actualización**: DD de Mes, AAAA - HH:MM COT
```

---

## 🗄️ Agentes Archivados

La carpeta `archive/` contiene agentes consolidados o deprecados. **No usar** - su contenido fue integrado en los 4 agentes principales.

---

*Índice actualizado: 12 de Enero 2026, 11:00 COT*
