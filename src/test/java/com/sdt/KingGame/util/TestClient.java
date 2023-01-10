package com.sdt.KingGame.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TestClient {
    private final String name;
    private String session_id;
    private WebSocketSession session;
    private String connectionMessage;
    private String lastMessage;
    Logger LOGGER = LoggerFactory.getLogger(TestClient.class);

    public TestClient(String name) {
        this.name = name;
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        try {
            session = webSocketClient.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) throws JSONException {
                    JSONObject receivedMessage = new JSONObject(message.getPayload());
                    if (receivedMessage.isNull("session_id")) {
                        lastMessage = message.getPayload();
                    } else {
                        session_id = receivedMessage.getString("session_id");
                    }
                }

                @Override
                public void afterConnectionEstablished(WebSocketSession session) {
                    connectionMessage = session.getId();
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        LOGGER.error("Sleep interruption: " + e);
                    }
                }
            }, new WebSocketHttpHeaders(), URI.create("ws://localhost:8080/ws")).get();
        } catch (InterruptedException e) {
            LOGGER.error("Client thread was interrupted: " + e);
        } catch (ExecutionException e) {
            LOGGER.error("Cannot execute: " + e);
        }
    }

    public String getName() {
        return name;
    }

    public String getSessionId() {
        return session_id;
    }

    public WebSocketSession getSession() {
        return session;
    }
    public void sendMessage(String message) {
        try {
            session.sendMessage(new TextMessage(message));
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (IOException e) {
            LOGGER.error("Cannot send message: " + e);
        } catch (InterruptedException e) {
            LOGGER.error("Sleep interruption: " + e);
        }
    }

    public String getLastMessage() {
        while (lastMessage == null) {}
        return lastMessage;
    }

    public String getConnectionMessage() {
        while (connectionMessage == null) {}
        return connectionMessage;
    }

    public void disconnect() {
        try {
            session.close();
        } catch (IOException e) {
            LOGGER.error("Cannot close session: " + e);
        }
    }
}
