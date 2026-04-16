package com.jzyskowski.inventorystore.player.api;

import com.jzyskowski.inventorystore.player.domain.Player;
import com.jzyskowski.inventorystore.player.services.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlayerResponse createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player player = playerService.createPlayer(request.displayName());
        return PlayerResponse.from(player);
    }

    @GetMapping("/{playerId}")
    public PlayerResponse getPlayer(@PathVariable UUID playerId) {
        return PlayerResponse.from(playerService.getPlayer(playerId));
    }
}
