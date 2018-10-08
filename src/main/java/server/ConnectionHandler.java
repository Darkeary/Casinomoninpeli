package server;

import communication.Connection;
import communication.PlayerAction;
import util.PlayerBet;
import util.PlayerHand;

import java.io.IOException;
import java.net.ServerSocket;
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

    private HashMap<Integer, Connection> playerConnections = new HashMap<>();

    private Logic logic;

    @Override
    public void run() {
        if (logic == null) {
            System.err.println("Pelin tilaa ei ole asetettu.");
            return;
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (!isInterrupted()) {

                PlayerHand playerHand = new PlayerHand();

                System.out.println("Waiting for clients...");
                playerConnections.put(playerHand.getInGameId(), new Connection(server.accept()));

                System.out.println("Client received, doing start task...");

                ConnectionStartTask start = new ConnectionStartTask(playerConnections.get(playerHand.getInGameId()), playerHand, logic);

                pool.submit(start);
            }
        } catch (IOException ex) {
            System.err.println("Serveriä ei pystytty käynnistämään.");
            ex.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    public void doDisconnect(PlayerAction action) throws IOException {
        Connection connection = playerConnections.get(action.getPlayerId());
        logic.removePlayer(action.getPlayerId());
        connection.close();
    }

    @Override
    public void setGameLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public void startListener() {
        this.start();
    }

    @Override
    public synchronized PlayerAction sendGameStateAndWaitForReply() {

        Collection<Callable<PlayerAction>> roundTasks = new ArrayList<>();

        for (int playerId : playerConnections.keySet()) {
            try {
                roundTasks.add(new ConnectionRoundTurnTask(playerConnections.get(playerId), playerId, logic.getCurrentGameState()));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }

        return actionToReturn;
    }

    @Override
    public List<Future<PlayerAction>> askForRoundParticipation() {
        Collection<Callable<PlayerAction>> roundEndTasks = new ArrayList<>();

        for (int playerId : playerConnections.keySet()) {
            if (logic.getPlayer(playerId) != null || logic.pausedPlayers.get(playerId) != null) {
                try {
                    roundEndTasks.add(new ConnectionRoundEndTask(playerConnections.get(playerId), playerId, logic.getCurrentGameState()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            return pool.invokeAll(roundEndTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Future<PlayerBet>> askForRoundBet() {

        Collection<Callable<PlayerBet>> betTasks = new ArrayList<>();

        System.out.println("Pelaajia " + playerConnections.size());

        for (int playerId : playerConnections.keySet()) {
            if (logic.getPlayer(playerId) != null) {
                try {
                    betTasks.add(new ConnectionRoundBetsTask(playerConnections.get(playerId), playerId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {

            return pool.invokeAll(betTasks);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
