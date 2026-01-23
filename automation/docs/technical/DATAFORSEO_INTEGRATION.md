# DataForSEO API Integration for MW#3 (SEO Content Factory)

**Version:** 1.0
**Created:** 2026-01-22
**Status:** READY FOR IMPLEMENTATION
**Related:** [STATUS.md](../../workflows/MW3_SEO_CONTENT_FACTORY/STATUS.md) | [03_MEGA_WORKFLOW_3_SEO.md](arquitectura/03_MEGA_WORKFLOW_3_SEO.md)

---

## TABLE OF CONTENTS

1. [Overview](#1-overview)
2. [Authentication](#2-authentication)
3. [Endpoint 1: Keyword Ideas](#3-endpoint-1-keyword-ideas)
4. [Endpoint 2: Bulk Keyword Difficulty](#4-endpoint-2-bulk-keyword-difficulty)
5. [Endpoint 3: SERP Organic Analysis](#5-endpoint-3-serp-organic-analysis)
6. [n8n HTTP Request Configuration](#6-n8n-http-request-configuration)
7. [Firestore Mapping](#7-firestore-mapping)
8. [Cost Estimation](#8-cost-estimation)
9. [Implementation Checklist](#9-implementation-checklist)

---

## 1. OVERVIEW

### Why DataForSEO (Not SEMrush)

| Aspect | SEMrush Business | DataForSEO |
|--------|------------------|------------|
| API Access | $499.95 USD/month | Pay-as-you-go |
| Monthly Cost | $500+ USD | $20-50 USD estimated |
| n8n Integration | Complex OAuth | Simple HTTP Basic Auth |
| Data Quality | Industry standard | Comparable quality |

**Decision:** Use DataForSEO for n8n automation, SEMrush Pro for manual research.

### Endpoints for SUB-K (Keyword Research)

| Purpose | Endpoint | When to Use |
|---------|----------|-------------|
| Generate keyword ideas | `/keyword_ideas/live` | Initial seed expansion |
| Get difficulty scores | `/bulk_keyword_difficulty/live` | Prioritization |
| Analyze SERP | `/serp/google/organic/live/advanced` | Competition analysis |

---

## 2. AUTHENTICATION

### Method: HTTP Basic Authentication

DataForSEO uses Basic Authentication with credentials encoded in Base64.

**Steps:**
1. Create account at [app.dataforseo.com](https://app.dataforseo.com)
2. Navigate to **API Access** in dashboard
3. Copy your **API Login** and **API Password** (different from account password)

### Header Format

```
Authorization: Basic {base64(login:password)}
```

### Example

If credentials are:
- Login: `your_email@example.com`
- Password: `abc123xyz`

Base64 encode `your_email@example.com:abc123xyz`:
```
Authorization: Basic eW91cl9lbWFpbEBleGFtcGxlLmNvbTphYmMxMjN4eXo=
```

### n8n Credential Setup

In n8n, create a **Header Auth** credential:
- **Name:** `DataForSEO API`
- **Header Name:** `Authorization`
- **Header Value:** `Basic {your_base64_encoded_credentials}`

---

## 3. ENDPOINT 1: KEYWORD IDEAS

### Purpose
Generate keyword suggestions based on seed keywords for PI Colombia.

### Endpoint
```
POST https://api.dataforseo.com/v3/dataforseo_labs/google/keyword_ideas/live
```

### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `keywords` | array | Yes | Seed keywords (max 200) |
| `location_code` | integer | Yes | Colombia = `2170` |
| `language_code` | string | Yes | Spanish = `es` |
| `include_serp_info` | boolean | No | Include SERP data (recommended: `true`) |
| `limit` | integer | No | Max results (default: 700, max: 1000) |
| `filters` | array | No | Filter conditions |
| `order_by` | array | No | Sort results |

### Request Example

```json
[
  {
    "keywords": [
      "registrar marca colombia",
      "propiedad intelectual",
      "patentes colombia"
    ],
    "location_code": 2170,
    "language_code": "es",
    "include_serp_info": true,
    "limit": 100,
    "filters": [
      ["keyword_info.search_volume", ">=", 100],
      "and",
      ["keyword_properties.keyword_difficulty", "<=", 30]
    ],
    "order_by": [
      "keyword_info.search_volume,desc"
    ]
  }
]
```

### Response Structure

```json
{
  "version": "0.1.20260122",
  "status_code": 20000,
  "status_message": "Ok.",
  "time": "2.5432 sec.",
  "cost": 0.0105,
  "tasks_count": 1,
  "tasks_error": 0,
  "tasks": [
    {
      "id": "01221544-0696-0392-0000-f2ae21b8a66e",
      "status_code": 20000,
      "status_message": "Ok.",
      "time": "2.4521 sec.",
      "cost": 0.0105,
      "result_count": 1,
      "path": ["v3", "dataforseo_labs", "google", "keyword_ideas", "live"],
      "data": {
        "api": "dataforseo_labs",
        "function": "keyword_ideas",
        "se": "google",
        "keywords": ["registrar marca colombia"],
        "location_code": 2170,
        "language_code": "es"
      },
      "result": [
        {
          "se_type": "google",
          "seed_keywords": ["registrar marca colombia"],
          "location_code": 2170,
          "language_code": "es",
          "total_count": 1523,
          "items_count": 100,
          "offset": 0,
          "items": [
            {
              "keyword": "como registrar una marca en colombia",
              "location_code": 2170,
              "language_code": "es",
              "keyword_info": {
                "last_updated_time": "2026-01-15 12:00:00 +00:00",
                "competition": 0.15,
                "cpc": 2.35,
                "search_volume": 480,
                "categories": [12345, 67890],
                "monthly_searches": [
                  {"year": 2026, "month": 1, "search_volume": 520},
                  {"year": 2025, "month": 12, "search_volume": 450},
                  {"year": 2025, "month": 11, "search_volume": 470}
                ]
              },
              "keyword_properties": {
                "core_keyword": "registrar marca colombia",
                "keyword_difficulty": 25
              },
              "impressions_info": {
                "bid": 999,
                "match_type": "exact",
                "ad_position_min": 1.0,
                "ad_position_max": 1.5,
                "cpc_min": 1.80,
                "cpc_max": 3.20,
                "daily_impressions_average": 45.5,
                "daily_clicks_average": 3.2
              },
              "serp_info": {
                "check_url": "https://www.google.com/search?q=...",
                "serp_item_types": ["organic", "people_also_ask", "related_searches"],
                "se_results_count": "1,250,000",
                "last_updated_time": "2026-01-20 08:00:00 +00:00"
              }
            }
          ]
        }
      ]
    }
  ]
}
```

### Key Fields for SUB-K

| Field Path | Use in SUB-K |
|------------|--------------|
| `items[].keyword` | `keyword_text` in Firestore |
| `items[].keyword_info.search_volume` | `volume` |
| `items[].keyword_properties.keyword_difficulty` | `kd` |
| `items[].keyword_info.cpc` | `cpc` |
| `items[].keyword_info.competition` | For analysis |
| `items[].keyword_info.monthly_searches` | Trend analysis |

---

## 4. ENDPOINT 2: BULK KEYWORD DIFFICULTY

### Purpose
Get keyword difficulty scores for up to 1000 keywords in a single request.

### Endpoint
```
POST https://api.dataforseo.com/v3/dataforseo_labs/google/bulk_keyword_difficulty/live
```

### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `keywords` | array | Yes | Keywords to analyze (max 1000) |
| `location_code` | integer | Yes | Colombia = `2170` |
| `language_code` | string | Yes | Spanish = `es` |
| `tag` | string | No | User identifier |

### Request Example

```json
[
  {
    "location_code": 2170,
    "language_code": "es",
    "keywords": [
      "registrar marca colombia",
      "patente software colombia",
      "derechos de autor colombia",
      "propiedad intelectual pymes",
      "como proteger una marca"
    ]
  }
]
```

### Response Structure

```json
{
  "version": "0.1.20260122",
  "status_code": 20000,
  "status_message": "Ok.",
  "time": "0.0510 sec.",
  "cost": 0.0103,
  "tasks_count": 1,
  "tasks_error": 0,
  "tasks": [
    {
      "id": "03021544-0696-0392-0000-f2ae21b8a66e",
      "status_code": 20000,
      "status_message": "Ok.",
      "time": "0.0412 sec.",
      "cost": 0.0103,
      "result_count": 1,
      "path": ["v3", "dataforseo_labs", "google", "bulk_keyword_difficulty", "live"],
      "data": {
        "api": "dataforseo_labs",
        "function": "bulk_keyword_difficulty",
        "se": "google",
        "location_code": 2170,
        "language_code": "es"
      },
      "result": [
        {
          "se_type": "google",
          "location_code": 2170,
          "language_code": "es",
          "total_count": 5,
          "items_count": 5,
          "items": [
            {
              "se_type": "google",
              "keyword": "registrar marca colombia",
              "keyword_difficulty": 28
            },
            {
              "se_type": "google",
              "keyword": "patente software colombia",
              "keyword_difficulty": 18
            },
            {
              "se_type": "google",
              "keyword": "derechos de autor colombia",
              "keyword_difficulty": 22
            },
            {
              "se_type": "google",
              "keyword": "propiedad intelectual pymes",
              "keyword_difficulty": 15
            },
            {
              "se_type": "google",
              "keyword": "como proteger una marca",
              "keyword_difficulty": 35
            }
          ]
        }
      ]
    }
  ]
}
```

### Keyword Difficulty Scale

| Score | Difficulty | Recommendation |
|-------|------------|----------------|
| 0-14 | Very Easy | High priority |
| 15-29 | Easy | Good opportunity |
| 30-49 | Possible | Consider if volume high |
| 50-69 | Difficult | Only if highly relevant |
| 70-84 | Hard | Avoid unless critical |
| 85-100 | Very Hard | Do not target |

**SUB-K Filter:** `kd <= 30` (Easy to Possible)

---

## 5. ENDPOINT 3: SERP ORGANIC ANALYSIS

### Purpose
Analyze current SERP results for a keyword to understand competition.

### Endpoint
```
POST https://api.dataforseo.com/v3/serp/google/organic/live/advanced
```

### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `keyword` | string | Yes | Search query (max 700 chars) |
| `location_code` | integer | Yes | Colombia = `2170` |
| `language_code` | string | Yes | Spanish = `es` |
| `device` | string | No | `desktop` or `mobile` |
| `depth` | integer | No | Results count (default: 10, max: 100) |

### Request Example

```json
[
  {
    "keyword": "como registrar marca en colombia",
    "location_code": 2170,
    "language_code": "es",
    "device": "desktop",
    "depth": 10
  }
]
```

### Response Structure (Simplified)

```json
{
  "version": "0.1.20260122",
  "status_code": 20000,
  "status_message": "Ok.",
  "time": "1.2345 sec.",
  "cost": 0.002,
  "tasks_count": 1,
  "tasks": [
    {
      "id": "01221544-0696-0392-0000-abc123def456",
      "status_code": 20000,
      "result": [
        {
          "keyword": "como registrar marca en colombia",
          "type": "organic",
          "se_domain": "google.com.co",
          "location_code": 2170,
          "language_code": "es",
          "check_url": "https://www.google.com.co/search?q=...",
          "datetime": "2026-01-22 10:30:00 +00:00",
          "item_types": [
            "organic",
            "people_also_ask",
            "related_searches",
            "featured_snippet"
          ],
          "se_results_count": 1250000,
          "items": [
            {
              "type": "organic",
              "rank_group": 1,
              "rank_absolute": 1,
              "position": "left",
              "title": "Registro de Marca - SIC Colombia",
              "url": "https://www.sic.gov.co/registro-de-marca",
              "domain": "sic.gov.co",
              "description": "Pasos para registrar tu marca...",
              "breadcrumb": "sic.gov.co > servicios > registro"
            },
            {
              "type": "organic",
              "rank_group": 2,
              "rank_absolute": 2,
              "position": "left",
              "title": "Guia Completa Registro de Marcas 2026",
              "url": "https://example.com/registro-marcas",
              "domain": "example.com",
              "description": "Todo lo que necesitas saber..."
            },
            {
              "type": "people_also_ask",
              "rank_group": 3,
              "rank_absolute": 5,
              "items": [
                {
                  "title": "Cuanto cuesta registrar una marca en Colombia?",
                  "expanded_element": null
                },
                {
                  "title": "Cuanto tiempo tarda el registro de marca?",
                  "expanded_element": null
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

### Use Cases for SERP Analysis

1. **Competition Assessment:** Who ranks in Top 10?
2. **Content Gap:** What questions appear in "People Also Ask"?
3. **SERP Features:** Is there a featured snippet opportunity?
4. **Domain Authority:** Are competitors major sites or similar-sized?

---

## 6. N8N HTTP REQUEST CONFIGURATION

### Node 1: Keyword Ideas

```json
{
  "name": "DataForSEO - Keyword Ideas",
  "type": "n8n-nodes-base.httpRequest",
  "typeVersion": 4.2,
  "position": [450, 300],
  "parameters": {
    "method": "POST",
    "url": "https://api.dataforseo.com/v3/dataforseo_labs/google/keyword_ideas/live",
    "authentication": "genericCredentialType",
    "genericAuthType": "httpHeaderAuth",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {
          "name": "Content-Type",
          "value": "application/json"
        }
      ]
    },
    "sendBody": true,
    "specifyBody": "json",
    "jsonBody": "=[\n  {\n    \"keywords\": {{ $json.seed_keywords }},\n    \"location_code\": 2170,\n    \"language_code\": \"es\",\n    \"include_serp_info\": true,\n    \"limit\": 100,\n    \"filters\": [\n      [\"keyword_info.search_volume\", \">=\", 100],\n      \"and\",\n      [\"keyword_properties.keyword_difficulty\", \"<=\", 30]\n    ],\n    \"order_by\": [\"keyword_info.search_volume,desc\"]\n  }\n]",
    "options": {
      "timeout": 30000,
      "response": {
        "response": {
          "fullResponse": false,
          "responseFormat": "json"
        }
      }
    }
  },
  "credentials": {
    "httpHeaderAuth": {
      "id": "YOUR_CREDENTIAL_ID",
      "name": "DataForSEO API"
    }
  }
}
```

### Node 2: Bulk Keyword Difficulty

```json
{
  "name": "DataForSEO - Bulk KD",
  "type": "n8n-nodes-base.httpRequest",
  "typeVersion": 4.2,
  "position": [650, 300],
  "parameters": {
    "method": "POST",
    "url": "https://api.dataforseo.com/v3/dataforseo_labs/google/bulk_keyword_difficulty/live",
    "authentication": "genericCredentialType",
    "genericAuthType": "httpHeaderAuth",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {
          "name": "Content-Type",
          "value": "application/json"
        }
      ]
    },
    "sendBody": true,
    "specifyBody": "json",
    "jsonBody": "=[\n  {\n    \"location_code\": 2170,\n    \"language_code\": \"es\",\n    \"keywords\": {{ $json.keywords_array }}\n  }\n]",
    "options": {
      "timeout": 30000
    }
  },
  "credentials": {
    "httpHeaderAuth": {
      "id": "YOUR_CREDENTIAL_ID",
      "name": "DataForSEO API"
    }
  }
}
```

### Node 3: SERP Analysis

```json
{
  "name": "DataForSEO - SERP Analysis",
  "type": "n8n-nodes-base.httpRequest",
  "typeVersion": 4.2,
  "position": [850, 300],
  "parameters": {
    "method": "POST",
    "url": "https://api.dataforseo.com/v3/serp/google/organic/live/advanced",
    "authentication": "genericCredentialType",
    "genericAuthType": "httpHeaderAuth",
    "sendHeaders": true,
    "headerParameters": {
      "parameters": [
        {
          "name": "Content-Type",
          "value": "application/json"
        }
      ]
    },
    "sendBody": true,
    "specifyBody": "json",
    "jsonBody": "=[\n  {\n    \"keyword\": \"{{ $json.keyword_text }}\",\n    \"location_code\": 2170,\n    \"language_code\": \"es\",\n    \"device\": \"desktop\",\n    \"depth\": 10\n  }\n]",
    "options": {
      "timeout": 60000
    }
  },
  "credentials": {
    "httpHeaderAuth": {
      "id": "YOUR_CREDENTIAL_ID",
      "name": "DataForSEO API"
    }
  }
}
```

### Code Node: Transform & Calculate Priority

```javascript
// Transform DataForSEO response to Firestore format
const items = $input.first().json.tasks[0].result[0].items;

const keywords = items.map((item, index) => {
  const volume = item.keyword_info?.search_volume || 0;
  const kd = item.keyword_properties?.keyword_difficulty || 50;
  const cpc = item.keyword_info?.cpc || 0;

  // Priority Score Formula: (volume / 10) - kd + (cpc * 5)
  const priority_score = Math.round((volume / 10) - kd + (cpc * 5));

  // Word count for long-tail filtering
  const word_count = item.keyword.split(' ').length;

  return {
    keyword_id: `kw_${Date.now()}_${index}`,
    keyword_text: item.keyword,
    volume: volume,
    kd: kd,
    cpc: cpc,
    priority_score: priority_score,
    word_count: word_count,
    competition: item.keyword_info?.competition || 0,
    monthly_searches: item.keyword_info?.monthly_searches || [],
    serp_features: item.serp_info?.serp_item_types || [],
    source: 'dataforseo',
    status: 'pendiente',
    created_at: new Date().toISOString()
  };
});

// Filter: word_count >= 3 (long-tail) AND priority_score > 0
const filtered = keywords
  .filter(kw => kw.word_count >= 3 && kw.priority_score > 0)
  .sort((a, b) => b.priority_score - a.priority_score);

return filtered.map(kw => ({ json: kw }));
```

---

## 7. FIRESTORE MAPPING

### Collection: `keywords_pipeline`

| Firestore Field | DataForSEO Source | Type |
|-----------------|-------------------|------|
| `keyword_id` | Generated | string |
| `keyword_text` | `items[].keyword` | string |
| `volume` | `items[].keyword_info.search_volume` | number |
| `kd` | `items[].keyword_properties.keyword_difficulty` | number |
| `cpc` | `items[].keyword_info.cpc` | number |
| `priority_score` | Calculated | number |
| `word_count` | Calculated | number |
| `competition` | `items[].keyword_info.competition` | number |
| `monthly_searches` | `items[].keyword_info.monthly_searches` | array |
| `serp_features` | `items[].serp_info.serp_item_types` | array |
| `category` | Manual/AI assigned | string |
| `source` | `"dataforseo"` | string |
| `status` | `"pendiente"` | string |
| `created_at` | Generated | timestamp |

### Firestore Node Configuration

```json
{
  "name": "Firestore - Save Keywords",
  "type": "n8n-nodes-base.googleFirestore",
  "typeVersion": 1.1,
  "position": [1050, 300],
  "parameters": {
    "operation": "create",
    "projectId": "carrillo-marketing-core",
    "database": "(default)",
    "collection": "keywords_pipeline",
    "documentId": "={{ $json.keyword_id }}",
    "fields": {
      "values": [
        { "fieldPath": "keyword_text", "fieldValue": "={{ $json.keyword_text }}" },
        { "fieldPath": "volume", "fieldValue": "={{ $json.volume }}" },
        { "fieldPath": "kd", "fieldValue": "={{ $json.kd }}" },
        { "fieldPath": "cpc", "fieldValue": "={{ $json.cpc }}" },
        { "fieldPath": "priority_score", "fieldValue": "={{ $json.priority_score }}" },
        { "fieldPath": "word_count", "fieldValue": "={{ $json.word_count }}" },
        { "fieldPath": "competition", "fieldValue": "={{ $json.competition }}" },
        { "fieldPath": "source", "fieldValue": "dataforseo" },
        { "fieldPath": "status", "fieldValue": "pendiente" },
        { "fieldPath": "created_at", "fieldValue": "={{ $now.toISO() }}" }
      ]
    }
  },
  "credentials": {
    "googleFirestoreOAuth2Api": {
      "id": "AAhdRNGzvsFnYN9O",
      "name": "Google Firestore"
    }
  }
}
```

---

## 8. COST ESTIMATION

### DataForSEO Pricing Model

- **Minimum deposit:** $50 USD
- **Pay-as-you-go:** No monthly fees
- **Rate limits:** 2000 API calls/minute, 30 concurrent requests

### Per-Endpoint Costs (Estimated)

| Endpoint | Cost per Request | Notes |
|----------|------------------|-------|
| Keyword Ideas | ~$0.01-0.02 | Varies by result count |
| Bulk Keyword Difficulty | ~$0.01 per 100 keywords | Up to 1000 keywords |
| SERP Organic Live Advanced | ~$0.002 per query | Top 10 results |

### Monthly Scenario: SUB-K Workflow

| Operation | Frequency | Queries | Est. Cost |
|-----------|-----------|---------|-----------|
| Keyword Ideas (3 seed categories) | 1x/month | 3 | $0.06 |
| Bulk KD (validate 200 keywords) | 1x/month | 2 | $0.02 |
| SERP Analysis (top 20 keywords) | 1x/month | 20 | $0.04 |
| **Monthly Total** | - | 25 | **~$0.12** |

### Annual Budget Projection

| Scenario | Monthly | Annual |
|----------|---------|--------|
| Conservative (basic research) | $5-10 | $60-120 |
| Moderate (+ SERP analysis) | $15-25 | $180-300 |
| Aggressive (daily tracking) | $40-60 | $480-720 |

**Recommended budget:** $50-100 USD initial deposit covers 6-12 months.

---

## 9. IMPLEMENTATION CHECKLIST

### Phase 1: Account Setup
- [ ] Create DataForSEO account at [app.dataforseo.com](https://app.dataforseo.com)
- [ ] Add initial deposit ($50 USD minimum)
- [ ] Copy API Login and API Password from dashboard
- [ ] Create Base64 encoded credential string
- [ ] Create Header Auth credential in n8n Cloud

### Phase 2: Test API Calls
- [ ] Test Keyword Ideas endpoint in Postman/Insomnia
- [ ] Verify Colombia location code (2170) works
- [ ] Verify Spanish language code (es) works
- [ ] Test filters for volume and KD
- [ ] Test Bulk KD endpoint
- [ ] Test SERP endpoint

### Phase 3: Build SUB-K Workflow
- [ ] Create HTTP Request node for Keyword Ideas
- [ ] Create Code node for transformation
- [ ] Create Firestore node for storage
- [ ] Add error handling
- [ ] Add Gmail notification
- [ ] Test end-to-end

### Phase 4: Validation
- [ ] Compare results with SEMrush manual check
- [ ] Verify priority score calculation
- [ ] Check Firestore document structure
- [ ] Monitor API costs in dashboard

---

## APPENDIX A: Location Codes

| Country | Location Code |
|---------|---------------|
| Colombia | 2170 |
| United States | 2840 |
| Spain | 2724 |
| Mexico | 2484 |
| Argentina | 2032 |

## APPENDIX B: Error Codes

| Status Code | Meaning | Action |
|-------------|---------|--------|
| 20000 | Success | Continue |
| 40000 | Bad Request | Check parameters |
| 40100 | Unauthorized | Check credentials |
| 40200 | Payment Required | Add funds |
| 50000 | Internal Error | Retry |

## APPENDIX C: References

- [DataForSEO Labs API Overview](https://docs.dataforseo.com/v3/dataforseo_labs-google-overview/)
- [Bulk Keyword Difficulty Docs](https://docs.dataforseo.com/v3/dataforseo_labs-google-bulk_keyword_difficulty-live/)
- [Keyword Ideas Docs](https://docs.dataforseo.com/v3/dataforseo_labs-keyword_ideas-live/)
- [SERP API Docs](https://docs.dataforseo.com/v3/serp-google-organic-overview/)
- [Authentication Guide](https://docs.dataforseo.com/v3/auth/)
- [DataForSEO Pricing](https://dataforseo.com/pricing)

---

**Document Status:** COMPLETE
**Ready for:** SUB-K Workflow Implementation (TICKET-MW3-002)
**Next Step:** Get DataForSEO account credentials from Don Omar approval
