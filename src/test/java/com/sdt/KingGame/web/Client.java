package com.sdt.KingGame.web;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.util.States;
import com.sdt.KingGame.util.Suits;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Client {
    private final String name;
    private Integer player_id;
    private Integer points;
    private String session_id;
    private Long game_session_id;
    private final List<Card> cards;
    private final WebSocketSession session;
    private final State state;
    private String lastMessage = "{}";
    private final Random random = new Random(100);

    public Client(String name) throws ExecutionException, InterruptedException {
        this.name = name;
        state = new State();
        cards = new ArrayList<>();
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        session = webSocketClient.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws JSONException {
                lastMessage = message.getPayload();
                JSONObject jsonMessage = new JSONObject(lastMessage);
                if (!jsonMessage.isNull("session_id")) {
                    session_id = jsonMessage.getString("session_id");
                } else if (!Objects.equals(jsonMessage.getJSONObject("game_state").getString("state"), States.FINISHED.getName())) {
                    game_session_id = jsonMessage.getLong("game_session_id");
                    JSONObject gameState = jsonMessage.getJSONObject("game_state");
                    JSONArray players = gameState.getJSONArray("players");
                    state.setGameNum(gameState.getInt("game_num"));
                    state.setCircleNum(gameState.getInt("circle_num"));
                    player_id = players.getJSONObject(0).getInt("player_id");
                    points = players.getJSONObject(0).getInt("points");
                    JSONArray playerCards = gameState.getJSONArray("cards");
                    for (int i = 0; i < playerCards.length(); i++) {
                        JSONObject playerCard = playerCards.getJSONObject(i);
                        cards.add(new Card(Suits.getSuit(playerCard.getString("suit")), playerCard.getInt("magnitude")));
                    }
                }
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                System.out.println("established connection: " + session);
            }
        }, new WebSocketHttpHeaders(), URI.create("ws://localhost:8080/ws")).get();
    }

    public String getSessionId() {
        return session_id;
    }

    public String getName() {
        return name;
    }

    public Integer getPlayerId() {
        return player_id;
    }

    public Integer getPoints() {
        return points;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Long getGameSessionId() {
        return game_session_id;
    }

    public State getState() {
        return state;
    }

    public Card getCard() {
        return cards.get(random.nextInt(cards.size()));
    }

    public void sendMessage(String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }

    public void setLastMessageNull() {
        lastMessage = "{}";
    }

    public void closeSession() throws IOException {
        session.close();
    }
}
