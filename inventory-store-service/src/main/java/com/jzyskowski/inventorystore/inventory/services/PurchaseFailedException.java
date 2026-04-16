package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class PurchaseFailedException extends RuntimeException {
    public PurchaseFailedException(UUID purchaseId, String reason) {
        super("Purchase %s failed: %s".formatted(purchaseId, reason));
    }
}