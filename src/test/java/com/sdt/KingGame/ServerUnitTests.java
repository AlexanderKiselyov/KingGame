package com.sdt.KingGame;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ServerUnitTests {
    private static final int CLIENTS_COUNT = 4;

    @Test
    void deckTest() {
        Deck deck = new Deck();
        List<Card> cards = new ArrayList<>();
        Card card = deck.dealTopCard();
        while (card != null) {
            cards.add(card);
            card = deck.dealTopCard();
        }
        assertThat(cards.size()).isEqualTo(Card.getValidMagnitude().size() * Card.getValidSuits().size());
        for (Card deckCard : cards) {
            assertThat(deckCard.getSuit()).isIn(Card.getValidSuits());
            assertThat(deckCard.getMagnitude()).isIn(Card.getValidMagnitude());
        }
    }

    @Test
    void gameSessionTest() {
        List<Player> players = new ArrayList<>(CLIENTS_COUNT);
        for (int i = 1; i <= CLIENTS_COUNT; i++) {
            players.add(new Player(i, "player" + i, null));
        }
        GameSession gameSession = new GameSession(1L, players);
        Integer startedBy = 1;
        gameSession.setStartedState(startedBy);
        assertThat(gameSession.getState()).hasFieldOrPropertyWithValue("startedBy", startedBy);
        Integer cancelledBy = 2;
        gameSession.setCancelledState(cancelledBy);
        assertThat(gameSession.getState()).hasFieldOrPropertyWithValue("cancelledBy", cancelledBy);
        gameSession.setFinishedState();
        assertThat(gameSession.getState()).hasFieldOrProperty("winner");
    }
}
