package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;

import java.util.List;

public class StartedGameState extends GameState {
    private final Integer startedBy;

    public StartedGameState(List<Player> players, Deck deck) {
        super(players, deck);
        state = "started";
        startedBy = players.get(0).getId();
    }

    public Integer getStartedBy() {
        return startedBy;
    }
}
