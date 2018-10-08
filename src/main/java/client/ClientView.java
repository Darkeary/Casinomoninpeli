package client;

import communication.GameState;

public interface ClientView {

    void updateToStartGameState(int playerId);

    void updateToPlaceBetsState();

    void updateBasedOnGameState(GameState gameState);
}
