/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import communication.GameState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;

/**
 * @author Tuomas
 */
public class GUIView extends Application implements ClientView {

    ClientController controller;
    int playerId;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
/*
        try {

            ClientConnection connection = new ClientConnection();
            controller = new ClientController(this, connection);
            connection.initializeConnection(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void updateToStartGameState(int playerId) {
        this.playerId = playerId;
        String playerName = JOptionPane.showInputDialog("Kirjoita nimi", "Nimi tähän");

        controller.startGame(playerName);

    }

    @Override
    public void updateToPlaceBetsState() {
        int bet = 0;

        while (bet == 0) {

            String betStr = JOptionPane.showInputDialog("Aseta panos", "Panos tähän");

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

    }
}
