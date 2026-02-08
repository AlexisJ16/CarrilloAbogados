# MAES Integration with MW#3

**Document Type:** Business Context - Strategic Integration
**Last Updated:** 2026-02-07
**Status:** Architecture v5.0 - SUB-K Bidirectional - Sheets CREATED
**Owner:** Juan Jose (Marketing Director)

---

## Executive Summary

This document defines how the **MAES (Metodología Ágil de Estrategia SEO)** framework integrates with **MW#3 (SEO Content Factory)** to create a data-driven, strategically aligned content production system.

**Key Innovation:** SUB-K operates in **dual mode** (Investigation + Production) to maximize ROI of DataForSEO API and ensure content aligns with business strategy, not just SEO metrics.

---

## 1. What is MAES?

**MAES (Metodología Ágil de Estrategia SEO)** is an 8-phase strategic framework executed in Notion by Juan + Claude AI to define:
- WHAT content to create
- WHY it matters for the business
- HOW it aligns with "Motor Futuro" (target audience: tech SMBs without IP protection)

### 1.1 MAES: 8 Phases Overview

| Phase | Name | Duration | Purpose | Output |
|-------|------|----------|---------|--------|
| 1 | Kick-off | 2h | Understand business DNA, priorities, resources | Aligned objectives |
| 2 | SEO Strategy | 2h | Define SEO type, vision, prioritization | Strategic plan |
| 3 | Goals + Audience | 2h | SMART goals + detailed buyer personas | Measurable targets + profiles |
| 4 | Competition Analysis | 3h | Gap analysis, competitor metrics, opportunities | Keyword gaps, quick wins |
| 5 | Current Positioning | 2h | Site audit, strengths, low-hanging fruit | Optimization list |
| 6 | Strategic Planning | 3h | URL architecture, thematic clusters, roadmap | URL structure + clusters |
| 7 | Content Planning | 1h | Editorial calendar, briefs | Content priorities |
| 8 | SEO Verification | 1h | On-page checklist, copywriter guidelines | Quality standards |

**Total Time:** ~15 hours (one-time setup)
**Tool:** Notion Database
**Frequency:** Initial setup + quarterly reviews

---

## 2. How MAES Integrates with MW#3

### 2.1 Separation of Responsibilities

| System | Role | Frequency | Owner | Output |
|--------|------|-----------|-------|--------|
| **MAES** | Strategic decisions (WHAT & WHY) | One-time + quarterly | Human (Juan) + AI (Claude) | Keywords_Master Sheet |
| **MW#3** | Tactical execution (HOW) | Continuous (weekly) | Automation (n8n) | Published content |

**The "Contract":** `Keywords_Master` Google Sheet bridges MAES (strategy) and MW#3 (execution).

### 2.2 Complete Bidirectional Cycle

```
┌────────────────────────────────────────────────────────────────┐
│                    BIDIRECTIONAL CYCLE                         │
└────────────────────────────────────────────────────────────────┘

PHASE 1: STRATEGIC SETUP (Once, ~15 hours)
═══════════════════════════════════════════════════════════════════

[1. MAES Phases 1-3] → Notion
    • Kick-off, Strategy, Goals + Audience
    • Define "Motor Futuro" target
          ↓
[2. SUB-K INVESTIGATION] → Ad-hoc execution
    • Input: Seed keywords + competitors
    • DataForSEO API → MAES_Research Sheet (200+ keywords)
          ↓
[3. MAES Phases 4-6] → Notion (enriched with API data)
    • Phase 4: Competition Analysis (uses MAES_Research)
    • Phase 5: Current Positioning
    • Phase 6: Strategic Planning
    • Define clusters, priorities, briefs
          ↓
[4. Keywords_Master Creation] → Google Sheet
    • Juan consolidates validated keywords (50-100)
    • Assigns clusters, briefs, target URLs
    • Marks enabled=TRUE (approved) / FALSE (rejected)
          ↓
[5. Don Omar Validation] → Approval
    • Present MAES strategy
    • Approve Keywords_Master
          ↓

PHASE 2: CONTINUOUS EXECUTION (Weekly, automated)
═══════════════════════════════════════════════════════════════════

[MONDAY 8:00 AM - Automated]
    SUB-K PRODUCTION
    • Reads Keywords_Master (enabled=TRUE, status=pending)
    • Selects 2-3 priority keywords
    • Syncs to Firestore keywords_pipeline
    • Triggers SUB-L for each keyword
          ↓
[MONDAY-TUESDAY - Automated]
    SUB-L Content Writer
    • Reads keyword + MAES brief
    • Gemini generates article (guided by brief)
    • Creates Google Doc in MW3_Drafts/
    • Notifies Juan
          ↓
[WEDNESDAY-THURSDAY - Manual]
    HUMAN REVIEW (Juan)
    • Edits in Google Docs (WYSIWYG)
    • Approves: Marks status=aprobado in Keywords_Master
          ↓
[FRIDAY - Automated]
    SUB-M Publisher (FUTURE)
    • Detects status=aprobado
    • Publishes to blog-service via API
    • Updates Keywords_Master: status=publicado
          ↓
[DAILY - Automated]
    SUB-N Performance Tracker (FUTURE)
    • Queries Google Search Console
    • Updates content_performance in Firestore
    • Updates Keywords_Master with metrics
          ↓

PHASE 3: STRATEGIC FEEDBACK (Quarterly, ~3 hours)
═══════════════════════════════════════════════════════════════════

[EVERY 3 MONTHS]
    1. SUB-N generates consolidated report
       • Top performers, keywords in Top 10, leads generated
          ↓
    2. Juan + Claude AI review in MAES (Notion)
       • Update cluster priorities
       • Identify new opportunities
       • Deprecate what doesn't work
          ↓
    3. Juan updates Keywords_Master Sheet
       • Add new keywords (enabled=TRUE)
       • Deprecate keywords (enabled=FALSE)
       • Refine briefs based on performance
          ↓
    4. (Optional) Execute SUB-K INVESTIGATION
       • Refresh competitor data
       • New opportunities with DataForSEO
          ↓
    5. Don Omar validation
       • Present strategy adjustments
          ↓
    6. MW#3 continues with updated strategy
```

---

## 3. SUB-K: Dual Purpose Architecture

### 3.1 Why Dual Purpose?

**Problem identified:**
- Original design: SUB-K only fed SUB-L (unidirectional)
- DataForSEO API investment ($50-100 USD) underutilized
- MAES required competitor/gap data but was doing manual research

**Solution:**
SUB-K operates in **TWO MODES** to maximize API ROI and ensure data-driven strategy.

### 3.2 Mode Comparison

| Aspect | MODE 1: INVESTIGATION | MODE 2: PRODUCTION |
|--------|----------------------|-------------------|
| **Consumer** | MAES (Notion) | SUB-L (n8n) |
| **Purpose** | Enrich strategic analysis | Execute defined strategy |
| **Trigger** | Manual (ad-hoc) | Automated (weekly schedule) |
| **Input** | Seed keywords + competitors | Keywords_Master (enabled=TRUE) |
| **Output** | MAES_Research Sheet (200+ keywords raw) | Firestore + trigger SUB-L |
| **Filters** | Minimal (broad exploration: vol >= 50, KD <= 50) | Strict (MAES-validated only) |
| **Frequency** | During MAES + quarterly reviews | Continuous (weekly) |
| **Human time** | 2-3h analysis | 0 (automated) |

### 3.3 Complete Flow Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                  KEYWORDS FLOW (Bidirectional)                │
└──────────────────────────────────────────────────────────────┘

    ┌────────────────┐
    │  DataForSEO    │
    │  API           │
    └────────┬───────┘
             │
             │ SUB-K Investigation (ad-hoc)
             │
             ▼
    ┌────────────────────┐
    │  MAES_Research      │  ← Human analysis
    │  (Google Sheets)   │     (Phases 4-6)
    └────────┬───────────┘
             │
             │ MAES Phases 4-6 (Notion)
             │ Juan + Claude AI decisions
             │
             ▼
    ┌─────────────────────────────┐
    │  Keywords_Master (Sheets)   │ ← Source of Truth
    │  enabled = TRUE/FALSE       │    (Juan controls)
    └──────┬──────────────┬───────┘
           │              │
           │              │ Performance feedback
           │              │ (SUB-N quarterly)
           │              │
           │              ▼
           │      ┌───────────────────┐
           │      │  MAES Review      │
           │      │  (Notion)         │
           │      │  Adjust strategy  │
           │      └───────────────────┘
           │
           │ enabled=TRUE keywords only
           │ SUB-K Production (weekly)
           │
           ▼
    ┌────────────────────┐
    │  Firestore         │ ← Operational cache
    │  keywords_pipeline │    (NOT authoritative)
    └────────┬───────────┘
             │
             │ SUB-L reads
             │
             ▼
    ┌────────────────────┐
    │  Content           │
    │  Published         │
    │  (aligned with     │
    │  MAES strategy)    │
    └────────────────────┘
```

---

## 4. Keywords_Master: The Contract

### 4.1 Purpose

`Keywords_Master` Google Sheet is the **single source of truth** that bridges:
- **Strategic layer** (MAES decisions in Notion)
- **Operational layer** (MW#3 automation in n8n)

### 4.2 Structure (21 Columns - Updated 7 Feb 2026)

| Col | Field | Type | Purpose | Edited By |
|-----|-------|------|---------|-----------|
| A | `keyword_id` | String | Unique ID | System |
| B | `cluster_name` | String | Thematic cluster | Juan (MAES) |
| C | `keyword_text` | String | Specific keyword | Juan / DataForSEO |
| D | `tipo` | Enum | Head / Long-tail | Juan (MAES) |
| E | `intencion` | Enum | Informational / Transactional / Commercial / Navigational | Juan (MAES) |
| F | `volume` | Integer | Monthly searches | DataForSEO |
| G | `kd` | Integer | Keyword Difficulty (0-100) | DataForSEO |
| H | `cpc` | Float | CPC in USD | DataForSEO |
| **I** | **`enabled`** | **Boolean** | **TRUE/FALSE - HUMAN CONTROL** | **Juan** |
| J | `priority_score` | Formula | Auto-calculated | Calculated |
| K | `status` | Enum | pendiente / en_progreso / publicado | System |
| L | `url_target` | String | Target URL | Juan (MAES) |
| M | `brief_notes` | Text | Brief notes | Juan (MAES) |
| N | `content_id` | String | Draft ID | SUB-L |
| O | `fecha_creacion` | Date | Creation date | System |
| P | `fecha_publicacion` | Date | Publication date | SUB-M |
| Q | `trend` | String | Search trend | DataForSEO |
| R | `serp_features` | String | SERP features present | DataForSEO |
| S | `competitor_rank` | Integer | Best competitor position | DataForSEO |
| T | `content_type` | Enum | blog/landing/faq/guide | Juan (MAES) |
| U | `notes` | Text | Additional notes | Juan |

> **Note:** Columns A-P match original spec. Q-U added during Sheet creation (7 Feb 2026) for richer data capture.

### 4.3 Critical Column: `enabled` (Column I)

**Purpose:** Human control over which keywords MW#3 processes.

- **enabled=TRUE:** Keyword approved by MAES, syncs to Firestore, SUB-L will process
- **enabled=FALSE:** Keyword rejected, does NOT sync, remains in Sheet for reference

**Why this matters:**
- Prevents automation from creating content for wrong keywords
- Documents strategic decisions (WHY a keyword was rejected)
- Easy rollback (change FALSE to TRUE)
- Transparent workflow

### 4.4 Tabs Structure (6 tabs - Created 7 Feb 2026)

| Tab | Purpose | Maintained By |
|-----|---------|---------------|
| **All_Keywords** | Flat list of all keywords (21 cols) | System + Juan |
| **By_Cluster** | Keywords grouped by thematic cluster | Calculated |
| **Pipeline** | Content production pipeline tracking | System |
| **Briefs** | Detailed instructions per cluster | Juan (MAES Phase 7) |
| **Dashboard** | Executive summary view | Calculated |
| **Config** | Dropdowns and validation lists | System |

---

## 5. MAES Phases that Use MW#3 Data

### 5.1 Phase 4: Competition Analysis

**How SUB-K Investigation helps:**
- DataForSEO provides competitor keywords they rank for
- Identifies **keyword gaps** (competitors have, we don't)
- SERP features analysis (snippets, PAA, etc.)
- Volume + KD + CPC data for prioritization

**MAES Output:** List of opportunities based on gaps and quick wins.

### 5.2 Phase 5: Current Positioning

**How SUB-K Investigation helps:**
- Baseline metrics for our current rankings (if any)
- Identify low-hanging fruit (keywords where we're on page 2)

**MAES Output:** Optimization list for existing content.

### 5.3 Phase 6: Strategic Planning

**How SUB-K Investigation helps:**
- Data-driven cluster definition (group related keywords)
- Priority scoring based on volume, KD, commercial intent

**MAES Output:**
- URL architecture
- Thematic clusters
- Roadmap (which clusters to attack first)

---

## 6. Timeline and Resource Allocation

### 6.1 Initial Setup

| Activity | Duration | Owner | Deliverable |
|----------|----------|-------|-------------|
| MAES Phases 1-3 | 6 hours | Juan + Claude AI | Strategy foundation |
| SUB-K Investigation execution | 1 hour | n8n (automated) | MAES_Research Sheet |
| MAES Phases 4-6 (with API data) | 7 hours | Juan + Claude AI | Clusters + priorities |
| Keywords_Master creation | 2 hours | Juan | Validated keyword list |
| Don Omar validation | 1 hour | Don Omar | Approval |
| **TOTAL** | **~15-17 hours** | | MAES complete + MW#3 ready |

### 6.2 Ongoing Operations

| Activity | Frequency | Duration | Owner |
|----------|-----------|----------|-------|
| SUB-K Production | Weekly (automated) | 0 hours | n8n |
| SUB-L Content generation | Weekly (automated) | 0 hours | n8n |
| Content review & approval | Weekly | 4 hours/month | Juan |
| MAES quarterly review | Every 3 months | 3 hours | Juan + Claude AI |
| Keywords_Master updates | After each review | 1 hour | Juan |

---

## 7. Success Metrics

### 7.1 MAES Quality Indicators

| Indicator | Target | Measurement |
|-----------|--------|-------------|
| Strategic alignment | 100% | All keywords in Keywords_Master have documented "why" |
| Cluster coherence | >= 80% | Keywords grouped logically by user intent |
| Priority accuracy | >= 70% | High-priority keywords generate leads |
| Execution fidelity | >= 90% | Published content matches MAES briefs |

### 7.2 MW#3 Performance Indicators

| Indicator | Target Month 3 | Target Month 12 |
|-----------|----------------|-----------------|
| Articles published | 12 | 50+ |
| Keywords in Top 10 | 5 | 25+ |
| Organic traffic/month | 500 | 3,000+ |
| Leads from SEO/month | 10 | 50+ |
| Time to publish (after approval) | < 24h | < 1h (fully automated) |

### 7.3 Integration Health Metrics

| Metric | Target | Risk If Below |
|--------|--------|---------------|
| Keywords_Master accuracy | >= 95% | Content misaligned with strategy |
| enabled=TRUE adoption | >= 60% | Too much rejection = bad filtering |
| Brief completeness | 100% | AI generates generic content |
| Quarterly review completion | 4/year | Strategy becomes stale |

---

## 8. Risks and Mitigations

### 8.1 Strategic Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| MAES not completed | Medium | Critical | Allocate dedicated time, use Claude AI to accelerate |
| Strategy not followed by MW#3 | Medium | High | enabled column + SUB-K only processes enabled=TRUE |
| Keyword selection bias | Low | Medium | DataForSEO API provides objective data |
| Don Omar doesn't approve | Low | Critical | Involve early with weekly checkpoints |

### 8.2 Operational Risks

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Keywords_Master becomes stale | Medium | High | Quarterly reviews mandatory |
| enabled column misused | Low | Medium | Clear documentation + training |
| SUB-K Investigation fails | Low | Medium | Error handling + manual fallback |
| Performance data not fed back to MAES | Medium | Medium | SUB-N quarterly report automatic |

---

## 9. Future Research: SEO Programático

> ⚠️ **INVESTIGATION PENDING**

**Context:** SEO Programático (programmatic SEO) is a technique for generating thousands of pages targeting long-tail keywords using templates + databases.

**Question:** Should Carrillo Abogados explore this approach instead of/in addition to the current MAES + MW#3 model?

**Example use case:**
- Template: "Cómo registrar marca de [INDUSTRY] en [CITY]"
- Database: 20 industries x 32 Colombian cities = 640 pages
- Auto-generated content with location/industry-specific data

**Evaluation criteria:**
1. Does it align with "Motor Futuro" (PyMEs tech)?
2. Can it maintain content quality (avoid thin content penalty)?
3. What's the resource requirement vs ROI?
4. How does it integrate with current MAES framework?

**Status:** To be evaluated in Phase 2 of the project (Q2 2026).

---

## 10. Key Takeaways

1. **MAES defines strategy, MW#3 executes.** Clear separation of responsibilities.

2. **SUB-K bidirectional.** DataForSEO API used for both investigation (MAES input) and production (MW#3 execution).

3. **Keywords_Master is the contract.** Single source of truth bridging strategy and automation.

4. **enabled column = human control.** Juan decides what gets automated, preventing misaligned content.

5. **Quarterly feedback loop.** SUB-N performance data adjusts MAES strategy every 3 months.

6. **15 hours initial setup.** One-time MAES investment enables continuous automated execution.

7. **Data-driven decisions.** DataForSEO API ensures decisions based on metrics, not intuition.

---

## 11. Related Documentation

| Document | Location | Purpose |
|----------|----------|---------|
| MW#3 Architecture (v5.0) | `docs/technical/arquitectura/03_MEGA_WORKFLOW_3_SEO.md` | Complete technical specs |
| MW#3 STATUS | `workflows/MW3_SEO_CONTENT_FACTORY/STATUS.md` | Implementation status |
| Keywords_Master Spec | `docs/technical/arquitectura/MW3_GOOGLE_SHEETS_KEYWORDS_MASTER.md` | Google Sheet structure |
| SUB-K v2.0 Spec | (to be created) | Dual mode implementation |
| MAES Template | Notion (private) | 8-phase framework |

---

**Last Updated:** 2026-02-07
**Version:** 1.1
**Next Review:** After MAES Phase 4 completion (estimated S4-S5)
**Owner:** Juan Jose (marketing@carrilloabgd.com)
