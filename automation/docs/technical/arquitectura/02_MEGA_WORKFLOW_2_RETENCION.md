# 🔄 MEGA-WORKFLOW #2: RETENCIÓN Y REACTIVACIÓN

**Versión:** 2.0  
**Última actualización:** 2025-12-19  
**Estado:** ⚪ PLANIFICADO (Q2 2026)  
**Prioridad:** ALTO - Estrategia Flywheel

---

## 📋 TABLA DE CONTENIDOS

1. [Visión General](#1-visión-general)
2. [Análisis Crítico: Por qué este diseño](#2-análisis-crítico-por-qué-este-diseño)
3. [Arquitectura del MEGA-WORKFLOW](#3-arquitectura-del-mega-workflow)
4. [SUB-G: Client Segmentation](#4-sub-g-client-segmentation)
5. [SUB-H: Newsletter Generator](#5-sub-h-newsletter-generator)
6. [SUB-I: Dormant Reactivation](#6-sub-i-dormant-reactivation)
7. [SUB-J: Upsell Detector](#7-sub-j-upsell-detector)
8. [Flujo de Datos](#8-flujo-de-datos)
9. [Plan de Implementación](#9-plan-de-implementación)
10. [Métricas de Éxito](#10-métricas-de-éxito)

---

## 1. VISIÓN GENERAL

### 1.1 Propósito Estratégico

El **MEGA-WORKFLOW #2: Retención y Reactivación** implementa la estrategia **Flywheel** descrita en el Framework Estratégico:

> *"El cliente está constantemente siendo nutrido y educado, manteniéndolo en un ciclo de compromiso perpetuo. Esto facilita la decisión en ciclos de venta largos."*

**Este NO es un workflow administrativo de recordatorios.** Es un **motor de marketing activo** que:
1. **Segmenta** la base de clientes/leads por comportamiento real
2. **Nutre** con contenido de valor (newsletters, whitepapers, casos de éxito)
3. **Reactiva** contactos dormidos antes de perderlos
4. **Detecta** oportunidades de upsell/cross-sell

### 1.2 Contexto de Negocio: El Problema que Resuelve

| Problema Actual | Impacto | Solución MW#2 |
|-----------------|---------|---------------|
| Clientes "olvidados" después de cerrar caso | Pérdida de recompra y referidos | Newsletter mensual de valor |
| No sabemos quién está "frío" | Churn silencioso | Segmentación por engagement |
| Leads de MW#1 que no convirtieron | Base de datos desperdiciada | Secuencias de reactivación |
| Oportunidades de upsell no detectadas | Revenue perdido | Scoring automatizado |

### 1.3 KPIs Objetivo (Flywheel Metrics)

| Métrica | Fórmula | Target | Por qué importa |
|---------|---------|--------|-----------------|
| **Engagement Rate** | (Opens + Clicks) / Enviados | > 25% | Mide si el contenido conecta |
| **Reactivation Rate** | Dormidos reactivados / Total dormidos | > 15% | Mide recuperación efectiva |
| **Upsell Conversion** | Upsells cerrados / Oportunidades detectadas | > 20% | Mide revenue incremental |
| **Referral Rate** | Referidos recibidos / Clientes activos | > 0.3 | Mide "boca a boca" |
| **Newsletter Unsubscribe** | Bajas / Suscriptores | < 0.5% | Mide calidad de contenido |

---

## 2. ANÁLISIS CRÍTICO: POR QUÉ ESTE DISEÑO

### 2.1 Riesgos y Mitigaciones

#### Riesgo 1: Newsletters que nadie lee

**El problema:** La mayoría de newsletters legales son aburridos (actualizaciones normativas, noticias de la firma). El cliente los ignora.

**Nuestra mitigación:**
- SUB-H genera contenido orientado al **problema del cliente**, no a la firma
- Ejemplos: "5 señales de que tu marca necesita protección" vs "Carrillo Abogados ganó caso X"
- A/B testing de subject lines para optimizar opens
- Contenido en formato "checklist", "errores comunes", "casos de éxito" (alto engagement)

#### Riesgo 2: Segmentación que no segmenta

**El problema:** Segmentar solo por datos demográficos (industria, tamaño) no predice comportamiento.

**Nuestra mitigación:**
- SUB-G segmenta por **comportamiento real**: opens, clicks, tiempo desde última interacción
- Tres segmentos dinámicos: ENGAGED, AT-RISK, DORMANT
- El segmento determina qué contenido recibe y con qué frecuencia

#### Riesgo 3: Reactivación que parece spam

**El problema:** Emails de "te echamos de menos" son obvios y molestos.

**Nuestra mitigación:**
- SUB-I usa contenido de **alto valor** como gancho (whitepaper, webinar, descuento real)
- Secuencia progresiva: valor → valor → pregunta directa
- Si no responde después de 3 intentos, se marca como "churned" (no más emails)

#### Riesgo 4: Upsell que parece venta agresiva

**El problema:** Detectar oportunidad y enviar email de venta inmediato destruye confianza.

**Nuestra mitigación:**
- SUB-J detecta pero **no vende directamente**
- Crea "señal" para que Dr. Carrillo haga contacto personal
- El workflow prepara contexto (qué servicio tiene, qué podría necesitar, por qué ahora)

### 2.2 Decisiones de Arquitectura Críticas

#### ¿Por qué 4 sub-workflows y no 1 monolítico?

| Factor | Monolítico | Hub & Spoke (elegido) |
|--------|------------|----------------------|
| **Frecuencia de ejecución** | Todo junto | SUB-G: diario, SUB-H: semanal, SUB-I: diario |
| **Modificaciones** | Cambiar prompt de newsletter afecta todo | Cambio aislado en SUB-H |
| **Debugging** | "El email de reactivación no llega" → revisar 72 nodos | Revisar solo SUB-I (20 nodos) |
| **Escalabilidad** | Si la base crece, todo se hace lento | SUB-G puede ejecutarse en batch |

#### ¿Por qué SUB-G (Segmentation) es independiente?

**Decisión crítica:** La segmentación corre **antes** de los demás workflows porque su output determina qué hace cada uno.

```
[SUB-G: Segmentation]  ← Corre primero (6 AM)
         │
         ▼
┌────────┴────────┐
│  Actualiza      │
│  campo "segment"│
│  en Firestore   │
└────────┬────────┘
         │
         ├──────────────────────────────────────┐
         │                                      │
         ▼                                      ▼
[SUB-H: Newsletter]                    [SUB-I: Reactivation]
Query: segment = "ENGAGED"             Query: segment = "DORMANT"
Corre: 10 AM (después de SUB-G)        Corre: 2 PM (después de SUB-G)
```

---

## 3. ARQUITECTURA DEL MEGA-WORKFLOW

### 3.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    MEGA-WORKFLOW #2: RETENCIÓN Y REACTIVACIÓN                   │
│                         (Estrategia Flywheel)                                   │
└─────────────────────────────────────────────────────────────────────────────────┘

                              TRIGGERS
                    ┌──────────────────────────────────┐
                    │  • Schedule (diario/semanal)     │
                    │  • Webhook (evento de cliente)   │
                    │  • MW#1 (lead no convertido)     │
                    │  • Mailersend (engagement data)  │
                    └──────────────┬───────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                      WORKFLOW B: CLIENT RELATIONSHIP MANAGER                    │
│                              (ORQUESTADOR / HUB)                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │  Multi-Trigger  →  Identificar  →   Router    →   Consolidar            │   │
│  │                     Evento          por Tipo       Logs                 │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                   │                                             │
│         ┌─────────────────────────┼─────────────────────────┐                  │
│         │                         │                         │                  │
│         ▼                         ▼                         ▼                  │
│  ┌─────────────┐          ┌─────────────┐          ┌─────────────┐            │
│  │ segment_run │          │ newsletter  │          │ reactivate  │            │
│  │  (6 AM)     │          │   (10 AM)   │          │   (2 PM)    │            │
│  └──────┬──────┘          └──────┬──────┘          └──────┬──────┘            │
└─────────┼────────────────────────┼────────────────────────┼─────────────────────┘
          │                        │                        │
          ▼                        ▼                        ▼
┌──────────────────┐      ┌──────────────────┐      ┌──────────────────┐
│     SUB-G        │      │     SUB-H        │      │     SUB-I        │
│    Client        │      │   Newsletter     │      │    Dormant       │
│  Segmentation    │      │   Generator      │      │  Reactivation    │
│                  │      │                  │      │                  │
│ • Query todos    │      │ • Query ENGAGED  │      │ • Query DORMANT  │
│ • Calcular score │      │ • Generar tema   │      │ • Secuencia 3    │
│ • Asignar segment│      │ • Crear contenido│      │   emails         │
│ • Actualizar DB  │      │ • Enviar email   │      │ • Escalar a      │
│                  │      │ • A/B test       │      │   "churned"      │
│ Trigger: 6:00 AM │      │ Trigger: 10:00AM │      │ Trigger: 2:00 PM │
└──────────────────┘      └──────────────────┘      └────────┬─────────┘
                                                             │
                                                             │ Si detecta
                                                             │ señal de interés
                                                             ▼
                                                    ┌──────────────────┐
                                                    │     SUB-J        │
                                                    │    Upsell        │
                                                    │   Detector       │
                                                    │                  │
                                                    │ • Analizar perfil│
                                                    │ • Scoring oport. │
                                                    │ • Notificar Dr.  │
                                                    │   Carrillo       │
                                                    │                  │
                                                    │ Trigger: On-demand│
                                                    └──────────────────┘
```

### 3.2 Secuencia de Ejecución Diaria

```
06:00 AM ─── SUB-G: Segmentation ─────────────────────────────────────┐
             │                                                         │
             │  Recalcula segmentos para TODA la base                 │
             │  Output: Firestore actualizado con "segment"           │
             │                                                         │
10:00 AM ─── SUB-H: Newsletter (solo si es día de envío) ─────────────┤
             │                                                         │
             │  Query: segment = "ENGAGED"                            │
             │  Output: Email con contenido de valor                  │
             │                                                         │
02:00 PM ─── SUB-I: Dormant Reactivation ─────────────────────────────┤
             │                                                         │
             │  Query: segment = "DORMANT" AND reactivation_step < 3  │
             │  Output: Email de la secuencia de recuperación         │
             │                                                         │
On-demand ── SUB-J: Upsell Detector (cuando hay señal) ───────────────┘
             │
             │  Trigger: Cliente abrió email, clickeó link, etc.
             │  Output: Alerta a Dr. Carrillo si hay oportunidad
```

---

## 4. SUB-G: CLIENT SEGMENTATION

### 4.1 Propósito

Clasificar TODA la base de contactos (clientes + leads no convertidos) en segmentos **basados en comportamiento real**, no en datos demográficos.

### 4.2 Los 3 Segmentos

| Segmento | Criterio | Comportamiento Típico | Acción |
|----------|----------|----------------------|--------|
| **ENGAGED** 🟢 | Abrió email en últimos 30 días O clickeó en últimos 60 días | Activo, interesado | Newsletter regular |
| **AT-RISK** 🟡 | Sin apertura 31-90 días Y sin clicks 61-120 días | Perdiendo interés | Newsletter + CTA más fuerte |
| **DORMANT** 🔴 | Sin actividad > 90 días | Probablemente perdido | Secuencia de reactivación |

### 4.3 Flujo Técnico

```
[Schedule Trigger: 6:00 AM diario]
         │
         ▼
[1. Query Firestore: TODOS los contactos]
    Collection: contacts
    Campos: email, last_email_open, last_click, created_at, segment
         │
         ▼
[2. Loop: Calcular nuevo segmento para cada uno]
    │
    │   Para cada contacto:
    │   ├── dias_sin_open = HOY - last_email_open
    │   ├── dias_sin_click = HOY - last_click
    │   │
    │   ├── IF dias_sin_open <= 30 OR dias_sin_click <= 60
    │   │   └── nuevo_segment = "ENGAGED"
    │   │
    │   ├── ELIF dias_sin_open <= 90 AND dias_sin_click <= 120
    │   │   └── nuevo_segment = "AT_RISK"
    │   │
    │   └── ELSE
    │       └── nuevo_segment = "DORMANT"
    │
         ▼
[3. Batch Update Firestore]
    Actualizar campo "segment" para todos los que cambiaron
         │
         ▼
[4. Log de métricas]
    Guardar en Firestore: segmentation_logs
    • total_engaged: X
    • total_at_risk: Y
    • total_dormant: Z
    • changes_today: N
         │
         ▼
[5. Alerta si hay anomalía]
    IF total_dormant > 50% del total
    └── Email alerta a Juan (algo está mal)
```

### 4.4 Crítica del Diseño

**¿Por qué estos umbrales (30/60/90 días)?**

Basado en ciclo de venta típico de servicios legales:
- **30 días:** Si no abrió ningún email en un mes, ya no está prestando atención
- **60 días:** El click es señal más fuerte que el open, damos más margen
- **90 días:** Después de 3 meses sin actividad, es casi seguro que está perdido

**¿Deberíamos ajustar después?**

SÍ. Estos son valores iniciales. Después de 3 meses de datos reales, analizaremos:
- ¿Cuántos DORMANT realmente reactivamos?
- ¿Cuántos ENGAGED se convierten en clientes?
- Ajustar umbrales según resultados

---

## 5. SUB-H: NEWSLETTER GENERATOR

### 5.1 Propósito

Generar y enviar contenido de valor que mantenga al cliente en el **ciclo Flywheel**. El newsletter NO es "noticias de la firma", es **educación que posiciona a Carrillo como experto**.

### 5.2 Tipos de Contenido (Rotación)

| Semana | Tipo de Contenido | Ejemplo | Objetivo |
|--------|------------------|---------|----------|
| 1 | **Educativo** | "5 errores al registrar una marca en Colombia" | Demostrar expertise |
| 2 | **Caso de Éxito** | "Cómo ayudamos a [Cliente] a proteger su software" | Generar confianza |
| 3 | **Actualidad** | "Nueva regulación de PI: qué significa para tu empresa" | Mostrar actualización |
| 4 | **Recurso** | "Descarga: Checklist de protección de marca" | Generar engagement |

### 5.3 Flujo Técnico

```
[Schedule Trigger: Miércoles 10:00 AM]
         │
         ▼
[1. Determinar tipo de contenido esta semana]
    semana_del_mes = (día_actual / 7) + 1
    tipo = ["educativo", "caso", "actualidad", "recurso"][semana_del_mes]
         │
         ▼
[2. Query Firestore: Contactos ENGAGED]
    WHERE segment = "ENGAGED"
    AND email_opt_in = true
         │
         ▼
[3. Generar contenido con IA (Gemini)]
    │
    │   Prompt estructurado:
    │   "Eres un experto en PI colombiana. Escribe un artículo
    │   tipo [tipo_contenido] sobre [tema_relevante].
    │   Audiencia: dueños de PyMEs tech.
    │   Tono: profesional pero accesible.
    │   Longitud: 400-600 palabras.
    │   Incluir: 3 puntos clave, 1 CTA al final."
    │
         ▼
[4. Generar subject line (A/B)]
    │
    │   Generar 2 versiones de subject:
    │   A: Pregunta ("¿Estás cometiendo estos errores?")
    │   B: Afirmación ("Los 5 errores más comunes en registro de marca")
    │
         ▼
[5. Split audiencia 50/50 para A/B test]
    grupo_a = contactos[0:len/2]
    grupo_b = contactos[len/2:]
         │
         ▼
[6. Enviar via Mailersend]
    │
    │   Configuración:
    │   • From: "Dr. Omar Carrillo <omar@carrilloabogados.com>"
    │   • Track opens: YES
    │   • Track clicks: YES
    │   • Unsubscribe link: YES
    │
         ▼
[7. Log envío]
    Firestore: newsletter_logs
    • newsletter_id
    • fecha_envio
    • total_enviados
    • subject_a / subject_b
    • contenido_tipo
         │
         ▼
[8. Schedule: Análisis A/B (24 horas después)]
    Comparar open_rate de A vs B
    Guardar winner para próximo envío
```

### 5.4 Crítica del Diseño

**¿Por qué generar contenido con IA y no escribir manualmente?**

| Factor | Manual | IA + Revisión |
|--------|--------|---------------|
| Tiempo Dr. Carrillo | 4-6 horas/semana | 30 min revisión |
| Consistencia | Variable | Estable |
| Escalabilidad | No escala | Escala |
| Personalización | Difícil | Fácil (segmentos) |

**Riesgo:** Contenido IA puede sonar genérico.

**Mitigación:** 
1. Prompt muy específico con contexto de PI colombiana
2. Dr. Carrillo revisa y ajusta antes de envío
3. Banco de "datos reales" de casos (anonimizados) que enriquecen el prompt

**¿Por qué Mailersend y no Gmail?**

| Factor | Gmail | Mailersend |
|--------|-------|------------|
| Límite envíos | 500/día | 3,000+/mes |
| Tracking opens | Básico | Completo |
| Tracking clicks | No | Sí |
| Unsubscribe management | Manual | Automático |
| Deliverability | Puede ir a spam | Optimizado |

---

## 6. SUB-I: DORMANT REACTIVATION

### 6.1 Propósito

Recuperar contactos que han dejado de interactuar ANTES de que se pierdan definitivamente. La clave es **ofrecer valor, no pedir**.

### 6.2 Secuencia de Reactivación (3 emails)

| Paso | Día | Subject | Contenido | CTA |
|------|-----|---------|-----------|-----|
| 1 | 0 | "Esto te puede interesar: [Recurso gratuito]" | Whitepaper o guía de alto valor | Descargar recurso |
| 2 | 7 | "¿Sabías que [dato impactante sobre PI]?" | Estadística + mini-artículo educativo | Ver más en blog |
| 3 | 14 | "¿Sigues interesado en proteger tu empresa?" | Pregunta directa + oferta consulta | Agendar llamada gratis |

### 6.3 Flujo Técnico

```
[Schedule Trigger: 2:00 PM diario]
         │
         ▼
[1. Query Firestore: Contactos DORMANT pendientes]
    WHERE segment = "DORMANT"
    AND reactivation_step < 3
    AND (last_reactivation_email IS NULL 
         OR last_reactivation_email < HOY - 7 días)
         │
         ▼
[2. Loop por cada contacto]
    │
    │   Para cada contacto:
    │   ├── step = reactivation_step (0, 1, o 2)
    │   ├── Seleccionar template de email según step
    │   │
    │   IF step == 0:
    │   │   template = "whitepaper_value"
    │   │   content = [Generar con Gemini: intro + link a whitepaper]
    │   │
    │   ELIF step == 1:
    │   │   template = "educational_hook"
    │   │   content = [Generar con Gemini: dato impactante + educación]
    │   │
    │   ELIF step == 2:
    │   │   template = "direct_question"
    │   │   content = [Texto fijo: pregunta directa + consulta gratis]
    │   │
         ▼
[3. Enviar email via Mailersend]
    Track opens y clicks
         │
         ▼
[4. Actualizar Firestore]
    • reactivation_step += 1
    • last_reactivation_email = HOY
         │
         ▼
[5. Verificar si se reactivó]
    │
    │   Query webhook de Mailersend (SUB-E del MW#1):
    │   IF email fue abierto OR link clickeado:
    │   │   └── Cambiar segment a "ENGAGED"
    │   │       └── Resetear reactivation_step = 0
    │
         ▼
[6. Marcar como "churned" si completó secuencia sin respuesta]
    IF reactivation_step == 3 AND no_activity:
    └── segment = "CHURNED"
        └── Excluir de futuros envíos
```

### 6.4 Crítica del Diseño

**¿Por qué solo 3 emails y no más?**

Datos de industria: después del 3er email sin respuesta, la probabilidad de reactivación cae a < 2%. Más emails solo generan:
- Riesgo de spam complaints
- Daño a deliverability
- Mala experiencia de marca

**¿Qué hacemos con los "CHURNED"?**

Los marcamos pero **no los borramos**. Razones:
1. Pueden volver por otra vía (referido, búsqueda orgánica)
2. Datos históricos útiles para análisis
3. Posible campaña de "win-back" 1 vez al año (muy diferente a reactivación)

**¿Por qué ofrecer whitepaper gratis en paso 1?**

Es el "gancho de alto valor". Psicología:
- No estamos pidiendo nada (como una reunión)
- Estamos dando algo útil
- Si lo descargan, sabemos que todavía hay interés
- Si no lo descargan en 3 intentos, realmente están perdidos

---

## 7. SUB-J: UPSELL DETECTOR

### 7.1 Propósito

Identificar oportunidades de venta cruzada o adicional en clientes existentes. **No vende directamente** - prepara el contexto para que Dr. Carrillo haga contacto personal.

### 7.2 Señales de Oportunidad

| Señal | Fuente | Score | Acción Sugerida |
|-------|--------|-------|-----------------|
| Clickeó link de servicio diferente | Mailersend | +30 | Cross-sell ese servicio |
| Marca registrada vence en <6 meses | Firestore | +50 | Ofrecer renovación |
| Abrió 3+ emails seguidos | Mailersend | +20 | Posible recompra |
| Empresa creció (news) | Web scraping | +25 | Servicios adicionales |
| Preguntó por otro servicio (email) | Gmail/parse | +40 | Follow-up inmediato |

### 7.3 Flujo Técnico

```
[Trigger: Evento de engagement (webhook Mailersend)]
[Trigger: Schedule diario para vencimientos]
         │
         ▼
[1. Identificar tipo de señal]
    │
    ├── click_servicio_diferente
    ├── vencimiento_proximo
    ├── engagement_alto
    ├── crecimiento_empresa
    └── pregunta_directa
         │
         ▼
[2. Buscar perfil completo del cliente]
    Query Firestore:
    • Servicios actuales
    • Historial de interacciones
    • Valor total contratado
    • Última comunicación
         │
         ▼
[3. Calcular Upsell Score]
    │
    │   score = 0
    │   score += señal_score (ver tabla arriba)
    │   
    │   // Ajustes por perfil
    │   IF valor_contratado > $10M: score += 10
    │   IF última_comunicación < 30 días: score += 15
    │   IF NPS_score >= 9: score += 20
    │
         ▼
[4. ¿Score suficiente para notificar?]
    │
    IF score >= 50:
    │   │
    │   ▼
    │   [5. Preparar brief para Dr. Carrillo]
    │       • Nombre y empresa del cliente
    │       • Servicio actual
    │       • Señal detectada
    │       • Servicio sugerido para ofrecer
    │       • Contexto relevante (últimas interacciones)
    │       • Valor potencial estimado
    │   │
    │   ▼
    │   [6. Enviar notificación]
    │       Email a Dr. Carrillo con brief
    │       Formato: Accionable, no solo informativo
    │   │
    │   ▼
    │   [7. Crear tarea de seguimiento]
    │       Firestore: upsell_opportunities
    │       Status: "pending_contact"
    │
    ELSE:
        └── Log señal para análisis futuro
            (puede acumularse y disparar después)
```

### 7.4 Crítica del Diseño

**¿Por qué no enviar email de venta automático?**

En servicios legales B2B, la relación personal es crítica. Un email automático de "¿Quieres contratar otro servicio?" destruye la confianza construida. 

El workflow **prepara el terreno**, el cierre lo hace el humano.

**¿Por qué threshold de 50 puntos?**

Calibración inicial conservadora:
- Evita "falsos positivos" (molestar a Dr. Carrillo con leads fríos)
- Una señal fuerte (vencimiento próximo = 50) basta
- Señales débiles deben acumularse

Ajustaremos después de ver tasa de conversión real de las oportunidades notificadas.

**¿Qué pasa si Dr. Carrillo no hace follow-up?**

El workflow tiene un "recordatorio escalado":
- Día 3: Recordatorio suave
- Día 7: Recordatorio con "la oportunidad puede enfriarse"
- Día 14: Marca como "expired" (dato para optimización)

---

## 8. FLUJO DE DATOS

### 8.1 Estructura de Contacto en Firestore

```json
{
  "contact_id": "contact_2026-01-15_maria-techstartup",
  "type": "client",  // "client" o "lead"
  "origen_lead_id": "2026-01-15T10:30:00.000Z-maria-techstartup-co",
  
  // Datos básicos
  "nombre": "María Rodríguez",
  "email": "maria@techstartup.co",
  "empresa": "TechStartup SAS",
  
  // Estado de engagement
  "segment": "ENGAGED",  // ENGAGED, AT_RISK, DORMANT, CHURNED
  "email_opt_in": true,
  
  // Métricas de comportamiento
  "last_email_open": "2026-02-10T14:30:00.000Z",
  "last_click": "2026-02-08T09:15:00.000Z",
  "total_emails_received": 12,
  "total_opens": 8,
  "total_clicks": 3,
  
  // Estado de reactivación (solo para DORMANT)
  "reactivation_step": 0,  // 0, 1, 2, o 3
  "last_reactivation_email": null,
  
  // Datos de cliente (si type = "client")
  "servicios_contratados": ["Registro de Marca"],
  "valor_total": 8500000,
  "fecha_ultimo_servicio": "2026-01-20",
  
  // Oportunidades detectadas
  "upsell_opportunities": [
    {
      "opportunity_id": "opp_001",
      "fecha_detectada": "2026-02-15",
      "señal": "vencimiento_proximo",
      "score": 55,
      "servicio_sugerido": "Renovación de marca",
      "status": "pending_contact"
    }
  ],
  
  // Auditoría
  "segment_updated_at": "2026-02-15T06:00:00.000Z",
  "created_at": "2026-01-15T14:30:00.000Z"
}
```

### 8.2 Interacción con MW#1 (Captura)

```
MW#1 (Lead Lifecycle)                    MW#2 (Retention)
         │                                      │
         │                                      │
[Lead no convierte después de nurturing]        │
         │                                      │
         └────────────────────────────────────►│
                                               │
         "Pasar a MW#2 para reactivación"      │
         type = "lead"                         │
         segment = "DORMANT"                   │
                                               │
                                               ▼
                                        [SUB-I: Reactivation]
                                               │
         ┌─────────────────────────────────────┤
         │                                     │
         │  Si se reactiva (abre/clickea)      │
         │                                     │
         ▼                                     │
         segment = "ENGAGED"                   │
         └─────────────────────────────────────┘
```

---

## 9. PLAN DE IMPLEMENTACIÓN

### 9.1 Fases de Desarrollo

| Fase | Período | Componentes | Horas Est. | Prerrequisitos |
|------|---------|-------------|------------|----------------|
| **1** | Sem 1-2 | Orquestador B + SUB-G | 12 | Mailersend configurado |
| **2** | Sem 3-4 | SUB-H (Newsletter) | 15 | SUB-G funcionando |
| **3** | Sem 5-6 | SUB-I (Reactivation) | 12 | SUB-G + templates listos |
| **4** | Sem 7-8 | SUB-J (Upsell) | 10 | SUB-G + datos de clientes |
| **TOTAL** | 8 semanas | - | **49 horas** | - |

### 9.2 Dependencias Externas

| Dependencia | Estado | Bloqueante para |
|-------------|--------|-----------------|
| Cuenta Mailersend | ⚪ Pendiente | Todo el MW#2 |
| Datos de clientes migrados | ⚪ Pendiente | SUB-J |
| Templates de email diseñados | ⚪ Pendiente | SUB-H, SUB-I |
| Whitepaper/recurso de valor | ⚪ Pendiente | SUB-I paso 1 |

### 9.3 Criterios de Éxito por Fase

| Fase | Criterio de Éxito |
|------|-------------------|
| 1 | Segmentación corre diariamente sin errores, métricas visibles |
| 2 | Newsletter enviado a ENGAGED, open rate > 20%, unsubscribe < 1% |
| 3 | Secuencia de 3 emails funciona, reactivation rate > 10% |
| 4 | Al menos 5 oportunidades detectadas/mes, Dr. Carrillo recibe notificaciones |

---

## 10. MÉTRICAS DE ÉXITO

### 10.1 Dashboard Flywheel

| Métrica | Fórmula | Target Mes 1 | Target Mes 6 |
|---------|---------|--------------|--------------|
| **Base activa (ENGAGED)** | Contactos con segment=ENGAGED | > 50% | > 60% |
| **Newsletter Open Rate** | Opens / Enviados | > 20% | > 30% |
| **Newsletter Click Rate** | Clicks / Enviados | > 3% | > 5% |
| **Reactivation Rate** | DORMANT→ENGAGED / Total DORMANT | > 10% | > 20% |
| **Upsell Detection Rate** | Oportunidades / Clientes | > 5% | > 15% |
| **Upsell Conversion** | Upsells cerrados / Detectados | > 15% | > 25% |

### 10.2 Señales de Alerta

| Señal | Umbral | Acción |
|-------|--------|--------|
| Open rate < 15% | Alerta | Revisar subject lines, timing de envío |
| Unsubscribe > 1% | Alerta | Revisar frecuencia y relevancia de contenido |
| DORMANT > 40% base | Crítico | Revisar calidad de leads de MW#1 |
| 0 reactivaciones en 2 semanas | Alerta | Revisar templates de SUB-I |

---

## 📚 DOCUMENTOS RELACIONADOS

| Documento | Ubicación |
|-----------|-----------|
| Arquitectura General | `00_ARQUITECTURA_GENERAL.md` |
| MW#1 Captura | `01_MEGA_WORKFLOW_1_CAPTURA.md` |
| MW#3 SEO | `03_MEGA_WORKFLOW_3_SEO.md` |
| Framework Estratégico | `02-context/business/Framework estrategico ABGD.pdf` |

---

**Última actualización:** 2025-12-19 | **Versión:** 2.0
