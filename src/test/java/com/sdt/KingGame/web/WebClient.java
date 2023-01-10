package com.sdt.KingGame.web;

import com.sdt.KingGame.game.Card;
import com.sdt.KingGame.util.States;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A web client for server functionality testing
 */
public class WebClient {
    private static final int CLIENTS_COUNT = 4;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Client> clients = new ArrayList<>(CLIENTS_COUNT);
        for (int i = 1; i <= CLIENTS_COUNT; i++) {
            clients.add(new Client("client" + i));
        }

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(() -> {
            for (Client client : clients) {
                String lastMessage = client.getLastMessage();
                try {
                    JSONObject message = new JSONObject(lastMessage);
                    if (!message.isNull("session_id")) {
                        String playMessage = "{\n" +
                                            "    \"session_id\" : " + client.getSessionId() + ",\n" +
                                            "    \"player_name\" : \"" + client.getName() + "\",\n" +
                                            "    \"action\" : \"play\"\n" +
                                            "}";
                        client.sendMessage(playMessage);
                    } else if (!message.isNull("game_state") &&
                            !Objects.equals(message.getJSONObject("game_state").getString("state"), States.FINISHED.getName()) &&
                            message.getJSONObject("game_state").getInt("player_turn") == client.getPlayerId() &&
                            client.getState().getIsTurn()) {
                        Card turnCard = client.getCard();
                        String turnMessage = "{\n" +
                                            "    \"game_session_id\" : " + client.getGameSessionId() + ",\n" +
                                            "    \"action\" : \"turn\",\n" +
                                            "    \"game_state\" : {\n" +
                                            "        \"game_num\" : " + client.getState().getGameNum() + ",\n" +
                                            "        \"circle_num\" : " + client.getState().getCircleNum() + "\n" +
                                            "     },\n" +
                                            "    \"player_id\" : " + client.getPlayerId() + ",\n" +
                                            "    \"turn\" : {\n" +
                                            "        \"suit\" : " + turnCard.getSuit().getName() + ",\n" +
                                            "        \"magnitude\" : " + turnCard.getMagnitude() + "\n" +
                                            "    }" +
                                            "}";
                        client.sendMessage(turnMessage);
                        client.setLastMessageNull();
                        client.getState().setIsTurn(false);
                    } else if (!message.isNull("game_state") &&
                            Objects.equals(message.getJSONObject("game_state").getString("state"), States.FINISHED.getName())) {
                        for (Client pointClient : clients) {
                            System.out.println(pointClient.getName() + " - " + pointClient.getPoints());
                        }
                        service.awaitTermination(1, TimeUnit.SECONDS);
                        service.shutdown();
                        for (Client closeClient : clients) {
                            closeClient.closeSession();
                        }
                    }
                    for (Client otherClients : clients) {
                        if (otherClients != client) {
                            otherClients.getState().setIsTurn(true);
                        }
                    }
                } catch (JSONException e) {
                    System.out.println("Cannot convert string to JSON: " + e);
                } catch (IOException e) {
                    System.out.println("Cannot send message: " + e);
                } catch (InterruptedException e) {
                    System.out.println("Executor was interrupted: " + e);
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }
}
