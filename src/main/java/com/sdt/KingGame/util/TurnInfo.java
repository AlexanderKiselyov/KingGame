package com.sdt.KingGame.util;

public class TurnInfo {
    private final Integer playerId;
    private final String suit;
    private final Integer magnitude;

    public TurnInfo(Integer playerId, String suit, Integer magnitude) {
        this.playerId = playerId;
        this.suit = suit;
        this.magnitude = magnitude;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Suits getSuit() {
        return Suits.valueOf(suit);
    }

    public Integer getMagnitude() {
        return magnitude;
    }
}
