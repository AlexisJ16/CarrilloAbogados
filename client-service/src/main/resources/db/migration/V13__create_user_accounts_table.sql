-- V13: Create user_accounts table for unified authentication
-- This table handles all authenticated users (clients, lawyers, admins)
-- with support for both LOCAL (password) and OAUTH2_GOOGLE authentication
CREATE TABLE IF NOT EXISTS user_accounts (
    account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(150),
    last_name VARCHAR(150),
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    -- Role and authentication
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_CLIENT',
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    -- Local auth (password)
    password_hash VARCHAR(255),
    -- OAuth2 provider
    provider_id VARCHAR(255),
    -- Status flags
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_verified_at TIMESTAMP WITH TIME ZONE,
    -- Session tracking
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip VARCHAR(45),
    -- Links to other entities
    source_lead_id UUID,
    lawyer_id INTEGER,
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Constraints
    CONSTRAINT uk_user_accounts_email UNIQUE (email),
    CONSTRAINT uk_user_accounts_provider_id UNIQUE (provider_id),
    CONSTRAINT chk_user_accounts_role CHECK (
        role IN (
            'ROLE_VISITOR',
            'ROLE_CLIENT',
            'ROLE_LAWYER',
            'ROLE_ADMIN'
        )
    ),
    CONSTRAINT chk_user_accounts_auth_provider CHECK (auth_provider IN ('LOCAL', 'OAUTH2_GOOGLE'))
);
-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_user_accounts_email ON user_accounts(LOWER(email));
CREATE INDEX IF NOT EXISTS idx_user_accounts_role ON user_accounts(role);
CREATE INDEX IF NOT EXISTS idx_user_accounts_auth_provider ON user_accounts(auth_provider);
CREATE INDEX IF NOT EXISTS idx_user_accounts_is_active ON user_accounts(is_active);
CREATE INDEX IF NOT EXISTS idx_user_accounts_created_at ON user_accounts(created_at);
-- Trigger for updated_at
CREATE OR REPLACE FUNCTION update_user_accounts_updated_at() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trg_user_accounts_updated_at ON user_accounts;
CREATE TRIGGER trg_user_accounts_updated_at BEFORE
UPDATE ON user_accounts FOR EACH ROW EXECUTE FUNCTION update_user_accounts_updated_at();
-- Comments
COMMENT ON TABLE user_accounts IS 'Unified user accounts for authentication (clients, lawyers, admins)';
COMMENT ON COLUMN user_accounts.role IS 'User role: ROLE_VISITOR, ROLE_CLIENT, ROLE_LAWYER, ROLE_ADMIN';
COMMENT ON COLUMN user_accounts.auth_provider IS 'Authentication method: LOCAL (password) or OAUTH2_GOOGLE (Google Workspace)';
COMMENT ON COLUMN user_accounts.provider_id IS 'OAuth2 provider account ID (e.g., Google sub claim)';
COMMENT ON COLUMN user_accounts.source_lead_id IS 'Reference to lead if user was converted from a lead';