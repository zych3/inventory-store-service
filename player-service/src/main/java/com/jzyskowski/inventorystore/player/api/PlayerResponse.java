package com.jzyskowski.inventorystore.player.api;

import com.jzyskowski.inventorystore.player.domain.Player;
import com.jzyskowski.inventorystore.player.domain.PlayerStatus;

import java.time.Instant;
import java.util.UUID;

public record PlayerResponse(UUID id, String displayName, PlayerStatus status, Instant createdAt) {
    public static PlayerResponse from(Player player) {
        return new PlayerResponse(player.getId(), player.getDisplayName(), player.getStatus(), player.getCreatedAt());
    }
}
