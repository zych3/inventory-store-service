package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.domain.Purchase;
import com.jzyskowski.inventorystore.inventory.domain.PurchaseStatus;

import java.time.Instant;
import java.util.UUID;

public record PurchaseResponse(UUID id, UUID playerId, UUID offerId, UUID itemId,
                               long pricePaid, PurchaseStatus status,
                               String failureReason, Instant createdAt,
                               Instant completedAt) {
    public static PurchaseResponse from(Purchase purchase) {
        return new PurchaseResponse(purchase.getId(), purchase.getPlayerId(),
                purchase.getOfferId(), purchase.getItemId(), purchase.getPricePaid(),
                purchase.getStatus(), purchase.getFailureReason(),
                purchase.getCreatedAt(), purchase.getCompletedAt());
    }
}