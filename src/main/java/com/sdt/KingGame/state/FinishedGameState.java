package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;

import java.util.List;

public class FinishedGameState extends GameState {
    private final Integer winner;

    public FinishedGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.FINISHED;
        winner = players.get(0).getId();
    }

    public Integer getWinner() {
        return winner;
    }
}
