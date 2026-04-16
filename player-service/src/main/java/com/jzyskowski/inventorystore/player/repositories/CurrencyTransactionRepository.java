package com.jzyskowski.inventorystore.player.repositories;

import com.jzyskowski.inventorystore.player.domain.CurrencyTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, UUID> {
    Optional<CurrencyTransaction> findByPlayerIdAndIdempotencyKey(UUID playerId, String idempotencyKey);
    List<CurrencyTransaction> findByPlayerIdOrderByCreatedAtDesc(UUID playerId, Pageable pageable);
}
