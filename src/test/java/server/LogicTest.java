package server;

import org.junit.Before;
import org.junit.Test;
import util.Card;
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

        int sizeBeforeAdd = logic.getPlayerHands().get((long) 1).getPlayerHand().size();

        for (int i = 0; i < 10; i++)
            logic.givePlayerNewCard((long) 1);

        int sizeAfterAdd = logic.getPlayerHands().get((long) 1).getPlayerHand().size();

        assertEquals(sizeBeforeAdd + 10, sizeAfterAdd);

    }
    
    @Test
    public void quitRoundRemovesPlayer() {
        testView.setTestToRun("QUIT_TEST");
        logic.startRound();

        // TestView lähettää lopetus signaalin kaikkina pelaajina.

        assertEquals(0, logic.getPlayerHands().size());

    }

    @Test
    public void resetMakesNewDeckWhenUnder105Cards() {


        Card card = logic.givePlayerNewCard((long) 1);
        assertTrue("Pelaajan kättä ei ollut olemassa, koska korttia ei pystytty jakamaan", card != null);
        assertEquals(415, logic.getCurrentDecks().size());
        // Korttien jako toimii

        for (int i = 0; i < 100; i++) {
            logic.givePlayerNewCard((long) 1);
        }

        assertTrue(logic.getCurrentDecks().size() > 105);
        logic.resetGame();
        assertEquals(315, logic.getCurrentDecks().size());
        // Resetointi ei muuta pakkaa jos korttien määrä > 105

        for (int i = 0; i < 211; i++) {
            logic.givePlayerNewCard((long) 1);
        }

        assertTrue(logic.getCurrentDecks().size() < 105);
        logic.resetGame();
        assertEquals(416, logic.getCurrentDecks().size());
        // Resetointi muuttaa pakkaa jos korttien määrä < 105

    }
}