package com.sdt.KingGame.game;

import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.state.CancelledGameState;
import com.sdt.KingGame.state.FinishedGameState;
import com.sdt.KingGame.state.GameState;
import com.sdt.KingGame.state.PausedGameState;
import com.sdt.KingGame.state.StartedGameState;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * Class for the game session
 */
public class GameSession {
    /**
     * ID игровой сессии
     */
    private final Long gameSessionId;
    /**
     * Текущее состояние игры
     */
    private GameState state;
    private final List<Player> players;
    private final Deck deck;
    private final GameTurnsPK turnsPK;


    public GameSession(Long gameSessionId, List<Player> players) {
        this.gameSessionId = gameSessionId;
        this.players = players;
        this.deck = new Deck();
        this.turnsPK = new GameTurnsPK(gameSessionId);
        state = new StartedGameState(players, deck, turnsPK);
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public GameState getState() {
        return state;
    }

    public void setFinishedState() {
        state = new FinishedGameState(players, deck, turnsPK);
        int winner = 0;
        int maxPoints = 10 - Integer.MAX_VALUE;
        for (Player player : state.getPlayersWithCards().keySet()) {
            if (player.getPoints() > maxPoints) {
                winner = player.getId();
                maxPoints = player.getPoints();
            }
        }
        ((FinishedGameState) state).setWinner(winner);
    }

    public void setCancelledOrPausedState(Integer pausedBy) {
        if (state instanceof PausedGameState) {
            setCancelledState(pausedBy);
        } else {
            state = new PausedGameState(players, deck, turnsPK);
            ((PausedGameState) state).setPausedBy(pausedBy);
        }
    }

    public void setCancelledState(Integer cancelledBy) {
        state = new CancelledGameState(players, deck, turnsPK);
        ((CancelledGameState) state).setCancelledBy(cancelledBy);
    }

    public void setStartedState(Integer startedBy) {
        state = new StartedGameState(players, deck, turnsPK);
        ((StartedGameState) state).setStartedBy(startedBy);
    }

    public void setStartedStateWithReconnect(Integer reconnectBy, WebSocketSession session) {
        setStartedState(reconnectBy);
        for (Player player : state.getPlayersWithCards().keySet()) {
            if (player.getId() == reconnectBy) {
                player.setWebSocketSession(session);
            }
        }
        state.setDoublePlayers();
    }
}
