# 👥 ROLES DE USUARIOS - Carrillo Abogados Legal Tech Platform

**Versión**: 1.0  
**Fecha**: 19 de Diciembre, 2025  
**Estado**: ✅ Documento Base Aprobado

---

## 📋 RESUMEN

La plataforma Carrillo ABGD contempla **4 tipos de usuarios** con diferentes niveles de acceso y funcionalidades: Administrador, Abogado, Cliente y Visitante. Este documento define los roles, permisos y casos de uso para cada tipo de usuario.

---

## 🎭 TIPOS DE USUARIO

```
┌─────────────────────────────────────────────────────────────────┐
│                  ARQUITECTURA DE ROLES                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │                   ADMINISTRADORES                        │  │
│   │          (Alexis + Compañero Marketing)                  │  │
│   │                                                         │  │
│   │  Acceso total a toda la plataforma y configuraciones    │  │
│   └─────────────────────────┬───────────────────────────────┘  │
│                             │                                   │
│   ┌─────────────────────────┴───────────────────────────────┐  │
│   │                      ABOGADOS                            │  │
│   │              (7 abogados del bufete)                     │  │
│   │                                                         │  │
│   │  Gestión de casos, clientes, documentos, calendario     │  │
│   └─────────────────────────┬───────────────────────────────┘  │
│                             │                                   │
│   ┌─────────────────────────┴───────────────────────────────┐  │
│   │                      CLIENTES                            │  │
│   │           (Clientes del bufete con acceso)               │  │
│   │                                                         │  │
│   │  Ver sus casos, subir documentos, comunicarse           │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
│   ┌─────────────────────────────────────────────────────────┐  │
│   │                    VISITANTES                            │  │
│   │                 (Sin autenticación)                      │  │
│   │                                                         │  │
│   │  Portal público, solicitar cita, formulario contacto    │  │
│   └─────────────────────────────────────────────────────────┘  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 👑 ROL: ADMINISTRADOR

### Descripción
Los administradores tienen **acceso total** a todas las funcionalidades del sistema, incluyendo configuraciones, gestión de usuarios y supervisión de toda la operación.

### Usuarios con este Rol

| Usuario | Correo | Responsabilidad |
|---------|--------|-----------------|
| Alexis | ingenieria@carrilloabgd.com | Desarrollo, infraestructura, mantenimiento técnico |
| Compañero Marketing | marketing@carrilloabgd.com | Contenido, SEO, campañas, blog |

### Permisos

| Módulo | Permiso | Descripción |
|--------|---------|-------------|
| **Usuarios** | CRUD completo | Crear, editar, eliminar, desactivar usuarios |
| **Clientes** | CRUD completo | Acceso a todos los clientes del bufete |
| **Casos** | CRUD completo | Acceso a todos los casos de todos los abogados |
| **Documentos** | CRUD completo | Acceso a todos los documentos |
| **Calendario** | CRUD completo | Ver y gestionar calendarios de todos los abogados |
| **Pagos** | CRUD completo | Ver todos los pagos y configurar |
| **Notificaciones** | Configurar | Configurar templates y reglas de notificación |
| **Blog/CMS** | CRUD completo | Crear, editar, publicar artículos |
| **Configuración** | Acceso total | Configuración del sistema, integraciones |
| **Reportes** | Acceso total | Todos los reportes y analytics |
| **Auditoría** | Lectura | Ver logs de actividad del sistema |
| **N8N** | Configurar | Crear y modificar workflows de automatización |

### Funcionalidades Exclusivas

1. **Gestión de Usuarios**
   - Crear nuevos usuarios (abogados, clientes)
   - Asignar roles y permisos
   - Resetear contraseñas
   - Desactivar usuarios

2. **Configuración del Sistema**
   - Configurar integraciones (Google Workspace, N8N)
   - Gestionar templates de email
   - Configurar automatizaciones

3. **Contenido Público**
   - Gestionar blog y artículos
   - Actualizar páginas del sitio
   - Moderar comentarios

4. **Supervisión**
   - Ver métricas de uso
   - Acceder a logs de auditoría
   - Monitorear salud del sistema

### Restricciones

- ❌ No pueden borrar datos de forma permanente sin respaldo
- ❌ No pueden acceder a configuraciones de infraestructura (solo Alexis)

---

## ⚖️ ROL: ABOGADO

### Descripción
Los abogados son los usuarios principales del sistema interno. Gestionan sus propios casos y clientes, con visibilidad limitada a la información que les corresponde.

### Usuarios con este Rol

| # | Área de Práctica | Correo |
|---|------------------|--------|
| 1 | Derecho Administrativo | abogado1@carrilloabgd.com |
| 2 | Derecho de Competencias | abogado2@carrilloabgd.com |
| 3 | Derecho Corporativo | abogado3@carrilloabgd.com |
| 4 | Derecho Telecomunicaciones | abogado4@carrilloabgd.com |
| 5 | Derecho de Marcas | abogado5@carrilloabgd.com |
| 6 | General/Apoyo | abogado6@carrilloabgd.com |
| 7 | General/Apoyo | abogado7@carrilloabgd.com |

> **Nota**: Los correos específicos serán configurados según la estructura real del bufete.

### Permisos

| Módulo | Permiso | Alcance |
|--------|---------|---------|
| **Clientes** | CRUD | Solo clientes asignados |
| **Casos** | CRUD | Solo casos asignados |
| **Documentos** | CRUD | Solo documentos de sus casos |
| **Calendario** | CRUD | Su propio calendario |
| **Tareas** | CRUD | Tareas asignadas a él/ella |
| **Pagos** | Ver/Crear | Pagos de sus casos |
| **Comunicación** | Enviar | Mensajes a sus clientes |
| **Reportes** | Ver | Reportes de sus casos |

### Funcionalidades

1. **Gestión de Casos**
   - Crear nuevos casos para clientes asignados
   - Actualizar estado del caso
   - Registrar actividades y actuaciones
   - Agregar términos y fechas importantes
   - Ver línea de tiempo del caso

2. **Gestión de Clientes**
   - Ver información de clientes asignados
   - Actualizar datos de contacto
   - Ver historial de casos del cliente
   - Comunicarse con el cliente

3. **Documentos**
   - Subir documentos a casos
   - Clasificar y organizar documentos
   - Compartir documentos con cliente
   - Descargar documentos

4. **Calendario**
   - Ver su calendario sincronizado con Google
   - Crear audiencias y eventos
   - Configurar recordatorios
   - Ver disponibilidad

5. **Tareas**
   - Ver tareas asignadas
   - Marcar tareas como completadas
   - Crear tareas para sí mismo

6. **Pagos**
   - Registrar pagos gubernamentales de casos
   - Subir comprobantes
   - Ver estado de pagos

### Restricciones

- ❌ No puede ver casos de otros abogados (a menos que sea colaborador)
- ❌ No puede crear nuevos usuarios
- ❌ No puede acceder a configuración del sistema
- ❌ No puede modificar el sitio web público
- ❌ No puede eliminar clientes (solo desactivar)

### Colaboración entre Abogados

Un abogado puede ser agregado como **colaborador** en un caso de otro abogado:

| Rol en Caso | Permisos |
|-------------|----------|
| **Abogado Principal** | CRUD completo del caso |
| **Abogado Colaborador** | Ver + Agregar actividades |

---

## 🧑‍💼 ROL: CLIENTE

### Descripción
Los clientes del bufete pueden acceder al **portal de clientes** para consultar el estado de sus casos, subir documentos y comunicarse con su abogado asignado.

### Requisitos de Acceso

1. Ser cliente activo del bufete
2. Tener un caso registrado en el sistema
3. Recibir invitación del abogado o administrador
4. Registrarse con email verificado

### Permisos

| Módulo | Permiso | Alcance |
|--------|---------|---------|
| **Mi Perfil** | Ver/Editar | Sus propios datos |
| **Mis Casos** | Ver | Solo sus casos |
| **Documentos** | Ver/Subir | Documentos de sus casos |
| **Calendario** | Ver | Eventos relacionados con sus casos |
| **Mensajes** | Enviar | Solo a abogado asignado |
| **Pagos** | Ver | Estado de pagos de sus casos |
| **Citas** | Solicitar | Agendar citas con el bufete |

### Funcionalidades

1. **Dashboard Personal**
   - Resumen de casos activos
   - Próximos eventos/audiencias
   - Documentos recientes
   - Mensajes no leídos

2. **Mis Casos**
   - Ver listado de casos propios
   - Ver detalle de cada caso
   - Ver estado actual (En trámite, Suspendido, etc.)
   - Ver línea de tiempo de actividades
   - Ver abogado asignado

3. **Documentos**
   - Ver documentos compartidos por el abogado
   - Subir documentos requeridos
   - Descargar documentos

4. **Comunicación**
   - Enviar mensajes al abogado
   - Ver historial de comunicación
   - Recibir notificaciones por email

5. **Agenda**
   - Ver próximas citas y audiencias
   - Solicitar nueva cita
   - Confirmar/cancelar citas

### Restricciones

- ❌ No puede ver casos de otros clientes
- ❌ No puede ver información interna del bufete
- ❌ No puede editar actividades del caso
- ❌ No puede eliminar documentos
- ❌ No puede ver otros abogados del bufete (solo el asignado)
- ❌ No puede acceder a información de pagos internos

### Tipos de Cliente

| Tipo | Descripción | Campos Adicionales |
|------|-------------|-------------------|
| **Persona Natural** | Individuo | Cédula, fecha nacimiento |
| **Persona Jurídica** | Empresa | NIT, representante legal, razón social |

---

## 🌐 ROL: VISITANTE (Sin Autenticación)

### Descripción
Usuarios anónimos que visitan el sitio web público. No requieren autenticación.

### Funcionalidades Disponibles

1. **Navegación del Sitio**
   - Ver página de inicio
   - Ver quiénes somos
   - Ver servicios y áreas de práctica
   - Ver equipo de abogados
   - Ver casos de éxito
   - Leer blog

2. **Interacción**
   - Llenar formulario de contacto
   - Solicitar cita (sin registro)
   - Chat en vivo (si está disponible)
   - Suscribirse a newsletter

3. **Conversión**
   - Registrarse como cliente potencial
   - Recibir seguimiento del bufete

---

## 🔐 MATRIZ DE PERMISOS DETALLADA

### Permisos por Entidad

```
┌──────────────────┬─────────────┬─────────────┬─────────────┬─────────────┐
│ Entidad/Acción   │   Admin     │   Abogado   │   Cliente   │  Visitante  │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ USUARIOS                                                                  │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Listar todos     │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver perfil       │     ✅      │   Solo él   │   Solo él   │     ❌      │
│ Crear            │     ✅      │     ❌      │     ❌      │     ❌      │
│ Editar           │     ✅      │   Solo él   │   Solo él   │     ❌      │
│ Eliminar         │     ✅      │     ❌      │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ CLIENTES                                                                  │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Listar todos     │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver detalle      │     ✅      │  Asignados  │   Solo él   │     ❌      │
│ Crear            │     ✅      │     ✅      │     ❌      │   Registro  │
│ Editar           │     ✅      │  Asignados  │   Solo él   │     ❌      │
│ Eliminar         │     ✅      │     ❌      │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ CASOS                                                                     │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Listar todos     │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver detalle      │     ✅      │  Asignados  │   Propios   │     ❌      │
│ Crear            │     ✅      │     ✅      │     ❌      │     ❌      │
│ Editar           │     ✅      │  Asignados  │     ❌      │     ❌      │
│ Cambiar estado   │     ✅      │  Asignados  │     ❌      │     ❌      │
│ Agregar actividad│     ✅      │  Asignados  │     ❌      │     ❌      │
│ Eliminar         │     ✅      │     ❌      │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ DOCUMENTOS                                                                │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Listar todos     │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver/Descargar    │     ✅      │  Sus casos  │  Compartidos │     ❌      │
│ Subir            │     ✅      │     ✅      │  Sus casos  │     ❌      │
│ Eliminar         │     ✅      │  Sus casos  │     ❌      │     ❌      │
│ Compartir        │     ✅      │  Sus casos  │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ CALENDARIO                                                                │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Ver todos        │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver propio       │     ✅      │     ✅      │  Sus casos  │     ❌      │
│ Crear evento     │     ✅      │     ✅      │     ❌      │     ❌      │
│ Solicitar cita   │     ✅      │     ✅      │     ✅      │     ✅      │
│ Eliminar evento  │     ✅      │  Propios    │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ PAGOS                                                                     │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Ver todos        │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver de caso      │     ✅      │  Sus casos  │  Sus casos  │     ❌      │
│ Crear            │     ✅      │     ✅      │     ❌      │     ❌      │
│ Subir comprobante│     ✅      │  Sus casos  │     ❌      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ NOTIFICACIONES                                                            │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Configurar       │     ✅      │     ❌      │     ❌      │     ❌      │
│ Ver propias      │     ✅      │     ✅      │     ✅      │     ❌      │
│ Preferencias     │     ✅      │     ✅      │     ✅      │     ❌      │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ CONFIGURACIÓN                                                             │
├──────────────────┼─────────────┼─────────────┼─────────────┼─────────────┤
│ Sistema          │     ✅      │     ❌      │     ❌      │     ❌      │
│ Integraciones    │     ✅      │     ❌      │     ❌      │     ❌      │
│ Templates        │     ✅      │     ❌      │     ❌      │     ❌      │
│ Blog/CMS         │     ✅      │     ❌      │     ❌      │     ❌      │
│ Auditoría        │     ✅      │     ❌      │     ❌      │     ❌      │
└──────────────────┴─────────────┴─────────────┴─────────────┴─────────────┘
```

---

## 🔑 AUTENTICACIÓN

### Mecanismo de Autenticación

| Rol | Método de Autenticación |
|-----|------------------------|
| **Administrador** | OAuth2 con Google Workspace (@carrilloabgd.com) |
| **Abogado** | OAuth2 con Google Workspace (@carrilloabgd.com) |
| **Cliente** | Email + Contraseña (con verificación email) |
| **Visitante** | Sin autenticación |

### Flujo de Autenticación

```
┌──────────────────────────────────────────────────────────────────┐
│                    FLUJO DE LOGIN                                 │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ADMINISTRADOR/ABOGADO:                                          │
│  ┌─────┐    ┌─────────────┐    ┌─────────┐    ┌───────────┐    │
│  │Login│ -> │Google OAuth2│ -> │Validar  │ -> │Dashboard  │    │
│  │Page │    │@carrilloabgd│    │Dominio  │    │Interno    │    │
│  └─────┘    └─────────────┘    └─────────┘    └───────────┘    │
│                                                                  │
│  CLIENTE:                                                        │
│  ┌─────┐    ┌─────────────┐    ┌─────────┐    ┌───────────┐    │
│  │Login│ -> │Email +      │ -> │Verificar│ -> │Portal     │    │
│  │Page │    │Password     │    │Token    │    │Cliente    │    │
│  └─────┘    └─────────────┘    └─────────┘    └───────────┘    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

### Seguridad

| Control | Implementación |
|---------|---------------|
| **2FA** | Habilitado para todos (Google o TOTP) |
| **Sesión** | JWT con expiración 8 horas |
| **Refresh Token** | 7 días |
| **Bloqueo** | 5 intentos fallidos = bloqueo 30 min |
| **Auditoría** | Log de todos los accesos |

---

## 📱 EXPERIENCIA POR DISPOSITIVO

| Rol | Desktop | Tablet | Mobile |
|-----|---------|--------|--------|
| **Administrador** | ✅ Completo | ✅ Parcial | ⚠️ Limitado |
| **Abogado** | ✅ Completo | ✅ Completo | ✅ Optimizado |
| **Cliente** | ✅ Completo | ✅ Completo | ✅ Optimizado |
| **Visitante** | ✅ Completo | ✅ Completo | ✅ Optimizado |

---

## 🚀 ONBOARDING POR ROL

### Administrador
1. Acceso provisto por configuración inicial del sistema
2. Tour guiado de funcionalidades administrativas
3. Documentación técnica de referencia

### Abogado
1. Invitación por email del administrador
2. Login con cuenta Google Workspace existente
3. Tour guiado del sistema
4. Configuración de perfil y notificaciones
5. Sincronización de Google Calendar

### Cliente
1. Invitación por email del abogado asignado
2. Registro con email y contraseña
3. Verificación de email
4. Tour guiado del portal de cliente
5. Configuración de preferencias de notificación

---

## 🔗 DOCUMENTOS RELACIONADOS

- [MODELO_NEGOCIO.md](./MODELO_NEGOCIO.md) - Contexto de negocio
- [REQUERIMIENTOS.md](./REQUERIMIENTOS.md) - Requerimientos del sistema
- [CASOS_USO.md](./CASOS_USO.md) - Flujos detallados por rol
- [ARQUITECTURA_FUNCIONAL.md](./ARQUITECTURA_FUNCIONAL.md) - Mapeo técnico

---

*Documento generado para la plataforma Carrillo Abogados Legal Tech*
