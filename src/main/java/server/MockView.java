package server;

import communication.GameState;
import communication.PlayerAction;

public class MockView implements ServerListener {
    private static MockView ourInstance = new MockView();

    private MockView() {
    }

    public static MockView getInstance() {
        return ourInstance;
    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend) {

        // Käsitteelee GameState olion

        // Pyytää inputin, mikä muunnetaan oikeaksi PlayerActioniksi

        return null;
    }
}
