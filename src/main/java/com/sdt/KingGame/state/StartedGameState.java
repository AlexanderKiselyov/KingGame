package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.util.States;

import java.util.List;

/**
 * Состояние игры в процессе самой игры
 */
public class StartedGameState extends GameState {
    /**
     * ID игрока, создавшего игру
     */
    private Integer startedBy;

    public StartedGameState(List<Player> players, Deck deck, GameTurnsPK gameTurnsPK) {
        super(players, deck, gameTurnsPK);
        state = States.STARTED;
        startedBy = players.get(0).getId();
    }

    public StartedGameState(GameState state) {
        super(state.getTurnsPK());
        this.gameNumber = state.gameNumber;
        this.circleNumber = state.circleNumber;
        this.playerNumTurn = state.playerNumTurn;
        this.playersWithCards = state.playersWithCards;
        this.doublePlayers = state.doublePlayers;
        this.playerTurn = state.playerTurn;
        this.state = States.STARTED;
        startedBy = state.getPlayersWithCards().keySet().iterator().next().getId();
    }

    public void setStartedBy(Integer startedBy) {
        this.startedBy = startedBy;
    }

    public Integer getStartedBy() {
        return startedBy;
    }
}
