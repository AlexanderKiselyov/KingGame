package com.sdt.KingGame.game;

import org.springframework.web.socket.WebSocketSession;

public class Player {
    private final Integer id;
    private final String name;
    private Integer points;
    private WebSocketSession session;

    public Player(Integer id, String name, WebSocketSession session) {
        this.id = id;
        this.name = name;
        this.session = session;
        this.points = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setWebSocketSession(WebSocketSession newWebSocketSession) {
        this.session = newWebSocketSession;
    }
}
