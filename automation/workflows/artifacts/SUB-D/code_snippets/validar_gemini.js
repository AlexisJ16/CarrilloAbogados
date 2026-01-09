// ===================================================================
// Nodo 10: Validar Output Gemini
// Workflow: SUB-D: Nurturing Sequence Engine
// Proposito: Asegurar que Gemini genero un email valido y aplicar fallback si fallo
// ===================================================================

const input = $input.item.json;
const previousData = $('Cargar Template Email').item.json;

// Extraer respuesta de Gemini
let geminiOutput;

try {
  // La respuesta de Gemini API viene en candidates[0].content.parts[0].text
  const textContent = input.candidates?.[0]?.content?.parts?.[0]?.text;

  if (!textContent) {
    throw new Error('No se encontro contenido en respuesta de Gemini');
  }

  // Limpiar el texto de posibles marcadores markdown
  // Gemini a veces envuelve JSON en bloques de codigo
  let cleanedText = textContent.trim();

  if (cleanedText.startsWith('```json')) {
    cleanedText = cleanedText.slice(7);
  }
  if (cleanedText.startsWith('```')) {
    cleanedText = cleanedText.slice(3);
  }
  if (cleanedText.endsWith('```')) {
    cleanedText = cleanedText.slice(0, -3);
  }
  cleanedText = cleanedText.trim();

  // Parsear JSON
  geminiOutput = JSON.parse(cleanedText);

} catch (e) {
  // Fallback: Si Gemini falla, usar template sin personalizar
  // Esto asegura que siempre enviemos un email, aunque sea generico
  console.log('Gemini parsing failed, using fallback template:', e.message);

  geminiOutput = {
    subject: previousData.template?.subject || 'Gracias por contactar a Carrillo Abogados',
    body: `Estimado/a ${previousData.nombre},

Gracias por su interes en nuestros servicios legales.

En Carrillo Abogados contamos con mas de 15 anos de experiencia en Propiedad Intelectual y Registro de Marcas ante la Superintendencia de Industria y Comercio.

Estamos a su disposicion para resolver cualquier consulta sobre proteccion de marcas, patentes o derechos de autor.

Atentamente,
Dr. Omar Carrillo
Carrillo Abogados
Tel: +57 2 XXX XXXX`
  };
}

// Validar campos requeridos - subject
if (!geminiOutput.subject || geminiOutput.subject.trim() === '') {
  geminiOutput.subject = previousData.template?.subject || 'Mensaje de Carrillo Abogados';
}

// Validar campos requeridos - body
if (!geminiOutput.body || geminiOutput.body.trim() === '') {
  geminiOutput.body = `Estimado/a ${previousData.nombre},

Gracias por su interes en nuestros servicios.

Quedamos atentos a cualquier consulta.

Atentamente,
Dr. Omar Carrillo
Carrillo Abogados`;
}

// Retornar datos limpios y consolidados
// Incluimos todos los campos necesarios para los nodos siguientes
return {
  json: {
    // Datos del lead
    lead_id: previousData.lead_id,
    nombre: previousData.nombre,
    email: previousData.email,
    empresa: previousData.empresa,
    servicio: previousData.servicio,

    // Datos de posicion
    position: previousData.position,
    emails_sent: previousData.emails_sent,
    days_since_capture: previousData.days_since_capture,
    next_email_date: previousData.next_email_date,

    // Email generado
    subject: geminiOutput.subject,
    body: geminiOutput.body,

    // Metadata
    generated_by: 'gemini',
    generated_at: new Date().toISOString()
  }
};
