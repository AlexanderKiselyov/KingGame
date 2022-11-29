package com.sdt.KingGame.webSocket;

import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.state.GameState;
import com.sdt.KingGame.util.MessageGenerator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageListener {
    private final static Integer PLAYERS_CNT = 4;
    private final AtomicInteger playerIdCounter = new AtomicInteger(1);
    private final Queue<Player> queueSession = new ConcurrentLinkedQueue<>();
    private final MessageGenerator messageGenerator = new MessageGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public MessageListener() {
    }

    public void handle(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions) {
        try {
            String action = jsonValue.getString("action");
            switch (action) {
                case "play" -> playAction(session, jsonValue, gameSessions);
                case "turn" -> turnAction(jsonValue, gameSessions);
                case "pause" -> {
                    // TODO логика обработки паузы (пауза одного игроков или потеря соединения с одним из игроков)
                }
                case "resume" -> {
                    // TODO логика обработки закрытия меню паузы у игрока
                }
                case "reconnect" -> {
                    // TODO логика повторного присоединения игрока после потери соединения
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot handle message. Error: " + e);
        }
    }

    private void playAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions) throws IOException {
        String getSessionId = jsonValue.getString("session_id");
        if (!session.getId().equals(getSessionId)) {
            LOGGER.error("Session id is not valid.");
            return;
        }
        String playerName = jsonValue.getString("player_name");
        List<Player> players = new LinkedList<>();
        synchronized (queueSession) {
            queueSession.add(new Player(playerIdCounter.getAndIncrement(), playerName, session));
            if (queueSession.size() >= PLAYERS_CNT) {
                for (int i = 0; i < PLAYERS_CNT; i++) {
                    players.add(queueSession.poll());
                }
                Long currentGameNumber = System.currentTimeMillis();
                GameSession newGameSession = new GameSession(currentGameNumber, players);
                gameSessions.add(newGameSession);
                messageGenerator.generateMessage(newGameSession);
            }
        }
    }

    private void turnAction(JSONObject jsonValue, List<GameSession> gameSessions) throws IOException {
        Long gameSessionId = jsonValue.getLong("game_session_id");
        GameSession currentGameSession = null;
        for (GameSession gameSession : gameSessions) {
            if (Objects.equals(gameSession.getGameSessionId(), gameSessionId)) {
                currentGameSession = gameSession;
                continue;
            }
            LOGGER.error("No such game session id found: " + gameSessionId);
        }
        JSONObject gameState = jsonValue.getJSONObject("game_state");
        int gameNum = (int) gameState.get("game_num");
        int circleNum = (int) gameState.get("circle_num");
        assert currentGameSession != null;
        GameState currentState = currentGameSession.getState();
        if (currentState.getGameNumber() > gameNum ||
                currentState.getGameNumber() == gameNum && currentState.getCircleNumber() > circleNum ||
                currentState.getGameNumber() == gameNum && currentState.getCircleNumber() + 1 < circleNum ||
                currentState.getGameNumber() + 1 == gameNum && circleNum != 1 ||
                currentState.getGameNumber() + 1 < gameNum) {
            LOGGER.error("Client is not synchronized with the current server game state.");
        }
        int playerId = jsonValue.getInt("player_id");
        JSONObject turn = jsonValue.getJSONObject("turn");
        String suit = (String) turn.get("suit");
        int magnitude = (int) turn.get("magnitude");
        currentGameSession.getState().changeState(playerId, suit, magnitude);
        messageGenerator.generateMessage(currentGameSession);
    }
}
