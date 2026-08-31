CREATE TABLE insurance_policies (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES users (id),
    type            VARCHAR(20) NOT NULL CHECK (type IN ('LIABILITY', 'DISABILITY', 'HOUSEHOLD', 'HEALTH', 'OTHER')),
    coverage_amount NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (coverage_amount >= 0),
    valid_from      DATE NOT NULL,
    valid_until     DATE,
    CONSTRAINT chk_valid_range CHECK (valid_until IS NULL OR valid_until >= valid_from)
);

CREATE INDEX idx_insurance_policies_user ON insurance_policies (user_id);
