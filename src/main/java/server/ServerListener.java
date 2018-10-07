package server;

import communication.PlayerAction;
import util.PlayerBet;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Server - client yhteyden rajapinta
 */
public interface ServerListener {

    void startListener();
    PlayerAction sendGameStateAndWaitForReply();

    List<Future<PlayerAction>> askForRoundParticipation();

    List<Future<PlayerBet>> askForRoundBet();
    void setGameLogic(Logic logic);

}
