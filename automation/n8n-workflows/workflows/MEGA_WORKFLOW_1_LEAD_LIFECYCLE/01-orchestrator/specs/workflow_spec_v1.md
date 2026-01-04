# FASE 1: DISEÑO - ESPECIFICACIÓN TÉCNICA (V1)
# FASE 1: DISEÑO - ESPECIFICACIÓN TÉCNICA (V1)
# MEGA WORKFLOW 1: LEAD LIFECYCLE (CONCEPTO)
# COMPONENTE: WORKFLOW A (ORQUESTADOR)

## 1. Objetivo del Sistema
Construir un sistema de gestión de ciclo de vida de leads robusto, escalable y agentico.
El sistema consta de:
1.  **Workflow A (Orquestador - Hub)**: Agente IA que entiende la intención de los eventos entrantes y los enruta.
2.  **Sub-Workflow A (Spoke)**: Procesa nuevos leads usando IA para normalización y lógica determinista para scoring.

## 2. Arquitectura de Componentes
**Patrón**: Hub & Spoke con Inteligencia Híbrida.

### A. El HUB (Orquestador)
- **Rol**: Portero Inteligente.
- **Entrada**: Webhook único (`/lead-events`).
- **Cerebro**: Modelo LLM (Gemini) que clasifica el payload.
- **Salida**: Ejecuta el Spoke correspondiente.

### B. El SPOKE (Sub-Workflow A)
- **Rol**: Procesador Especializado.
- **Entrada**: Datos del lead desde el Orquestador.
- **Proceso Híbrido**:
    1.  **AI Normalization**: "Dueño" -> `C-Level`.
    2.  **Deterministic Scoring**: IF `C-Level` THEN `Score += 30`.
- **Salida**: Resultado consolidado a Firestore y Email.

## 3. Especificación de Flujos de Datos

### Payload de Entrada (Ejemplo)
```json
{
  "text": "Hola, soy Juan, dueño de una pyme de tecnologia, quiero saber precios",
  "source": "whatsapp"
}
```

### Transformación en Orquestador (IA)
```json
{
  "event_type": "new_lead",
  "confidence": 0.98,
  "reasoning": "El texto indica intención de compra ('quiero saber precios') y presentación ('soy Juan')."
}
```

### Transformación en Sub-A (IA Normalizer)
```json
{
  "raw_role": "dueño",
  "normalized_role": "C-Level",
  "normalized_industry": "Technology"
}
```

### Transformación en Sub-A (Scoring Determinista)
```javascript
// Lógica de Código
let score = 0;
if (normalized_role === 'C-Level') score += 30;
if (normalized_industry === 'Technology') score += 10;
if (source === 'whatsapp') score += 5;
return { final_score: score }; // Total: 45
```

## 4. Requisitos de Seguridad y Robustez
- **Fallback**: Si la IA falla en clasificar, enviar a "Revisión Manual".
- **Idempotencia**: Evitar procesar el mismo lead dos veces (check por email/ID).
- **Timeouts**: Configurar el webhook del orquestador para esperar al menos 2 minutos (por latencia de IA).
