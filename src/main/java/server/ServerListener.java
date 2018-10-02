package server;

import communication.PlayerAction;

public interface ServerListener {

    public PlayerAction sendGameStateAndWaitForReply();
    public PlayerAction askForRoundParticipation(long playerId);

    public void setGameLogic(Logic logic);

}
