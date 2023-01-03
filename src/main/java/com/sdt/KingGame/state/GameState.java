package com.sdt.KingGame.state;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.game.Deck;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.util.States;
import com.sdt.KingGame.util.Suits;
import com.sdt.KingGame.model.GameTurnsPK;
import com.sdt.KingGame.util.TurnInfo;
import com.sdt.KingGame.webSocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    Integer gameNumber;
    /**
     * Номер круга в рамках одной игры
     */
    Integer circleNumber;
    /**
     * Номер id ходящего игрока
     */
    Integer playerTurn;
    /**
     * Порядковый номер ходящего игрока в рамках одного круга
     */
    Integer playerNumTurn;
    /**
     * Карта масти
     */
    private Suits suitCard;
    /**
     * Список карт во взятке на текущем круге
     */
    private final List<Card> bribe;
    Map<Player, List<Card>> playersWithCards;
    List<Player> doublePlayers;

    public GameTurnsPK getTurnsPK() {
        return turnsPK;
    }

    private final GameTurnsPK turnsPK;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

    public GameState(List<Player> players, Deck deck, GameTurnsPK turnsPK) {
        gameNumber = 1;
        circleNumber = 1;
        playerNumTurn = 1;
        playersWithCards = new LinkedHashMap<>();
        handOutCards(players, deck);
        playerTurn = randomTurn(players);
        this.turnsPK = turnsPK;
        bribe = new ArrayList<>();
    }

    public GameState(GameTurnsPK turnsPK) {
        bribe = new ArrayList<>();
        this.turnsPK = turnsPK;
        playersWithCards = new LinkedHashMap<>();
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

    public List<Card> getBribeCards() {
        return bribe;
    }

    public void changeState(int playerId, String suit, int magnitude, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO turns(game_session_id, game_number, circle_number, player_id, suit, magnitude) VALUES (" + turnsPK.getGameSessionId() + ", " + gameNumber + ", " + circleNumber + ", " + playerId + ", '" + suit + "', " + magnitude + ")");

        if (Objects.equals(playerNumTurn, WebSocketHandler.getPlayersCount())) {
            bribe.add(new Card(Suits.getSuit(suit), magnitude));
            changePlayerPoints(connection);
            if (gameNumber > GAMES_COUNT) {
                state = States.FINISHED;
                return;
            }
            if (Objects.equals(circleNumber, MAX_CARDS_NUMBER)) {
                gameNumber++;
                circleNumber = 1;
                handOutCards(playersWithCards.keySet().stream().toList(), new Deck());
                bribe.clear();
            } else {
                circleNumber++;
            }
            playerNumTurn = 1;
        } else {
            if (playerNumTurn++ == 1) {
                suitCard = Suits.getSuit(suit);
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
        currentCards.remove(new Card(Suits.getSuit(suit), magnitude));
        playersWithCards.put(currentPlayer, currentCards);
        playerTurn = nextTurn(currentPlayer);
    }

    private void changePlayerPoints(Connection connection) throws SQLException {
        turnsPK.setGameNumber(gameNumber);
        turnsPK.setCircleNumber(circleNumber);
        List<GameTurnsPK> keys = new ArrayList<>();
        List<TurnInfo> playerTurns = new ArrayList<>();
        int i = 0;
        Statement statement = connection.createStatement();
        for (Player player : playersWithCards.keySet()) {
            keys.add(turnsPK);
            GameTurnsPK gameTurnPK = keys.get(i);
            gameTurnPK.setPlayerId(player.getId());
            ResultSet result = statement.executeQuery("SELECT * FROM turns WHERE game_session_id = " + gameTurnPK.getGameSessionId() + " AND game_number = " + gameTurnPK.getGameNumber() + " AND circle_number = " + gameTurnPK.getCircleNumber() + " AND player_id = " + gameTurnPK.getPlayerId());
            while (result.next()) {
                playerTurns.add(new TurnInfo(result.getInt("player_id"), result.getString("suit"), result.getInt("magnitude")));
            }
            i++;
        }
        int bribeIndex = 0;
        int maxMagnitude = 0;
        i = 0;
        for (TurnInfo turnInfo : playerTurns) {
            if (Objects.equals(turnInfo.getSuit(), suitCard) && turnInfo.getMagnitude() > maxMagnitude) {
                maxMagnitude = turnInfo.getMagnitude();
                bribeIndex = i;
            }
            i++;
        }
        Player bribeTaker = getPlayerById(playerTurns.get(bribeIndex).getPlayerId());
        assert bribeTaker != null;
        recalculatePlayerPoints(bribeTaker);
    }

    private void recalculatePlayerPoints(Player bribeTaker) {
        int currentBribeTakerPoints = bribeTaker.getPoints();
        switch (gameNumber) {
            case 1 -> bribeTaker.setPoints(currentBribeTakerPoints - 2);
            case 2 -> {
                for (Card bribeCard : bribe) {
                    if (bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 4);
                    }
                }
            }
            case 3 -> {
                for (Card bribeCard : bribe) {
                    if (bribeCard.getMagnitude() == 12) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 4);
                    }
                }
            }
            case 4 -> {
                for (Card bribeCard : bribe) {
                    if (bribeCard.getSuit() == Suits.HEARTS) {
                        bribeTaker.setPoints(currentBribeTakerPoints - 2);
                    }
                }
            }
            case 5 -> {
                for (Card bribeCard : bribe) {
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
                for (Card bribeCard : bribe) {
                    if (bribeCard.getMagnitude() == 13) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 4);
                    }
                }
            }
            case 9 -> {
                for (Card bribeCard : bribe) {
                    if (bribeCard.getMagnitude() == 12) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 4);
                    }
                }
            }
            case 10 -> {
                for (Card bribeCard : bribe) {
                    if (bribeCard.getSuit() == Suits.HEARTS) {
                        bribeTaker.setPoints(currentBribeTakerPoints + 2);
                    }
                }
            }
            case 11 -> {
                for (Card bribeCard : bribe) {
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

    void handOutCards(List<Player> players, Deck deck) {
        playersWithCards.clear();
        for (Player player : players) {
            List<Card> currentPlayerCards = new ArrayList<>();
            for (int i = 0; i < MAX_CARDS_NUMBER; i++) {
                currentPlayerCards.add(deck.dealTopCard());
            }
            playersWithCards.put(player, currentPlayerCards);
        }
        setDoublePlayers();
    }

    public void setDoublePlayers() {
        doublePlayers = new ArrayList<>(playersWithCards.keySet());
        doublePlayers.addAll(playersWithCards.keySet());
    }
}
