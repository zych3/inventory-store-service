package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.domain.StoreOffer;

import java.time.Instant;
import java.util.UUID;

public record OfferResponse(UUID id, UUID itemId, String itemName, long price,
                            String currencyCode, Instant activeFrom,
                            Instant activeUntil, Integer maxPerPlayer) {
    public static OfferResponse from(StoreOffer offer) {
        return new OfferResponse(offer.getId(), offer.getItem().getId(),
                offer.getItem().getName(), offer.getPrice(),
                offer.getCurrencyCode(), offer.getActiveFrom(),
                offer.getActiveUntil(), offer.getMaxPerPlayer());
    }
}
