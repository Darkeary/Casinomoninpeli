package client;

import communication.GameState;

/**
 * Client näyttö rajapinta
 */
public interface ClientView {

    void updateToStartGameState(int playerId);

    void updateToPlaceBetsState();

    void updateBasedOnGameState(GameState gameState);
}
