package server;

import org.junit.Before;
import org.junit.Test;
import util.Card;
import util.PlayerHand;

import java.util.ArrayList;
import java.util.Stack;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LogicTest {

    private Logic logic;

    @Before
    public void setUp() {
        PlayerHand.resetCounter();
        logic = new Logic(null);
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
        assertEquals(0, logic.getDealerHand().getPlayerHand().size());

        for (PlayerHand playerHand : logic.getPlayerHands().values()) {
            assertEquals(0, playerHand.getPlayerHand().size());
        }

        // Aloitetaan kierros
        logic.startRound();

        // Testataan onko kaikilla nyt kaksi korttia
        assertEquals(2, logic.getDealerHand().getPlayerHand().size());

        for (PlayerHand playerHand : logic.getPlayerHands().values()) {
            assertEquals(2, playerHand.getPlayerHand().size());
        }

    }

    @Test
    public void givePlayerNewCard() {
        logic.startRound();

        int sizeBeforeAdd = logic.getPlayerHands().get((long) 1).getPlayerHand().size();

        for (int i = 0; i < 10; i++)
            logic.givePlayerNewCard((long) 1);

        int sizeAfterAdd = logic.getPlayerHands().get((long) 1).getPlayerHand().size();

        assertEquals(sizeBeforeAdd + 10, sizeAfterAdd);

    }
}