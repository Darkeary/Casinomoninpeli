package server;

import communication.GameState;
import communication.PlayerAction;

/**
 * Server - client yhteyden rajapinta
 */
public interface ServerListener {

    PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend);

    PlayerAction askForRoundParticipation(long playerId);

    int askForRoundBet(long playerId);

}
