# 🔗 Guía de Integración para Desarrollador de Marketing

**Versión**: 1.2  
**Fecha**: 11 de Enero, 2026  
**Autor**: Equipo de Desarrollo  
**Para**: Juan José Gómez Agudelo (Marketing Tech)  
**Fase Proyecto**: FASE 10 - Autenticación Frontend Completa

---

## 📋 Resumen

Este documento explica **cómo funciona la integración** entre el repositorio de desarrollo principal (`CarrilloAbogados`) y el repositorio de marketing (`MarketingTech`), y cómo puedes contribuir al proyecto de forma sincronizada.

---

## 🏗️ Arquitectura de Repositorios

### Dos Repositorios, Un Proyecto

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           FLUJO DE TRABAJO                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   MarketingTech (tu repo)          CarrilloAbogados (repo principal)       │
│   ┌─────────────────────┐          ┌─────────────────────────────┐         │
│   │ 🎨 Workflows n8n    │ ──────▶  │ docs/n8n-workflows/         │         │
│   │ 📄 Documentación    │  SYNC    │ docs/business/Marketing-N8N/ │         │
│   │ 🤖 Agentes Claude   │ ──────▶  │ .github/copilot-agents/     │         │
│   └─────────────────────┘          └─────────────────────────────┘         │
│          │                                     │                            │
│          │ (rama: Alexis)                     │ (ramas: main, dev,         │
│          │                                     │         automation)        │
│          ▼                                     ▼                            │
│   ┌─────────────────────┐          ┌─────────────────────────────┐         │
│   │ n8n Cloud           │◀──API──▶│ n8n-integration-service     │         │
│   │ (Producción)        │         │ Puerto 8800                  │         │
│   └─────────────────────┘          └─────────────────────────────┘         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Dónde Está Tu Contenido en CarrilloAbogados

| Tu Archivo en MarketingTech | Ubicación en CarrilloAbogados |
|----------------------------|-------------------------------|
| `02-context/technical/arquitectura/*.md` | `docs/business/Marketing-N8N/` |
| `04-workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/` | `docs/n8n-workflows/mega-workflow-1/` |
| `03-agents/AGENTS.md` | `docs/n8n-workflows/agents/` |
| `02-context/technical/n8n_mcp_guide.md` | `docs/n8n-workflows/` |

---

## 🌳 Sistema de Ramas

### Ramas del Proyecto Principal

| Rama | Propósito | Quién la usa |
|------|-----------|--------------|
| `main` | Producción (protegida) | Merges automáticos |
| `dev` | Desarrollo activo | Alexis (Backend/Frontend) |
| `automation` | Desarrollo n8n | Juan José (Marketing) |

### Tu Flujo de Trabajo Recomendado

```bash
# 1. Clonar el repositorio principal
git clone https://github.com/AlexisJ16/CarrilloAbogados.git
cd CarrilloAbogados

# 2. Cambiar a la rama automation
git checkout automation

# 3. Sincronizar con los últimos cambios de dev
git pull origin dev

# 4. Trabajar en tus cambios de n8n/marketing
# ... editar archivos ...

# 5. Hacer commit
git add .
git commit -m "feat(n8n): descripción de tu cambio"

# 6. Push a automation
git push origin automation

# 7. Crear PR: automation → dev (Alexis revisa e integra)
```

---

## 📁 Estructura de Carpetas para Marketing

### Dónde Agregar Nuevos Workflows

```
docs/n8n-workflows/
├── README.md                          ← Documentación principal
├── n8n_mcp_guide.md                   ← Tu guía de MCP
├── NODE_STANDARDS.md                  ← Estándares de nodos
│
├── agents/                            ← Agentes especializados
│   └── AGENTS.md                      ← Índice de 5 agentes
│
├── mega-workflow-1/                   ← MW#1: Lead Lifecycle ✅
│   ├── STATUS.md                      ← Estado actual
│   ├── orchestrator/                  ← Hub principal
│   │   ├── ORQUESTADOR_PRODUCTION*.json
│   │   └── specs/
│   └── spokes/                        ← Sub-workflows
│       └── sub-a-lead-intake/
│
├── mega-workflow-2/                   ← MW#2: Retención 🔄 (CREAR)
│   ├── STATUS.md
│   ├── orchestrator/
│   └── spokes/
│
└── mega-workflow-3/                   ← MW#3: SEO 🔄 (CREAR)
    ├── STATUS.md
    ├── orchestrator/
    └── spokes/
```

### Dónde Agregar Documentación de Negocio

```
docs/business/Marketing-N8N/
├── 00_ARQUITECTURA_GENERAL.md         ✅ Ya existe
├── 01_MEGA_WORKFLOW_1_CAPTURA.md      ✅ Ya existe
├── 02_MEGA_WORKFLOW_2_RETENCION.md    ✅ Ya existe
├── 03_MEGA_WORKFLOW_3_SEO.md          ✅ Ya existe
├── DOFA, OBJ, MERCADO.pdf             ✅ Ya existe
└── Framework estrategico ABGD.pdf      ✅ Ya existe
```

---

## 🔌 Integración con el Backend

### n8n-integration-service (Puerto 8800)

Este microservicio es el **puente bidireccional** entre la plataforma web y n8n Cloud:

```
┌───────────────────────────────────────────────────────────────────┐
│               n8n-integration-service                              │
├───────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ESCUCHA NATS (Eventos de la Plataforma)                         │
│  ──────────────────────────────────────                          │
│  • lead.capturado      → Envía a n8n /lead-events                │
│  • cita.agendada       → Envía a n8n /meeting-events             │
│  • caso.cerrado        → Envía a n8n /case-events                │
│  • cliente.inactivo    → Envía a n8n /client-events              │
│                                                                   │
│  EXPONE WEBHOOKS (n8n llama a estos endpoints)                   │
│  ───────────────────────────────────────────                     │
│  POST /webhook/lead-scored    ← SUB-A actualiza score            │
│  POST /webhook/lead-hot       ← SUB-B notifica HOT lead          │
│  POST /webhook/upsell-detected← SUB-J detecta oportunidad        │
│  POST /webhook/sync-response  ← Respuesta de sincronización      │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

### Configuración de n8n Cloud

| Variable | Valor |
|----------|-------|
| **Webhook Base** | `https://carrilloabgd.app.n8n.cloud/webhook/` |
| **Orquestador ID** | `bva1Kc1USbbITEAw` |
| **SUB-A ID** | `RHj1TAqBazxNFriJ` |

### URLs del Backend (para testing)

| Ambiente | URL Backend | URL n8n Webhook |
|----------|-------------|-----------------|
| **Local** | `http://localhost:8080` | `http://localhost:8800` |
| **Producción** | `https://api.carrilloabgd.com` | `https://carrilloabgd.app.n8n.cloud/webhook/` |

---

## 🧪 Cómo Probar Localmente

### 1. Levantar el Backend

```bash
# Desde la raíz del proyecto
docker-compose up -d

# Verificar que todo esté corriendo
docker ps

# Deberías ver 10+ contenedores healthy
```

### 2. Probar el Endpoint de Leads

```bash
# Crear un lead de prueba
curl -X POST http://localhost:8080/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test Lead",
    "email": "test@empresa.com",
    "servicio": "Marcas",
    "mensaje": "Quiero registrar mi marca"
  }'
```

### 3. Ver Eventos en NATS

```bash
# Conectar al monitor de NATS
docker exec -it carrillo-nats nats sub "carrillo.events.>"
```

### 4. Probar Webhook de n8n

```bash
# Simular callback de n8n (lead scored)
curl -X POST http://localhost:8800/n8n-integration-service/webhook/lead-scored \
  -H "Content-Type: application/json" \
  -d '{
    "leadId": "uuid-del-lead",
    "score": 75,
    "category": "HOT"
  }'
```

---

## 🔄 Sincronización de Contenido

### Cuándo Sincronizar MarketingTech → CarrilloAbogados

1. **Nuevo workflow JSON** creado/actualizado en n8n Cloud
2. **Nueva documentación** de arquitectura o diseño
3. **Cambios en los agentes** Claude especializados

### Cómo Sincronizar

**Opción A: Copiar manualmente**

```powershell
# Desde PowerShell en Windows
Copy-Item "C:\GitHub Desktop\MarketingTech\04-workflows\*" `
  -Destination "C:\Carrillo Abogados\Repositorios GitHub\CarrilloAbogados\docs\n8n-workflows\" `
  -Recurse -Force
```

**Opción B: Usar el Branch Sync Agent**

Ver: `.github/copilot-agents/branch-sync-agent.md`

Este agente automatiza la sincronización entre ramas.

---

## 📝 Convenciones de Commits

### Formato

```
tipo(alcance): descripción corta

Cuerpo opcional con más detalles
```

### Tipos para Marketing

| Tipo | Uso |
|------|-----|
| `feat(n8n)` | Nuevo workflow o funcionalidad |
| `fix(n8n)` | Corrección de workflow |
| `docs(marketing)` | Documentación de marketing |
| `refactor(n8n)` | Reestructuración de workflow |

### Ejemplos

```
feat(n8n): add SUB-B hot lead notification workflow
fix(n8n): correct optional chaining in SUB-A expressions
docs(marketing): update MW#1 architecture diagram
```

---

## ⚠️ Notas Técnicas Importantes

### Expresiones n8n

```javascript
// ❌ NO usar optional chaining (no soportado en n8n)
$json.contact?.email

// ✅ Usar ternarios
$json.contact ? $json.contact.email : ''
```

### Nodo IF

```json
// ✅ Siempre incluir "options" en conditions
{
  "conditions": {
    "options": { "caseSensitive": true, "leftValue": "", "typeValidation": "strict" },
    "conditions": [ ... ]
  }
}
```

### Gmail Node

```json
// ✅ Siempre especificar operation
{
  "operation": "send"
}
```

---

## 📞 Comunicación

### Canales

- **GitHub Issues**: Reportar bugs o solicitar features
- **Pull Requests**: Proponer cambios (automation → dev)
- **Comentarios en código**: Para discusiones técnicas

### Responsabilidades

| Persona | Área | Contacto |
|---------|------|----------|
| **Alexis** | Backend, Frontend, DevOps | @AlexisJ16 |
| **Juan José** | n8n Workflows, Marketing | @JuanJoseGomezAgudelo |

---

## 🚀 Próximos Pasos para Ti

### Esta Semana

1. [ ] Clonar `CarrilloAbogados` y checkout `automation`
2. [ ] Revisar `docs/n8n-workflows/` y validar que todo esté correcto
3. [ ] Probar backend local con `docker-compose up -d`

### Próxima Semana

4. [ ] Crear estructura para MW#2 (Retención)
5. [ ] Documentar nuevos sub-workflows
6. [ ] Sincronizar cambios via PR

---

## 📚 Documentos Relacionados

- [README Principal del Proyecto](../../README.md)
- [CLAUDE.md - Contexto para IA](../../CLAUDE.md)
- [Branch Sync Agent](../../.github/copilot-agents/branch-sync-agent.md)
- [Documentación n8n Workflows](../n8n-workflows/README.md)
- [Estrategia de Automatización](../business/ESTRATEGIA_AUTOMATIZACION.md)

---

**¡Bienvenido al equipo!** 🎉

Si tienes dudas, revisa los agentes en `.github/copilot-agents/` o crea un Issue en GitHub.
