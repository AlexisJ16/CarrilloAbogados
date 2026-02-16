# 📚 Documentación de Negocio - Carrillo Abogados

**Última Actualización**: 16 de Febrero, 2026  
**Responsable**: Business Product Agent  
**Estado**: ✅ Organizado

---

## 📋 Índice de Documentos

### 📌 Documentos Principales (Desarrollo)

Estos documentos son la **fuente de verdad** para el desarrollo del MVP:

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [MVP_ROADMAP.md](./MVP_ROADMAP.md) | **⭐ INICIO AQUÍ** - Roadmap completo del MVP con 5 pilares | ✅ Activo |
| [REQUERIMIENTOS.md](./REQUERIMIENTOS.md) | 100+ requerimientos funcionales y no funcionales (MoSCoW) | ✅ Activo |
| [ARQUITECTURA_FUNCIONAL.md](./ARQUITECTURA_FUNCIONAL.md) | Mapeo de funcionalidades a microservicios | ✅ Activo |
| [ESTRATEGIA_AUTOMATIZACION.md](./ESTRATEGIA_AUTOMATIZACION.md) | Integración plataforma ↔ n8n Cloud | ✅ Activo |

### 👥 Documentos de Usuarios y Casos de Uso

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [ROLES_USUARIOS.md](./ROLES_USUARIOS.md) | Los 4 tipos de usuario: Visitante, Cliente, Abogado, Admin | ✅ Activo |
| [CASOS_USO.md](./CASOS_USO.md) | Flujos detallados por cada rol | ✅ Activo |

### 🏢 Contexto Empresarial

| Documento | Descripción | Estado |
|-----------|-------------|--------|
| [MODELO_NEGOCIO.md](./MODELO_NEGOCIO.md) | Contexto del bufete, 5 áreas de práctica, equipo | ✅ Referencia |

---

## 📂 Subcarpetas

### � Documentacion_Proporcionada/

Documentos originales entregados por el cliente (Carrillo ABGD):

| Archivo | Descripción |
|---------|-------------|
| Brief de requerimientos.pdf | Cuestionario inicial |
| Respuestas del Brief... | Respuestas en CSV, TXT, XLSX |
| Estimación de clientes... | Proyecciones financieras |
| Importancia de la página... | Justificación del proyecto |
| Planeación empresarial... | Contexto de la industria |
| DOFA, OBJ, MERCADO.pdf | Análisis DOFA y mercado |
| Framework estratégico ABGD.pdf | Marco estratégico del bufete |
| PRESUPUESTO_MARKETING_2026... | Presupuesto marketing |

> **Nota**: La documentación detallada de n8n/marketing está en `/automation/` (fuente de verdad).

---

## 🗺️ Orden de Lectura Recomendado

Para entender completamente el proyecto:

```
1. MVP_ROADMAP.md          ← Empezar aquí: Qué vamos a construir
   │
2. ROLES_USUARIOS.md       ← Quién va a usar el sistema
   │
3. REQUERIMIENTOS.md       ← Qué funcionalidades necesitamos
   │
4. CASOS_USO.md            ← Cómo interactúan los usuarios
   │
5. ARQUITECTURA_FUNCIONAL.md ← Cómo lo implementamos técnicamente
   │
6. ESTRATEGIA_AUTOMATIZACION.md ← Cómo integramos con n8n
   │
7. /automation/docs/*      ← Detalles de cada workflow (ver /automation/)
```

---

## 🎯 Las 5 Funcionalidades del MVP

Quick reference (ver [MVP_ROADMAP.md](./MVP_ROADMAP.md) para detalles):

| # | Pilar | Descripción | Prioridad |
|---|-------|-------------|-----------|
| 1 | **Autenticación y Roles** | 4 tipos de usuario con OAuth2 y RBAC | CRÍTICO |
| 2 | **Captura de Leads** | Formulario → API → NATS → Notificación | CRÍTICO |
| 3 | **Integración n8n** | Scoring automático + Clasificación HOT/WARM/COLD | CRÍTICO |
| 4 | **Dashboards** | Visualización de KPIs y métricas | ALTO |
| 5 | **Producción** | Seguridad, SEO, Responsive, Alta Disponibilidad | ALTO |

---

## 📅 Timeline MVP

- **Inicio**: 6 de Enero, 2026
- **Lanzamiento**: 27 de Marzo, 2026
- **Duración**: 12 semanas

---

## 🔗 Documentos Relacionados (Fuera de esta carpeta)

| Documento | Ubicación | Descripción |
|-----------|-----------|-------------|
| PROYECTO_ESTADO.md | `/PROYECTO_ESTADO.md` | Estado actual del proyecto |
| CLAUDE.md | `/CLAUDE.md` | Contexto para Claude AI |
| copilot-agents/ | `/.github/copilot-agents/` | Agentes especializados |
| architecture/ | `/docs/architecture/` | ADRs y arquitectura técnica |
| operations/ | `/docs/operations/` | Guías operativas |

---

*Índice gestionado por el Business Product Agent*
