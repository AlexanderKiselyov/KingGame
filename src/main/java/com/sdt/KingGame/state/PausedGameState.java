package com.sdt.KingGame.state;

import com.sdt.KingGame.util.States;

/**
 * Состояние игры на паузе
 */
public class PausedGameState extends GameState {
    /**
     * ID игрока, поставившего игру на паузу или отключившегося от игры
     */
    private Integer pausedBy;

    public PausedGameState(GameState state) {
        super(state.getTurnsPK());
        this.gameNumber = state.gameNumber;
        this.circleNumber = state.circleNumber;
        this.playerNumTurn = state.playerNumTurn;
        this.playersWithCards = state.playersWithCards;
        this.doublePlayers = state.doublePlayers;
        this.playerTurn = state.playerTurn;
        this.lastStartedCirclePlayer = state.playerTurn;
        this.state = States.PAUSED;
        pausedBy = state.getPlayersWithCards().keySet().iterator().next().getId();
    }

    public void setPausedBy(Integer pausedBy) {
        this.pausedBy = pausedBy;
    }

    public Integer getPausedBy() {
        return pausedBy;
    }
}
