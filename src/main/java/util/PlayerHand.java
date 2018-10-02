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

/**
 * Luokka kuvaa pelaajan blacjack kortti "kättä".
 * Luokassa säilytetään pelin aikana jaetut kortit ja lasketaan korttien summa.
 * @author Anders
 * @author Tuomas
 */
@Entity
@Table(name = "player_hands")
public class PlayerHand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    /**
     * Tietokannassa käytettävä id
     */
    private long databaseHandId;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Card> playerCards = new Stack<>();

    private int total;

    /**
     * Tähän käteen liittyvä panos (jos pelaaja on "jakanut" korttinsa, on hänellä kaksi kättä millä oma panos)
     */
    private int handBet;

    @OneToOne(cascade = CascadeType.ALL)
    private Player handPlayer;

    @Transient
    private PlayerHand splitHand;

    public PlayerHand() {
        this.inGameId = sequence.getAndIncrement();
    }

    /**
     * @param name pelaajan nimi
     */
    public PlayerHand(String name) {
        handPlayer = new Player(name);
    }

    /**
     * @param name pelaajan nimi
     * @param id   pelaajan id
     */
    public PlayerHand(String name, Long id) {
        handPlayer = new Player(id, name);
    }

    /**
     * @param handPlayer Käden pelaajan Player olio
     */
    public PlayerHand(Player handPlayer) {
        this.handPlayer = handPlayer;
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

            // Jos kaikki ässät 11 pysyy 21 tai alle, lisätään suoraan
            if (acesMaxValue <= valueLeft) {
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

    public PlayerHand getSplitHand() {
        return splitHand;
    }

    public void setSplitHand(PlayerHand splitHand) {
        this.splitHand = splitHand;
    }

    public Player getHandPlayer() {
        return handPlayer;
    }

    public int getFunds() {
        return handPlayer.getFunds();
    }

    public void setFunds(int funds) {
        handPlayer.setFunds(funds);
    }

    public void addToFunds(int fundsToAdd) {
        handPlayer.addToFunds(fundsToAdd);
    }

    public int getBet() {
        return handBet;
    }

    public void resetBet() {
        handPlayer.addToBet(-handBet);
        handBet = 0;
    }

    public void addToBet(int betToAdd) {
        handBet += betToAdd;
        handPlayer.addToBet(betToAdd);
    }

    public void setBet(int bet) {
        handPlayer.addToBet(-handBet);
        handBet = bet;
        handPlayer.addToBet(bet);
    }

    static public void resetCounter() {
        Player.resetCounter();
    }

    public String getName() {
        return handPlayer.getName();
    }

    public void setName(String name) {
        handPlayer.setName(name);
    }

    public long getInGameId() {
        return handPlayer.getInGameId();
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
