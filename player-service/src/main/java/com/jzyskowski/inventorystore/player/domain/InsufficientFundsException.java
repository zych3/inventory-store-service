package com.jzyskowski.inventorystore.player.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InsufficientFundsException extends RuntimeException {
    private final UUID playerId;
    private final long currentBalance;
    private final long requestedAmount;

    public InsufficientFundsException(UUID playerId, long currentBalance, long requestedAmount) {
        super("Insufficient funds for player %s: balande=%d, requested=%d"
                .formatted(playerId, currentBalance, requestedAmount));
        this.playerId = playerId;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }
}
