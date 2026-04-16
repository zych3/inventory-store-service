package com.jzyskowski.inventorystore.events;

import java.time.Instant;
import java.util.UUID;

public record PurchaseCompletedEvent(
        UUID eventId,
        UUID purchaseId,
        UUID playerId,
        UUID itemId,
        long pricePaid,
        Instant occurredAt
) {}