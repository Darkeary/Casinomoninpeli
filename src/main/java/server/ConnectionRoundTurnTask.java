package server;

import communication.Connection;
import communication.GameState;
import communication.PlayerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;


/**
 * Taski mikä suorittaa blackjack pelin yksittäisen kierroksen toimet. Lähettää pelaajalle pelin tilan ja kysyy mitä tällä hetkellä vuorossa oleva pelaaja haluaa tehdä.
 */
public class ConnectionRoundTurnTask extends ConnectionTask implements Callable<PlayerAction> {

    private GameState currentGameState;
    private int playerId;

    ConnectionRoundTurnTask(Connection connection, int playerId, GameState gameState) throws IOException {
        super(connection);
        this.playerId = playerId;
        this.currentGameState = gameState;
    }

    @Override
    public PlayerAction call() {
        System.out.println("Starting " + playerId + " turn...");

        PlayerAction action = null;
        try {

            sendStateSignal(GameState.ROUND_TURN);

            ObjectOutputStream os = connection.getOs();

            os.reset();

            os.writeObject(currentGameState);

            os.flush();

            ObjectInputStream is = connection.getIs();

            action = new PlayerAction(is.readInt(), playerId);

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }

        return action;
    }
}
