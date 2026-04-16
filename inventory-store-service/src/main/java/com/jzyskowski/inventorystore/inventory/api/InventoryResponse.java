package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.domain.Inventory;

import java.util.List;
import java.util.UUID;

public record InventoryResponse(UUID playerId, List<EntryResponse> entries) {

    public record EntryResponse(UUID itemId, int quantity) {}

    public static InventoryResponse from(Inventory inventory) {
        List<EntryResponse> entries = inventory.getEntries().stream()
                .map(e -> new EntryResponse(e.getItemId(), e.getQuantity()))
                .toList();
        return new InventoryResponse(inventory.getPlayerId(), entries);
    }
}