package com.g2t2.types;

import java.io.Serializable;
import java.util.Objects;

import com.g2t2.enums.CardColour;
import com.g2t2.util.Constants;

/**
 * Card class, modelling a singular Parade card.
 * 
 * @author Oliver
 */
public class Card implements Comparable<Card>, Serializable {
    private static final long serialVersionUID = 1L;
    private int value;
    private CardColour colour;

    /**
     * Creates a new Card with the specified value and colour.
     * 
     * @param value the numerical value of the card
     * @param colour the colour of the card
     */
    public Card(int value, CardColour colour) {
        this.value = value;
        this.colour = colour;
    }

    /**
     * Compares two cards first by their colour and then by their numerical value.
     * If comparing by colour, cards will be placed in the following order:
     * RED, BLUE, PURPLE, GREEN, SEAFOAM, ORANGE
     * 
     * @param other the other card being compared to
     * @return a negative integer, zero, or a positive integer if this card is less than,
     *         equal to, or greater than the other card
     */
    @Override
    public int compareTo(Card other) {
        if (colour.equals(other.colour)) {
            return value - other.value;
        }
        return colour.ordinal() - other.colour.ordinal();
    }

    /**
     * Returns the value of the card.
     * 
     * @return the value of the card as an integer
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the card to the specified value.
     * 
     * @param value the new value of the card
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Returns the colour of the card.
     * 
     * @return the colour of the card as a CardColour
     */
    public CardColour getColour() {
        return colour;
    }

    /**
     * Sets the colour of the card to the specified colour.
     * 
     * @param colour the new colour of the card
     */
    public void setColour(CardColour colour) {
        this.colour = colour;
    }

    /**
     * Returns a string representation of the card, including its value and colour,
     * formatted with ANSI color codes.
     *
     * @return a string representation of this card, including its value and colour
     */    
    @Override
    public String toString() {
        String ansiString = colour.getAnsiCode();
        return String.format(ansiString + "Card[value=%d, colour=%s]" +
        Constants.ANSI_RESET, value, colour);
    }

    /**
     * Returns a hash code value for the card based on its colour and value.
     *
     * @return the hashcode of this card
     */   
    @Override
    public int hashCode() {
        return Objects.hash(colour.ordinal(), value);
    }
}
