package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;

import java.util.List;

public class FinishedGameState extends GameState {
    private final Integer winner;

    public FinishedGameState(List<Player> players, Deck deck) {
        super(players, deck);
        state = "finished";
        winner = players.get(0).getId();
    }

    public Integer getWinner() {
        return winner;
    }
}
