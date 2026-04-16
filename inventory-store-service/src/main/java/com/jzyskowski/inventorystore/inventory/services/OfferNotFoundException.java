package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(UUID offerId) {
        super("Offer not found: " + offerId);
    }
}
