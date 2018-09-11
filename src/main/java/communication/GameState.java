package communication;

import util.PlayerHand;

import java.util.HashMap;

public class GameState {

    public HashMap<Long, PlayerHand> playerHands;
    public PlayerHand dealerHand;
    public long currentPlayerId;
    public long nextPlayerId;
    public int timeoutInSeconds = 30;
    public boolean gameEnded;
    public long gameWinnerId;

    public GameState(HashMap<Long, PlayerHand> playerHands, PlayerHand dealerHand, long currentPlayerId, boolean gameEnded, long gameWinnerId) {
        this.playerHands = playerHands;
        this.dealerHand = dealerHand;
        this.currentPlayerId = currentPlayerId;
        this.gameEnded = gameEnded;
        this.gameWinnerId = gameWinnerId;
    }

}
