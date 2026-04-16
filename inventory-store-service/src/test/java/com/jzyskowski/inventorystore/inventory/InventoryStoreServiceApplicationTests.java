package com.jzyskowski.inventorystore.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class InventoryStoreServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
