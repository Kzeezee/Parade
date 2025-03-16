package com.g2t2.types;

import com.g2t2.enums.CardColour;
import com.g2t2.util.Constants;

/**
 * Card class, modelling a singular Parade card.
 * 
 * @author Oliver
 */
public class Card implements Comparable<Card> {
    private int value;
    private CardColour colour;

    // added to create new cards - ben
    public Card(int value, CardColour colour) {
        this.value = value;
        this.colour = colour;
    }

    // order by colour then value
    // Colour order: RED, BLUE, PURPLE, GREEN, SEAFOAM, ORANGE
    @Override
    public int compareTo(Card other) {
        if (colour.equals(other.colour)) {
            return value - other.value;
        }
        return colour.ordinal() - other.colour.ordinal();
    }

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

    public String toString() {
        String ansiString = colour.getAnsiCode();
        return String.format(ansiString + "Card[value=%d, colour=%s]" +
        Constants.ANSI_RESET, value, colour);
    }

}
