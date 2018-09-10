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
        //System.out.println("noname player.");
    }
    PlayerHand(String string){
        this.name=string;
        System.out.println("player name: "+ this.name  );
    }
    private String name;
    private int total=0;
    public ArrayList<Card> playerhand= new ArrayList<>();

    public String getName(){
        return this.name;
    }
    public void insertCard(Card card){
        playerhand.add(card);
    }

    public ArrayList getPlayerHand() {
        return this.playerhand;
    }
    public int getPlayerTotal() {
        return this.total;
    }
    public void setPlayerTotal(int integ) {
        this.total=integ;
    }
}
