package com.jzyskowski.inventorystore.inventory.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jzyskowski.inventorystore.events.PurchaseCompletedEvent;
import com.jzyskowski.inventorystore.inventory.domain.*;
import com.jzyskowski.inventorystore.inventory.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final StoreOfferRepository storeOfferRepository;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final PlayerServiceClient playerServiceClient;
    private final ObjectMapper objectMapper;

    private static final int MAX_DEBIT_RETRIES = 3;

    @Transactional
    public Purchase initiatePurchase(UUID playerId, UUID offerId, String idempotencyKey) {
        Optional<Purchase> existing = purchaseRepository.findByPlayerIdAndIdempotencyKey(
                playerId, idempotencyKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        StoreOffer offer = storeOfferRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        if (!offer.isActiveAt(Instant.now())) {
            throw new OfferNotActiveException(offerId);
        }

        if (offer.getMaxPerPlayer() != null) {
            long count = purchaseRepository.countByPlayerIdAndOfferId(playerId, offerId);
            if (count >= offer.getMaxPerPlayer()) {
                throw new PurchaseLimitReachedException(playerId, offerId, offer.getMaxPerPlayer());
            }
        }

        Purchase purchase = Purchase.initiate(playerId, offer, idempotencyKey);
        purchaseRepository.save(purchase);

        PlayerServiceClient.DebitResult debitResult = debitWithRetry(
                playerId, offer.getPrice(),
                "purchase:" + purchase.getId(),
                purchase.getId().toString());

        if (!debitResult.success()) {
            purchase.fail(debitResult.failureReason());
            purchaseRepository.save(purchase);
            if ("INSUFFICIENT_FUNDS".equals(debitResult.failureReason())) {
                throw new InsufficientFundsForPurchaseException(playerId, offer.getPrice());
            }
            throw new PurchaseFailedException(purchase.getId(), debitResult.failureReason());
        }

        grantAndComplete(purchase, offer);

        return purchase;
    }

    private void grantAndComplete(Purchase purchase, StoreOffer offer) {
        Inventory inventory = inventoryRepository.findByPlayerIdWithEntries(purchase.getPlayerId())
                .orElseGet(() -> inventoryRepository.save(
                        Inventory.createForPlayer(purchase.getPlayerId())));

        Item item = itemRepository.findById(purchase.getItemId())
                .orElseThrow(() -> new IllegalStateException("Item not found: " + purchase.getItemId()));

        inventory.grantItem(item, 1);
        inventoryRepository.save(inventory);

        purchase.complete();
        purchaseRepository.save(purchase);

        writeOutboxEvent(purchase);
    }

    private void writeOutboxEvent(Purchase purchase) {
        try {
            PurchaseCompletedEvent event = new PurchaseCompletedEvent(
                    UUID.randomUUID(),
                    purchase.getId(),
                    purchase.getPlayerId(),
                    purchase.getItemId(),
                    purchase.getPricePaid(),
                    Instant.now()
            );

            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.create(
                    "Purchase",
                    purchase.getId().toString(),
                    "purchase.completed",
                    payload
            );

            outboxEventRepository.save(outbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event", e);
        }
    }

    private PlayerServiceClient.DebitResult debitWithRetry(UUID playerId, long amount,
                                                           String reason, String idempotencyKey) {
        PlayerServiceClient.DebitResult result = null;
        for (int attempt = 1; attempt <= MAX_DEBIT_RETRIES; attempt++) {
            result = playerServiceClient.debit(playerId, amount, reason, idempotencyKey);
            if (result.success() || "INSUFFICIENT_FUNDS".equals(result.failureReason())) {
                return result;
            }
            log.warn("Debit attempt {} failed: {}", attempt, result.failureReason());
            if (attempt < MAX_DEBIT_RETRIES) {
                try {
                    Thread.sleep(100L * attempt);  // simple linear backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return result;
                }
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Purchase getPurchase(UUID purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new PurchaseNotFoundException(purchaseId));
    }
}
