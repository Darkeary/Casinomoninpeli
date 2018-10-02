package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.PlayerHand;

import java.util.*;

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

    /**
     * Pelin olio, mikä toteuttaa kommunikaatio kanavan pelaajiin
     */
    private final ServerListener serverListener;


    /**
     * Pelin tämänhetkisen tilan säilyttävä olio
     */
    private final GameState currentGameState;

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
    private boolean playerHandsAccess = false;

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
        currentGameState = new GameState();
        serverListener.setGameLogic(this);

    }

    public synchronized PlayerHand addPlayer(PlayerHand playerHand) {

        while (playerHandsAccess) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        playerHandsAccess = true;

        playerHands.put(playerHand.getInGameId(), playerHand);

        playerHandsAccess = false;
        notify();

        return playerHand;
    }

    public void removePlayer(long playerId) {
        while (playerHandsAccess) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        playerHandsAccess = true;

        playerHands.remove(playerId);

        playerHandsAccess = false;
        notify();
    }

    public synchronized PlayerHand getPlayer(long playerId) {

        while (playerHandsAccess) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        playerHandsAccess = true;

        PlayerHand hand = playerHands.get(playerId);

        playerHandsAccess = false;
        notify();

        return hand;
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

        PlayerHand playerHand = getPlayer(playerId);
        if (playerHand.getPlayerTotal() >= 21) {
            playerTurn(playerTurns.pollFirst());
            return;
        }

        currentGameState.setGameState(playerHands, dealerHand, playerId, false);
        PlayerAction pAction = serverListener.sendGameStateAndWaitForReply();
        int action = pAction.getActionId();

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

        if (currentDecks.size() < 105) {
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
    public Card givePlayerNewCard(Long playerId) {
        PlayerHand playerHand = getPlayer(playerId);
        if (playerHand != null) {
            Card newCard = currentDecks.pop();
            playerHand.insertCard(newCard);
            return newCard;
        } else {
            return null;
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

        Statistic statForRound = new Statistic(playerHands.values(), dealerHand);

        DatabaseInterface dbIF = DatabaseInterface.getInstance();

        dbIF.saveStatistic(statForRound);

        System.out.println("\nJakajan kädet aikaisemmilta kierroksilta:");

        List<Statistic> stats = dbIF.getStatistics();

        for (Statistic stat : stats) {
            System.out.println(stat.getId() + ": ");
            System.out.println(stat.getDealerHand());
        }

        System.out.print("\n");

        doNextRound();
    }

    /**
     * Kysyy pelaajilta haluavatko he osallistua seuraavalle kierrokselle ja aloittaa sen.
     */
    public void doNextRound() {

        ArrayList<Long> playersToRemove = new ArrayList<Long>();
        ArrayList<Long> playersToContinue = new ArrayList<Long>();

        for(PlayerHand playerHand : playerHands.values()) {
            PlayerAction pAction = serverListener.askForRoundParticipation(playerHand.getInGameId());
            int action = pAction.getActionId();

           if(action == PlayerAction.QUIT) {
               playersToRemove.add(playerHand.getInGameId());
           } else {
               playersToContinue.add(playerHand.getInGameId());
           }
        }

        for(Long playerId : playersToRemove) {
            playerHands.remove(playerId);
        }


        // Pelaajien kädet uusitaan Hibernaten takia. Halutaan tehdä uusi rivi tietokantaan.
        for (Long playerId : playersToContinue) {
            String playerName = getPlayer(playerId).getName();
            addPlayer(new PlayerHand(playerName, playerId));
        }

        String dealerName = dealerHand.getName();
        Long dealerId = dealerHand.getInGameId();

        dealerHand = new PlayerHand(dealerName, dealerId);

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

    public GameState getCurrentGameState() {
        return currentGameState;
    }
}
