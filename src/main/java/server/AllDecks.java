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

    // Näitä käytetty testaamiseen
    static final Card ace = new Card(11, 1, "A");
    static final Card two = new Card(2, "2");
    static final Card three = new Card(3, "3");
    static final Card four = new Card(4, "4");
    static final Card five = new Card(5, "5");
    static final Card six = new Card(6, "6");
    static final Card seven = new Card(7, "7");
    static final Card eight = new Card(8, "8");
    static final Card nine = new Card(9, "9");
    static final Card ten = new Card(10, "10");
    static final Card jack = new Card(10, "J");
    static final Card queen = new Card(10, "Q");
    static final Card king = new Card(10, "K");

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

        return decks;
    }

}
