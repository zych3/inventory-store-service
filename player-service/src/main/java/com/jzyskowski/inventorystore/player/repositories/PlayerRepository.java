package com.jzyskowski.inventorystore.player.repositories;

import com.jzyskowski.inventorystore.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
}
