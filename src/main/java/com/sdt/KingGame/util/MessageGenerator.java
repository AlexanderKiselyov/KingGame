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
import org.springframework.web.socket.WebSocketSession;

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
            if (state instanceof StartedGameState) {
                startedState(playersWithCards, player, gameState, (StartedGameState) state);
            } else if (state instanceof PausedGameState) {
                pausedState(gameState, (PausedGameState) state);
            } else if (state instanceof CancelledGameState) {
                cancelledState(gameState, (CancelledGameState) state);
            } else if (state instanceof FinishedGameState) {
                finishedState(gameState, (FinishedGameState) state);
            } else {
                LOGGER.error("Unexpected value: " + state.getStateValue());
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
            if (player.getSession().isOpen()) {
                player.getSession().sendMessage(new TextMessage(message.toString(4)));
            }
        }
    }

    public void generateCancelledMessage(WebSocketSession session) throws IOException {
        JSONObject message = new JSONObject();
        JSONObject gameState = new JSONObject();
        gameState.put("state", States.CANCELLED.getName());
        message.put("game_state", gameState);
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(message.toString(4)));
        }
    }

    private void startedState(Map<Player, List<Card>> playersWithCards, Player player, JSONObject gameState, StartedGameState state) {
        gameState.put("state", States.STARTED.getName());
        gameState.put("started_by", state.getStartedBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
        gameState.put("player_turn", state.getPlayerTurn());
        JSONArray cards = new JSONArray();
        List<Card> playerCards = playersWithCards.get(player);
        for (Card card : playerCards) {
            JSONObject currentCard = new JSONObject();
            currentCard.put("suit", card.getSuit().getName());
            currentCard.put("magnitude", card.getMagnitude());
            cards.put(currentCard);
        }
        gameState.put("cards", cards);
        JSONArray bribe = new JSONArray();
        for (Card bribeCard : state.getBribeCards()) {
            JSONObject currentCard = new JSONObject();
            currentCard.put("suit", bribeCard.getSuit().getName());
            currentCard.put("magnitude", bribeCard.getMagnitude());
            bribe.put(currentCard);
        }
        gameState.put("bribe", bribe);
    }

    private void pausedState(JSONObject gameState, PausedGameState state) {
        gameState.put("state", States.PAUSED.getName());
        gameState.put("paused_by", state.getPausedBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
    }

    private void cancelledState(JSONObject gameState, CancelledGameState state) {
        gameState.put("state", States.CANCELLED.getName());
        gameState.put("cancelled_by", state.getCancelledBy());
        gameState.put("game_num", state.getGameNumber());
        gameState.put("circle_num", state.getCircleNumber());
    }

    private void finishedState(JSONObject gameState, FinishedGameState state) {
        gameState.put("state", States.FINISHED.getName());
        gameState.put("winner", state.getWinner());
    }
}
