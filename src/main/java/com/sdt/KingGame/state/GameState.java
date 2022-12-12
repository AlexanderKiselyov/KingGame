package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.game.Suits;
import com.sdt.KingGame.model.GameTurns;
import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.repository.TurnRepository;
import com.sdt.KingGame.util.TurnInfo;
import com.sdt.KingGame.webSocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Общее состояние игры
 */
public class GameState {
    private final static Integer MAX_CARDS_NUMBER = 8;
    private final static Integer GAMES_COUNT = 12;
    /**
     * Текущее состояние игры
     */
    protected States state = States.STARTED;
    /**
     * Номер игры
     */
    private Integer gameNumber;
    /**
     * Номер круга в рамках одной игры
     */
    private Integer circleNumber;
    /**
     * Номер id ходящего игрока
     */
    private Integer playerTurn;
    /**
     * Порядковый номер ходящего игрока в рамках одного круга
     */
    private Integer playerNumTurn;
    /**
     * Карта масти
     */
    private String suitCard;
    private final Map<Player, List<Card>> playersWithCards;
    private List<Player> doublePlayers;
    private TurnRepository turns;
    private final GameTurnsPK turnsPK;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

    public GameState(List<Player> players, Deck deck, GameTurnsPK turnsPK) {
        gameNumber = 1;
        circleNumber = 1;
        playerNumTurn = 1;
        playersWithCards = new HashMap<>();
        for (Player player : players) {
            List<Card> currentPlayerCards = new ArrayList<>();
            for (int i = 0; i < MAX_CARDS_NUMBER; i++) {
                currentPlayerCards.add(deck.dealTopCard());
            }
            playersWithCards.put(player, currentPlayerCards);
        }
        playerTurn = randomTurn(players);
        this.turnsPK = turnsPK;
        setDoublePlayers();
    }

    private Integer randomTurn(List<Player> players) {
        return players.get(new Random().nextInt(players.size())).getId();
    }

    private Integer nextTurn(Player player) {
        return doublePlayers.get(doublePlayers.indexOf(player) + 1).getId();
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

    public States getStateValue() {
        return state;
    }

    public void changeState(int playerId, String suit, int magnitude) {
        turns.save(new GameTurns(turnsPK.getGameSessionId(), gameNumber, circleNumber, playerId, suit, magnitude));

        if (Objects.equals(playerNumTurn, WebSocketHandler.getPlayersCount())) {
            changePlayerPoints();
            if (gameNumber > GAMES_COUNT) {
                state = States.FINISHED;
                return;
            }
            if (Objects.equals(circleNumber, MAX_CARDS_NUMBER)) {
                gameNumber++;
                circleNumber = 1;
            } else {
                circleNumber++;
            }
            playerNumTurn = 1;
        } else {
            if (playerNumTurn++ == 0) {
                suitCard = suit;
            }
        }
        changeCurrentPlayerAndCards(playerId, suit, magnitude);
    }

    private Player getPlayerById(int playerId) {
        for (Player player : playersWithCards.keySet()) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }

    private void changeCurrentPlayerAndCards(int playerId, String suit, int magnitude) {
        Player currentPlayer = getPlayerById(playerId);
        List<Card> currentCards = playersWithCards.get(currentPlayer);
        currentCards.remove(new Card(Suits.valueOf(suit), magnitude));
        playersWithCards.put(currentPlayer, currentCards);
        playerTurn = nextTurn(currentPlayer);
    }

    private void changePlayerPoints() {
        turnsPK.setGameNumber(gameNumber);
        turnsPK.setCircleNumber(circleNumber);
        List<GameTurnsPK> keys = new ArrayList<>();
        List<TurnInfo> playerTurns = new ArrayList<>();
        int i = 0;
        for (Player player : playersWithCards.keySet()) {
            keys.add(turnsPK);
            keys.get(i).setPlayerId(player.getId());
            if (turns.findById(keys.get(i)).isPresent()) {
                playerTurns.add(turns.findById(keys.get(i)).get().getTurnInfo());
            } else {
                LOGGER.error("Cannot get database table entry.");
            }
            i++;
        }
        List<Card> bribeCards = new ArrayList<>();
        int bribeIndex = 0;
        int maxMagnitude = 0;
        i = 0;
        for (TurnInfo turnInfo : playerTurns) {
            if (Objects.equals(turnInfo.getSuit(), suitCard) && turnInfo.getMagnitude() > maxMagnitude) {
                maxMagnitude = turnInfo.getMagnitude();
                bribeIndex = i;
            }
            bribeCards.add(new Card(Suits.valueOf(turnInfo.getSuit()), turnInfo.getMagnitude()));
            i++;
        }
        Player bribeTaker = getPlayerById(playerTurns.get(bribeIndex).getPlayerId());
        assert bribeTaker != null;
        recalculatePlayerPoints(bribeTaker, bribeCards);
    }

    private void recalculatePlayerPoints(Player bribeTaker, List<Card> bribeCards) {
        int currentBribeTakerPoints = bribeTaker.getPoints();
        switch (gameNumber) {
            case 1 -> bribeTaker.setPoints(currentBribeTakerPoints - 2);
            case 2 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 4);
                    }
                }
            }
            case 3 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getMagnitude() == 12) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 4);
                    }
                }
            }
            case 4 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getSuit() == Suits.HEARTS) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 2);
                    }
                }
            }
            case 5 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getSuit() == Suits.HEARTS && bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 16);
                    }
                }
            }
            case 6 -> {
                if (Objects.equals(circleNumber, MAX_CARDS_NUMBER) || Objects.equals(circleNumber, MAX_CARDS_NUMBER - 1)) {
                    bribeTaker.setPoints(currentBribeTakerPoints - 8);
                }
            }
            case 7 -> bribeTaker.setPoints(currentBribeTakerPoints + 2);
            case 8 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 4);
                    }
                }
            }
            case 9 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getMagnitude() == 12) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 4);
                    }
                }
            }
            case 10 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getSuit() == Suits.HEARTS) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 2);
                    }
                }
            }
            case 11 -> {
                for (Card bribeCard : bribeCards) {
                    if (bribeCard.getSuit() == Suits.HEARTS && bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 16);
                    }
                }
            }
            case 12 -> {
                if (Objects.equals(circleNumber, MAX_CARDS_NUMBER) || Objects.equals(circleNumber, MAX_CARDS_NUMBER - 1)) {
                    bribeTaker.setPoints(currentBribeTakerPoints + 8);
                }
            }
            default -> LOGGER.error("Wrong game number.");
        }
    }

    public void setDoublePlayers() {
        doublePlayers = new ArrayList<>(playersWithCards.keySet());
        doublePlayers.addAll(playersWithCards.keySet());
    }
}
