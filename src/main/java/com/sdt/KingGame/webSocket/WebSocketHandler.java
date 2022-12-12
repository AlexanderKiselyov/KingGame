package com.sdt.KingGame.webSocket;

import com.badlogic.gdx.utils.Array;
import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final static Integer PLAYERS_CNT = 4;
    private final Array<WebSocketSession> sessions = new Array<>();
    private final List<GameSession> gameSessions = new LinkedList<>();
    private ConnectListener connectListener = new ConnectListener();
    private DisconnectListener disconnectListener = new DisconnectListener();
    private MessageListener messageListener = new MessageListener();
    private final Queue<Player> queueSession = new ConcurrentLinkedQueue<>();

    public WebSocketHandler() {
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
        messageListener.handle(session, new JSONObject(message.getPayload()), queueSession, gameSessions, PLAYERS_CNT);
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
