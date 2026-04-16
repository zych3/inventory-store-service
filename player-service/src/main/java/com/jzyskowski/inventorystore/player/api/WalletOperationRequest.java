package com.jzyskowski.inventorystore.player.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletOperationRequest(
        @Min(1) long amount,
        @NotBlank @Size(max = 128) String reason,
        @NotBlank @Size(max = 128) String idempotencyKey
) {
}
