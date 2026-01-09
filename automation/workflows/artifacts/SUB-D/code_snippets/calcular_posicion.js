// ===================================================================
// Nodo 7: Calcular Posicion en Secuencia
// Workflow: SUB-D: Nurturing Sequence Engine
// Proposito: Determinar que email enviar segun dias transcurridos
// ===================================================================

const lead = $input.item.json;

// Parsear fecha de creacion
const createdAt = new Date(lead.created_at);
const now = new Date();

// Calcular dias transcurridos desde captura
const diffMs = now - createdAt;
const daysSinceCapture = Math.floor(diffMs / (1000 * 60 * 60 * 24));

// Mapeo de dias a posicion en secuencia (1-12)
// Cada posicion tiene un rango de dias donde es valida
const emailSchedule = [
  { position: 1, minDay: 0, maxDay: 2 },      // Dia 0-2 (inmediato/bienvenida)
  { position: 2, minDay: 3, maxDay: 5 },      // Dia 3-5 (educativo)
  { position: 3, minDay: 7, maxDay: 9 },      // Dia 7-9 (case study)
  { position: 4, minDay: 10, maxDay: 13 },    // Dia 10-13 (recurso de valor)
  { position: 5, minDay: 14, maxDay: 17 },    // Dia 14-17 (urgencia)
  { position: 6, minDay: 21, maxDay: 24 },    // Dia 21-24 (autoridad)
  { position: 7, minDay: 28, maxDay: 31 },    // Dia 28-31 (oferta)
  { position: 8, minDay: 35, maxDay: 38 },    // Dia 35-38 (re-engagement)
  { position: 9, minDay: 42, maxDay: 45 },    // Dia 42-45 (tendencias)
  { position: 10, minDay: 49, maxDay: 52 },   // Dia 49-52 (last chance)
  { position: 11, minDay: 56, maxDay: 59 },   // Dia 56-59 (break-up)
  { position: 12, minDay: 90, maxDay: 999 }   // Dia 90+ (win-back)
];

// Encontrar posicion segun dias transcurridos
let position = null;
for (const schedule of emailSchedule) {
  if (daysSinceCapture >= schedule.minDay && daysSinceCapture <= schedule.maxDay) {
    position = schedule.position;
    break;
  }
}

// Caso especial: Si ya pasaron 90+ dias y emails_sent < 12, enviar email 12
if (!position && daysSinceCapture >= 90 && lead.emails_sent < 12) {
  position = 12;
}

// Determinar si debe enviar email hoy
// Solo enviar si la posicion calculada es mayor que emails ya enviados
const shouldSend = position !== null && lead.emails_sent < position;

// Calcular siguiente fecha de email basado en la posicion actual
// Estos delays determinan cuando se enviara el proximo email
const nextEmailDelays = {
  1: 3,    // Despues de pos 1, esperar 3 dias
  2: 4,    // Despues de pos 2, esperar 4 dias
  3: 3,    // Despues de pos 3, esperar 3 dias
  4: 4,    // Despues de pos 4, esperar 4 dias
  5: 7,    // Despues de pos 5, esperar 7 dias
  6: 7,    // Despues de pos 6, esperar 7 dias
  7: 7,    // Despues de pos 7, esperar 7 dias
  8: 7,    // Despues de pos 8, esperar 7 dias
  9: 7,    // Despues de pos 9, esperar 7 dias
  10: 7,   // Despues de pos 10, esperar 7 dias
  11: 34,  // Despues de pos 11, esperar 34 dias (pausa larga antes de win-back)
  12: null // Posicion 12 es el fin de la secuencia
};

const delay = nextEmailDelays[position];
const nextEmailDate = delay
  ? new Date(Date.now() + delay * 24 * 60 * 60 * 1000).toISOString()
  : null;

// Retornar datos enriquecidos
return {
  json: {
    ...lead,
    position: position || lead.emails_sent + 1,
    days_since_capture: daysSinceCapture,
    should_send: shouldSend,
    next_email_date: nextEmailDate
  }
};
