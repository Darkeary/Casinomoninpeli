package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.CardCounterPrediction;
import util.Player;
import util.PlayerHand;

import java.util.*;

/**
 * Luokka sisältää blacjack pelin logiikan ja tilan. Kaikki pelin eteneminen ja
 * muutokset tapahtuvat täällä.
 * @author Anders
 * @author Tuomas
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

    /**
     * Pelin kortinlaskija
     */
    private CardCounter cardCounter = new CardCounter();

    /* --------------------- */

    /**
     * Konstruktori. serverListenerissä annettuun parametriin lähetetään pelin
     * aikana luotuja GameState olioita. ServerListenerin tehtävänä on viedä
     * nämä oliot eteenpäin client puolelle
     *
     * @param serverListener Luokka mikä toteuttaa serverListener rajapinnan
     */
    Logic(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    void addPlayer(PlayerHand playerHand) {
        playerHands.put(playerHand.getInGameId(), playerHand);
    }

    void shuffle() {
        Collections.shuffle(currentDecks);
    }

    /**
     * Keskustelee serverListener rajapinnan välityksellä client koneille. Tämä
     * rutiini kutsuu itseään käyttäen vuorolistassa olevia id arvoja kunnes
     * kaikki vuorolistan pelaajat on käyty läpi
     *
     * @param playerId Tällä hetkellä vuorossa olevan pelaajan id. Yleensä haetaan playerTurns listasta
     */
    void playerTurn(Long playerId) {
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

        // Annetaan kortinlaskijalle
        CardCounterPrediction prediction = cardCounter.getNewPredictionBasedOnGameState(currentState);

        // Lisätään lähetettävään olioon
        currentState.addCardCounterPrediction(prediction);

        // Annetaan pelaajille
        PlayerAction action = serverListener.sendGameStateAndWaitForReply(currentState);

        Stack<Card> playerCards = (Stack<Card>) playerHand.getPlayerHand();
        int playerTotal = playerHand.getPlayerTotal();

        if (action != null)
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
    void startRound() {

        if (playerHands.size() == 0) return;

        resetGame();

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
    void resetGame() {
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
     * Antaa kaikille peliin kuuluville pelaajille kortin
     */
    void giveAllPlayersNewCard() {
        for (Long playerId : playerHands.keySet()) {
            givePlayerNewCard(playerId);
        }
    }

    /**
     * Antaa pelaajalle kortin.
     *
     * @param playerId Pelaajan id kenelle halutaan antaa kortti
     */
    Card givePlayerNewCard(Long playerId) {
        PlayerHand playerHand = playerHands.get(playerId);
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

        System.out.println("Jakajan lopullinen käsi: " + dealerHand);
        System.out.println("Käden summa: " + dealerHand.getPlayerTotal() + "\n");

        for (PlayerHand playerHand : playerHands.values()) {
            if (playerHasWon(playerHand, dealerHand)) {

                System.out.println(playerHand.getName() + " voitti.");
                int winnings = calculateAndAddRoundWinnings(playerHand);

                System.out.println("Voitettu: " + (winnings - playerHand.getHandPlayer().getBet()));
                System.out.println("Pisteitä jäljellä: " + playerHand.getFunds());

            } else if (playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() == dealerHand.getPlayerTotal()) {
                System.out.println(playerHand.getName() + " tasapeli.");
                playerHand.addToFunds(playerHand.getBet());
                System.out.println("Pisteitä jäljellä: " + playerHand.getFunds());
            }
        }

        // Tilasto osuus - kommentoi pois jos ei PuTTY tunnelia

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

        CalculateStatistics.calculateWinStats(stats);

        System.out.println("Pelattujen kierrosten määrä: " + CalculateStatistics.getTotalPlayerRounds());
        System.out.println("Pelaajien voittomäärä: " + CalculateStatistics.getTotalPlayerWins());
        System.out.println("Pelaajien voittoprosentti: " + CalculateStatistics.getPlayerWinPercentage() + "%");

        // --------------------------------

        doNextRound();
    }

    public static boolean playerHasWon(PlayerHand playerHand, PlayerHand dealerHand) {
        return (playerHand.getPlayerTotal() <= 21 && playerHand.getPlayerTotal() > dealerHand.getPlayerTotal())
                || (dealerHand.getPlayerTotal() > 21 && playerHand.getPlayerTotal() <= 21);
    }

    /**
     * Kysyy pelaajilta haluavatko he osallistua seuraavalle kierrokselle ja aloittaa sen
     */
    void doNextRound() {

        ArrayList<Long> playersToRemove = new ArrayList<>();
        ArrayList<Long> playersToContinue = new ArrayList<>();

        for (PlayerHand playerHand : playerHands.values()) {
            PlayerAction action = serverListener.askForRoundParticipation(playerHand.getInGameId());

            if (action == PlayerAction.QUIT) {
                playersToRemove.add(playerHand.getInGameId());
            } else {
                playerHand.resetBet();
                playersToContinue.add(playerHand.getInGameId());
            }
        }

        for (Long playerId : playersToRemove) {
            playerHands.remove(playerId);
        }


        // Pelaajien kädet uusitaan Hibernaten takia. Halutaan tehdä uusi rivi tietokantaan.
        for (Long playerId : playersToContinue) {
            Player player = playerHands.get(playerId).getHandPlayer();
            playerHands.put(playerId, new PlayerHand(player));
        }

        Player dealer = dealerHand.getHandPlayer();

        dealerHand = new PlayerHand(dealer);

        startRound();
    }

    /**
     * Kerää pelaajilta tiedon kierrokselle asetettavasta panoksesta
     */
    void askForAndSetRoundBets() {

        for (Long playerId : playerHands.keySet()) {
            int playerBet = serverListener.askForRoundBet(playerId);
            PlayerHand playerHand = playerHands.get(playerId);
            playerHand.setBet(playerBet);
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

    HashMap<Long, PlayerHand> getPlayerHands() {
        return playerHands;
    }
}
