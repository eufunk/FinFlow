CREATE TABLE transactions (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES users (id),
    category_id UUID NOT NULL REFERENCES categories (id),
    amount      NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    booked_at   DATE NOT NULL,
    description VARCHAR(255),
    source      VARCHAR(20) NOT NULL DEFAULT 'MANUAL' CHECK (source IN ('MANUAL', 'JSON_IMPORT', 'XML_IMPORT')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_user_booked_at ON transactions (user_id, booked_at DESC);
CREATE INDEX idx_transactions_category ON transactions (category_id);
