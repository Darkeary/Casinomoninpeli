/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import communication.GameState;
import communication.PlayerAction;

/**
 *
 * @author Tuomas
 */
public class TestView implements ServerListener {
    
    private String testToRun = "";

    public void setTestToRun(String testToRun) {
        this.testToRun = testToRun;
    }
    
    private boolean testToRunIs(String testToRun) {
        return testToRun.contains(testToRun);
    }
    
    @Override
    public PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend) {
        if(testToRunIs("START_ROUND_TEST")) {
            return PlayerAction.STAY;
        } else {
            return PlayerAction.STAY;
        }
    }

    @Override
    public PlayerAction askForRoundParticipation(long playerId) {
        
        if(testToRunIs("QUIT_TEST") || testToRunIs("START_ROUND_TEST")) {
            return PlayerAction.QUIT;
        }
        else {
            return PlayerAction.QUIT;
        }
    }
    
}
