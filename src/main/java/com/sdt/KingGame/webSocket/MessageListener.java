package com.sdt.KingGame.webSocket;

import com.badlogic.gdx.utils.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdt.KingGame.model.Client;
import com.sdt.KingGame.repository.ClientRepository;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class MessageListener {
    public MessageListener() {}
    public void handle(WebSocketSession session, JsonValue jsonValue, ClientRepository clientRepository) throws IOException {
        String method = jsonValue.getString("method");
        switch (method) {
            case "add" -> {
                Client client = new Client();
                client.setName(jsonValue.getString("name"));
                client.setEmail(jsonValue.getString("email"));
                client.setPhone(jsonValue.getString("phone"));
                clientRepository.save(client);
                session.sendMessage(new TextMessage("Client with name " + jsonValue.getString("name") + "was created."));
            }
            case "all" -> {
                final Iterable<Client> clients = clientRepository.findAll();
                String allClients = new ObjectMapper().writeValueAsString(clients);
                session.sendMessage(new TextMessage(allClients));
            }
            case "delete" -> {
                int id = jsonValue.getInt("id");
                if (clientRepository.existsById(id)) {
                    clientRepository.deleteById(id);
                    session.sendMessage(new TextMessage("Client with id " + id + "was deleted."));
                }
                session.sendMessage(new TextMessage("Client with id " + id + "was not deleted."));
            }
            default -> session.sendMessage(new TextMessage("Cannot handle request."));
        }
    }
}
