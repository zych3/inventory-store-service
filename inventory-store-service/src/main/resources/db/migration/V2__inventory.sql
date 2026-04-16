CREATE TABLE inventories (
                             player_id   UUID PRIMARY KEY,
                             version     BIGINT NOT NULL DEFAULT 0,
                             updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE inventory_entries (
                                   inventory_player_id UUID NOT NULL REFERENCES inventories(player_id),
                                   item_id             UUID NOT NULL REFERENCES items(id),
                                   quantity            INTEGER NOT NULL CHECK (quantity >= 0),
                                   PRIMARY KEY (inventory_player_id, item_id)
);