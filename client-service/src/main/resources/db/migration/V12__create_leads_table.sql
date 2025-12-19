-- ============================================================================
-- V12: Create leads table for marketing automation
-- ============================================================================
-- Purpose: Lead capture for n8n integration (MW#1: Captura - Lead → Client < 1 min)
-- Schema: clients
-- ============================================================================

CREATE TABLE IF NOT EXISTS leads (
    lead_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Basic contact information
    nombre VARCHAR(200) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefono VARCHAR(50),
    empresa VARCHAR(200),
    cargo VARCHAR(150),
    
    -- Interest information
    servicio VARCHAR(100),  -- Practice area of interest
    mensaje TEXT,
    
    -- Lead scoring (calculated by n8n)
    lead_score INTEGER DEFAULT 0,
    lead_category VARCHAR(20) DEFAULT 'COLD',  -- HOT, WARM, COLD
    lead_status VARCHAR(30) DEFAULT 'NUEVO',   -- NUEVO, NURTURING, MQL, SQL, CONVERTIDO, CHURNED
    
    -- Email engagement tracking
    emails_sent INTEGER DEFAULT 0,
    emails_opened INTEGER DEFAULT 0,
    emails_clicked INTEGER DEFAULT 0,
    
    -- Contact tracking
    last_contact TIMESTAMP,
    last_engagement TIMESTAMP,
    
    -- Source tracking
    source VARCHAR(50) DEFAULT 'WEBSITE',  -- WEBSITE, REFERRAL, LINKEDIN, EVENTO, GOOGLE_ADS, TELEFONO, OTRO
    
    -- Conversion tracking
    client_id UUID,  -- Reference to clients table if converted
    converted_at TIMESTAMP,
    
    -- n8n notification tracking
    hot_notified BOOLEAN DEFAULT FALSE,
    hot_notified_at TIMESTAMP,
    
    -- Audit fields
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_lead_category CHECK (lead_category IN ('HOT', 'WARM', 'COLD')),
    CONSTRAINT chk_lead_status CHECK (lead_status IN ('NUEVO', 'NURTURING', 'MQL', 'SQL', 'CONVERTIDO', 'CHURNED')),
    CONSTRAINT chk_lead_source CHECK (source IN ('WEBSITE', 'REFERRAL', 'LINKEDIN', 'EVENTO', 'GOOGLE_ADS', 'TELEFONO', 'OTRO')),
    CONSTRAINT chk_lead_score CHECK (lead_score >= 0 AND lead_score <= 100)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_leads_email ON leads(email);
CREATE INDEX IF NOT EXISTS idx_leads_category ON leads(lead_category);
CREATE INDEX IF NOT EXISTS idx_leads_status ON leads(lead_status);
CREATE INDEX IF NOT EXISTS idx_leads_created_at ON leads(created_at);
CREATE INDEX IF NOT EXISTS idx_leads_hot_pending ON leads(lead_category, hot_notified) WHERE lead_category = 'HOT' AND hot_notified = FALSE;
CREATE INDEX IF NOT EXISTS idx_leads_inactive ON leads(last_engagement) WHERE lead_status NOT IN ('CONVERTIDO', 'CHURNED');

-- Trigger for updated_at
CREATE OR REPLACE FUNCTION update_leads_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_leads_updated_at ON leads;
CREATE TRIGGER trigger_leads_updated_at
    BEFORE UPDATE ON leads
    FOR EACH ROW
    EXECUTE FUNCTION update_leads_updated_at();

-- Comment on table
COMMENT ON TABLE leads IS 'Marketing leads captured from website for n8n automation (MW#1: Captura)';
COMMENT ON COLUMN leads.lead_score IS 'Score 0-100 calculated by n8n (HOT >= 70, WARM 40-69, COLD < 40)';
COMMENT ON COLUMN leads.lead_category IS 'Category based on score: HOT (>= 70), WARM (40-69), COLD (< 40)';
COMMENT ON COLUMN leads.hot_notified IS 'Whether lawyer has been notified about this hot lead';
