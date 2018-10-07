package server;

import communication.Connection;
import communication.GameState;
import util.PlayerHand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

public class ConnectionStartTask extends ConnectionTask implements Callable<Void> {

    Logic logic;
    PlayerHand playerHand;

    ConnectionStartTask(Connection connection, PlayerHand playerHand, Logic logic) throws IOException {
        super(connection);
        this.playerHand = playerHand;
        this.logic = logic;
    }

    @Override
    public Void call() throws IOException {

        ObjectOutputStream os = connection.getOs();
        ObjectInputStream is = connection.getIs();

        // Confirm stream is working
        int code = is.readInt();
        os.writeInt(code + 1);
        os.flush();
        System.out.println("Connection succesful with client code: " + code);

        sendStateSignal(GameState.START_GAME);

        os.writeInt(playerHand.getInGameId());

        os.flush();

        String playerName = is.readUTF();

        playerHand.setName(playerName);

        logic.addPlayer(playerHand);

        System.out.println("Received player: " + playerName);

        if (logic.getPlayerHands().size() == 2) logic.startRound();
        else System.out.println("Pelaajia " + logic.getPlayerHands().size() + ", waiting for more players...");

        return null;
    }
}
