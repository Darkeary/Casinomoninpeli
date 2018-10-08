package client;

import communication.Connection;
import communication.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientConnection extends Thread {

    final static String IP = "localhost";
    final static int PORT = 5566;

    ClientView view;

    Connection connection;

    @Override
    public void run() {

        if (connection == null) return;

        while (connection.isConnected()) {
            try {
                ObjectInputStream is = connection.getIs();
                int state = is.readInt();

                switch (state) {
                    case GameState.START_GAME:
                        int playerId = is.readInt();
                        view.updateToStartGameState(playerId);
                        break;
                    case GameState.PLACE_BETS:
                        view.updateToPlaceBetsState();
                        break;
                    case GameState.ROUND_TURN:
                        view.updateBasedOnGameState(readGameStateFromServer());
                        break;
                    case GameState.ROUND_END:
                        view.updateBasedOnGameState(readGameStateFromServer());
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void connectionStart(String playerName) throws IOException {

        ObjectOutputStream os = connection.getOs();

        os.writeUTF(playerName);

        os.flush();
    }

    void writePlayerBetToServer(int playerBet) throws IOException {

        ObjectOutputStream os = connection.getOs();

        os.writeInt(playerBet);

        os.flush();
    }

    GameState readGameStateFromServer() throws IOException {

        ObjectInputStream is = connection.getIs();

        GameState gameState = null;

        try {
            Object o = is.readObject();
            if (o == null) {
                System.err.println("Read null from server!");
            } else if ((o instanceof GameState)) {

                gameState = (GameState) o;

            } else {
                throw new IllegalArgumentException("Wanted GameState, got " + o);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Wanted GameState, got INVALID CLASS (" + e + ")");
        }

        return gameState;
    }

    void writePlayerActionToServer(int playerAction) throws IOException {
        ObjectOutputStream os = connection.getOs();

        os.writeInt(playerAction);
        os.flush();
    }

    void initializeConnection(ClientView view) throws IOException {
        connection = new Connection(new Socket(IP, PORT));

        connection.getOs().writeInt(999);
        connection.getOs().flush();

        int returnCode = connection.getIs().readInt();

        if (returnCode == 1000) {
            System.out.println("Connection succesful");
        } else {
            quitGame();
            return;
        }

        this.view = view;
    }

    void quitGame() throws IOException {
        connection.close();
    }

    private synchronized void doWait() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
