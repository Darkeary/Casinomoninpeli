package server;

import communication.GameState;
import communication.PlayerAction;
import util.PlayerHand;

import java.util.Scanner;

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

        System.out.println("\n" + currentPlayer.getName() + " vuoro: Ota uusi kortti (o) tai jää (j).");

        Scanner reader = new Scanner(System.in);
        String choice = reader.nextLine();

        if (choice.contains("o")) {
            return PlayerAction.HIT;
        } else if (choice.contains("j")) {
            return PlayerAction.STAY;
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
}
