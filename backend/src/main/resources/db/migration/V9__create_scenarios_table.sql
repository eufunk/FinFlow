CREATE TABLE scenarios (
    id                 UUID PRIMARY KEY,
    user_id            UUID NOT NULL REFERENCES users (id),
    name               VARCHAR(150) NOT NULL,
    current_capital    NUMERIC(19, 2) NOT NULL DEFAULT 0,
    monthly_savings    NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (monthly_savings >= 0),
    annual_return      NUMERIC(6, 4) NOT NULL,
    inflation          NUMERIC(6, 4) NOT NULL,
    duration_in_years  INT NOT NULL CHECK (duration_in_years BETWEEN 1 AND 100),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_scenarios_user ON scenarios (user_id);
