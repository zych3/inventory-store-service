package com.jzyskowski.inventorystore.player.api;

import com.jzyskowski.inventorystore.player.domain.CurrencyTransaction;
import com.jzyskowski.inventorystore.player.services.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/{playerId}")
    public WalletResponse getWallet(@PathVariable UUID playerId) {
        return WalletResponse.from(walletService.getWallet(playerId));
    }

    @PostMapping("/{playerId}/credit")
    public TransactionResponse credit(@PathVariable UUID playerId,
                                      @Valid @RequestBody WalletOperationRequest request) {
        CurrencyTransaction tx = walletService.credit(
                playerId, request.amount(), request.reason(), request.idempotencyKey());
        return TransactionResponse.from(tx);
    }

    @PostMapping("/{playerId}/debit")
    public TransactionResponse debit(@PathVariable UUID playerId,
                                     @Valid @RequestBody WalletOperationRequest request) {
        CurrencyTransaction tx = walletService.debit(
                playerId, request.amount(), request.reason(), request.idempotencyKey());
        return TransactionResponse.from(tx);
    }

    @GetMapping("/{playerId}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable UUID playerId,
            @RequestParam(defaultValue = "20") int limit) {
        return walletService.getTransactions(playerId, Math.min(limit, 100)).stream()
                .map(TransactionResponse::from)
                .toList();
    }
}
