package com.jzyskowski.inventorystore.player.api;

import com.jzyskowski.inventorystore.player.domain.Wallet;

import java.util.UUID;

public record WalletResponse(UUID playerId, long balance) {
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(wallet.getPlayerId(), wallet.getBalance());
    }
}