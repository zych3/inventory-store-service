package com.jzyskowski.inventorystore.inventory.services;

import java.util.UUID;

public class OfferNotActiveException extends RuntimeException {
    public OfferNotActiveException(UUID offerId) {
        super("Offer is not currently active: " + offerId);
    }
}