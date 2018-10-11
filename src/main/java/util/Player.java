package util;

import javax.persistence.*;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Luokka kapseloi kaikki pelaajaan liittyvät tiedot
 *
 * @author Tuomas
 */
@Entity
@Table(name = "players")
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long databasePlayerId;

    @Transient
    private static AtomicInteger sequence = new AtomicInteger(0);

    @Transient
    private int inGameId;

    @Transient
    private int bet;

    private String name;
    private int funds = 100;

    static public void resetCounter() {
        sequence.set(0);
    }

    public Player() {
        this.inGameId = sequence.getAndIncrement();
    }

    public Player(String name) {
        this.name = name;
        this.inGameId = sequence.getAndIncrement();
    }

    public Player(int inGameId, String name) {
        this.inGameId = inGameId;
        this.name = name;
    }

    public Player(int inGameId, String name, int funds, int bet) {
        this.inGameId = inGameId;
        this.name = name;
        this.funds = funds;
        this.bet = bet;
    }

    public int getInGameId() {
        return inGameId;
    }

    public void setInGameId(int inGameId) {
        this.inGameId = inGameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFunds() {
        return funds;
    }

    public void setFunds(int funds) {
        this.funds = funds;
    }

    public void addToFunds(int fundsToAdd) {
        funds += fundsToAdd;
    }

    public int getBet() {
        return bet;
    }

    public void addToBet(int betToAdd) {
        bet += betToAdd;
        if (betToAdd > 0) {
            funds -= betToAdd;
        }
    }

    public long getDatabasePlayerId() {
        return databasePlayerId;
    }

    public void setDatabasePlayerId(long databasePlayerId) {
        this.databasePlayerId = databasePlayerId;
    }
}
