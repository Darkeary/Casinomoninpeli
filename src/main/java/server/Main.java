/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.PlayerHand;

/**
 * @author roman
 */

public class Main {

    // Test menu
    public static void main(String[] args) throws Exception {

        System.out.println("Hello");
        System.out.println("server started...");

        Logic logic = new Logic(MockView.getInstance());

        logic.addPlayer(new PlayerHand("Pertti"));
        logic.addPlayer(new PlayerHand("Jaakko"));
        logic.addPlayer(new PlayerHand("Ville"));

        logic.shuffle();

        logic.startRound();

    }

}
