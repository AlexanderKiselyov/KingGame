package com.sdt.KingGame.webSocket;

import com.badlogic.gdx.utils.Array;
import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.repository.ClientRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final Array<WebSocketSession> sessions = new Array<>();
    private final List<GameSession> gameSessions = new LinkedList<>();
    private ConnectListener connectListener = new ConnectListener();
    private DisconnectListener disconnectListener = new DisconnectListener();
    private MessageListener messageListener = new MessageListener();
    private final ClientRepository clientRepository;

    public WebSocketHandler(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        synchronized (sessions) {
            sessions.add(session);
            connectListener.handle(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        messageListener.handle(session, new JSONObject(message.getPayload()), gameSessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        synchronized (sessions) {
            sessions.removeValue(session, true);
            disconnectListener.handle(session);
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
}
