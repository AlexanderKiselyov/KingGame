package com.sdt.KingGame.state;

import com.sdt.KingGame.util.States;

/**
 * Состояние завершенной игры
 */
public class FinishedGameState extends GameState {
    /**
     * ID победителя игры
     */
    private Integer winner;

    public FinishedGameState(GameState state) {
        super(state.getTurnsPK());
        this.gameNumber = state.gameNumber;
        this.circleNumber = state.circleNumber;
        this.playerNumTurn = state.playerNumTurn;
        this.playersWithCards = state.playersWithCards;
        this.doublePlayers = state.doublePlayers;
        this.playerTurn = state.playerTurn;
        this.lastStartedCirclePlayer = state.playerTurn;
        this.state = States.FINISHED;
        winner = state.getPlayersWithCards().keySet().iterator().next().getId();
    }

    public void setWinner(Integer winner) {
        this.winner = winner;
    }

    public Integer getWinner() {
        return winner;
    }
}
