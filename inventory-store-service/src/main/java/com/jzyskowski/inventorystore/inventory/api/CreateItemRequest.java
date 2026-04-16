package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.domain.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record CreateItemRequest(
        @NotBlank @Size(max = 64) String sku,
        @NotBlank @Size(max = 128) String name,
        @NotNull ItemType type,
        Map<String, Object> metadata
) {}