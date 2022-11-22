package com.sdt.KingGame.game;

import java.util.Arrays;
import java.util.List;

public class Card {
    private String suit;
    private int magnitude;

    public Card(String suit, int magnitude) {
        setMagnitude(magnitude);
        setSuit(suit);
    }

    private String getSuit() {
        return suit;
    }

    private int getMagnitude() {
        return magnitude;
    }

    private void setSuit(String suit) {
        List<String> validSuits = getValidSuits();
        if (validSuits.contains(suit)) {
            this.suit = suit;
        } else {
            throw new IllegalArgumentException("Valid suits are: " + validSuits);
        }
    }

    private void setMagnitude(int magnitude) {
        List<Integer> validMagnitudes = getValidMagnitude();
        if (validMagnitudes.contains(magnitude)) {
            this.magnitude = magnitude;
        } else {
            throw new IllegalArgumentException("Valid magnitudes are: " + validMagnitudes);
        }
    }

    public static List<Integer> getValidMagnitude() {
        return Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14);
    }

    public static List<String> getValidSuits() {
        return Arrays.asList("hearts", "clubs", "diamonds", "spades");
    }
}
