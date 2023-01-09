package com.sdt.KingGame.webSocket;

import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.state.GameState;
import com.sdt.KingGame.util.PauseMaker;
import com.sdt.KingGame.util.States;
import com.sdt.KingGame.util.MessageGenerator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageListener {
    private final AtomicInteger playerIdCounter = new AtomicInteger(1);
    private final MessageGenerator messageGenerator = new MessageGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public MessageListener() {
    }

    public void handle(WebSocketSession session, JSONObject jsonValue, Queue<Player> queueSession, List<GameSession> gameSessions,
                       Integer playersCount, Map<GameSession, PauseMaker> pausedGames, Connection connection) {
        try {
            String action = jsonValue.getString("action");
            switch (action) {
                case "play" -> playAction(session, jsonValue, gameSessions, playersCount, queueSession);
                case "turn" -> turnAction(session, jsonValue, gameSessions, connection);
                case "pause" -> pauseAction(session, jsonValue, gameSessions, pausedGames);
                case "resume" -> resumeAction(session, jsonValue, gameSessions, pausedGames);
                case "reconnect" -> reconnectAction(session, jsonValue, gameSessions, pausedGames);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot handle message. Error: " + e);
        }
    }

    private void playAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions, Integer playersCount,
                            Queue<Player> queueSession) throws IOException {
        String getSessionId = jsonValue.getString("session_id");
        if (!session.getId().equals(getSessionId)) {
            LOGGER.error("Session id is not valid.");
            return;
        }
        String playerName = jsonValue.getString("player_name");
        List<Player> players = new LinkedList<>();
        synchronized (queueSession) {
            queueSession.add(new Player(playerIdCounter.getAndIncrement(), playerName, session));
            if (queueSession.size() >= playersCount) {
                for (int i = 0; i < playersCount; i++) {
                    players.add(queueSession.poll());
                }
                Long currentGameNumber = System.currentTimeMillis();
                GameSession newGameSession = new GameSession(currentGameNumber, players);
                gameSessions.add(newGameSession);
                messageGenerator.generateMessage(newGameSession);
            }
        }
    }

    private void turnAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions, Connection connection) throws IOException, SQLException {
        GameSession currentGameSession = getGameSession(jsonValue, gameSessions);
        if (currentGameSession != null) {
            GameState currentState = currentGameSession.getState();
            JSONObject gameState = jsonValue.getJSONObject("game_state");
            int gameNum = (int) gameState.get("game_num");
            int circleNum = (int) gameState.get("circle_num");
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
            currentState.changeState(playerId, suit, magnitude, connection);
            if (currentState.getStateValue() == States.FINISHED) {
                currentGameSession.setFinishedState();
            }
            messageGenerator.generateMessage(currentGameSession);
            currentState.clearBribe();
            if (currentState.getStateValue() == States.FINISHED) {
                gameSessions.remove(currentGameSession);
            }
        } else {
            messageGenerator.generateCancelledMessage(session);
        }
    }

    private void pauseAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions,
                             Map<GameSession, PauseMaker> pausedGames) throws IOException {
        GameSession currentGameSession = getGameSession(jsonValue, gameSessions);
        if (currentGameSession != null) {
            Integer pausedBy = jsonValue.getInt("player_id");
            pausedGames.put(currentGameSession, new PauseMaker(System.currentTimeMillis(), pausedBy));
            currentGameSession.setCancelledOrPausedState(pausedBy);
            messageGenerator.generateMessage(currentGameSession);
            if (currentGameSession.getState().getStateValue() == States.CANCELLED) {
                gameSessions.remove(currentGameSession);
            }
        } else {
            messageGenerator.generateCancelledMessage(session);
        }
    }

    private void resumeAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions,
                              Map<GameSession, PauseMaker> pausedGames) throws IOException {
        GameSession currentGameSession = getGameSession(jsonValue, gameSessions);
        if (currentGameSession != null) {
            Integer startedBy = jsonValue.getInt("player_id");
            pausedGames.remove(currentGameSession);
            currentGameSession.setStartedState(startedBy);
            messageGenerator.generateMessage(currentGameSession);
        } else {
            messageGenerator.generateCancelledMessage(session);
        }

    }

    private void reconnectAction(WebSocketSession session, JSONObject jsonValue, List<GameSession> gameSessions,
                                 Map<GameSession, PauseMaker> pausedGames) throws IOException {
        GameSession currentGameSession = getGameSession(jsonValue, gameSessions);
        if (currentGameSession != null) {
            Integer reconnectBy = jsonValue.getInt("player_id");
            pausedGames.remove(currentGameSession);
            currentGameSession.setStartedStateWithReconnect(reconnectBy, session);
            messageGenerator.generateMessage(currentGameSession);
        } else {
            messageGenerator.generateCancelledMessage(session);
        }
    }

    private GameSession getGameSession(JSONObject jsonValue, List<GameSession> gameSessions) {
        Long gameSessionId = jsonValue.getLong("game_session_id");
        GameSession currentGameSession = null;
        for (GameSession gameSession : gameSessions) {
            if (Objects.equals(gameSession.getGameSessionId(), gameSessionId)) {
                currentGameSession = gameSession;
                continue;
            }
            LOGGER.error("No such game session id found: " + gameSessionId);
        }
        return currentGameSession;
    }
}
