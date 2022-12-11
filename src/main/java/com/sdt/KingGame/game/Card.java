package com.sdt.KingGame.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class Card {
    private Suits suit;
    private Integer magnitude;
    private static final Logger LOGGER = LoggerFactory.getLogger(Card.class);

    public Card(Suits suit, Integer magnitude) {
        setMagnitude(magnitude);
        setSuit(suit);
    }

    public Suits getSuit() {
        return suit;
    }

    public int getMagnitude() {
        return magnitude;
    }

    private void setSuit(Suits suit) {
        List<Suits> validSuits = getValidSuits();
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

    public static List<Suits> getValidSuits() {
        return Arrays.asList(Suits.HEARTS, Suits.CLUBS, Suits.DIAMONDS, Suits.SPADES);
    }
}
