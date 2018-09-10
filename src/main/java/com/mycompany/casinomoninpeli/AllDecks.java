/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.casinomoninpeli;

import java.util.ArrayList;
import java.util.Random;



/**
 *
 * @author Anders
 */
public class AllDecks {
    
    AllDecks(){
        
        this.newDecksGenerator();
    
    }
    
    public  ArrayList<Card> newDecks=new ArrayList<>();

    public ArrayList<Card> getNewDecks(){
        return newDecks;
    }
            
    private ArrayList<Card> newDecksGenerator(){
        //ArrayList<Card> newDecks=new ArrayList<>();
        //Random rgen = new Random();
        for(int i=0;i<4*8;i++){
            
            newDecks.add(new Card(2,"2"));
            newDecks.add(new Card(3,"3"));
            newDecks.add(new Card(4,"4"));
            newDecks.add(new Card(5,"5"));
            newDecks.add(new Card(6,"6"));
            newDecks.add(new Card(7,"7"));
            newDecks.add(new Card(8,"8"));
            newDecks.add(new Card(9,"9"));
            newDecks.add(new Card(10,"10"));
            newDecks.add(new Card(10,"J"));
            newDecks.add(new Card(10,"Q"));
            newDecks.add(new Card(10,"K"));
            newDecks.add(new Card(11,1,"A"));
        }
        System.out.print("cardDecks created.");
        return newDecks;
        
    }
    
}
