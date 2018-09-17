package server;

import communication.GameState;
import communication.PlayerAction;
import java.util.ArrayList;
import util.Card;
import util.PlayerHand;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author Anders
 * @author Tuomas
 *
 * Luokka sisältää blacjack pelin logiikan ja tilan. Kaikki pelin eteneminen ja
 * muutokset tapahtuvat täällä.
 *
 */
public class Logic {

    /* MUUTTUJAT */
    /**
     * Perus pakka mistä tehdään vain kopioita, ei sekoiteta tätä
     */
    private static final AllDecks allDecks = new AllDecks();

    private final ServerListener serverListener;

    /**
     * Tällä hetkellä pelissä käytössä oleva pakka
     */
    private Stack<Card> currentDecks = allDecks.getNewDecks();

    /**
     * Jakajan käsi
     */
    private PlayerHand dealerHand = new PlayerHand("dealer");

    /**
     * Pelaajien kädet järjestettynä id:n mukaan
     */
    private HashMap<Long, PlayerHand> playerHands = new HashMap<>();

    /**
     * Pelaajien id:eistä koostuva vuorolista
     */
    private LinkedList<Long> playerTurns = new LinkedList<>();

    /* --------------------- */
    /**
     * Konstruktori. serverListenerissä annettuun parametriin lähetetään pelin
     * aikana luotuja GameState olioita. ServerListenerin tehtävänä on viedä
     * nämä oliot eteenpäin client puolelle.
     *
     * @param serverListener Luokka mikä toteuttaa serverListener rajapinnan
     */
    public Logic(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void addPlayer(PlayerHand playerHand) {
        playerHands.put(playerHand.getId(), playerHand);
    }

    public void shuffle() {
        Collections.shuffle(currentDecks);
    }

    /**
     * Keskustelee serverListener rajapinnan välityksellä client koneille. Tämä
     * rutiini kutsuu itseään käyttäen vuorolistassa olevia id arvoja kunnes
     * kaikki vuorolistan pelaajat on käyty läpi.
     *
     * @param playerId Tällä hetkellä vuorossa olevan pelaajan id. Yleensä haetaan playerTurns listasta.
     */
    public void playerTurn(Long playerId) {
        if (serverListener == null) {
            return;
        }
        if (playerId == null) {
            endRound();
            return;
        }

        PlayerHand playerHand = playerHands.get(playerId);
        if (playerHand.getPlayerTotal() >= 21) {
            playerTurn(playerTurns.pollFirst());
            return;
        }

        GameState currentState = new GameState(playerHands, dealerHand, playerId, false);
        PlayerAction action = serverListener.sendGameStateAndWaitForReply(currentState);

        if (action == PlayerAction.HIT) {
            givePlayerNewCard(playerId);
            playerTurn(playerId);
        } else if (action == PlayerAction.STAY) {
            playerTurn(playerTurns.pollFirst());
        } else if (action == PlayerAction.QUIT) {
            playerHands.remove(playerId);
        }
    }

    /**
     * Aloittaa uuden kierroksen eli tyhjentää aikaisemman vuoron tilan ja jakaa kaikille kaksi korttia.
     */
    public void startRound() {
        
        if(playerHands.size() == 0) return;

        resetGame();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        playerTurn(playerTurns.pollFirst());

    }

    /**
     * Muuttaa pelin tilan sen alkuperäiseen muotoon.
     */
    public void resetGame() {
        dealerHand.clear();
        for (PlayerHand playerHand : playerHands.values()) {
            playerHand.clear();
        }
        playerTurns = new LinkedList<>(playerHands.keySet());
        
        if(currentDecks.size() > 104) {
            currentDecks = allDecks.getNewDecks();
            shuffle();
        }
    }

    /**
     * Antaa kaikille peliin kuuluville pelaajille kortin.
     */
    public void giveAllPlayersNewCard() {
        for (Long playerId : playerHands.keySet()) {
            givePlayerNewCard(playerId);
        }
    }

    /**
     * Antaa pelaajalle kortin.
     * @param playerId Pelaajan id kenelle halutaan antaa kortti
     */
    public void givePlayerNewCard(Long playerId) {
        PlayerHand playerHand = playerHands.get(playerId);
        if (playerHand != null) {
            playerHand.insertCard(currentDecks.pop());
        }
    }

    public void endRound() {

        while (dealerHand.getPlayerTotal() < 17) {
            dealerHand.insertCard(currentDecks.pop());
        }

        System.out.println("Jakajan lopullinen käsi: " + dealerHand);
        System.out.println("Käden summa: " + dealerHand.getPlayerTotal() + "\n");

        for (PlayerHand playerHand : playerHands.values()) {
            if ((playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() > dealerHand.getPlayerTotal())
                    || (dealerHand.getPlayerTotal() > 21 && playerHand.getPlayerTotal() <= 21)) {
                System.out.println(playerHand.getName() + " voitti.");
            }
        }
        
        doNextRound();
    }
    
    /**
     * Kysyy pelaajilta haluavatko he osallistua seuraavalle kierrokselle ja aloittaa sen.
     */
    public void doNextRound() {
        
        ArrayList<Long> playersToRemove = new ArrayList<Long>();
        
        for(PlayerHand playerHand : playerHands.values()) {
           PlayerAction action =  serverListener.askForRoundParticipation(playerHand.getId());
           
           if(action == PlayerAction.QUIT) {
               playersToRemove.add(playerHand.getId());
           }
        }
        
        for(Long playerId : playersToRemove) {
            playerHands.remove(playerId);
        }
        
        startRound();
    }

    Stack<Card> getCurrentDecks() {
        return currentDecks;
    }

    public PlayerHand getDealerHand() {
        return dealerHand;
    }

    public HashMap<Long, PlayerHand> getPlayerHands() {
        return playerHands;
    }
}
