# Testing de Integración SUB-A ↔ SUB-D

**Fecha**: 11 de Enero, 2026
**Propósito**: Validar la integración de datos entre SUB-A (Lead Intake) y SUB-D (Nurturing Sequence) a través de Google Firestore.

---

## 📁 Archivos en este Directorio

| Archivo | Propósito | Tiempo |
|---------|-----------|--------|
| `README.md` | Índice y guía general | 5 min |
| `VALIDATION_SUMMARY.md` | Resumen ejecutivo para el usuario | 10 min |
| `integration_validation_report_v1.md` | Reporte técnico completo (10 secciones) | 30 min |
| `manual_validation_checklist.md` | Guía paso a paso con checklist interactivo | 60-90 min |
| `test_commands.sh` | Comandos curl listos para ejecutar | Referencia |

---

## 🎯 Objetivo de la Validación

Asegurar que los **5 campos de integración** funcionan correctamente:

1. `nurturing_status` - Control de elegibilidad para nurturing
2. `lead_captured_at` - Timestamp de captura del lead
3. `emails_sent` - Contador de emails enviados (0-12)
4. `last_email_sent` - Timestamp del último email
5. `last_email_position` - Posición en secuencia (0-12)

**Flujo de datos**:
```
SUB-A (escribe) → Firestore → SUB-D (lee/actualiza)
```

---

## 🚀 Inicio Rápido

### Paso 1: Leer el Resumen (10 min)
```bash
# Abrir VALIDATION_SUMMARY.md para contexto general
```

### Paso 2: Ejecutar Validación Manual (60-90 min)
```bash
# Abrir manual_validation_checklist.md
# Seguir los 5 pasos principales:
#   1. Validar SUB-A (15 min)
#   2. Validar SUB-D (20 min)
#   3. Testing Funcional (30 min)
#   4. Verificar Compatibilidad (10 min)
#   5. Documentar Resultados (10 min)
```

### Paso 3: Usar Comandos de Prueba (Referencia)
```bash
# Abrir test_commands.sh
# Copiar y ejecutar los comandos curl para enviar leads de prueba
```

### Paso 4: Revisar Reporte Técnico (Referencia)
```bash
# Abrir integration_validation_report_v1.md
# Para análisis detallado, riesgos, y recomendaciones
```

---

## 📋 Flujo de Trabajo Recomendado

```
1. Leer VALIDATION_SUMMARY.md
   ↓
2. Abrir manual_validation_checklist.md
   ↓
3. Ejecutar Paso 1: Validar SUB-A en n8n Cloud
   ├─ Verificar nodo "Save Lead to Firestore"
   ├─ Confirmar 5 campos de nurturing
   └─ Test workflow
   ↓
4. Ejecutar Paso 2: Validar SUB-D en n8n Cloud
   ├─ Verificar nodo "Query Active Leads"
   ├─ Confirmar filtros correctos
   ├─ Verificar nodo de actualización
   └─ Test workflow
   ↓
5. Ejecutar Paso 3: Testing Funcional
   ├─ Usar test_commands.sh para enviar lead de prueba
   ├─ Verificar en Firestore Console
   ├─ Ejecutar SUB-D manualmente
   └─ Verificar actualización en Firestore
   ↓
6. Ejecutar Paso 4: Verificar Compatibilidad
   ├─ Comparar nombres de campos
   ├─ Verificar collection names
   └─ Verificar credential IDs
   ↓
7. Ejecutar Paso 5: Documentar Resultados
   ├─ Completar checklist
   ├─ Tomar screenshots
   ├─ Documentar errores/warnings
   └─ Marcar decisión final
```

---

## 🔴 Riesgos Críticos a Verificar

### RIESGO-001: Nombres de Campos No Coinciden
**Verificación**: Comparar nombres en SUB-A y SUB-D (case-sensitive)
**Impacto**: Integración completamente rota si no coinciden

### RIESGO-002: Tipos de Datos Incompatibles
**Verificación**: emails_sent y last_email_position deben ser Number, no String
**Impacto**: Query de Firestore falla

### RIESGO-003: Collection Name Diferente
**Verificación**: Ambos workflows deben usar collection "leads"
**Impacto**: SUB-D no encuentra leads de SUB-A

---

## ✅ Criterios de Aprobación

### ✅ APROBAR SI:
- 0 errores críticos
- Nombres de campos coinciden
- Lead de prueba se guarda y actualiza correctamente
- Testing E2E exitoso

### ⚠️ APROBAR CON WARNINGS SI:
- 0 errores críticos
- Warnings menores documentados
- Plan de remediación definido

### ❌ RECHAZAR SI:
- 1+ errores críticos
- Campos no coinciden
- Testing funcional falla

---

## 🛠️ Herramientas Necesarias

### n8n Cloud
- **URL**: https://carrilloabgd.app.n8n.cloud
- **Workflows**:
  - SUB-A: ID `RHj1TAqBazxNFriJ`
  - SUB-D: ID `PZboUEnAxm5A7Lub`

### Google Firestore
- **Console**: https://console.cloud.google.com/firestore
- **Proyecto**: carrillo-marketing-core
- **Collection**: leads

### Testing Tools
- **curl** (Linux/Mac) - Para enviar leads de prueba
- **PowerShell** (Windows) - Invoke-RestMethod
- **Postman** (Opcional) - GUI para testing

---

## 📊 Test Cases Disponibles

| ID | Nombre | Propósito | Comando |
|----|--------|-----------|---------|
| TC-001 | Lead HOT | Verificar scoring alto | Ver test_commands.sh |
| TC-002 | Lead WARM | Verificar scoring medio | Ver test_commands.sh |
| TC-003 | Lead COLD | Verificar scoring bajo | Ver test_commands.sh |
| TC-004 | Email Inválido | Verificar error handling | Ver test_commands.sh |
| TC-005 | Campos Faltantes | Verificar validación | Ver test_commands.sh |

---

## 📸 Screenshots Requeridos

Tomar y guardar estos screenshots:

1. **SUB-A_firestore_config.png**
   - Nodo "Save Lead to Firestore" mostrando los 5 campos

2. **SUB-D_query_config.png**
   - Nodo "Query Active Leads" mostrando filtros

3. **SUB-D_update_config.png**
   - Nodo de actualización mostrando campos

4. **firestore_lead_document.png**
   - Documento en Firestore Console con todos los campos

5. **SUB-D_execution_success.png**
   - Ejecución exitosa de SUB-D con output

---

## 🔍 Verificaciones en Firestore

### Después de SUB-A
```json
{
  "nurturing_status": "active",
  "lead_captured_at": "2026-01-11T10:00:00.000Z",
  "emails_sent": 0,
  "last_email_sent": null,
  "last_email_position": 0
}
```

### Después de SUB-D (primer email)
```json
{
  "nurturing_status": "active",
  "lead_captured_at": "2026-01-11T10:00:00.000Z",
  "emails_sent": 1,                           // ← Incrementado
  "last_email_sent": "2026-01-11T16:00:00.000Z", // ← Timestamp actual
  "last_email_position": 1                    // ← Posición actualizada
}
```

---

## 📞 Soporte

### Si encuentras problemas:
1. **Revisar Troubleshooting**: Sección en `manual_validation_checklist.md`
2. **Revisar Riesgos**: Sección 5 de `integration_validation_report_v1.md`
3. **Escalar**: ingenieria@carrilloabgd.com

### Recursos Adicionales:
- **Arquitectura MW#1**: `automation/n8n-workflows/workflows/MEGA_WORKFLOW_1_LEAD_LIFECYCLE/ARQUITECTURA_MW1_V3_NATE_HERK.md`
- **Status MW#1**: `docs/n8n-workflows/mega-workflow-1/STATUS.md`
- **User Memory**: `~/.claude/CLAUDE.md`

---

## 📝 Documentar Resultados

Al completar la validación, actualizar:

1. **manual_validation_checklist.md**
   - Marcar todas las casillas
   - Documentar errores/warnings encontrados
   - Marcar decisión final

2. **integration_validation_report_v1.md**
   - Actualizar Sección 9 (Decisión Final) con resultados reales
   - Agregar screenshots como anexos

3. **Generar Reporte Final**
   ```bash
   > Actúa como Agente Validator y genera reporte final de SUB-A ↔ SUB-D
   ```

---

## 🎯 Métricas de Éxito

La validación es exitosa si:
- ✅ 5/5 campos de integración funcionan correctamente
- ✅ Lead de prueba pasa por todo el flujo sin errores
- ✅ Firestore se actualiza correctamente después de SUB-D
- ✅ 0 errores críticos encontrados

---

**Preparado por**: QA Specialist Agent
**Fecha**: 11 de Enero, 2026
**Estado**: Documentación completa - Lista para validación manual
**Tiempo total estimado**: 60-90 minutos
