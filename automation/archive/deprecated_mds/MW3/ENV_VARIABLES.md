# SUB-K: Keyword Research - Environment Variables & Credentials

**Version:** 1.0
**Created:** 2026-01-23
**Related Workflow:** `SUB-K_KEYWORD_RESEARCH.json`

---

## Required Credentials in n8n Cloud

### 1. DataForSEO API (Header Auth)

| Field | Value | Notes |
|-------|-------|-------|
| **Credential Type** | Header Auth | Generic credential |
| **Name** | `DataForSEO API` | Use exact name |
| **Header Name** | `Authorization` | Case-sensitive |
| **Header Value** | `Basic {base64_encoded}` | See encoding below |

**How to create Base64 encoded credentials:**

```bash
# Format: login:password
# Example: echo -n "your_email@example.com:your_api_password" | base64

# Result example:
# eW91cl9lbWFpbEBleGFtcGxlLmNvbTp5b3VyX2FwaV9wYXNzd29yZA==

# Full header value:
# Basic eW91cl9lbWFpbEBleGFtcGxlLmNvbTp5b3VyX2FwaV9wYXNzd29yZA==
```

**Where to get credentials:**
1. Sign up at [app.dataforseo.com](https://app.dataforseo.com)
2. Go to **API Access** in the dashboard
3. Copy **API Login** (your email) and **API Password** (NOT account password)

---

### 2. Google Firestore OAuth2

| Field | Value | Notes |
|-------|-------|-------|
| **Credential Type** | Google Cloud Firestore OAuth2 | Built-in n8n credential |
| **Credential ID** | `AAhdRNGzvsFnYN9O` | Already configured |
| **Name** | `Google Firestore` | Existing credential |
| **Project ID** | `carrillo-marketing-core` | Firebase project |

**Collection used:** `keywords_pipeline`

---

### 3. Gmail OAuth2

| Field | Value | Notes |
|-------|-------|-------|
| **Credential Type** | Gmail OAuth2 | Built-in n8n credential |
| **Credential ID** | `l2mMgEf8YUV7HHlK` | Already configured |
| **Name** | `Gmail OAuth2` | Existing credential |
| **Sender Account** | marketing@carrilloabgd.com | Notifications sent from here |

---

## Workflow Parameters (Editable in Set Node)

These parameters are defined in node "1. Define Seed Keywords" and can be modified without changing code:

| Parameter | Default Value | Description |
|-----------|---------------|-------------|
| `seed_keywords` | `["registrar marca colombia", ...]` | Array of seed keywords for research |
| `location_code` | `2170` | Colombia (DataForSEO location code) |
| `language_code` | `es` | Spanish |
| `min_volume` | `100` | Minimum monthly search volume |
| `max_kd` | `30` | Maximum keyword difficulty (0-100) |

---

## DataForSEO Location Codes (Reference)

| Country | Code |
|---------|------|
| Colombia | 2170 |
| United States | 2840 |
| Spain | 2724 |
| Mexico | 2484 |
| Argentina | 2032 |
| Peru | 2604 |
| Chile | 2152 |

---

## API Costs (Budget Planning)

| Endpoint | Estimated Cost | Usage in SUB-K |
|----------|----------------|----------------|
| Keyword Ideas | ~$0.01-0.02/request | 1x/month |
| Bulk KD (future) | ~$0.01/100 keywords | Optional |
| SERP Analysis (future) | ~$0.002/query | Optional |

**Monthly estimated cost:** $0.02-0.05 USD
**Recommended initial deposit:** $50 USD (lasts 6-12 months)

---

## Pre-Deployment Checklist

- [ ] DataForSEO account created at app.dataforseo.com
- [ ] Initial deposit made ($50 USD minimum)
- [ ] API Login and Password copied from dashboard
- [ ] Base64 credential string generated
- [ ] Header Auth credential created in n8n Cloud
- [ ] Credential ID updated in workflow JSON (replace `DATAFORSEO_CREDENTIAL_ID`)
- [ ] Test API call successful (use Manual Trigger)
- [ ] Firestore collection `keywords_pipeline` accessible
- [ ] Gmail notifications working

---

## Troubleshooting

### Error: 40100 Unauthorized
- Check API Login and Password are correct
- Verify Base64 encoding includes colon separator (login:password)
- Ensure Header value starts with "Basic "

### Error: 40200 Payment Required
- Add funds to DataForSEO account
- Check balance at app.dataforseo.com

### Error: Firestore permission denied
- Verify credential has access to project `carrillo-marketing-core`
- Check collection name is `keywords_pipeline`

### Error: Gmail send failed
- Re-authorize Gmail OAuth2 credential
- Verify sender account is marketing@carrilloabgd.com

---

**Last Updated:** 2026-01-23
