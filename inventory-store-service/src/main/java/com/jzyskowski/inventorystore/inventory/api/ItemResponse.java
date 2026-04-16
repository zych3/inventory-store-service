package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.domain.Item;
import com.jzyskowski.inventorystore.inventory.domain.ItemType;

import java.util.Map;
import java.util.UUID;

public record ItemResponse(UUID id, String sku, String name, ItemType type,
                           Map<String, Object> metadata, boolean active) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getSku(), item.getName(),
                item.getType(), item.getMetadata(), item.isActive());
    }
}