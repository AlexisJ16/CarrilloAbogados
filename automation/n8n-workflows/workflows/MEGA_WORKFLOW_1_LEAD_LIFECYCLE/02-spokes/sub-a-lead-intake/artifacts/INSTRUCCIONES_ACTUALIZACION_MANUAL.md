# INSTRUCCIONES PARA ACTUALIZAR SUB-A MANUALMENTE EN N8N CLOUD

**Workflow ID**: RHj1TAqBazxNFriJ
**Nombre**: SUB-A: Lead Intake & Enrichment
**Versión Objetivo**: v3 (REPARADO)
**Fecha**: 2025-12-18

---

## OPCIÓN 1: ACTUALIZACIÓN RÁPIDA (RECOMENDADA)

### Método: Importar JSON Reparado

1. **Abrir n8n Cloud**
   - URL: https://carrilloabgd.app.n8n.cloud
   - Iniciar sesión

2. **Hacer Backup del Workflow Actual**
   - Navegar al workflow SUB-A (ID: RHj1TAqBazxNFriJ)
   - Clic en el menú (tres puntos)
   - Seleccionar "Download"
   - Guardar como `SUB-A_backup_v2_pre-reparacion.json`

3. **Importar Versión Reparada**
   - En el mismo workflow (SUB-A)
   - Clic en el menú (tres puntos)
   - Seleccionar "Import from File"
   - Seleccionar archivo: `C:\Automatizaciones\n8n-antigravity\04-workflows\MEGA_WORKFLOW_1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\artifacts\SUB-A_REPARADO_v3.json`
   - Confirmar la importación

4. **Verificar Credenciales**
   - Revisar que los nodos mantengan las credenciales:
     - Nodo 2 (Firestore): Credencial `AAhdRNGzvsFnYN9O`
     - Nodo 4 (Gmail Notificar): Credencial `l2mMgEf8YUV7HHlK`
     - Nodo 5 (Gemini): Credencial `jk2FHcbAC71LuRl2`
     - Nodo 6 (Gmail Enviar): Credencial `l2mMgEf8YUV7HHlK`

5. **Guardar**
   - Clic en "Save" (esquina superior derecha)
   - El workflow se guardará con el mismo ID

---

## OPCIÓN 2: ACTUALIZACIÓN MANUAL NODO POR NODO

Si prefieres hacer cambios granulares:

### PASO 1: Actualizar Nodo 4 (Notificar Equipo)

1. Abrir nodo "4. Notificar Equipo (HOT)"
2. En el campo "Message", reemplazar con:
```
={{ '�� NUEVO LEAD HOT\n\nNombre: ' + $json.nombre + '\nEmail: ' + $json.email + '\nEmpresa: ' + $json.empresa + '\nScore: ' + $json.score + '\nInterés: ' + $json.servicio_interes + '\nMensaje: ' + $json.mensaje }}
```
3. Guardar nodo

### PASO 2: Reemplazar Nodo 5 (Gemini)

**ELIMINAR NODO ACTUAL**:
1. Seleccionar nodo "5. Generar Respuesta (Gemini)"
2. Presionar Delete
3. Confirmar eliminación

**AGREGAR NODO NUEVO**:
1. Clic en el botón "+" entre los nodos
2. Buscar "Google Gemini Chat Model"
3. Seleccionar el nodo `@n8n/n8n-nodes-langchain.lmChatGoogleGemini`
4. Configurar:
   - **Name**: 5. Generar Respuesta (Gemini)
   - **Model**: gemini-1.5-pro-latest
   - **Prompt**:
```
={{ "Genera un email de respuesta personalizado para este lead:\n\nNombre: " + $json.nombre + "\nEmpresa: " + $json.empresa + "\nInterés: " + $json.servicio_interes + "\nMensaje: " + $json.mensaje + "\n\nEl email debe:\n1. Agradecer el contacto\n2. Confirmar que hemos recibido su consulta\n3. Mencionar que el Dr. Omar Carrillo se pondrá en contacto pronto\n4. Ser profesional pero cálido\n5. Firma: Equipo Carrillo Abogados\n\nResponde SOLO con el texto del email, sin subject." }}
```
   - **Credentials**: Seleccionar "Google Gemini(PaLM) Api account" (ID: jk2FHcbAC71LuRl2)
5. Posición: [1568, 304]
6. Guardar nodo

### PASO 3: Actualizar Nodo 6 (Enviar Respuesta)

1. Abrir nodo "6. Enviar Respuesta Lead"
2. En el campo "Message", cambiar de:
   - ANTES: `={{ $json.text }}`
   - DESPUÉS: `={{ $json.response }}`
3. Guardar nodo

### PASO 4: Mover Nodo FINAL

1. Seleccionar nodo "FINAL. Resultado del Sub-Workflow"
2. Arrastrarlo hasta el final del flujo (a la derecha del nodo 6)
3. Posición aproximada: X=2016, Y=304

### PASO 5: Corregir Conexiones del Nodo 3 (If)

**VERIFICAR CONEXIONES ACTUALES**:
1. Seleccionar nodo "3. Es Lead HOT? (If)"
2. Ver las dos salidas (outputs)

**DEBE QUEDAR ASÍ**:
- **TRUE (output superior)**:
  - Conectar a "4. Notificar Equipo (HOT)"

- **FALSE (output inferior)**:
  - Conectar a "5. Generar Respuesta (Gemini)"

**CONEXIÓN ADICIONAL**:
- Desde "4. Notificar Equipo (HOT)" → "5. Generar Respuesta (Gemini)"

**FLUJO COMPLETO**:
```
[If] → TRUE → [4. Notificar] → [5. Gemini] → [6. Enviar] → [FINAL]
     → FALSE → [5. Gemini] → [6. Enviar] → [FINAL]
```

### PASO 6: Guardar y Verificar

1. Guardar workflow (Ctrl+S o botón "Save")
2. Verificar visualmente el flujo completo
3. No hay errores rojos en ningún nodo
4. Todas las conexiones están correctas

---

## OPCIÓN 3: ACTUALIZACIÓN VÍA API (AVANZADO)

Si tienes acceso a la API de n8n:

### Usar cURL para actualizar el workflow

```bash
# 1. Exportar workflow actual (backup)
curl -X GET \
  https://carrilloabgd.app.n8n.cloud/api/v1/workflows/RHj1TAqBazxNFriJ \
  -H "X-N8N-API-KEY: YOUR_API_KEY" \
  > SUB-A_backup.json

# 2. Actualizar con versión reparada
curl -X PUT \
  https://carrilloabgd.app.n8n.cloud/api/v1/workflows/RHj1TAqBazxNFriJ \
  -H "Content-Type: application/json" \
  -H "X-N8N-API-KEY: YOUR_API_KEY" \
  -d @SUB-A_REPARADO_v3.json
```

**NOTA**: Reemplazar `YOUR_API_KEY` con tu API key de n8n Cloud.

---

## VERIFICACIÓN POST-ACTUALIZACIÓN

Después de aplicar cualquiera de las opciones, verificar:

### Checklist Visual

- [ ] Nodo 0: "Mapear Input del Orquestador" - sin cambios
- [ ] Nodo 1: "Validar y Clasificar" - sin cambios
- [ ] Nodo 2: "Guardar en Firestore" - sin cambios
- [ ] Nodo 3: "Es Lead HOT? (If)" - conexiones correctas (TRUE→4, FALSE→5)
- [ ] Nodo 4: "Notificar Equipo (HOT)" - mensaje expandido con todos los campos
- [ ] Nodo 5: "Generar Respuesta (Gemini)" - tipo `lmChatGoogleGemini`, prompt completo
- [ ] Nodo 6: "Enviar Respuesta Lead" - campo `$json.response` (no `$json.text`)
- [ ] Nodo FINAL: "Resultado del Sub-Workflow" - posición al final del flujo

### Checklist de Conexiones

- [ ] Trigger → Nodo 0
- [ ] Nodo 0 → Nodo 1
- [ ] Nodo 1 → Nodo 2
- [ ] Nodo 2 → Nodo 3
- [ ] Nodo 3 (TRUE) → Nodo 4
- [ ] Nodo 3 (FALSE) → Nodo 5
- [ ] Nodo 4 → Nodo 5
- [ ] Nodo 5 → Nodo 6
- [ ] Nodo 6 → Nodo FINAL

### Checklist de Credenciales

- [ ] Nodo 2: Firestore credencial configurada
- [ ] Nodo 4: Gmail OAuth2 credencial configurada
- [ ] Nodo 5: Google Gemini credencial configurada
- [ ] Nodo 6: Gmail OAuth2 credencial configurada

---

## TESTING POST-ACTUALIZACIÓN

Una vez actualizado, ejecutar tests con el agente QA Specialist:

```bash
# Test 1: Lead HOT
{
  "nombre": "María Test",
  "email": "maria.test@techcorp.co",
  "telefono": "+573101234567",
  "empresa": "TechCorp Test SAS",
  "cargo": "CEO",
  "servicio_interes": "Registro de Marca",
  "mensaje": "Necesitamos proteger nuestra marca de software urgentemente con más de 50 caracteres para activar scoring",
  "utm_source": "google",
  "utm_campaign": "test-hot-lead"
}

# Test 2: Lead WARM
{
  "nombre": "Carlos Test",
  "email": "carlos@gmail.com",
  "empresa": "PyME Test",
  "servicio_interes": "Consulta General",
  "mensaje": "Información básica",
  "utm_source": "facebook"
}
```

---

## PROBLEMAS COMUNES Y SOLUCIONES

### Problema 1: "Credential not found"

**Solución**:
1. Ir a Settings → Credentials
2. Buscar las credenciales por ID:
   - `AAhdRNGzvsFnYN9O` (Firestore)
   - `l2mMgEf8YUV7HHlK` (Gmail)
   - `jk2FHcbAC71LuRl2` (Gemini)
3. Si no existen, crearlas nuevamente
4. Actualizar los nodos con las nuevas credenciales

### Problema 2: "Node type not found: @n8n/n8n-nodes-langchain.lmChatGoogleGemini"

**Solución**:
1. Verificar que tu instancia de n8n Cloud esté actualizada
2. Si el nodo no está disponible, usar un nodo alternativo compatible
3. Contactar soporte de n8n para confirmar disponibilidad

### Problema 3: Expresiones n8n no funcionan

**Verificar**:
- Todas las expresiones deben empezar con `={{`
- No con `{{` solamente
- Ejemplo correcto: `={{ $json.email }}`
- Ejemplo incorrecto: `{{ $json.email }}`

---

## RECOMENDACIÓN FINAL

**OPCIÓN 1 es la más rápida y segura**: Importar el archivo JSON reparado reemplazará todo el contenido del workflow manteniendo el mismo ID.

Si tienes problemas, usar OPCIÓN 2 para hacer cambios granulares nodo por nodo.

---

## ARCHIVOS DE REFERENCIA

- **Workflow Reparado**: `C:\Automatizaciones\n8n-antigravity\04-workflows\MEGA_WORKFLOW_1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\artifacts\SUB-A_REPARADO_v3.json`
- **Reporte de Auditoría**: `C:\Automatizaciones\n8n-antigravity\04-workflows\MEGA_WORKFLOW_1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\testing\AUDITORIA_Y_REPARACION_FINAL.md`
- **Plan de Operaciones**: `C:\Automatizaciones\n8n-antigravity\04-workflows\MEGA_WORKFLOW_1_LEAD_LIFECYCLE\02-spokes\sub-a-lead-intake\artifacts\update_operations_plan.md`

---

## PRÓXIMO PASO

Después de actualizar el workflow:

1. Guardar los cambios
2. Ejecutar tests E2E con datos de prueba
3. Si todo funciona correctamente, documentar en el reporte final
4. Activar el workflow para uso en producción

**IMPORTANTE**: No activar el workflow hasta que todos los tests pasen exitosamente.
