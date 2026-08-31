CREATE TABLE financial_profiles (
    id                UUID PRIMARY KEY,
    user_id           UUID NOT NULL UNIQUE REFERENCES users (id),
    monthly_income    NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (monthly_income >= 0),
    monthly_expenses  NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (monthly_expenses >= 0),
    emergency_fund    NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (emergency_fund >= 0),
    total_debt        NUMERIC(19, 2) NOT NULL DEFAULT 0 CHECK (total_debt >= 0),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
