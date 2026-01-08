# Plan de Operaciones para Actualizar SUB-A (ID: RHj1TAqBazxNFriJ)

**Fecha**: 2025-12-18
**Objetivo**: Aplicar reparaciones críticas identificadas en auditoría
**Método**: n8n_update_partial_workflow con operaciones granulares

---

## OPERACIONES REQUERIDAS

### 1. ACTUALIZAR NODO 3: Es Lead HOT? (If)
**Tipo**: updateNode
**Cambio**: Corregir conexiones (ya está bien configurado, solo ajustar connections)
**Notas**: El nodo en sí está correcto, el problema está en las conexiones

### 2. ACTUALIZAR NODO 4: Notificar Equipo (HOT)
**Tipo**: updateNode
**NodeId**: notify_team
**Cambios en parameters.message**:
```
ANTES: "{{ '🔥 NUEVO LEAD HOT: ' + $json.nombre + '\\nEmpresa: ' + $json.empresa + '\\nScore: ' + $json.score }}"

DESPUÉS: "={{ '🔥 NUEVO LEAD HOT\\n\\nNombre: ' + $json.nombre + '\\nEmail: ' + $json.email + '\\nEmpresa: ' + $json.empresa + '\\nScore: ' + $json.score + '\\nInterés: ' + $json.servicio_interes + '\\nMensaje: ' + $json.mensaje }}"
```

### 3. REEMPLAZAR NODO 5: Generar Respuesta (Gemini)
**Problema**: Cambio de tipo de nodo completo
**Estrategia**: removeNode + addNode
**Tipo anterior**: `@n8n/n8n-nodes-langchain.googleGemini`
**Tipo nuevo**: `@n8n/n8n-nodes-langchain.lmChatGoogleGemini`

**Configuración completa del nodo nuevo**:
```json
{
  "id": "generate_email_gemini",
  "name": "5. Generar Respuesta (Gemini)",
  "type": "@n8n/n8n-nodes-langchain.lmChatGoogleGemini",
  "typeVersion": 1.1,
  "position": [1568, 304],
  "parameters": {
    "modelId": {
      "mode": "list",
      "value": "gemini-1.5-pro-latest"
    },
    "prompt": "={{ \"Genera un email de respuesta personalizado para este lead:\\n\\nNombre: \" + $json.nombre + \"\\nEmpresa: \" + $json.empresa + \"\\nInterés: \" + $json.servicio_interes + \"\\nMensaje: \" + $json.mensaje + \"\\n\\nEl email debe:\\n1. Agradecer el contacto\\n2. Confirmar que hemos recibido su consulta\\n3. Mencionar que el Dr. Omar Carrillo se pondrá en contacto pronto\\n4. Ser profesional pero cálido\\n5. Firma: Equipo Carrillo Abogados\\n\\nResponde SOLO con el texto del email, sin subject.\" }}",
    "options": {}
  },
  "credentials": {
    "googleGeminiOAuth2Api": {
      "id": "jk2FHcbAC71LuRl2",
      "name": "Google Gemini(PaLM) Api account"
    }
  }
}
```

### 4. ACTUALIZAR NODO 6: Enviar Respuesta Lead
**Tipo**: updateNode
**NodeId**: send_response
**Cambio en parameters.message**:
```
ANTES: "={{ $json.text }}"
DESPUÉS: "={{ $json.response }}"
```

### 5. ACTUALIZAR NODO FINAL: Posición
**Tipo**: moveNode
**NodeId**: final_result
**Posición**:
```
ANTES: [688, 304]
DESPUÉS: [2016, 304]
```

### 6. CORREGIR CONEXIONES DEL NODO 3 (If)

**Conexiones actuales (INCORRECTAS)**:
```json
"3. Es Lead HOT? (If)": {
  "main": [
    [
      { "node": "4. Notificar Equipo (HOT)", "type": "main", "index": 0 }
    ],
    [
      { "node": "4. Notificar Equipo (HOT)", "type": "main", "index": 0 }  // ❌ INCORRECTO
    ]
  ]
}
```

**Conexiones corregidas (CORRECTAS)**:
```json
"3. Es Lead HOT? (If)": {
  "main": [
    [
      { "node": "4. Notificar Equipo (HOT)", "type": "main", "index": 0 }  // TRUE
    ],
    [
      { "node": "5. Generar Respuesta (Gemini)", "type": "main", "index": 0 }  // FALSE ✅
    ]
  ]
}
```

---

## ORDEN DE EJECUCIÓN

1. Actualizar Nodo 4 (Notificar - mejorar mensaje)
2. Eliminar Nodo 5 actual (Gemini viejo)
3. Remover conexiones relacionadas con Nodo 5
4. Agregar Nodo 5 nuevo (Gemini correcto)
5. Actualizar Nodo 6 (Gmail - campo correcto)
6. Mover Nodo FINAL a posición correcta
7. Reconstruir conexiones:
   - Nodo 3 (If) TRUE → Nodo 4
   - Nodo 3 (If) FALSE → Nodo 5
   - Nodo 4 → Nodo 5
   - Nodo 5 → Nodo 6
   - Nodo 6 → Nodo FINAL

---

## ESTRATEGIA ALTERNATIVA

Dado que los cambios son extensos y afectan la estructura de conexiones, una estrategia más segura podría ser:

1. Exportar backup del workflow actual
2. Crear una nueva versión importando el archivo reparado
3. Validar la nueva versión
4. Si funciona, activarla

Sin embargo, esto crearía un workflow nuevo. Para mantener el mismo ID, debemos usar update_partial_workflow.

---

## NOTAS IMPORTANTES

- Las credenciales deben mantenerse con los mismos IDs
- Validar cada operación antes de aplicar la siguiente
- Usar validateOnly: true primero para preview
- Hacer backup antes de aplicar cambios

---

## PRÓXIMO PASO

Ejecutar las operaciones con n8n_update_partial_workflow
