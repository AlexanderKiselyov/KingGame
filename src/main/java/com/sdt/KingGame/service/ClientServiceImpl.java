package com.sdt.KingGame.service;

import com.sdt.KingGame.controller.ClientController;
import com.sdt.KingGame.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientController clientController;

    @Override
    public void create(Client client) {
        this.clientController.create(client);
    }

    @Override
    public Iterable<Client> readAll() {
        return (Iterable<Client>) clientController.read();
    }

    @Override
    public Client read(int id) {
        return clientController.read(id).getBody();
    }

    @Override
    public boolean update(Client client, int id) {
        ResponseEntity<?> response = clientController.update(client, id);
        return new ResponseEntity<>(HttpStatus.OK).equals(response);
    }

    @Override
    public boolean delete(int id) {
        ResponseEntity<?> response = clientController.delete(id);
        return new ResponseEntity<>(HttpStatus.OK).equals(response);
    }
}
