package com.sdt.KingGame.game;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 *
 * Class for the game session
 *
 */
public class GameSession {
    private final Long gameSessionId;
    private final List<WebSocketSession> playersSessions;
    private final GameState state;

    public GameSession(Long gameSessionId, List<WebSocketSession> playersSessions) {
        this.gameSessionId = gameSessionId;
        this.playersSessions = playersSessions;
        state = new GameState();
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public List<WebSocketSession> getPlayersSessions() {
        return playersSessions;
    }

    public GameState getState() {
        return state;
    }
}
