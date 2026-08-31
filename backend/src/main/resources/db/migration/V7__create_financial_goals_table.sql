CREATE TABLE financial_goals (
    id                      UUID PRIMARY KEY,
    user_id                 UUID NOT NULL REFERENCES users (id),
    name                    VARCHAR(150) NOT NULL,
    target_amount           NUMERIC(19, 2) NOT NULL CHECK (target_amount > 0),
    current_amount          NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (current_amount >= 0),
    target_date             DATE,
    monthly_contribution    NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (monthly_contribution >= 0),
    expected_annual_return  NUMERIC(6, 4),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_financial_goals_user ON financial_goals (user_id);
