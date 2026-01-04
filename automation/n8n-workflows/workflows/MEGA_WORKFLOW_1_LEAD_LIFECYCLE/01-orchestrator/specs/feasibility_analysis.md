# ANÁLISIS DE VIABILIDAD TÉCNICA

## 1. Latencia del Orquestador IA
- **Riesgo**: El uso de un LLM (Gemini) en el nodo 2 del orquestador añade una latencia de 1-3 segundos.
- **Impacto**: Si la petición de origen (ej: formulario web) tiene un timeout corto (ej: 5s), podría fallar.
- **Mitigación**: 
    - Asegurar que el webhook tenga `Response Mode: Last Node` o `Response Node` (no `On Received`).
    - Configurar timeouts del cliente web a > 30s.

## 2. Consistencia del Modelo (Router)
- **Riesgo**: El modelo podría alucinar un `event_type` no existente.
- **Mitigación**:
    - Usar `n8n-nodes-base.basicLLMChain` con "Output Parser" estructurado (JSON).
    - En el Switch, incluir siempre un "Fallback Route" que mande un alerta de "Clasificación Fallida".

## 3. Manejo de Errores en Sub-Workflow A
- **Riesgo**: Si la normalización falla (IA retorna basura), el Scoring (código) podría romper.
- **Mitigación**:
    - Validar el output de la IA antes del nodo Code.
    - Default values: `if (!normalized_role) normalized_role = 'Unknown'`.

## 4. Costos API
- **Gemini Pro**: Costo por 1k tokens. Para 300 leads/mes, es despreciable (<$5/mes).
- **Viabilidad**: ✅ Alta.

## CONCLUSIÓN
La arquitectura propuesta es **VIABLE**. El mayor reto es la latencia de la IA, que se gestiona con buena configuración de timeouts.
