package com.gmail.detection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GmailManagementApplicationTests {

	@Test
	void contextLoads() {
		// If this passes, all beans (security, JPA, JWT, etc.) wired up correctly.
	}

}
