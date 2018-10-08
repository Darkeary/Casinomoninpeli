package server;

import org.junit.Before;
import org.junit.Test;
import util.Card;
import util.Player;
import util.PlayerHand;

import java.util.ArrayList;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class LogicTest {

    private Logic logic;
    private TestView testView = new TestView();

    @Before
    public void setUp() {
        PlayerHand.resetCounter();
        logic = new Logic(testView);
        logic.setDatabaseEnabled(false);
        logic.shuffle();
        logic.addPlayer(new PlayerHand("Player 1"));
        logic.addPlayer(new PlayerHand("Player 2"));
        logic.addPlayer(new PlayerHand("Player 3"));
    }

    // Shuffle muuttaa pakan järjestystä
    @Test
    public void shuffle() {
        Stack<Card> originalCards = (Stack<Card>) logic.getCurrentDecks().clone();

        ArrayList<String> originalTypes = new ArrayList<>();

        for (Card card : originalCards) {
            originalTypes.add(card.getType());
        }

        logic.shuffle();

        Stack<Card> shuffledCards = logic.getCurrentDecks();

        ArrayList<String> shuffledTypes = new ArrayList<>();

        for (Card card : shuffledCards) {
            shuffledTypes.add(card.getType());
        }

        assertThat(shuffledTypes.toArray(), not(equalTo(originalTypes.toArray())));

    }

    // Kun startRound on kutsuttu, kaikilla pelaajilla ja jakajalla on kaksi korttia
    @Test
    public void startRound() {
        System.out.println("\n--startRound--\n");

        // Testataan että ennen kutsua kenelläkään ei ole kortteja
        for (PlayerHand playerHand : logic.getPlayerHands().values()) {
            assertEquals(0, playerHand.getPlayerHand().size());
        }

        testView.setTestToRun("START_ROUND_TEST");
        // Aloitetaan kierros
        logic.startRound();

        // Testataan onko kaikilla nyt kaksi korttia

        for (PlayerHand playerHand : logic.getPlayerHands().values()) {
            assertEquals(2, playerHand.getPlayerHand().size());
        }

    }

    // Kun givePlayerNewCard on kutsuttu, pelaajan käteen tulee uusi kortti
    @Test
    public void givePlayerNewCard() {
        System.out.println("\n--givePlayerNewCard--\n");
        int sizeBeforeAdd = logic.getPlayerHands().get(1).getPlayerHand().size();

        for (int i = 0; i < 10; i++)
            logic.givePlayerNewCard(1);

        int sizeAfterAdd = logic.getPlayerHands().get(1).getPlayerHand().size();

        assertEquals(sizeBeforeAdd + 10, sizeAfterAdd);

    }
    
    @Test
    public void quitRoundRemovesPlayer() {
        System.out.println("\n--quitRoundRemovesPlayer--\n");
        testView.setTestToRun("QUIT_TEST");
        logic.startRound();

        // TestView lähettää lopetus signaalin kaikkina pelaajina.

        assertEquals(0, logic.getPlayerHands().size());

    }

    @Test
    public void resetMakesNewDeckWhenUnder105Cards() {
        System.out.println("\n--resetMakesNewDeckWhenUnder105Cards--\n");
        Card card = logic.givePlayerNewCard(1);
        assertTrue("Pelaajan kättä ei ollut olemassa, koska korttia ei pystytty jakamaan", card != null);
        assertEquals(415, logic.getCurrentDecks().size());
        // Korttien jako toimii

        for (int i = 0; i < 100; i++) {
            logic.givePlayerNewCard(1);
        }

        assertTrue(logic.getCurrentDecks().size() > 105);
        logic.resetGame();
        assertEquals(315, logic.getCurrentDecks().size());
        // Resetointi ei muuta pakkaa jos korttien määrä > 105

        for (int i = 0; i < 211; i++) {
            logic.givePlayerNewCard(1);
        }

        assertTrue(logic.getCurrentDecks().size() < 105);
        logic.resetGame();
        assertEquals(416, logic.getCurrentDecks().size());
        // Resetointi muuttaa pakkaa jos korttien määrä < 105

    }

    @Test
    public void placeBetReducesFundsByBetAmount() {
        System.out.println("\n--placeBetReducesFundsByBetAmount--\n");
        testView.setTestToRun("BET_PLACED_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        PlayerHand playerHand = logic.getPlayerHands().get(1);

        // Eli lopputulos on 90
        assertEquals(90, playerHand.getFunds());

    }

    @Test
    public void fundsIncreasedByBetWhenRoundWon() {
        System.out.println("\n--fundsIncreasedByBetWhenRoundWon--\n");
        testView.setTestToRun("FUNDS_ADD_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        Player player = logic.getPlayerHands().get(1).getHandPlayer();

        // Jakajan summa on aina yli 21, pelaajat voittavat aina
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);

        logic.endRound();

        // Alku funds on 100 ja bet on 10 eli lopputulos voitossa on 110
        assertEquals(110, player.getFunds());

    }

    @Test
    public void blackjackGives2to1Winnings() {
        System.out.println("\n--blackjackGives2to1Winnings--\n");
        testView.setTestToRun("FUNDS_ADD_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        PlayerHand playerHand = logic.getPlayerHands().get(1);

        // Jakajan summa on aina yli 21, pelaajat voittavat aina
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);

        // Annetaan pelaajalle blackjack
        playerHand.getPlayerHand().add(AllDecks.ten);
        playerHand.getPlayerHand().add(AllDecks.ace);

        logic.endRound();

        // Alku funds on 100 ja bet on 10 eli lopputulos voitossa on 120
        assertEquals(120, playerHand.getFunds());

    }

    @Test
    public void sameTotalAsDealerEndsInDraw() {
        System.out.println("\n--sameTotalAsDealerEndsInDraw--\n");
        testView.setTestToRun("FUNDS_ADD_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        PlayerHand playerHand = logic.getPlayerHands().get(1);

        // Pelaajalla blackjack
        playerHand.getPlayerHand().add(AllDecks.ten);
        playerHand.getPlayerHand().add(AllDecks.ace);

        // Jakajalla blackjack, tasapeli
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.ace);

        logic.endRound();

        // Alku funds on 100 ja bet on 10, mutta tasapeli eli lopputulos vieläkin 100
        assertEquals(100, playerHand.getFunds());
    }

    @Test
    public void sameTotalAsDealerButOver21StillLoses() {
        System.out.println("\n--sameTotalAsDealerEndsInDraw--\n");
        testView.setTestToRun("FUNDS_ADD_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        PlayerHand playerHand = logic.getPlayerHands().get(1);

        // Pelaajalla yli 21, summa 30
        playerHand.getPlayerHand().add(AllDecks.ten);
        playerHand.getPlayerHand().add(AllDecks.ten);
        playerHand.getPlayerHand().add(AllDecks.ten);

        // Jakajalla yli 21, summa 30
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);

        logic.endRound();

        // Alku funds on 100 ja bet on 10 eli lopputulos häviössä 90
        assertEquals(90, playerHand.getFunds());
    }


    @Test
    public void doublingDoublesBet() {
        System.out.println("\n--doublingDoublesBet--\n");
        testView.setTestToRun("DOUBLE_TEST");

        // Alku funds on 100 ja bet on 10
        logic.askForAndSetRoundBets();

        PlayerHand playerHand = logic.getPlayerHands().get(1);

        // Pelaajalla summa >= 9 && summa <= 11
        playerHand.getPlayerHand().add(AllDecks.four);
        playerHand.getPlayerHand().add(AllDecks.six);

        // Jakajalla summa yli 21, pelaaja voittaa aina
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);
        logic.getDealerHand().insertCard(AllDecks.king);

        logic.playerTurn(playerHand.getInGameId());

        // Alku funds on 100 ja bet on 10, mutta tuplattiin joten lopputulos voitossa 120
        assertEquals(120, playerHand.getFunds());

    }

}