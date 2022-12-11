package com.sdt.KingGame.game;

public enum Suits {
    HEARTS("hearts"),
    CLUBS("clubs"),
    DIAMONDS("diamonds"),
    SPADES("spades");

    private final String name;

    Suits(String name) {
        this.name = name;
    }
}
