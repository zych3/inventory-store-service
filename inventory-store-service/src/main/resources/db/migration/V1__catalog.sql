CREATE TABLE items (
                       id          UUID PRIMARY KEY,
                       sku         VARCHAR(64) UNIQUE NOT NULL,
                       name        VARCHAR(128) NOT NULL,
                       type        VARCHAR(32) NOT NULL,
                       metadata    JSONB NOT NULL DEFAULT '{}',
                       active      BOOLEAN NOT NULL DEFAULT true,
                       created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE store_offers (
                              id              UUID PRIMARY KEY,
                              item_id         UUID NOT NULL REFERENCES items(id),
                              price           BIGINT NOT NULL CHECK (price > 0),
                              currency_code   VARCHAR(16) NOT NULL DEFAULT 'SOFT',
                              active_from     TIMESTAMPTZ NOT NULL,
                              active_until    TIMESTAMPTZ,
                              max_per_player  INTEGER,
                              created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_offers_active ON store_offers (active_from, active_until);