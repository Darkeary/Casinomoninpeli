/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import communication.GameState;
import communication.PlayerAction;

/**
 * @author Tuomas
 */
public class TestView implements ServerListener {

    private String testToRun = "";
    private int iteration = 0;

    public void setTestToRun(String testToRun) {
        this.testToRun = testToRun;
        iteration = 0;
    }

    private boolean testToRunIs(String testToRun) {
        return this.testToRun.contentEquals(testToRun);
    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend) {
        if (testToRunIs("START_ROUND_TEST")) {
            return PlayerAction.STAY;
        } else if (testToRunIs("DOUBLE_TEST")) {
            if (iteration == 0) {
                iteration++;
                return PlayerAction.DOUBLE;
            } else {
                return PlayerAction.STAY;
            }
        } else {
            return PlayerAction.STAY;
        }
    }

    @Override
    public PlayerAction askForRoundParticipation(long playerId) {
        return PlayerAction.QUIT;
    }

    @Override
    public int askForRoundBet(long playerId) {
        if (testToRunIs("BET_PLACED_TEST") || testToRunIs("FUNDS_ADD_TEST") || testToRunIs("DOUBLE_TEST")) {
            return 10;
        } else {
            return 0;
        }
    }

}
