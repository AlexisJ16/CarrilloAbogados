# 🛠️ HERRAMIENTAS Y MEJORES PRÁCTICAS - Desarrollo con AI Agents

**Fecha**: 19 de Diciembre, 2025  
**Propósito**: Guía para optimizar el trabajo con GitHub Copilot y otras herramientas de desarrollo

---

## 📋 ÍNDICE

1. [Optimización de GitHub Copilot](#-optimización-de-github-copilot)
2. [MCPs (Model Context Protocol) Recomendados](#-mcps-recomendados)
3. [Extensiones de VS Code](#-extensiones-de-vs-code)
4. [Plataformas de Integración](#-plataformas-de-integración)
5. [Herramientas de Calidad](#-herramientas-de-calidad)
6. [Herramientas de Seguridad](#-herramientas-de-seguridad)
7. [Mejores Prácticas con Agentes AI](#-mejores-prácticas-con-agentes-ai)

---

## 🤖 OPTIMIZACIÓN DE GITHUB COPILOT

### Configuración Óptima del Workspace

#### 1. Archivos de Instrucciones (Ya configurados ✅)

```
.github/
└── copilot-instructions.md    # Instrucciones específicas del proyecto
```

**Beneficios:**
- Copilot lee automáticamente este archivo
- Proporciona contexto específico del proyecto
- Define convenciones y patrones a seguir

#### 2. Estructura de Documentación para Contexto

```
PROYECTO_ESTADO.md    # Estado actual (lee primero)
CLAUDE.md             # Contexto técnico completo
COPILOT_PROMPT.md     # Prompt de transición entre chats
docs/                 # Documentación detallada
```

### Estrategias de Trabajo con Copilot

#### A. Planificación con TODO Lists
```
1. Antes de empezar, pide que cree un TODO list
2. Un item a la vez: marca "in-progress" antes de trabajar
3. Marca "completed" inmediatamente después de terminar
4. Esto da visibilidad y evita saltos entre tareas
```

#### B. Contexto Incremental
```
1. Primera petición: proporciona contexto alto nivel
2. Peticiones siguientes: referencia archivos específicos
3. Cada ~10-15 interacciones: resume lo hecho
4. Al saturar: usa COPILOT_PROMPT.md para nuevo chat
```

#### C. Comandos Específicos para Eficiencia
```
"Lee el archivo X antes de hacer cambios"
"Usa el patrón que ya existe en Y"
"Ejecuta los tests después de cada cambio"
"Haz commit con mensaje descriptivo al terminar"
```

### Cuándo Iniciar Nuevo Chat

Señales de que el chat está saturado:
- Respuestas más lentas
- Errores de contexto (olvida decisiones anteriores)
- Repetición innecesaria de información
- ~50-100 mensajes en el chat

---

## 🔌 MCPs RECOMENDADOS

### Actualmente Configurados ✅

| MCP | Uso |
|-----|-----|
| **GitHub** | PRs, Issues, Commits, Branches |
| **Docker** | Contenedores, Imágenes, Compose |
| **Octopus Deploy** | Deployments (para futuro) |

### MCPs Adicionales Recomendados

#### 1. **Database MCP** (Para PostgreSQL)
```json
{
  "name": "postgres",
  "description": "Ejecutar queries, ver esquemas, gestionar migraciones"
}
```
**Beneficio**: El agente puede explorar la BD directamente

#### 2. **Kubernetes MCP**
```json
{
  "name": "kubernetes",
  "description": "kubectl commands, pod logs, deployments"
}
```
**Beneficio**: Gestión directa de K8s sin necesidad de terminal

#### 3. **Jira/Linear MCP** (Gestión de Proyectos)
```json
{
  "name": "jira",
  "description": "Crear issues, actualizar estados, sprint planning"
}
```
**Beneficio**: Sincronizar desarrollo con gestión de proyecto

#### 4. **Slack/Discord MCP** (Comunicación)
```json
{
  "name": "slack",
  "description": "Notificaciones de deployments, alertas"
}
```

---

## 🧩 EXTENSIONES DE VS CODE

### Esenciales (Ya configuradas ✅)

```json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vmware.vscode-spring-boot",
    "vscjava.vscode-maven",
    "ms-azuretools.vscode-docker",
    "ms-kubernetes-tools.vscode-kubernetes-tools",
    "redhat.vscode-yaml",
    "humao.rest-client",
    "github.copilot",
    "github.copilot-chat"
  ]
}
```

### Recomendadas Adicionales

#### Productividad
| Extensión | Propósito |
|-----------|-----------|
| `GitLens` | Visualización avanzada de Git |
| `Error Lens` | Errores inline en el código |
| `Todo Tree` | Visualizar TODOs en sidebar |
| `Path Intellisense` | Autocompletado de rutas |
| `Bracket Pair Colorizer` | Colores para brackets |

#### Calidad de Código
| Extensión | Propósito |
|-----------|-----------|
| `SonarLint` | Análisis de código en tiempo real |
| `Checkstyle` | Validación de estilo Java |
| `SpotBugs` | Detección de bugs |

#### Testing
| Extensión | Propósito |
|-----------|-----------|
| `Test Runner for Java` | Ejecutar tests desde IDE |
| `Coverage Gutters` | Visualizar cobertura |

#### Kubernetes/Docker
| Extensión | Propósito |
|-----------|-----------|
| `Kubernetes` | Gestión de clusters |
| `Docker` | Gestión de contenedores |
| `Helm Intellisense` | Autocompletado Helm |

---

## 🌐 PLATAFORMAS DE INTEGRACIÓN

### 1. **Snyk** - Seguridad de Dependencias
```
URL: snyk.io
Integración: GitHub, CI/CD
Costo: Gratis para open source
```
**Beneficios:**
- Escaneo automático de vulnerabilidades
- PRs con fixes sugeridos
- Monitoreo continuo

### 2. **SonarCloud** - Calidad de Código
```
URL: sonarcloud.io
Integración: GitHub Actions
Costo: Gratis para open source
```
**Beneficios:**
- Análisis estático completo
- Code smells, bugs, vulnerabilidades
- Quality gates en PRs

### 3. **Codecov** - Cobertura de Tests
```
URL: codecov.io
Integración: GitHub Actions
Costo: Gratis para open source
```
**Beneficios:**
- Reportes de cobertura
- Visualización de cambios
- Bloqueo de PRs si baja cobertura

### 4. **Dependabot** - Actualizaciones Automáticas
```
Integración: GitHub nativo
Costo: Gratis
```
**Beneficios:**
- PRs automáticos para actualizaciones
- Alertas de seguridad
- Agrupación de updates

### 5. **Renovate** (Alternativa a Dependabot)
```
URL: renovatebot.com
Integración: GitHub App
Costo: Gratis
```
**Beneficios:**
- Más configurable que Dependabot
- Agrupa actualizaciones
- Mejor para monorepos

---

## 🔒 HERRAMIENTAS DE SEGURIDAD

### Para el Proyecto Actual

#### 1. **OWASP Dependency-Check**
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.9</version>
</plugin>
```

#### 2. **SpotBugs con Find Security Bugs**
```xml
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <configuration>
        <plugins>
            <plugin>
                <groupId>com.h3xstream.findsecbugs</groupId>
                <artifactId>findsecbugs-plugin</artifactId>
            </plugin>
        </plugins>
    </configuration>
</plugin>
```

#### 3. **Trivy** (Container Scanning)
```yaml
# En CI/CD
- name: Scan Container
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: 'carrillo/client-service:latest'
    format: 'sarif'
```

### Integración en CI/CD

```yaml
# .github/workflows/security-scan.yml
name: Security Scan
on: [push, pull_request]
jobs:
  security:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: OWASP Dependency Check
        run: ./mvnw dependency-check:check
      - name: Snyk Scan
        uses: snyk/actions/maven@master
      - name: Trivy Container Scan
        uses: aquasecurity/trivy-action@master
```

---

## 📊 HERRAMIENTAS DE CALIDAD

### 1. **JaCoCo** (Cobertura - Ya configurado)
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

### 2. **Checkstyle** (Estilo de código)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
</plugin>
```

### 3. **PMD** (Análisis estático)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
</plugin>
```

### 4. **Mutation Testing** (PIT)
```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
</plugin>
```

---

## 💡 MEJORES PRÁCTICAS CON AGENTES AI

### 1. Preparación del Contexto

```markdown
✅ Mantener archivos de contexto actualizados:
   - PROYECTO_ESTADO.md (después de cada sesión)
   - CLAUDE.md (cuando cambian decisiones arquitectónicas)
   - copilot-instructions.md (cuando cambian convenciones)

✅ Estructurar las peticiones:
   - Contexto → Tarea → Criterios de éxito
   - "Dado X, quiero Y, para lograr Z"

✅ Usar el TODO list de Copilot:
   - Planificar antes de ejecutar
   - Un item a la vez
   - Marcar completado inmediatamente
```

### 2. Durante el Desarrollo

```markdown
✅ Validar cada cambio:
   - "Ejecuta los tests después de este cambio"
   - "Verifica que no hay errores de compilación"

✅ Commits frecuentes:
   - "Haz commit con mensaje descriptivo"
   - Conventional commits: feat, fix, docs, etc.

✅ Documentar mientras se desarrolla:
   - "Actualiza el README con estos cambios"
   - "Añade comentarios JSDoc/JavaDoc"
```

### 3. Transición entre Chats

```markdown
✅ Antes de cerrar:
   - Actualizar PROYECTO_ESTADO.md
   - Hacer push de todos los cambios
   - Documentar lecciones aprendidas

✅ Al iniciar nuevo chat:
   - Copiar prompt de COPILOT_PROMPT.md
   - Pedir que lea archivos de contexto
   - Resumir objetivos de la sesión
```

### 4. Patrones de Comunicación Efectiva

```markdown
❌ Evitar:
   - Peticiones vagas: "mejora el código"
   - Múltiples tareas en una petición
   - Omitir contexto importante

✅ Preferir:
   - "En client-service, añade validación de email usando @Email de Jakarta"
   - "Sigue el patrón que usamos en InputValidationSecurityTest"
   - "Ejecuta solo los tests de seguridad: -Dtest=*SecurityTest"
```

---

## 🚀 ROADMAP DE INTEGRACIÓN

### Fase 1: Inmediato (Esta semana)
- [x] GitHub Copilot optimizado
- [x] CI/CD con GitHub Actions
- [ ] SonarCloud para calidad
- [ ] Snyk para seguridad

### Fase 2: Corto plazo (Enero 2026)
- [ ] Codecov para cobertura
- [ ] Dependabot activado
- [ ] Database MCP configurado

### Fase 3: Mediano plazo (Febrero 2026)
- [ ] Kubernetes MCP
- [ ] Monitoring con Grafana Cloud
- [ ] Alertas con PagerDuty/Opsgenie

---

## 📞 RECURSOS ADICIONALES

### Documentación
- [GitHub Copilot Docs](https://docs.github.com/copilot)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Kubernetes Docs](https://kubernetes.io/docs/home/)

### Comunidades
- [Spring Community](https://spring.io/community)
- [CNCF Slack](https://slack.cncf.io/)
- [GitHub Community](https://github.community/)

---

*Documento de mejores prácticas - 19 de Diciembre 2025*
