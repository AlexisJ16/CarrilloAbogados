# 🎯 DEMO PASO A PASO - Presentación con Abogados
## Carrillo Abogados - 14 de Enero, 2026

---

## ⚠️ ANTES DE EMPEZAR

### 1. Verificar que Docker esté corriendo
```powershell
docker ps | Select-String "carrillo"
```
**Deben aparecer 18 contenedores UP**

### 2. Si alguno está DOWN, reiniciar:
```powershell
docker-compose restart
```

---

## 🔑 CREDENCIALES REALES (EN BASE DE DATOS)

| Rol | Email | Password | Notas |
|-----|-------|----------|-------|
| **Cliente** | alexisj4a@gmail.com | `Cliente123!` | ✅ Verificado |
| **Abogado** | abogado.test@gmail.com | `Cliente123!` | ✅ Verificado |
| **Admin** | admin.test@gmail.com | `Cliente123!` | ✅ Verificado |

---

## 📋 GUIÓN DE DEMO (30 MIN)

### PARTE 1: Páginas Públicas (5 min)

1. **Abrir navegador**: http://localhost:3000
2. **Mostrar Landing Page**:
   - Hero section con CTA
   - Servicios destacados
   - Testimonios

3. **Navegar a Nosotros** (/nosotros):
   - Historia del bufete (fundado 2001)
   - 25 años de experiencia
   - Dr. Omar Carrillo - 15 años en SIC

4. **Navegar a Servicios** (/servicios):
   - 5 áreas de práctica:
     * ✅ Derecho de Marcas (especialidad)
     * ✅ Derecho Corporativo
     * ✅ Derecho Laboral
     * ✅ Derecho de Familia
     * ✅ Litigios Comerciales

5. **Navegar a Equipo** (/equipo):
   - 7 abogados presentados
   - Especialidades de cada uno

---

### PARTE 2: Formulario de Contacto (5 min)

**URL**: http://localhost:3000/contacto

#### Crear Lead de Prueba
```
Nombre:    Demo Abogados 14-Ene 14:45
Email:     alexisj4a@gmail.com
Teléfono:  +57 300 123 4567
Empresa:   Carrillo ABGD SAS
Servicio:  Derecho de Marcas
Mensaje:   Necesito registrar mi marca empresarial urgente antes de la expansión internacional
```

#### ✅ Resultado esperado:
- ✅ Mensaje de éxito en pantalla
- ✅ **Email automático** enviado (revisar Gmail)
- ✅ Lead guardado en base de datos

---

### PARTE 3: Login y Autenticación (5 min)

**URL**: http://localhost:3000/login

#### Opción A: Login como Abogado
```
Email:    abogado.test@gmail.com
Password: test123
```

#### Opción B: Login como Admin
```
Email:    admin.test@gmail.com
Password: test123
```

#### ✅ Resultado esperado:
- Redirección automática a Dashboard
- Menú con acceso a:
  * Dashboard
  * Leads (solo Abogado/Admin)
  * Casos (solo Abogado/Admin)
  * Notificaciones
  * Perfil

---

### PARTE 4: Gestión de Leads (10 min)

**URL**: http://localhost:3000/leads

#### Ver Leads Capturados
1. **Lista de leads** con:
   - Nombre
   - Email
   - Teléfono
   - Servicio de interés
   - Estado (NUEVO, CONTACTADO, CALIFICADO, etc.)
   - Fecha de creación

2. **El lead creado en Parte 2 debe aparecer**

#### Gestionar un Lead
1. Click en el lead creado
2. Ver información completa
3. **Cambiar estado**: NUEVO → CONTACTADO
4. Guardar cambios

#### Filtros Disponibles
- Por estado
- Por servicio
- Búsqueda por nombre/email

---

### PARTE 5: Automatización n8n (5 min)

#### Mostrar Gmail
- Abrir: https://mail.google.com (alexisj4a@gmail.com)
- **Mostrar emails automáticos recibidos**:
  * "Gracias por contactar a Carrillo Abogados"
  * Confirmación de recepción instantánea

#### Explicar Flujo Automático
```
1. Cliente llena formulario
   ↓
2. Backend guarda lead
   ↓
3. n8n Cloud procesa en < 1 minuto
   ↓
4. Email automático al cliente
   ↓
5. Notificación al equipo
   ↓
6. Lead Scoring automático
```

#### Lead Scoring (Explicar)
```
Base: 30 pts
+ Servicio "marca"/"litigio": +20
+ Mensaje > 50 chars: +10
+ Tiene teléfono: +10
+ Tiene empresa: +10
+ Email corporativo: +10
+ Cargo C-Level: +20
───────────────────
HOT:  ≥70 pts → Notificación inmediata
WARM: 40-69 pts → Nurturing automatizado
COLD: <40 pts → Respuesta genérica
```

---

## 🚀 ROADMAP (Próximos Pasos)

### Febrero 2026
- ✅ Calendar as integrado con Google Calendar
- ✅ Sistema de notificaciones push en tiempo real
- ✅ Gestión completa de casos legales
- ✅ Sistema de documentos con templates

### Marzo 2026
- ✅ App móvil (PWA)
- ✅ Dashboard de analíticas
- ✅ Integración con WhatsApp Business
- **🚀 LANZAMIENTO MVP: 27 de Marzo**

---

## 📊 MÉTRICAS OBJETIVO

| Métrica | Actual | Objetivo | Incremento |
|---------|-------:|--------:|------------|
| **Tiempo de respuesta** | 4-24h | < 1 min | **1440x** |
| **Leads/mes** | ~20 | 300+ | **15x** |
| **Conversión** | ~5% | 15%+ | **3x** |
| **Clientes nuevos/año** | ~15 | 100+ | **6.7x** |

---

## 🛠️ TROUBLESHOOTING

### Si un servicio no responde
```powershell
# Ver estado
docker-compose ps

# Reiniciar servicio
docker-compose restart client-service

# Ver logs
docker logs carrillo-client-service --tail 50
```

### Si el login falla
- ✅ Verificar credenciales: `test123`
- ✅ Verificar que client-service esté UP
- ✅ Verificar que PostgreSQL esté UP

### Si el formulario no envía
- ✅ Verificar que el email sea válido
- ✅ Ver logs de client-service
- ✅ Verificar NATS esté UP

---

## 🎨 URLS IMPORTANTES

| Página | URL |
|--------|-----|
| 🏠 Landing | http://localhost:3000 |
| 📞 Contacto | http://localhost:3000/contacto |
| 🔐 Login | http://localhost:3000/login |
| 📊 Dashboard | http://localhost:3000/dashboard |
| 👥 Leads | http://localhost:3000/leads |
| 📋 Casos | http://localhost:3000/cases |
| 📊 Grafana | http://localhost:3100 (admin/carrillo2025) |

---

## ❓ PREGUNTAS FRECUENTES

### P: ¿Cuánto costará el sistema?
**R**: Ya está incluido en el proyecto académico. Solo costo de hosting (~$50/mes en producción).

### P: ¿Cuándo estará en producción?
**R**: MVP listo el **27 de Marzo, 2026**.

### P: ¿Funciona en móviles?
**R**: Sí, 100% responsive. Próximamente app PWA nativa.

### P: ¿Qué pasa con nuestros datos actuales?
**R**: Se pueden migrar desde Excel/Google Sheets antes del lanzamiento.

### P: ¿Necesitamos capacitación?
**R**: Sí, incluimos 2 sesiones de capacitación de 2 horas cada una.

---

## 📝 NOTAS PARA DESPUÉS DE LA DEMO

### Feedback a documentar:
- [ ] Features prioritarias mencionadas
- [ ] Dudas o preocupaciones
- [ ] Sugerencias de mejora
- [ ] Ajustes de diseño solicitados

### Próximos pasos:
1. Implementar feedback prioritario
2. Completar integración n8n MW#1
3. Testing con usuarios reales (1-2 abogados piloto)
4. Deploy a staging en GCP

---

**Preparado por**: Desarrollo Técnico  
**Fecha**: 14 de Enero, 2026  
**Versión**: 1.0
