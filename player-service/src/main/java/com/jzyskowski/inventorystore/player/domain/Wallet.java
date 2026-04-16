package com.jzyskowski.inventorystore.player.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @Column(name = "player_id")
    private UUID playerId;

    @Column(nullable = false)
    private Long balance;

    @Version
    @Column(nullable = false)
    private Long version;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Wallet createForPlayer(UUID playerId) {
        Wallet wallet = new Wallet();
        wallet.playerId = playerId;
        wallet.balance = 0L;
        return wallet;
    }

    public void credit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Credit amount must be positive");
        this.balance += amount;
    }

    public void debit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Debit amount must be positive");
        if (this.balance < amount) throw new InsufficientFundsException(playerId, balance, amount);
        this.balance -= amount;
    }
}
