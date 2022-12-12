package com.sdt.KingGame.webSocket;

import com.sdt.KingGame.game.GameSession;
import com.sdt.KingGame.game.Player;
import com.sdt.KingGame.util.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Set;

public class DisconnectListener {
    private final MessageGenerator messageGenerator = new MessageGenerator();
    private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectListener.class);

    public DisconnectListener() {
    }

    public void handle(WebSocketSession session, List<GameSession> gameSessions) {
        try {
            for (GameSession gameSession : gameSessions) {
                Set<Player> players = gameSession.getState().getPlayersWithCards().keySet();
                for (Player player : players) {
                    if (player.getSession() == session) {
                        gameSession.setCancelledOrPausedState(player.getId());
                        if (session != null) {
                            messageGenerator.generateMessage(gameSession);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot handle message. Error: " + e);
        }
    }
}
