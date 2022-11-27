package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;

import java.util.List;

public class PausedGameState extends GameState {
    private final Integer pausedBy;

    public PausedGameState(List<Player> players, Deck deck) {
        super(players, deck);
        state = "paused";
        pausedBy = players.get(0).getId();
    }

    public Integer getPausedBy() {
        return pausedBy;
    }
}
