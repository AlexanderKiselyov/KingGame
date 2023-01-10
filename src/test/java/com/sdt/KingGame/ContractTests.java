package com.sdt.KingGame;

import com.sdt.KingGame.util.TestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContractTests {
	private static final int CLIENTS_COUNT = 4;
	private List<TestClient> testClients;

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
	void pauseTest() {
		for (TestClient client : testClients) {
			client.sendMessage("{\"session_id\":" + client.getSessionId() + ",\"player_name\":" + client.getName() + ",\"action\":\"play\"}");
		}
		testClients.get(0).sendMessage("{\"game_session_id\":" + testClients.get(0).getGameSessionId() + ",\"player_id\":" + testClients.get(0).getPlayerId() + ",\"action\":\"pause\"}");
		for (TestClient client : testClients) {
			assertThat(client.getLastMessage().replace("\n", "").replaceAll("\\s+", "")).matches("\\{\"game_state\":\\{\"players\":\\[\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\},\\{\"player_id\":[0-9]+,\"player_name\":\"[A-Za-z0-9]+\",\"points\":0\\}\\],\"game_num\":1,\"circle_num\":1,\"state\":\"paused\",\"paused_by\":[0-9]+\\},\"game_session_id\":[0-9]+\\}");
		}
	}
}
