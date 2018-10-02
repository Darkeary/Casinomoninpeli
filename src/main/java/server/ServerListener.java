package server;

import communication.PlayerAction;

/**
 * Server - client yhteyden rajapinta
 */
public interface ServerListener {

    PlayerAction sendGameStateAndWaitForReply();
    PlayerAction askForRoundParticipation(long playerId);
    int askForRoundBet(long playerId);
    void setGameLogic(Logic logic);

}
