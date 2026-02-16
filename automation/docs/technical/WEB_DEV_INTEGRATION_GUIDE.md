# GUÍA DE INTEGRACIÓN WEB - CARRILLO ABOGADOS AUTOMATION

**Versión:** 1.0
**Fecha:** 30 de Enero, 2026
**Responsable:** Alexis (Dev Backend/Frontend)
**Coordinación:** Juan Jose (Director Marketing)
**Tiempo Total Estimado:** 43.5 horas
**Timeline:** FASE 1-3 del Roadmap Marketing (Enero-Marzo 2026)

---

## 0. RESUMEN EJECUTIVO

### 0.1 Propósito del Documento

Este documento consolida **TODAS** las tareas de desarrollo web necesarias para el proyecto automation de Carrillo Abogados. Sirve como guía maestra para Alexis con:

- **5 Tracks principales** de desarrollo
- **Especificaciones técnicas completas**
- **División de trabajo** con Marketing
- **Timeline sincronizado** con ROADMAP_MARKETING_2026_v2.md

### 0.2 Tiempo Total Estimado

| Track | Descripción | Horas | Fase Roadmap | Prioridad |
|-------|-------------|-------|--------------|-----------|
| **Track 1** | Integración MW#1 (NATS + webhooks) | 7h | S3-S6 | P0 - CRÍTICO |
| **Track 2** | Integración MW#3 (blog-service) | 10h | S3-S6 | P1 - ALTA |
| **Track 3** | SEO Técnico | 11.5h | S3-S6 | P0 - CRÍTICO |
| **Track 4** | Landing Pages (5 LPs) | 10h | S5-S9 | P1 - ALTA |
| **Track 5** | Lead Magnets (5 formularios) | 5h | S5-S9 | P2 - MEDIA |
| **TOTAL** | - | **43.5h** | - | - |

### 0.3 Priorización de Tareas

**CRÍTICO (P0 - FASE 2):**
- Track 1: Integración MW#1 → Backend funcional para captura de leads
- Track 3: SEO Técnico → Fundamento para tráfico orgánico

**ALTA PRIORIDAD (P1 - FASE 2-3):**
- Track 2: blog-service → Habilita publicación automática de contenido
- Track 4: Landing Pages → Conversión de tráfico en leads

**MEDIA PRIORIDAD (P2 - FASE 3):**
- Track 5: Lead Magnets → Captura adicional de leads

### 0.4 Timeline General

```
FASE 1 (S1-S3): Investigación SEO
└─ Alexis: SOLO auditoría técnica (diagnóstico)

FASE 2 (S3-S6): IMPLEMENTACIÓN CRÍTICA ← MAYOR CARGA ALEXIS
├─ Track 1: MW#1 Integration (S3-S5)
├─ Track 2: blog-service (S3-S6)
└─ Track 3: SEO Técnico (S3-S6)

FASE 3 (S5-S9): Landing Pages + Soporte Contenido
├─ Track 4: Landing Pages (S7-S9)
└─ Track 5: Lead Magnets (S6-S8)

FASE 4 (S9-S11): Testing Conjunto
└─ E2E testing con Marketing
```

**NOTA IMPORTANTE:** Alexis tiene el MVP Spring Boot como prioridad principal. Este trabajo se coordina en paralelo sin sobrecargar.

---

## 1. TRACK 1: INTEGRACIÓN MW#1 (LEAD LIFECYCLE)

### 1.1 Referencia

**Documentación Completa Existe:**
[`automation/docs/technical/BACKEND_DEV_TASKS.md`](../BACKEND_DEV_TASKS.md)

Este track está **completamente documentado** en el documento de referencia arriba. A continuación, solo un resumen ejecutivo para ubicación contextual.

### 1.2 Resumen Ejecutivo

**Objetivo:** Conectar formularios web → n8n Cloud → Backend callbacks → PostgreSQL actualizado

**Arquitectura:**
```
[Formulario Web]
    │ POST /api/leads
    ▼
[client-service]
    │ Guarda en PostgreSQL (estado: NEW)
    │ Publica evento NATS "lead.capturado"
    ▼
[n8n-integration-service]
    │ NATS Listener
    │ POST a n8n Cloud webhook
    ▼
[n8n Cloud: MW#1 Orquestador v3.0]
    │ AI Agent (Gemini) procesa lead
    │ Scoring automático (0-100)
    │ Callback: POST /webhook/lead-scored
    ▼
[n8n-integration-service]
    │ Recibe callback
    │ PATCH /api/leads/{id}/score
    ▼
[client-service]
    │ Actualiza PostgreSQL (score, categoria)
    └─ COMPLETO
```

### 1.3 Componentes a Implementar

| # | Componente | Servicio | Tiempo | Detalle |
|---|------------|----------|--------|---------|
| **1.1** | Evento NATS Lead Captured | client-service | 2h | Ver BACKEND_DEV_TASKS.md §1 |
| **1.2** | NATS Listener + Webhook n8n | n8n-integration-service | 2h | Ver BACKEND_DEV_TASKS.md §2 |
| **1.3** | Webhooks Callbacks n8n | n8n-integration-service | 3h | Ver BACKEND_DEV_TASKS.md §3 |

**Total Track 1:** 7 horas

### 1.4 Estado Actual

**Verificado con STATUS.md MW#1:**
- ✅ Orquestador v3.0 (AI Agent) activo en n8n Cloud
- ✅ SUB-A: Lead Intake (17 nodos) listo
- ✅ Callbacks n8n YA configurados (Pipedream URLs en testing)
- ⏳ Backend: Endpoints pendientes de implementación

### 1.5 Próximos Pasos

**Para Alexis (Fase 2 - S3-S5):**
1. [ ] Implementar LeadCapturedEvent.java (client-service)
2. [ ] Modificar LeadService para publicar NATS
3. [ ] Agregar endpoint PATCH /api/leads/{id}/score
4. [ ] Implementar NATS Listener (n8n-integration-service)
5. [ ] Implementar N8nWebhookController (callbacks)
6. [ ] Crear Feign Client para client-service
7. [ ] Test E2E completo

**Ver especificaciones completas:** [`BACKEND_DEV_TASKS.md`](../BACKEND_DEV_TASKS.md)

---

## 2. TRACK 2: INTEGRACIÓN MW#3 (BLOG-SERVICE)

### 2.1 Contexto

**Estado:** SUB-M Publisher BLOQUEADO esperando decisión.

MW#3 genera contenido automáticamente:
- **SUB-L (Content Writer):** Genera artículos con Gemini AI → Google Docs
- **SUB-M (Publisher):** Debe publicar artículos aprobados en el blog

**DECISIÓN CRÍTICA:** Don Omar + Alexis deben decidir arquitectura del blog.

### 2.2 Opción A: blog-service (Spring Boot) ✅ RECOMENDADO

#### Pros y Contras

**Pros:**
- ✅ Arquitectura consistente (Spring Boot como resto de servicios)
- ✅ Autenticación unificada (OAuth2 ya configurado)
- ✅ Base de datos única (PostgreSQL)
- ✅ Control total del código
- ✅ Integración natural con client-service, case-service

**Contras:**
- ❌ Requiere desarrollo (8-10h Alexis)
- ❌ Nuevo microservicio a mantener

**Tiempo estimado:** 8-10 horas

#### 2.2.1 Componentes blog-service

**Estructura del proyecto:**
```
blog-service/
├── domain/
│   ├── BlogPost.java
│   ├── BlogCategory.java
│   └── BlogTag.java
├── repository/
│   └── BlogPostRepository.java
├── service/
│   └── BlogPostService.java
├── resource/
│   └── BlogPostResource.java (REST API)
└── config/
    └── SecurityConfig.java
```

#### 2.2.2 API Endpoints Necesarios

**API REST para SUB-M (n8n):**

```java
// Crear artículo (usado por SUB-M)
POST /api/blog/posts
{
  "title": "Cómo Registrar una Marca en Colombia 2026",
  "slug": "como-registrar-marca-colombia-2026",
  "meta_description": "Guía completa para registrar tu marca en Colombia...",
  "content": "<h1>Cómo Registrar...</h1><p>...</p>",
  "excerpt": "Guía completa...",
  "keyword_principal": "registro marca colombia",
  "keywords_secundarias": ["registrar marca", "cómo registrar marca"],
  "category_id": "uuid-categoria-pi",
  "featured_image_url": "https://...",
  "status": "draft"  // Siempre draft, Juan aprueba manualmente
}

// Listar artículos
GET /api/blog/posts?status=draft&limit=10

// Obtener por slug
GET /api/blog/posts/{slug}

// Actualizar artículo
PATCH /api/blog/posts/{id}
{
  "content": "...",
  "status": "draft"
}

// Publicar artículo (cambiar status)
POST /api/blog/posts/{id}/publish
{
  "publish_date": "2026-02-15T10:00:00Z"
}

// Eliminar artículo
DELETE /api/blog/posts/{id}
```

#### 2.2.3 Schema PostgreSQL

**Schema:** `blog`

```sql
CREATE SCHEMA blog;

-- Tabla principal de posts
CREATE TABLE blog.posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    meta_description VARCHAR(160),
    content TEXT NOT NULL,
    excerpt TEXT,
    keyword_principal VARCHAR(100),
    keywords_secundarias TEXT[],
    status VARCHAR(20) DEFAULT 'draft',
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    author_id UUID,
    category_id UUID,
    featured_image_url TEXT,
    seo_score INTEGER,
    views INTEGER DEFAULT 0,
    CONSTRAINT status_check CHECK (status IN ('draft', 'published', 'archived'))
);

-- Índices
CREATE INDEX idx_posts_slug ON blog.posts(slug);
CREATE INDEX idx_posts_status ON blog.posts(status);
CREATE INDEX idx_posts_published_at ON blog.posts(published_at);
CREATE INDEX idx_posts_keyword ON blog.posts(keyword_principal);

-- Tabla de categorías
CREATE TABLE blog.categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES blog.categories(id),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla de tags
CREATE TABLE blog.tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla de relación posts-tags (many-to-many)
CREATE TABLE blog.post_tags (
    post_id UUID REFERENCES blog.posts(id) ON DELETE CASCADE,
    tag_id UUID REFERENCES blog.tags(id) ON DELETE CASCADE,
    PRIMARY KEY (post_id, tag_id)
);
```

#### 2.2.4 Payload que SUB-M Enviará

**Desde n8n Cloud (SUB-M) → blog-service:**

```json
{
  "title": "Cómo Registrar una Marca en Colombia 2026",
  "slug": "como-registrar-marca-colombia-2026",
  "meta_description": "Guía completa para registrar tu marca en Colombia. Requisitos, costos y proceso paso a paso.",
  "content": "<h1>Cómo Registrar una Marca en Colombia</h1>\n<p>El registro de marca es un proceso...</p>\n<h2>Requisitos para Registro</h2>\n<ul><li>...</li></ul>",
  "excerpt": "Guía completa para PyMEs tech que necesitan proteger su marca comercial en Colombia.",
  "keyword_principal": "registro marca colombia",
  "keywords_secundarias": [
    "registrar marca",
    "cómo registrar marca",
    "registro marca colombia 2026"
  ],
  "category_id": "550e8400-e29b-41d4-a716-446655440000",  // UUID Propiedad Intelectual
  "featured_image_url": "https://storage.googleapis.com/.../registro-marca.jpg",
  "status": "draft"  // Siempre draft para revisión humana
}
```

**IMPORTANTE:** SUB-M NUNCA publica directamente. Juan debe aprobar manualmente vía:
- Opción 1: Dashboard blog-service (frontend)
- Opción 2: PATCH manual → POST /api/blog/posts/{id}/publish

#### 2.2.5 Tareas Implementación

**Tarea 2.1: Setup blog-service microservicio (3h)**

```bash
# Crear proyecto Spring Boot
spring init --dependencies=web,data-jpa,postgresql,security,validation \
  --group-id=com.carrillo.blog \
  --artifact-id=blog-service \
  --name=BlogService \
  blog-service

cd blog-service
```

**application.yml básico:**
```yaml
spring:
  application:
    name: blog-service
  datasource:
    url: jdbc:postgresql://localhost:5432/carrillo_legal_tech
    username: carrillo
    password: ${DB_PASSWORD}
  jpa:
    properties:
      hibernate:
        default_schema: blog
  flyway:
    schemas: blog

server:
  port: 8900
  servlet:
    context-path: /blog-service

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

**Tarea 2.2: Modelo y Repository (2h)**

**BlogPost.java:**
```java
package com.carrillo.blog.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "posts", schema = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "keyword_principal", length = 100)
    private String keywordPrincipal;

    @Column(name = "keywords_secundarias")
    private String[] keywordsSecundarias;  // PostgreSQL array type

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "featured_image_url", columnDefinition = "TEXT")
    private String featuredImageUrl;

    @Column(name = "seo_score")
    private Integer seoScore;

    @Column(nullable = false)
    private Integer views = 0;

    public enum PostStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
```

**BlogPostRepository.java:**
```java
package com.carrillo.blog.repository;

import com.carrillo.blog.domain.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {

    Optional<BlogPost> findBySlug(String slug);

    Page<BlogPost> findByStatus(BlogPost.PostStatus status, Pageable pageable);

    Page<BlogPost> findByCategoryId(UUID categoryId, Pageable pageable);

    boolean existsBySlug(String slug);
}
```

**Flyway migration:** `src/main/resources/db/migration/V1__create_blog_schema.sql`
```sql
CREATE SCHEMA IF NOT EXISTS blog;

CREATE TABLE blog.posts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    meta_description VARCHAR(160),
    content TEXT NOT NULL,
    excerpt TEXT,
    keyword_principal VARCHAR(100),
    keywords_secundarias TEXT[],
    status VARCHAR(20) DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    author_id UUID,
    category_id UUID,
    featured_image_url TEXT,
    seo_score INTEGER,
    views INTEGER DEFAULT 0,
    CONSTRAINT status_check CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);

CREATE INDEX idx_posts_slug ON blog.posts(slug);
CREATE INDEX idx_posts_status ON blog.posts(status);
CREATE INDEX idx_posts_published_at ON blog.posts(published_at);
```

**Tarea 2.3: Service Layer (2h)**

**BlogPostService.java:**
```java
package com.carrillo.blog.service;

import com.carrillo.blog.domain.BlogPost;
import com.carrillo.blog.dto.CreateBlogPostDTO;
import com.carrillo.blog.dto.UpdateBlogPostDTO;
import com.carrillo.blog.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    @Transactional
    public BlogPost createPost(CreateBlogPostDTO dto) {
        // Validar slug único
        if (blogPostRepository.existsBySlug(dto.getSlug())) {
            throw new IllegalArgumentException("Slug already exists: " + dto.getSlug());
        }

        BlogPost post = new BlogPost();
        post.setTitle(dto.getTitle());
        post.setSlug(dto.getSlug());
        post.setMetaDescription(dto.getMetaDescription());
        post.setContent(dto.getContent());
        post.setExcerpt(dto.getExcerpt());
        post.setKeywordPrincipal(dto.getKeywordPrincipal());
        post.setKeywordsSecundarias(dto.getKeywordsSecundarias());
        post.setStatus(BlogPost.PostStatus.DRAFT);
        post.setCategoryId(dto.getCategoryId());
        post.setFeaturedImageUrl(dto.getFeaturedImageUrl());

        BlogPost savedPost = blogPostRepository.save(post);

        log.info("Created blog post: id={}, slug={}", savedPost.getId(), savedPost.getSlug());
        return savedPost;
    }

    @Transactional
    public BlogPost updatePost(UUID id, UpdateBlogPostDTO dto) {
        BlogPost post = blogPostRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found: " + id));

        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
        if (dto.getStatus() != null) {
            post.setStatus(dto.getStatus());
        }
        // ... otros campos

        return blogPostRepository.save(post);
    }

    @Transactional
    public BlogPost publishPost(UUID id, Instant publishDate) {
        BlogPost post = blogPostRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found: " + id));

        post.setStatus(BlogPost.PostStatus.PUBLISHED);
        post.setPublishedAt(publishDate != null ? publishDate : Instant.now());

        BlogPost published = blogPostRepository.save(post);

        log.info("Published blog post: id={}, slug={}", published.getId(), published.getSlug());
        return published;
    }

    public BlogPost getPostBySlug(String slug) {
        return blogPostRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Post not found: " + slug));
    }

    public Page<BlogPost> getAllPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable);
    }

    public Page<BlogPost> getPostsByStatus(BlogPost.PostStatus status, Pageable pageable) {
        return blogPostRepository.findByStatus(status, pageable);
    }

    @Transactional
    public void deletePost(UUID id) {
        blogPostRepository.deleteById(id);
        log.info("Deleted blog post: id={}", id);
    }
}
```

**Tarea 2.4: REST API (2h)**

**BlogPostResource.java:**
```java
package com.carrillo.blog.resource;

import com.carrillo.blog.domain.BlogPost;
import com.carrillo.blog.dto.*;
import com.carrillo.blog.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/blog/posts")
@RequiredArgsConstructor
@Slf4j
public class BlogPostResource {

    private final BlogPostService blogPostService;

    @PostMapping
    public ResponseEntity<BlogPostDTO> createPost(@RequestBody @Valid CreateBlogPostDTO dto) {
        log.info("Creating blog post: title={}", dto.getTitle());

        BlogPost post = blogPostService.createPost(dto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(toDTO(post));
    }

    @GetMapping
    public ResponseEntity<Page<BlogPostDTO>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String status
    ) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<BlogPost> posts = status != null
            ? blogPostService.getPostsByStatus(BlogPost.PostStatus.valueOf(status.toUpperCase()), pageable)
            : blogPostService.getAllPosts(pageable);

        return ResponseEntity.ok(posts.map(this::toDTO));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BlogPostDTO> getPostBySlug(@PathVariable String slug) {
        BlogPost post = blogPostService.getPostBySlug(slug);
        return ResponseEntity.ok(toDTO(post));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogPostDTO> updatePost(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateBlogPostDTO dto
    ) {
        log.info("Updating blog post: id={}", id);

        BlogPost updated = blogPostService.updatePost(id, dto);

        return ResponseEntity.ok(toDTO(updated));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<BlogPostDTO> publishPost(
        @PathVariable UUID id,
        @RequestBody(required = false) PublishPostDTO dto
    ) {
        log.info("Publishing blog post: id={}", id);

        Instant publishDate = dto != null && dto.getPublishDate() != null
            ? dto.getPublishDate()
            : Instant.now();

        BlogPost published = blogPostService.publishPost(id, publishDate);

        return ResponseEntity.ok(toDTO(published));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        log.info("Deleting blog post: id={}", id);

        blogPostService.deletePost(id);

        return ResponseEntity.noContent().build();
    }

    // Helper method
    private BlogPostDTO toDTO(BlogPost post) {
        return BlogPostDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .slug(post.getSlug())
            .metaDescription(post.getMetaDescription())
            .content(post.getContent())
            .excerpt(post.getExcerpt())
            .keywordPrincipal(post.getKeywordPrincipal())
            .keywordsSecundarias(post.getKeywordsSecundarias())
            .status(post.getStatus().name())
            .publishedAt(post.getPublishedAt())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .categoryId(post.getCategoryId())
            .featuredImageUrl(post.getFeaturedImageUrl())
            .seoScore(post.getSeoScore())
            .views(post.getViews())
            .build();
    }
}
```

**DTOs:**

**CreateBlogPostDTO.java:**
```java
package com.carrillo.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlogPostDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Slug is required")
    @Size(max = 255)
    private String slug;

    @Size(max = 160)
    private String metaDescription;

    @NotBlank(message = "Content is required")
    private String content;

    private String excerpt;

    @Size(max = 100)
    private String keywordPrincipal;

    private String[] keywordsSecundarias;

    private UUID categoryId;

    private String featuredImageUrl;
}
```

**UpdateBlogPostDTO.java:**
```java
package com.carrillo.blog.dto;

import com.carrillo.blog.domain.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBlogPostDTO {

    private String content;

    private BlogPost.PostStatus status;

    private String excerpt;

    private String featuredImageUrl;

    private Integer seoScore;
}
```

**BlogPostDTO.java:**
```java
package com.carrillo.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostDTO {

    private UUID id;
    private String title;
    private String slug;
    private String metaDescription;
    private String content;
    private String excerpt;
    private String keywordPrincipal;
    private String[] keywordsSecundarias;
    private String status;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID categoryId;
    private String featuredImageUrl;
    private Integer seoScore;
    private Integer views;
}
```

**Tarea 2.5: Seguridad (1h)**

**SecurityConfig.java:**
```java
package com.carrillo.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Para API REST, manejar con tokens
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (lecturas de blog publicado)
                .requestMatchers(HttpMethod.GET, "/api/blog/posts/{slug}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/posts").permitAll()

                // Escrituras solo para n8n o admin (implementar con API key o OAuth2)
                .requestMatchers(HttpMethod.POST, "/api/blog/posts").hasRole("N8N_SERVICE")
                .requestMatchers(HttpMethod.PATCH, "/api/blog/posts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/blog/posts/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/blog/posts/*/publish").hasRole("ADMIN")

                // Health checks
                .requestMatchers("/actuator/health").permitAll()

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```

**NOTA:** Para MVP, considerar API key simple para n8n en lugar de OAuth2 completo.

**Total Opción A (blog-service):** 10 horas

---

### 2.3 Opción B: WordPress REST API ⚠️ BACKUP PLAN

**Solo usar si blog-service se rechaza o retrasa demasiado.**

#### 2.3.1 Pros y Contras

**Pros:**
- ✅ 0 horas desarrollo backend (Alexis)
- ✅ CMS maduro y conocido
- ✅ Plugins SEO existentes (Yoast, RankMath)
- ✅ Temas profesionales disponibles

**Contras:**
- ❌ Arquitectura inconsistente (PHP + MySQL vs. Spring Boot + PostgreSQL)
- ❌ Autenticación separada (WordPress users vs. OAuth2)
- ❌ Base de datos separada (requiere MySQL)
- ❌ Dependencia externa (hosting WordPress)

**Tiempo estimado:** 3-4 horas configuración + integración n8n

#### 2.3.2 Integración n8n → WordPress

**Endpoint WordPress REST API:**
```
POST https://blog.carrilloabgd.com/wp-json/wp/v2/posts
```

**Headers:**
```
Authorization: Bearer {WORDPRESS_APP_PASSWORD}
Content-Type: application/json
```

**Payload desde SUB-M:**
```json
{
  "title": "Cómo Registrar una Marca en Colombia 2026",
  "content": "<h1>...</h1><p>...</p>",
  "excerpt": "Guía completa...",
  "status": "draft",
  "categories": [1],
  "tags": [2, 3],
  "meta": {
    "yoast_wpseo_metadesc": "Guía completa para registrar...",
    "yoast_wpseo_focuskw": "registro marca colombia"
  },
  "featured_media": 123  // ID de imagen en WordPress
}
```

**Autenticación:** Application Passwords (WordPress 5.6+)

**Nodo n8n:**
- HTTP Request node
- Method: POST
- URL: `{{$env.WORDPRESS_URL}}/wp-json/wp/v2/posts`
- Authentication: Generic Credential Type
  - Header Auth
  - Name: Authorization
  - Value: `Bearer {{$env.WORDPRESS_APP_PASSWORD}}`

**Total Opción B (WordPress):** 3-4 horas (pero deuda técnica arquitectura)

---

### 2.4 Opción C: Manual Temporal 🔄 WORKAROUND

**Solo para MVP o mientras se decide A o B.**

**Flujo:**
1. SUB-L genera contenido → Google Docs
2. Juan copia/pega manualmente a blog actual (WordPress, Webflow, etc.)
3. Juan publica manualmente

**Pros:**
- ✅ 0 horas dev
- ✅ Control total humano

**Contras:**
- ❌ NO escalable (20 artículos = 10 horas manual)
- ❌ Bottleneck en Juan
- ❌ Derrota propósito automatización

**Uso:** Solo temporal hasta implementar A o B.

---

### 2.5 Recomendación Final

**RECOMENDADO:** Opción A (blog-service)

**Razones:**
1. Consistencia arquitectónica (todo Spring Boot)
2. Inversión 10h se amortiza rápido (vs. 10h/mes manual)
3. Control total sobre features (analytics, SEO personalizado)
4. Base datos única → reportes consolidados
5. Escalable a largo plazo

**Decisión requerida:** Don Omar + Alexis validan viabilidad según carga MVP.

**Si blog-service se rechaza:** Opción B (WordPress) es fallback aceptable.

---

## 3. TRACK 3: SEO TÉCNICO (CRÍTICO - FASE 2)

### 3.1 Contexto

**Basado en:** ROADMAP_MARKETING_2026_v2.md - FASE 2 (S3-S6)

Este track implementa mejoras técnicas para SEO on-page y off-page. Marketing genera las instrucciones específicas, Alexis implementa.

**Coordinación:** Alexis ejecuta **SOLO DESPUÉS** de recibir instrucciones de Marketing.

### 3.2 Auditoría Técnica Web Actual (FASE 1 - S2)

**Responsable:** Alexis (Dev)
**Coordinación:** Marketing espera reporte
**Duración:** 1 semana

**Tarea 3.1.1: Auditoría SEO Técnico (2h)**

**Herramientas:**
- Google Search Console (requiere verificación)
- SEMrush Pro (manual)
- Screaming Frog SEO Spider (free version)

**Checklist auditoría:**
```bash
# Verificar errores HTTP
curl -I https://www.carrilloabgd.com
# Expected: 200 OK

# Verificar redirects
curl -I http://www.carrilloabgd.com
# Expected: 301 → https://www.carrilloabgd.com

# Verificar sitemap
curl https://www.carrilloabgd.com/sitemap.xml
# Expected: 200, XML válido

# Verificar robots.txt
curl https://www.carrilloabgd.com/robots.txt
# Expected: 200, sin bloqueos críticos
```

**Output esperado (documento para Marketing):**
```markdown
## Reporte Auditoría SEO Técnico

**Fecha:** [fecha]
**Auditor:** Alexis

### Errores Críticos (P0)
- [ ] 404 Not Found: X páginas
- [ ] 500 Server Error: X páginas
- [ ] Sin SSL (HTTP): X URLs
- [ ] Duplicate content: X páginas

### Warnings (P1)
- [ ] Redirects 301: X cadenas > 3 niveles
- [ ] Missing meta description: X páginas
- [ ] Páginas sin indexar: X

### Velocidad
- [ ] Tiempo carga promedio: X segundos
- [ ] Time to First Byte (TTFB): X ms

### Mobile
- [ ] Responsive issues: X elementos
- [ ] Touch targets < 48px: X botones

### Indexación
- [ ] Páginas indexadas: X (Google Search Console)
- [ ] Páginas bloqueadas por robots.txt: X
```

**Tarea 3.1.2: Core Web Vitals Check (1h)**

**Herramienta:** PageSpeed Insights (https://pagespeed.web.dev/)

**URLs a testear:**
- Homepage: https://www.carrilloabgd.com
- Página de servicio: https://www.carrilloabgd.com/servicios/propiedad-intelectual
- Blog (si existe): https://www.carrilloabgd.com/blog
- Contacto: https://www.carrilloabgd.com/contacto

**Métricas críticas:**
```
LCP (Largest Contentful Paint): < 2.5s
FID (First Input Delay): < 100ms
CLS (Cumulative Layout Shift): < 0.1
```

**Output esperado (Google Sheet o tabla):**
| URL | LCP (s) | FID (ms) | CLS | Mobile Score | Desktop Score | Issues Priorizados |
|-----|---------|----------|-----|--------------|---------------|-------------------|
| Homepage | X | X | X | X/100 | X/100 | [lista] |
| ... | ... | ... | ... | ... | ... | ... |

**Tarea 3.1.3: Análisis Arquitectura Info (1h)**

**Herramienta:** Screaming Frog (manual)

**Análisis:**
1. Estructura URLs actual (flat vs. jerárquica)
2. Profundidad de contenido (clicks desde home)
3. Internal linking (páginas huérfanas)
4. Breadcrumbs existentes

**Output esperado:**
```markdown
## Arquitectura de Información Actual

### Estructura URLs
Actual: /servicios?id=123 (query params - MAL)
Recomendado: /servicios/propiedad-intelectual/registro-de-marca

### Profundidad
- Nivel 1 (Home): 1 página
- Nivel 2 (/servicios, /sobre-nosotros): X páginas
- Nivel 3 (/servicios/propiedad-intelectual): X páginas
- Nivel 4+: X páginas ← REDUCIR

### Internal Links
- Páginas huérfanas (0 enlaces internos): X
- Páginas con > 100 enlaces salientes: X

### Breadcrumbs
Estado actual: [Presente/Ausente]
```

**Total Auditoría (Track 3 - Fase 1):** 4 horas

---

### 3.3 Implementación SEO Técnico (FASE 2 - S3-S6)

**IMPORTANTE:** Estas tareas se ejecutan DESPUÉS de recibir instrucciones de Marketing basadas en la auditoría.

**Tarea 3.2.1: Optimización Core Web Vitals (4h)**

**Objetivo:** LCP < 2.5s, FID < 100ms, CLS < 0.1

**Acciones específicas por métrica:**

**LCP (Largest Contentful Paint):**

**Next.js config optimizations:**
```javascript
// next.config.js
module.exports = {
  images: {
    formats: ['image/webp', 'image/avif'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
    loader: 'default',
  },
  compiler: {
    removeConsole: process.env.NODE_ENV === 'production',
  },
  swcMinify: true,
}
```

**Image optimization:**
```jsx
// Antes (MAL)
<img src="/hero-image.jpg" alt="Hero" />

// Después (BIEN)
import Image from 'next/image'

<Image
  src="/hero-image.jpg"
  alt="Hero"
  width={1920}
  height={1080}
  priority  // Para hero images
  placeholder="blur"
  blurDataURL="data:image/..." // Low quality placeholder
/>
```

**Preconnect y preload:**
```javascript
// pages/_document.js
import { Html, Head, Main, NextScript } from 'next/document'

export default function Document() {
  return (
    <Html lang="es-CO">
      <Head>
        {/* Preconnect a dominios externos */}
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin />
        <link rel="dns-prefetch" href="https://www.googletagmanager.com" />

        {/* Preload recursos críticos */}
        <link
          rel="preload"
          href="/fonts/inter-var.woff2"
          as="font"
          type="font/woff2"
          crossOrigin="anonymous"
        />
      </Head>
      <body>
        <Main />
        <NextScript />
      </body>
    </Html>
  )
}
```

**FID (First Input Delay):**

**Code splitting dinámico:**
```jsx
// Antes (MAL) - Importa todo de una
import HeavyComponent from '@/components/HeavyComponent'

// Después (BIEN) - Carga bajo demanda
import dynamic from 'next/dynamic'

const HeavyComponent = dynamic(() => import('@/components/HeavyComponent'), {
  loading: () => <p>Loading...</p>,
  ssr: false  // Solo client-side si no es crítico para SEO
})
```

**Defer JavaScript no crítico:**
```javascript
// pages/_app.js
import Script from 'next/script'

function MyApp({ Component, pageProps }) {
  return (
    <>
      <Component {...pageProps} />

      {/* Google Analytics - defer */}
      <Script
        src="https://www.googletagmanager.com/gtag/js?id=GA_MEASUREMENT_ID"
        strategy="afterInteractive"  // Carga después de interactividad
      />
    </>
  )
}
```

**CLS (Cumulative Layout Shift):**

**Dimensiones explícitas en imágenes:**
```jsx
// Antes (MAL) - Causa layout shift
<img src="/logo.png" alt="Logo" />

// Después (BIEN) - Espacio reservado
<img
  src="/logo.png"
  alt="Logo"
  width={200}
  height={50}
  style={{ width: '200px', height: '50px' }}
/>
```

**Font display optimization:**
```css
/* styles/globals.css */
@font-face {
  font-family: 'Inter';
  src: url('/fonts/inter-var.woff2') format('woff2');
  font-weight: 100 900;
  font-display: swap;  /* Evita FOIT (Flash of Invisible Text) */
  font-style: normal;
}
```

**Evitar inserción dinámica de contenido:**
```jsx
// Antes (MAL) - Banner aparece después causando shift
{showBanner && <Banner />}

// Después (BIEN) - Espacio reservado desde inicio
<div className="banner-container" style={{ minHeight: showBanner ? '80px' : '0' }}>
  {showBanner && <Banner />}
</div>
```

**CDN para assets estáticos:**
```javascript
// next.config.js
module.exports = {
  assetPrefix: process.env.NODE_ENV === 'production'
    ? 'https://cdn.carrilloabgd.com'
    : '',
}
```

**Tarea 3.2.2: Schema Markup Implementación (3h)**

**Objetivo:** Rich snippets en Google

**Schemas necesarios:**
1. Organization Schema (sitio completo)
2. LegalService Schema (páginas de servicio)
3. FAQPage Schema (FAQs)
4. Article Schema (artículos blog)
5. BreadcrumbList Schema (navegación)

**Implementación Organization Schema:**

**components/SchemaMarkup.tsx:**
```tsx
export function OrganizationSchema() {
  const schema = {
    "@context": "https://schema.org",
    "@type": "LegalService",
    "name": "Carrillo Abogados",
    "alternateName": "Carrillo ABGD SAS",
    "description": "Firma de abogados especializada en Propiedad Intelectual, Registro de Marcas y Contratación Estatal en Colombia. 24 años de experiencia.",
    "url": "https://www.carrilloabgd.com",
    "logo": "https://www.carrilloabgd.com/images/logo.png",
    "image": "https://www.carrilloabgd.com/images/oficina-torre-cali.jpg",
    "telephone": "+57-2-XXX-XXXX",
    "email": "contacto@carrilloabgd.com",
    "foundingDate": "2001-04",
    "address": {
      "@type": "PostalAddress",
      "streetAddress": "Torre de Cali, Piso 21",
      "addressLocality": "Cali",
      "addressRegion": "Valle del Cauca",
      "postalCode": "760001",
      "addressCountry": "CO"
    },
    "areaServed": {
      "@type": "Country",
      "name": "Colombia"
    },
    "priceRange": "$$",
    "sameAs": [
      "https://www.linkedin.com/company/carrillo-abogados",
      "https://www.facebook.com/carrilloabogados"
    ]
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
    />
  );
}
```

**Uso en _app.tsx:**
```tsx
// pages/_app.tsx
import { OrganizationSchema } from '@/components/SchemaMarkup';

function MyApp({ Component, pageProps }) {
  return (
    <>
      <OrganizationSchema />
      <Component {...pageProps} />
    </>
  );
}

export default MyApp;
```

**LegalService Schema (página servicio):**

**components/LegalServiceSchema.tsx:**
```tsx
interface LegalServiceSchemaProps {
  serviceName: string;
  description: string;
  price?: string;
}

export function LegalServiceSchema({ serviceName, description, price }: LegalServiceSchemaProps) {
  const schema = {
    "@context": "https://schema.org",
    "@type": "Service",
    "serviceType": serviceName,
    "provider": {
      "@type": "LegalService",
      "name": "Carrillo Abogados"
    },
    "areaServed": {
      "@type": "Country",
      "name": "Colombia"
    },
    "description": description,
    "offers": price ? {
      "@type": "Offer",
      "price": price,
      "priceCurrency": "COP"
    } : undefined
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
    />
  );
}
```

**Uso en página servicio:**
```tsx
// pages/servicios/registro-marca.tsx
import { LegalServiceSchema } from '@/components/LegalServiceSchema';

export default function RegistroMarcaPage() {
  return (
    <>
      <LegalServiceSchema
        serviceName="Registro de Marca en Colombia"
        description="Servicio completo de registro de marcas ante la SIC con acompañamiento legal especializado. Incluye búsqueda de anterioridades, presentación de solicitud y seguimiento hasta la aprobación."
        price="Desde 1500000"
      />
      {/* Contenido de la página */}
    </>
  );
}
```

**FAQPage Schema:**

```tsx
interface FAQ {
  question: string;
  answer: string;
}

interface FAQSchemaProps {
  faqs: FAQ[];
}

export function FAQSchema({ faqs }: FAQSchemaProps) {
  const schema = {
    "@context": "https://schema.org",
    "@type": "FAQPage",
    "mainEntity": faqs.map(faq => ({
      "@type": "Question",
      "name": faq.question,
      "acceptedAnswer": {
        "@type": "Answer",
        "text": faq.answer
      }
    }))
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
    />
  );
}
```

**Article Schema (blog):**

```tsx
interface ArticleSchemaProps {
  title: string;
  description: string;
  publishDate: string;
  modifiedDate: string;
  authorName: string;
  imageUrl: string;
}

export function ArticleSchema({
  title,
  description,
  publishDate,
  modifiedDate,
  authorName,
  imageUrl
}: ArticleSchemaProps) {
  const schema = {
    "@context": "https://schema.org",
    "@type": "Article",
    "headline": title,
    "description": description,
    "image": imageUrl,
    "datePublished": publishDate,
    "dateModified": modifiedDate,
    "author": {
      "@type": "Person",
      "name": authorName
    },
    "publisher": {
      "@type": "Organization",
      "name": "Carrillo Abogados",
      "logo": {
        "@type": "ImageObject",
        "url": "https://www.carrilloabgd.com/images/logo.png"
      }
    }
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
    />
  );
}
```

**BreadcrumbList Schema:**

```tsx
interface BreadcrumbItem {
  name: string;
  url?: string;
}

interface BreadcrumbSchemaProps {
  items: BreadcrumbItem[];
}

export function BreadcrumbSchema({ items }: BreadcrumbSchemaProps) {
  const schema = {
    "@context": "https://schema.org",
    "@type": "BreadcrumbList",
    "itemListElement": items.map((item, index) => ({
      "@type": "ListItem",
      "position": index + 1,
      "name": item.name,
      "item": item.url
    }))
  };

  return (
    <script
      type="application/ld+json"
      dangerouslySetInnerHTML={{ __html: JSON.stringify(schema) }}
    />
  );
}
```

**Uso:**
```tsx
<BreadcrumbSchema
  items={[
    { name: "Inicio", url: "https://www.carrilloabgd.com" },
    { name: "Servicios", url: "https://www.carrilloabgd.com/servicios" },
    { name: "Propiedad Intelectual" }
  ]}
/>
```

**Tarea 3.2.3: Arquitectura Información Mejorada (2h)**

**Input:** Nueva estructura URLs + menú navegación (de Marketing)

**Estructura URLs SEO-friendly:**

**ANTES (mal):**
```
/servicios?id=123
/blog?post=456
/contacto?ref=landing
```

**DESPUÉS (bien):**
```
/servicios/propiedad-intelectual/registro-de-marca
/blog/como-registrar-marca-colombia-2026
/contacto
```

**Implementación Next.js (App Router):**

**Nueva estructura de carpetas:**
```
app/
├── page.tsx (/)
├── servicios/
│   ├── page.tsx (/servicios)
│   ├── propiedad-intelectual/
│   │   ├── page.tsx (/servicios/propiedad-intelectual)
│   │   ├── registro-de-marca/
│   │   │   └── page.tsx
│   │   ├── patentes/
│   │   │   └── page.tsx
│   │   └── derechos-autor/
│   │       └── page.tsx
│   ├── contratacion-estatal/
│   │   ├── page.tsx
│   │   └── licitaciones-publicas/
│   │       └── page.tsx
│   └── litigio/
│       └── page.tsx
├── blog/
│   ├── page.tsx (/blog)
│   └── [slug]/
│       └── page.tsx (/blog/{slug})
└── contacto/
    └── page.tsx
```

**Generar slug SEO-friendly:**

**utils/slugify.ts:**
```typescript
export function slugify(text: string): string {
  return text
    .toLowerCase()
    .normalize('NFD')  // Descomponer caracteres con tildes
    .replace(/[\u0300-\u036f]/g, '')  // Eliminar diacríticos
    .replace(/[^a-z0-9]+/g, '-')  // Reemplazar no-alfanuméricos con guiones
    .replace(/^-+|-+$/g, '');  // Eliminar guiones al inicio/fin
}

// Uso:
// slugify("Cómo Registrar una Marca en Colombia")
// → "como-registrar-una-marca-en-colombia"
```

**Breadcrumbs component:**

**components/Breadcrumbs.tsx:**
```tsx
'use client';

import { usePathname } from 'next/navigation';
import Link from 'next/link';
import { BreadcrumbSchema } from './SchemaMarkup';

export function Breadcrumbs() {
  const pathname = usePathname();
  const paths = pathname.split('/').filter(Boolean);

  const breadcrumbItems = [
    { name: 'Inicio', url: 'https://www.carrilloabgd.com' },
    ...paths.map((path, index) => {
      const url = index === paths.length - 1
        ? undefined  // Último item sin URL
        : `https://www.carrilloabgd.com/${paths.slice(0, index + 1).join('/')}`;

      return {
        name: path.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase()),
        url
      };
    })
  ];

  return (
    <>
      <BreadcrumbSchema items={breadcrumbItems} />
      <nav aria-label="Breadcrumb">
        <ol className="flex space-x-2">
          {breadcrumbItems.map((item, index) => (
            <li key={index}>
              {item.url ? (
                <Link href={item.url} className="text-blue-600 hover:underline">
                  {item.name}
                </Link>
              ) : (
                <span className="text-gray-700">{item.name}</span>
              )}
              {index < breadcrumbItems.length - 1 && (
                <span className="mx-2">/</span>
              )}
            </li>
          ))}
        </ol>
      </nav>
    </>
  );
}
```

**Tarea 3.2.4: Setup Google Search Console (30 min)**

**Paso 1: Verificar propiedad del sitio**

**Método recomendado:** HTML tag

```html
<!-- pages/_document.js - En <Head> -->
<meta name="google-site-verification" content="VERIFICATION_CODE" />
```

**Alternativa:** TXT record DNS
```
Tipo: TXT
Host: @
Valor: google-site-verification=VERIFICATION_CODE
```

**Paso 2: Sitemap XML dinámico**

**app/sitemap.ts (Next.js 13+ App Router):**
```typescript
import { MetadataRoute } from 'next'

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const baseUrl = 'https://www.carrilloabgd.com'

  // Páginas estáticas
  const routes = [
    '',
    '/servicios',
    '/servicios/propiedad-intelectual',
    '/servicios/propiedad-intelectual/registro-de-marca',
    '/servicios/propiedad-intelectual/patentes',
    '/servicios/contratacion-estatal',
    '/sobre-nosotros',
    '/contacto',
  ].map((route) => ({
    url: `${baseUrl}${route}`,
    lastModified: new Date().toISOString(),
    changeFrequency: 'monthly' as const,
    priority: route === '' ? 1 : 0.8,
  }))

  // Artículos blog (dinámico desde API/BD)
  // Si blog-service está implementado:
  const blogPosts = await getBlogPosts();  // Función fetch a blog-service
  const posts = blogPosts.map((post) => ({
    url: `${baseUrl}/blog/${post.slug}`,
    lastModified: post.updated_at,
    changeFrequency: 'weekly' as const,
    priority: 0.6,
  }))

  return [...routes, ...posts]
}

async function getBlogPosts() {
  // Llamada a blog-service API
  const res = await fetch('https://api.carrilloabgd.com/blog-service/api/blog/posts?status=published', {
    next: { revalidate: 3600 }  // Cache 1 hora
  });

  if (!res.ok) return [];

  const data = await res.json();
  return data.content || [];  // Asumiendo Page<BlogPostDTO>
}
```

**Paso 3: robots.txt**

**public/robots.txt:**
```
# Permitir todo
User-agent: *
Allow: /

# Bloquear áreas privadas
Disallow: /admin
Disallow: /api

# Sitemap
Sitemap: https://www.carrilloabgd.com/sitemap.xml
```

**Paso 4: Enviar sitemap a Google Search Console**

1. Ir a https://search.google.com/search-console
2. Seleccionar propiedad verificada
3. Sitemaps → Agregar sitemap → `https://www.carrilloabgd.com/sitemap.xml`
4. Enviar

**Tarea 3.2.5: Optimización Mobile (2h)**

**Checklist Mobile:**

1. **Responsive design verificado**

**Breakpoints consistentes:**
```css
/* tailwind.config.js */
module.exports = {
  theme: {
    screens: {
      'sm': '640px',
      'md': '768px',
      'lg': '1024px',
      'xl': '1280px',
      '2xl': '1536px',
    },
  },
}
```

2. **Touch targets > 48px**

```css
/* Antes (MAL) */
.button {
  padding: 8px 16px;  /* Solo 32px alto */
}

/* Después (BIEN) */
.button {
  padding: 12px 24px;  /* 48px alto mínimo */
  min-height: 48px;
}
```

3. **Font size legible (16px mínimo)**

```css
/* globals.css */
body {
  font-size: 16px;  /* NUNCA < 16px en mobile */
  line-height: 1.5;
}

/* Títulos escalables */
h1 {
  font-size: clamp(1.75rem, 5vw, 3rem);  /* 28px min, 48px max */
}
```

4. **No horizontal scroll**

```css
/* Prevenir overflow horizontal */
html, body {
  overflow-x: hidden;
  max-width: 100vw;
}

/* Imágenes responsivas */
img {
  max-width: 100%;
  height: auto;
}
```

5. **Viewport meta tag**

```html
<!-- pages/_document.js -->
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0" />
```

**Testing:**

**Chrome DevTools (Device Mode):**
```
1. F12 (DevTools)
2. Toggle Device Toolbar (Ctrl+Shift+M)
3. Probar:
   - iPhone SE (375x667)
   - iPhone 12 Pro (390x844)
   - Pixel 5 (393x851)
   - Samsung Galaxy S20 (360x800)
```

**Real Device Testing:**
- iOS Safari (iPhone)
- Android Chrome (Samsung/Pixel)

**Google Mobile-Friendly Test:**
https://search.google.com/test/mobile-friendly

**Total SEO Técnico:** 11.5 horas

---

## 4. TRACK 4: LANDING PAGES TRANSACCIONALES (FASE 3)

### 4.1 Contexto

**Basado en:** ROADMAP - FASE 3 (S5-S9)

Landing pages de alta conversión para servicios específicos. Marketing provee copywriting + wireframes, Alexis implementa HTML/CSS.

### 4.2 Landing Pages a Implementar

**Total:** 5 landing pages (Tarea 3.3 del roadmap)

| # | Landing Page | Servicio | Keyword Principal | Deadline |
|---|--------------|----------|-------------------|----------|
| 1 | LP: Registro de Marca | PI - Marcas | "registro marca colombia" | S7 - 6 Marzo |
| 2 | LP: Asesoría PI | PI - Consultoría | "abogado propiedad intelectual" | S8 - 13 Marzo |
| 3 | LP: Contratación Estatal | Contratación | "abogado licitaciones públicas" | S8 - 13 Marzo |
| 4 | LP: Derecho Comercial Startups | Corporativo | "abogado comercial startups" | S9 - 20 Marzo |
| 5 | LP: Litigios PI | PI - Defensa | "abogado litigios marcas" | S9 - 20 Marzo |

### 4.3 División de Trabajo

**Marketing (Juan):**
- ✅ Copywriting completo (Gemini AI + validación)
- ✅ Wireframes (Canva/Figma)
- ✅ Instrucciones técnicas SEO (keywords, meta, schema)
- ✅ Assets visuales (imágenes, iconos)

**Tech (Alexis):**
- ✅ Implementación HTML/CSS/React
- ✅ Integración formularios (MW#1 webhook)
- ✅ Schema markup (Service, Organization)
- ✅ Optimización conversión (CTA, above fold)

### 4.4 Componentes Cada Landing Page

**Estructura estándar:**

```
Landing Page Structure:
├── Hero Section
│   ├── H1 con keyword principal
│   ├── Propuesta de valor (1-2 líneas)
│   └── CTA primario (formulario above fold)
├── Formulario Captura
│   ├── Campos: nombre, email, teléfono, empresa, mensaje
│   ├── Integración MW#1 (webhook n8n)
│   └── Botón CTA destacado
├── Beneficios Section
│   ├── 3-4 beneficios con iconos
│   └── Enfoque en resultados
├── Social Proof
│   ├── Testimonios clientes (2-3)
│   ├── Logos empresas atendidas
│   └── Datos cuantitativos (24 años experiencia, etc.)
├── FAQ Section
│   ├── 5-7 preguntas frecuentes
│   └── FAQPage Schema
├── CTA Final
│   └── Formulario repetido o botón WhatsApp
└── Footer Minimal
    └── Links legales + contacto
```

### 4.5 Implementación Técnica

**Estructura Next.js:**
```
app/
├── landing/
│   ├── registro-marca/
│   │   └── page.tsx
│   ├── asesoria-pi/
│   │   └── page.tsx
│   ├── contratacion-estatal/
│   │   └── page.tsx
│   ├── derecho-comercial-startups/
│   │   └── page.tsx
│   └── litigios-pi/
│       └── page.tsx
└── components/
    ├── LandingHero.tsx
    ├── LeadForm.tsx  ← Componente reutilizable (KEY)
    ├── SocialProof.tsx
    └── FAQSection.tsx
```

**Componente LeadForm (Integración MW#1):**

**components/LeadForm.tsx:**
```tsx
'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';

interface LeadFormProps {
  service: string;
  source: string;
  ctaText?: string;
}

export function LeadForm({ service, source, ctaText = "Solicitar Consulta Gratis" }: LeadFormProps) {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    telefono: '',
    empresa: '',
    mensaje: ''
  });

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Enviar a client-service API (que dispara NATS → n8n)
      const response = await fetch('/api/leads', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nombre: formData.nombre,
          email: formData.email,
          telefono: formData.telefono,
          empresa: formData.empresa,
          servicio: service,      // Ej: "derecho-marcas"
          mensaje: formData.mensaje,
          source: source,         // Ej: "landing-registro-marca"
        }),
      });

      if (response.ok) {
        // Redirigir a página de gracias
        router.push(`/gracias?service=${service}`);
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Error al enviar. Intenta nuevamente.');
      }
    } catch (err) {
      console.error('Error enviando lead:', err);
      setError('Error de conexión. Verifica tu internet.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 bg-white p-6 rounded-lg shadow-lg">
      <div>
        <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">
          Nombre completo *
        </label>
        <input
          type="text"
          id="nombre"
          name="nombre"
          required
          value={formData.nombre}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      <div>
        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
          Email corporativo *
        </label>
        <input
          type="email"
          id="email"
          name="email"
          required
          value={formData.email}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      <div>
        <label htmlFor="telefono" className="block text-sm font-medium text-gray-700">
          Teléfono
        </label>
        <input
          type="tel"
          id="telefono"
          name="telefono"
          value={formData.telefono}
          onChange={handleChange}
          placeholder="+57 300 123 4567"
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      <div>
        <label htmlFor="empresa" className="block text-sm font-medium text-gray-700">
          Empresa
        </label>
        <input
          type="text"
          id="empresa"
          name="empresa"
          value={formData.empresa}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      <div>
        <label htmlFor="mensaje" className="block text-sm font-medium text-gray-700">
          ¿En qué podemos ayudarte? *
        </label>
        <textarea
          id="mensaje"
          name="mensaje"
          required
          rows={4}
          value={formData.mensaje}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      {error && (
        <div className="bg-red-50 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-3 px-6 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
      >
        {loading ? 'Enviando...' : ctaText}
      </button>

      <p className="text-xs text-gray-500 text-center">
        Al enviar aceptas nuestra política de privacidad. Respuesta en menos de 1 hora.
      </p>
    </form>
  );
}
```

**Uso en landing page:**

**app/landing/registro-marca/page.tsx:**
```tsx
import { LeadForm } from '@/components/LeadForm';
import { LegalServiceSchema, FAQSchema } from '@/components/SchemaMarkup';

export const metadata = {
  title: 'Registro de Marca en Colombia 2026 | Carrillo Abogados',
  description: 'Registra tu marca en Colombia con expertos en PI. 24 años de experiencia, proceso completo ante la SIC. Consulta gratis.',
};

export default function RegistroMarcaPage() {
  return (
    <>
      {/* Schema Markup */}
      <LegalServiceSchema
        serviceName="Registro de Marca en Colombia"
        description="Servicio completo de registro de marcas ante la SIC..."
        price="Desde 1500000"
      />
      <FAQSchema faqs={faqs} />

      {/* Hero Section */}
      <section className="bg-gradient-to-br from-blue-600 to-blue-800 text-white py-20">
        <div className="container mx-auto px-4">
          <div className="grid md:grid-cols-2 gap-8 items-center">
            <div>
              <h1 className="text-4xl md:text-5xl font-bold mb-4">
                Registra tu Marca en Colombia con Expertos en Propiedad Intelectual
              </h1>
              <p className="text-xl mb-6">
                24 años de experiencia. Dr. Omar Carrillo, 15 años en la SIC. Proceso completo ante la Superintendencia de Industria y Comercio.
              </p>
              <ul className="space-y-2 mb-6">
                <li>✓ Búsqueda de anterioridades incluida</li>
                <li>✓ Respuesta en menos de 1 hora</li>
                <li>✓ Acompañamiento hasta la aprobación</li>
              </ul>
            </div>
            <div>
              <LeadForm
                service="derecho-marcas"
                source="landing-registro-marca"
                ctaText="Solicitar Consulta Gratis"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Beneficios Section */}
      <section className="py-16 bg-gray-50">
        {/* ... contenido de beneficios ... */}
      </section>

      {/* Social Proof */}
      <section className="py-16">
        {/* ... testimonios ... */}
      </section>

      {/* FAQ Section */}
      <section className="py-16 bg-gray-50">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">
            Preguntas Frecuentes sobre Registro de Marcas
          </h2>
          <div className="max-w-3xl mx-auto space-y-6">
            {faqs.map((faq, index) => (
              <div key={index} className="bg-white p-6 rounded-lg shadow">
                <h3 className="text-lg font-semibold mb-2">{faq.question}</h3>
                <p className="text-gray-700">{faq.answer}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Final */}
      <section className="py-16 bg-blue-600 text-white">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold mb-4">
            Protege tu Marca Hoy Mismo
          </h2>
          <p className="text-xl mb-8">
            Consulta gratis con experto en menos de 1 hora
          </p>
          <div className="max-w-md mx-auto">
            <LeadForm
              service="derecho-marcas"
              source="landing-registro-marca-cta-final"
              ctaText="Comenzar Ahora"
            />
          </div>
        </div>
      </section>
    </>
  );
}

const faqs = [
  {
    question: "¿Cuánto cuesta registrar una marca en Colombia?",
    answer: "El costo total incluye la tasa de la SIC (aproximadamente $1,200,000 COP) más los honorarios legales. En Carrillo Abogados ofrecemos paquetes desde $1,500,000 COP que incluyen búsqueda de anterioridades, presentación y seguimiento completo."
  },
  // ... más FAQs
];
```

### 4.6 Schema Markup Landing Pages

**Service Schema (ya visto arriba):**

Cada landing page incluye:
- `LegalServiceSchema` con servicio específico
- `FAQSchema` con preguntas frecuentes
- `OrganizationSchema` (heredado de _app.tsx)

### 4.7 Testing y Optimización

**Checklist por landing page:**

- [ ] **Title tag:** 50-60 caracteres, incluye keyword
- [ ] **Meta description:** 150-160 chars, incluye keyword, tiene CTA
- [ ] **URL:** Sin tildes, sin mayúsculas, incluye keyword
- [ ] **H1:** Solo 1, incluye keyword
- [ ] **Imágenes:** Alt text con keyword, comprimidas (< 200KB)
- [ ] **Internal links:** Mínimo 2-3 a otros servicios/blog
- [ ] **External links:** 1-2 a fuentes (SIC, OMPI)
- [ ] **CTA:** Visible above the fold + al final
- [ ] **FAQ Schema:** Implementado y validado
- [ ] **Service Schema:** Implementado y validado
- [ ] **Formulario integrado:** POST a /api/leads funciona
- [ ] **Mobile responsive:** Probado en 3 dispositivos
- [ ] **Page Speed:** > 85 mobile y desktop

**Total Landing Pages:** 10 horas (2h por landing page × 5)

---

## 5. TRACK 5: FORMULARIOS LEAD MAGNETS (FASE 3)

### 5.1 Contexto

**Basado en:** ROADMAP - FASE 3 (S5-S9) - Tarea 3.1

Lead magnets son recursos descargables (PDFs, templates) que capturan leads mediante un formulario simple.

### 5.2 Lead Magnets a Implementar

| # | Lead Magnet | Tipo | Keyword Target | Deadline |
|---|-------------|------|----------------|----------|
| 1 | Guía: Registro de Marca Colombia 2026 | PDF descargable | "guía registro marca colombia" | S6 - 27 Feb |
| 2 | Checklist: Protección PI Startups | PDF descargable | "checklist protección pi" | S7 - 6 Mar |
| 3 | Template: Contrato NDA | DOCX descargable | "contrato confidencialidad colombia" | S7 - 6 Mar |
| 4 | Calculadora: Costos Registro Marca | Herramienta web | "costo registrar marca colombia" | S8 - 13 Mar |
| 5 | eBook: Errores Contratación Estatal | PDF descargable | "errores licitaciones públicas" | S8 - 13 Mar |

### 5.3 Flujo Lead Magnet

```
1. Usuario llega a landing page lead magnet
2. Completa formulario corto (nombre, email)
3. Formulario envía a client-service API
4. client-service crea lead + dispara NATS
5. NATS → n8n → MW#1 procesa lead
6. n8n envía email automático con link descarga
7. Usuario descarga lead magnet
8. MW#1 activa nurturing (12 emails)
```

### 5.4 Implementación Técnica

**Estructura de páginas:**
```
app/
└── recursos/
    ├── guia-registro-marca/
    │   └── page.tsx
    ├── checklist-pi-startups/
    │   └── page.tsx
    ├── template-nda/
    │   └── page.tsx
    ├── calculadora-costos/
    │   └── page.tsx
    └── ebook-errores-contratacion/
        └── page.tsx
```

**Formulario simplificado (solo nombre + email):**

**components/LeadMagnetForm.tsx:**
```tsx
'use client';

import { useState, FormEvent } from 'react';

interface LeadMagnetFormProps {
  leadMagnetId: string;
  leadMagnetName: string;
  ctaText?: string;
}

export function LeadMagnetForm({
  leadMagnetId,
  leadMagnetName,
  ctaText = "Descargar Gratis"
}: LeadMagnetFormProps) {
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    nombre: '',
    email: ''
  });

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Enviar a client-service API
      const response = await fetch('/api/leads', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          nombre: formData.nombre,
          email: formData.email,
          telefono: '',
          empresa: '',
          servicio: 'lead-magnet',  // Categoría especial
          mensaje: `Solicita descarga: ${leadMagnetName}`,
          source: leadMagnetId,  // Ej: "lm-guia-registro-marca"
        }),
      });

      if (response.ok) {
        setSubmitted(true);
        // n8n enviará email con link de descarga automáticamente
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Error al enviar. Intenta nuevamente.');
      }
    } catch (err) {
      console.error('Error enviando lead:', err);
      setError('Error de conexión. Verifica tu internet.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  if (submitted) {
    return (
      <div className="bg-green-50 border border-green-400 text-green-700 px-6 py-8 rounded-lg text-center">
        <h3 className="text-xl font-bold mb-2">¡Solicitud Recibida!</h3>
        <p className="mb-4">
          Te enviamos un email a <strong>{formData.email}</strong> con el link de descarga.
        </p>
        <p className="text-sm text-gray-600">
          Revisa tu bandeja de entrada (y spam, por si acaso).
        </p>
      </div>
    );
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4 bg-white p-6 rounded-lg shadow-lg">
      <div>
        <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">
          Nombre completo *
        </label>
        <input
          type="text"
          id="nombre"
          name="nombre"
          required
          value={formData.nombre}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      <div>
        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
          Email *
        </label>
        <input
          type="email"
          id="email"
          name="email"
          required
          value={formData.email}
          onChange={handleChange}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500"
        />
      </div>

      {error && (
        <div className="bg-red-50 border border-red-400 text-red-700 px-4 py-3 rounded text-sm">
          {error}
        </div>
      )}

      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-600 text-white py-3 px-6 rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors font-semibold"
      >
        {loading ? 'Enviando...' : ctaText}
      </button>

      <p className="text-xs text-gray-500 text-center">
        Al descargar aceptas recibir emails con contenido sobre Propiedad Intelectual. Sin spam.
      </p>
    </form>
  );
}
```

**Uso en página lead magnet:**

**app/recursos/guia-registro-marca/page.tsx:**
```tsx
import { LeadMagnetForm } from '@/components/LeadMagnetForm';

export const metadata = {
  title: 'Guía Gratuita: Cómo Registrar una Marca en Colombia 2026 | Carrillo Abogados',
  description: 'Descarga gratis la guía completa para registrar tu marca en Colombia. Requisitos, costos, proceso paso a paso ante la SIC.',
};

export default function GuiaRegistroMarcaPage() {
  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="container mx-auto px-4">
        <div className="max-w-4xl mx-auto">
          {/* Hero */}
          <div className="text-center mb-12">
            <h1 className="text-4xl font-bold mb-4">
              Guía Completa: Cómo Registrar una Marca en Colombia
            </h1>
            <p className="text-xl text-gray-600">
              Descarga gratis la guía definitiva con todo lo que necesitas saber para proteger tu marca en 2026.
            </p>
          </div>

          <div className="grid md:grid-cols-2 gap-8">
            {/* Beneficios */}
            <div className="bg-white p-6 rounded-lg shadow">
              <h2 className="text-2xl font-bold mb-4">¿Qué incluye la guía?</h2>
              <ul className="space-y-3">
                <li className="flex items-start">
                  <svg className="w-6 h-6 text-green-500 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span>Requisitos actualizados 2026 para registro ante la SIC</span>
                </li>
                <li className="flex items-start">
                  <svg className="w-6 h-6 text-green-500 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span>Costos detallados (tasas SIC + honorarios)</span>
                </li>
                <li className="flex items-start">
                  <svg className="w-6 h-6 text-green-500 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span>Proceso completo paso a paso (timeline real: 6-12 meses)</span>
                </li>
                <li className="flex items-start">
                  <svg className="w-6 h-6 text-green-500 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span>7 errores comunes que causan rechazo (y cómo evitarlos)</span>
                </li>
                <li className="flex items-start">
                  <svg className="w-6 h-6 text-green-500 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span>Checklist descargable para no olvidar nada</span>
                </li>
              </ul>
            </div>

            {/* Formulario */}
            <div>
              <LeadMagnetForm
                leadMagnetId="lm-guia-registro-marca"
                leadMagnetName="Guía: Registro de Marca Colombia 2026"
                ctaText="Descargar Guía Gratis"
              />
            </div>
          </div>

          {/* Disclaimer */}
          <div className="mt-12 text-center text-sm text-gray-600">
            <p>
              Creado por <strong>Carrillo Abogados</strong> - 24 años de experiencia en Propiedad Intelectual.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
```

**Storage de lead magnets:**

**Opción A: Google Drive (público):**
```
https://drive.google.com/file/d/FILE_ID/view?usp=sharing
```

**Opción B: Almacenamiento propio:**
```
public/
└── downloads/
    ├── guia-registro-marca-colombia-2026.pdf
    ├── checklist-proteccion-pi-startups.pdf
    ├── template-nda-colombia.docx
    └── ebook-errores-contratacion-estatal.pdf
```

**n8n envía email con link:**

En SUB-A Lead Intake, al detectar `servicio === "lead-magnet"`:

```javascript
// Nodo Gmail en n8n
const leadMagnetId = items[0].json.source;  // "lm-guia-registro-marca"

const downloadLinks = {
  "lm-guia-registro-marca": "https://www.carrilloabgd.com/downloads/guia-registro-marca-colombia-2026.pdf",
  "lm-checklist-pi": "https://www.carrilloabgd.com/downloads/checklist-proteccion-pi-startups.pdf",
  // ...
};

const emailBody = `
Hola ${items[0].json.nombre},

Gracias por tu interés en nuestra guía.

Descarga aquí: ${downloadLinks[leadMagnetId]}

Saludos,
Equipo Carrillo Abogados
`;
```

**Total Lead Magnets:** 5 horas (1h por lead magnet × 5)

---

## 6. COORDINACIÓN CON MARKETING

### 6.1 Puntos de Sincronización

**Semanal:**
- **Lunes 9am (15 min):** Prioridades de la semana
- **Viernes 4pm (30 min):** Checkpoint de progreso

**Críticos:**
- **Fin FASE 1 (S3):** Demo auditoría técnica
- **Fin FASE 2 (S6):** Demo SEO técnico + blog-service
- **Fin FASE 3 (S9):** Demo landing pages

### 6.2 Entregables de Marketing para Alexis

**Marketing debe proveer ANTES de que Alexis implemente:**

1. **Wireframes** de landing pages (Figma/Canva)
2. **Copywriting** completo (H1, CTAs, FAQs)
3. **Instrucciones técnicas SEO** (keywords, meta descriptions, schema)
4. **Assets visuales** (imágenes, iconos, banners)

**Alexis implementa** basado en esas especificaciones.

### 6.3 Handoff Process

**Para cada landing page:**

```
1. Marketing crea ticket en Notion/Trello:
   - Nombre: "LP: Registro de Marca"
   - Deadline: S7 - 6 Marzo
   - Adjuntos: Wireframe (Figma link), Copywriting (Google Doc)
   - Metadata SEO: title, description, keywords

2. Alexis implementa en Next.js

3. Alexis deploy a staging:
   - URL: https://staging.carrilloabgd.com/landing/registro-marca

4. Marketing revisa:
   - Content match wireframe
   - SEO metadata correcto
   - Formulario funcional

5. Alexis corrige feedback

6. Marketing aprueba

7. Alexis deploy a producción
```

---

## 7. TESTING Y VALIDACIÓN

### 7.1 Checklist Pre-Launch (FASE 4 - S9-S11)

**SEO Técnico:**
- [ ] PageSpeed score > 85 (mobile + desktop)
- [ ] Core Web Vitals aprobados (LCP < 2.5s, FID < 100ms, CLS < 0.1)
- [ ] 0 errores críticos en Search Console
- [ ] Schema markup validado (Rich Results Test)
- [ ] Sitemap XML enviado
- [ ] Robots.txt configurado
- [ ] HTTPS funcionando en todas las URLs
- [ ] Mobile-first indexing ready

**Integración MW#1:**
- [ ] Formulario web integrado con webhook n8n
- [ ] Test E2E: Lead → PostgreSQL → NATS → n8n → Callback → PostgreSQL actualizado
- [ ] Score IA actualizado en BD
- [ ] Email HOT lead enviado (si score >= 70)

**Integración MW#3 (si blog-service implementado):**
- [ ] blog-service API funcional
- [ ] SUB-M puede publicar artículo en draft
- [ ] Schema Article implementado
- [ ] URLs SEO-friendly funcionando

**Landing Pages:**
- [ ] 5 landing pages publicadas y accesibles
- [ ] Formularios integrados con MW#1
- [ ] Service Schema implementado
- [ ] Mobile responsive verificado en 3 dispositivos

**Lead Magnets:**
- [ ] 5 formularios lead magnets funcionales
- [ ] Links de descarga funcionando
- [ ] Emails automáticos con link descarga (n8n)

### 7.2 Testing Tools

| Categoría | Herramienta | URL |
|-----------|-------------|-----|
| **Page Speed** | PageSpeed Insights | https://pagespeed.web.dev/ |
| **Mobile** | Mobile-Friendly Test | https://search.google.com/test/mobile-friendly |
| **Schema** | Rich Results Test | https://search.google.com/test/rich-results |
| **Sitemap** | XML Sitemaps Validator | https://www.xml-sitemaps.com/validate-xml-sitemap.html |
| **SEO** | SEMrush Site Audit | https://www.semrush.com/siteaudit/ |
| **Accessibility** | WAVE | https://wave.webaim.org/ |

---

## 8. DEPENDENCIAS Y BLOCKERS

### 8.1 Bloqueantes para Alexis

**CRÍTICO:**
- ⏳ **Marketing debe completar investigación SEO** (keywords, estructura) ANTES de landing pages
- ⏳ **Decisión blog-service vs WordPress** debe resolverse en S4-S5
- ⏳ **Wireframes de landing pages** deben estar listos ANTES de implementación

**ALTA PRIORIDAD:**
- ⏳ **SEMrush Pro pagado** (investigación manual, Don Omar)
- ⏳ **DataForSEO aprobado** (API automatización, Don Omar)
- ⏳ **Google Sheets creados** (Keywords_Master, MAES_RawData) por Marketing

### 8.2 Bloqueantes para Marketing

**CRÍTICO:**
- ⏳ **SEO técnico implementado** ANTES de lanzar pauta (S7)
- ⏳ **blog-service API funcional** ANTES de activar SUB-M (S6)

### 8.3 Decisiones Pendientes

| ID | Decisión | Deadline | Responsable | Impacto en Alexis |
|----|----------|----------|-------------|-------------------|
| **DEC-002** | blog-service vs WordPress | S6 (27 Feb) | Don Omar + Alexis | 10h si blog-service, 0h si WordPress |
| **DEC-004** | Estructura URLs nueva | S3 (6 Feb) | Marketing | 2h implementación arquitectura info |

---

## 9. RESUMEN TIEMPOS Y PRIORIZACIÓN

### 9.1 Distribución por Fase

| Fase | Tracks Activos | Horas Alexis | Periodo | Prioridad |
|------|----------------|--------------|---------|-----------|
| **FASE 1 (S1-S3)** | Track 3 (Auditoría) | 4h | 23 Ene - 6 Feb | P0 |
| **FASE 2 (S3-S6)** | Track 1, 2, 3 | 28.5h | 6 Feb - 27 Feb | P0 + P1 |
| **FASE 3 (S5-S9)** | Track 4, 5 | 15h | 20 Feb - 20 Mar | P1 + P2 |
| **TOTAL** | - | **47.5h** | 12 semanas | - |

### 9.2 Tabla Consolidada

| Track | Descripción | Horas | Fase Roadmap | Prioridad |
|-------|-------------|-------|--------------|-----------|
| **Track 1** | Integración MW#1 (NATS + webhooks) | 7h | S3-S6 | P0 - CRÍTICO |
| **Track 2** | Integración MW#3 (blog-service) | 10h | S3-S6 | P1 - ALTA |
| **Track 3** | SEO Técnico | 11.5h | S3-S6 | P0 - CRÍTICO |
| **Track 4** | Landing Pages (5 LPs) | 10h | S5-S9 | P1 - ALTA |
| **Track 5** | Lead Magnets (5 formularios) | 5h | S5-S9 | P2 - MEDIA |
| **TOTAL** | - | **43.5h** | - | - |

**Nota:** Track 3 incluye 4h de auditoría (FASE 1).

### 9.3 Carga Semanal Estimada

| Semana | Fase | Tracks Activos | Horas Estimadas |
|--------|------|----------------|-----------------|
| S2 (23-30 Ene) | FASE 1 | Track 3 (Auditoría) | 4h |
| S3 (30 Ene - 6 Feb) | FASE 2 | Track 1, 3 | 6h |
| S4 (6-13 Feb) | FASE 2 | Track 1, 2, 3 | 8h |
| S5 (13-20 Feb) | FASE 2 | Track 2, 3 | 6h |
| S6 (20-27 Feb) | FASE 2 | Track 2 | 4h |
| S7 (27 Feb - 6 Mar) | FASE 3 | Track 4, 5 | 5h |
| S8 (6-13 Mar) | FASE 3 | Track 4, 5 | 6h |
| S9 (13-20 Mar) | FASE 3 | Track 4 | 4h |

**Pico de carga:** S4 (8h) - **Coordinación requerida con Don Omar para priorización MVP vs. automation.**

---

## 10. DOCUMENTOS DE REFERENCIA

| Documento | Ubicación | Propósito |
|-----------|-----------|-----------|
| **BACKEND_DEV_TASKS.md** | [`automation/docs/technical/BACKEND_DEV_TASKS.md`](BACKEND_DEV_TASKS.md) | Detalle Track 1 (MW#1 Integration) |
| **ROADMAP_MARKETING_2026_v2.md** | [`automation/docs/ROADMAP_MARKETING_2026_v2.md`](../ROADMAP_MARKETING_2026_v2.md) | Timeline maestro 12 semanas |
| **03_MEGA_WORKFLOW_3_SEO.md** | [`automation/docs/technical/arquitectura/03_MEGA_WORKFLOW_3_SEO.md`](arquitectura/03_MEGA_WORKFLOW_3_SEO.md) | Arquitectura MW#3 (blog-service context) |
| **PROJECT_STATUS.md** | [`automation/PROJECT_STATUS.md`](../../PROJECT_STATUS.md) | Estado general automation |
| **MAES_INTEGRATION.md** | [`automation/docs/business/MAES_INTEGRATION.md`](../business/MAES_INTEGRATION.md) | Integración estratégica MW#3 |

---

## 11. CHANGELOG

| Versión | Fecha | Cambios |
|---------|-------|---------|
| **v1.0** | 2026-01-30 | Creación inicial - Consolidación todas las tareas web development automation |

---

## PRÓXIMOS PASOS (INMEDIATOS)

### Para Alexis (Esta Semana - S2)

1. **Leer documentos de referencia:**
   - [ ] `BACKEND_DEV_TASKS.md` (detalle Track 1)
   - [ ] `ROADMAP_MARKETING_2026_v2.md` (contexto timeline)

2. **Ejecutar auditoría técnica (Track 3 - FASE 1):**
   - [ ] Auditoría SEO Técnico (2h)
   - [ ] Core Web Vitals Check (1h)
   - [ ] Análisis Arquitectura Info (1h)
   - [ ] Entregar reporte a Marketing

3. **Coordinación con Don Omar:**
   - [ ] Validar viabilidad blog-service (10h) vs. carga MVP
   - [ ] Priorizar S4 (pico 8h) - ¿MVP o automation?

### Para Marketing (Esta Semana - S2)

1. **Crear Google Sheets** (BLOQUEANTE para MW#3)
2. **Pagar SEMrush Pro** (investigación manual)
3. **Preparar instrucciones SEO** basadas en reporte auditoría Alexis

### Para Don Omar (Esta Semana - S2)

1. **Decidir blog-service vs WordPress** (impacta 10h Alexis)
2. **Aprobar DataForSEO budget** ($50-100 USD)
3. **Coordinar prioridades** Alexis (MVP vs. automation en S4-S6)

---

**FIN DEL DOCUMENTO**

**Contacto para dudas:**
- Marketing: Juan Jose (marketing@carrilloabgd.com)
- Backend: Alexis
- Decisiones: Don Omar
