package com.jzyskowski.inventorystore.inventory;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class InventoryStoreIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static WireMockServer wireMock;

    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    static void startWireMock() {
        wireMock = new WireMockServer(9091);
        wireMock.start();
        WireMock.configureFor("localhost", 9091);
    }

    @AfterAll
    static void stopWireMock() {
        wireMock.stop();
    }

    @BeforeEach
    void resetWireMock() {
        wireMock.resetAll();
    }

    @Test
    void fullPurchaseHappyPath() throws Exception {
        UUID playerId = UUID.randomUUID();

        stubDebitSuccess(playerId);

        String itemResponse = mockMvc.perform(post("/api/v1/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "SWORD_001", "name": "Iron Sword", "type": "COSMETIC"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SWORD_001"))
                .andReturn().getResponse().getContentAsString();

        String itemId = com.jayway.jsonpath.JsonPath.read(itemResponse, "$.id");

        String offerResponse = mockMvc.perform(post("/api/v1/admin/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "itemId": "%s",
                                    "price": 300,
                                    "activeFrom": "2025-01-01T00:00:00Z"
                                }
                                """.formatted(itemId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(300))
                .andReturn().getResponse().getContentAsString();

        String offerId = com.jayway.jsonpath.JsonPath.read(offerResponse, "$.id");

        String purchaseResponse = mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Player-Id", playerId.toString())
                        .content("""
                                {"offerId": "%s", "idempotencyKey": "buy-001"}
                                """.formatted(offerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.pricePaid").value(300))
                .andReturn().getResponse().getContentAsString();

        String purchaseId = com.jayway.jsonpath.JsonPath.read(purchaseResponse, "$.id");

        mockMvc.perform(get("/api/v1/inventories/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries.length()").value(1))
                .andExpect(jsonPath("$.entries[0].itemId").value(itemId))
                .andExpect(jsonPath("$.entries[0].quantity").value(1));

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Player-Id", playerId.toString())
                        .content("""
                                {"offerId": "%s", "idempotencyKey": "buy-001"}
                                """.formatted(offerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(purchaseId))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        wireMock.verify(1, postRequestedFor(urlPathMatching("/api/v1/wallets/.*/debit")));
    }

    @Test
    void purchaseFailsOnInsufficientFunds() throws Exception {
        UUID playerId = UUID.randomUUID();

        stubDebitInsufficientFunds(playerId);

        String itemId = createItem("SHIELD_001", "Wooden Shield");
        String offerId = createOffer(itemId, 500);

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Player-Id", playerId.toString())
                        .content("""
                                {"offerId": "%s", "idempotencyKey": "buy-shield-001"}
                                """.formatted(offerId)))
                .andExpect(status().isPaymentRequired());

        mockMvc.perform(get("/api/v1/inventories/" + playerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void purchaseLimitEnforced() throws Exception {
        UUID playerId = UUID.randomUUID();
        stubDebitSuccess(playerId);

        String itemId = createItem("HELM_001", "Iron Helm");
        String offerId = createOfferWithLimit(itemId, 200, 1);

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Player-Id", playerId.toString())
                        .content("""
                                {"offerId": "%s", "idempotencyKey": "helm-001"}
                                """.formatted(offerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Player-Id", playerId.toString())
                        .content("""
                                {"offerId": "%s", "idempotencyKey": "helm-002"}
                                """.formatted(offerId)))
                .andExpect(status().isConflict());
    }

    @Test
    void catalogAndStoreEndpoints() throws Exception {
        mockMvc.perform(post("/api/v1/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "BOW_001", "name": "Longbow", "type": "COSMETIC"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/store/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.sku == 'BOW_001')]").exists());

        mockMvc.perform(get("/api/v1/store/offers"))
                .andExpect(status().isOk());
    }


    private void stubDebitSuccess(UUID playerId) {
        wireMock.stubFor(WireMock.post(urlPathMatching("/api/v1/wallets/.*/debit"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"balanceAfter": 700}
                                """)));
    }

    private void stubDebitInsufficientFunds(UUID playerId) {
        wireMock.stubFor(WireMock.post(urlPathMatching("/api/v1/wallets/.*/debit"))
                .willReturn(aResponse()
                        .withStatus(402)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"detail": "Insufficient funds"}
                                """)));
    }

    private String createItem(String sku, String name) throws Exception {
        String response = mockMvc.perform(post("/api/v1/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"sku": "%s", "name": "%s", "type": "COSMETIC"}
                                """.formatted(sku, name)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.id");
    }

    private String createOffer(String itemId, long price) throws Exception {
        String response = mockMvc.perform(post("/api/v1/admin/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "itemId": "%s",
                                    "price": %d,
                                    "activeFrom": "2025-01-01T00:00:00Z"
                                }
                                """.formatted(itemId, price)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.id");
    }

    private String createOfferWithLimit(String itemId, long price, int maxPerPlayer) throws Exception {
        String response = mockMvc.perform(post("/api/v1/admin/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "itemId": "%s",
                                    "price": %d,
                                    "activeFrom": "2025-01-01T00:00:00Z",
                                    "maxPerPlayer": %d
                                }
                                """.formatted(itemId, price, maxPerPlayer)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.id");
    }
}
