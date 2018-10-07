package client;

import java.io.IOException;

public class ClientController {

    ClientView view;
    ClientConnection connection;

    ClientController(ClientView view, ClientConnection connection) {
        this.view = view;
        this.connection = connection;
    }

    void HandleIOException(IOException e) {
        e.printStackTrace();
    }

    void startGame(String playerName) {
        try {
            connection.connectionStart(playerName);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }

    void placeBet(int bet) {
        try {
            connection.writePlayerBetToServer(bet);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }

    void doPlayerAction(int playerAction) {
        try {
            connection.writePlayerActionToServer(playerAction);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }

}
