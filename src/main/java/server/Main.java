/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

/**
 * @author roman
 */

public class Main {

    // Test menu
    public static void main(String[] args) throws Exception {

        System.out.println("Hello");
        System.out.println("server started...");

        Logic logic = new Logic(new ConnectionHandler());

        logic.shuffle();

        logic.startRound();

    }

}
