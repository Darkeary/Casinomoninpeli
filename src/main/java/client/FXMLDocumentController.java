/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;

/**
 * @author Tuomas
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    TextFlow textFlow;
    @FXML
    ScrollPane textContainer;

    @FXML
    private void hitPressed(ActionEvent event) {
        Text text1 = new Text("Big italic red text\n");
        textFlow.getChildren().add(text1);
    }

    public void addTextRow(String text) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Text textObj = new Text(text + "\n");
                textFlow.getChildren().add(textObj);
            }
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
