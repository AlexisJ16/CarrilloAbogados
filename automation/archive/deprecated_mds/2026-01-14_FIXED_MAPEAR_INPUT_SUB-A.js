// ===================================================================
// NODO: 0. Mapear Input del Orquestador (FIXED v2)
// Problema original: Los datos llegan en campo 'query' como JSON string
// Solucion v1: Agregar caso para parsear 'query'
// Solucion v2: Extraer 'payload' anidado dentro de 'query' (AI Agent)
//
// SINCRONIZADO CON PRODUCCION: 2026-01-21
// ===================================================================

// Parsear input que puede venir de diferentes fuentes:
// 1. AI Agent Tool con query: { query: '{"timestamp":"...","payload":{...}}' }
// 2. Orquestador: { event: 'lead.created', payload: { nombre: '...', ... } }
// 3. AI Agent Tool: { input: '{"nombre":"...", ...}' } - JSON string
// 4. Execute Workflow directo: { nombre: "...", email: "..." }

const raw = $input.first().json;
let data = {};

console.log('=== DEBUG MAPEAR INPUT v2 ===');
console.log('Raw keys:', Object.keys(raw));
console.log('Raw data:', JSON.stringify(raw, null, 2));

// CASO 1 (PRIORIDAD): Datos vienen en campo 'query' como JSON string del AI Agent
// Estructura esperada: { query: '{"timestamp":"...","source":"...","payload":{...}}' }
if (raw.query && typeof raw.query === 'string') {
  try {
    const parsed = JSON.parse(raw.query);
    // El AI Agent puede enviar payload anidado dentro de query
    if (parsed.payload && typeof parsed.payload === 'object') {
      data = parsed.payload;
      console.log('Parseado desde query.payload (AI Agent anidado)');
    } else {
      // Si no hay payload anidado, usar el objeto parseado directo
      data = parsed;
      console.log('Parseado desde query (JSON directo)');
    }
  } catch(e) {
    console.log('Error parseando query:', e.message);
    // Continuar a otros casos en lugar de usar raw
  }
}

// CASO 2: Viene del Orquestador v1.0 con payload directo
if (Object.keys(data).length === 0 && raw.payload && typeof raw.payload === 'object') {
  data = raw.payload;
  console.log('Parseado desde Orquestador payload');
}

// CASO 3: AI Agent Tool envia 'input' como JSON string
if (Object.keys(data).length === 0 && raw.input && typeof raw.input === 'string') {
  try {
    data = JSON.parse(raw.input);
    console.log('Parseado desde AI Agent Tool input string');
  } catch(e) {
    console.log('Error parseando input string:', e.message);
  }
}

// CASO 4: input es un objeto (ya parseado)
if (Object.keys(data).length === 0 && raw.input && typeof raw.input === 'object') {
  data = raw.input;
  console.log('Usando AI Agent Tool input object');
}

// CASO 5: Datos vienen en body (webhook directo)
if (Object.keys(data).length === 0 && raw.body && typeof raw.body === 'object') {
  data = raw.body;
  console.log('Usando body de webhook');
}

// CASO 6: Datos vienen directamente en el json (Execute Workflow simple)
if (Object.keys(data).length === 0 && (raw.nombre || raw.email)) {
  data = raw;
  console.log('Usando datos directos');
}

// CASO 7 (FALLBACK): Buscar en cualquier campo que sea JSON string
if (Object.keys(data).length === 0) {
  for (const key of Object.keys(raw)) {
    if (typeof raw[key] === 'string' && raw[key].startsWith('{')) {
      try {
        const parsed = JSON.parse(raw[key]);
        // Verificar si tiene payload anidado
        if (parsed.payload && typeof parsed.payload === 'object') {
          data = parsed.payload;
        } else {
          data = parsed;
        }
        console.log('Parseado desde campo:', key);
        break;
      } catch(e) {
        console.log('Error parseando campo', key, ':', e.message);
      }
    }
  }
}

// CASO 8 (ULTIMO RECURSO): Usar raw data
if (Object.keys(data).length === 0) {
  data = raw;
  console.log('FALLBACK: usando raw data');
}

console.log('Datos extraidos:', JSON.stringify(data, null, 2));
console.log('=============================');

// Determinar source para trazabilidad
let source = 'unknown';
if (raw.query) {
  source = 'ai_agent_query';
} else if (raw.payload) {
  source = 'orquestador';
} else if (raw.input) {
  source = 'ai_agent_tool';
} else if (raw.body) {
  source = 'webhook';
} else if (raw.nombre || raw.email) {
  source = 'direct';
}

// Validar campos requeridos
const email = data.email || '';
const nombre = data.nombre || '';

if (!email) {
  console.log('ADVERTENCIA: Campo email vacio o no encontrado');
}
if (!nombre) {
  console.log('ADVERTENCIA: Campo nombre vacio o no encontrado');
}

return {
  json: {
    lead_id: data.lead_id || data.leadId || '',
    nombre: nombre,
    email: email,
    telefono: data.telefono || '',
    empresa: data.empresa || '',
    cargo: data.cargo || '',
    servicio_interes: data.servicio_interes || data.servicio || '',
    mensaje: data.mensaje || '',
    utm_source: data.utm_source || '',
    utm_campaign: data.utm_campaign || '',
    event_type: raw.event || data.event_type || 'new_lead',
    _source: source,
    _raw_keys: Object.keys(raw),
    _parsed_at: new Date().toISOString()
  }
};
