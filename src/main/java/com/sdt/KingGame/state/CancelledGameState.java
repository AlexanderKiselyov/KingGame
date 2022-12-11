package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;

import java.util.List;

public class CancelledGameState extends GameState {
    private final Integer cancelledBy;

    public CancelledGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.CANCELLED;
        cancelledBy = players.get(0).getId();
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }
}
