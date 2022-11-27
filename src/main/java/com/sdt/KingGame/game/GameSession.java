package com.sdt.KingGame.game;

import com.sdt.KingGame.state.GameState;
import com.sdt.KingGame.state.StartedGameState;

import java.util.List;

/**
 * Class for the game session
 */
public class GameSession {
    private final Long gameSessionId;
    private final GameState state;

    public GameSession(Long gameSessionId, List<Player> players) {
        this.gameSessionId = gameSessionId;
        state = new StartedGameState(players, new Deck());
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public GameState getState() {
        return state;
    }
}
