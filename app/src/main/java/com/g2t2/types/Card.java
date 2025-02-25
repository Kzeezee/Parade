package com.g2t2.types;

import com.g2t2.enums.CardColour;

/**
 * Card class, modelling a singular Parade card.
 * @author Oliver
 */
public class Card {
    private int value;
    private CardColour colour;

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public CardColour getColour() {
        return colour;
    }
    public void setColour(CardColour colour) {
        this.colour = colour;
    }
    
    
}
