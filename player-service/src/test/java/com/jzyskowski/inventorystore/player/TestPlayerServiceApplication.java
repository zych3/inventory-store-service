package com.jzyskowski.inventorystore.player;

import org.springframework.boot.SpringApplication;

public class TestPlayerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PlayerServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
