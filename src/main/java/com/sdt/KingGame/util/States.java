package com.sdt.KingGame.util;

public enum States {
    STARTED("started"),
    PAUSED("paused"),
    CANCELLED("cancelled"),
    FINISHED("finished");

    private final String name;

    States(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
