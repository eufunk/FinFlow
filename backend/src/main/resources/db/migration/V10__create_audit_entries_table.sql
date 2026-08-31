CREATE TABLE audit_entries (
    id             UUID PRIMARY KEY,
    actor_user_id  UUID REFERENCES users (id),
    action         VARCHAR(50) NOT NULL,
    target_type    VARCHAR(50),
    target_id      UUID,
    occurred_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    metadata       JSONB
);

CREATE INDEX idx_audit_entries_actor_time ON audit_entries (actor_user_id, occurred_at DESC);
