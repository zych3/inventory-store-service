package com.jzyskowski.inventorystore.inventory.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PurchaseRequest(
        @NotNull UUID offerId,
        @NotBlank @Size(max = 128) String idempotencyKey
) {}