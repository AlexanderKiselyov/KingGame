package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.util.States;

import java.util.List;

/**
 * Состояние завершенной игры
 */
public class FinishedGameState extends GameState {
    /**
     * ID победителя игры
     */
    private Integer winner;

    public FinishedGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.FINISHED;
        winner = players.get(0).getId();
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getWinner() {
        return winner;
    }
}
