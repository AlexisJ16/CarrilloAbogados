// ===================================================================
// NODO: 0. Mapear Input del Orquestador (FIXED v2)
// Fecha: 2026-01-21
// Autor: Agente Engineer
//
// PROBLEMA ORIGINAL: Los datos del AI Agent llegan en campo 'query'
// como JSON string, y DENTRO de ese JSON hay un campo 'payload'
// con los datos reales del lead.
//
// SOLUCION: Parsear 'query' Y extraer 'payload' si existe.
// ===================================================================

// Escenarios de entrada soportados:
// 1. AI Agent Tool v3.0: { query: '{"payload":{"nombre":"..."},...}' }
// 2. Orquestador v1.0: { event: 'lead.created', payload: { nombre: '...' } }
// 3. AI Agent Tool input string: { input: '{"nombre":"...", ...}' }
// 4. AI Agent Tool input object: { input: { nombre: '...' } }
// 5. Webhook directo body: { body: { nombre: '...' } }
// 6. Execute Workflow directo: { nombre: "...", email: "..." }
// 7. Fallback: buscar cualquier campo JSON string

const raw = $input.first().json;
let data = {};
let sourceType = 'unknown';

console.log('=== DEBUG MAPEAR INPUT v2 ===');
console.log('Raw keys:', Object.keys(raw));
console.log('Raw data:', JSON.stringify(raw, null, 2).substring(0, 500));

// ---------------------------------------------------------------
// CASO 1: AI Agent Tool con campo 'query' (Orquestador v3.0)
// Estructura: { query: '{"timestamp":"...","source":"...","payload":{...},"event_type":"..."}' }
// ---------------------------------------------------------------
if (raw.query && typeof raw.query === 'string') {
  try {
    const parsed = JSON.parse(raw.query);
    console.log('Parsed query keys:', Object.keys(parsed));

    // Los datos del lead estan en parsed.payload
    if (parsed.payload && typeof parsed.payload === 'object') {
      data = parsed.payload;
      // Preservar event_type del nivel superior
      data.event_type = parsed.event_type || data.event_type || 'new_lead';
      sourceType = 'ai_agent_query_with_payload';
      console.log('Parseado desde query -> payload');
    } else {
      // Fallback: usar parsed directamente si no tiene payload
      data = parsed;
      sourceType = 'ai_agent_query_direct';
      console.log('Parseado desde query (sin payload anidado)');
    }
  } catch(e) {
    console.log('Error parseando query:', e.message);
    data = raw;
    sourceType = 'query_parse_error';
  }
}
// ---------------------------------------------------------------
// CASO 2: Orquestador v1.0 con payload directo
// Estructura: { event: 'lead.created', payload: { nombre: '...' } }
// ---------------------------------------------------------------
else if (raw.payload && typeof raw.payload === 'object') {
  data = raw.payload;
  data.event_type = raw.event || raw.event_type || 'new_lead';
  sourceType = 'orquestador_v1_payload';
  console.log('Usando Orquestador v1.0 payload');
}
// ---------------------------------------------------------------
// CASO 3: AI Agent Tool con 'input' como JSON string
// Estructura: { input: '{"nombre":"...", ...}' }
// ---------------------------------------------------------------
else if (raw.input && typeof raw.input === 'string') {
  try {
    const parsed = JSON.parse(raw.input);
    // Verificar si tiene payload anidado
    if (parsed.payload && typeof parsed.payload === 'object') {
      data = parsed.payload;
      data.event_type = parsed.event_type || 'new_lead';
    } else {
      data = parsed;
    }
    sourceType = 'ai_agent_input_string';
    console.log('Parseado desde input string');
  } catch(e) {
    console.log('Error parseando input string:', e.message);
    data = raw;
    sourceType = 'input_parse_error';
  }
}
// ---------------------------------------------------------------
// CASO 4: AI Agent Tool con 'input' como objeto
// Estructura: { input: { nombre: '...', email: '...' } }
// ---------------------------------------------------------------
else if (raw.input && typeof raw.input === 'object') {
  // Verificar si tiene payload anidado
  if (raw.input.payload && typeof raw.input.payload === 'object') {
    data = raw.input.payload;
    data.event_type = raw.input.event_type || 'new_lead';
  } else {
    data = raw.input;
  }
  sourceType = 'ai_agent_input_object';
  console.log('Usando input object');
}
// ---------------------------------------------------------------
// CASO 5: Webhook directo con body
// Estructura: { body: { nombre: '...', email: '...' } }
// ---------------------------------------------------------------
else if (raw.body && typeof raw.body === 'object') {
  // Verificar si body tiene payload anidado
  if (raw.body.payload && typeof raw.body.payload === 'object') {
    data = raw.body.payload;
    data.event_type = raw.body.event_type || 'new_lead';
  } else {
    data = raw.body;
  }
  sourceType = 'webhook_body';
  console.log('Usando body de webhook');
}
// ---------------------------------------------------------------
// CASO 6: Datos directos en el JSON
// Estructura: { nombre: "...", email: "..." }
// ---------------------------------------------------------------
else if (raw.nombre || raw.email) {
  data = raw;
  sourceType = 'direct_fields';
  console.log('Usando datos directos');
}
// ---------------------------------------------------------------
// CASO 7: Fallback - buscar cualquier campo con JSON string
// ---------------------------------------------------------------
else {
  for (const key of Object.keys(raw)) {
    if (typeof raw[key] === 'string' && raw[key].trim().startsWith('{')) {
      try {
        const parsed = JSON.parse(raw[key]);
        // Verificar payload anidado
        if (parsed.payload && typeof parsed.payload === 'object') {
          data = parsed.payload;
          data.event_type = parsed.event_type || 'new_lead';
        } else {
          data = parsed;
        }
        sourceType = `fallback_field_${key}`;
        console.log('Parseado desde campo:', key);
        break;
      } catch(e) {
        console.log('Error parseando campo', key, ':', e.message);
      }
    }
  }

  if (Object.keys(data).length === 0) {
    data = raw;
    sourceType = 'fallback_raw';
    console.log('Fallback: usando raw data sin parsear');
  }
}

// ---------------------------------------------------------------
// NORMALIZACION Y OUTPUT
// ---------------------------------------------------------------

// Extraer campos con fallbacks para diferentes nombres
const leadId = data.lead_id || data.leadId || data.id || '';
const nombre = data.nombre || data.name || data.fullName || '';
const email = data.email || data.correo || data.emailAddress || '';
const telefono = data.telefono || data.phone || data.tel || '';
const empresa = data.empresa || data.company || data.companyName || '';
const cargo = data.cargo || data.position || data.title || '';
const servicioInteres = data.servicio_interes || data.servicio || data.service || data.interest || '';
const mensaje = data.mensaje || data.message || data.description || '';
const utmSource = data.utm_source || data.utmSource || '';
const utmCampaign = data.utm_campaign || data.utmCampaign || '';
const eventType = data.event_type || data.eventType || 'new_lead';

console.log('=== DATOS EXTRAIDOS ===');
console.log('Source:', sourceType);
console.log('Lead ID:', leadId);
console.log('Nombre:', nombre);
console.log('Email:', email);
console.log('Empresa:', empresa);
console.log('Mensaje:', mensaje.substring(0, 100) + '...');
console.log('========================');

// Validacion basica
if (!email) {
  console.log('ADVERTENCIA: Email vacio - posible error de mapeo');
}

return {
  json: {
    // Datos del lead
    lead_id: leadId,
    nombre: nombre,
    email: email,
    telefono: telefono,
    empresa: empresa,
    cargo: cargo,
    servicio_interes: servicioInteres,
    mensaje: mensaje,
    utm_source: utmSource,
    utm_campaign: utmCampaign,
    event_type: eventType,

    // Metadata para debugging
    _source: sourceType,
    _raw_keys: Object.keys(raw),
    _mapped_at: new Date().toISOString()
  }
};
