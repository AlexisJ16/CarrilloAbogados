# 🔧 TAREAS BACKEND DEV - Integración n8n MW#1

**Documento Técnico**: Tareas específicas para desarrollador backend
**Fecha**: 5 de Enero, 2026
**Tiempo Estimado Total**: 7 horas
**Objetivo**: Completar integración NATS ↔ n8n para MW#1

---

## 🎯 RESUMEN EJECUTIVO

Implementar 3 componentes clave en la plataforma Spring Boot:

1. **client-service**: Publicar evento NATS cuando se crea un lead (2h)
2. **n8n-integration-service**: NATS Listener + llamada webhook n8n (2h)
3. **n8n-integration-service**: Webhooks para callbacks de n8n (3h)

**Criterio de Éxito**: Lead capturado en formulario web aparece en PostgreSQL con score IA en < 1 minuto

---

## 📋 TAREA 1: Evento NATS en client-service (2 horas)

### Archivos a Modificar

#### 1.1. Crear LeadCapturedEvent.java

**Ubicación**: `client-service/src/main/java/com/carrillo/clients/event/LeadCapturedEvent.java`

```java
package com.carrillo.clients.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCapturedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String leadId;           // UUID del lead en PostgreSQL
    private String nombre;
    private String email;
    private String telefono;
    private String empresa;
    private String servicio;
    private String mensaje;
    private String source;           // "web_contacto", "web_landing", "api"
    private Instant timestamp;
}
```

---

#### 1.2. Modificar LeadService.java

**Ubicación**: `client-service/src/main/java/com/carrillo/clients/service/LeadService.java`

**ANTES**:

```java
@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    public Lead createLead(LeadDTO dto) {
        Lead lead = new Lead();
        lead.setNombre(dto.getNombre());
        lead.setEmail(dto.getEmail());
        lead.setTelefono(dto.getTelefono());
        lead.setEmpresa(dto.getEmpresa());
        lead.setServicio(dto.getServicio());
        lead.setMensaje(dto.getMensaje());
        lead.setEstado(LeadEstado.NEW);
        lead.setFechaCreacion(Instant.now());

        return leadRepository.save(lead);
    }
}
```

**DESPUÉS**:

```java
package com.carrillo.clients.service;

import com.carrillo.clients.domain.Lead;
import com.carrillo.clients.domain.LeadEstado;
import com.carrillo.clients.dto.LeadDTO;
import com.carrillo.clients.event.LeadCapturedEvent;
import com.carrillo.clients.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.nats.core.NatsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final NatsTemplate natsTemplate;  // ← AGREGAR

    @Transactional
    public Lead createLead(LeadDTO dto) {
        // Guardar lead en PostgreSQL
        Lead lead = new Lead();
        lead.setNombre(dto.getNombre());
        lead.setEmail(dto.getEmail());
        lead.setTelefono(dto.getTelefono());
        lead.setEmpresa(dto.getEmpresa());
        lead.setServicio(dto.getServicio());
        lead.setMensaje(dto.getMensaje());
        lead.setEstado(LeadEstado.NEW);
        lead.setFechaCreacion(Instant.now());

        Lead savedLead = leadRepository.save(lead);

        log.info("Lead created successfully: {}", savedLead.getId());

        // ✅ NUEVO: Publicar evento NATS
        try {
            LeadCapturedEvent event = new LeadCapturedEvent(
                savedLead.getId().toString(),
                savedLead.getNombre(),
                savedLead.getEmail(),
                savedLead.getTelefono() != null ? savedLead.getTelefono() : "",
                savedLead.getEmpresa() != null ? savedLead.getEmpresa() : "",
                savedLead.getServicio(),
                savedLead.getMensaje(),
                "web_contacto",
                Instant.now()
            );

            natsTemplate.convertAndSend("lead.capturado", event);
            log.info("Published lead.capturado event for leadId: {}", savedLead.getId());

        } catch (Exception e) {
            log.error("Error publishing NATS event for leadId: {}", savedLead.getId(), e);
            // No fallar la operación completa si falla NATS
        }

        return savedLead;
    }

    // ✅ NUEVO: Método para actualizar score desde n8n
    @Transactional
    public Lead updateLeadScore(UUID leadId, Integer score, String categoria) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        lead.setScore(score);
        lead.setCategoria(categoria);
        lead.setEstado(LeadEstado.QUALIFIED);

        Lead updatedLead = leadRepository.save(lead);

        log.info("Updated lead score: leadId={}, score={}, categoria={}",
            leadId, score, categoria);

        return updatedLead;
    }
}
```

---

#### 1.3. Agregar Endpoint PATCH en LeadResource.java

**Ubicación**: `client-service/src/main/java/com/carrillo/clients/resource/LeadResource.java`

**Agregar este método**:

```java
@PatchMapping("/{id}/score")
public ResponseEntity<LeadDTO> updateLeadScore(
    @PathVariable UUID id,
    @RequestBody @Valid UpdateLeadScoreDTO dto
) {
    log.info("Updating lead score: leadId={}, score={}, categoria={}",
        id, dto.getScore(), dto.getCategoria());

    Lead updatedLead = leadService.updateLeadScore(id, dto.getScore(), dto.getCategoria());

    return ResponseEntity.ok(leadMapper.toDTO(updatedLead));
}
```

**Crear DTO**:

```java
package com.carrillo.clients.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLeadScoreDTO {

    @NotNull(message = "Score es requerido")
    @Min(value = 0, message = "Score mínimo es 0")
    @Max(value = 100, message = "Score máximo es 100")
    private Integer score;

    @NotBlank(message = "Categoría es requerida")
    private String categoria;  // HOT, WARM, COLD
}
```

---

#### 1.4. Configuración NATS en application.yml

**Ubicación**: `client-service/src/main/resources/application.yml`

```yaml
spring:
  nats:
    server: nats://localhost:4222
    connection-timeout: 3s

# Para Docker Compose:
---
spring:
  config:
    activate:
      on-profile: docker
  nats:
    server: nats://nats:4222
```

---

#### 1.5. Agregar Dependencia NATS

**Ubicación**: `client-service/pom.xml`

```xml
<dependency>
    <groupId>io.nats</groupId>
    <artifactId>spring-nats</artifactId>
    <version>0.5.5</version>
</dependency>
```

---

### Testing Tarea 1

```bash
# 1. Levantar stack
docker-compose up -d nats postgresql client-service

# 2. Crear lead via API
curl -X POST http://localhost:8200/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test NATS",
    "email": "test@empresa.com",
    "telefono": "+57 300 123 4567",
    "empresa": "Test Corp",
    "servicio": "derecho-marcas",
    "mensaje": "Prueba evento NATS"
  }'

# 3. Verificar logs
docker logs client-service | grep "Published lead.capturado"

# 4. Suscribirse a NATS (con nats-box)
docker run --network carrillo-network --rm -it synadia/nats-box \
  nats sub --server nats://nats:4222 "lead.capturado"
```

---

## 📋 TAREA 2: NATS Listener en n8n-integration-service (2 horas)

### Archivos a Crear

#### 2.1. LeadEventListener.java

**Ubicación**: `n8n-integration-service/src/main/java/com/carrillo/n8n/listener/LeadEventListener.java`

```java
package com.carrillo.n8n.listener;

import com.carrillo.n8n.event.LeadCapturedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.nats.annotation.NatsListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeadEventListener {

    private final RestTemplate restTemplate;

    @Value("${n8n.webhook.lead-events}")
    private String n8nWebhookUrl;

    @NatsListener(subject = "lead.capturado")
    public void handleLeadCapturado(LeadCapturedEvent event) {
        log.info("Received lead.capturado event for leadId: {}", event.getLeadId());

        try {
            sendToN8n(event);
        } catch (Exception e) {
            log.error("Error processing lead.capturado event", e);
            // El retry lo maneja @Retryable
        }
    }

    @Retryable(
        value = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void sendToN8n(LeadCapturedEvent event) {
        // Transformar a formato n8n
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_type", "new_lead");
        payload.put("lead_id", event.getLeadId());
        payload.put("nombre", event.getNombre());
        payload.put("email", event.getEmail());
        payload.put("telefono", event.getTelefono());
        payload.put("empresa", event.getEmpresa());
        payload.put("servicio_interes", event.getServicio());  // ← Mapeo de nombre
        payload.put("mensaje", event.getMensaje());
        payload.put("source", event.getSource());
        payload.put("orchestrator_timestamp", event.getTimestamp().toString());

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Llamar webhook n8n
        ResponseEntity<String> response = restTemplate.postForEntity(
            n8nWebhookUrl,
            request,
            String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully sent lead to n8n. LeadId: {}, Response: {}",
                event.getLeadId(), response.getBody());
        } else {
            log.error("n8n webhook failed. Status: {}, Body: {}",
                response.getStatusCode(), response.getBody());
            throw new RestClientException("n8n webhook returned " + response.getStatusCode());
        }
    }
}
```

---

#### 2.2. Configuración

**Ubicación**: `n8n-integration-service/src/main/resources/application.yml`

```yaml
spring:
  nats:
    server: nats://localhost:4222

n8n:
  webhook:
    lead-events: https://carrilloabgd.app.n8n.cloud/webhook/lead-events

# Retry configuration
spring:
  retry:
    enabled: true
```

---

#### 2.3. RestTemplate Bean

**Ubicación**: `n8n-integration-service/src/main/java/com/carrillo/n8n/config/RestTemplateConfig.java`

```java
package com.carrillo.n8n.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
    }
}
```

---

### Testing Tarea 2

```bash
# 1. Levantar stack completo
docker-compose up -d

# 2. Crear lead (dispara NATS → n8n-integration-service → n8n)
curl -X POST http://localhost:8200/client-service/api/leads \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Test E2E",
    "email": "test@empresa.com",
    "servicio": "derecho-marcas",
    "mensaje": "Prueba integración completa NATS → n8n"
  }'

# 3. Verificar logs n8n-integration-service
docker logs n8n-integration-service | grep "Successfully sent lead to n8n"

# 4. Verificar ejecución en n8n Cloud
# Ir a: https://carrilloabgd.app.n8n.cloud
# Dashboard → Executions → Filtrar por "Orquestador"
```

---

## 📋 TAREA 3: Webhooks para Callbacks n8n (3 horas)

### Archivos a Crear

#### 3.1. N8nWebhookController.java

**Ubicación**: `n8n-integration-service/src/main/java/com/carrillo/n8n/controller/N8nWebhookController.java`

```java
package com.carrillo.n8n.controller;

import com.carrillo.n8n.dto.HotLeadWebhookDTO;
import com.carrillo.n8n.dto.LeadScoredWebhookDTO;
import com.carrillo.n8n.dto.UpdateLeadScoreDTO;
import com.carrillo.n8n.service.ClientServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
public class N8nWebhookController {

    private final ClientServiceClient clientServiceClient;

    /**
     * Callback de n8n cuando lead es scored
     */
    @PostMapping("/lead-scored")
    public ResponseEntity<?> handleLeadScored(@RequestBody LeadScoredWebhookDTO dto) {
        log.info("Received lead-scored webhook for leadId: {}", dto.getLeadId());

        try {
            // Actualizar lead en client-service
            UpdateLeadScoreDTO updateDto = new UpdateLeadScoreDTO(
                dto.getScore(),
                dto.getCategoria()
            );

            clientServiceClient.updateLeadScore(dto.getLeadId(), updateDto);

            log.info("Successfully updated lead score in client-service");

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Lead score updated successfully"
            ));

        } catch (Exception e) {
            log.error("Error updating lead score", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Callback de n8n cuando lead es HOT (score >= 70)
     */
    @PostMapping("/lead-hot")
    public ResponseEntity<?> handleLeadHot(@RequestBody HotLeadWebhookDTO dto) {
        log.info("Received lead-hot webhook for leadId: {} with score: {}",
            dto.getLeadId(), dto.getScore());

        try {
            // TODO: Crear notificación para abogado vía notification-service
            log.info("HOT lead notification logged: {}", dto);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "HOT lead notification processed"
            ));

        } catch (Exception e) {
            log.error("Error processing HOT lead notification", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
```

---

#### 3.2. DTOs

**Ubicación**: `n8n-integration-service/src/main/java/com/carrillo/n8n/dto/`

```java
// LeadScoredWebhookDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadScoredWebhookDTO {
    private String leadId;
    private Integer score;
    private String categoria;
    private Map<String, Object> aiAnalysis;
    private Instant processedAt;
}

// HotLeadWebhookDTO.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotLeadWebhookDTO {
    private String leadId;
    private Integer score;
    private String categoria;
    private Instant notifiedAt;
    private String emailSentTo;
}

// UpdateLeadScoreDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLeadScoreDTO {
    private Integer score;
    private String categoria;
}
```

---

#### 3.3. Feign Client para client-service

**Ubicación**: `n8n-integration-service/src/main/java/com/carrillo/n8n/service/ClientServiceClient.java`

```java
package com.carrillo.n8n.service;

import com.carrillo.n8n.dto.UpdateLeadScoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "client-service", url = "${client-service.url}")
public interface ClientServiceClient {

    @PatchMapping("/api/leads/{id}/score")
    void updateLeadScore(
        @PathVariable("id") String id,
        @RequestBody UpdateLeadScoreDTO dto
    );
}
```

**Configuración**: `application.yml`

```yaml
client-service:
  url: http://localhost:8200/client-service
```

---

### Testing Tarea 3

```bash
# 1. Test callback lead-scored
curl -X POST http://localhost:8800/n8n-integration-service/webhook/lead-scored \
  -H "Content-Type: application/json" \
  -d '{
    "lead_id": "550e8400-e29b-41d4-a716-446655440000",
    "score": 85,
    "categoria": "HOT",
    "processed_at": "2026-01-05T20:00:00Z"
  }'

# 2. Verificar en PostgreSQL
docker exec -it postgresql psql -U carrillo -d carrillo_legal_tech -c \
  "SELECT id, nombre, score, categoria FROM clients.leads ORDER BY fecha_creacion DESC LIMIT 5;"

# 3. Test callback lead-hot
curl -X POST http://localhost:8800/n8n-integration-service/webhook/lead-hot \
  -H "Content-Type: application/json" \
  -d '{
    "lead_id": "550e8400-e29b-41d4-a716-446655440000",
    "score": 85,
    "categoria": "HOT",
    "notified_at": "2026-01-05T20:00:00Z",
    "email_sent_to": "marketing@carrilloabgd.com"
  }'
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Fase 1: client-service

- [ ] Crear `LeadCapturedEvent.java`
- [ ] Modificar `LeadService.java` para publicar NATS
- [ ] Agregar método `updateLeadScore()` en `LeadService.java`
- [ ] Crear `UpdateLeadScoreDTO.java`
- [ ] Agregar endpoint PATCH en `LeadResource.java`
- [ ] Configurar NATS en `application.yml`
- [ ] Agregar dependencia spring-nats en `pom.xml`
- [ ] Testing: Crear lead y verificar evento NATS

### Fase 2: n8n-integration-service Listener

- [ ] Crear `LeadEventListener.java` con @NatsListener
- [ ] Crear `RestTemplateConfig.java`
- [ ] Configurar URL webhook n8n en `application.yml`
- [ ] Agregar retry configuration
- [ ] Testing: Lead → NATS → n8n

### Fase 3: n8n-integration-service Webhooks

- [ ] Crear `N8nWebhookController.java`
- [ ] Crear DTOs: LeadScoredWebhookDTO, HotLeadWebhookDTO
- [ ] Crear `ClientServiceClient.java` (Feign)
- [ ] Configurar URL client-service
- [ ] Testing: Callback n8n → client-service

### Fase 4: Integración E2E

- [ ] Levantar stack completo
- [ ] Crear lead via formulario
- [ ] Verificar: PostgreSQL → NATS → n8n → Callback → PostgreSQL
- [ ] Validar score actualizado en BD
- [ ] Verificar email HOT lead enviado

---

## 🚨 NOTAS IMPORTANTES

1. **Manejo de Errores**: Los eventos NATS NO deben fallar la creación del lead
2. **Retry Logic**: Usar @Retryable para llamadas HTTP a n8n
3. **Logs**: Agregar logs detallados en cada paso para debugging
4. **Testing**: Probar cada componente aislado antes de integración
5. **Configuración**: Separar configs dev (localhost) vs docker vs prod

---

## 📞 COORDINACIÓN CON MARKETING DEV

### URLs Importantes

| Entorno | n8n Webhook (recibe) | Platform Webhooks (envía) |
|---------|---------------------|---------------------------|
| **Dev Local** | https://carrilloabgd.app.n8n.cloud/webhook/lead-events | http://localhost:8800/n8n-integration-service/webhook/* |
| **Docker** | https://carrilloabgd.app.n8n.cloud/webhook/lead-events | http://n8n-integration-service:8800/webhook/* |
| **Prod GCP** | https://carrilloabgd.app.n8n.cloud/webhook/lead-events | https://api.carrilloabgd.com/n8n-integration-service/webhook/* |

### Punto de Sincronización

Cuando termines **Tarea 2** (NATS Listener), avisa a Marketing Dev para:
1. Agregar nodos HTTP Request en SUB-A para callbacks
2. Testing conjunto E2E

---

**Próximo Paso**: Cuando completes estas 3 tareas, coordinar con Marketing Dev para testing E2E completo.
