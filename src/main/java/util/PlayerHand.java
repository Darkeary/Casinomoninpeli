/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Anders
 */
public class PlayerHand {

    private static AtomicInteger sequence = new AtomicInteger(0);
    private final String name;
    private final long id;
    private Stack<Card> playerHand = new Stack<>();
    private int total;

    public PlayerHand(String name) {
        this.name = name;
        this.id = sequence.getAndIncrement();
    }

    public void insertCard(Card card) {
        playerHand.push(card);
    }

    public void empty() {
        playerHand.empty();
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return id;
    }

    public Stack getPlayerHand() {
        return this.playerHand;
    }

    public int getPlayerTotal() {
        total = 0;
        ArrayList<Card> aces = new ArrayList<>();

        // Käydään kaikki kortit läpi
        for (Card card : playerHand) {

            String type = card.getType();

            // Otetaan ässät talteen muistiin ja summataan muiden korttien arvot
            if (type.equals("A")) {
                aces.add(card);
            } else {
                total += card.getValue();
            }
        }

        // Jos summa missä kaikki ässät on ykkösiä on 21 tai enemmän, lisätään suoraan
        if (total + aces.size() >= 21) {
            for (Card ace : aces) {
                total += ace.getValue2();
            }
        } else {

            int valueLeft = 21 - total;
            int acesMaxValue = aces.size() * 11;

            // Jos kaikki ässät 11 pysyy alle 21, lisätään suoraan
            if (acesMaxValue < valueLeft) total += acesMaxValue;
            else {
                // Muussa tapauksessa muutetaan ässiä ykkösiksi kunnes päästään 21 tai alle
                int valueToAdd = acesMaxValue;
                while (valueLeft - valueToAdd <= 0) {
                    valueToAdd -= 10;
                }
                total += valueToAdd;
            }
        }

        return total;
    }

    static public void resetCounter() {
        sequence.set(0);
    }

    public String toString() {
        String handString = "| ";

        for (Card card : playerHand) {
            handString += card.getType() + " | ";
        }

        return handString;
    }

}
