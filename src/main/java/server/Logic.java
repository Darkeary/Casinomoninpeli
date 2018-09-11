package server;

import communication.GameState;
import util.Card;
import util.PlayerHand;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author Anders
 * @author Tuomas
 */
public class Logic {

    /* MUUTTUJAT */

    // Perus pakka mistä tehdään vain kopioita, ei sekoiteta tätä
    private static final AllDecks allDecks = new AllDecks();

    private final ServerListener serverListener;

    // Tällä hetkellä pelissä käytössä oleva pakka
    private Stack<Card> currentDecks = allDecks.getNewDecks();

    // Jakajan käsi
    private PlayerHand dealerHand = new PlayerHand("dealer");

    // Pelaajien kädet järjestettynä id:n mukaan
    private HashMap<Long, PlayerHand> playerHands = new HashMap<>();

    private LinkedList<Long> playerTurns = new LinkedList<>();

    /* --------------------- */

    public Logic(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void addPlayer(PlayerHand playerHand) {
        playerHands.put(playerHand.getId(), playerHand);
    }

    public void shuffle() {
        Collections.shuffle(currentDecks);
    }

    public GameState checkResult() {
        return null;
    }

    public void playerTurn(Long playerId) {
    }

    public void startRound() {

        resetGame();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

    }

    public void resetGame() {
        dealerHand.empty();
        for (PlayerHand playerHand : playerHands.values())
            playerHand.empty();
        playerTurns = new LinkedList<>(playerHands.keySet());
    }

    public void giveAllPlayersNewCard() {
        for (Long playerId : playerHands.keySet())
            givePlayerNewCard(playerId);
    }

    public void givePlayerNewCard(Long playerId) {
        PlayerHand playerHand = playerHands.get(playerId);
        playerHand.insertCard(currentDecks.pop());
    }

    public void endRound() {
    }

    Stack<Card> getCurrentDecks() {
        return currentDecks;
    }

    public PlayerHand getDealerHand() {
        return dealerHand;
    }

    public HashMap<Long, PlayerHand> getPlayerHands() {
        return playerHands;
    }
}
