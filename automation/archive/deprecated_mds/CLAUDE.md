Recuerda estos datos clave del proyecto n8n-antigravity:



=== PROYECTO ===

\- Cliente: Carrillo Abogados (Firma legal colombiana - PI, Marcas, Contratación Estatal)

\- Objetivo: Automatizar marketing legal, escalar de 20 a 300+ leads/mes

\- Budget: 70.3M COP anual

\- Timeline: Launch Marzo 2026

\- Modelo estratégico: Dos velocidades (Motor Futuro 60%, Motor Actual 40%)



=== CONFIGURACIÓN TÉCNICA ===

\- n8n Cloud: https://carrilloabgd.app.n8n.cloud

\- API URL: https://carrilloabgd.app.n8n.cloud/api/v1

\- Versión n8n: 1.120.4

\- Arquitectura OBLIGATORIA: Hub \& Spoke (workflows modulares)

\- Stack: n8n + Google Cloud Platform (Firestore, Gemini, Gmail)

\- MCP Server: n8n-mcp (20 tools disponibles)



=== UBICACIONES DEL PROYECTO ===

\- Directorio raíz: C:\\Automatizaciones\\n8n-antigravity

\- Guía MCP CRÍTICA: 02-context/technical/n8n\_mcp\_guide.md (SIEMPRE leer antes de trabajar)

\- Contexto negocio: 02-context/business/ (DOFA, presupuesto, estrategia)

\- Workflows desarrollo: 04-workflows/

\- Workflows producción: 06-outputs/ (solo después de validación QA completa)

\- Subagentes: .claude/agents/ (5 agentes especializados)



=== SUBAGENTES DISPONIBLES ===

1\. architect - Diseña workflows, crea specs y diagramas

2\. engineer - Implementa workflows según specs

3\. qa-specialist - Valida, prueba, genera reportes

4\. optimizer - Optimiza performance y código

5\. validator - Validación final y deployment a producción



Flujo secuencial: architect → engineer → qa-specialist → optimizer → validator



=== MI CONTEXTO ===

\- Rol: Director de Estrategia y Marketing en Carrillo Abogados

\- Disponibilidad: 5 horas/semana para marketing

\- Preferencia: Automatización sobre procesos manuales

\- Enfoque: Data-driven, requiero análisis crítico de asunciones

\- Ubicación: Cali, Valle del Cauca, Colombia



=== REGLAS CRÍTICAS ===

1\. SIEMPRE leer 02-context/technical/n8n\_mcp\_guide.md antes de trabajar con workflows

2\. NUNCA asumir estados de workflows - SIEMPRE verificar con comandos

3\. NUNCA mover archivos a 06-outputs/ sin validación QA completa

4\. SIEMPRE usar arquitectura Hub \& Spoke (no workflows monolíticos)

5\. NUNCA duplicar información que ya existe en archivos

6\. SIEMPRE verificar con diagnostic antes de afirmar configuraciones



=== CONVENCIONES ===

\- Nomenclatura workflows: MEGA\_WORKFLOW\_X (principales), SUB-X-nombre (spokes)

\- Estructura por workflow: specs/ (diseño), artifacts/ (implementación), testing/ (QA)

\- Verificar estado real con: "Dame un listado de los workflows que hay en mi instancia de n8n"

\- Archivo crítico: n8n\_mcp\_guide.md contiene 35+ tools MCP (SIEMPRE consultar)

