package server;

import communication.GameState;
import communication.PlayerAction;
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

    public void playerTurn(Long playerId) {
        if (serverListener == null) return;
        if (playerId == null) {
            endRound();
            return;
        }

        PlayerHand playerHand = playerHands.get(playerId);
        if (playerHand.getPlayerTotal() >= 21) {
            playerTurn(playerTurns.pollFirst());
            return;
        }

        GameState currentState = new GameState(playerHands, dealerHand, playerId, false);
        PlayerAction action = serverListener.sendGameStateAndWaitForReply(currentState);

        if (action == PlayerAction.HIT) {
            givePlayerNewCard(playerId);
            playerTurn(playerId);
        } else if (action == PlayerAction.STAY) {
            playerTurn(playerTurns.pollFirst());
        } else if (action == PlayerAction.QUIT) {
            playerHands.remove(playerId);
        }
    }

    public void startRound() {

        resetGame();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        playerTurn(playerTurns.pollFirst());

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
        if (playerHand != null)
            playerHand.insertCard(currentDecks.pop());
    }

    public void endRound() {

        while (dealerHand.getPlayerTotal() < 17) {
            dealerHand.insertCard(currentDecks.pop());
        }

        System.out.println("Jakajan lopullinen käsi: " + dealerHand);
        System.out.println("Käden summa: " + dealerHand.getPlayerTotal() + "\n");

        for (PlayerHand playerHand : playerHands.values()) {
            if (
                    (playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() > dealerHand.getPlayerTotal()) ||
                            (dealerHand.getPlayerTotal() > 21 && playerHand.getPlayerTotal() <= 21)
            ) {
                System.out.println(playerHand.getName() + " voitti.");
            }
        }
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
