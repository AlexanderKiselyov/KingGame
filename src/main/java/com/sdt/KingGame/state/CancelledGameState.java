package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;

import java.util.List;

/**
 * Состояние отменной игры
 */
public class CancelledGameState extends GameState {
    /**
     * ID игрока, отменившего игру
     */
    private Integer cancelledBy;

    public CancelledGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.CANCELLED;
        cancelledBy = players.get(0).getId();
    }

    public void setCancelledBy(Integer cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }
}
