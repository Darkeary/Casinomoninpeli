package communication;

import util.PlayerHand;

import java.io.Serializable;
import java.util.HashMap;

public class GameState implements Serializable {

    public HashMap<Long, PlayerHand> playerHands;
    public PlayerHand dealerHand;
    public long currentPlayerId;
    public long nextPlayerId;
    public int timeoutInSeconds = 30;
    public boolean gameEnded;

    public GameState setGameState(HashMap<Long, PlayerHand> playerHands, PlayerHand dealerHand, long currentPlayerId, boolean gameEnded) {
        this.playerHands = playerHands;
        this.dealerHand = dealerHand;
        this.currentPlayerId = currentPlayerId;
        this.gameEnded = gameEnded;

        return this;
    }
}
