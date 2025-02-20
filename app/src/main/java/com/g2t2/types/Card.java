package com.g2t2.types;

import com.g2t2.enums.CardColour;

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
