package com.sdt.KingGame.web;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * A web client for server functionality testing
 *
 */
public class WebClient {
    private static final int CLIENTS_COUNT = 1;

    public static void main(String[] args) {
        try {

            List<WebSocketSession> clientsSessions = new CopyOnWriteArrayList<>();
            for (int i = 0; i < CLIENTS_COUNT; i++) {
                WebSocketClient webSocketClient = new StandardWebSocketClient();

                WebSocketSession webSocketSession = webSocketClient.doHandshake(new TextWebSocketHandler() {
                    @Override
                    public void handleTextMessage(WebSocketSession session, TextMessage message) {
                        System.out.println("received message - " + message.getPayload());
                    }

                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) {
                        System.out.println("established connection - " + session);
                    }
                }, new WebSocketHttpHeaders(), URI.create("ws://localhost:8080/ws")).get();
                clientsSessions.add(webSocketSession);
            }

            TextMessage message = new TextMessage("[{ \"method\" : \"all\" }]");
            ScheduledExecutorService service = Executors.newScheduledThreadPool(CLIENTS_COUNT);
            for (int i = 0; i < CLIENTS_COUNT; i++) {
                WebSocketSession session = clientsSessions.get(i);
                service.scheduleAtFixedRate(() -> {
                    try {
                        session.sendMessage(message);
                        System.out.println("sent message - " + message.getPayload());
                    } catch (Exception e) {
                        System.out.println("Exception while sending a message: " + e);
                    }
                }, 1, 1, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            System.out.println("Exception while accessing websockets: " + e);
        }
    }
}
