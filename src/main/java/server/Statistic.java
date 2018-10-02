/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import util.PlayerHand;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Luokka sisältää kaikki tietokantaan tallennettavat tilastoitavat tiedot.
 * @author Tuomas
 */
@Entity
@Table(name = "statistics")
public class Statistic implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<PlayerHand> gameHands;

    @OneToOne(cascade = CascadeType.ALL)
    private PlayerHand dealerHand;

    public Statistic() {
    }

    public Statistic(Collection<PlayerHand> gameHands, PlayerHand dealerHand) {
        this.gameHands = gameHands;
        this.dealerHand = dealerHand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<PlayerHand> getGameHands() {
        return gameHands;
    }

    public void setGameHands(Collection<PlayerHand> gameHands) {
        this.gameHands = gameHands;
    }

    public PlayerHand getDealerHand() {
        return dealerHand;
    }

    public void setDealerHand(PlayerHand dealerHand) {
        this.dealerHand = dealerHand;
    }
}
