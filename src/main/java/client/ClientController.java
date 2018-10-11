package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /**
     * Aloittaa yhteyden peli serveriin
     */
    void startGame(String playerName) {
        try {
            connection.connectionStart(playerName);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }

    /**
     * Asettaa panoksen kierrokselle
     * 
     * @param bet Kierroksen panos
     */
    void placeBet(int bet) {
        try {
            connection.writePlayerBetToServer(bet);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }

    /**
     * Ilmoittaa että pelaaja haluaa suorittaa kyseisen toimenpiteen
     * 
     * @param playerAction Suoritettavan toimenpiteen tunnus
     */
    void doPlayerAction(int playerAction) {
        try {
            connection.writePlayerActionToServer(playerAction);
        } catch (IOException e) {
            HandleIOException(e);
        }
    }
    
    void quit() {
        try {
            connection.quitGame();
        } catch (IOException ex) {
        }
    }

}
