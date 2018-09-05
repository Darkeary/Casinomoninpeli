/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.casinomoninpeli;

/**
 *
 * @author roman
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {

    public boolean newround = true;
    AllDecks testdecks = new AllDecks();
    ArrayList<Card> allDecks = testdecks.getNewDecks();
        
    public void shuffle(){
        Collections.shuffle(allDecks);    
    }
    public void newroundTest()throws Exception{
        while (newround == true) {
            round(allDecks);
        }
    }
        
    public void round(ArrayList allDecks1) throws Exception {
        this.newround = false;
        ArrayList<Card> allDecks = allDecks1;
        ArrayList<Card> dealerHand = new ArrayList();
        int x=0;
        dealerHand.add(allDecks.get(0));
        allDecks.remove(0);

        PlayerHand player = new PlayerHand("namehere");
        player.insertCard(allDecks.get(0));
        allDecks.remove(0);

        dealerHand.add(allDecks.get(0));
        allDecks.remove(0);

        player.insertCard(allDecks.get(0));
        allDecks.remove(0);

        System.out.println("Player has: ");
        int playerTotal = 0;

        for (int i = 0; i < player.playerhand.size(); i++) {

            playerTotal += player.playerhand.get(i).getValue();
            System.out.println(player.playerhand.get(i).getValue());
            if (player.playerhand.get(i).getType().equalsTo("A")) {
            }
            System.out.println("Player has total: " + playerTotal + "\n");

            System.out.println("Dealer has: ");
            int dealerTotal = 0;
            for (int j = 0; j < dealerHand.size(); j++) {

                dealerTotal += dealerHand.get(j).getValue();
                System.out.println(dealerHand.get(j).getValue());

            }
            System.out.println("Dealer has total: " + dealerTotal + " \n");

            System.out.println("type true for new round... otherwise game quits");
            Scanner keyboard = new Scanner(System.in);
            String inputstring = keyboard.nextLine();
            if (inputstring.equalsIgnoreCase("true")) {
                newround = true;
            }
        }
    }

    public static void main(String[] args)throws Exception {
        System.out.println("Hello");
        //AllDecks testdecks = new AllDecks();
        //ArrayList<Card> allDecks = testdecks.getNewDecks();
        //Collections.shuffle(allDecks);
        Main main = new Main();
        System.out.println("server started...");
        
        main.newroundTest();
    }
}
