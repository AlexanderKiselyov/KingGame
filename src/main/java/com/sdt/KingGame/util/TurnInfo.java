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
        for (Suits suits : Suits.values()) {
            if (suits.getName().equals(suit)) {
                return suits;
            }
        }
        return null;
    }

    public Integer getMagnitude() {
        return magnitude;
    }
}
