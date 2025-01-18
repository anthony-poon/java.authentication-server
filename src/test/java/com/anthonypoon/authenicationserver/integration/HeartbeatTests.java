package com.anthonypoon.authenicationserver.integration;

import com.anthonypoon.authenticationserver.AuthenticationServerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.MOCK,
	classes = AuthenticationServerApplication.class
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HeartbeatTests {
	@Autowired
	private MockMvc mvc;

	@Test
	public void testCanHeartbeat() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/heartbeat"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.application").value("authentication-server-test"));
	}

}
