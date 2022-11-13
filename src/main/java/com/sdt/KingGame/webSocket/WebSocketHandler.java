package com.sdt.KingGame.webSocket;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.sdt.KingGame.repository.ClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    private final static Integer PLAYERS_CNT = 2;
    private Long currentGameNumber = System.currentTimeMillis();
    private final Array<WebSocketSession> sessions = new Array<>();
    private final Map<Long, WebSocketSession> gameSessions = new HashMap<>();
    private final Queue<WebSocketSession> queueSession = new LinkedBlockingQueue<>(PLAYERS_CNT);
    private ConnectListener connectListener = new ConnectListener();
    private DisconnectListener disconnectListener = new DisconnectListener();
    private MessageListener messageListener = new MessageListener();
    private final ClientRepository clientRepository;
    private final JsonReader reader = new JsonReader();


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
        String payload = message.getPayload();
        JsonValue jsonValue = reader.parse(payload);
        messageListener.handle(session, jsonValue, clientRepository);
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
