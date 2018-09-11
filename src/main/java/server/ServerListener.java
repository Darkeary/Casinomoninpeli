package server;

import communication.GameState;
import communication.PlayerAction;

public interface ServerListener {

    public PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend);


}
