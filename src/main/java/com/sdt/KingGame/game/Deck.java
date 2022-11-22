package com.sdt.KingGame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        createNewDeck();
    }

    private void createNewDeck() {
        List<String> suits = Card.getValidSuits();
        List<Integer> magnitudes = Card.getValidMagnitude();
        for (String suit : suits) {
            for (Integer magnitude : magnitudes) {
                deck.add(new Card(suit, magnitude));
            }
        }
        shuffle();
    }

    private Card dealTopCard() {
        if (deck.size() > 0) {
            return deck.remove(0);
        }
        else {
            return null;
        }
    }

    private void shuffle() {
        Collections.shuffle(deck);
    }
}
