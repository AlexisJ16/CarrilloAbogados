# Configuracion de Mailersend para SUB-D

## Estado Actual: PENDIENTE CONFIGURACION

El workflow SUB-D: Nurturing Sequence Engine requiere Mailersend para enviar los emails de nurturing. Esta guia detalla los pasos para configurarlo.

---

## Paso 1: Crear Cuenta en Mailersend

1. Ir a https://mailersend.com
2. Registrarse con email corporativo (preferiblemente marketing@carrilloabgd.com)
3. Verificar email de confirmacion
4. Completar perfil de empresa

### Free Tier Incluye:
- 3,000 emails/mes
- 1 dominio verificado
- Tracking de aperturas y clicks
- Webhooks para eventos
- API REST completa

---

## Paso 2: Verificar Dominio

### Opcion A: Verificacion DNS (Recomendada)

1. En Mailersend Dashboard, ir a **Email > Domains**
2. Click **Add domain**
3. Ingresar: `carrilloabgd.com`
4. Agregar los siguientes registros DNS:

```
Tipo: TXT
Host: @
Valor: (proporcionado por Mailersend)

Tipo: TXT
Host: mta._domainkey
Valor: (DKIM key proporcionada)

Tipo: MX
Host: @
Valor: (MX record proporcionado)
```

5. Esperar propagacion DNS (hasta 48 horas)
6. Click **Verify domain** en Mailersend

### Opcion B: Verificacion por Email

1. Mailersend enviara email a admin@carrilloabgd.com
2. Click en link de verificacion
3. Dominio queda verificado

---

## Paso 3: Obtener API Key

1. En Mailersend Dashboard, ir a **Settings > API tokens**
2. Click **Create new token**
3. Configurar:
   - **Name**: `n8n-nurturing-subd`
   - **Permissions**:
     - [x] Email: Full access
     - [x] Analytics: Read
     - [ ] Templates: No necesario
     - [ ] Webhooks: Read (opcional para SUB-E)
4. Click **Create token**
5. **COPIAR Y GUARDAR** el token (solo se muestra una vez)

Formato del token:
```
mlsn.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

## Paso 4: Configurar Credencial en n8n Cloud

1. Ir a https://carrilloabgd.app.n8n.cloud
2. Menu lateral: **Credentials**
3. Click **Add credential**
4. Seleccionar: **Header Auth**
5. Configurar:

```
Name: Mailersend API
Header Name: Authorization
Header Value: Bearer mlsn.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

6. Click **Save**
7. Anotar el ID de credencial generado

---

## Paso 5: Actualizar Workflow SUB-D

Despues de crear la credencial, actualizar el nodo "Enviar Email con Mailersend":

1. Abrir workflow SUB-D en n8n
2. Editar nodo **Enviar Email con Mailersend**
3. En **Authentication**:
   - Seleccionar: Generic Credential Type
   - Tipo: Header Auth
   - Credencial: Mailersend API
4. Guardar workflow

---

## Configuracion del Nodo HTTP Request

### Headers Requeridos

```json
{
  "Content-Type": "application/json",
  "X-Requested-With": "XMLHttpRequest"
}
```

### Body Structure

```json
{
  "from": {
    "email": "marketing@carrilloabgd.com",
    "name": "Dr. Omar Carrillo - Carrillo Abogados"
  },
  "to": [
    {
      "email": "destinatario@ejemplo.com",
      "name": "Nombre del Lead"
    }
  ],
  "subject": "Subject del email",
  "html": "<p>Contenido HTML del email</p>",
  "tags": [
    "nurturing",
    "position-X",
    "mw1-sub-d"
  ],
  "settings": {
    "track_clicks": true,
    "track_opens": true,
    "track_content": false
  }
}
```

---

## Configuracion de Webhooks (Para SUB-E Futuro)

Para trackear aperturas y clicks, configurar webhooks en Mailersend:

1. Dashboard > **Webhooks**
2. **Create webhook**
3. Configurar:
   - URL: `https://carrilloabgd.app.n8n.cloud/webhook/mailersend-events`
   - Events:
     - [x] email.opened
     - [x] email.clicked
     - [x] email.bounced
     - [x] email.unsubscribed
4. Click **Create**

---

## Troubleshooting

### Error 401 Unauthorized
- Verificar que el API Key es correcto
- Verificar que el header es `Authorization: Bearer {API_KEY}`
- Regenerar API Key si es necesario

### Error 403 Forbidden
- Verificar que el dominio esta verificado
- Verificar que el email "from" usa el dominio verificado

### Error 422 Unprocessable Entity
- Verificar formato del email destinatario
- Verificar que el JSON body es valido
- Revisar campos requeridos (from, to, subject)

### Emails no llegan
- Verificar carpeta de spam del destinatario
- Verificar logs en Mailersend Dashboard > Activity
- Verificar que el dominio tiene DKIM/SPF configurados

---

## Costos Estimados

| Plan | Emails/mes | Costo/mes | Recomendado |
|------|------------|-----------|-------------|
| Free | 3,000 | $0 | Para inicio |
| Starter | 50,000 | $25 USD | Cuando escale |
| Professional | 100,000 | $65 USD | Produccion |

### Estimacion de uso SUB-D:
- 300 leads/mes objetivo
- 12 emails por lead (secuencia completa)
- Maximo teorico: 3,600 emails/mes
- Free tier cubre primeros meses

---

## Checklist de Configuracion

- [ ] Cuenta Mailersend creada
- [ ] Email verificado
- [ ] Dominio carrilloabgd.com agregado
- [ ] DNS records configurados (TXT, DKIM, MX)
- [ ] Dominio verificado en Mailersend
- [ ] API Key generada
- [ ] Credencial creada en n8n Cloud
- [ ] Workflow SUB-D actualizado con credencial
- [ ] Test de envio exitoso
- [ ] Webhooks configurados (opcional, para SUB-E)

---

## Contacto Soporte

- **Mailersend Support**: support@mailersend.com
- **Documentacion API**: https://developers.mailersend.com/
- **Status Page**: https://status.mailersend.com/

---

**Autor**: @engineer
**Fecha**: 7 de Enero, 2026
**Version**: 1.0
