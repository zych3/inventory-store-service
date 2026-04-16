package com.jzyskowski.inventorystore.inventory;

import org.springframework.boot.SpringApplication;

public class TestInventoryStoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(InventoryStoreServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
