#!/bin/bash

# Test Commands para Validación SUB-A ↔ SUB-D
# Fecha: 11 de Enero, 2026
# Propósito: Comandos listos para ejecutar testing manual

echo "======================================"
echo "TEST COMMANDS: SUB-A ↔ SUB-D Integration"
echo "======================================"
echo ""

# Variables de configuración
N8N_URL="https://carrilloabgd.app.n8n.cloud"
WEBHOOK_ENDPOINT="/webhook/lead-events"
WEBHOOK_V3_ENDPOINT="/webhook/lead-events-v3"

echo "⚙️ Configuración:"
echo "  - n8n Cloud: $N8N_URL"
echo "  - Webhook v1: $WEBHOOK_ENDPOINT"
echo "  - Webhook v3: $WEBHOOK_V3_ENDPOINT"
echo ""

# ============================================
# TEST CASE 1: Lead Válido - HOT
# ============================================
echo "📋 TEST CASE 1: Lead Válido - HOT (Score >= 70)"
echo "Expected: SUB-A guarda con nurturing_status=active, emails_sent=0"
echo ""
echo "Comando curl:"
echo "-------------"
cat << 'EOF'
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "email": "qa.hot.lead@techstartup.co",
    "nombre": "Carlos Emprendedor",
    "empresa": "TechStartup SAS",
    "telefono": "+57 300 123 4567",
    "cargo": "CEO",
    "interes": "Registro de marca",
    "mensaje": "Necesito registrar la marca de mi startup tech que está creciendo rápidamente. Tenemos inversión y queremos proteger nuestra propiedad intelectual antes de expandirnos internacionalmente."
  }'
EOF
echo ""
echo "Verificar en Firestore:"
echo "  1. Document ID: [copiar de respuesta]"
echo "  2. nurturing_status = 'active'"
echo "  3. lead_captured_at = [timestamp ISO]"
echo "  4. emails_sent = 0"
echo "  5. last_email_sent = null"
echo "  6. last_email_position = 0"
echo ""
echo "========================================"
echo ""

# ============================================
# TEST CASE 2: Lead Válido - WARM
# ============================================
echo "📋 TEST CASE 2: Lead Válido - WARM (Score 40-69)"
echo "Expected: SUB-A guarda con nurturing_status=active, emails_sent=0"
echo ""
echo "Comando curl:"
echo "-------------"
cat << 'EOF'
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "email": "qa.warm.lead@example.com",
    "nombre": "María González",
    "empresa": "Comercializadora ABC",
    "telefono": "+57 301 234 5678",
    "interes": "Consultoría empresarial",
    "mensaje": "Necesito asesoría legal para mi negocio"
  }'
EOF
echo ""
echo "Verificar en Firestore: mismos campos que TEST CASE 1"
echo ""
echo "========================================"
echo ""

# ============================================
# TEST CASE 3: Lead Válido - COLD
# ============================================
echo "📋 TEST CASE 3: Lead Válido - COLD (Score < 40)"
echo "Expected: SUB-A guarda con nurturing_status=active, emails_sent=0"
echo ""
echo "Comando curl:"
echo "-------------"
cat << 'EOF'
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "email": "qa.cold.lead@gmail.com",
    "nombre": "Pedro",
    "interes": "Información general",
    "mensaje": "Hola"
  }'
EOF
echo ""
echo "Verificar en Firestore: mismos campos que TEST CASE 1"
echo ""
echo "========================================"
echo ""

# ============================================
# TEST CASE 4: Email Inválido (Error Case)
# ============================================
echo "📋 TEST CASE 4: Email Inválido (Error Case)"
echo "Expected: Error, NO se guarda en Firestore"
echo ""
echo "Comando curl:"
echo "-------------"
cat << 'EOF'
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email",
    "nombre": "Lead Inválido",
    "empresa": "Test Company",
    "interes": "Marcas",
    "mensaje": "Mensaje de prueba"
  }'
EOF
echo ""
echo "Verificar: Debe retornar error 400 o mensaje de validación"
echo ""
echo "========================================"
echo ""

# ============================================
# TEST CASE 5: Campos Faltantes (Error Case)
# ============================================
echo "📋 TEST CASE 5: Campos Faltantes (Error Case)"
echo "Expected: Error, NO se guarda en Firestore"
echo ""
echo "Comando curl:"
echo "-------------"
cat << 'EOF'
curl -X POST https://carrilloabgd.app.n8n.cloud/webhook/lead-events \
  -H "Content-Type: application/json" \
  -d '{
    "email": "qa.incomplete@example.com"
  }'
EOF
echo ""
echo "Verificar: Debe retornar error indicando campos faltantes"
echo ""
echo "========================================"
echo ""

# ============================================
# TESTING SUB-D
# ============================================
echo "🔄 TESTING SUB-D (Nurturing Sequence Engine)"
echo ""
echo "⚠️ IMPORTANTE: SUB-D NO se puede ejecutar con curl"
echo "   Debe ejecutarse manualmente desde n8n Cloud"
echo ""
echo "Pasos para ejecutar SUB-D:"
echo "  1. Ir a https://carrilloabgd.app.n8n.cloud"
echo "  2. Abrir workflow 'SUB-D: Nurturing Sequence Engine' (ID: PZboUEnAxm5A7Lub)"
echo "  3. Click en botón 'Test workflow' (arriba a la derecha)"
echo "  4. Esperar a que termine la ejecución"
echo "  5. Verificar output de cada nodo"
echo ""
echo "Verificaciones después de ejecutar SUB-D:"
echo "  1. Nodo 'Query Active Leads' debe retornar los leads de TEST CASE 1-3"
echo "  2. Verificar que se envían emails (o se intenta enviar)"
echo "  3. Verificar en Firestore que los campos se actualizaron:"
echo "     - emails_sent = 1 (incrementado)"
echo "     - last_email_sent = [timestamp actual]"
echo "     - last_email_position = 1"
echo "     - nurturing_status = 'active' (todavía activo)"
echo ""
echo "========================================"
echo ""

# ============================================
# VERIFICACIÓN EN FIRESTORE
# ============================================
echo "🔍 VERIFICACIÓN EN FIRESTORE CONSOLE"
echo ""
echo "URL: https://console.cloud.google.com/firestore"
echo ""
echo "Pasos:"
echo "  1. Seleccionar proyecto: carrillo-marketing-core"
echo "  2. Navegar a collection: leads"
echo "  3. Buscar documentos por email:"
echo "     - qa.hot.lead@techstartup.co"
echo "     - qa.warm.lead@example.com"
echo "     - qa.cold.lead@gmail.com"
echo ""
echo "Verificar campos en cada documento:"
echo "  - nurturing_status (String)"
echo "  - lead_captured_at (Timestamp)"
echo "  - emails_sent (Number)"
echo "  - last_email_sent (Timestamp o null)"
echo "  - last_email_position (Number)"
echo ""
echo "========================================"
echo ""

# ============================================
# COMANDOS ÚTILES
# ============================================
echo "🛠️ COMANDOS ÚTILES"
echo ""
echo "Ver últimas ejecuciones de SUB-A:"
echo "  https://carrilloabgd.app.n8n.cloud/workflow/RHj1TAqBazxNFriJ/executions"
echo ""
echo "Ver últimas ejecuciones de SUB-D:"
echo "  https://carrilloabgd.app.n8n.cloud/workflow/PZboUEnAxm5A7Lub/executions"
echo ""
echo "Firestore Console:"
echo "  https://console.cloud.google.com/firestore/databases/-default-/data/panel/leads"
echo ""
echo "========================================"
echo ""

# ============================================
# TESTING CON POSTMAN (OPCIONAL)
# ============================================
echo "📮 TESTING CON POSTMAN (Opcional)"
echo ""
echo "Si prefieres usar Postman en lugar de curl:"
echo ""
echo "Configuración de request:"
echo "  - Method: POST"
echo "  - URL: https://carrilloabgd.app.n8n.cloud/webhook/lead-events"
echo "  - Headers:"
echo "      Content-Type: application/json"
echo "  - Body (raw JSON):"
echo "      [Copiar JSON de cualquier TEST CASE]"
echo ""
echo "========================================"
echo ""

# ============================================
# WINDOWS POWERSHELL COMMANDS
# ============================================
echo "🪟 COMANDOS PARA WINDOWS POWERSHELL"
echo ""
echo "Si estás en Windows PowerShell, usa estos comandos:"
echo ""
echo "TEST CASE 1 (PowerShell):"
echo "-------------------------"
cat << 'EOF'
$body = @{
    email = "qa.hot.lead@techstartup.co"
    nombre = "Carlos Emprendedor"
    empresa = "TechStartup SAS"
    telefono = "+57 300 123 4567"
    cargo = "CEO"
    interes = "Registro de marca"
    mensaje = "Necesito registrar la marca de mi startup tech que está creciendo rápidamente."
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://carrilloabgd.app.n8n.cloud/webhook/lead-events" -Method POST -Body $body -ContentType "application/json"
EOF
echo ""
echo "========================================"
echo ""

# ============================================
# CHECKLIST FINAL
# ============================================
echo "✅ CHECKLIST DE VALIDACIÓN"
echo ""
echo "Completar después de ejecutar todos los tests:"
echo ""
echo "[ ] TEST CASE 1: Lead HOT enviado y verificado en Firestore"
echo "[ ] TEST CASE 2: Lead WARM enviado y verificado en Firestore"
echo "[ ] TEST CASE 3: Lead COLD enviado y verificado en Firestore"
echo "[ ] TEST CASE 4: Error de email inválido manejado correctamente"
echo "[ ] TEST CASE 5: Error de campos faltantes manejado correctamente"
echo "[ ] SUB-D ejecutado manualmente desde n8n Cloud"
echo "[ ] SUB-D seleccionó los leads correctamente (Query Active Leads)"
echo "[ ] SUB-D envió emails (o intentó enviar)"
echo "[ ] Firestore actualizado con emails_sent = 1"
echo "[ ] Firestore actualizado con last_email_sent = [timestamp]"
echo "[ ] Firestore actualizado con last_email_position = 1"
echo "[ ] nurturing_status sigue siendo 'active'"
echo "[ ] Screenshots tomados (SUB-A config, SUB-D config, Firestore docs)"
echo "[ ] manual_validation_checklist.md completado"
echo "[ ] Decisión documentada (Aprobar/Aprobar con Warnings/Rechazar)"
echo ""
echo "========================================"
echo ""

echo "✨ Testing suite preparado"
echo "   Ejecuta los comandos curl uno por uno"
echo "   Verifica cada resultado en Firestore"
echo "   Documenta hallazgos en manual_validation_checklist.md"
echo ""
echo "🎯 Objetivo: 5 campos de integración funcionando correctamente"
echo "   - nurturing_status"
echo "   - lead_captured_at"
echo "   - emails_sent"
echo "   - last_email_sent"
echo "   - last_email_position"
echo ""
echo "📞 Soporte: ingenieria@carrilloabgd.com"
echo ""
