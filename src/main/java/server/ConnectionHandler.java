package server;

import communication.PlayerAction;
import util.PlayerHand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Luokka vastaanottaa yhteyksia ja luo jokaiselle yhteydelle tarvittavat taskit Thread pooliin
 */
public class ConnectionHandler extends Thread implements ServerListener {

    private final static int PORT = 5566;

    private ExecutorService pool = Executors.newFixedThreadPool(50);

    private HashMap<Long, Socket> playerConnections = new HashMap<>();

    private Logic logic;

    @Override
    public void run() {
        if (logic == null) {
            System.err.println("Pelin tilaa ei ole asetettu.");
            return;
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (!isInterrupted()) {
                try {

                    Socket connection = server.accept();

                    PlayerHand playerHand = new PlayerHand();

                    playerConnections.put(playerHand.getInGameId(), connection);

                    ConnectionStartTask start = new ConnectionStartTask(connection, playerHand, logic);

                    pool.submit(start);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println("Serveriä ei pystytty käynnistämään.");
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    public void doDisconnect(PlayerAction action) throws IOException {
        Socket connection = playerConnections.get(action.getPlayerId());
        logic.removePlayer(action.getPlayerId());
        connection.close();
    }

    @Override
    public void setGameLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply() {

        Collection<Callable<PlayerAction>> roundTasks = new ArrayList<>();

        for (long playerId : playerConnections.keySet()) {
            Socket connection = playerConnections.get(playerId);
            roundTasks.add(new ConnectionRoundTurnTask(connection, playerId, logic.getCurrentGameState()));
        }

        PlayerAction actionToReturn = null;

        try {

            List<Future<PlayerAction>> futures = pool.invokeAll(roundTasks);

            for (Future<PlayerAction> future : futures) {
                PlayerAction action = future.get();

                if (action.getPlayerId() == logic.getCurrentGameState().currentPlayerId)
                    actionToReturn = action;

                if (action.getActionId() == PlayerAction.DISCONNECT) doDisconnect(action);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return actionToReturn;
    }

    @Override
    public PlayerAction askForRoundParticipation(long playerId) {
        return null;
    }
}
