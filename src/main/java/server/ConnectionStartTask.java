package server;

import util.PlayerHand;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ConnectionStartTask extends ConnectionTask implements Callable<Void> {

    Logic logic;
    PlayerHand playerHand;

    ConnectionStartTask(Socket connection, PlayerHand playerHand, Logic logic) {
        this.connection = connection;
        this.playerHand = playerHand;
        this.logic = logic;
    }

    @Override
    public Void call() throws Exception {
        DataInputStream in = new DataInputStream(connection.getInputStream());

        while (in.available() < 32) {
            doWait(100);
        }

        int nameSize = in.readInt();

        while (in.available() < nameSize) {
            doWait(100);
        }

        String playerName = in.readUTF();

        playerHand.setName(playerName);

        logic.addPlayer(playerHand);

        return null;
    }
}
