package com.jzyskowski.inventorystore.player.repositories;

import com.jzyskowski.inventorystore.player.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
}
