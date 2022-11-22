package com.sdt.KingGame.webSocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class ConnectListener {
    public ConnectListener() {}

    public void handle(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("{ sessionId : " + session.getId() + " }"));
    }
}
