// ===================================================================
// Nodo 8: Cargar Template Email
// Workflow: SUB-D: Nurturing Sequence Engine
// Proposito: Obtener estructura del email segun posicion en secuencia
// ===================================================================

const position = $json.position;
const nombre = $json.nombre;
const empresa = $json.empresa;

// 12 templates de nurturing sequence para Carrillo Abogados
// Cada template define: subject, objetivo, estructura y limite de palabras
const templates = {
  1: {
    subject: `Gracias por contactarnos, ${nombre}`,
    objective: "Bienvenida",
    structure: "Saludo personalizado + Presentacion firma (15 anos experiencia en SIC) + Valor que ofrecemos en Propiedad Intelectual + CTA: Agendar llamada exploratoria gratuita",
    max_words: 200
  },
  2: {
    subject: `Por que proteger tu marca ${empresa}?`,
    objective: "Educativo",
    structure: "Riesgos de no registrar marca en Colombia + Casos reales de perdida de marca por falta de registro + Beneficios concretos del registro ante la SIC + CTA: Descargar checklist gratuito de registro de marcas",
    max_words: 250
  },
  3: {
    subject: `Como ayudamos a empresas como ${empresa}`,
    objective: "Case Study",
    structure: "Historia de cliente similar (sector tech/startup) + Problema especifico que tenian con su marca + Solucion implementada por Carrillo Abogados + Resultado cuantificable (tiempo, ahorro, proteccion lograda) + CTA: Ver mas casos de exito en nuestra web",
    max_words: 300
  },
  4: {
    subject: "Checklist gratuito: Registro de marcas en Colombia",
    objective: "Recurso de valor",
    structure: "Introduccion breve del checklist y su utilidad + 5 pasos principales del proceso de registro + Link de descarga del PDF + CTA: Necesitas ayuda con algun paso? Respondenos",
    max_words: 200
  },
  5: {
    subject: `3 riesgos que enfrenta ${empresa} sin registro`,
    objective: "Urgencia",
    structure: "Riesgo 1: Demandas por infraccion de marca ajena + Riesgo 2: Perdida de mercado ante competidores registrados + Riesgo 3: Inversion perdida en branding sin proteccion legal + CTA: Protege tu marca ahora - agenda consulta",
    max_words: 250
  },
  6: {
    subject: "Dr. Carrillo en la SIC: 15 anos de experiencia",
    objective: "Autoridad",
    structure: "Trayectoria profesional del Dr. Omar Carrillo + Experiencia directa en la Superintendencia de Industria y Comercio + Estadisticas de casos ganados y registros exitosos + Testimoniales breves de clientes satisfechos + CTA: Agenda una consulta con el experto",
    max_words: 300
  },
  7: {
    subject: `Consulta inicial GRATIS esta semana, ${nombre}`,
    objective: "Oferta",
    structure: "Oferta exclusiva: 30 minutos de consulta gratuita + Que cubrimos en la consulta (analisis de marca, viabilidad de registro, costos estimados) + Valor real de la consulta (mencionar precio normal) + CTA: Agendar ahora - cupos limitados esta semana",
    max_words: 200
  },
  8: {
    subject: "Sigues interesado en proteger tu marca?",
    objective: "Re-engagement",
    structure: "Recordar el contacto inicial y el interes mostrado + Preguntar si sigue siendo prioridad la proteccion de marca + Ofrecer ayuda especifica segun su situacion + CTA: Responder este email para reconectar",
    max_words: 150
  },
  9: {
    subject: "Propiedad Intelectual en 2026: Lo que debes saber",
    objective: "Tendencias",
    structure: "Cambios legislativos relevantes en PI para 2026 + Nuevas oportunidades de proteccion (IA, marcas digitales) + Riesgos emergentes en el mercado colombiano + CTA: Mantente protegido - hablemos de tu estrategia",
    max_words: 300
  },
  10: {
    subject: `Ultima oportunidad: Consulta gratuita para ${empresa}`,
    objective: "Last Chance",
    structure: "Ultima oportunidad de agendar consulta gratuita + Recordar beneficios clave de la consulta + Fecha limite explicita (fin de semana/mes) + CTA urgente: Agendar hoy antes de que expire",
    max_words: 200
  },
  11: {
    subject: "Nos despedimos? (Por ahora)",
    objective: "Break-up",
    structure: "Reconocer que no ha habido respuesta a nuestros mensajes + Respetar su decision y tiempo + Dejar la puerta abierta para el futuro + CTA: Si cambias de opinion, estamos aqui - solo responde este email",
    max_words: 150
  },
  12: {
    subject: `Han pasado 3 meses, ${nombre}... hablamos?`,
    objective: "Win-back",
    structure: "Recordar el contacto inicial de hace 3 meses + Mencionar nueva oferta especial o recurso actualizado + Preguntar si la situacion o necesidades han cambiado + CTA: Reconectar con una breve llamada",
    max_words: 200
  }
};

// Retornar datos con template seleccionado
// Si la posicion no existe, usar template 1 como fallback
return {
  json: {
    ...($json),
    template: templates[position] || templates[1]
  }
};
