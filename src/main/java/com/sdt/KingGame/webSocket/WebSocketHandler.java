package com.sdt.KingGame.webSocket;

import com.badlogic.gdx.utils.Array;
import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.util.PauseMaker;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final static Integer PLAYERS_CNT = 4;
    private final static Long PAUSE_WAITING_MILLIS = 180000L;
    private final Array<WebSocketSession> sessions = new Array<>();
    private final List<GameSession> gameSessions = new LinkedList<>();
    private final ConnectListener connectListener = new ConnectListener();
    private final DisconnectListener disconnectListener = new DisconnectListener();
    private final MessageListener messageListener = new MessageListener();
    private final Queue<Player> queueSession = new ConcurrentLinkedQueue<>();
    private final Map<GameSession, PauseMaker> pausedGames = new ConcurrentHashMap<>();
    private Connection connection;

    public WebSocketHandler() throws ClassNotFoundException {
        Runnable task = () -> {
            for (Map.Entry<GameSession, PauseMaker> pausedGame : pausedGames.entrySet()) {
                if (System.currentTimeMillis() - PAUSE_WAITING_MILLIS > pausedGame.getValue().getPauseTime()) {
                    pausedGame.getKey().setCancelledState(pausedGame.getValue().getPausedBy());
                    pausedGames.remove(pausedGame.getKey());
                }
            }
        };
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException, SQLException {
        synchronized (sessions) {
            if (sessions.size == 0) {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/KingGame","root", "root");
            }
            sessions.add(session);
            connectListener.handle(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        messageListener.handle(session, new JSONObject(message.getPayload()), queueSession, gameSessions, PLAYERS_CNT, pausedGames, connection);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws SQLException {
        synchronized (sessions) {
            sessions.removeValue(session, true);
            for (Player player : queueSession) {
                if (player.getSession() == session) {
                    queueSession.remove(player);
                    break;
                }
            }
            disconnectListener.handle(session, gameSessions);
            if (sessions.size == 0) {
                connection.close();
            }
        }
    }

    public static Integer getPlayersCount() {
        return PLAYERS_CNT;
    }
}
