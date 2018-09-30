package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.PlayerHand;

import java.util.Scanner;
import java.util.Stack;

/**
 * Testi käyttöliittymä.
 *
 * @author Tuomas
 */
public class MockView implements ServerListener {

    private static MockView ourInstance = new MockView();

    private MockView() {
    }

    public static MockView getInstance() {
        return ourInstance;
    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply(GameState gameStateToSend) {

        System.out.println("Dealerin käsi:");

        System.out.println(gameStateToSend.dealerHand);

        System.out.println("Käden summa: " + gameStateToSend.dealerHand.getPlayerTotal());

        System.out.println("\nPelaajien kädet:\n");

        for (PlayerHand playerHand : gameStateToSend.playerHands.values()) {
            System.out.println(playerHand.getName() + ": \n" + playerHand);
            System.out.println("Käden summa: " + playerHand.getPlayerTotal());
        }

        PlayerHand currentPlayer = gameStateToSend.playerHands.get(gameStateToSend.currentPlayerId);

        Stack<Card> playerCards = (Stack<Card>) currentPlayer.getPlayerHand();

        String instructions = "\n" + currentPlayer.getName() + " vuoro: Ota uusi kortti (o)";

        if (currentPlayer.getPlayerTotal() > 9 && currentPlayer.getPlayerTotal() < 11)
            instructions += ", tuplaa (t)";
        else if (playerCards.size() == 2) {

            Card cardOne = playerCards.get(0);
            Card cardTwo = playerCards.get(1);

            /*if (cardOne.getType().contentEquals(cardTwo.getType()))
                instructions += ", splittaa (s)";
                */
        }
        instructions += " tai jää (j).";

        System.out.println(instructions);

        Scanner reader = new Scanner(System.in);
        String choice = reader.nextLine();

        if (choice.contains("o")) {
            return PlayerAction.HIT;
        } else if (choice.contains("j")) {
            return PlayerAction.STAY;
        } else if (choice.contains("t")) {
            return PlayerAction.DOUBLE;
        } else if (choice.contains("s")) {
            return PlayerAction.SPLIT;
        } else {
            return PlayerAction.QUIT;
        }
    }

    @Override
    public PlayerAction askForRoundParticipation(long playerId) {
        System.out.println("Pelaaja " + playerId + ":");
        System.out.println("Jatkatko seuraavalle kierrokselle (j) vai lopetatko pelin (l)?");

        Scanner reader = new Scanner(System.in);
        String choice = reader.nextLine();

        if (choice.contains("j")) {
            return PlayerAction.PLAY;
        } else if (choice.contains("l")) {
            return PlayerAction.QUIT;
        } else {
            return PlayerAction.QUIT;
        }
    }

    @Override
    public int askForRoundBet(long playerId) {
        System.out.println("Pelaaja " + playerId + ":");
        System.out.println("Syötä panoksesi: ");

        Scanner reader = new Scanner(System.in);
        String amount = reader.nextLine();

        return Integer.parseInt(amount);

    }
}
