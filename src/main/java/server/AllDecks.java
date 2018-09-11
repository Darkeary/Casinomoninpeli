/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.Card;

import java.util.Stack;


/**
 * @author Anders
 */
public class AllDecks {

    public Stack<Card> decks = new Stack<>();

    AllDecks() {

        this.newDecksGenerator();

    }

    public Stack<Card> getNewDecks() {
        return (Stack<Card>) decks.clone();
    }

    private Stack<Card> newDecksGenerator() {

        for (int i = 0; i < 4 * 8; i++) {

            decks.push(new Card(2, "2"));
            decks.push(new Card(3, "3"));
            decks.push(new Card(4, "4"));
            decks.push(new Card(5, "5"));
            decks.push(new Card(6, "6"));
            decks.push(new Card(7, "7"));
            decks.push(new Card(8, "8"));
            decks.push(new Card(9, "9"));
            decks.push(new Card(10, "10"));
            decks.push(new Card(10, "J"));
            decks.push(new Card(10, "Q"));
            decks.push(new Card(10, "K"));
            decks.push(new Card(11, 1, "A"));
        }
        System.out.print("cardDecks created.");
        return decks;
    }

}
