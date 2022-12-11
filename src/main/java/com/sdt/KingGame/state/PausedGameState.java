package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;

import java.util.List;

public class PausedGameState extends GameState {
    private final Integer pausedBy;

    public PausedGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.PAUSED;
        pausedBy = players.get(0).getId();
    }

    public Integer getPausedBy() {
        return pausedBy;
    }
}
