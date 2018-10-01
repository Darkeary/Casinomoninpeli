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

    public HashMap<Long, PlayerHand> playerHands;
    public PlayerHand dealerHand;
    public long currentPlayerId;
    public long nextPlayerId;
    public int timeoutInSeconds = 30;
    public boolean gameEnded;
    public CardCounterPrediction prediction;

    public GameState(HashMap<Long, PlayerHand> playerHands, PlayerHand dealerHand, long currentPlayerId, boolean gameEnded) {
        this.playerHands = playerHands;
        this.dealerHand = dealerHand;
        this.currentPlayerId = currentPlayerId;
        this.gameEnded = gameEnded;
    }
    
    public void addCardCounterPrediction(CardCounterPrediction prediction) {
        this.prediction = prediction;
    }

}
