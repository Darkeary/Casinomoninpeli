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
/*
    public boolean newround = true;
    AllDecks testdecks = new AllDecks();
    ArrayList<Card> allDecks = testdecks.getNewDecks();

    public void shuffle() {
        Collections.shuffle(allDecks);
    }

    public void newroundTest() throws Exception {
        while (newround == true) {
            round(allDecks);
        }
    }

    private ArrayList<PlayerHand> countTotals(PlayerHand playerH) {

        ArrayList<PlayerHand> playerTotals = new ArrayList<>();
        playerTotals.add(playerH);
        for (int i = 0; i < playerH.playerhand.size(); i++) {

            
                if (playerH.playerhand.get(i).getType().equalsIgnoreCase("A")) {

                    playerTotals.add(playerH);
                    int playerTotalsTempLastIndex = playerTotals.size() - 1;
                    playerTotals.get(i).setPlayerTotal(playerTotals.get(i).getPlayerTotal() + playerH.playerhand.get(i).getValue());
                    playerTotals.get(playerTotalsTempLastIndex).setPlayerTotal(playerTotals.get(i).getPlayerTotal() + playerH.playerhand.get(i).getValue2());
                } else {
                    playerTotals.get(i).setPlayerTotal(playerTotals.get(i).getPlayerTotal() + playerH.playerhand.get(i).getValue());
                }

               // System.out.println(playerH.playerhand.get(i).getValue());
                 //       if (player.playerhand.get(i).getType().equalsIgnoreCase("A")) {
                //System.out.println("Player has total: " + playerTotal + "\n");//undone
            //}else{
              //  System.out.println("Player has total: " + playerTotal + "\n");
            //}
              //  System.out.println("Player has total sum: " + playerTotals + "\n");
               // System.out.println("Dealer has: ");

                

           // int dealerTotal = 0;
            //for (int j = 0; j < dealerHand.size(); j++) {

              //  dealerTotal += dealerHand.get(j).getValue();
               // System.out.println(dealerHand.get(j).getValue());

            //}
            //System.out.println("Dealer has total: " + dealerTotal + " \n");
            
            
                 
            
        }
        System.out.println("type true for new round... otherwise game quits");
        Scanner keyboard = new Scanner(System.in);
        String inputstring = keyboard.nextLine();
        if (inputstring.equalsIgnoreCase("true")) {
            newround = true;
        }

        return playerTotals;
    }

    public void round(ArrayList allDecks1) throws Exception {
        this.newround = false;
        ArrayList<Card> allDecks = allDecks1;
        PlayerHand dealerHand = new PlayerHand("dealer");
        //int x = 0;
        int idnr = 1;

        PlayerHand playerH = new PlayerHand("namehere, idnr:" + idnr);

        dealerHand.insertCard(allDecks.get(0));
        allDecks.remove(0);

        playerH.insertCard(allDecks.get(0));
        allDecks.remove(0);

        dealerHand.insertCard(allDecks.get(0));
        allDecks.remove(0);

        playerH.insertCard(allDecks.get(0));
        allDecks.remove(0);

        System.out.println("Player has: ");

        //playerTotals.add(player);
        
        ArrayList<PlayerHand> player=countTotals(playerH);
        for(int i=0;i<player.size();i++){
            
            if(i>1){
                System.out.println("alternative total sum:"+player.get(i).getPlayerTotal());
            }else{
                System.out.println("total sum:"+player.get(i).getPlayerTotal());
            }
        }
        
        countTotals(dealerHand);
    }
*/
    public static void main(String[] args) throws Exception {
        System.out.println("Hello");
        //AllDecks testdecks = new AllDecks();
        //ArrayList<Card> allDecks = testdecks.getNewDecks();
        //Collections.shuffle(allDecks);
        //Main main = new Main();
        Logic logic = new Logic();
        System.out.println("server started...");

        //main.newroundTest();
        logic.shuffle();
        logic.newroundTest();
    }

}
