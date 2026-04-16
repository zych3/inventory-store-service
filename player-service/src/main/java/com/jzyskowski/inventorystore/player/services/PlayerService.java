package com.jzyskowski.inventorystore.player.services;

import com.jzyskowski.inventorystore.player.domain.Player;
import com.jzyskowski.inventorystore.player.domain.Wallet;
import com.jzyskowski.inventorystore.player.repositories.PlayerRepository;
import com.jzyskowski.inventorystore.player.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public Player createPlayer(String displayName) {
        Player player = Player.create(displayName);
        playerRepository.save(player);

        Wallet wallet = Wallet.createForPlayer(player.getId());
        walletRepository.save(wallet);

        return player;
    }

    @Transactional(readOnly = true)
    public Player getPlayer(UUID playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
