/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import communication.GameState;
import communication.PlayerAction;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import util.PlayerHand;

/**
 * @author Tuomas
 */
public class GUIView extends Application implements ClientView {

     FXMLDocumentController docController;
    ClientController controller;
    int playerId;
    String playerName;
    
    Stage stage;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        ClientConnection connection = new ClientConnection();
        controller = new ClientController(this, connection);
        connection.initializeConnection(this);
        connection.start();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXMLDocument.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        docController = loader.getController();
        
        docController.textFlow.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    docController.textFlow.layout();
                    docController.textContainer.layout();
                    docController.textContainer.setVvalue(1.0f);
                }));
        
        stage.setScene(scene);
        stage.show();
        
        this.stage = stage;
    }
    
        @Override
    public void stop() {
        controller.quit();
    }

    @Override
    public void updateToStartGameState(int playerId) {
        this.playerId = playerId;
        String playerName = JOptionPane.showInputDialog("Kirjoita nimi", "Nimi tähän");
        this.playerName = playerName;
        
                             Platform.runLater(new Runnable() {
                 @Override public void run() {
                     stage.setTitle(playerName);
                 }
             });
        
        controller.startGame(playerName);

    }

    @Override
    public void updateToPlaceBetsState() {
        int bet = 0;

        while (bet == 0) {

            String betStr = JOptionPane.showInputDialog(null, "Aseta panos", playerName, JOptionPane.PLAIN_MESSAGE);

            try {
                bet = Integer.parseInt(betStr);
            } catch (NumberFormatException e) {
                bet = 0;
            }
        }
        controller.placeBet(bet);
    }

    @Override
    public void updateBasedOnGameState(GameState gameState) {
        
             docController.addTextRow("\n-------------------------\n");
            docController.addTextRow("KIERROS");
            docController.addTextRow("\n-------------------------\n");
        
        docController.addTextRow("Dealerin käsi:");

        docController.addTextRow("" + gameState.dealerHand);

        docController.addTextRow("Käden summa: " + gameState.dealerHand.getPlayerTotal());

        docController.addTextRow("\nPelaajien kädet:\n");

        for (PlayerHand playerHand : gameState.playerHands.values()) {
            docController.addTextRow(playerHand.getName() + ": \n" + playerHand);
            docController.addTextRow("Käden summa: " + playerHand.getPlayerTotal());
        }

        int playerAction = PlayerAction.NULL;

        if (gameState.gameEnded) {
            
            docController.addTextRow("\n-------------------------\n");
            docController.addTextRow("KIERROS PÄÄTTYI");
            docController.addTextRow("\n-------------------------\n");

            for (PlayerHand playerHand : gameState.playerHands.values()) {

                if (playerHand.isHandWon()) {

                    docController.addTextRow(playerHand.getName() + " voitti.");

                }
                docController.addTextRow(playerHand.getName() + " rahasto: " + playerHand.getFunds());

            }
            
            docController.addTextRow("\n-------------------------\n");
            docController.addTextRow("TILASTOT");
            docController.addTextRow("\n-------------------------\n");

            docController.addTextRow("Pelattujen kierrosten määrä: " + gameState.totalPlayerRounds);
            docController.addTextRow("Pelaajien voittomäärä: " + gameState.totalPlayerWins);
            docController.addTextRow("Pelaajien voittoprosentti: " + gameState.playerWinPercentage + "%");

            while (playerAction == PlayerAction.NULL) {
                
                    String[] options = new String[] {"Jatka", "Lopeta"};
                     int choice  = JOptionPane.showOptionDialog(null, "Jatkatko seuraavalle kierrokselle vai lopetatko pelin?", playerName,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, options, options[0]);

                if (choice == 0) {
                    playerAction = PlayerAction.PLAY;
                } else if (choice == 1) {
                    playerAction = PlayerAction.QUIT;
                }
            }

        } else {
            PlayerHand currentPlayer = gameState.playerHands.get(gameState.currentPlayerId);

            String instructions = "\n" + currentPlayer.getName() + " vuoro: Ota uusi kortti";

            if (currentPlayer.getPlayerTotal() > 9 && currentPlayer.getPlayerTotal() < 11)
                instructions += ", tuplaa";

            instructions += " tai jää.";

            if (gameState.currentPlayerId == playerId) {

                while (playerAction == PlayerAction.NULL) {

                    String[] options = new String[] {"Ota", "Jää", "Tuplaa"};
                     int choice  = JOptionPane.showOptionDialog(null, instructions, playerName,
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, options, options[0]);

                    if (choice == 0) {
                        playerAction = PlayerAction.HIT;
                    } else if (choice == 1) {
                        playerAction = PlayerAction.STAY;
                    } else if (choice == 2) {
                        playerAction = PlayerAction.DOUBLE;
                    }
                }
            }

        }
        controller.doPlayerAction(playerAction);
    }
}
