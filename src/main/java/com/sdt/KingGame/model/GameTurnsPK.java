package com.sdt.KingGame.model;

import java.io.Serializable;

public class GameTurnsPK implements Serializable {
    private final Long gameSessionId;
    private Integer gameNumber = 0;
    private Integer circleNumber = 0;
    private Integer playerId = 0;

    public GameTurnsPK(Long gameSessionId, Integer gameNumber, Integer circleNumber, Integer playerId) {
        this.gameSessionId = gameSessionId;
        this.gameNumber = gameNumber;
        this.circleNumber = circleNumber;
        this.playerId = playerId;
    }

    public GameTurnsPK(Long gameSessionId) {
        this.gameSessionId = gameSessionId;
    }

    public Long getGameSessionId() {
        return gameSessionId;
    }

    public void setGameNumber(Integer gameNumber) {
        this.gameNumber = gameNumber;
    }

    public void setCircleNumber(Integer circleNumber) {
        this.circleNumber = circleNumber;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
