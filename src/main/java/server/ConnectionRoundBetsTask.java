package server;

import communication.Connection;
import communication.GameState;
import util.PlayerBet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Callable;

public class ConnectionRoundBetsTask extends ConnectionTask implements Callable<PlayerBet> {

    int playerId;

    ConnectionRoundBetsTask(Connection connection, int playerId) throws IOException {
        super(connection);
        this.playerId = playerId;
    }


    @Override
    public PlayerBet call() throws IOException {

        sendStateSignal(GameState.PLACE_BETS);

        ObjectInputStream is = connection.getIs();

        int playerBet = is.readInt();

        return new PlayerBet(playerId, playerBet);

    }
}
