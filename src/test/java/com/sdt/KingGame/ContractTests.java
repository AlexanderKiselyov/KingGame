package com.sdt.KingGame;

import com.sdt.KingGame.util.MessageGenerator;
import com.sdt.KingGame.util.TestClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContractTests {
	private static final int CLIENTS_COUNT = 4;
	private List<TestClient> testClients;
	Logger LOGGER = LoggerFactory.getLogger(ContractTests.class);

	@BeforeAll
	static void beforeAll() {
		SpringApplication.run(KingGameApplication.class);
	}

	@BeforeEach
	void beforeEach() {
		testClients = new ArrayList<>(CLIENTS_COUNT);
		for (int i = 1; i <= CLIENTS_COUNT; i++) {
			testClients.add(new TestClient("client" + i));
		}
	}

	@AfterEach
	void afterEach() {
		for (int i = 0; i < CLIENTS_COUNT; i++) {
			testClients.get(i).disconnect();
		}
		testClients.clear();
	}

	@Test
	void createSessionTest() {
		TestClient testClient1 = new TestClient("client");
		assertThat(testClient1.getConnectionMessage()).matches("[a-z0-9-]+");
	}

	@Test
	void startGameTest() {
		for (TestClient client : testClients) {
			client.sendMessage("{\"session_id\":" + client.getSessionId() + ",\"player_name\":" + client.getName() + ",\"action\":\"play\"}");
		}
		for (TestClient client : testClients) {
			assertThat(client.getLastMessage().replace("\n", "").replaceAll("\\s+", "")).matches("\\{\"game_state\":\\{\"cards\":\\[\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\},\\{\"magnitude\":[0-9]{1,2},\"suit\":\"[a-z]+\"\\}\\],\"bribe\":\\[\\],\"players\":\\[\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\}\\],\"game_num\":1,\"circle_num\":1,\"state\":\"started\",\"started_by\":[0-9]+,\"player_turn\":[0-9]+\\},\"game_session_id\":[0-9]+\\}");
		}
	}

	@Test
	void generateCancelledMessageTest() {
		MessageGenerator messageGenerator = new MessageGenerator();
		for (TestClient client : testClients) {
			client.sendMessage("{\"session_id\":" + client.getSessionId() + ",\"player_name\":" + client.getName() + ",\"action\":\"play\"}");
		}
		try {
			messageGenerator.generateCancelledMessage(testClients.get(0).getSession());
		} catch (IOException e) {
			LOGGER.error("Cannot send message: " + e);
		}
		for (TestClient client : testClients) {
			try {
				JSONObject message = new JSONObject(client.getLastMessage());
				assertThat(message).isEqualTo(new JSONObject("\\{\"game_state\":\\{\"state\":\"cancelled\"\\}\\}"));
			} catch (JSONException e) {
				LOGGER.error("Cannot convert to JSON: " + e);
			}
		}
	}
}
