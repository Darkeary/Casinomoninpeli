/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.Serializable;

public class CardCounterPrediction implements Serializable {

    public int suggestedAction;
    public Card predictedNextCard;

    public CardCounterPrediction(int suggestedAction, Card predictedNextCard) {
        this.suggestedAction = suggestedAction;
        this.predictedNextCard = predictedNextCard;
    }
    
}
