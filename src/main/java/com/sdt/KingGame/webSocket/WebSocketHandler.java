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
    private ConnectListener connectListener = new ConnectListener();
    private DisconnectListener disconnectListener = new DisconnectListener();
    private MessageListener messageListener = new MessageListener();
    private final Queue<Player> queueSession = new ConcurrentLinkedQueue<>();
    private final Map<GameSession, PauseMaker> pausedGames = new ConcurrentHashMap<>();

    public WebSocketHandler() {
        Runnable task = () -> {
            for (Map.Entry<GameSession, PauseMaker> pausedGame : pausedGames.entrySet()) {
                if (System.currentTimeMillis() - PAUSE_WAITING_MILLIS > pausedGame.getValue().getPauseTime()) {
                    pausedGame.getKey().setCancelledState(pausedGame.getValue().getPausedBy());
                }
            }
        };
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        synchronized (sessions) {
            sessions.add(session);
            connectListener.handle(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        messageListener.handle(session, new JSONObject(message.getPayload()), queueSession, gameSessions, PLAYERS_CNT, pausedGames);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        synchronized (sessions) {
            sessions.removeValue(session, true);
            for (Player player : queueSession) {
                if (player.getSession() == session) {
                    queueSession.remove(player);
                    break;
                }
            }
            disconnectListener.handle(session, gameSessions);
        }
    }

    public Array<WebSocketSession> getSessions() {
        return sessions;
    }

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void setDisconnectListener(DisconnectListener disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public static Integer getPlayersCount() {
        return PLAYERS_CNT;
    }
}
