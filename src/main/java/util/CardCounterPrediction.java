/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import communication.PlayerAction;

public class CardCounterPrediction {
    
    public PlayerAction suggestedAction;
    public Card predictedNextCard;
    
    public CardCounterPrediction(PlayerAction suggestedAction, Card predictedNextCard) {
        this.suggestedAction = suggestedAction;
        this.predictedNextCard = predictedNextCard;
    }
    
}
