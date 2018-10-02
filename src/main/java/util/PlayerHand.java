/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Anders
 * @author Tuomas
 * <p>
 * Luokka kuvaa pelaajan blacjack kortti "kättä".
 * Luokassa säilytetään pelin aikana jaetut kortit ja lasketaan korttien summa.
 */
@Entity
@Table(name = "player_hands")
public class PlayerHand implements Serializable {

    // Näillä on merkitystä vain pelin aikana
    @Transient
    private static AtomicInteger sequence = new AtomicInteger(0);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long databaseHandId;
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Card> playerCards = new Stack<>();
    private int total;
    @Transient
    private long inGameId;

    public PlayerHand() {
        this.inGameId = sequence.getAndIncrement();
    }

    /**
     * Yksinkertainen konstruktori.
     * Asettaa kädelle uniikin id numeron.
     *
     * @param name pelaajan nimi
     */
    public PlayerHand(String name) {
        this.name = name;
        this.inGameId = sequence.getAndIncrement();
    }

    public PlayerHand(String name, Long id) {
        this.name = name;
        this.inGameId = id;
    }

    static public void resetCounter() {
        sequence.set(0);
    }

    /**
     * Lisää kortin pelaajan käteen.
     *
     * @param card käteen lisättävä kortti
     */
    public void insertCard(Card card) {
        getPlayerHand().add(card);
    }

    /**
     * Tyhtentää käden korteista
     */
    public void clear() {
        playerCards.clear();
    }

    /**
     * Laskee käden tämänhektisen summan.
     * Ässät lasketaan niin että saatu summa on mahdollisimman lähellä kahtakymmentäyhtä, mutta ei koskaan sen yli.
     */
    public int getPlayerTotal() {
        total = 0;
        ArrayList<Card> aces = new ArrayList<>();

        // Käydään kaikki kortit läpi
        for (Card card : playerCards) {

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
            if (acesMaxValue < valueLeft) {
                total += acesMaxValue;
            } else {
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

    public String toString() {
        String handString = "| ";

        for (Card card : playerCards) {
            handString += card.getType() + " | ";
        }

        return handString;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInGameId() {
        return inGameId;
    }

    public Collection<Card> getPlayerHand() {
        return this.playerCards;
    }

    public long getDatabaseHandId() {
        return databaseHandId;
    }

    public void setDatabaseHandId(long databaseHandId) {
        this.databaseHandId = databaseHandId;
    }
}
