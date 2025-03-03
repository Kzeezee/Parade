package com.g2t2.types;

import com.g2t2.enums.CardColour;

import java.util.*;

/**
 * CardDeck class, modelling the deck of Parade cards.
 * @author Ben
 */

public class CardDeck {

    private ArrayList<Card> deck;

    public CardDeck() {
        deck = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            for (CardColour colour : CardColour.values()) {
                deck.add(new Card(i, colour));
            }
        }   
    }

    public ArrayList<Card> getDeck() {
        return deck;
    } 

}
