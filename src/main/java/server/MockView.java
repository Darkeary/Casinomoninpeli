package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.PlayerBet;
import util.PlayerHand;

import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.Future;

/**
 * Vanha serverin testikäyttöliittymä, ei enään käytössä.
 *
 * @author Tuomas
 */
public class MockView implements ServerListener {

    private static MockView ourInstance = new MockView();
    private Logic logic;

    private MockView() {
    }

    public static MockView getInstance() {
        return ourInstance;
    }

    @Override
    public void startListener() {

    }

    @Override
    public PlayerAction sendGameStateAndWaitForReply() {

        GameState gameStateToSend = logic.getCurrentGameState();

        int playerId = gameStateToSend.currentPlayerId;

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
            return new PlayerAction(PlayerAction.HIT, playerId);
        } else if (choice.contains("j")) {
            return new PlayerAction(PlayerAction.STAY, playerId);
        } else if (choice.contains("t")) {
            return new PlayerAction(PlayerAction.DOUBLE, playerId);
        } else if (choice.contains("s")) {
            return new PlayerAction(PlayerAction.SPLIT, playerId);
        } else {
            return new PlayerAction(PlayerAction.QUIT, playerId);
        }

    }

    @Override
    public List<Future<PlayerAction>> askForRoundParticipation() {
        return null;
    }

    public PlayerAction askForRoundParticipation(int playerId) {
        System.out.println("Pelaaja " + playerId + ":");
        System.out.println("Jatkatko seuraavalle kierrokselle (j) vai lopetatko pelin (l)?");

        Scanner reader = new Scanner(System.in);
        String choice = reader.nextLine();

        if (choice.contains("j")) {
            return new PlayerAction(PlayerAction.PLAY, playerId);
        } else if (choice.contains("l")) {
            return new PlayerAction(PlayerAction.QUIT, playerId);
        } else {
            return new PlayerAction(PlayerAction.QUIT, playerId);
        }
    }

    @Override
    public List<Future<PlayerBet>> askForRoundBet() {
        return null;
    }

    public int askForRoundBet(int playerId) {
        System.out.println("Pelaaja " + playerId + ":");
        System.out.println("Syötä panoksesi: ");

        Scanner reader = new Scanner(System.in);
        String amount = reader.nextLine();

        return Integer.parseInt(amount);

    }

    @Override
    public void setGameLogic(Logic logic) {
        this.logic = logic;
    }
}
