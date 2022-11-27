package com.sdt.KingGame.webSocket;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class ConnectListener {
    public ConnectListener() {
    }

    public void handle(WebSocketSession session) throws IOException {
        JSONObject message = new JSONObject();
        message.put("session_id", session.getId());
        session.sendMessage(new TextMessage(message.toString(4)));
    }
}
