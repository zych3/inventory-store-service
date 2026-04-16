package com.jzyskowski.inventorystore.player;

import org.junit.jupiter.api.Test;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class PlayerServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    MockMvc mockMvc;

    @Test
    void fullPurchaseLifecycle() throws Exception {
        String playerResponse = mockMvc.perform(post("/api/v1/players")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"displayName":"TestPlayer"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.displayName").value("TestPlayer"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String playerId = com.jayway.jsonpath.JsonPath.read(playerResponse, "$.id");

        mockMvc.perform(get("/api/v1/wallets/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(0));

        mockMvc.perform(post("/api/v1/wallets/" + playerId + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 1000, "reason": "initial_grant", "idempotencyKey": "grant-001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceAfter").value(1000));

        mockMvc.perform(post("/api/v1/wallets/" + playerId + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 1000, "reason": "initial_grant", "idempotencyKey": "grant-001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceAfter").value(1000));

        mockMvc.perform(get("/api/v1/wallets/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));

        mockMvc.perform(post("/api/v1/wallets/" + playerId + "/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 300, "reason": "purchase:sword", "idempotencyKey": "debit-001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balanceAfter").value(700));

        mockMvc.perform(post("/api/v1/wallets/" + playerId + "/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 800, "reason": "purchase:expensive", "idempotencyKey": "debit-002"}
                                """))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.detail").exists());

        mockMvc.perform(get("/api/v1/wallets/" + playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700));

        mockMvc.perform(get("/api/v1/wallets/" + playerId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void playerNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/players/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void walletNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }
}
