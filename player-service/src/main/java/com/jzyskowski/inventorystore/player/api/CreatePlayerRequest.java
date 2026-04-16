package com.jzyskowski.inventorystore.player.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(
        @NotBlank @Size(max = 64) String displayName
) {
}
