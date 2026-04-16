CREATE TABLE purchases (
                           id              UUID PRIMARY KEY,
                           player_id       UUID NOT NULL,
                           offer_id        UUID NOT NULL REFERENCES store_offers(id),
                           item_id         UUID NOT NULL REFERENCES items(id),
                           price_paid      BIGINT NOT NULL,
                           currency_code   VARCHAR(16) NOT NULL,
                           idempotency_key VARCHAR(128) NOT NULL,
                           status          VARCHAR(16) NOT NULL,
                           failure_reason  VARCHAR(256),
                           created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                           completed_at    TIMESTAMPTZ,
                           UNIQUE (player_id, idempotency_key)
);

CREATE INDEX idx_purchases_pending
    ON purchases (created_at) WHERE status = 'PENDING';