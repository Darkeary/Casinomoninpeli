package server;

import util.PlayerHand;

import java.util.Collection;
import java.util.List;
/**
 * Luokan tehtävä on ottaa sisään kaikki tilasto oliot ja laskea näiden tietojen perusteella järkevia tilastotietoja
 */
public class CalculateStatistics {

    private static float totalPlayerRounds = 0;
    private static float totalPlayerWins = 0;
    private static float playerWinPercentage = 0;

    /**
     * Laskee pelatut kierrokset, voitetut kierrokset ja voittoprosentin
     */
    public static void calculateWinStats(List<Statistic> stats) {
        totalPlayerRounds = 0;
        totalPlayerWins = 0;
        playerWinPercentage = 0;

        for (Statistic stat : stats) {

            PlayerHand dealerHand = stat.getDealerHand();

            Collection<PlayerHand> playerHands = stat.getGameHands();

            for (PlayerHand playerHand : playerHands) {
                totalPlayerRounds++;

                if (Logic.playerHasWon(playerHand, dealerHand)) {
                    totalPlayerWins++;
                }
            }
        }

        playerWinPercentage = totalPlayerWins / totalPlayerRounds * 100;
    }

    public static int getTotalPlayerRounds() {
        return (int) totalPlayerRounds;
    }

    public static int getTotalPlayerWins() {
        return (int) totalPlayerWins;
    }

    public static float getPlayerWinPercentage() {
        return playerWinPercentage;
    }
}
