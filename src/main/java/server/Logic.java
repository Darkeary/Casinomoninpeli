package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.CardCounterPrediction;
import util.PlayerBet;
import util.PlayerHand;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static communication.PlayerAction.*;

/**
 * Luokka sisältää blacjack pelin logiikan ja tilan. Kaikki pelin eteneminen ja
 * muutokset tapahtuvat täällä.
 *
 * @author Anders
 * @author Tuomas
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
    private HashMap<Integer, PlayerHand> playerHands = new HashMap<>();
    private boolean playerHandsAccess = false;

    /**
     * Pelaajien id:eistä koostuva vuorolista
     */
    private LinkedList<Integer> playerTurns = new LinkedList<>();

    /**
     * Pelin kortinlaskija
     */
    private CardCounter cardCounter = new CardCounter();

    private boolean databaseEnabled = false;

    /* --------------------- */

    /**
     * Konstruktori. serverListenerissä annettuun parametriin lähetetään pelin
     * aikana luotuja GameState olioita. ServerListenerin tehtävänä on viedä
     * nämä oliot eteenpäin client puolelle
     *
     * @param serverListener Luokka mikä toteuttaa serverListener rajapinnan
     */
    public Logic(ServerListener serverListener) {
        this.serverListener = serverListener;
        currentGameState = new GameState();
        serverListener.setGameLogic(this);
        serverListener.startListener();
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

    public void removePlayer(int playerId) {
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

    public synchronized PlayerHand getPlayer(int playerId) {

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
     * kaikki vuorolistan pelaajat on käyty läpi
     *
     * @param playerId Tällä hetkellä vuorossa olevan pelaajan id. Yleensä haetaan playerTurns listasta
     */
    public void playerTurn(Integer playerId) {
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

        // Annetaan kortinlaskijalle
        CardCounterPrediction prediction = cardCounter.getNewPredictionBasedOnGameState(currentGameState);

        // Lisätään lähetettävään olioon
        currentGameState.addCardCounterPrediction(prediction);

        // Annetaan pelaajille
        PlayerAction pAction = serverListener.sendGameStateAndWaitForReply();
        int action = pAction.getActionId();

        Stack<Card> playerCards = (Stack<Card>) playerHand.getPlayerHand();
        int playerTotal = playerHand.getPlayerTotal();

        switch (action) {
            case HIT:
                givePlayerNewCard(playerId);
                playerTurn(playerId);
                break;
            case STAY:
                playerTurn(playerTurns.pollFirst());
                break;
            case DOUBLE:
                if (playerCards.size() == 2 && playerTotal >= 9 && playerTotal <= 11) {
                    playerHand.addToBet(playerHand.getBet());
                    givePlayerNewCard(playerId);
                    playerTurn(playerTurns.pollFirst());
                }
                break;
            case SPLIT:
                if (playerCards.size() == 2) {
                        /*
                        Card cardOne = playerCards.get(0);
                        Card cardTwo = playerCards.get(1);

                        if(cardOne.getType().contentEquals(cardTwo.getType())) {
                            playerHands.remove(playerId);

                            PlayerHand playerHand1 = new PlayerHand(playerHand.getHandPlayer());
                            PlayerHand playerHand2 = new PlayerHand(playerHand.getHandPlayer());

                            playerHand1.getPlayerHand().add(cardOne);
                            playerHand2.getPlayerHand().add(cardOne);

                            playerHand1.setSplitHand(playerHand2);

                            playerHands.put(playerId, playerHand1);

                            playerTurn(playerId);

                        }
                        */
                }
                break;
            case QUIT:
                playerHands.remove(playerId);
                break;
            default:
                break;
        }
    }

    /**
     * Aloittaa uuden kierroksen eli tyhjentää aikaisemman vuoron tilan ja jakaa kaikille kaksi korttia
     */
    public void startRound() {

        if (playerHands.size() == 0) return;

        resetGame();

        currentGameState.gameEnded = false;

        askForAndSetRoundBets();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        dealerHand.insertCard(currentDecks.pop());

        giveAllPlayersNewCard();

        playerTurn(playerTurns.pollFirst());

    }

    /**
     * Muuttaa pelin tilan sen alkuperäiseen muotoon
     */
    public void resetGame() {
        playerTurns = new LinkedList<>(playerHands.keySet());

        if (currentDecks.size() < 105) {
            currentDecks = allDecks.getNewDecks();
            shuffle();
        }
    }

    /**
     * Antaa kaikille peliin kuuluville pelaajille kortin
     */
    public void giveAllPlayersNewCard() {
        for (Integer playerId : playerHands.keySet()) {
            givePlayerNewCard(playerId);
        }
    }

    /**
     * Antaa pelaajalle kortin.
     *
     * @param playerId Pelaajan id kenelle halutaan antaa kortti
     */
    public Card givePlayerNewCard(int playerId) {
        PlayerHand playerHand = getPlayer(playerId);
        if (playerHand != null) {
            Card newCard = currentDecks.pop();
            playerHand.insertCard(newCard);
            return newCard;
        } else {
            return null;
        }
    }

    /**
     * Lopettaa kierroksen. Jakaa kortit jakajalle ja katsoo kuka pelaajista voitti
     */
    void endRound() {

        while (dealerHand.getPlayerTotal() < 17) {
            dealerHand.insertCard(currentDecks.pop());
        }

        for (PlayerHand playerHand : playerHands.values()) {
            if (playerHasWon(playerHand, dealerHand)) {

                calculateAndAddRoundWinnings(playerHand);

                playerHand.setHandWon(true);

            } else if (playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() == dealerHand.getPlayerTotal()) {
                playerHand.addToFunds(playerHand.getBet());
            }
        }

        // Tilasto osuus - kommentoi pois jos ei PuTTY tunnelia

        if (isDatabaseEnabled()) {

            Statistic statForRound = new Statistic(playerHands.values(), dealerHand);

            DatabaseInterface dbIF = DatabaseInterface.getInstance();

            dbIF.saveStatistic(statForRound);

            List<Statistic> stats = dbIF.getStatistics();

            CalculateStatistics.calculateWinStats(stats);

        }

        // --------------------------------

        currentGameState.addStatistics(CalculateStatistics.getTotalPlayerRounds(), CalculateStatistics.getTotalPlayerWins(), CalculateStatistics.getPlayerWinPercentage());

        currentGameState.setGameState(playerHands, dealerHand, -1, true);

        List<Future<PlayerAction>> playerActions = serverListener.askForRoundParticipation();

        doNextRound(playerActions);
    }

    public static boolean playerHasWon(PlayerHand playerHand, PlayerHand dealerHand) {
        return (playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() > dealerHand.getPlayerTotal())
                || (dealerHand.getPlayerTotal() > 21 && playerHand.getPlayerTotal() <= 21);
    }

    /**
     * Kysyy pelaajilta haluavatko he osallistua seuraavalle kierrokselle ja aloittaa sen
     */
    public void doNextRound(List<Future<PlayerAction>> futures) {

        ArrayList<PlayerHand> playersToContinue = new ArrayList<>();

        if (futures == null) return;

        for (Future<PlayerAction> future : futures) {
            try {

                PlayerAction pAction = future.get();
                int action = pAction.getActionId();

                if (action != PlayerAction.QUIT) {
                    PlayerHand playerHand = playerHands.get(pAction.getPlayerId());
                    playerHand.resetBet();
                    playersToContinue.add(playerHand);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        playerHands.clear();

        // Pelaajien kädet uusitaan Hibernaten takia. Halutaan tehdä uusi rivi tietokantaan.
        for (PlayerHand playerHand : playersToContinue) {
            addPlayer(new PlayerHand(playerHand.getHandPlayer()));
        }

        dealerHand = new PlayerHand(dealerHand.getHandPlayer());

        startRound();
    }

    /**
     * Kerää pelaajilta tiedon kierrokselle asetettavasta panoksesta
     */
    void askForAndSetRoundBets() {

        List<Future<PlayerBet>> futures = serverListener.askForRoundBet();

        if (futures == null) return;

        for (Future<PlayerBet> future : futures) {
            try {

                PlayerBet bet = future.get();

                System.out.println("Pelaajan " + bet.playerId + " panos on " + bet.playerBet);

                playerHands.get(bet.playerId).setBet(bet.playerBet);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Laskee pelaajan voitot olettaen että pelaaja on voittanut ja se on tarkistettu.
     */
    private int calculateAndAddRoundWinnings(PlayerHand playerHand) {

        int winnings;

        //  Mielivaltaisesti päätetty että blacjack saa 2:1 voiton
        if (playerHand.getPlayerTotal() == 21)
            winnings = playerHand.getBet() + playerHand.getBet() * 2;
        else
            winnings = playerHand.getBet() * 2;

        playerHand.addToFunds(winnings);

        return winnings;
    }

    Stack<Card> getCurrentDecks() {
        return currentDecks;
    }

    public PlayerHand getDealerHand() {
        return dealerHand;
    }

    public HashMap<Integer, PlayerHand> getPlayerHands() {
        return playerHands;
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public boolean isDatabaseEnabled() {
        return databaseEnabled;
    }

    public void setDatabaseEnabled(boolean databaseEnabled) {
        this.databaseEnabled = databaseEnabled;
    }
}
