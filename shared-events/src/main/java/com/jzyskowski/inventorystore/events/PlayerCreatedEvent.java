package com.jzyskowski.inventorystore.events;

import java.time.Instant;
import java.util.UUID;

public record PlayerCreatedEvent(
        UUID eventId,
        UUID playerId,
        String displayName,
        Instant occurredAt
) {}