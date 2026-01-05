# 🎯 PLAN DE EJECUCIÓN MW#1 - Lead Lifecycle Manager

**Fecha**: 5 de Enero, 2026
**Objetivo**: Completar integración Web → n8n → Plataforma
**Tiempo Total Estimado**: 13 horas (6h Marketing Dev + 7h Backend Dev)
**Meta**: MW#1 100% funcional en 1 semana

---

## 📊 ESTADO ACTUAL (Verificado)

### ✅ Lo que Funciona

| Componente | Estado | Evidencia |
|------------|--------|-----------|
| Frontend `/contacto` | ✅ | Formulario funcional, envía a client-service |
| client-service API | ✅ | Guarda leads en PostgreSQL |
| n8n Webhook activo | ✅ | `/lead-events` recibe POST |
| n8n Orquestador | ✅ | 8 nodos, 60% tasa éxito |
| n8n SUB-A con IA | ✅ | 13 nodos, Gemini 2.5 Pro, 40% tasa éxito |
| Firestore | ✅ | Guardando datos |
| Gmail API | ✅ | Enviando emails |

### ❌ Lo que Falta (BLOQUEADORES)

| Problema | Impacto | Responsable |
|----------|---------|-------------|
| Formulario NO envía a n8n | Lead nunca llega a n8n | Marketing Dev |
| client-service NO emite NATS | Integración rota | Backend Dev |
| n8n-integration-service sin listener | Bridge no funciona | Backend Dev |
| n8n NO llama callbacks | BD no se actualiza con score | Marketing Dev + Backend Dev |
| 50% tasa error en n8n | Leads se pierden | Marketing Dev |

---

## 🎯 ESTRATEGIA: Trabajo en Paralelo

### TÚ (Marketing Dev) - 6 horas

**Prioridad 1**: Estabilizar n8n (2h)
- Investigar errores actuales (50% tasa fallo)
- Validar credenciales (Gemini, Firestore, Gmail)
- Probar con payloads reales

**Prioridad 2**: Agregar callbacks en SUB-A (2h)
- Nodo HTTP Request → `/webhook/lead-scored`
- Nodo IF + HTTP Request → `/webhook/lead-hot` (solo HOT)
- Testing manual

**Prioridad 3**: Documentación (2h)
- Crear ejemplos de curl para testing
- Documentar errores encontrados
- Preparar handoff para backend dev

### DESARROLLADOR BACKEND - 7 horas

**Ver**: `BACKEND_DEV_TASKS.md` para código completo

**Tarea 1**: client-service evento NATS (2h)
- Publicar `lead.capturado` cuando se crea lead
- Endpoint PATCH `/api/leads/{id}/score`

**Tarea 2**: n8n-integration-service listener (2h)
- @NatsListener para `lead.capturado`
- Llamar webhook n8n

**Tarea 3**: n8n-integration-service webhooks (3h)
- POST `/webhook/lead-scored` → actualizar BD
- POST `/webhook/lead-hot` → crear notificación

---

## 📅 TIMELINE SUGERIDA

### Día 1-2: Trabajo Independiente

**Marketing Dev**:
- ✅ Debuggear y estabilizar n8n (Prioridad 1)
- ✅ Agregar callbacks SUB-A (Prioridad 2)

**Backend Dev**:
- ✅ Implementar Tarea 1 (client-service NATS)
- ✅ Implementar Tarea 2 (n8n-integration listener)

### Día 3: Punto de Sincronización

**Ambos**:
- Testing conjunto: Lead → NATS → n8n
- Validar que n8n recibe leads correctamente
- Ajustar payloads si hay diferencias

### Día 4-5: Completar Integración

**Backend Dev**:
- ✅ Implementar Tarea 3 (webhooks callbacks)

**Marketing Dev**:
- ✅ Validar callbacks n8n → plataforma
- ✅ Testing E2E completo

### Día 5: Testing Final

**Ambos**:
- Prueba E2E: Formulario → PostgreSQL (con score) < 1 min
- Validar notificaciones HOT leads
- Verificar emails automáticos
- Revisar logs de errores

---

## 📁 DOCUMENTOS CLAVE

| Documento | Propósito | Para Quién |
|-----------|-----------|------------|
| `WEB_INTEGRATION.md` | Especificación completa de payloads | Ambos |
| `BACKEND_DEV_TASKS.md` | Código Java completo para implementar | Backend Dev |
| `PLAN_EJECUCION_MW1.md` | Este archivo - Plan de trabajo | Ambos |

---

## 🧪 TESTING E2E

### Escenario Completo

```
1. Usuario llena formulario /contacto
   - Nombre: "Test E2E"
   - Email: "test@empresa.com"
   - Servicio: "derecho-marcas"
   - Mensaje: "Necesito registrar una marca urgente"

2. Frontend POST → client-service
   ✅ Lead guardado en PostgreSQL con estado=NEW

3. client-service publica NATS → "lead.capturado"
   ✅ Evento recibido por n8n-integration-service

4. n8n-integration-service → POST a n8n webhook
   ✅ n8n Orquestador recibe lead

5. n8n SUB-A procesa lead
   ✅ Gemini analiza: Score=85, Categoria=HOT
   ✅ Guarda en Firestore
   ✅ Email a marketing@carrilloabgd.com (HOT)
   ✅ Email IA personalizado al lead

6. n8n → POST /webhook/lead-scored
   ✅ n8n-integration-service recibe callback

7. n8n-integration-service → PATCH client-service
   ✅ Lead actualizado: score=85, categoria=HOT, estado=QUALIFIED

8. Validar en PostgreSQL
   ✅ Lead tiene score y categoria
   ✅ Timestamp < 1 minuto desde captura

RESULTADO: ✅ MW#1 100% FUNCIONAL
```

---

## 🎯 CRITERIOS DE ÉXITO

Para considerar MW#1 **COMPLETO**:

| Métrica | Objetivo | Cómo Medir |
|---------|----------|------------|
| Lead capturado | 100% | Formulario → PostgreSQL |
| Lead scored con IA | 100% | PostgreSQL tiene score |
| Notificación HOT | 100% | Email recibido en marketing@ |
| Respuesta automática | 100% | Lead recibe email IA |
| Tiempo total | < 1 min | Timestamp formulario → BD con score |
| Tasa error n8n | < 5% | Dashboard n8n Executions |

---

## 🚨 RIESGOS Y MITIGACIONES

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| n8n Cloud caído | Baja | Alto | Retry logic + logs |
| NATS no conecta | Media | Alto | Health checks + alertas |
| Gemini API límite | Media | Medio | Rate limiting + queue |
| Payloads incompatibles | Alta | Alto | Testing temprano + ajustes |
| Timeout en IA | Media | Medio | Timeout 30s + retry |

---

## 📞 PRÓXIMOS PASOS

### AHORA (Marketing Dev)

1. Revisar `WEB_INTEGRATION.md` - Especificación de payloads
2. Debuggear errores actuales en n8n (ver ejecuciones fallidas)
3. Agregar nodos callback en SUB-A según spec
4. Testing manual con curl

### COORDINACIÓN CON BACKEND DEV

1. Entregar `BACKEND_DEV_TASKS.md` con código completo
2. Acordar fecha para testing conjunto (Día 3)
3. Definir URLs dev vs prod
4. Preparar datos de prueba

### DESPUÉS DEL TESTING E2E

1. Actualizar `PROYECTO_ESTADO.md` con estado real
2. Actualizar memoria de usuario con logros
3. Documentar lecciones aprendidas
4. Planificar MW#2 (Retención)

---

## 🎉 BENEFICIOS DE MW#1 COMPLETO

Cuando termines esta integración:

✅ **Automatización Real**: Leads procesados con IA en < 1 min
✅ **Notificaciones Inteligentes**: Dr. Carrillo recibe solo HOT leads
✅ **Respuesta Instantánea**: Leads reciben email personalizado automático
✅ **Data Unificada**: PostgreSQL (plataforma) + Firestore (n8n) sincronizados
✅ **Escalabilidad**: Base sólida para MW#2 (Retención) y MW#3 (SEO)
✅ **ROI Medible**: Métricas claras (leads/mes, tiempo respuesta, conversión)

---

**¿Listo para empezar?** 🚀

Comienza por estabilizar n8n (investigar errores) y avísame cuando quieras hacer el testing conjunto.
