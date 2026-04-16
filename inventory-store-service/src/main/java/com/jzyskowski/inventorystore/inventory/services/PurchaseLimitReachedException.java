package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class PurchaseLimitReachedException extends RuntimeException {
    public PurchaseLimitReachedException(UUID playerId, UUID offerId, int limit) {
        super("Player %s has reached the purchase limit of %d for offer %s"
                .formatted(playerId, limit, offerId));
    }
}
