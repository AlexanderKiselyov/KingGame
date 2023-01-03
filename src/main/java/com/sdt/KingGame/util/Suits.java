package com.sdt.KingGame.util;

public enum Suits {
    HEARTS("hearts"),
    CLUBS("clubs"),
    DIAMONDS("diamonds"),
    SPADES("spades");

    private final String name;

    Suits(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Suits getSuit(String suit) {
        for (Suits suits : Suits.values()) {
            if (suits.getName().equals(suit)) {
                return suits;
            }
        }
        return null;
    }
}
