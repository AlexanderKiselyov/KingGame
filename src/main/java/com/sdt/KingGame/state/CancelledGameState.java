package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;

import java.util.List;

public class CancelledGameState extends GameState {
    private final Integer cancelledBy;

    public CancelledGameState(List<Player> players, Deck deck) {
        super(players, deck);
        state = "cancelled";
        cancelledBy = players.get(0).getId();
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }
}
