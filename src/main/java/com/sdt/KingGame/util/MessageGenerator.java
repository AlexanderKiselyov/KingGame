package com.sdt.KingGame.util;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.state.CancelledGameState;
import com.sdt.KingGame.state.FinishedGameState;
import com.sdt.KingGame.state.GameState;
import com.sdt.KingGame.state.PausedGameState;
import com.sdt.KingGame.state.StartedGameState;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collects data for sending to the clients and sends it
 */
public class MessageGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageGenerator.class);

    public MessageGenerator() {
    }

    public void generateMessage(GameSession gameSession) throws IOException {
        GameState state = gameSession.getState();
        Map<Player, List<Card>> playersWithCards = state.getPlayersWithCards();
        for (Player player : playersWithCards.keySet()) {
            JSONObject message = new JSONObject();
            message.put("game_session_id", gameSession.getGameSessionId());
            JSONObject gameState = new JSONObject();
            switch (state) {
                case StartedGameState startedGameState ->
                        startedState(state, playersWithCards, player, gameState, startedGameState);
                case PausedGameState pausedGameState -> pausedState(state, gameState, pausedGameState);
                case CancelledGameState cancelledGameState -> cancelledState(state, gameState, cancelledGameState);
                case FinishedGameState finishedGameState -> finishedState(gameState, finishedGameState);
                default -> LOGGER.error("Unexpected value: " + state.state);
            }
            JSONArray playersJson = new JSONArray();
            List<Player> doublePlayers = new ArrayList<>(playersWithCards.keySet());
            int playersNum = doublePlayers.size();
            doublePlayers.addAll(playersWithCards.keySet());
            int initialPosition = doublePlayers.indexOf(player);
            for (int i = 0; i < playersNum; i++) {
                JSONObject currentPlayerJson = new JSONObject();
                Player currentPlayer = doublePlayers.get(initialPosition);
                currentPlayerJson.put("player_id", currentPlayer.getId());
                currentPlayerJson.put("player_name", currentPlayer.getName());
                currentPlayerJson.put("points", currentPlayer.getPoints());
                initialPosition++;
                playersJson.put(currentPlayerJson);
            }
            gameState.put("players", playersJson);
            message.put("game_state", gameState);
            player.getSession().sendMessage(new TextMessage(message.toString(4)));
        }
    }

    private void startedState(GameState state, Map<Player, List<Card>> playersWithCards, Player player, JSONObject gameState, StartedGameState startedGameState) {
        gameState.put("state", "started");
        gameState.put("started_by", startedGameState.getStartedBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
        gameState.put("player_turn", state.getPlayerTurn());
        JSONArray cards = new JSONArray();
        List<Card> playerCards = playersWithCards.get(player);
        for (Card card : playerCards) {
            JSONObject currentCard = new JSONObject();
            currentCard.put("suit", card.getSuit());
            currentCard.put("magnitude", card.getMagnitude());
            cards.put(currentCard);
        }
        gameState.put("cards", cards);
    }

    private void pausedState(GameState state, JSONObject gameState, PausedGameState pausedGameState) {
        gameState.put("state", "paused");
        gameState.put("paused_by", pausedGameState.getPausedBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
    }

    private void cancelledState(GameState state, JSONObject gameState, CancelledGameState cancelledGameState) {
        gameState.put("state", "cancelled");
        gameState.put("cancelled_by", cancelledGameState.getCancelledBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
    }

    private void finishedState(JSONObject gameState, FinishedGameState finishedGameState) {
        gameState.put("state", "finished");
        gameState.put("winner", finishedGameState.getWinner());
    }
}
