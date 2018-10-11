/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import communication.GameState;
import communication.PlayerAction;
import util.PlayerBet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Tuomas
 */
public class TestView implements ServerListener {

    private String testToRun = "";
    private int iteration = 0;
    private Logic logic;

    public void setTestToRun(String testToRun) {
        this.testToRun = testToRun;
        iteration = 0;
    }

    private boolean testToRunIs(String testToRun) {
        return this.testToRun.contentEquals(testToRun);
    }

    @Override
    public void startListener() {

    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply() {
        GameState gameStateToSend = logic.getCurrentGameState();

        if (testToRunIs("START_ROUND_TEST")) {
            return new PlayerAction(PlayerAction.STAY, gameStateToSend.currentPlayerId);
        } else if (testToRunIs("DOUBLE_TEST")) {
            if (iteration == 0) {
                iteration++;
                return new PlayerAction(PlayerAction.DOUBLE, gameStateToSend.currentPlayerId);
            } else {
                return new PlayerAction(PlayerAction.STAY, gameStateToSend.currentPlayerId);
            }
        } else {
            return new PlayerAction(PlayerAction.STAY, gameStateToSend.currentPlayerId);
        }
    }

    @Override
    public List<Future<PlayerAction>> askForRoundParticipation() {

        List<Future<PlayerAction>> bets = new ArrayList<>();

        FutureTask<PlayerAction> task1 = new FutureTask<>(() -> new PlayerAction(PlayerAction.QUIT, 1));

        task1.run();

        bets.add(task1);

        FutureTask<PlayerAction> task2 = new FutureTask<>(() -> new PlayerAction(PlayerAction.QUIT, 2));

        task2.run();

        bets.add(task2);

        FutureTask<PlayerAction> task3 = new FutureTask<>(() -> new PlayerAction(PlayerAction.QUIT, 3));

        task3.run();

        bets.add(task3);

        return bets;

    }

    @Override
    public List<Future<PlayerBet>> askForRoundBet() {
        if (testToRunIs("BET_PLACED_TEST") || testToRunIs("FUNDS_ADD_TEST") || testToRunIs("DOUBLE_TEST")) {
            List<Future<PlayerBet>> bets = new ArrayList<>();

            FutureTask<PlayerBet> task1 = new FutureTask<>(() -> new PlayerBet(1, 10));

            task1.run();

            bets.add(task1);

            FutureTask<PlayerBet> task2 = new FutureTask<>(() -> new PlayerBet(2, 10));

            task2.run();

            bets.add(task2);

            FutureTask<PlayerBet> task3 = new FutureTask<>(() -> new PlayerBet(3, 10));

            task3.run();

            bets.add(task3);

            return bets;
        } else {
            return null;
        }
    }

    @Override
    public void setGameLogic(Logic logic) {
        this.logic = logic;
    }

}
