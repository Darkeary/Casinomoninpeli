/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.casinomoninpeli;

import java.util.ArrayList;

/**
 *
 * @author Anders
 */
public class PlayerHand {
    
    PlayerHand(){
        System.out.println("noname player.");
    }
    PlayerHand(String string){
        this.name=string;
        System.out.println("player name: "+ this.name  );
    }
    private String name;
    public ArrayList<Card> playerhand= new ArrayList<>();

    public String getName(){
        return this.name;
    }
    public void insertCard(Card card){
        playerhand.add(card);
    }

    ArrayList getPlayerHand() {
        return this.playerhand;
    }
}
