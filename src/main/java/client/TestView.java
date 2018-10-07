package client;

import communication.GameState;
import communication.PlayerAction;
import util.PlayerHand;

import java.io.IOException;
import java.util.Scanner;

public class TestView implements ClientView {

    int playerId;
    String playerName;
    ClientController controller;
    Scanner sc = new Scanner(System.in);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        TestView cv = new TestView();

        ClientConnection connection = new ClientConnection();
        cv.setController(new ClientController(cv, connection));
        connection.initializeConnection(cv);
        connection.start();

    }

    @Override
    public void updateToStartGameState(int playerId) {
        this.playerId = playerId;

        System.out.println("Anna nimi: ");
        playerName = sc.nextLine();

        controller.startGame(playerName);
    }

    @Override
    public void updateToPlaceBetsState() {

        System.out.println("Aseta panos: ");
        int bet = sc.nextInt();

        controller.placeBet(bet);

    }

    @Override
    public void updateBasedOnGameState(GameState gameState) {

        System.out.println("Dealerin käsi:");

        System.out.println(gameState.dealerHand);

        System.out.println("Käden summa: " + gameState.dealerHand.getPlayerTotal());

        System.out.println("\nPelaajien kädet:\n");

        for (PlayerHand playerHand : gameState.playerHands.values()) {
            System.out.println(playerHand.getName() + ": \n" + playerHand);
            System.out.println("Käden summa: " + playerHand.getPlayerTotal());
        }

        int playerAction = PlayerAction.NULL;

        if (gameState.gameEnded) {

            for (PlayerHand playerHand : gameState.playerHands.values()) {

                if (playerHand.isHandWon()) {

                    System.out.println(playerHand.getName() + " voitti.");

                }
                System.out.println(playerHand.getName() + " rahasto: " + playerHand.getFunds());

            }

            System.out.println("Pelattujen kierrosten määrä: " + gameState.totalPlayerRounds);
            System.out.println("Pelaajien voittomäärä: " + gameState.totalPlayerWins);
            System.out.println("Pelaajien voittoprosentti: " + gameState.playerWinPercentage + "%");

            while (playerAction == PlayerAction.NULL) {

                System.out.println(playerName + ":");
                System.out.println("Jatkatko seuraavalle kierrokselle (j) vai lopetatko pelin (l)?");

                Scanner reader = new Scanner(System.in);
                String choice = reader.nextLine();

                if (choice.contains("j")) {
                    playerAction = PlayerAction.PLAY;
                } else if (choice.contains("l")) {
                    playerAction = PlayerAction.QUIT;
                }
            }

        } else {
            PlayerHand currentPlayer = gameState.playerHands.get(gameState.currentPlayerId);

            String instructions = "\n" + currentPlayer.getName() + " vuoro: Ota uusi kortti (o)";

            if (currentPlayer.getPlayerTotal() > 9 && currentPlayer.getPlayerTotal() < 11)
                instructions += ", tuplaa (t)";

            instructions += " tai jää (j).";

            if (gameState.currentPlayerId == playerId) {

                while (playerAction == PlayerAction.NULL) {

                    System.out.println(instructions);


                    String choice = sc.nextLine();

                    if (choice.contains("o")) {
                        playerAction = PlayerAction.HIT;
                    } else if (choice.contains("j")) {
                        playerAction = PlayerAction.STAY;
                    } else if (choice.contains("t")) {
                        playerAction = PlayerAction.DOUBLE;
                    }
                }
            }

        }
        controller.doPlayerAction(playerAction);
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }
}
