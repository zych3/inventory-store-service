CREATE TABLE players (
                         id           UUID PRIMARY KEY,
                         display_name VARCHAR(64) NOT NULL,
                         status       VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
                         created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE wallets (
                         player_id   UUID PRIMARY KEY REFERENCES players(id),
                         balance     BIGINT NOT NULL DEFAULT 0 CHECK (balance >= 0),
                         version     BIGINT NOT NULL DEFAULT 0,
                         updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE currency_transactions (
                                       id               UUID PRIMARY KEY,
                                       player_id        UUID NOT NULL REFERENCES players(id),
                                       amount           BIGINT NOT NULL,
                                       reason           VARCHAR(128) NOT NULL,
                                       idempotency_key  VARCHAR(128) NOT NULL,
                                       balance_after    BIGINT NOT NULL,
                                       created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
                                       UNIQUE (player_id, idempotency_key)
);

CREATE INDEX idx_currency_tx_player_created
    ON currency_transactions (player_id, created_at DESC);