package com.sdt.KingGame.webSocket;

import org.springframework.web.socket.WebSocketSession;

public class DisconnectListener {
    public DisconnectListener() {
    }

    public void handle(WebSocketSession session) {
        // TODO логика обработки потери соединения с игроком, а также отсоединение клиента
    }
}
