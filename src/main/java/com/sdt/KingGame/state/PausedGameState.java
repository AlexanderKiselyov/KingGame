package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.util.States;

import java.util.List;

/**
 * Состояние игры на паузе
 */
public class PausedGameState extends GameState {
    /**
     * ID игрока, поставившего игру на паузу или отключившегося от игры
     */
    private Integer pausedBy;

    public PausedGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.PAUSED;
        pausedBy = players.get(0).getId();
    }

    public void setPausedBy(Integer pausedBy) {
        this.pausedBy = pausedBy;
    }

    public Integer getPausedBy() {
        return pausedBy;
    }
}
