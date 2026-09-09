CREATE DOMAIN non_negative_amount_domain AS NUMERIC(19,2)
    CHECK ( VALUE >= 0 );

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES owners(id),
    account_type_id SMALLINT NOT NULL REFERENCES account_types(id),
    balance non_negative_amount_domain NOT NULL CHECK (balance >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_accounts_owner_id ON accounts(owner_id);
CREATE INDEX idx_accounts_account_type_id ON accounts(account_type_id);

