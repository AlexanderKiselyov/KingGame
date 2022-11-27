package com.sdt.KingGame.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Card {
    private String suit;
    private Integer magnitude;
    private static final Logger LOGGER = LoggerFactory.getLogger(Card.class);

    public Card(String suit, Integer magnitude) {
        setMagnitude(magnitude);
        setSuit(suit);
    }

    public String getSuit() {
        return suit;
    }

    public int getMagnitude() {
        return magnitude;
    }

    private void setSuit(String suit) {
        List<String> validSuits = getValidSuits();
        if (validSuits.contains(suit)) {
            this.suit = suit;
        } else {
            LOGGER.error("Valid suits are: " + validSuits);
        }
    }

    private void setMagnitude(Integer magnitude) {
        List<Integer> validMagnitudes = getValidMagnitude();
        if (validMagnitudes.contains(magnitude)) {
            this.magnitude = magnitude;
        } else {
            LOGGER.error("Valid magnitudes are: " + validMagnitudes);
        }
    }

    public static List<Integer> getValidMagnitude() {
        return Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14);
    }

    public static List<String> getValidSuits() {
        return Arrays.asList("hearts", "clubs", "diamonds", "spades");
    }
}
