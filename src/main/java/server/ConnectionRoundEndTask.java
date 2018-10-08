package server;

import communication.Connection;
import communication.GameState;
import communication.PlayerAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;


/**
 * Taski mikä tekee pelaajan kanssa kierroksen lopputoimet
 */
public class ConnectionRoundEndTask extends ConnectionTask implements Callable<PlayerAction> {

    private GameState currentGameState;
    private int playerId;

    ConnectionRoundEndTask(Connection connection, int playerId, GameState gameState) throws IOException {
        super(connection);
        this.playerId = playerId;
        this.currentGameState = gameState;
    }

    @Override
    public PlayerAction call() {
        System.out.println("Ending round...");

        PlayerAction action = null;
        try {

            sendStateSignal(GameState.ROUND_END);

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
