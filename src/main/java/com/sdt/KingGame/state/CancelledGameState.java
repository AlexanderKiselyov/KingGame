package com.sdt.KingGame.state;

import com.sdt.KingGame.util.States;

/**
 * Состояние отменной игры
 */
public class CancelledGameState extends GameState {
    /**
     * ID игрока, отменившего игру
     */
    private Integer cancelledBy;

    public CancelledGameState(GameState state) {
        super(state.getTurnsPK());
        this.gameNumber = state.gameNumber;
        this.circleNumber = state.circleNumber;
        this.playerNumTurn = state.playerNumTurn;
        this.playersWithCards = state.playersWithCards;
        this.doublePlayers = state.doublePlayers;
        this.playerTurn = state.playerTurn;
        this.lastStartedCirclePlayer = state.playerTurn;
        this.state = States.CANCELLED;
        cancelledBy = state.getPlayersWithCards().keySet().iterator().next().getId();
    }

    public void setCancelledBy(Integer cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }
}
