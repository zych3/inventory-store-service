package com.jzyskowski.inventorystore.player.services;

import com.jzyskowski.inventorystore.player.domain.CurrencyTransaction;
import com.jzyskowski.inventorystore.player.domain.Wallet;
import com.jzyskowski.inventorystore.player.repositories.CurrencyTransactionRepository;
import com.jzyskowski.inventorystore.player.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final CurrencyTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public Wallet getWallet(UUID playerId) {
        return walletRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }

    @Transactional
    public CurrencyTransaction credit(UUID playerId, long amount,
                                      String reason, String idempotencyKey) {
        Optional<CurrencyTransaction> existing =
                transactionRepository.findByPlayerIdAndIdempotencyKey(playerId, idempotencyKey);

        if(existing.isPresent()) {
            return existing.get();
        }

        Wallet wallet = walletRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        wallet.credit(amount);
        walletRepository.save(wallet);

        CurrencyTransaction tx = CurrencyTransaction.record(
                playerId, amount, reason, idempotencyKey, wallet.getBalance());
        return transactionRepository.save(tx);
    }

    @Transactional
    public CurrencyTransaction debit(UUID playerId, long amount,
                                     String reason, String idempotencyKey) {
        Optional<CurrencyTransaction> existing =
                transactionRepository.findByPlayerIdAndIdempotencyKey(playerId, idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        Wallet wallet = walletRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        wallet.debit(amount);
        walletRepository.save(wallet);

        CurrencyTransaction tx = CurrencyTransaction.record(
                playerId, -amount, reason, idempotencyKey, wallet.getBalance());
        return transactionRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public List<CurrencyTransaction> getTransactions(UUID playerId, int limit) {
        return transactionRepository.findByPlayerIdOrderByCreatedAtDesc(
                playerId, PageRequest.of(0, limit));
    }
}
