# Credenciales Pendientes: SUB-D Nurturing Sequence Engine

**Fecha**: 7 de Enero, 2026
**Workflow ID**: `PZboUEnAxm5A7Lub`
**Estado**: BLOQUEADO hasta configurar credenciales

---

## Resumen de Credenciales

| Credencial | Servicio | Estado | Prioridad |
|------------|----------|--------|-----------|
| httpHeaderAuth (Mailersend) | Email sending | **NO CONFIGURADA** | BLOQUEANTE |
| googleApi (Firestore) | Database | Existente, verificar | BLOQUEANTE |
| googlePalmApi (Gemini) | AI Personalization | Existente, verificar | BLOQUEANTE |
| BACKEND_URL | Callback | **NO CONFIGURADA** | Opcional |

---

## 1. MAILERSEND - Configuracion Completa

### Paso 1: Crear Cuenta Mailersend

1. Ir a https://mailersend.com
2. Click en "Sign Up Free"
3. Registrar con email corporativo: `ingenieria@carrilloabgd.com`
4. Verificar email de confirmacion
5. Completar wizard de configuracion inicial

### Paso 2: Verificar Dominio

**Importante**: Sin dominio verificado, los emails se enviaran desde @mailersend.net

1. En dashboard Mailersend, ir a **Email > Domains**
2. Click **Add Domain**
3. Ingresar: `carrilloabgd.com`
4. Copiar los registros DNS proporcionados:

```
TXT Record: ms._domainkey.carrilloabgd.com
Value: (proporcionado por Mailersend)

TXT Record: _dmarc.carrilloabgd.com
Value: v=DMARC1; p=none; rua=mailto:dmarc@carrilloabgd.com

MX Record: (si aplica para recepcion)
```

5. Agregar registros en el DNS del dominio (GoDaddy, Cloudflare, etc.)
6. Esperar propagacion DNS (hasta 24h)
7. Click **Verify** en Mailersend

### Paso 3: Generar API Key

1. En dashboard Mailersend, ir a **Integration > API Tokens**
2. Click **Create New Token**
3. Configurar:
   - **Token Name**: `n8n-nurturing-subd`
   - **Permissions**:
     - [x] Email: Full Access
     - [x] Templates: Read
   - **Domain**: `carrilloabgd.com` (o All Domains)
4. Click **Create**
5. **COPIAR API KEY INMEDIATAMENTE** (solo se muestra una vez)

### Paso 4: Crear Credencial en n8n Cloud

1. Ir a https://carrilloabgd.app.n8n.cloud
2. Click en **Credentials** (menu lateral)
3. Click **Add Credential**
4. Buscar: **Header Auth**
5. Configurar:

```
Name: Mailersend API Key
Header Auth Type: Authorization Header
Header Name: Authorization
Header Value: Bearer YOUR_API_KEY_HERE
```

6. Click **Save**
7. Copiar el ID de la credencial para referencia

### Paso 5: Asignar Credencial al Nodo

1. Abrir workflow SUB-D: `PZboUEnAxm5A7Lub`
2. Click en nodo **Enviar Email con Mailersend**
3. En **Credential for Header Auth**, seleccionar `Mailersend API Key`
4. Guardar workflow

---

## 2. GOOGLE API (Firestore) - Verificacion

### Verificar Credencial Existente

1. En n8n Cloud, ir a **Credentials**
2. Buscar credencial con tipo `Google API`
3. Verificar que incluya scope: `https://www.googleapis.com/auth/datastore`

### Si No Existe o Esta Incorrecta

1. Ir a Google Cloud Console: https://console.cloud.google.com
2. Proyecto: `carrillo-marketing-core`
3. Navegar a **APIs & Services > Credentials**
4. Crear **Service Account** o usar existente
5. Generar **JSON Key**
6. En n8n Cloud, crear credencial:

```
Type: Google API
Auth Method: Service Account JSON
JSON Key: (pegar contenido del archivo JSON)
```

### Verificar Permisos Firestore

En la cuenta de servicio, verificar roles:
- `roles/datastore.user` (minimo)
- `roles/datastore.owner` (recomendado para desarrollo)

---

## 3. GOOGLE GEMINI API - Verificacion

### Credencial Referenciada

El workflow referencia credencial ID: `jk2FHcbAC71LuRl2` (googlePalmApi)

### Verificar API Key

1. Ir a Google AI Studio: https://aistudio.google.com/app/apikey
2. Verificar que exista API Key activa
3. En n8n Cloud, verificar credencial `Google PaLM API`:

```
Type: Google PaLM API
API Key: (tu API key)
```

### Si Necesitas Nueva API Key

1. En Google AI Studio, click **Create API Key**
2. Seleccionar proyecto: `carrillo-marketing-core`
3. Copiar API Key
4. Actualizar en n8n Cloud

---

## 4. VARIABLE DE ENTORNO: BACKEND_URL

### Configurar en n8n Cloud

1. Ir a https://carrilloabgd.app.n8n.cloud
2. Click en **Settings** (engranaje)
3. Navegar a **Variables**
4. Click **Add Variable**
5. Configurar:

```
Name: BACKEND_URL
Value: http://localhost:8800
Type: String
```

**Valores segun ambiente:**

| Ambiente | Valor |
|----------|-------|
| Desarrollo | `http://localhost:8800` |
| Staging | `https://staging-api.carrilloabgd.com/n8n-integration-service` |
| Produccion | `https://api.carrilloabgd.com/n8n-integration-service` |

### Nota

El nodo "Callback: Nurturing Sent" tiene fallback:
```
{{ $env.BACKEND_URL || 'http://localhost:8800' }}
```

Si BACKEND_URL no esta configurado, usara localhost (no funcionara en produccion pero no bloqueara el workflow).

---

## 5. TESTING POST-CONFIGURACION

### Crear Lead de Prueba en Firestore

En Google Cloud Console > Firestore:

1. Ir a collection `leads`
2. Click **Add Document**
3. Document ID: `test-nurturing-subd-001`
4. Campos:

```json
{
  "lead_id": "test-nurturing-subd-001",
  "nombre": "Juan Prueba",
  "email": "TU_EMAIL_PERSONAL@gmail.com",
  "empresa": "Empresa Test SAS",
  "servicio": "Registro de Marca",
  "categoria": "WARM",
  "score": 55,
  "status": "nuevo",
  "emails_sent": 0,
  "created_at": "2026-01-04T00:00:00Z",
  "next_email_date": "2026-01-07T00:00:00Z",
  "processed_at": "2026-01-04T00:00:00Z"
}
```

**Nota**: `created_at` debe ser 3 dias antes de hoy para que calcule posicion = 2.

### Ejecutar Test Manual

1. Abrir workflow SUB-D
2. Click **Execute Workflow** (boton manual)
3. Observar cada nodo en el canvas
4. Verificar:
   - [x] Query retorna el lead de prueba
   - [x] Posicion calculada = 2
   - [x] Gemini genera email personalizado
   - [x] Mailersend envia email
   - [x] Tu email recibe el mensaje
   - [x] Firestore actualizado (emails_sent = 1)

### Verificar Actualizacion Firestore

Despues del test, verificar en Firestore:

```json
{
  "emails_sent": 1,
  "status": "nurturing",
  "nurturing_position": 2,
  "last_contact": "2026-01-07T...",
  "next_email_date": "2026-01-11T..."
}
```

---

## 6. CHECKLIST FINAL

- [ ] Cuenta Mailersend creada
- [ ] Dominio carrilloabgd.com verificado en Mailersend
- [ ] API Key Mailersend generada
- [ ] Credencial httpHeaderAuth creada en n8n con API Key
- [ ] Credencial asignada al nodo "Enviar Email con Mailersend"
- [ ] Credencial Google API verificada (Firestore)
- [ ] Credencial Google PaLM API verificada (Gemini)
- [ ] Variable BACKEND_URL configurada
- [ ] Lead de prueba creado en Firestore
- [ ] Test manual ejecutado exitosamente
- [ ] Email de prueba recibido
- [ ] Firestore actualizado correctamente

---

## 7. ACTIVACION DEL WORKFLOW

**SOLO DESPUES de completar el checklist:**

1. Abrir workflow SUB-D
2. Toggle **Active** a ON (esquina superior derecha)
3. El workflow comenzara a ejecutarse cada 6 horas

### Monitoreo Inicial

Durante las primeras 48 horas:

1. Revisar **Executions** en n8n
2. Verificar emails enviados en Mailersend dashboard
3. Verificar actualizaciones en Firestore
4. Revisar logs de errores

---

## Soporte

Si encuentras problemas:

1. **Mailersend**: support@mailersend.com
2. **Google Cloud**: cloud.google.com/support
3. **n8n**: community.n8n.io

**Contacto interno**: ingenieria@carrilloabgd.com

---

**Documento generado por**: QA Specialist Agent
**Fecha**: 7 de Enero, 2026
