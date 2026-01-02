# 📖 CASOS DE USO - Carrillo Abogados Legal Tech Platform

**Versión**: 1.0  
**Fecha**: 19 de Diciembre, 2025  
**Estado**: ✅ Documento Base Aprobado

---

## 📋 ÍNDICE

1. [Casos de Uso del Portal Público](#casos-de-uso-del-portal-público)
2. [Casos de Uso del Cliente](#casos-de-uso-del-cliente)
3. [Casos de Uso del Abogado](#casos-de-uso-del-abogado)
4. [Casos de Uso del Administrador](#casos-de-uso-del-administrador)
5. [Diagramas de Flujo](#diagramas-de-flujo)

---

## 🌐 CASOS DE USO DEL PORTAL PÚBLICO

### CU-PUB-001: Solicitar Cita de Consulta

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-PUB-001 |
| **Nombre** | Solicitar Cita de Consulta |
| **Actor** | Visitante |
| **Precondición** | El visitante está en el sitio web |
| **Postcondición** | Cita agendada y confirmación enviada |

**Flujo Principal:**
1. Visitante navega a página de contacto o servicios
2. Visitante hace clic en "Agendar Cita"
3. Sistema muestra calendario con disponibilidad
4. Visitante selecciona fecha y hora disponible
5. Sistema muestra formulario de datos:
   - Nombre completo*
   - Teléfono*
   - Email*
   - Área de práctica de interés
   - Breve descripción del asunto
6. Visitante completa y envía formulario
7. Sistema valida datos
8. Sistema crea evento en calendario del abogado
9. Sistema envía email de confirmación al visitante
10. Sistema notifica al abogado/administrador

**Flujos Alternativos:**
- **FA1**: No hay disponibilidad
  - Sistema muestra mensaje sugiriendo contacto telefónico
- **FA2**: Email inválido
  - Sistema solicita corrección

**Microservicios Involucrados:**
- `calendar-service` - Disponibilidad y creación de evento
- `notification-service` - Envío de confirmación
- `client-service` - Registro de lead (opcional)

---

### CU-PUB-002: Enviar Consulta por Formulario

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-PUB-002 |
| **Nombre** | Enviar Consulta por Formulario de Contacto |
| **Actor** | Visitante |
| **Precondición** | El visitante está en el sitio web |
| **Postcondición** | Consulta recibida y notificación enviada |

**Flujo Principal:**
1. Visitante navega a página de contacto
2. Visitante completa formulario:
   - Nombre completo*
   - Email*
   - Teléfono
   - Tipo de consulta (dropdown por área)
   - Mensaje*
   - Captcha
3. Visitante envía formulario
4. Sistema valida datos y captcha
5. Sistema almacena consulta en base de datos
6. Sistema envía email de confirmación al visitante
7. Sistema notifica al abogado/administrador por email
8. Sistema muestra mensaje de éxito

**Microservicios Involucrados:**
- `notification-service` - Envío de emails
- `n8n-integration-service` - Workflow de seguimiento

---

### CU-PUB-003: Registrarse como Cliente Potencial

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-PUB-003 |
| **Nombre** | Registro de Cliente Potencial |
| **Actor** | Visitante |
| **Precondición** | Visitante desea crear cuenta |
| **Postcondición** | Cuenta creada pendiente de activación |

**Flujo Principal:**
1. Visitante hace clic en "Registrarse" o "Crear Cuenta"
2. Sistema muestra formulario de registro:
   - Nombre completo*
   - Tipo de persona (Natural/Jurídica)
   - Documento de identidad (Cédula/NIT)*
   - Email*
   - Teléfono*
   - Contraseña*
   - Confirmar contraseña*
   - Aceptar términos y condiciones*
   - Aceptar política de privacidad*
3. Visitante completa y envía formulario
4. Sistema valida datos únicos (email, documento)
5. Sistema crea usuario con estado "Pendiente Verificación"
6. Sistema envía email con link de verificación
7. Visitante hace clic en link de verificación
8. Sistema activa cuenta con rol "Cliente"
9. Sistema notifica al administrador de nuevo registro

**Flujos Alternativos:**
- **FA1**: Email ya registrado
  - Sistema sugiere recuperar contraseña
- **FA2**: Link expirado
  - Sistema permite reenviar link

**Microservicios Involucrados:**
- `client-service` - Registro de cliente
- `notification-service` - Email de verificación

---

## 🧑‍💼 CASOS DE USO DEL CLIENTE

### CU-CLI-001: Ver Estado de Mis Casos

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-CLI-001 |
| **Nombre** | Consultar Estado de Casos |
| **Actor** | Cliente |
| **Precondición** | Cliente autenticado, tiene casos registrados |
| **Postcondición** | Cliente visualiza información de sus casos |

**Flujo Principal:**
1. Cliente inicia sesión en portal
2. Sistema muestra dashboard con resumen de casos
3. Cliente hace clic en "Mis Casos"
4. Sistema muestra listado de casos:
   - Número de caso
   - Título/Descripción breve
   - Estado (En trámite, Suspendido, etc.)
   - Abogado asignado
   - Última actualización
5. Cliente selecciona un caso
6. Sistema muestra detalle del caso:
   - Información general
   - Línea de tiempo de actividades
   - Documentos compartidos
   - Próximos eventos
   - Abogado asignado con foto

**Microservicios Involucrados:**
- `case-service` - Obtener casos del cliente
- `document-service` - Listar documentos
- `calendar-service` - Próximos eventos

---

### CU-CLI-002: Subir Documento

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-CLI-002 |
| **Nombre** | Subir Documento a Caso |
| **Actor** | Cliente |
| **Precondición** | Cliente autenticado, tiene acceso al caso |
| **Postcondición** | Documento almacenado y abogado notificado |

**Flujo Principal:**
1. Cliente navega al detalle del caso
2. Cliente hace clic en "Subir Documento"
3. Sistema muestra formulario de upload:
   - Seleccionar archivo(s)
   - Tipo de documento (dropdown)
   - Descripción (opcional)
4. Cliente selecciona archivo (máx 50MB)
5. Sistema valida tipo de archivo permitido
6. Sistema sube archivo a storage seguro
7. Sistema registra documento vinculado al caso
8. Sistema notifica al abogado del nuevo documento
9. Sistema muestra confirmación al cliente

**Formatos Permitidos:**
- PDF, DOC, DOCX, XLS, XLSX
- JPG, PNG (para fotos de documentos)

**Microservicios Involucrados:**
- `document-service` - Upload y almacenamiento
- `notification-service` - Notificación al abogado

---

### CU-CLI-003: Enviar Mensaje al Abogado

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-CLI-003 |
| **Nombre** | Comunicación con Abogado |
| **Actor** | Cliente |
| **Precondición** | Cliente autenticado, tiene abogado asignado |
| **Postcondición** | Mensaje enviado y abogado notificado |

**Flujo Principal:**
1. Cliente navega a sección "Mensajes" o desde detalle de caso
2. Cliente selecciona caso relacionado (si aplica)
3. Cliente escribe mensaje en editor de texto
4. Cliente puede adjuntar archivo (opcional)
5. Cliente envía mensaje
6. Sistema almacena mensaje en historial
7. Sistema notifica al abogado por email
8. Sistema muestra confirmación al cliente

**Microservicios Involucrados:**
- `client-service` o `case-service` - Almacenar mensaje
- `notification-service` - Notificación al abogado

---

### CU-CLI-004: Solicitar Cita con Abogado

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-CLI-004 |
| **Nombre** | Agendar Cita desde Portal Cliente |
| **Actor** | Cliente |
| **Precondición** | Cliente autenticado |
| **Postcondición** | Solicitud de cita enviada |

**Flujo Principal:**
1. Cliente navega a "Agendar Cita"
2. Sistema muestra calendario con disponibilidad del abogado asignado
3. Cliente selecciona fecha y hora
4. Cliente selecciona modalidad (Presencial/Virtual)
5. Cliente agrega descripción del motivo
6. Cliente confirma solicitud
7. Sistema crea solicitud de cita (pendiente confirmación)
8. Sistema notifica al abogado
9. Abogado confirma o sugiere alternativa
10. Sistema notifica al cliente la confirmación

**Microservicios Involucrados:**
- `calendar-service` - Gestión de citas
- `notification-service` - Notificaciones

---

## ⚖️ CASOS DE USO DEL ABOGADO

### CU-ABG-001: Crear Nuevo Caso

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-001 |
| **Nombre** | Registrar Nuevo Caso Legal |
| **Actor** | Abogado |
| **Precondición** | Abogado autenticado, cliente existe en sistema |
| **Postcondición** | Caso creado y vinculado a cliente |

**Flujo Principal:**
1. Abogado navega a "Casos" → "Nuevo Caso"
2. Sistema muestra formulario de creación:
   - **Datos del Cliente**
     - Buscar cliente existente o crear nuevo
   - **Datos del Caso**
     - Título del caso*
     - Descripción*
     - Área de práctica* (dropdown)
     - Tipo de proceso (Judicial/Administrativo/Consultoría)
     - Número de radicado (si existe)
     - Autoridad/Juzgado
     - Materia específica
   - **Contraparte** (opcional)
     - Nombre
     - Representante legal
     - Datos de contacto
   - **Asignación**
     - Abogado principal (por defecto el creador)
     - Abogados colaboradores (opcional)
3. Abogado completa formulario
4. Sistema valida datos requeridos
5. Sistema crea caso con estado "ABIERTO"
6. Sistema crea actividad inicial "Caso creado"
7. Sistema notifica a abogados colaboradores (si hay)
8. Sistema muestra confirmación y opción de agregar documentos

**Microservicios Involucrados:**
- `case-service` - Creación del caso
- `client-service` - Búsqueda/creación de cliente
- `notification-service` - Notificación a colaboradores

---

### CU-ABG-002: Agregar Actividad a Caso

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-002 |
| **Nombre** | Registrar Actividad/Actuación |
| **Actor** | Abogado |
| **Precondición** | Abogado tiene acceso al caso |
| **Postcondición** | Actividad registrada en línea de tiempo |

**Flujo Principal:**
1. Abogado navega al detalle del caso
2. Abogado hace clic en "Agregar Actividad"
3. Sistema muestra formulario:
   - Tipo de actividad* (dropdown):
     - Audiencia
     - Presentación de documento
     - Notificación recibida
     - Reunión con cliente
     - Investigación
     - Otro
   - Fecha de la actividad*
   - Descripción*
   - Visible para cliente (checkbox)
   - Archivos adjuntos (opcional)
4. Abogado completa y guarda
5. Sistema registra actividad en línea de tiempo
6. Si "Visible para cliente" está activo:
   - Sistema notifica al cliente

**Microservicios Involucrados:**
- `case-service` - Registro de actividad
- `document-service` - Adjuntos
- `notification-service` - Notificación a cliente

---

### CU-ABG-003: Registrar Término Legal

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-003 |
| **Nombre** | Agregar Término Procesal |
| **Actor** | Abogado |
| **Precondición** | Caso existe |
| **Postcondición** | Término registrado con alertas configuradas |

**Flujo Principal:**
1. Abogado navega al detalle del caso
2. Abogado hace clic en "Agregar Término"
3. Sistema muestra formulario:
   - Tipo de término*:
     - Vencimiento para contestar
     - Término para recurrir
     - Fecha de audiencia
     - Entrega de documento
     - Otro
   - Fecha límite*
   - Descripción*
   - Alertas (días antes para recordatorio)
4. Abogado completa y guarda
5. Sistema registra término
6. Sistema crea evento en calendario del abogado
7. Sistema programa notificaciones de recordatorio

**Microservicios Involucrados:**
- `case-service` - Registro de término
- `calendar-service` - Evento en calendario
- `notification-service` - Programar recordatorios

---

### CU-ABG-004: Registrar Pago Gubernamental

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-004 |
| **Nombre** | Registrar Pago de Trámite |
| **Actor** | Abogado |
| **Precondición** | Caso existe, pago requerido |
| **Postcondición** | Pago registrado y comprobante adjunto |

**Flujo Principal:**
1. Abogado navega al caso → sección "Pagos"
2. Abogado hace clic en "Nuevo Pago"
3. Sistema muestra formulario:
   - Concepto del pago*:
     - Tasa judicial
     - Arancel notarial
     - Registro SIC (marcas)
     - Impuesto de registro
     - Otro
   - Entidad receptora*
   - Monto*
   - Fecha de pago*
   - Número de referencia
   - Comprobante (upload PDF/imagen)*
4. Abogado sube comprobante
5. Sistema valida archivo
6. Sistema registra pago con estado "VERIFICANDO"
7. Sistema notifica al administrador para verificación

**Microservicios Involucrados:**
- `payment-service` - Registro de pago
- `document-service` - Almacenar comprobante
- `notification-service` - Notificación

---

### CU-ABG-005: Ver Calendario y Audiencias

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-005 |
| **Nombre** | Consultar Calendario |
| **Actor** | Abogado |
| **Precondición** | Abogado autenticado |
| **Postcondición** | Vista de calendario con eventos |

**Flujo Principal:**
1. Abogado navega a "Calendario"
2. Sistema muestra vista mensual/semanal/diaria
3. Sistema sincroniza con Google Calendar del abogado
4. Abogado ve eventos:
   - Audiencias (de casos)
   - Términos próximos a vencer
   - Reuniones con clientes
   - Citas agendadas
5. Abogado puede filtrar por:
   - Tipo de evento
   - Caso específico
   - Rango de fechas
6. Abogado puede crear nuevo evento
7. Cambios se sincronizan con Google Calendar

**Microservicios Involucrados:**
- `calendar-service` - Gestión de calendario
- `case-service` - Obtener casos para contexto

---

### CU-ABG-006: Cambiar Estado del Caso

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ABG-006 |
| **Nombre** | Actualizar Estado del Caso |
| **Actor** | Abogado |
| **Precondición** | Abogado es responsable del caso |
| **Postcondición** | Estado actualizado y partes notificadas |

**Flujo Principal:**
1. Abogado navega al detalle del caso
2. Abogado hace clic en "Cambiar Estado"
3. Sistema muestra opciones según estado actual:

| Estado Actual | Estados Permitidos |
|---------------|-------------------|
| ABIERTO | EN_PROGRESO, SUSPENDIDO, CERRADO |
| EN_PROGRESO | SUSPENDIDO, EN_ESPERA, CERRADO |
| SUSPENDIDO | EN_PROGRESO, CERRADO |
| EN_ESPERA | EN_PROGRESO, SUSPENDIDO |
| CERRADO | REABIERTO (solo admin) |

4. Abogado selecciona nuevo estado
5. Sistema solicita motivo del cambio
6. Sistema actualiza estado
7. Sistema registra actividad automática
8. Sistema notifica al cliente (si está configurado)

**Microservicios Involucrados:**
- `case-service` - Actualización de estado
- `notification-service` - Notificación a cliente

---

## 👑 CASOS DE USO DEL ADMINISTRADOR

### CU-ADM-001: Crear Usuario Abogado

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ADM-001 |
| **Nombre** | Agregar Nuevo Abogado |
| **Actor** | Administrador |
| **Precondición** | Administrador autenticado |
| **Postcondición** | Usuario abogado creado |

**Flujo Principal:**
1. Administrador navega a "Usuarios" → "Nuevo Usuario"
2. Sistema muestra formulario:
   - Tipo de usuario: Abogado
   - Nombre completo*
   - Email @carrilloabgd.com*
   - Área de práctica principal*
   - Teléfono
   - Foto de perfil
   - Biografía (para mostrar en web)
3. Administrador completa y guarda
4. Sistema valida email en dominio corporativo
5. Sistema crea usuario con rol ABOGADO
6. Sistema envía invitación por email
7. Abogado hace login con Google Workspace

**Microservicios Involucrados:**
- `client-service` - Gestión de usuarios
- `notification-service` - Email de invitación

---

### CU-ADM-002: Publicar Artículo en Blog

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ADM-002 |
| **Nombre** | Crear/Publicar Contenido Blog |
| **Actor** | Administrador (Marketing) |
| **Precondición** | Administrador autenticado |
| **Postcondición** | Artículo publicado en sitio web |

**Flujo Principal:**
1. Administrador navega a "Blog" → "Nuevo Artículo"
2. Sistema muestra editor:
   - Título*
   - Slug (auto-generado desde título)
   - Contenido (editor rico)
   - Imagen destacada
   - Categoría*
   - Tags
   - Autor*
   - Extracto/Resumen
   - Estado (Borrador/Publicado)
   - Fecha de publicación
3. Administrador completa contenido
4. Administrador puede guardar como borrador
5. Administrador publica artículo
6. Sistema genera URL y sitemap
7. Sistema puede compartir en redes sociales (opcional)

---

### CU-ADM-003: Ver Reportes de Actividad

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ADM-003 |
| **Nombre** | Consultar Reportes y Métricas |
| **Actor** | Administrador |
| **Precondición** | Administrador autenticado |
| **Postcondición** | Dashboard con métricas visualizado |

**Flujo Principal:**
1. Administrador navega a "Reportes"
2. Sistema muestra dashboard con:
   - **Métricas de Casos**
     - Total casos activos
     - Casos por estado
     - Casos por área de práctica
     - Casos por abogado
   - **Métricas de Clientes**
     - Total clientes activos
     - Nuevos clientes (último mes)
     - Consultas recibidas (leads)
   - **Métricas de Actividad**
     - Logins por día
     - Documentos subidos
     - Citas agendadas
3. Administrador puede filtrar por rango de fechas
4. Administrador puede exportar reportes a Excel/PDF

**Microservicios Involucrados:**
- `case-service` - Métricas de casos
- `client-service` - Métricas de clientes
- `calendar-service` - Métricas de citas

---

### CU-ADM-004: Configurar Automatizaciones N8N

| Campo | Descripción |
|-------|-------------|
| **ID** | CU-ADM-004 |
| **Nombre** | Gestionar Workflows de Automatización |
| **Actor** | Administrador (Técnico) |
| **Precondición** | Administrador autenticado, N8N Pro configurado |
| **Postcondición** | Workflow activo |

**Flujo Principal:**
1. Administrador accede a panel N8N
2. Administrador crea nuevo workflow
3. Configura trigger (evento que dispara):
   - Nuevo caso creado
   - Cliente registrado
   - Término próximo a vencer
   - Pago registrado
4. Configura acciones:
   - Enviar email
   - Crear tarea
   - Actualizar registro
   - Integrar con servicio externo
5. Administrador activa workflow
6. Sistema registra workflow activo

**Microservicios Involucrados:**
- `n8n-integration-service` - Bridge con N8N Pro

---

## 📊 DIAGRAMAS DE FLUJO

### Flujo: Nuevo Cliente desde Portal

```
┌──────────────────────────────────────────────────────────────────┐
│                   FLUJO: NUEVO CLIENTE                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  VISITANTE                      SISTEMA                          │
│                                                                  │
│  ┌─────────────┐                                                │
│  │ Visita web  │                                                │
│  └──────┬──────┘                                                │
│         │                                                        │
│         ▼                                                        │
│  ┌─────────────┐     ┌─────────────┐                            │
│  │ Llena       │────▶│ Valida      │                            │
│  │ formulario  │     │ datos       │                            │
│  └─────────────┘     └──────┬──────┘                            │
│                             │                                    │
│                    ┌────────┴────────┐                          │
│                    ▼                 ▼                           │
│             ┌──────────┐      ┌──────────┐                      │
│             │ Válido   │      │ Inválido │                      │
│             └────┬─────┘      └────┬─────┘                      │
│                  │                  │                            │
│                  ▼                  ▼                            │
│           ┌────────────┐    ┌────────────┐                      │
│           │ Crear lead │    │ Mostrar    │                      │
│           │ en BD      │    │ errores    │                      │
│           └─────┬──────┘    └────────────┘                      │
│                 │                                                │
│                 ▼                                                │
│          ┌─────────────┐                                        │
│          │ Enviar email│                                        │
│          │ confirmación│                                        │
│          └─────┬───────┘                                        │
│                │                                                 │
│                ▼                                                 │
│         ┌──────────────┐                                        │
│         │ Notificar    │                                        │
│         │ al equipo    │                                        │
│         └──────────────┘                                        │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Flujo: Gestión de Caso Completo

```
┌──────────────────────────────────────────────────────────────────┐
│                   CICLO DE VIDA DEL CASO                         │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────┐                                                    │
│  │ NUEVO   │  Consulta inicial con cliente                      │
│  └────┬────┘                                                    │
│       │                                                          │
│       ▼                                                          │
│  ┌─────────┐                                                    │
│  │ ABIERTO │  Caso creado, documentos iniciales                 │
│  └────┬────┘                                                    │
│       │                                                          │
│       ▼                                                          │
│  ┌───────────────┐                                              │
│  │ EN_PROGRESO   │  Trabajo activo: actuaciones, términos       │
│  └───────┬───────┘                                              │
│          │                                                       │
│    ┌─────┴─────┐                                                │
│    │           │                                                │
│    ▼           ▼                                                │
│ ┌──────────┐ ┌──────────┐                                       │
│ │SUSPENDIDO│ │EN_ESPERA │  Esperando respuesta/resolución       │
│ └────┬─────┘ └────┬─────┘                                       │
│      │           │                                               │
│      └─────┬─────┘                                              │
│            │                                                     │
│            ▼                                                     │
│       ┌─────────┐                                               │
│       │ CERRADO │  Caso finalizado                              │
│       └────┬────┘                                               │
│            │                                                     │
│            ▼                                                     │
│       ┌──────────┐                                              │
│       │ARCHIVADO │  Retención documental                        │
│       └──────────┘                                              │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Flujo: Notificación de Término

```
┌──────────────────────────────────────────────────────────────────┐
│               FLUJO: ALERTA DE TÉRMINO LEGAL                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│     case-service              notification-service               │
│                                                                  │
│  ┌─────────────────┐                                            │
│  │ Término creado  │                                            │
│  │ con fecha límite│                                            │
│  └────────┬────────┘                                            │
│           │                                                      │
│           ▼                                                      │
│  ┌─────────────────┐     ┌─────────────────┐                    │
│  │ Evento NATS:    │────▶│ Escucha evento  │                    │
│  │ termino.creado  │     │ de término      │                    │
│  └─────────────────┘     └────────┬────────┘                    │
│                                   │                              │
│                                   ▼                              │
│                          ┌─────────────────┐                    │
│                          │ Programa alertas│                    │
│                          │ • 7 días antes  │                    │
│                          │ • 3 días antes  │                    │
│                          │ • 1 día antes   │                    │
│                          │ • Día del venc. │                    │
│                          └────────┬────────┘                    │
│                                   │                              │
│                                   ▼                              │
│                          ┌─────────────────┐                    │
│                          │ Ejecuta alerta: │                    │
│                          │ • Email abogado │                    │
│                          │ • Push notif    │                    │
│                          │ • Email cliente │                    │
│                          └─────────────────┘                    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📝 HISTORIAS DE USUARIO PRIORIZADAS

### Sprint 1 (MVP Core)

| ID | Historia de Usuario | Estimación |
|----|---------------------|------------|
| US-001 | Como visitante, quiero ver la información del bufete para conocer sus servicios | 3 pts |
| US-002 | Como visitante, quiero llenar un formulario de contacto para solicitar información | 5 pts |
| US-003 | Como visitante, quiero agendar una cita en línea para una consulta inicial | 8 pts |
| US-004 | Como abogado, quiero crear un nuevo caso para un cliente | 8 pts |
| US-005 | Como abogado, quiero agregar actividades a un caso para registrar el trabajo realizado | 5 pts |
| US-006 | Como abogado, quiero ver mi calendario sincronizado con Google para gestionar mi agenda | 8 pts |

### Sprint 2 (Portal Cliente)

| ID | Historia de Usuario | Estimación |
|----|---------------------|------------|
| US-007 | Como cliente, quiero registrarme en el portal para acceder a mis casos | 5 pts |
| US-008 | Como cliente, quiero ver el estado de mis casos para estar informado | 5 pts |
| US-009 | Como cliente, quiero subir documentos a mis casos para enviarlos al abogado | 5 pts |
| US-010 | Como cliente, quiero enviar mensajes a mi abogado para comunicarme | 5 pts |
| US-011 | Como abogado, quiero recibir notificaciones cuando un cliente sube documentos | 3 pts |

### Sprint 3 (Gestión Avanzada)

| ID | Historia de Usuario | Estimación |
|----|---------------------|------------|
| US-012 | Como abogado, quiero registrar términos legales con alertas automáticas | 8 pts |
| US-013 | Como abogado, quiero registrar pagos gubernamentales asociados a casos | 5 pts |
| US-014 | Como administrador, quiero ver reportes de casos y clientes | 8 pts |
| US-015 | Como administrador, quiero publicar artículos en el blog | 5 pts |

---

## 🔗 DOCUMENTOS RELACIONADOS

- [MODELO_NEGOCIO.md](./MODELO_NEGOCIO.md) - Contexto de negocio
- [REQUERIMIENTOS.md](./REQUERIMIENTOS.md) - Requerimientos funcionales
- [ROLES_USUARIOS.md](./ROLES_USUARIOS.md) - Definición de roles
- [ARQUITECTURA_FUNCIONAL.md](./ARQUITECTURA_FUNCIONAL.md) - Mapeo técnico

---

*Documento generado para la plataforma Carrillo Abogados Legal Tech*
