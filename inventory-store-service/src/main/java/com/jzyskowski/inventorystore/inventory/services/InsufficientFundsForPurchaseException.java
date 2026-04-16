package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class InsufficientFundsForPurchaseException extends RuntimeException {
    public InsufficientFundsForPurchaseException(UUID playerId, long price) {
        super("Insufficient funds for player %s to purchase item costing %d".formatted(playerId, price));
    }
}
