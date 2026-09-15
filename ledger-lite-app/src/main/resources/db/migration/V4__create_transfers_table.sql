CREATE TYPE transfer_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED');

CREATE TABLE IF NOT EXISTS transfers(
    id BIGSERIAL PRIMARY KEY,
    idempotency_key UUID NOT NULL UNIQUE,
    from_account_id UUID NOT NULL REFERENCES accounts(id),
    to_account_id UUID NOT NULL REFERENCES accounts(id),
    amount NUMERIC(19,2) NOT NULL CHECK ( amount > 0 ),
    status transfer_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT different_accounts_check CHECK (from_account_id <> to_account_id)
);

CREATE INDEX idx_transfers_idempotency_key ON transfers(idempotency_key);
