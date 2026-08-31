CREATE TABLE advisor_assignments (
    id          UUID PRIMARY KEY,
    advisor_id  UUID NOT NULL REFERENCES users (id),
    client_id   UUID NOT NULL REFERENCES users (id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_advisor_client UNIQUE (advisor_id, client_id),
    CONSTRAINT chk_advisor_not_client CHECK (advisor_id <> client_id)
);

CREATE INDEX idx_advisor_assignments_client ON advisor_assignments (client_id);
