package server;

import communication.GameState;
import communication.PlayerAction;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ConnectionRoundTurnTask extends ConnectionTask implements Callable<PlayerAction> {

    private GameState currentGameState;
    private long playerId;

    ConnectionRoundTurnTask(Socket connection, long playerId, GameState gameState) {
        this.connection = connection;
        this.playerId = playerId;
        this.currentGameState = gameState;
    }

    @Override
    public PlayerAction call() {
        PlayerAction action = null;
        try {

            ObjectOutputStream os = new ObjectOutputStream(connection.getOutputStream());

            os.writeObject(currentGameState);

            DataInputStream in = new DataInputStream(connection.getInputStream());

            while (in.available() < 32) {
                doWait(100);
            }

            action = new PlayerAction(in.readInt(), playerId);

        } catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }

        return action;
    }
}
