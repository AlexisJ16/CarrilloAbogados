# 🤖 Copilot Agents Index - Carrillo Abogados

## Agentes Disponibles

Este directorio contiene **agentes especializados** para diferentes áreas del proyecto. Cada agente tiene conocimiento profundo de su dominio y puede ser invocado según la tarea.

---

## 📋 Lista de Agentes

### 1. [Backend Agent](./backend-agent.md)
**Especialización**: Spring Boot, Microservicios, Java 21
- Desarrollo de servicios REST
- Patrones de código
- Configuraciones Spring
- Tests unitarios e integración

### 2. [Frontend Agent](./frontend-agent.md)
**Especialización**: React, Next.js, TypeScript, Tailwind
- Desarrollo UI/UX
- Integración con APIs
- Componentes reutilizables
- Estado y formularios

### 3. [DevOps Agent](./devops-agent.md)
**Especialización**: Docker, Kubernetes, CI/CD
- Configuración de contenedores
- Helm charts
- GitHub Actions
- Troubleshooting de infraestructura

### 4. [Testing Agent](./testing-agent.md)
**Especialización**: Tests exhaustivos y seguridad
- Tests unitarios (JUnit 5)
- Tests de integración (MockMvc)
- Tests de seguridad (OWASP)
- Cobertura de código

### 5. [Documentation Agent](./documentation-agent.md) ⭐
**Especialización**: Gestión integral de documentación + Auditoría
- Auditar toda la documentación del repositorio
- Control absoluto de archivos .md y textos
- Detectar documentos obsoletos, duplicados o inconsistentes
- Actualizar PROYECTO_ESTADO.md, CLAUDE.md, copilot-instructions.md
- Mantener consistencia y calidad documental
- Limpiar documentación basura

### 6. [Project Manager Agent](./project-manager-agent.md)
**Especialización**: Gestión y tracking
- Estado del proyecto
- Priorización de tareas
- Timeline y milestones
- Métricas de progreso

### 7. [QA & Quality Agent](./qa-quality-agent.md)
**Especialización**: Control de calidad y depuración
- Análisis profundo del proyecto
- Depuración y limpieza de código
- Validación E2E de Docker y APIs
- Monitoreo de Snyk, SonarCloud y VSCode
- Asegurar cero errores en herramientas de calidad
- Tests unitarios e integración

### 8. [Business Product Agent](./business-product-agent.md)
**Especialización**: Investigación, Gerencia de Producto y Planificación de Negocio
- Gestión completa de la carpeta `docs/business/`
- Definición de requerimientos y prioridades del MVP
- Investigación del contexto legal colombiano
- Traducción de necesidades de negocio a requerimientos técnicos
- Coordinación de estrategia Flywheel + n8n
- Roadmap del producto y timeline
- Definición de los 4 tipos de usuario y sus funcionalidades

### 9. [Branch Sync Agent](./branch-sync-agent.md) 🆕
**Especialización**: Sincronización de ramas y gestión de integración
- Mantener sincronizadas las ramas main, dev y automation
- Integrar cambios de automation a dev de forma segura
- Revisar y resolver conflictos de merge
- Mantener historial de git limpio y ordenado
- Coordinación entre equipo de desarrollo y marketing

---

## 🎯 Cómo Usar los Agentes

### Opción 1: Referenciar en el Chat
```
@workspace Usando el conocimiento del backend-agent, 
crea un nuevo endpoint para gestión de audiencias en calendar-service
```

### Opción 2: Contextualizar la Tarea
```
Estoy trabajando en frontend. Necesito crear un formulario de contacto 
que se integre con la Lead API del backend.
```

### Opción 3: Pedir Actualización de Documentación
```
Acabo de completar la implementación de calendar-service.
Actualiza la documentación del proyecto usando el documentation-agent.
```

---

## 📁 Estructura de Archivos

```
.github/
├── copilot-instructions.md       # Instrucciones principales de Copilot
├── copilot-agents/
│   ├── INDEX.md                  # Este archivo
│   ├── backend-agent.md          # Desarrollo Spring Boot
│   ├── frontend-agent.md         # Desarrollo React/Next.js
│   ├── devops-agent.md           # Docker, K8s, CI/CD
│   ├── testing-agent.md          # Tests y seguridad
│   ├── documentation-agent.md    # Mantenimiento de docs
│   ├── project-manager-agent.md  # Gestión de proyecto
│   ├── qa-quality-agent.md       # Control de calidad
│   ├── business-product-agent.md # Producto y negocio
│   └── branch-sync-agent.md      # Sincronización de ramas 🆕
└── workflows/                    # GitHub Actions
```

---

## 🔄 Mantenimiento

Estos agentes deben actualizarse cuando:

1. **Cambia la arquitectura** → Actualizar backend-agent, devops-agent
2. **Nuevos endpoints API** → Actualizar frontend-agent
3. **Nuevas convenciones de código** → Actualizar todos los agentes relevantes
4. **Nuevas herramientas** → Actualizar devops-agent

---

*Última actualización: 11 de Enero, 2026 - FASE 10: Autenticación Frontend Completa*
