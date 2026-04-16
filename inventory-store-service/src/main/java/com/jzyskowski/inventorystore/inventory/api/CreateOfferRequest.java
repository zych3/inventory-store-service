package com.jzyskowski.inventorystore.inventory.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateOfferRequest(
        @NotNull UUID itemId,
        @Min(1) long price,
        String currencyCode,
        @NotNull Instant activeFrom,
        Instant activeUntil,
        Integer maxPerPlayer
) {}
