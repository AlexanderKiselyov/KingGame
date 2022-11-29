package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameState {
    private final static Integer MAX_CARDS_NUMBER = 8;
    protected String state = "";
    private final Integer gameNumber;
    private final Integer circleNumber;
    private final Integer playerTurn;
    private final Map<Player, List<Card>> playersWithCards;

    public GameState(List<Player> players, Deck deck) {
        gameNumber = 1;
        circleNumber = 1;
        playersWithCards = new HashMap<>();
        for (Player player : players) {
            List<Card> currentPlayerCards = new ArrayList<>();
            for (int i = 0; i < MAX_CARDS_NUMBER; i++) {
                currentPlayerCards.add(deck.dealTopCard());
            }
            playersWithCards.put(player, currentPlayerCards);
        }
        playerTurn = randomTurn(players);
    }

    private Integer randomTurn(List<Player> players) {
        return players.get(new Random().nextInt(players.size())).getId();
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public Integer getCircleNumber() {
        return circleNumber;
    }

    public Integer getPlayerTurn() {
        return playerTurn;
    }

    public Map<Player, List<Card>> getPlayersWithCards() {
        return playersWithCards;
    }

    public String getStateValue() {
        return state;
    }

    public void changeState(int playerId, String suit, int magnitude) {
        // TODO логика изменения состояния
    }
}
