package com.jzyskowski.inventorystore.inventory.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class InventoryEntryId implements Serializable {

    private UUID inventoryPlayerId;
    private UUID itemId;

    public InventoryEntryId() {}

    public InventoryEntryId(UUID inventoryPlayerId, UUID itemId) {
        this.inventoryPlayerId = inventoryPlayerId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryEntryId that)) return false;
        return Objects.equals(inventoryPlayerId, that.inventoryPlayerId)
                && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryPlayerId, itemId);
    }
}
