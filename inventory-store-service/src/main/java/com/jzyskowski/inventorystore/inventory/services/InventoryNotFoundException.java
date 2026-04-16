package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(UUID playerId) {
        super("Inventory not found for player: " + playerId);
    }
}
