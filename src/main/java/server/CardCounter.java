/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import communication.GameState;
import communication.PlayerAction;
import util.Card;
import util.CardCounterPrediction;

/**
 * Kortinlaskija, antaa pelaajille ennustuksia seuraavasta kortista ja suosituksia siitä mitä pelaajan kannattaisi tehdä
 */
public class CardCounter {
    
    public CardCounterPrediction getNewPredictionBasedOnGameState(GameState gamestate) {
        return new CardCounterPrediction(PlayerAction.HIT, new Card(1, ""));
    }
    
    
}
