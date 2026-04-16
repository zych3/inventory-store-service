package com.jzyskowski.inventorystore.inventory.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import java.net.http.HttpClient;


import java.util.UUID;

@Component
public class PlayerServiceClient {

    private final RestClient restClient;

    public PlayerServiceClient(@Value("${player-service.base-url}") String baseUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    public DebitResult debit(UUID playerId, long amount, String reason, String idempotencyKey) {
        try {
            DebitResponse response = restClient.post()
                    .uri("/api/v1/wallets/{playerId}/debit", playerId)
                    .body(new DebitRequest(amount, reason, idempotencyKey))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        if (res.getStatusCode().value() == 402) {
                            throw new InsufficientFundsException();
                        }
                        if (res.getStatusCode().value() == 409) {
                            throw new WalletConflictException();
                        }
                        throw new PlayerServiceException("Client error: " + res.getStatusCode());
                    })
                    .body(DebitResponse.class);

            return new DebitResult(true, response != null ? response.balanceAfter() : 0, null);
        } catch (InsufficientFundsException e) {
            return new DebitResult(false, 0, "INSUFFICIENT_FUNDS");
        } catch (WalletConflictException e) {
            return new DebitResult(false, 0, "CONFLICT");
        } catch (Exception e) {
            return new DebitResult(false, 0, "SERVICE_UNAVAILABLE: " + e.getMessage());
        }
    }

    public CreditResult credit(UUID playerId, long amount, String reason, String idempotencyKey) {
        try {
            restClient.post()
                    .uri("/api/v1/wallets/{playerId}/credit", playerId)
                    .body(new DebitRequest(amount, reason, idempotencyKey))
                    .retrieve()
                    .body(DebitResponse.class);
            return new CreditResult(true, null);
        } catch (Exception e) {
            return new CreditResult(false, e.getMessage());
        }
    }

    record DebitRequest(long amount, String reason, String idempotencyKey) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DebitResponse(long balanceAfter) {}

    public record DebitResult(boolean success, long balanceAfter, String failureReason) {}
    public record CreditResult(boolean success, String failureReason) {}

    static class InsufficientFundsException extends RuntimeException {}
    static class WalletConflictException extends RuntimeException {}
    static class PlayerServiceException extends RuntimeException {
        PlayerServiceException(String msg) { super(msg); }
    }
}
