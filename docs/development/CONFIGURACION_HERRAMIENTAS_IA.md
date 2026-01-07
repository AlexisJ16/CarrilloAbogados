# 🤖 GUÍA COMPLETA: Configuración Óptima de Herramientas IA para Desarrollo

**Fecha**: 7 de Enero, 2026  
**Proyecto**: Carrillo Abogados Legal Tech Platform  
**Audiencia**: Desarrollador Full-Stack con Claude Pro

---

## 📋 TABLA DE CONTENIDOS

1. [Análisis de Modelos de IA](#análisis-de-modelos-de-ia)
2. [Configuración Recomendada para GitHub Copilot](#configuración-github-copilot)
3. [Herramientas Gratuitas Esenciales](#herramientas-gratuitas)
4. [Herramientas Pagas que Vale la Pena Considerar](#herramientas-pagas)
5. [Configuración Óptima de VSCode](#configuración-vscode)
6. [Workflow Recomendado](#workflow-recomendado)

---

## 🎯 1. ANÁLISIS DE MODELOS DE IA

### Comparativa de Modelos para Programación

| Modelo | Programación | Razonamiento | Contexto | Velocidad | Costo | Recomendación |
|--------|--------------|--------------|----------|-----------|-------|---------------|
| **Claude Sonnet 4.5** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 200K tokens | Media | Alto | **✅ MEJOR OPCIÓN** |
| **Claude Opus 4.5** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 200K tokens | Lenta | Muy Alto | Tareas complejas |
| **GPT-4.1** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 128K tokens | Rápida | Medio | Alternativa válida |
| **GPT-4o** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 128K tokens | Muy Rápida | Medio | Código repetitivo |
| **Gemini 2.5 Pro** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | 2M tokens | Media | Alto | Proyectos gigantes |
| **Claude Haiku 4.5** | ⭐⭐⭐ | ⭐⭐⭐ | 200K tokens | Muy Rápida | Bajo | Autocomplete rápido |

### 🏆 Recomendación Final: **Claude Sonnet 4.5**

**Por qué Sonnet 4.5 es la mejor opción:**

1. **Balance Perfecto**: Velocidad + Calidad + Costo
2. **Excelente en Java/Spring Boot**: Conoce profundamente el stack que usas
3. **Contexto Masivo (200K)**: Puede analizar múltiples archivos a la vez
4. **Razonamiento Superior**: Mejor para arquitectura y debugging complejo
5. **Menor Latencia que Opus**: Respuestas más rápidas sin sacrificar calidad
6. **Ya lo tienes con Claude Pro**: No requiere suscripción adicional

**Cuándo usar otros modelos:**

- **Opus 4.5**: Solo para decisiones arquitectónicas críticas o refactorización masiva (usa 33% de tus créditos)
- **Haiku 4.5**: Autocomplete en línea, tareas simples, commits git (usa solo 3%)
- **GPT-4o**: Cuando Sonnet esté lento o llegues al límite de rate (backup)
- **Gemini 2.5 Pro**: Nunca para este proyecto (overkill, mejor guardarlo para análisis de datos masivos)

---

## ⚙️ 2. CONFIGURACIÓN GITHUB COPILOT

### Configuración Óptima en VSCode

**Settings.json recomendada:**

```json
{
  // ===== COPILOT CORE =====
  "github.copilot.enable": {
    "*": true,
    "plaintext": false,
    "markdown": true,
    "yaml": true
  },
  
  // ===== MODELO PRINCIPAL =====
  "github.copilot.editor.enableAutoCompletions": true,
  "github.copilot.editor.enableCodeActions": true,
  
  // ===== CHAT =====
  "github.copilot.chat.localeOverride": "es",
  "github.copilot.chat.useProjectTemplates": true,
  
  // ===== ASIGNACIÓN DE MODELOS =====
  // Mantén en "Auto" o configura manualmente:
  // - Claude Sonnet 4.5: Para chat extenso (este archivo de instrucciones)
  // - Claude Haiku 4.5: Para inline suggestions (0x en el dropdown)
  // - GPT-4.1: Backup cuando Sonnet esté ocupado
  
  // ===== PERFORMANCE =====
  "github.copilot.advanced": {
    "debug.overrideEngine": "Claude Sonnet 4.5",
    "debug.testOverrideProxyUrl": "",
    "inlineSuggestCount": 3,
    "listCount": 10
  }
}
```

### ⚠️ Problema Detectado en tus Screenshots

**Issue**: Veo que en tus capturas Copilot muestra "GPT-4.1 • 0x" en lugar de Claude Sonnet 4.5.

**Causa**: GitHub Copilot en modo "Auto" está eligiendo GPT-4.1 porque:
1. Claude Sonnet 4.5 tiene límites de rate más estrictos
2. El sistema detectó alta carga y switcheó a GPT
3. Tu configuración puede estar en "Auto" sin override

**Solución**:

1. **Opción A - Forzar Sonnet 4.5 (Recomendado)**:
   - Click en el ícono de Copilot en la barra inferior de VSCode
   - Click en "Language Models"
   - Selecciona **"Claude Sonnet 4.5"** manualmente
   - Desmarca "Auto" (el switch azul)

2. **Opción B - Mantener Auto pero priorizar Claude**:
   - Mantén "Auto" activado (útil cuando llegues al rate limit)
   - GitHub elegirá el mejor disponible
   - Ventaja: No te quedarás sin servicio si Claude se satura

**Mi recomendación**: **Opción A** para este proyecto. Solo usa Auto si trabajas en múltiples proyectos simultáneos.

---

## 🆓 3. HERRAMIENTAS GRATUITAS ESENCIALES

### Stack Actual (Ya tienes ✅)

1. **VSCode** - Editor principal ✅
2. **GitHub Copilot** - IA asistente ✅
3. **Claude Code CLI** - Terminal IA ✅
4. **Git + GitHub** - Control de versiones ✅
5. **Docker Desktop** - Contenedorización ✅
6. **PostgreSQL** - Base de datos ✅

### Herramientas Gratuitas que DEBES Agregar Ahora

#### 🔥 **CRÍTICAS (Instalar Hoy)**

| Herramienta | Propósito | Instalación | Valor |
|-------------|-----------|-------------|-------|
| **Bruno** | Cliente REST API (alternativa a Postman) | https://www.usebruno.com/ | ⭐⭐⭐⭐⭐ |
| **DBeaver** | Cliente PostgreSQL visual | https://dbeaver.io/ | ⭐⭐⭐⭐⭐ |
| **Lens** | Kubernetes IDE (para GKE) | https://k8slens.dev/ | ⭐⭐⭐⭐⭐ |
| **n8n Desktop** | Testing workflows localmente | https://n8n.io/download | ⭐⭐⭐⭐ |

**Por qué estas 4 son críticas:**

1. **Bruno**: 
   - Guarda colecciones REST en Git (`.bru` files)
   - No requiere login/cloud como Postman
   - Perfecto para probar endpoints de client-service, case-service
   - **Alternativa**: Thunder Client (extensión VSCode)

2. **DBeaver**:
   - Visualizar schemas PostgreSQL fácilmente
   - Ejecutar queries con autocomplete
   - Ver relaciones entre tablas
   - Exportar datos a CSV/JSON
   - **Alternativa**: pgAdmin 4 (más pesada)

3. **Lens**:
   - Ver pods, deployments, services en GKE
   - Logs en tiempo real
   - Shell directo a contenedores
   - Esencial cuando despliegues a producción
   - **Alternativa**: k9s (CLI)

4. **n8n Desktop**:
   - Probar workflows antes de subir a n8n Cloud
   - Depurar MW#1 (Lead Scoring) localmente
   - Ver ejecuciones paso a paso
   - **Alternativa**: n8n Cloud trial (ya lo tienes)

#### 📊 **MUY ÚTILES (Instalar Esta Semana)**

| Herramienta | Propósito | Instalación | Valor |
|-------------|-----------|-------------|-------|
| **k9s** | Terminal UI para Kubernetes | `winget install derailed.k9s` | ⭐⭐⭐⭐ |
| **lazygit** | Git UI en terminal | `winget install jesseduffield.lazygit` | ⭐⭐⭐⭐ |
| **lazydocker** | Docker UI en terminal | `winget install jesseduffield.lazydocker` | ⭐⭐⭐⭐ |
| **HTTPie** | cURL mejorado | `winget install httpie` | ⭐⭐⭐ |
| **jq** | JSON processor | `winget install stedolan.jq` | ⭐⭐⭐⭐ |

**Comandos de instalación (PowerShell):**

```powershell
# Terminal tools
winget install derailed.k9s
winget install jesseduffield.lazygit
winget install jesseduffield.lazydocker
winget install httpie
winget install stedolan.jq

# Desktop tools
winget install DBeaver.DBeaver
winget install Mirantis.Lens
```

#### 🎨 **OPCIONALES (Nice to Have)**

| Herramienta | Propósito | Cuando Instalar | Valor |
|-------------|-----------|-----------------|-------|
| **Figma** | Diseño UI/UX | Si vas a cambiar frontend | ⭐⭐⭐ |
| **Excalidraw** | Diagramas arquitectura | Para documentar ADRs | ⭐⭐⭐⭐ |
| **Obsidian** | Notas técnicas | Ya tienes docs/ en repo | ⭐⭐⭐ |
| **Grafana** | Monitoreo (ya en compose) | Ya lo tienes configurado | ⭐⭐⭐⭐ |

---

## 💳 4. HERRAMIENTAS PAGAS QUE VALE LA PENA

### 🚫 **NO PAGUES POR AHORA**

Estas herramientas son innecesarias en desarrollo:

- ❌ **Postman Teams** - Bruno es suficiente
- ❌ **GitHub Teams** - Free tier es suficiente para 1 desarrollador
- ❌ **DataGrip** - DBeaver Free hace lo mismo
- ❌ **Cursor IDE** - Ya tienes VSCode + Copilot + Claude Pro
- ❌ **Copilot Business** - Tu plan actual es suficiente

### 💰 **CONSIDERA PARA PRODUCCIÓN (Después del MVP)**

| Herramienta | Costo | Cuándo Pagar | Justificación |
|-------------|-------|--------------|---------------|
| **Sentry** | $26/mes | Post-MVP | Error tracking en producción |
| **Vercel Pro** | $20/mes | Si frontend crece | Deploy automático Next.js |
| **Upstash Redis** | $10/mes | Si necesitas cache | Redis serverless |
| **Better Stack** | $18/mes | Post-MVP | Uptime monitoring |
| **n8n Enterprise** | Contactar | Si automatizas mucho | Workflows ilimitados |

**Mi recomendación**: **NO pagues nada más ahora**. Tienes todo lo necesario:
- Claude Pro ($20/mes) ✅
- GitHub Copilot (incluido en tu plan) ✅
- Herramientas gratuitas de arriba

Total actual: **$20/mes** - Perfecto para desarrollo.

---

## 🔧 5. CONFIGURACIÓN ÓPTIMA DE VSCODE

### Extensiones Recomendadas (Gratuitas)

**Instala SOLO estas (evita bloat):**

#### ✅ **ESENCIALES (Ya debes tenerlas)**

```json
{
  "recommendations": [
    // Java/Spring
    "vscjava.vscode-java-pack",
    "vscjava.vscode-spring-initializr",
    "vmware.vscode-spring-boot",
    
    // Docker/Kubernetes
    "ms-azuretools.vscode-docker",
    "ms-kubernetes-tools.vscode-kubernetes-tools",
    
    // Git
    "eamodio.gitlens",
    
    // AI
    "github.copilot",
    "github.copilot-chat",
    
    // Frontend
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "bradlc.vscode-tailwindcss",
    
    // Utilities
    "humao.rest-client",
    "redhat.vscode-yaml",
    "ms-vscode.powershell"
  ]
}
```

#### 🆕 **AGREGAR (Mejoran productividad)**

```bash
# REST API Testing (alternativa a Bruno)
code --install-extension rangav.vscode-thunder-client

# Database visualizer
code --install-extension cweijan.vscode-database-client2

# Error Lens (ver errores inline)
code --install-extension usernamehw.errorlens

# Git Graph (ver commits visualmente)
code --install-extension mhutchie.git-graph

# TODO Highlights
code --install-extension wayou.vscode-todo-highlight

# Markdown Preview Enhanced
code --install-extension shd101wyy.markdown-preview-enhanced
```

### Settings.json Completa Recomendada

```json
{
  // ===== EDITOR =====
  "editor.fontSize": 14,
  "editor.fontFamily": "'Cascadia Code', 'Fira Code', Consolas, monospace",
  "editor.fontLigatures": true,
  "editor.formatOnSave": true,
  "editor.formatOnPaste": false,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true
  },
  "editor.inlineSuggest.enabled": true,
  "editor.suggestSelection": "first",
  "editor.tabSize": 2,
  "editor.detectIndentation": true,
  "editor.minimap.enabled": false,
  "editor.renderWhitespace": "trailing",
  
  // ===== COPILOT =====
  "github.copilot.enable": {
    "*": true,
    "plaintext": false,
    "markdown": true
  },
  "github.copilot.editor.enableAutoCompletions": true,
  "github.copilot.chat.localeOverride": "es",
  
  // ===== FILES =====
  "files.autoSave": "afterDelay",
  "files.autoSaveDelay": 1000,
  "files.trimTrailingWhitespace": true,
  "files.insertFinalNewline": true,
  "files.exclude": {
    "**/node_modules": true,
    "**/target": true,
    "**/.git": true,
    "**/.DS_Store": true
  },
  
  // ===== JAVA =====
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.1.12-hotspot",
      "default": true
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  
  // ===== SPRING BOOT =====
  "spring-boot.ls.java.home": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.1.12-hotspot",
  "spring.initializr.defaultLanguage": "Java",
  
  // ===== TERMINAL =====
  "terminal.integrated.defaultProfile.windows": "PowerShell",
  "terminal.integrated.fontSize": 13,
  "terminal.integrated.cursorBlinking": true,
  "terminal.integrated.copyOnSelection": true,
  
  // ===== GIT =====
  "git.autofetch": true,
  "git.confirmSync": false,
  "git.enableSmartCommit": true,
  "gitlens.currentLine.enabled": false,
  "gitlens.hovers.currentLine.over": "line",
  
  // ===== ESLINT =====
  "eslint.format.enable": true,
  "eslint.lintTask.enable": true,
  
  // ===== PRETTIER =====
  "prettier.singleQuote": true,
  "prettier.semi": true,
  "prettier.trailingComma": "es5",
  
  // ===== TAILWIND =====
  "tailwindCSS.experimental.classRegex": [
    ["cva\\(([^)]*)\\)", "[\"'`]([^\"'`]*).*?[\"'`]"],
    ["cn\\(([^)]*)\\)", "(?:'|\"|`)([^']*)(?:'|\"|`)"]
  ],
  
  // ===== ERROR LENS =====
  "errorLens.enabledDiagnosticLevels": [
    "error",
    "warning"
  ],
  "errorLens.fontSize": "12",
  
  // ===== TODO HIGHLIGHT =====
  "todohighlight.keywords": [
    {
      "text": "TODO:",
      "color": "#ff6b6b",
      "backgroundColor": "transparent"
    },
    {
      "text": "FIXME:",
      "color": "#feca57",
      "backgroundColor": "transparent"
    },
    {
      "text": "NOTE:",
      "color": "#48dbfb",
      "backgroundColor": "transparent"
    }
  ]
}
```

### Keybindings Recomendados (keybindings.json)

```json
[
  // Quick Terminal
  {
    "key": "ctrl+`",
    "command": "workbench.action.terminal.toggleTerminal"
  },
  
  // Copilot Chat
  {
    "key": "ctrl+shift+i",
    "command": "workbench.panel.chat.view.copilot.focus"
  },
  
  // Run Task
  {
    "key": "ctrl+shift+b",
    "command": "workbench.action.tasks.build"
  },
  
  // Git Commit
  {
    "key": "ctrl+alt+c",
    "command": "git.commit"
  },
  
  // Format Document
  {
    "key": "ctrl+shift+f",
    "command": "editor.action.formatDocument"
  }
]
```

---

## 🔄 6. WORKFLOW RECOMENDADO

### Flujo Diario Óptimo

```
┌─────────────────────────────────────────────────────────────────────┐
│                    WORKFLOW DIARIO RECOMENDADO                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  MAÑANA (9:00 - 13:00)                                              │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 1. git pull origin dev                                       │  │
│  │ 2. docker-compose up -d (si cambió algo)                     │  │
│  │ 3. Abrir VSCode → Copilot Chat                               │  │
│  │ 4. Prompt: "Resume el trabajo de ayer y próximos pasos"     │  │
│  │ 5. Revisar TRABAJO_EN_PROGRESO.md                            │  │
│  │ 6. Desarrollo enfocado (Copilot inline)                      │  │
│  │ 7. Tests: ./mvnw test -pl <service>                          │  │
│  │ 8. Commit: git add . && git commit -m "feat: ..."           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  TARDE (14:00 - 18:00)                                              │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 1. Revisar errores: Get-Errors                               │  │
│  │ 2. Debugging con Copilot Chat                                │  │
│  │ 3. Integración E2E                                           │  │
│  │ 4. Documentar cambios en PROYECTO_ESTADO.md                  │  │
│  │ 5. Push: git push origin dev                                 │  │
│  │ 6. Actualizar TRABAJO_EN_PROGRESO.md para mañana             │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Uso Estratégico de Modelos de IA

| Tarea | Modelo | Herramienta | Ejemplo |
|-------|--------|-------------|---------|
| **Código simple** | Haiku 4.5 (0x) | Inline Copilot | Getters/setters, imports |
| **Refactoring** | Sonnet 4.5 (1x) | Copilot Chat | Optimizar método complejo |
| **Arquitectura** | Opus 4.5 (0.33x) | Copilot Chat | Diseñar nuevo microservicio |
| **Debugging** | Sonnet 4.5 (1x) | Copilot Chat | Resolver stack trace |
| **Documentación** | Sonnet 4.5 (1x) | Copilot Chat | Escribir ADR, README |
| **Tests** | Sonnet 4.5 (1x) | Copilot Chat | JUnit + Mockito tests |
| **SQL queries** | Haiku 4.5 (0x) | Inline Copilot | SELECT, INSERT básicos |
| **Git commits** | Haiku 4.5 (0x) | Terminal | Mensajes de commit |

**Regla de oro**: Usa Sonnet 4.5 para el 80% de tareas. Solo sube a Opus cuando estés realmente bloqueado.

### Claude Code CLI - Cuándo Usarlo

**✅ Úsalo para:**
- Tareas batch (renombrar 20 archivos, refactor masivo)
- Análisis de logs largos
- Generación de scripts shell/PowerShell
- Migración de datos
- Debugging cuando VSCode está lento

**❌ No lo uses para:**
- Desarrollo diario (VSCode + Copilot es más rápido)
- Edición interactiva de código
- Debugging con breakpoints

**Ejemplo de uso recomendado:**

```powershell
# Generar script de migración SQL
claude "Crea un script SQL para migrar la tabla clients.users de schema actual a nuevo schema con campo 'role' como enum"

# Analizar logs de error
docker logs carrillo-client-service 2>&1 | claude "Encuentra el error raíz en estos logs"

# Refactor masivo
claude "Renombra todos los archivos *Service.java a *ServiceImpl.java y actualiza imports"
```

---

## 🎓 RESUMEN EJECUTIVO

### ✅ Configuración Óptima AHORA (Gratuita)

1. **Modelo Principal**: **Claude Sonnet 4.5** (forzar en GitHub Copilot)
2. **Instalar Hoy**:
   - Bruno (API testing)
   - DBeaver (PostgreSQL GUI)
   - Lens (Kubernetes IDE)
   - k9s, lazygit, lazydocker (terminal tools)

3. **Extensiones VSCode**:
   - Thunder Client
   - Error Lens
   - Git Graph
   - Database Client

4. **Workflow**:
   - Copilot inline para código simple (Haiku)
   - Copilot Chat para debugging/arquitectura (Sonnet)
   - Claude Code CLI para batch tasks

### 💰 Inversión Actual

- **Claude Pro**: $20/mes ✅
- **GitHub Copilot**: Incluido ✅
- **Total**: $20/mes

**NO necesitas pagar nada más hasta que lances a producción.**

### 🚀 Post-MVP (Marzo 2026)

Entonces considera:
- **Sentry** ($26/mes) - Error tracking
- **Vercel Pro** ($20/mes) - Si frontend escala
- **Better Stack** ($18/mes) - Uptime monitoring

Total post-MVP: ~$84/mes

---

## 🎯 ACCIÓN INMEDIATA

**Pasos para configurar TODO ahora (15 minutos):**

```powershell
# 1. Instalar herramientas CLI
winget install derailed.k9s
winget install jesseduffield.lazygit
winget install jesseduffield.lazydocker
winget install stedolan.jq

# 2. Instalar herramientas Desktop
winget install DBeaver.DBeaver
winget install Mirantis.Lens

# 3. Instalar extensiones VSCode
code --install-extension rangav.vscode-thunder-client
code --install-extension usernamehw.errorlens
code --install-extension mhutchie.git-graph
code --install-extension wayou.vscode-todo-highlight

# 4. Configurar Copilot
# - Abrir VSCode
# - Click en ícono Copilot (barra inferior)
# - Language Models → Seleccionar "Claude Sonnet 4.5"
# - Desmarcar "Auto"

# 5. Copiar settings.json recomendado
# (del archivo anterior en esta guía)
```

**Después de esto, estás 100% configurado profesionalmente. ✅**

---

## 📚 RECURSOS ADICIONALES

- **Bruno Docs**: https://docs.usebruno.com/
- **DBeaver Docs**: https://dbeaver.com/docs/
- **Lens Docs**: https://docs.k8slens.dev/
- **Claude API Docs**: https://docs.anthropic.com/
- **GitHub Copilot Best Practices**: https://github.blog/developer-skills/github/how-to-use-github-copilot-in-your-ide-tips-tricks-and-best-practices/

---

*Documento generado para optimizar entorno de desarrollo - Carrillo Abogados Legal Tech*
