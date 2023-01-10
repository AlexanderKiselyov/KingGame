package com.sdt.KingGame.web;

public class State {
    private Integer game_num;
    private Integer circle_num;
    private Boolean isTurn = true;

    public Integer getGameNum() {
        return game_num;
    }

    public void setGameNum(Integer gameNum) {
        game_num = gameNum;
    }

    public Integer getCircleNum() {
        return circle_num;
    }

    public void setCircleNum(Integer circleNum) {
        circle_num = circleNum;
    }

    public Boolean getIsTurn() {
        return isTurn;
    }

    public void setIsTurn(Boolean turn) {
        isTurn = turn;
    }
}
