package communication;

import util.CardCounterPrediction;
import util.PlayerHand;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Luokan tarkoitus on toimia tiedon välittäjänä serveriltä clienttiin
 * @author Tuomas
 */
public class GameState implements Serializable {

    // Server signals for states
    public static final int START_GAME = 1;
    public static final int PLACE_BETS = 2;
    public static final int ROUND_TURN = 3;
    public static final int ROUND_END = 4;


    public HashMap<Integer, PlayerHand> playerHands;
    public PlayerHand dealerHand;
    public int currentPlayerId;
    public int nextPlayerId;
    public int timeoutInSeconds = 30;
    public boolean gameEnded;
    public CardCounterPrediction prediction;

    // Stats
    public float totalPlayerRounds;
    public float totalPlayerWins;
    public float playerWinPercentage;

    public GameState() {

    }

    public GameState(GameState gameState) {
        this.playerHands = gameState.playerHands;
        this.dealerHand = gameState.dealerHand;
        this.currentPlayerId = gameState.currentPlayerId;
        this.gameEnded = gameState.gameEnded;
    }

    public GameState setGameState(HashMap<Integer, PlayerHand> playerHands, PlayerHand dealerHand, int currentPlayerId, boolean gameEnded) {
        this.playerHands = playerHands;
        this.dealerHand = dealerHand;
        this.currentPlayerId = currentPlayerId;
        this.gameEnded = gameEnded;

        return this;
    }

    public void addCardCounterPrediction(CardCounterPrediction prediction) {
        this.prediction = prediction;
    }

    public void addStatistics(float totalPlayerRounds, float totalPlayerWins, float playerWinPercentage) {
        this.totalPlayerRounds = totalPlayerRounds;
        this.totalPlayerWins = totalPlayerWins;
        this.playerWinPercentage = playerWinPercentage;
    }

}
